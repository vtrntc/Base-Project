package com.worldpvp.terrenos.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.worldpvp.terrenos.WorldTerrenos;
import com.worldpvp.terrenos.events.TerrenoSellEvent;
import com.worldpvp.terrenos.objects.Terreno;
import com.worldpvp.terrenos.objects.TerrenoSize;
import com.worldpvp.utils.builders.ItemBuilder;
import com.worldpvp.utils.utils.StringUtils;

public class TerrenoListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onBreakOff(BlockBreakEvent e) {
		Player p = e.getPlayer();
		
		if(!WorldTerrenos.getManager().isAllowedWorld(e.getBlock().getWorld()) || e.getBlock().getType() != Material.MOB_SPAWNER) {
			return;
		}
		
		Terreno terreno = WorldTerrenos.getManager().getTerreno(e.getBlock().getLocation());
		if(terreno != null && terreno.isFriend(p.getName()) && Bukkit.getOnlinePlayers().stream().filter(on -> on.getName().equalsIgnoreCase(terreno.getPlayer())).findFirst().orElse(null) == null) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	private void onPistonExtend(BlockPistonExtendEvent e) {
		if(!WorldTerrenos.getManager().isAllowedWorld(e.getBlock().getWorld())) {
			return;
		}
		
		e.setCancelled(true);
	}
	
	@EventHandler
	private void onRedstone(BlockRedstoneEvent e) {
		if(!WorldTerrenos.getManager().isAllowedWorld(e.getBlock().getWorld())) {
			return;
		}
		
		e.setNewCurrent(0);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	private void onBreak(BlockBreakEvent e) {
		if(e.isCancelled() || !WorldTerrenos.getManager().isAllowedWorld(e.getBlock().getWorld())) {
			return;
		}
		
		Player p = e.getPlayer();
		
		Terreno terreno = WorldTerrenos.getManager().getTerreno(e.getBlock().getLocation());
		if(terreno != null) {
			if(terreno.getTerrenoVenda() != null) {
				Block block = e.getBlock();
				if(e.getBlock().getType() != Material.SIGN_POST && e.getBlock().getRelative(BlockFace.UP).getType() == Material.SIGN_POST) {
					block = e.getBlock().getRelative(BlockFace.UP);
				}
				
				if(terreno.getTerrenoVenda().getSign().equals(block.getState())) {
					if(!terreno.getPlayer().equalsIgnoreCase(p.getName())) {
						e.setCancelled(true);
					}else{
						terreno.setTerrenoVenda(null);
						
						p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "VendaCancelada"));
					}
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	private void onInteract(PlayerInteractEvent e) {
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player p = e.getPlayer();
			if(!WorldTerrenos.getManager().isAllowedWorld(p.getWorld()) || e.getClickedBlock().getType() != Material.SIGN_POST) {
				return;
			}

			Terreno terreno = WorldTerrenos.getManager().getTerreno(e.getClickedBlock().getLocation());
			if(terreno != null && terreno.getTerrenoVenda() != null && terreno.getTerrenoVenda().getSign().equals(e.getClickedBlock().getState())) {
				if(WorldTerrenos.getEconomy().getBalance(p) < terreno.getTerrenoVenda().getPrice()) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "SemDinheiro"));
				}else if(!p.hasPermission("worldterrenos.size." + terreno.getTerrenoSize().getName().toLowerCase())) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "SemPermTerreno"));
				}else if(!p.getName().equalsIgnoreCase(terreno.getPlayer())) {
					boolean has = false;
					for(Terreno t : WorldTerrenos.getManager().getTerrenos(p.getName())) {
						if(t.getTerrenoSize().equals(terreno.getTerrenoSize())) {
							has = true;
							
							p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "JaTemTamanho"));
							break;
						}
					}
					
					if(!has) {
						Bukkit.getPluginManager().callEvent(new TerrenoSellEvent(p, terreno));
						
						terreno.getTerrenoVenda().getSign().getBlock().breakNaturally();
						
						WorldTerrenos.getEconomy().withdrawPlayer(p, terreno.getTerrenoVenda().getPrice());
						WorldTerrenos.getEconomy().depositPlayer(terreno.getPlayer(), terreno.getTerrenoVenda().getPrice());
					
						WorldTerrenos.getManager().transfereTerreno(p, terreno);
						
						p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "ComprouTerrenoOutro").replaceAll("%valor%", StringUtils.formatMoney(terreno.getTerrenoVenda().getPrice())).replaceAll("%player%", terreno.getPlayer()));
					}
				}
				
				e.setCancelled(true);
			}
 		}
	}
	
	@EventHandler
	private void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if(!WorldTerrenos.getManager().isAllowedWorld(p.getWorld())) {
			return;
		}
	
		ApplicableRegionSet set = WorldTerrenos.getWorldGuard().getRegionManager(p.getLocation().getWorld()).getApplicableRegions(new Location(p.getLocation().getWorld(), p.getLocation().getX(), 1, p.getLocation().getZ()));
		for(ProtectedRegion pr : set.getRegions()) {
			if(pr != null && pr.getId().contains("-")) {
				String[] all = pr.getId().split("-");
				for(Terreno terreno : WorldTerrenos.getManager().getTerrenos(all[0])) {
					if(terreno.getRegion() != null && pr != null && terreno.getRegion().equals(pr)) {
						if(!p.hasPermission("worldterrenos.mod") && terreno.isImpedido(p.getName())) {
							p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "ImpedidoEntrar"));
							
					         Vector direction = p.getLocation().getDirection();
					         direction.setY(-0.35);
					         direction.multiply(-0.8);
					         
				             p.setVelocity(direction);
						}
						
						break;
					}
				}
			}
		}
	}
	
	@EventHandler
	private void onClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		
		if(e.getInventory().equals(WorldTerrenos.getManager().getBuyInventory())) {
			TerrenoSize terrenoSize = TerrenoSize.getByIcon(e.getCurrentItem());
			if(terrenoSize != null) {
				if(!p.hasPermission("worldterrenos.size." + terrenoSize.getName().toLowerCase())) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "SemPermTerreno"));
				}else if(WorldTerrenos.getEconomy().getBalance(p) < terrenoSize.getCost()) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "SemDinheiro"));
				}else if(!WorldTerrenos.getManager().isAllowedWorld(p.getWorld())) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "MundoInvalido"));
				}else if(WorldTerrenos.getManager().isRegion(p.getLocation(), terrenoSize)) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "NaoPodeComprarAqui"));
				}else{
					boolean has = false;
					for(Terreno terreno : WorldTerrenos.getManager().getTerrenos(p.getName())) {
						if(terreno != null && terreno.getTerrenoSize().equals(terrenoSize)) {
							has = true;
							
							p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "JaTemTamanho"));
							break;
						}
					}
					 
					if(!has) {
						WorldTerrenos.getManager().compraTerreno(p, terrenoSize);
						
						p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "ComprouTerreno").replaceAll("%preco%", StringUtils.formatMoney(terrenoSize.getCost())).replaceAll("%tamanho%", terrenoSize.getName()));
					}
				}
				
				p.closeInventory();
			}
			
			e.setCancelled(true);
		}else if(e.getInventory().getName().equalsIgnoreCase(StringUtils.convert(WorldTerrenos.getInstance().getConfig().getString("Menus.Visitar.Nome")))) {
			if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR && e.getCurrentItem().hasItemMeta()) {
				if(ItemBuilder.hasNBT(e.getCurrentItem(), "TerrenoID")) {
					Terreno terreno = WorldTerrenos.getManager().getTerreno(ItemBuilder.getNBT(e.getCurrentItem(), "Dono"), Integer.parseInt(ItemBuilder.getNBT(e.getCurrentItem(), "TerrenoID")));
					if(terreno != null) {
						p.teleport(terreno.getSpawn());
					}
				}
			}
			
			e.setCancelled(true);
		}else if(e.getInventory().getName().equalsIgnoreCase(StringUtils.convert(WorldTerrenos.getInstance().getConfig().getString("Menus.Ir.Nome")))) {
			if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR && e.getCurrentItem().hasItemMeta()) {
				if(ItemBuilder.hasNBT(e.getCurrentItem(), "TerrenoID")) {
					Terreno terreno = WorldTerrenos.getManager().getTerreno(ItemBuilder.getNBT(e.getCurrentItem(), "Dono"), Integer.parseInt(ItemBuilder.getNBT(e.getCurrentItem(), "TerrenoID")));
					if(terreno != null) {
						p.teleport(terreno.getSpawn());
					}
				}
			}
			
			e.setCancelled(true);
		}
	}
}
