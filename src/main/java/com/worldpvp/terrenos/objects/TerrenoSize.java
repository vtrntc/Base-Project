package com.worldpvp.terrenos.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import com.worldpvp.terrenos.WorldTerrenos;
import com.worldpvp.utils.builders.ItemBuilder;

public class TerrenoSize {

	private String name;

	private ItemStack icon;
	
	private int slot;
	private int cost;
	private int size;
	
	private int maxSpawners;
	private int maxMachines;
	private int maxSuperSpawners;
	
	private static List<TerrenoSize> sizes = new ArrayList<>();
	
	private TerrenoSize(String name, ItemStack icon, int cost, int size, int slot, int maxSpawners, int maxMachines, int maxSuperSpawners) {
		this.name = name;
		this.icon = icon;
		this.size = size;
		this.cost = cost;
		this.slot = slot;
		this.maxSpawners = maxSpawners;
		this.maxMachines = maxMachines;
		this.maxSuperSpawners = maxSuperSpawners;
	}
	
	public static void load() {
		FileConfiguration config = WorldTerrenos.getInstance().getConfig();
		
		for(String e : config.getConfigurationSection("Menus.Comprar.Tamanhos").getKeys(false)) {
			ItemBuilder icon = new ItemBuilder(config.getString("Menus.Comprar.Tamanhos." + e + ".Icone.ID"));
			
			icon.setName(config.getString("Menus.Comprar.Tamanhos." + e + ".Icone.Nome"));
			icon.setLore(config.getStringList("Menus.Comprar.Tamanhos." + e + ".Icone.Lore"));
			icon.setAmount(config.getInt("Menus.Comprar.Tamanhos." + e + ".Icone.Amount"));
			
			sizes.add(new TerrenoSize(e, icon.build(), config.getInt("Menus.Comprar.Tamanhos." + e + ".Custo"), config.getInt("Menus.Comprar.Tamanhos." + e + ".Tamanho"), config.getInt("Menus.Comprar.Tamanhos." + e + ".Slot"), config.getInt("Menus.Comprar.Tamanhos." + e + ".MaxSpawners"), config.getInt("Menus.Comprar.Tamanhos." + e + ".MaxMachines"), config.getInt("Menus.Comprar.Tamanhos." + e + ".MaxSuperSpawners")));
		}
	}
	
	public static TerrenoSize getByName(String name) {
		for(TerrenoSize terrenoSize : TerrenoSize.getSizes()) {
			if(terrenoSize.getName().equalsIgnoreCase(name)) {
				return terrenoSize;
			}
		}
		
		return null;
	}
	
	public static TerrenoSize getByIcon(ItemStack item) {
		if(item != null && item.getType() != Material.AIR) {
			for(TerrenoSize terrenoSize : TerrenoSize.getSizes()) {
				if(terrenoSize.getIcon().equals(item)) {
					return terrenoSize;
				}
			}
		}
		
		return null;
	}
	
	public String getName() {
		return name;
	}
	
	public ItemStack getIcon() {
		return icon;
	}
	
	public int getSlot() {
		return slot;
	}
	
	public int getCost() {
		return cost;
	}
	
	public int getSize() {
		return size;
	}
	
	public int getMaxMachines() {
		return maxMachines;
	}
	
	public int getMaxSpawners() {
		return maxSpawners;
	}
	
	public int getMaxSuperSpawners() {
		return maxSuperSpawners;
	}
	
	public static List<TerrenoSize> getSizes() {
		return sizes;
	}
}
