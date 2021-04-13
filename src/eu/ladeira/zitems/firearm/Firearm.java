package eu.ladeira.zitems.firearm;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import eu.ladeira.zitems.Rarity;
import eu.ladeira.zitems.ZItem;
import eu.ladeira.zitems.ZItems;
import net.md_5.bungee.api.ChatColor;

public class Firearm extends ZItem {
	
	private FiringArchitecture fa;
	private ReloadingArchitecture ra;
	
	public Firearm(String name, String desc, Rarity rarity, int id, FirearmType type, FiringArchitecture fa, ReloadingArchitecture ra) {
		super(name + " " + ChatColor.GRAY + "<" + ChatColor.WHITE + ra.getClipSize() + ChatColor.GRAY + ">", desc, rarity, type.name(), id);
		
		this.fa = fa;
		this.ra = ra;
	}
	
	public FiringArchitecture getFiringArch() {
		return this.fa;
	}
	
	public ReloadingArchitecture getReloadingArch() {
		return this.ra;
	}
	
	public static int getAmmo(ItemStack is) {
		if (is.hasItemMeta() && is.getItemMeta().getDisplayName().contains("<")) {
			String displayName = ChatColor.stripColor(is.getItemMeta().getDisplayName());
			int ammoStart = displayName.indexOf("<");
			int ammoEnd = displayName.indexOf(">");
			
			return Integer.valueOf(displayName.substring(ammoStart + 1, ammoEnd));
		}
		System.out.println("ERROR: Trying to get ammo count of item without ammo tags");
		return 0;
	}
	
	public static void setAmmo(ItemStack is, int amount) {
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(im.getDisplayName().replaceAll(ChatColor.GRAY + "<" + ChatColor.WHITE + getAmmo(is) + "",ChatColor.GRAY + "<" + ChatColor.WHITE + amount + ""));
		is.setItemMeta(im);
	}
	
	public static void modifyAmmo(ItemStack is, int amount) {
		setAmmo(is, getAmmo(is) + amount);
	}
	
	public static void loadFirearmFromConfig() {
		for (String name : ZItems.getConf().getConfigurationSection("firearm").getKeys(false)) {
			String desc = ZItems.getConf().getString("firearm." + name + ".desc");
			Rarity rarity;
			try {
				rarity = Rarity.valueOf(ZItems.getConf().getString("firearm." + name + ".rarity"));
			} catch (Exception e) {
				System.out.println("Invalid firearm rarity: " + ZItems.getConf().getString("firearm." + name + ".rarity"));
				continue;
			}
			int id = ZItems.getConf().getInt("firearm." + name + ".id");
			FirearmType type;
			try {
				type = FirearmType.valueOf(ZItems.getConf().getString("firearm." + name + ".type"));
			} catch (Exception e) {
				System.out.println("Invalid firearm type: " + ZItems.getConf().getString("firearm." + name + ".type"));
				continue;
			}
			FiringArchitecture fa = FiringArchitecture.loadFromConfig("firearm." + name + ".fa");
			if (fa == null) {
				System.out.println("Invalid firing architecture: " + ZItems.getConf().getString("firearm." + name + ".fa.type"));
				continue;
			}
			
			ReloadingArchitecture ra = ReloadingArchitecture.loadFromConfig("firearm." + name + ".ra");
			if (ra == null) {
				System.out.println("Invalid reloading architecture: " + ZItems.getConf().getString("firearm." + name + ".ra.type"));
				continue;
			}
			System.out.println("Adding item " + name + ":" + desc + ":" + rarity + ":" + id + ":" + type);
			Firearm fire = new Firearm(name, desc, rarity, id, type, fa, ra);
			ZItem.addItem(fire);
		}
	}
}
