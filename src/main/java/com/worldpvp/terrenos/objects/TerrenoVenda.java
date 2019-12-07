package com.worldpvp.terrenos.objects;

import org.bukkit.block.Sign;

public class TerrenoVenda {

	private Sign sign;
	private int price;
	
	public TerrenoVenda(Sign sign, int price) {
		this.sign = sign;
		this.price = price;
	}

	public Sign getSign() {
		return sign;
	}

	public int getPrice() {
		return price;
	}
}
