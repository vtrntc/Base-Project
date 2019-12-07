package com.worldpvp.terrenos.objects;

import java.util.ArrayList;

import org.bukkit.Location;

public class Square {
	
	private Location location1;
	private Location location2;
	
	public Square(Location l1, Location l2) {
		if(l1.getWorld().equals(l2.getWorld())) {
			throw new IllegalArgumentException("As duas locations devem pertencer ao mesmo mundo");
		}
		
		this.location1 = new Location(l1.getWorld(), Math.max(l1.getX(), l2.getX()), 0, Math.max(l1.getZ(), l2.getZ()));
		this.location2 = new Location(l1.getWorld(), Math.min(l1.getX(), l2.getX()), 0, Math.min(l1.getZ(), l2.getZ()));
	}
	
	public Square(Location center, int radius) {
		location1 = center.clone().add(radius + (radius % 2 == 1 ? 1 : 0), 0, radius + (radius % 2 == 1 ? 1 : 0));
		location1.setY(0);
		location2 = center.clone().subtract(radius, 0, radius);
		location2.setY(0);
	}
	
	public ArrayList<Location> getSquareLocations(int y) {
		ArrayList<Location> result = new ArrayList<>();
		for(int x = location2.getBlockX(); x <= location1.getBlockX(); x++) {
			for(int z = location2.getBlockZ(); z <= location1.getBlockZ(); z++) {
				if(location1.getBlockX() == x || location2.getBlockX() == x || location1.getBlockZ() == z || location2.getBlockZ() == z) {
					result.add(new Location(location1.getWorld(), x, y, z));
				}
			}
		}
		return result;
	}
	
	public Location getMinLocation () {
		return location2;
	}
	
	public Location getMaxLocation () {
		return location1;
	}
}