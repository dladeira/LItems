package eu.ladeira.zitems.firearm;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import eu.ladeira.zitems.ZItems;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R3.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_16_R3.ParticleType;
import net.minecraft.server.v1_16_R3.Particles;

public class FullAuto implements FiringArchitecture {
	
	/*
	 * 0 = Bullet will go randomly 25 degrees from the player's crosshair
	 * 100 = Bullet will go exactly where the player is aiming
	 */
	
	private int accuracy;
	private int damage;
	private int autoFireTicks;
	private int ticksPerRound;
	private int range;
	private int damageDropoff;
	
	private ArrayList<Projectile> projectiles;
	
	private HashMap<Player, Entry<ItemStack, Integer>> autoFireMap;
	private HashMap<Player, Integer> fireDelayMap;
	
	public FullAuto(int accuracy, int damage, int range, int ticksPerRound, int damageDropoff, int autoFireTicks) {
		this.accuracy = Math.max(0, Math.min(accuracy, 100));
		this.damage = damage;
		this.ticksPerRound = ticksPerRound;
		this.range = range;
		this.damageDropoff = damageDropoff;
		// Ticks to auto fire (automatically fire firearm after the event has been called)
		this.autoFireTicks = autoFireTicks;
		
		this.projectiles = new ArrayList<>();
		this.autoFireMap = new HashMap<>();
		this.fireDelayMap = new HashMap<>();
		
		new BukkitRunnable() {
			@Override
			public void run() {
				doProjectileTick(Particles.CRIT);
				
				ArrayList<Player> autoFireRemove = new ArrayList<>();
				ArrayList<Player> fireDelayRemove = new ArrayList<>();
				
				for (Player p : fireDelayMap.keySet()) {
					fireDelayMap.put(p, fireDelayMap.get(p) - 1);
					if (fireDelayMap.get(p) <= 0) fireDelayRemove.add(p);
				}
				
				for (Player p : fireDelayRemove) fireDelayMap.remove(p);
				
				for (Player p : autoFireMap.keySet()) {
					Entry<ItemStack, Integer> entry = autoFireMap.get(p);
					autoFireMap.put(p, new AbstractMap.SimpleEntry<ItemStack, Integer>(entry.getKey(), entry.getValue() - 1));
					
					if (autoFireMap.get(p).getValue() <= 0) autoFireRemove.add(p);
					if (!p.getInventory().getItemInMainHand().equals(entry.getKey())) autoFireRemove.add(p);
					
					// Player is still holding weapon and the tick hasn't ran out
					if (!autoFireRemove.contains(p)) fire(p, entry.getKey(), true);
				}
				
				for (Player p : autoFireRemove) autoFireMap.remove(p);
			}
		}.runTaskTimer(ZItems.getPlugin(), 1, 1);
	}
	
	public FullAuto(int accuracy, int damage, int range, int ticksPerRound, int damageDropoff) {
		this(accuracy, damage, range, ticksPerRound, damageDropoff, 5);
	}
	
	public int getDamage() {
		return this.damage;
	}
	
	public int getAccuracy() {
		return this.accuracy;
	}
	
	public int getTicksPerRound() {
		return this.ticksPerRound;
	}
	
	public int getRange() {
		return this.range;
	}
	
	public double getDamageDropoff(Location start, Location end) {
		double damageChipped = this.damage * (start.distance(end) / (this.damageDropoff * 2));
		return damageChipped > this.damage ? this.damage : damageChipped; // Prevent negative damage
	}
	
	// Max distance bullets can go in degrees from crosshair
	public int getInaccuracy() {
		int acc = Math.round(Math.abs(this.getAccuracy() - 100) / 6); // 16.7 degrees
		
		// If acc is less then 1 return 1
		return (acc > 0) ? acc : 1;
	}
	
	// Generate random projectile angle offset based on innacuracy
	public int generateSpread() {
		// Allow negative and positive values
		return new Random().nextInt(this.getInaccuracy()) - (this.getInaccuracy() / 2);
	}
	
	public Vector calculateInacurracy(Location loc) {
		// Add bullet spread to the player's angle
		double pitch = (((loc.getPitch() + 90 + this.generateSpread()) * Math.PI) / 180);
		double yaw  = (((loc.getYaw() + 90 + this.generateSpread())  * Math.PI) / 180);
		
		// Calculate vector from pitch and yaw
		double x = Math.sin(pitch) * Math.cos(yaw);
		double y = Math.sin(pitch) * Math.sin(yaw);
		double z = Math.cos(pitch);
		
		return new Vector(x, z, y);
	}
	
	@Override
	public void fire(Player p, ItemStack weapon) {
		fire(p, weapon, false);
	}
	
	public void fire(Player p, ItemStack weapon, boolean calledByAutoFire) {
		fire(p, weapon, calledByAutoFire, true);
	}
	
	public void fire(Player p, ItemStack weapon, boolean calledByAutoFire, boolean useAmmo) {
		if (Firearm.getAmmo(weapon) < 1) return;
		// Keep this above firingCooldown
		
		// Player manually fired
		if (!calledByAutoFire && (autoFireTicks > 0)) autoFireMap.put(p, new AbstractMap.SimpleEntry<ItemStack, Integer>(weapon, autoFireTicks));
		if (fireDelayMap.containsKey(p)) return; // Used to regulate rounds per minute
		if (getTicksPerRound() > 0) fireDelayMap.put(p, getTicksPerRound());
		
		if (useAmmo) Firearm.modifyAmmo(weapon, -1);
		Projectile fired = fireProjectile(p);
		
		// Make arrow invisible for all online players
		for (Player online : Bukkit.getOnlinePlayers()) {
			((CraftPlayer) online).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(fired.getEntityId()));
		}
		
		// Smoke doesn't spawn in player's face
		new BukkitRunnable() {
			public void run() { projectiles.add(fired); }
		}.runTaskLater(ZItems.getPlugin(), 1);
	}
	
	@Override
	public void land(Player p, Projectile proj, Location loc, Entity hit) {
		if ((hit != null) && (hit instanceof LivingEntity)) {
			LivingEntity entity = (LivingEntity) hit;
			
			entity.setNoDamageTicks(0); // Remove invincibility
			
			// Custom damage and knockback
			entity.setVelocity(proj.getVelocity().normalize().multiply(0.25));
			
			// Damage entity taking into account the damage dropoff
			entity.damage(this.getDamage() - this.getDamageDropoff(((Location) proj.getMetadata("fired_location").get(0).value()), proj.getLocation()));
			
		}
		projectiles.remove(proj);
		proj.remove();
	}
	
	private Projectile fireProjectile(Player p) {
		Projectile fired = p.launchProjectile(Snowball.class, calculateInacurracy(p.getLocation()).multiply(3));
		fired.setGravity(false);
		fired.setShooter(p);
		fired.setMetadata("firing_architecture", new FixedMetadataValue(ZItems.getPlugin(), this)); // Attach FiringArchitecture to projectile
		fired.setMetadata("fired_location", new FixedMetadataValue(ZItems.getPlugin(), fired.getLocation())); // Attach FiringArchitecture to projectile
		return fired;
	}
	
	// Calculate range for projectile and generate particles
	private void doProjectileTick(ParticleType particle) {
		ArrayList<Projectile> toLand = new ArrayList<>();
		for (Projectile proj : projectiles) {
			Location loc = proj.getLocation();
			PacketPlayOutWorldParticles particles = new PacketPlayOutWorldParticles(particle, true, loc.getX(), loc.getY(), loc.getZ(), 0, 0, 0, 0, 1);
			
			for (Player online : Bukkit.getOnlinePlayers()) {
				((CraftPlayer) online).getHandle().playerConnection.sendPacket(particles);
			}
			
			if (((Location)proj.getMetadata("fired_location").get(0).value()).distance(proj.getLocation()) > range) toLand.add(proj);
		}
		for (Projectile landing : toLand) land((Player) landing.getShooter(), landing, landing.getLocation(), null);
	}
}
