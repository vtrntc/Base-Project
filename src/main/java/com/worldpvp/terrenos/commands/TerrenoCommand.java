package com.worldpvp.terrenos.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.flags.StateFlag;
import com.worldpvp.terrenos.WorldTerrenos;
import com.worldpvp.terrenos.events.TerrenoAbandonEvent;
import com.worldpvp.terrenos.events.TerrenoPvPToggleEvent;
import com.worldpvp.terrenos.objects.Terreno;
import com.worldpvp.terrenos.objects.TerrenoVenda;
import com.worldpvp.utils.utils.MathUtils;
import com.worldpvp.utils.utils.StringUtils;

public class TerrenoCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		if(args.length == 0) {
			for(String s : StringUtils.getMessageList(WorldTerrenos.getInstance(), "UsoTerreno")) {
				p.sendMessage(s);
			}
		}else if(args[0].equalsIgnoreCase("comprar")) {
			p.openInventory(WorldTerrenos.getManager().getBuyInventory());
		}else if(args[0].equalsIgnoreCase("ir")) {
			if(args.length < 2 || !p.hasPermission("worldterrenos.mod")) {
				p.openInventory(WorldTerrenos.getManager().getIrInventory(p.getName()));
			}else if(WorldTerrenos.getManager().getTerrenos(args[1]).isEmpty()) {
				p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "NaoTemTerrenos"));
			}else{
				p.openInventory(WorldTerrenos.getManager().getIrInventory(args[1]));
			}
		}else if(args[0].equalsIgnoreCase("visitar")) {
			p.openInventory(WorldTerrenos.getManager().getVisitarInventory(p.getName()));
		}else if(args[0].equalsIgnoreCase("mob")) {
			Terreno terreno = WorldTerrenos.getManager().getTerreno(p.getLocation());
			if(terreno == null) {
				p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "NaoEstaTerreno"));
			}else if(!terreno.getPlayer().equalsIgnoreCase(p.getName()) && !p.hasPermission("worldterrenos.admin")) {
				p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "NaoESeu"));
			}else if(terreno.isMobSpawning()) {
				p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "DesativouMob"));
				terreno.setMobSpawning(StateFlag.State.DENY);
			}else{
				if(WorldTerrenos.getEconomy().getBalance(p) < WorldTerrenos.getInstance().getConfig().getInt("Config.CustoMob")) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "SemDinheiro"));
				}else{
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "AtivouMob"));
					terreno.setMobSpawning(StateFlag.State.ALLOW);
					
					WorldTerrenos.getEconomy().withdrawPlayer(p, WorldTerrenos.getInstance().getConfig().getInt("Config.CustoMob"));
				}
			}
		}else if(args[0].equalsIgnoreCase("pvp")) {
			Terreno terreno = WorldTerrenos.getManager().getTerreno(p.getLocation());
			if(terreno == null) {
				p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "NaoEstaTerreno"));
			}else if(!terreno.getPlayer().equalsIgnoreCase(p.getName()) && !p.hasPermission("worldterrenos.admin")) {
				p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "NaoESeu"));
			}else{ 
				boolean isPvP = terreno.isPvP();
				
				TerrenoPvPToggleEvent terrenoPvPToggleEvent = new TerrenoPvPToggleEvent(p, terreno, isPvP);
				Bukkit.getPluginManager().callEvent(terrenoPvPToggleEvent);
				
				if(!terrenoPvPToggleEvent.isCancelled()) {
					if(isPvP) {
						p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "DesativouPvP"));
						terreno.setPvP(StateFlag.State.DENY);
					}else{
						if(WorldTerrenos.getEconomy().getBalance(p) < WorldTerrenos.getInstance().getConfig().getInt("Config.CustoPvP")) {
							p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "SemDinheiro"));
						}else{
							p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "AtivouPvP"));
							terreno.setPvP(StateFlag.State.ALLOW);
							
							WorldTerrenos.getEconomy().withdrawPlayer(p, WorldTerrenos.getInstance().getConfig().getInt("Config.CustoPvP"));
						}
					}
				}
			}
		}else if(args[0].equalsIgnoreCase("add")) {
			if(args.length < 2) {
				p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "UsoAdd"));
			}else{
				Terreno terreno = WorldTerrenos.getManager().getTerreno(p.getLocation());
				if(terreno == null) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "NaoEstaTerreno"));
				}else if(!terreno.getPlayer().equalsIgnoreCase(p.getName()) && !p.hasPermission("worldterrenos.admin")) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "NaoESeu"));
				}else if(terreno.isFriend(args[1])) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "JaAdicionado"));
				}else if(args[1].equalsIgnoreCase(p.getName())) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "NaoPodeAmigoSi"));
				}else if(terreno.getImpedidos().size() == WorldTerrenos.getInstance().getConfig().getInt("Config.MaxImpedidos")) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "LimiteImpedidos"));
				}else if(args[1].length() > 16) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "NomeInvalido"));
				}else if(!args[1].equalsIgnoreCase("*") && !args[1].matches("^[a-zA-Z0-9_]*$")) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "NomeInvalido"));
				}else{
					terreno.addFriend(args[1]);
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "AdicionouAmigo").replaceAll("%player%", args[1]));
				}
			}
		}else if(args[0].equalsIgnoreCase("remove")) {
			if(args.length < 2) {
				p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "UsoRemove"));
			}else{
				Terreno terreno = WorldTerrenos.getManager().getTerreno(p.getLocation());
				if(terreno == null) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "NaoEstaTerreno"));
				}else if(!terreno.getPlayer().equalsIgnoreCase(p.getName()) && !p.hasPermission("worldterrenos.admin")) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "NaoESeu"));
				}else if(!terreno.isFriend(args[1])) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "NaoAdicionado"));
				}else{
					terreno.removeFriend(args[1]);
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "RemoveuAmigo").replaceAll("%player%", args[1]));
				}
			}
		}else if(args[0].equalsIgnoreCase("permitir")) {
			if(args.length < 2) {
				p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "UsoPermitir"));
			}else{
				Terreno terreno = WorldTerrenos.getManager().getTerreno(p.getLocation());
				if(terreno == null) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "NaoEstaTerreno"));
				}else if(!terreno.getPlayer().equalsIgnoreCase(p.getName()) && !p.hasPermission("worldterrenos.admin")) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "NaoESeu"));
				}else if(!terreno.isImpedido(args[1])) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "NaoImpedido"));
				}else if(!args[1].equalsIgnoreCase("*") && !args[1].matches("^[a-zA-Z0-9_]*$")) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "NomeInvalido"));
				}else{
					terreno.removeImpedido(args[1]);
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "RemoveuImpedido").replaceAll("%player%", args[1]));
				}
			}
		}else if(args[0].equalsIgnoreCase("impedir")) {
			if(args.length < 2) {
				p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "UsoImpedir"));
			}else{
				Terreno terreno = WorldTerrenos.getManager().getTerreno(p.getLocation());
				if(terreno == null) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "NaoEstaTerreno"));
				}else if(!terreno.getPlayer().equalsIgnoreCase(p.getName()) && !p.hasPermission("worldterrenos.admin")) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "NaoESeu"));
				}else if(terreno.isImpedido(args[1])) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "JaImpedido"));
				}else if(args[1].equalsIgnoreCase(p.getName())) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "NaoPodeImpedirSi"));
				}else if(terreno.getImpedidos().size() == WorldTerrenos.getInstance().getConfig().getInt("Config.MaxImpedidos")) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "LimiteImpedidos"));
				}else if(args[1].length() > 16) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "NomeInvalido"));
				}else if(!args[1].equalsIgnoreCase("*") && !args[1].matches("^[a-zA-Z0-9_]*$")) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "NomeInvalido"));
				}else if(terreno.isImpedido("*")) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "TodosImpedidosJa"));
				}else{
					terreno.addImpedido(args[1]);
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "Impediu").replaceAll("%player%", args[1]));
				}
			}
		}else if(args[0].equalsIgnoreCase("abandonar")) {
			Terreno terreno = WorldTerrenos.getManager().getTerreno(p.getLocation());
			if(terreno == null) {
				p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "NaoEstaTerreno"));
			}else if(!terreno.getPlayer().equalsIgnoreCase(p.getName()) && !p.hasPermission("worldterrenos.admin")) {
				p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "NaoESeu"));
			}else{
				if(!terreno.getSpawners().isEmpty() || !terreno.getMaquinas().isEmpty()) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "PrecisaTirar"));
				}else if(!WorldTerrenos.getManager().hasConfirm(p)) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "ConfirmAbandon"));
					WorldTerrenos.getManager().setConfirm(p);
				}else{
					Bukkit.getPluginManager().callEvent(new TerrenoAbandonEvent(p, terreno));
					
					WorldTerrenos.getManager().abandonaTerreno(terreno);
					
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "Abandonou"));
				}
			}
		}else if(args[0].equalsIgnoreCase("setspawn")) {
			Terreno terreno = WorldTerrenos.getManager().getTerreno(p.getLocation());
			if(terreno == null) {
				p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "NaoEstaTerreno"));
			}else if(!terreno.getPlayer().equalsIgnoreCase(p.getName()) && !p.hasPermission("worldterrenos.admin")) {
				p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "NaoESeu"));
			}else{
				terreno.setSpawn(p.getLocation());
				p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "SetouSpawn"));
			}
		}else if(args[0].equalsIgnoreCase("info")) {
			Terreno terreno = WorldTerrenos.getManager().getTerreno(p.getLocation());
			if(terreno == null) {
				p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "NaoEstaTerreno"));
			}else{
				for(String message : StringUtils.getMessageList(WorldTerrenos.getInstance(), "TerrenoInfo")) {
					String amigos = WorldTerrenos.getManager().formatList(terreno.getFriends());
					if(amigos.isEmpty()) {
						amigos = "Ningu�m";
					}
					
					String impedidos = WorldTerrenos.getManager().formatList(terreno.getImpedidos());
					if(impedidos.isEmpty()) {
						impedidos = "Ningu�m";
					}
					
					p.sendMessage(message
							.replaceAll("%dono%", terreno.getPlayer())
							.replaceAll("%amigos%", amigos)
							.replaceAll("%impedidos%", impedidos)
							.replaceAll("%mobs%", terreno.isMobSpawning() ? "�aLigado" : "�cDesligado")
							.replaceAll("%pvp%", terreno.isPvP() ? "�aLigado" : "�cDesligado"));
				}
			}
		}else if(args[0].equalsIgnoreCase("vender")) {
			if(args.length < 2) {
				p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "UsoVender"));
			}else{
				Terreno terreno = WorldTerrenos.getManager().getTerreno(p.getLocation());
				if(terreno == null) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "NaoEstaTerreno"));
				}else if(!terreno.getPlayer().equalsIgnoreCase(p.getName()) && !p.hasPermission("worldterrenos.admin")) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "NaoESeu"));
				}else if(!MathUtils.isInteger(args[1]) || Integer.parseInt(args[1]) <= 0) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "ValorInvalido"));
				}else if(terreno.getTerrenoVenda() != null) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "JaAVenda"));
				}else if(p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.GLOWSTONE) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "PrecisaBloco"));
				}else{
					Block block = p.getLocation().getBlock();
					block.setType(Material.SIGN_POST);
					
					Sign sign = (Sign) block.getState();
					
					int line = 0;
					for(String message : StringUtils.getMessageList(WorldTerrenos.getInstance(), "PlacaVenda")) {
						sign.setLine(line, message.replaceAll("%valor%", StringUtils.formatMoney(Integer.parseInt(args[1]))).replaceAll("%player%", p.getName()));
						line++;
					}
					
					sign.update();
					
					terreno.setTerrenoVenda(new TerrenoVenda(sign, Integer.parseInt(args[1])));
					
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "ColocouVenda").replaceAll("%valor%", StringUtils.formatMoney(Integer.parseInt(args[1]))));
				}
			} 
		}else if(args[0].equalsIgnoreCase("expulsar")) {
			if(args.length < 2) {
				p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "UsoExpulsar"));
			}else{
				Player target = Bukkit.getPlayer(args[1]);
				if(target == null) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "JogadorNaoEncontrado"));
				}else if(target.equals(p)) {
					p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "NaoPodeSeExpulsar"));
				}else{
					Terreno terreno = WorldTerrenos.getManager().getTerreno(target.getLocation());
					if(terreno == null || !terreno.getPlayer().equalsIgnoreCase(p.getName())) {
						p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "JogadorNaoEstaTerreno"));
					}else{
						target.teleport(target.getWorld().getSpawnLocation());
						
						p.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "ExpulsouJogador").replaceAll("%player%", target.getName()));
						target.sendMessage(StringUtils.getMessage(WorldTerrenos.getInstance(), "FoiExpulso").replaceAll("%player%", p.getName()));
					}
				}
			}
		}else{
			for(String s : StringUtils.getMessageList(WorldTerrenos.getInstance(), "UsoTerreno")) {
				p.sendMessage(s);
			}
		}
		return false;
	}
}
