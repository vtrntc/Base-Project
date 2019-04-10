package com.vtr.habilidades.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.vtr.api.commands.CustomCommand;
import com.vtr.api.utils.StringUtils;
import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.habilidades.Habilidade;
import com.vtr.habilidades.inventories.HabilidadesInventory;
import com.vtr.habilidades.objects.HabilidadeInfo;
import com.vtr.habilidades.objects.HabilidadePlayer;

public class HabilidadeCommand extends CustomCommand {

	public HabilidadeCommand() {
		super("habilidade", Arrays.asList("stats", "skills"));
	}

	public boolean execute(CommandSender sender, String label, String[] args) {
		Player p = (Player) sender;
		
		HabilidadePlayer habilidadePlayer = HabilidadePlugin.getManager().getPlayer(p.getName());
		if(args.length == 0 || !p.hasPermission("habilidades.admin")) {
			sendHabilidades(p, habilidadePlayer);
			HabilidadesInventory.open(p, p.getName());
		}else if(args[0].equalsIgnoreCase("addxp")) {
			if(args.length < 4) {
				StringUtils.getMessage(HabilidadePlugin.getYamlConfig(), "UseAddXp").send(p);
			}else if(!HabilidadePlugin.getManager().isPlayer(args[1])) {
				StringUtils.getMessage(HabilidadePlugin.getYamlConfig(), "PlayerNotFound").send(p);
			}else if(!StringUtils.isInteger(args[3])) {
				StringUtils.getMessage(HabilidadePlugin.getYamlConfig(), "InvalidAmount").send(p);
			}else{
				//          [0]   [1]  [2]    [3]
//				/habilidade addxp vtr_ mining 10
				Habilidade habilidade = HabilidadePlugin.getManager().getHabilidadeByTypeName(args[2]);
				if(habilidade == null) {
					StringUtils.getMessage(HabilidadePlugin.getYamlConfig(), "HabilidadeNotFound").send(p);
				}else{
					int xp = Integer.parseInt(args[3]);
					
					HabilidadePlayer targetPlayer = HabilidadePlugin.getManager().getPlayer(args[1]);
					
					HabilidadeInfo habilidadeInfo = targetPlayer.getHabilidade(habilidade.getType());
					if(habilidadeInfo == null) {
						StringUtils.getMessage(HabilidadePlugin.getYamlConfig(), "HabilidadeNotFound").send(p);
					}else{
						habilidadeInfo.setXp(habilidadeInfo.getXp() + xp);
						habilidadeInfo.getHabilidade().canLevelUP(habilidadePlayer);
						habilidadeInfo.save();
						
						Map<String, String> replacers = new HashMap<>();
						replacers.put("%xp%", Integer.toString(xp));
						replacers.put("%player%", targetPlayer.getPlayer());
						
						StringUtils.getMessage(HabilidadePlugin.getYamlConfig(), "XpAdded").replace(replacers).send(p);
					}
				}
			}
		}else if(args[0].equalsIgnoreCase("set")) {
			if(args.length < 3) {
				StringUtils.getMessage(HabilidadePlugin.getYamlConfig(), "UseSet").send(p);
			}else if(!HabilidadePlugin.getManager().isPlayer(args[0])) {
				StringUtils.getMessage(HabilidadePlugin.getYamlConfig(), "PlayerNotFound").send(p);
			}else if(!StringUtils.isInteger(args[2])) {
				StringUtils.getMessage(HabilidadePlugin.getYamlConfig(), "InvalidAmount").send(p);
			}else{
				StringUtils.getMessage(HabilidadePlugin.getYamlConfig(), "InvalidAmount").send(p);
			}
		}else{
			HabilidadesInventory.open(p, p.getName());
			sendHabilidades(p, habilidadePlayer);
		}
		
		return false;
	}
	
	private void sendHabilidades(Player player, HabilidadePlayer habilidadePlayer) {
		Map<String, String> replacers = new HashMap<>();
		
		int level = 0;
		for(HabilidadeInfo habilidade : habilidadePlayer.getHabilidades().values()) {
			level += habilidade.getLevel();
			
			replacers.put("%" + habilidade.getHabilidade().getType().name().toLowerCase() + "_level%", Integer.toString(habilidade.getLevel()));
			replacers.put("%" + habilidade.getHabilidade().getType().name().toLowerCase() + "_xp%", StringUtils.formatDouble(habilidade.getXp()));
			replacers.put("%" + habilidade.getHabilidade().getType().name().toLowerCase() + "_max%", Integer.toString(habilidade.getHabilidade().getXPToNextLevel(habilidade)));
		}
		
		replacers.put("%level%", Integer.toString(level));
		
		StringUtils.getMessage(HabilidadePlugin.getYamlConfig(), "HabilidadesInfo").replace(replacers).send(player);
	}
}
