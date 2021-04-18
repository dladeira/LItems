package eu.ladeira.zitems.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import eu.ladeira.zitems.ZItem;
import eu.ladeira.zitems.armor.Wearable;

public class ArmorProtectEvent implements Listener {
	
	@EventHandler
	public void onArmorProtectEvent(EntityDamageByEntityEvent e) {
		
		Entity hit = e.getEntity();
		if (hit == null || !(hit instanceof Player)) return;
		Player p = (Player) hit;
		e.setDamage(calculateProtection(p, e.getDamage())); // Makes sure damage is positive
	}
	
	private static double calculateProtection(Player p) {
		double protection = 0;
		ItemStack[] armor = p.getInventory().getArmorContents();
		
		for (ItemStack item : armor) {
			ZItem zitem = ZItem.getItem(item); // Returns null if item doesn't exist
			
			if (zitem instanceof Wearable) {
				protection+=((Wearable)zitem).getProtection();
			}
		}
		
		return protection * 0.01; // Return as precentage
	}
	
	public static double calculateProtection(Player p, double damage) {
		damage = damage - (damage * calculateProtection(p));
		return damage > 0 ? damage : 0; // Makes sure damage is positive
	}
}
