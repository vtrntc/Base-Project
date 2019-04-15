package com.vtr.habilidades.objects;

import java.util.List;

import org.bukkit.Material;

public class HabilidadeBlock {

	private Material material;
	
	private double xp;
	
	private List<HabilidadeDrop> drops;

	public HabilidadeBlock(Material material, double xp, List<HabilidadeDrop> drops) {
		this.material = material;
		this.xp = xp;
		this.drops = drops;
	}
	
	public List<HabilidadeDrop> getDrops() {
		return drops;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public double getXp() {
		return xp;
	}
	
	public boolean isDrop(HabilidadeDrop drop) {
		return drops.contains(drop);
	}
}
