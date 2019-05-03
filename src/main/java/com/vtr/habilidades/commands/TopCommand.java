package com.vtr.habilidades.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.vtr.api.shared.command.CommandRestriction;
import com.vtr.api.spigot.commands.SubCommand;
import com.vtr.api.spigot.user.User;
import com.vtr.habilidades.inventories.HabilidadeTopInventory;
import com.vtr.habilidades.objects.HabilidadeType;

public class TopCommand extends SubCommand {

	public TopCommand() {
		super("top", CommandRestriction.INGAME);
	}

	public void onCommand(CommandSender sender, User user, String[] args) {
		HabilidadeTopInventory.open((Player) sender, HabilidadeType.MINING);
	}
}
