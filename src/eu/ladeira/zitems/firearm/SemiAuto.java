package eu.ladeira.zitems.firearm;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;

public class SemiAuto implements FiringArchitecture {
	
	/*
	 * This class is a replica of FullAuto.class
	 * as they both do the same thing, the only differences
	 * being SemiAuto's lack of a fireCooldown or autoFireTicks
	 */
	
	private FullAuto fullAuto;
	
	// See FullAuto's constructor
	public SemiAuto(int accuracy, int damage, int range, int damageDropoff) {
		this.fullAuto = new FullAuto(accuracy, damage, range, 1, damageDropoff, 0);
	}

	@Override
	public void fire(Player player, ItemStack weapon) {
		fullAuto.fire(player, weapon);
	}

	@Override
	public void land(Player shooter, Projectile projectile, Location location, Entity hit) {
		fullAuto.land(shooter, projectile, location, hit);
	}
}
