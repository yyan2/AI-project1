package com.kpdavidson_yyan.gameplayer;

public class MoveRecord {
	
	public MoveRecord(Integer player, Integer column, Integer movetype) {
		this.player = player;
		this.column = column;
		this.movetype = movetype;
		this.value = null;
	}
	Integer player;
	Integer column;
	Integer movetype;
	Double value;
	
	public Integer getPlayer() {
		return player;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	public Integer getColumn() {
		return column;
	}
	public Integer getMovetype() {
		return movetype;
	}
	public Double getValue() {
		return value;
	}
	
}
