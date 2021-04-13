package eu.ladeira.zitems.firearm;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;

import eu.ladeira.zitems.ZItems;

public interface FiringArchitecture {
	
	public void fire(Player player, ItemStack weapon);
	public void land(Player shooter, Projectile projectile, Location location, Entity hit);
	
	public static FiringArchitecture loadFromConfig(String path) {
		FiringArchitecture fa = null;
		
		int accuracy = ZItems.getConf().getInt(path + ".accuracy");
		int damage = ZItems.getConf().getInt(path + ".damage");
		int range = ZItems.getConf().getInt(path + ".range");
		int ticksPerRound = ZItems.getConf().getInt(path + ".ticksPerRound");
		int damageDropoff = ZItems.getConf().getInt(path + ".damageDropoff");
		int pellets = ZItems.getConf().getInt(path + ".pellets");
		try {
			switch (ZItems.getConf().getString(path + ".type")) {
			case "SemiAuto":
				fa = new SemiAuto(accuracy, damage, range, damageDropoff);
				break;
			case "FullAuto":
				fa = new FullAuto(accuracy, damage, range, ticksPerRound, damageDropoff);
				break;
			case "Shotgun":
				fa = new Shotgun(accuracy, damage, range, damageDropoff, pellets);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return fa;
	}
}
