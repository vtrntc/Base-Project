package com.vtr.habilidades.habilidades.axes.extras;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.vtr.api.utils.MathUtils;
import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.habilidades.HabilidadeExtra;
import com.vtr.habilidades.habilidades.HabilidadeExtraType;
import com.vtr.habilidades.objects.HabilidadeInfo;
import com.vtr.habilidades.objects.HabilidadePlayer;
import com.vtr.habilidades.objects.HabilidadeType;

public class TreeCut extends HabilidadeExtra {

	private double perLevel;
	
	private double maxChance;
	
	private List<Material> wood = Arrays.asList(Material.LOG, Material.LOG_2);
	
	public TreeCut(double perLevel, double maxChance) {
		super(HabilidadeType.AXES, HabilidadeExtraType.TREE_CUT);
		this.perLevel = perLevel;
		this.maxChance = maxChance;
	}
	
	private boolean isWood(Material material) {
		return wood.contains(material);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		
		ItemStack item = p.getItemInHand();
		if(item != null) {
			if(habilidade.isTool(item.getType())) {
				Block block = e.getBlock();
				if(block != null) {
					if(isWood(block.getType())) {
						HabilidadePlayer habilidadePlayer = HabilidadePlugin.getManager().getPlayer(p.getName());
						
						HabilidadeInfo habilidadeInfo = habilidadePlayer.getHabilidade(habilidade.getType());
						if(habilidadeInfo != null) {
							double chance = habilidadeInfo.getLevel() * perLevel;
							if(chance > maxChance) {
								chance = maxChance;
							}
							
							if(MathUtils.percentDouble(chance, 100)) {
								while(isWood(block.getType())) {
									block.breakNaturally();
									block = block.getRelative(BlockFace.UP);
								}
							}
						}
					}
				}
			}
		}
	}
}
