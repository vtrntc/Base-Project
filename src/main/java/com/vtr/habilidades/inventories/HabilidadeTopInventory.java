package com.vtr.habilidades.inventories;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import com.vtr.api.shared.utils.StringUtils;
import com.vtr.api.spigot.builders.ItemBuilder;
import com.vtr.api.spigot.inventory.CustomInventory;
import com.vtr.api.spigot.inventory.InventoryItem;
import com.vtr.api.spigot.inventory.loader.InventoryLoader;
import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.habilidades.Habilidade;
import com.vtr.habilidades.objects.HabilidadeInfo;
import com.vtr.habilidades.objects.HabilidadeType;
import com.vtr.habilidades.user.HabilidadeUser;

public class HabilidadeTopInventory {
	
	private static Map<HabilidadeType, CustomInventory> inventories = new HashMap<>();

    public static void open(Player player, HabilidadeType habilidadeType) {
    	if(!inventories.containsKey(habilidadeType)) {
    		updateInventory(habilidadeType);
    	}
    	
    	inventories.get(habilidadeType).open(player);
    }

	public static void updateInventory(HabilidadeType habilidadeType) {
		CustomInventory customInventory = InventoryLoader.loadInventory(HabilidadePlugin.getYamlConfig(), "Menus.Top");
		customInventory.setExpire(true);
		
		customInventory.setInventoryAction((e) -> {
			e.setCancelled(true);
		});
		
		Habilidade habilidade = HabilidadePlugin.getManager().getHabilidadeByTypeName(habilidadeType.name());
		
		Map<String, String> replacers = new HashMap<>();
		replacers.put("%skill%", habilidade.getName());
		
		customInventory.replaceInventoryName(replacers);
		
		for(InventoryItem inventoryItem : customInventory.getItems()) {
			switch(inventoryItem.getName()) {
				case "Ranking":
					List<HabilidadeUser> top = HabilidadePlugin.getManager().getTopUpdater().getTopFromHabilidade(habilidadeType);
					
					int index = 0;
					for(HabilidadeUser user : top) {
						if(index >= inventoryItem.getSlots().size()) {
							index = 0;
						}
						
						HabilidadeInfo habilidadeInfo = user.getHabilidade(habilidadeType);
						if(habilidadeInfo != null) {
							ItemBuilder itemBuilder = new ItemBuilder(inventoryItem.getOriginalItem().clone());
							
							replacers.clear();
							replacers.put("%player%", user.getNetworkUser().getName());
							replacers.put("%position%", Integer.toString(top.indexOf(user) + 1));
							replacers.put("%level%", StringUtils.formatMoney(habilidadeInfo.getLevel()));
							
							itemBuilder.replaceName(replacers);
							itemBuilder.replaceLore(replacers);
							
							customInventory.addItem(new InventoryItem(customInventory, "Ranking", itemBuilder.build(), inventoryItem.getSlots().get(index)));
							 
							index++;
						}
					}
				
				break;
			default:
				break;
			}
		}
		
		inventories.put(habilidadeType, customInventory);
	}
}
