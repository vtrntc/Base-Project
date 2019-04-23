package com.vtr.habilidades.commands;

import com.vtr.api.shared.command.CommandRestriction;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.vtr.api.shared.utils.StringUtils;
import com.vtr.api.spigot.commands.CustomCommand;
import com.vtr.api.spigot.message.MessageUtils;
import com.vtr.api.spigot.user.User;
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
        super("habilidade", CommandRestriction.CONSOLE_AND_INGAME, "stats", "skills");
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        Player player = (Player) sender;

        HabilidadeUser habilidadePlayer = HabilidadePlugin.getModuleFactory().getUserModule(user.getId());
        if (args.length == 0 || !player.hasPermission("habilidades.admin")) {
            sendHabilidades(player, habilidadePlayer);
            HabilidadesInventory.open(player, player.getName());
            return;
        }
        if (args[0].equalsIgnoreCase("tst")) {
            for (Habilidade habilidade : HabilidadePlugin.getManager().getHabilidades()) {
                player.sendMessage(habilidade.getName() + ": " + habilidade.getExtras().size());
                if (!habilidade.getExtras().isEmpty()) {
                    player.sendMessage(habilidade.getName() + " not empty:");
                    for (HabilidadeExtra extra : habilidade.getExtras()) {
                        player.sendMessage("extra: " + extra.getExtraType().name());
                        if (extra instanceof HabilidadeExtraPercent) {
                            player.sendMessage("1: " + extra.getExtraType().name());
                            HabilidadeExtraPercent a = (HabilidadeExtraPercent) extra;
                            player.sendMessage("2: " + a.getExtraType().name());
                            player.sendMessage("3: " + a.getChance(habilidadePlayer));
                            player.sendMessage(extra.getExtraType().name() + ": " + a.getChance(habilidadePlayer));
                        } else if (extra instanceof HabilidadeExtraPerLevel) {
                            HabilidadeExtraPerLevel a = (HabilidadeExtraPerLevel) extra;
                            player.sendMessage(extra.getExtraType().name() + ": " + a.getChance(habilidadePlayer));
                        } else {
                            player.sendMessage("outro");
                        }
                    }
                }
            }
            return;
        }
        if (args[0].equalsIgnoreCase("addxp")) {
            if (args.length < 4) {
                MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "UseAddXp").send(player);
                return;
            }
            if (!StringUtils.isInteger(args[3])) {
                MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "InvalidAmount").send(player);
                return;
            }
            //          [0]   [1]  [2]    [3]
//				/habilidade addxp vtr_ mining 10
            Habilidade habilidade = HabilidadePlugin.getManager().getHabilidadeByTypeName(args[2]);
            if (habilidade == null) {
                MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "HabilidadeNotFound").send(player);
                return;
            }
            int xp = Integer.parseInt(args[3]);

            HabilidadeUser targetPlayer = HabilidadePlugin.getModuleFactory().getUserModule(args[1]);
            if (targetPlayer == null) {
                MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "PlayerNotFound").send(player);
            } else {
                HabilidadeInfo habilidadeInfo = targetPlayer.getHabilidade(habilidade.getType());
                if (habilidadeInfo == null) {
                    MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "HabilidadeNotFound").send(player);
                    return;
                }
                habilidadeInfo.setXp(habilidadeInfo.getXp() + xp);
                habilidadeInfo.getHabilidade().canLevelUP(habilidadePlayer);

                Map<String, String> replacers = new HashMap<>();
                replacers.put("%xp%", Integer.toString(xp));
                replacers.put("%player%", targetPlayer.getNetworkUser().getName());

                MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "XpAdded").replace(replacers).send(player);

            }
            return;
        }
        HabilidadesInventory.open(player, player.getName());
        sendHabilidades(player, habilidadePlayer);
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
