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

/**
 * 
 * @author Kyle & Yan
 * 
 * Assume that the maximum allowed time to compute a move is 10 seconds
 *
 */
public class TestPlayer {

	static final String playerName="kpdavidson_yyan";
	static BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
	
	static int[][] gameboard; // a 1 indicates that we own that space, -1 indicates that enemy owns the space,
					   // 0 indicates an unowned space
					   // [Row][Column]
	static int numrows; //number of rows on the board
	static int numcolumns; //number of columns on the board
	static int winlength; //number of pieces in a row to win
	static long timelimit; //time dedicated to running computation
	static boolean wePopped = false; //boolean ind
	static boolean theyPopped = false;

	/**
	 * Performs a drop on the board
	 * @param player indicates the player doing the move (1=us, -1=enemy)
	 * @param column the column number of the drop (0,1,...)
	 */
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
	
	/**
	 * Performs a pop move on the board
	 * @param column the column being popped
	 */
	static void makePopMove(int column) {
		int row;
		for(row=0;row<(numrows - 1);row++) {
			gameboard[row][column] = gameboard[row + 1][column];
		}
		gameboard[row][column] = 0;
	}
	
	/**
	 * Takes our move represented by 2 numbers and makes it
	 * @param ourMove the move to be made represented by two numbers
	 */
	static void Determine_and_move(int[] ourMove) {
		//make move
		if(ourMove[1] == 1){
			makeDropMove(1, ourMove[0]);
		} else if(ourMove[1] == 0){
			makePopMove(ourMove[0]);
			wePopped = true;
		}
			
		
	}
	
	/**
	 * Main program driving the player
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		System.out.println(TestPlayer.playerName);
//		 fw.write(TestPlayer.playerName);
		
		Logger.init();	//comment out if we no longer need it
		
		String str = input.readLine();		//throw out player name line
		Logger.log(str);
		str = input.readLine();				//read game config
		Logger.log(str);

		String[] gameConfig = str.split(" ");
		if(gameConfig.length != 5){
			System.out.println("BAD INPUT");
			return;
		}
		MoveAlgorithm.begintime = System.currentTimeMillis();
		numrows = Integer.parseInt(gameConfig[0]);
		numcolumns = Integer.parseInt(gameConfig[1]);
		gameboard = new int[numrows][numcolumns];
		winlength = Integer.parseInt(gameConfig[2]);
		timelimit = ((Integer.parseInt(gameConfig[4])) * 1000) / 4 * 3; //get 3/4 of the allowed time
		
		if(Integer.parseInt(gameConfig[3]) == 1) { //we start first
			//run getBestMove
			int startlevel = 2;
			
			int[] ourMove = MoveAlgorithm.doBestMove(startlevel);
			
			while(MoveAlgorithm.completed) {
				startlevel++;
				int ournewMove[] = MoveAlgorithm.doBestMove(startlevel);
				if(MoveAlgorithm.completed) {
					ourMove = ournewMove;
				}
			}
			
			//make the move
			Determine_and_move(ourMove);
			//output our move
			System.out.println(ourMove[0] + " " + ourMove[1]);
			System.out.flush();
		}
		
		
		boolean continueGame = true;
		while(continueGame) {
			MoveAlgorithm.turns++;
	    	String s=input.readLine();
	    	MoveAlgorithm.begintime = System.currentTimeMillis();
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
				int startlevel = 2;
				
				int[] ourMove = MoveAlgorithm.doBestMove(startlevel);
				
				while(MoveAlgorithm.completed) {
					startlevel++;
					int ournewMove[] = MoveAlgorithm.doBestMove(startlevel);
					if(MoveAlgorithm.completed) {
						ourMove = ournewMove;
					}
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
			
			StringBuilder debugString = new StringBuilder();
			debugString.append("\r\nboard-----\r\n");
			for(int i = gameboard.length - 1; i >= 0 ; i--){
				for(int j = 0; j < gameboard[0].length ; j++){
				
					debugString.append((gameboard[i][j] == 0 ? 0 : (gameboard[i][j] == -1 ? 2 : 1)) + " ");
				}
				debugString.append("\r\n");
			}
			debugString.append("\r\n-----board\r\n");
			
			Logger.log(debugString.toString());
		}
	}

}
