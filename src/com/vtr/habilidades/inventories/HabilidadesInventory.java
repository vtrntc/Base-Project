package com.vtr.habilidades.inventories;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import com.vtr.api.builders.ItemBuilder;
import com.vtr.api.inventory.CustomInventory;
import com.vtr.api.inventory.InventoryItem;
import com.vtr.api.utils.InventoryUtils;
import com.vtr.api.utils.StringUtils;
import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.objects.HabilidadeInfo;
import com.vtr.habilidades.objects.HabilidadePlayer;
import com.vtr.habilidades.objects.HabilidadeType;

public class HabilidadesInventory {

	public static void open(Player player, String target) {
		CustomInventory customInventory = InventoryUtils.loadInventory(HabilidadePlugin.getYamlConfig(), "Menus.Habilidades");
		customInventory.setExpire(true);
		
		customInventory.setInventoryAction((e) -> {
			e.setCancelled(true);
		});
		
		HabilidadePlayer habilidadePlayer = HabilidadePlugin.getManager().getPlayer(target);
		
		for(InventoryItem inventoryItem : customInventory.getItems()) {
			switch(inventoryItem.getName()) {
				default:
					HabilidadeType habilidadeType = HabilidadeType.getType(inventoryItem.getName());
					if(habilidadeType != null) {
						HabilidadeInfo info = habilidadePlayer.getHabilidade(habilidadeType);
						if(info != null) {
							ItemBuilder itemBuilder = new ItemBuilder(inventoryItem.getOriginalItem().clone());
							
							double maxXp = info.getHabilidade().getXPToNextLevel(info);
							
							Map<String, String> replacers = new HashMap<>();
							replacers.put("%skill_level%", Integer.toString(info.getLevel()));
							replacers.put("%skill_rank%", Integer.toString(1));
							replacers.put("%skill_percent%", Integer.toString((int) ((info.getXp() / maxXp) * 100)));
							replacers.put("%skill_xp%", StringUtils.formatDouble(info.getXp()));
							replacers.put("%skill_xpmax%", StringUtils.formatDouble(maxXp));
							
							itemBuilder.replaceName(replacers);
							itemBuilder.replaceLore(replacers);
							
							inventoryItem.setItem(itemBuilder.build());
						}
					}
					
					break;
			}
		}
		
		customInventory.open(player);
	}
}
