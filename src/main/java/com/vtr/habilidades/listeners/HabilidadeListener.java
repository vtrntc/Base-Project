package com.vtr.habilidades.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;

import com.vtr.habilidades.HabilidadePlugin;

public class HabilidadeListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onPlace(BlockPlaceEvent e) {
		e.getBlock().setMetadata("playerPlaced", new FixedMetadataValue(HabilidadePlugin.getInstance(), e.getPlayer().getName()));
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onBreak(BlockBreakEvent e) {
		if(e.getBlock().hasMetadata("playerPlaced")) {
			e.getBlock().removeMetadata("playerPlaced", HabilidadePlugin.getInstance());
		}
	}
}
