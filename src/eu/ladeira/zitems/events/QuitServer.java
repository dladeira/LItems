package eu.ladeira.zitems.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import eu.ladeira.zitems.ZItem;
import eu.ladeira.zitems.firearm.Firearm;

public class QuitServer implements Listener {

	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) { // Prevent memory leaks
		Player p = e.getPlayer();
		
		for (ZItem item : ZItem.getItems()) {
			if (item instanceof Firearm) {
				Firearm fa = (Firearm) item;
				fa.getReloadingArch().cancelReload(p);
			}
		}
	}
}
