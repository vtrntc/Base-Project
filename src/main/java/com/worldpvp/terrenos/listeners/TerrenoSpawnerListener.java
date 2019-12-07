package com.worldpvp.terrenos.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.worldnetwork.spawner.SpawnerPlugin;
import com.worldnetwork.spawner.events.SpawnerBreakEvent;
import com.worldnetwork.spawner.events.SpawnerPlaceEvent;
import com.worldnetwork.spawner.objects.EntityInfo;
import com.worldpvp.terrenos.WorldTerrenos;
import com.worldpvp.terrenos.objects.Terreno;
import com.worldpvp.utils.utils.StringUtils;

public class TerrenoSpawnerListener implements Listener {

	@SuppressWarnings("deprecation")
	@EventHandler
	private void onSpawnerPlace(SpawnerPlaceEvent e) {
		Terreno terreno = WorldTerrenos.getManager().getTerreno(e.getBlock().getLocation());
		if(terreno != null) {
			EntityInfo entityInfo = SpawnerPlugin.getManager().getEntityInfo(e.getBlock());
			if(entityInfo != null) {
				if(terreno.getSpawnerAmount() >= terreno.getTerrenoSize().getMaxSpawners()) {
					e.getPlayer().sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "LimiteSpawner"));
					e.setCancelled(true);
				}else{
					terreno.countSpawner(SpawnerPlugin.getManager().getOldEntity(EntityType.fromId(entityInfo.getEntityID()).getName()));
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	private void onSpawnerBreak(SpawnerBreakEvent e) {
		Terreno terreno = WorldTerrenos.getManager().getTerreno(e.getBlock().getLocation());
		if(terreno != null) {
			EntityInfo entityInfo = SpawnerPlugin.getManager().getEntityInfo(e.getBlock());
			if(entityInfo != null) {
				terreno.removeSpawner(SpawnerPlugin.getManager().getOldEntity(EntityType.fromId(entityInfo.getEntityID()).getName()));
			}
		}
	}
}
