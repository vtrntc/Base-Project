package com.vtr.habilidades.objects;

import com.vtr.habilidades.habilidades.Habilidade;

public class HabilidadeInfo {

	private Habilidade habilidade;
	
	private int level;
	
	private double xp;
	
	private boolean needUpdate;

	public HabilidadeInfo(Habilidade habilidade, int level, double xp) {
		this.habilidade = habilidade;
		this.level = level;
		this.xp = xp;
	}
	
	public Habilidade getHabilidade() {
		return habilidade;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public double getXp() {
		return xp;
	}
	
	public void setXp(double xp) {
		this.xp = xp;
	}
	
	public boolean isNeedUpdate() {
		return needUpdate;
	}
	
	public void setNeedUpdate(boolean needUpdate) {
		this.needUpdate = needUpdate;
	}
	
//	public void save() {
//		Map<String, String> sql = new LinkedHashMap<>();
//		sql.put("name", player);
//		sql.put("habilidade", habilidade.getType().name());
//		
//		Map<String, String> update = new LinkedHashMap<>(sql);
//		update.put("level", Integer.toString(level));
//		update.put("xp", Double.toString(xp));
//		
//		DatabaseUtils.insertIfExistUpdate("player_habilidades", sql, update);
//	}
}
