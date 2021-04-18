package eu.ladeira.zitems.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import eu.ladeira.zitems.ZItem;
import eu.ladeira.zitems.firearm.Firearm;
import eu.ladeira.zitems.firearm.ReloadingArchitecture;

public class FirearmFire implements Listener {

	@EventHandler
	public void onFirearmFire(PlayerInteractEvent e) {
		ItemStack is = e.getItem();
		ZItem zitem = ZItem.getItem(is); // Returns null if item doesn't exist
		
		if (zitem != null && zitem instanceof Firearm) { // Item exists and is a firearm
			handleFirearmAction(e.getPlayer(), is, (Firearm) zitem, e.getAction());
			e.setCancelled(true);
		}
	}
	
	private void handleFirearmAction(Player p, ItemStack is, Firearm firearm, Action action) {
		ReloadingArchitecture ra = firearm.getReloadingArch();
		if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
			if (ra.isReloading(p)) ra.cancelReload(p);
			else firearm.getFiringArch().fire(p, is);
		} else if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {
			if (!ra.isReloading(p)) ra.reload(p, is);
		}
	}
	
}