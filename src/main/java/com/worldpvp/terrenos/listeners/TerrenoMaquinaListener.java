package com.worldpvp.terrenos.listeners;

import org.bukkit.event.Listener;

public class TerrenoMaquinaListener implements Listener {

//	@EventHandler
//	private void onMaquinaPlace(MaquinaPlaceEvent e) {
//		Terreno terreno = WorldTerrenos.getManager().getTerreno(e.getLocation());
//		if(terreno != null) {
//			if(terreno.getMachineAmount() >= terreno.getTerrenoSize().getMaxMachines()) {
//				e.getPlayer().sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "LimiteMaquinas"));
//				e.setCancelled(true);
//			}else{
//				terreno.countMaquina(e.getMaquina());
//			}
//		}
//	}
//	
//	@EventHandler
//	private void onMaquinaBreak(MaquinaBreakEvent e) {
//		Terreno terreno = WorldTerrenos.getManager().getTerreno(e.getMaquinaInfo().getLocation());
//		if(terreno != null) {
//			if(!terreno.getPlayer().equalsIgnoreCase(e.getPlayer().getName())) {
//				if(Bukkit.getPlayer(terreno.getPlayer()) != null) {
//					terreno.removeMaquina(e.getMaquinaInfo().getMaquina());
//				}else if(!e.getPlayer().hasPermission("worldterrenos.admin")) {
//					e.setCancelled(true);
//				}
//			}else{
//				terreno.removeMaquina(e.getMaquinaInfo().getMaquina());
//			}
//		}
//	}
}
