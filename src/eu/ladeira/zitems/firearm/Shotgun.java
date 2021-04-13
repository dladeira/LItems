package eu.ladeira.zitems.firearm;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;

public class Shotgun implements FiringArchitecture {
	
	/*
	 * This class is a replica of FullAuto.class
	 * as they both do the same thing, the only differences
	 * being Shotgun's multi shot
	 */
	
	private int pellets;
	
	private FullAuto fullAuto;
	
	// See FullAuto's constructor
	public Shotgun(int accuracy, int damage, int range, int damageDropoff, int pellets) {
		this.fullAuto = new FullAuto(accuracy, damage, range, 0, damageDropoff, 0);
		
		this.pellets = pellets;
	}

	public int getPellets() {
		return this.pellets;
	}
	
	@Override
	public void fire(Player player, ItemStack weapon) {
		for (int i = 0; i < this.pellets; i++) {
			
			// Use ammo only for first shot
			if (i == 0) fullAuto.fire(player, weapon, false);
			else fullAuto.fire(player, weapon, false, false);
		}
	}

	@Override
	public void land(Player shooter, Projectile projectile, Location location, Entity hit) {
		fullAuto.land(shooter, projectile, location, hit);
	}
}
