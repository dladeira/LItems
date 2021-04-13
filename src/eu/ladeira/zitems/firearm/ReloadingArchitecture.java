package eu.ladeira.zitems.firearm;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.ladeira.zitems.ZItems;

public interface ReloadingArchitecture {
	
	public int getClipSize();
	public void reload(Player p, ItemStack is);
	public void cancelReload(Player p);
	public boolean isReloading(Player p);
	
	public static ReloadingArchitecture loadFromConfig(String path) {
		ReloadingArchitecture ra = null;
		String prefix = path;
		
		int clipSize = ZItems.getConf().getInt(prefix + ".clipSize");
		Ammo ammo = Ammo.getAmmo(ZItems.getConf().getString(prefix + ".ammo"));
		int reloadTime = ZItems.getConf().getInt(prefix + ".reloadTime");
		try {
			switch (ZItems.getConf().getString(prefix + ".type")) {
			case "ClipReload":
				ra = new ClipReload(clipSize, ammo, reloadTime);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ra;
	}
}
