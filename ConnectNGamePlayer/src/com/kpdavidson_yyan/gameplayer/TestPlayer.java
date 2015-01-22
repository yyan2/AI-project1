/**
 * Kyle Davidson
 * Yan Yan
 */

package com.kpdavidson_yyan.gameplayer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;


public class TestPlayer {

	static final String playerName="kpdavidson_yyan";
	static BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
	
	static int[][] gameboard; // a 1 indicates that we own that space, -1 indicates that enemy owns the space,
					   // 0 indicates an unowned space
					   // [Row][Column]
	static int numrows;
	static int numcolumns;
	static int winlength; //number of pieces in a row to win
	static int timelimit;
	static boolean wePopped = false;
	static boolean theyPopped = false;

	
	static void makeDropMove(int player, int column) {
		int row = 0;
		
		while(true) {
			if(gameboard[row][column] == 0) {
				gameboard[row][column] = player;
				break;
			}
			row++;
		}
	}
	
	static void makePopMove(int column) {
		int row;
		for(row=0;row<(numrows - 1);row++) {
			gameboard[row][column] = gameboard[row + 1][column];
		}
		gameboard[row][column] = 0;
	}
	
	
	static void Determine_and_move(int[] ourMove) {
		//make move
		if(ourMove[1] == 1){
			makeDropMove(1, ourMove[0]);
		} else if(ourMove[1] == 0){
			makePopMove(ourMove[0]);
			wePopped = true;
		}
			
		
	}
	
	public static void main(String[] args) throws IOException {
		
		System.out.println(TestPlayer.playerName);
		System.out.flush();
		
		String str = input.readLine();		//read game config
		String[] gameConfig = str.split(" ");
		
		if(gameConfig.length != 5){
			System.out.println("BAD INPUT");
			return;
		}
		
		numrows = Integer.parseInt(gameConfig[0]);
		numcolumns = Integer.parseInt(gameConfig[1]);
		gameboard = new int[numrows][numcolumns];
		winlength = Integer.parseInt(gameConfig[2]);

	
		timelimit = Integer.parseInt(gameConfig[4]);
		
		if(Integer.parseInt(gameConfig[3]) == 1) {
			//run getBestMove
			long TimeLeft = timelimit * 1000000000;
			long beginTime = System.nanoTime();
			int startlevel = 2;
			
			int[] ourMove = MoveAlgorithm.doBestMove(startlevel);
			long TimeElapsed = System.nanoTime() - beginTime;
			TimeLeft = TimeLeft - TimeElapsed;
			
			while(TimeLeft > (TimeElapsed + (TimeElapsed * (numcolumns * 2)))) {
				beginTime = System.nanoTime();
				startlevel++;
				ourMove = MoveAlgorithm.doBestMove(startlevel);
				TimeElapsed = System.nanoTime() - beginTime;
				TimeLeft = TimeLeft - TimeElapsed;
			}
			
			//make the move
			Determine_and_move(ourMove);
			//output our move
			System.out.println(ourMove[0] + " " + ourMove[1]);
			System.out.flush();
		}
		
		
		boolean continueGame = true;
		while(continueGame) {
			
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
				long TimeLeft = timelimit * 1000000000;
				long beginTime = System.nanoTime();
				int startlevel = 2;
				
				int[] ourMove = MoveAlgorithm.doBestMove(startlevel);
				long TimeElapsed = System.nanoTime() - beginTime;
				TimeLeft = TimeLeft - TimeElapsed;
				
				System.out.println("TimeLeft: " + TimeLeft + " Next's Cost: " + (TimeElapsed + (TimeElapsed * (numcolumns * 2))));
				while(TimeLeft > (TimeElapsed + (TimeElapsed * (numcolumns * 2)))) {
					beginTime = System.nanoTime();
					startlevel++;
					ourMove = MoveAlgorithm.doBestMove(startlevel);
					TimeElapsed = System.nanoTime() - beginTime;
					TimeLeft = TimeLeft - TimeElapsed;
				}
		
				//make the move
				Determine_and_move(ourMove);
				//output our move
				System.out.println(ourMove[0] + " " + ourMove[1]);
				System.out.flush();
				
			}
			else if(ls.size()==1){
				continueGame = false;
				System.out.println("game over!!!");
			}
			else {
				continueGame = false;
				System.out.println("not what I want");
			}
			/* DEBUG
			System.out.println("board--");
			for(int i = gameboard.length - 1; i >= 0 ; i--){
				for(int j = 0; j < gameboard[0].length ; j++){
				
					System.out.print(gameboard[i][j]);
				}
				System.out.println();
			}
			System.out.println("--board");
			*/
		}
	}

}
