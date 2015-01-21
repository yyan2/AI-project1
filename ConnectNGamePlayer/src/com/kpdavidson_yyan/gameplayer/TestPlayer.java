/**
 * Kyle Davidson
 * Yan Yan
 */

package com.kpdavidson_yyan.gameplayer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public class TestPlayer {

	String playerName="kpdavidson_yyan";
	BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
	
	static int[][] gameboard; // a 1 indicates that we own that space, -1 indicates that enemy owns the space,
					   // 0 indicates an unowned space
					   // [Row][Column]
	static int numrows;
	static int numcolumns;
	static int winlength; //number of pieces in a row to win
	static int timelimit;
	static boolean wePopped;
	static boolean theyPopped;
	
	public boolean processInput() throws IOException{	
	
		boolean returnValue = true;
		
    	String s=input.readLine();	
		List<String> ls=Arrays.asList(s.split(" "));
		if(ls.size()==2){
			//opponents move
			if(ls.get(1).equals("1")) {
				makeDropMove(-1, Integer.parseInt(ls.get(0)));
			}
			else {
				makePopMove(Integer.parseInt(ls.get(0)));
				theyPopped = true;
			}
			
			//run getBestMove
			//make the move
		}
		else if(ls.size()==1){
			System.out.println("game over!!!");
		}
		else if(ls.size()==5){          //ls contains game info
			init(ls);
		}
		else
			System.out.println("not what I want");
		
		return returnValue;
	}
	
	void init(List<String> ls) {
		numrows = Integer.parseInt(ls.get(0));
		numcolumns = Integer.parseInt(ls.get(1));
		gameboard = new int[numrows][numcolumns];
		winlength = Integer.parseInt(ls.get(2));
		timelimit = Integer.parseInt(ls.get(4));
		wePopped = false;
		theyPopped = false;
		
		if(Integer.parseInt(ls.get(3)) == 1) {
			//TODO run getBestMove
			//make move
		}
	}
	
	void makeDropMove(int player, int column) {
		int row = 0;
		
		while(true) {
			if(gameboard[row][column] == 0) {
				gameboard[row][column] = player;
				break;
			}
			row++;
		}
	}
	
	void makePopMove(int column) {
		int row;
		for(row=0;row<(numrows - 1);row++) {
			gameboard[row][column] = gameboard[row + 1][column];
		}
		gameboard[row][column] = 0;
	}
	
	
	void Determine_and_move() {
		
	}
	
	public static void main(String[] args) throws IOException {
		TestPlayer rp=new TestPlayer();
		System.out.println(rp.playerName);
		
		boolean continueGame = true;
		while(continueGame) {
			continueGame = rp.processInput();
		}
	}

}
