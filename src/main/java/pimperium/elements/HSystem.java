package pimperium.elements;

import pimperium.players.Player;

import java.io.Serializable;

public class HSystem implements Serializable {

	private static final long serialVersionUID = 1L;

	private int level;
	private Hexagon hex;
	private Player controller;
	
	public HSystem(int level) {
		this.level = level;
	}
	
	public int getLevel() {
		return this.level;
	}
	
	public void setHex(Hexagon hex) {
		this.hex = hex;
	}

	public Hexagon getHex() {
		return this.hex;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}
