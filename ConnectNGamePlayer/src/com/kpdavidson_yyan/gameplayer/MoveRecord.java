/**
 * Kyle Davidson
 * Yan Yan
 */
package com.kpdavidson_yyan.gameplayer;


/**
 * The data structure to store a move and its evaluated value
 * @author Kyle & Yan
 *
 */
public class MoveRecord {
	
	/**
	 * The constructor of a move record
	 * @param player the player of this move
	 * @param column the column to be affected by the move
	 * @param movetype whether the move is a drop or a pop
	 */
	public MoveRecord(Integer player, Integer column, Integer movetype) {
		this.player = player;
		this.column = column;
		this.movetype = movetype;
		this.value = null;
	}
	private Integer player;
	private Integer column;
	private Integer movetype;
	private Double value;
	
	/**
	 * Get the player of this move
	 * @return player
	 */
	public Integer getPlayer() {
		return player;
	}
	
	/**
	 * Set the value of the move
	 * @param value to be set to the move
	 */
	public void setValue(Double value) {
		this.value = value;
	}
	
	/**
	 * Get the column that this move is made to
	 * @return the column number
	 */
	public Integer getColumn() {
		return column;
	}
	
	/**
	 * Get the move type (drop or pop)
	 * @return the type of the move
	 */
	public Integer getMovetype() {
		return movetype;
	}
	
	/**
	 * Get the evaluate value of this move
	 * @return value of the move
	 */
	public Double getValue() {
		return value;
	}
	
}
