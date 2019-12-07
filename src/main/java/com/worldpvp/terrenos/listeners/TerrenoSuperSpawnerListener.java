package com.worldpvp.terrenos.listeners;

import org.bukkit.event.Listener;

public class TerrenoSuperSpawnerListener implements Listener {

//	@EventHandler
//	private void onSpawnerPlace(SuperSpawnerPlaceEvent e) {
//		Terreno terreno = WorldTerrenos.getManager().getTerreno(e.getMaquinaInfo().getLocation());
//		if(terreno != null) {
//			if(terreno.getSuperSpawnerAmount() >= terreno.getTerrenoSize().getMaxSuperSpawners()) {
//				e.getPlayer().sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "LimiteSuperSpawner"));
//				e.setCancelled(true);
//			}else{
//				terreno.countSuper(e.getMaquinaInfo().getMaquina());
//			}
//		}
//	}
//	
//	@EventHandler
//	private void onSpawnerBreak(SuperSpawnerBreakEvent e) {
//		Terreno terreno = WorldTerrenos.getManager().getTerreno(e.getMaquinaInfo().getLocation());
//		if(terreno != null) {
//			if(!terreno.getPlayer().equalsIgnoreCase(e.getPlayer().getName())) {
//				if(Bukkit.getPlayer(terreno.getPlayer()) != null) {
//					terreno.removeSpawner(e.getMaquinaInfo().getMaquina());
//				}else{
//					e.setCancelled(true);
//				}
//			}else{
//				terreno.removeSpawner(e.getMaquinaInfo().getMaquina());
//			}
//		}
//	}
}
