package com.vtr.habilidades.commands;

import org.bukkit.command.CommandSender;

import com.vtr.api.shared.command.CommandRestriction;
import com.vtr.api.spigot.commands.SubCommand;
import com.vtr.api.spigot.user.User;

public class TopCommand extends SubCommand {

	public TopCommand() {
		super("top", CommandRestriction.INGAME);
	}

	public void onCommand(CommandSender sender, User user, String[] args) {
		
	}
}
