package com.vtr.habilidades.habilidades;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.vtr.api.shared.utils.StringUtils;
import com.vtr.api.spigot.APISpigot;
import com.vtr.api.spigot.message.AbstractMessage;
import com.vtr.api.spigot.message.MessageUtils;
import com.vtr.api.spigot.misc.ActionBar;
import com.vtr.api.spigot.misc.RandomChooser;
import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.objects.HabilidadeDrop;
import com.vtr.habilidades.objects.HabilidadeInfo;
import com.vtr.habilidades.objects.HabilidadePlayer;
import com.vtr.habilidades.objects.HabilidadeType;

public abstract class Habilidade implements Listener {

	protected HabilidadeType type;
	
	private String name;
	
	protected List<HabilidadeDrop> drops;
	
	private List<HabilidadeExtra> extras;
	
	private List<Material> tools;
	
	protected RandomChooser<HabilidadeDrop> dropsRandomChooser;

	public Habilidade(HabilidadeType type, String name, List<HabilidadeDrop> drops, List<Material> tools) {
		this.type = type;
		this.name = name;
		this.tools = tools;
		this.drops = drops;
		this.extras = new ArrayList<>();
		this.dropsRandomChooser = new RandomChooser<>();
		
		for(HabilidadeDrop drop : drops) {
			dropsRandomChooser.option(drop, drop.getChance());
		}
		
		APISpigot.registerListener(HabilidadePlugin.getInstance(), this);
	}
	
	public String getName() {
		return name;
	}
	
	public HabilidadeType getType() {
		return type;
	}
	
	protected List<HabilidadeDrop> getDropsForLevel(int level) {
		return drops.stream().filter(d -> level >= d.getMinLevel()).collect(Collectors.toList());
	}
	
	protected List<HabilidadeDrop> getDropsForLevel(List<HabilidadeDrop> habilidadeDrops, int level) {
		return habilidadeDrops.stream().filter(hd -> level >= hd.getMinLevel()).collect(Collectors.toList());
	}
	
	public boolean isTool(Material material) {
		return tools.contains(material);
	}
	
	protected void giveXp(HabilidadePlayer habilidadePlayer, HabilidadeInfo habilidadeInfo, double xp) {
		habilidadeInfo.setXp(habilidadeInfo.getXp() + xp);
		habilidadeInfo.setNeedUpdate(true);
		
		habilidadePlayer.setNeedUpdate(true);
	}
	
	public void registerHabilidadeExtra(HabilidadeExtra extra) {
		this.extras.add(extra);
	}
	
	public HabilidadeExtra getHabilidadeExtra(HabilidadeExtraType extraType) {
		return extras.stream().filter(e -> e.getExtraType() == extraType).findFirst().orElse(null);
	}
	
	public boolean canLevelUP(HabilidadePlayer habilidadePlayer) {
		HabilidadeInfo habilidadeInfo = habilidadePlayer.getHabilidade(type);
		if(habilidadeInfo != null) {
			int xpToNextLevel = getXpToLevel(habilidadeInfo.getLevel(), habilidadeInfo.getLevel() + 1);
			if(habilidadeInfo.getXp() >= xpToNextLevel) {
				Map<String, String> replacers = new HashMap<>();
				replacers.put("%player%", habilidadePlayer.getPlayer());
				replacers.put("%level%", Integer.toString(habilidadeInfo.getLevel() + 1));
				replacers.put("%habilidade%", habilidadeInfo.getHabilidade().getName());
				
				AbstractMessage abstractMessage = MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "LevelMessage");
				abstractMessage.replace(replacers);
				
				for(Player on : Bukkit.getOnlinePlayers()) {
					abstractMessage.send(on);
				}
				
				habilidadePlayer.setNeedUpdate(false);
				
				double xp = habilidadeInfo.getXp() - xpToNextLevel;
				
				habilidadeInfo.setLevel(habilidadeInfo.getLevel() + 1);
				habilidadeInfo.setXp(xp);
				habilidadeInfo.save();
				return true;
			}
		}
		
		return false;
	}
	
	protected void sendActionBar(Player player, HabilidadeInfo habilidadeInfo, double xpWon) {
		double maxXp = habilidadeInfo.getHabilidade().getXPToNextLevel(habilidadeInfo);
		
		Map<String, String> replacers = new HashMap<>();
		replacers.put("%max%", StringUtils.formatDouble(maxXp));
		replacers.put("%habilidade%", habilidadeInfo.getHabilidade().getName());
		replacers.put("%level%", Integer.toString(habilidadeInfo.getLevel()));
		replacers.put("%xp%", StringUtils.formatDouble(habilidadeInfo.getXp()));
		replacers.put("%received%", StringUtils.formatDouble(xpWon));
		replacers.put("%percent%", Integer.toString((int) ((habilidadeInfo.getXp() / maxXp) * 100)));
		
		ActionBar.sendActionBar(player, MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "ActionBar").replace(replacers).asString());
	}
	
	public int getXPToNextLevel(HabilidadeInfo habilidadeInfo) {
		return getXpToLevel(habilidadeInfo.getLevel(), habilidadeInfo.getLevel() + 1);
	}
	
	private int getXpToLevel(int levelAtual, int levelTarget) {
		return 10 * (levelTarget-levelAtual) * (levelTarget+levelAtual+101);
	}
}
