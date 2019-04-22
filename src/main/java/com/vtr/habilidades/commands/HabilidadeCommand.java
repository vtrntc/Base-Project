package com.vtr.habilidades.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.vtr.api.shared.utils.StringUtils;
import com.vtr.api.spigot.commands.CustomCommand;
import com.vtr.api.spigot.message.MessageUtils;
import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.habilidades.Habilidade;
import com.vtr.habilidades.habilidades.extra.HabilidadeExtra;
import com.vtr.habilidades.habilidades.extra.HabilidadeExtraPerLevel;
import com.vtr.habilidades.habilidades.extra.HabilidadeExtraPercent;
import com.vtr.habilidades.inventories.HabilidadesInventory;
import com.vtr.habilidades.objects.HabilidadeInfo;
import com.vtr.habilidades.user.HabilidadeUser;

public class HabilidadeCommand extends CustomCommand {

    public HabilidadeCommand() {
        super("habilidade", Arrays.asList("stats", "skills"));
    }

    public boolean execute(CommandSender sender, String label, String[] args) {
        Player p = (Player) sender;

		HabilidadeUser habilidadePlayer = HabilidadePlugin.getModuleFactory().getUserModule(p.getName());
        if (args.length == 0 || !p.hasPermission("habilidades.admin")) {
            sendHabilidades(p, habilidadePlayer);
            HabilidadesInventory.open(p, p.getName());
        } else if (args[0].equalsIgnoreCase("tst")) {
        	p.sendMessage("habilidades: " + HabilidadePlugin.getManager().getHabilidades().size());
            for (Habilidade habilidade : HabilidadePlugin.getManager().getHabilidades()) {
            	p.sendMessage(habilidade.getName() + ":");
            		if (!habilidade.getExtras().isEmpty()) {
            			p.sendMessage(habilidade.getName() + " not empty:");
                    for (HabilidadeExtra extra : habilidade.getExtras()) {
                    	p.sendMessage("extra: " + extra.getExtraType().name());
                    	
                        if (extra instanceof HabilidadeExtraPercent) {
                            HabilidadeExtraPercent a = (HabilidadeExtraPercent) extra;
                            p.sendMessage(extra.getExtraType().name() + ": " + a.getChance(habilidadePlayer));
                        } else if (extra instanceof HabilidadeExtraPerLevel) {
                            HabilidadeExtraPerLevel a = (HabilidadeExtraPerLevel) extra;
                            p.sendMessage(extra.getExtraType().name() + ": " + a.getChance(habilidadePlayer));
                        }
                    }
                }
            }
        } else if (args[0].equalsIgnoreCase("addxp")) {
            if (args.length < 4) {
                MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "UseAddXp").send(p);
            } else if (!StringUtils.isInteger(args[3])) {
                MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "InvalidAmount").send(p);
            } else {
                //          [0]   [1]  [2]    [3]
//				/habilidade addxp vtr_ mining 10
                Habilidade habilidade = HabilidadePlugin.getManager().getHabilidadeByTypeName(args[2]);
                if (habilidade == null) {
                    MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "HabilidadeNotFound").send(p);
                } else {
                    int xp = Integer.parseInt(args[3]);

                    HabilidadeUser targetPlayer = HabilidadePlugin.getModuleFactory().getUserModule(args[1]);
                    if (targetPlayer == null) {
                    	MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "PlayerNotFound").send(p);
                    }else{
	                    HabilidadeInfo habilidadeInfo = targetPlayer.getHabilidade(habilidade.getType());
	                    if (habilidadeInfo == null) {
	                        MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "HabilidadeNotFound").send(p);
	                    } else {
	                        habilidadeInfo.setXp(habilidadeInfo.getXp() + xp);
	                        habilidadeInfo.getHabilidade().canLevelUP(habilidadePlayer);
	
	                        Map<String, String> replacers = new HashMap<>();
	                        replacers.put("%xp%", Integer.toString(xp));
	                        replacers.put("%player%", targetPlayer.getNetworkUser().getName());
	
	                        MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "XpAdded").replace(replacers).send(p);
	                    }
                    }
                }
            }
        } else {
            HabilidadesInventory.open(p, p.getName());
            sendHabilidades(p, habilidadePlayer);
        }

        return false;
    }

    private void sendHabilidades(Player player, HabilidadeUser habilidadePlayer) {
        Map<String, String> replacers = new HashMap<>();

        int level = 0;
        for (HabilidadeInfo habilidade : habilidadePlayer.getHabilidades().values()) {
            level += habilidade.getLevel();

            replacers.put("%" + habilidade.getHabilidade().getType().name().toLowerCase() + "_level%", Integer.toString(habilidade.getLevel()));
            replacers.put("%" + habilidade.getHabilidade().getType().name().toLowerCase() + "_xp%", StringUtils.formatDouble(habilidade.getXp()));
            replacers.put("%" + habilidade.getHabilidade().getType().name().toLowerCase() + "_max%", Integer.toString(habilidade.getHabilidade().getXPToNextLevel(habilidade)));
        }

        replacers.put("%level%", Integer.toString(level));

        MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "HabilidadesInfo").replace(replacers).send(player);
    }
}
