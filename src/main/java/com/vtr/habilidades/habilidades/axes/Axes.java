package com.vtr.habilidades.habilidades.axes;

import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.habilidades.Habilidade;
import com.vtr.habilidades.habilidades.extra.HabilidadeExtra;
import com.vtr.habilidades.habilidades.extra.HabilidadeExtraType;
import com.vtr.habilidades.objects.HabilidadeBlock;
import com.vtr.habilidades.objects.HabilidadeDrop;
import com.vtr.habilidades.objects.HabilidadeInfo;
import com.vtr.habilidades.objects.HabilidadeType;
import com.vtr.habilidades.user.HabilidadeUser;

public class Axes extends Habilidade {

	private Map<Material, HabilidadeBlock> blocks;
	
	public Axes(String name, List<HabilidadeDrop> drops, List<Material> tools, List<HabilidadeExtra> extras, Map<Material, HabilidadeBlock> blocks) {
		super(HabilidadeType.AXES, name, drops, tools, extras);
		this.blocks = blocks;
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled= true)
	private void onBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		
		ItemStack item = p.getItemInHand();
		if(item != null && isTool(item.getType())) {
			HabilidadeUser habilidadePlayer = HabilidadePlugin.getManager().getPlayer(p.getName());
			
			HabilidadeInfo habilidadeInfo = habilidadePlayer.getHabilidade(type);
			if(habilidadeInfo != null) {
				Block block = e.getBlock();
				if(blocks.containsKey(block.getType())) {
					HabilidadeBlock habilidadeBlock = blocks.get(block.getType());
					
					giveXp(habilidadePlayer, habilidadeInfo, habilidadeBlock.getXp());
					
					sendActionBar(p, habilidadeInfo, habilidadeBlock.getXp());
				}
				
				getHabilidadeExtra(HabilidadeExtraType.TREE_CUT).activate(e);
			}
		}
	}
}
