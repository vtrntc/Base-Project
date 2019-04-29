package com.vtr.habilidades.commands;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.vtr.api.shared.utils.StringUtils;
import com.vtr.api.spigot.commands.old.SubCommand;
import com.vtr.api.spigot.message.MessageUtils;
import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.habilidades.Habilidade;
import com.vtr.habilidades.objects.HabilidadeInfo;
import com.vtr.habilidades.user.HabilidadeUser;

public class SetLevelCommand extends SubCommand {

	public SetLevelCommand() {
		super("addxp", "skills.addxp");
	}

	public boolean execute(CommandSender sender, String label, String[] args) {
		Player player = (Player) sender;
        if (args.length < 4) {
            MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "UseAddXp").send(player);
        }else if (!StringUtils.isInteger(args[3])) {
            MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "InvalidAmount").send(player);
        }else{
	        //          [0]   [1]  [2]    [3]
	//			/habilidade addxp vtr_ mining 10
	        Habilidade habilidade = HabilidadePlugin.getManager().getHabilidadeByTypeName(args[2]);
	        if (habilidade == null) {
	            MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "HabilidadeNotFound").send(player);
	        }else{
		        int xp = Integer.parseInt(args[3]);
		
		        HabilidadeUser targetPlayer = HabilidadePlugin.getModuleFactory().getUserModule(args[1]);
		        if (targetPlayer == null) {
		            MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "PlayerNotFound").send(player);
		        } else {
		            HabilidadeInfo habilidadeInfo = targetPlayer.getHabilidade(habilidade.getType());
		            if (habilidadeInfo == null) {
		                MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "HabilidadeNotFound").send(player);
		            }else{
			            habilidadeInfo.setXp(habilidadeInfo.getXp() + xp);
			            habilidadeInfo.getHabilidade().canLevelUP(targetPlayer);
			
			            Map<String, String> replacers = new HashMap<>();
			            replacers.put("%xp%", Integer.toString(xp));
			            replacers.put("%player%", targetPlayer.getNetworkUser().getName());
			
			            MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "XpAdded").replace(replacers).send(player);
		            }
		        }
	        }
        }
		return false;
	}
}
