package eu.ladeira.zitems.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import eu.ladeira.zitems.firearm.FiringArchitecture;

public class ProjectileLand implements Listener {
	
	@EventHandler
	public void onProjectileLand(ProjectileHitEvent e) {
		Projectile proj = e.getEntity();
		if (proj.getMetadata("fa") != null) { // It's a custom projectile
			Player p = (Player) proj.getShooter();
			FiringArchitecture fa = (FiringArchitecture) proj.getMetadata("firing_architecture").get(0).value();
			fa.land(p, proj, proj.getLocation(), e.getHitEntity());
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onProjectileHit(EntityDamageByEntityEvent e) {
		Entity damager = e.getDamager();
		
		if (damager instanceof Projectile) {
			Projectile proj = (Projectile) damager;
			if (proj.getMetadata("fa") != null) {
				e.setCancelled(true);
			}
		}
	}
}
