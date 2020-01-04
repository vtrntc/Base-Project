package com.vtr.baseproject;

import org.bukkit.plugin.java.JavaPlugin;

import com.worldnetwork.api.spigot.misc.YamlConfig;

public class BaseProjectPlugin extends JavaPlugin {

	private static BaseProjectPlugin plugin;
	
	private static YamlConfig yamlConfig;
	
	public void onEnable() {
		plugin = this;
		
		saveDefaultConfig();
		
		yamlConfig = new YamlConfig("config", plugin);
	}
	
	public static BaseProjectPlugin getInstance() {
		return plugin;
	}
	
	public static YamlConfig getYamlConfig() {
		return yamlConfig;
	}
}
