/**
 * Kyle Davidson
 * Yan Yan
 */
package com.kpdavidson_yyan.gameplayer;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Kyle & Yan
 *
 */
public class MoveAlgorithm {
	
	static boolean completed = true; //boolean indicated whether or not the algorithm has completed its work for the last run
	static long begintime; //the time that input was received from the Referee
	static int turns = 0; //the number of turns that have passed in the game
	
	/**
	 * Performs a DFS to the level specified
	 * @param maxlevel determines how low the DFS will go before performing heuristic evaluations
	 * @return An integer array representing the best calculated move
	 */
	public static int[] doBestMove(int maxlevel) {
		
		completed = true; //assume that we do finish
		int[] bestmove = new int[2];// the move being returned
		List<MoveRecord> possibleMoves = new ArrayList<MoveRecord>(); //a list of all possible moves from the current board state
		
		//get all our possible moves
		int i;
		for(i=0;i<TestPlayer.numcolumns; i++) {
			if(TestPlayer.gameboard[TestPlayer.numrows-1][i] == 0) {
				possibleMoves.add(new MoveRecord(1, i, 1));
				
			}
		}
		if(!TestPlayer.wePopped) {
			for(i=0;i<TestPlayer.numcolumns; i++) {
				if(TestPlayer.gameboard[0][i] == 1) {
					possibleMoves.add(new MoveRecord(1, i, 0));
					
				}
			}
		}
		
		//for all possible moves evaluate how good they are
		Double bestValue = -99999.9; //begin assuming worst case scenario (for alpha beta pruning)
		for(MoveRecord r : possibleMoves) {
			if(TestPlayer.wePopped) {
				EvaluateMove(r, TestPlayer.gameboard, 1, 2, true, TestPlayer.theyPopped, bestValue, maxlevel);
			}
			else {
				EvaluateMove(r, TestPlayer.gameboard, 1, 2, false, TestPlayer.theyPopped, bestValue, maxlevel);
			}
			if(r.getValue() > bestValue) bestValue = r.getValue();
		}
		
		// choose move with highest value
		MoveRecord best = new MoveRecord(1, null, null);
		best.setValue(-99999.9);
		for(MoveRecord r : possibleMoves) {
			if(r.getValue() > best.getValue()) best = r;
			Logger.log("value is " + r.getValue() + " with move of " + r.getColumn() + " and type of " + r.getMovetype());
			
		}
		
		bestmove[0] = best.getColumn();
		bestmove[1] = best.getMovetype();
		
		
		return bestmove;
	}
	
	/**
	 * A recursive move evaluation
	 * @param desiredMove The move to be evaluated
	 * @param gboard The gameboard as it is when the move is being evaluated
	 * @param lastmoved The player who made "desiredMove" (1=us, -1=enemy)
	 * @param level The depth of this evaluation in the DFS
	 * @param weAlreadyPopped Boolean indicating that we have used our Pop already
	 * @param theyAlreadyPopped Boolean indicating that enemy has used their Pop already
	 * @param parentBest The current best value of the parent node (for Alpha-Beta pruning)
	 * @param maxlevel How far down the DFS can go
	 */
	private static void EvaluateMove(MoveRecord desiredMove, int[][] gboard, int lastmoved, int level, boolean weAlreadyPopped,
			boolean theyAlreadyPopped, Double parentBest, int maxlevel) {
		int currentLevel = level + 1;
		
		//Logger.log(System.currentTimeMillis() - begintime + " > " + TestPlayer.timelimit);
		if(System.currentTimeMillis() - begintime > TestPlayer.timelimit) {
			completed = false;
			desiredMove.setValue(99999.9); //this is for possible null pointer problems, value is not used due to timeout
			return;
		}
		
		boolean wepopped = weAlreadyPopped;
		boolean theypopped = theyAlreadyPopped;
		
		//copy game board
		int[][] board = new int[TestPlayer.numrows][TestPlayer.numcolumns];
		copyBoard(gboard, board);
		
		//perform desiredMove
		if(desiredMove.getMovetype() == 1) {
			makeDropMove(lastmoved, desiredMove.getColumn(), board);
			
		}
		else {
			makePopMove(desiredMove.getColumn(), board);
			if(desiredMove.getPlayer() == 1) wepopped = true;
			else theypopped = true;
		}
		
		
		//game over check, if game is over stop and set value that cannot be ignored
		int govercheck = gameOverCheck(board);
		if(govercheck >= -1 && govercheck <= 1){
			double finalValue = 0.0; //assume draw
			if(govercheck == 1) {
				finalValue = 9999999999.0 / level; //we win
			} else if(govercheck == -1) {
				finalValue = -9999999999.0 / level; //we lost
			}
			
			desiredMove.setValue(finalValue);
			
			return;
			
		}
		
		//if we must stop perform heuristic
		if(level == maxlevel) {
//			Logger.log("Performing Heuristic at Level " + level);
			desiredMove.setValue(heuristicEval(board));
			return;
		}
		
		// Begin evaluating all possible moves after desiredMove
		int player = lastmoved * -1;
		
		List<MoveRecord> possibleMoves = new ArrayList<MoveRecord>();
		
		//get all possible moves
		int i;
		for(i=0;i<TestPlayer.numcolumns; i++) {
			if(board[TestPlayer.numrows-1][i] == 0) {
				possibleMoves.add(new MoveRecord(player, i, 1));
			}
		}
		if((player == 1 && !wepopped) ||(player == -1 && !theypopped)) {
			for(i=0;i<TestPlayer.numcolumns; i++) {
				if(board[0][i] == player) {
					possibleMoves.add(new MoveRecord(player, i, 0));
				}
			}
		}
		
		/*****************************************************************************************************************/
		/* ALPHA BETA PRUNING selection */
		
		//choose super bad value
		MoveRecord best = new MoveRecord(player, null, null);
		if(lastmoved == 1) best.setValue(-99999.9);
		else best.setValue(99999.9);
		
		for(MoveRecord r : possibleMoves) {
			EvaluateMove(r, board, player, currentLevel, wepopped, theypopped, best.getValue(), maxlevel);
			
			//max
			if(lastmoved == 1 && r.getValue() > best.getValue()) {
				best = r;
				if(best.getValue() > parentBest) {
					break; //alpha beta says stop, this path will never be chosen
				}
			}
			
			//min
			else if(lastmoved == -1 && r.getValue() < best.getValue()) {
				best = r;
				if(best.getValue() < parentBest) {
					break; //alpha beta says stop, this path will never be chosen
				}
			}
		}
		
		desiredMove.setValue(best.getValue());
	}

	
	/**
	 * Copy the board
	 * @param gboard the board being copied
	 * @param board the board that would be changed after this method returns
	 */
	private static void copyBoard(int[][] gboard, int[][] board) {
		int i,j;
		for(i=0;i<TestPlayer.numrows;i++) {
			for(j=0;j<TestPlayer.numcolumns;j++) {
				board[i][j] = gboard[i][j];
			}
		}
	}
	
	/**
	 * Make a drop move to the board
	 * @param player the player who makes this move
	 * @param column the column that this move is made to
	 * @param board the board that the move is made to
	 */
	static void makeDropMove(int player, int column, int[][] board) {
		int row = 0;
		
		while(true) {
			if(board[row][column] == 0) {
				board[row][column] = player;
				break;
			}
			row++;
		}
	}
	
	/**
	 * Make a pop move to the board
	 * @param column the column that the pop move is made to
	 * @param board the board that the move is made to
	 */
	static void makePopMove(int column, int[][] board) {
		int row;
		for(row=0;row<(TestPlayer.numrows - 1);row++) {
			board[row][column] = board[row + 1][column];
		}
		board[row][column] = 0;
	}
	
	/**
	 * Check to see if the game is over (win, lose or draw)
	 * @param board the board that should be checked for game over status
	 * @return true if the game is over (win, lose or draw)
	 */
	private static int gameOverCheck(int[][] board) {
		int result = 10000;
		boolean weWon = false;
		boolean weLost = false;
		
		int i,j;
		
		//vertical (column) Check
		for(i = 0; i < TestPlayer.numcolumns; i++){
			int count = 0;
			for(j = TestPlayer.numrows - 1; j >= 0; j--){ 
				//start checking from the top of the board, skip all empty spaces
				if(board[j][i] == 0) continue;
				count += board[j][i];
				//if a winning condition is found, break the loop as there shouldn't be multiple
				//winning conditions in a single column for both players
				if(count == TestPlayer.winlength) {
					weWon = true;
					break;
				}
				else if(count == (-1 * TestPlayer.winlength)) {
					weLost = true;
					break;
				}
			}
		}
		
		//Horizontal (row) Check
		for(i = 0; i < TestPlayer.numrows; i++){
			int count = board[i][0];
			int player = board[i][0];
			for(j = 1; j <= TestPlayer.numcolumns - 1; j++){
				//reset the count number and player if empty space is met
				if(board[i][j] == 0) {
					count = 0;
					player = 0;
					continue;
				}
				//increment count number if consecutive discs from the same player is found
				else if(board[i][j] == player) count = count + board[i][j]; 
				//reset count number and player if the opponent's disc is found
				else if(board[i][j] != player) {
					player = board[i][j];
					count = board[i][j];
				}
				
				//check for count number for each column and set winning/losing condition
				if(count == TestPlayer.winlength) weWon = true;
				else if(count == (-1 * TestPlayer.winlength)) weLost = true;
			}
		}
		
		//Diagonal (\) check
		//start from the top row from left to right
		for(i=0;i<=TestPlayer.numcolumns-1;i++){
			int x = i, y = TestPlayer.numrows - 1;
			int count = 0;
			//keep checking bottom right direction while inside the board
			while(x < TestPlayer.numcolumns && y >= 0){
				if(board[y][x] == 0) count = 0;	//reset count if empty
				else if(count == 0) count = board[y][x]; //set count if a disc is found
				//increment count if consecutive discs are found
				else if(board[y][x] == (count / Math.abs(count))) count += board[y][x]; 
				else count = board[y][x]; //update count if opponent's disc is found
				x++;
				y--;
				//check for winning/losing condition
				if(Math.abs(count) == TestPlayer.winlength) {
					if(count > 0) weWon = true;
					else if(count < 0) weLost = true;
				}
			}
			
		}
		//then start from the left column from up to down, skip the top-left space
		//same algorithm as above
		for(i = 0; i < TestPlayer.numrows - 1; i++){
			int y = i, x = 0;
			int count = 0;
			while(x < TestPlayer.numcolumns && y >= 0){
				if(board[y][x] == 0) count = 0;
				else if(count == 0) count = board[y][x];
				else if(board[y][x] == (count / Math.abs(count))) count += board[y][x];
				else count = board[y][x];
				x++;
				y--;
				
				if(Math.abs(count) == TestPlayer.winlength) {
					if(count > 0) weWon = true;
					else if(count < 0) weLost = true;
				}
			}
		}
		
		//diagonal (/) check
		//start from bottom column from left to right
		for(i=0;i<=TestPlayer.numcolumns-1;i++){
			int x = i, y = 0;
			int count = 0;
			//keep checking upper right direction while inside the board
			while(x < TestPlayer.numcolumns && y < TestPlayer.numrows){
				if(board[y][x] == 0) count = 0;	//reset count if empty
				else if(count == 0) count = board[y][x]; //set count if a disc is found
				//increment count if consecutive discs are found
				else if(board[y][x] == (count / Math.abs(count))) count += board[y][x];
				else count = board[y][x];//update count if opponent's dics is found
				x++;
				y++;
				//check for winning/losing condition
				if(Math.abs(count) == TestPlayer.winlength) {
					if(count > 0) weWon = true;
					else if(count < 0) weLost = true;
				}
			}
		}
		//then start from the left column from bottom to top, skip the bottom-left space
		//same algorithm as above
		for(i = 1; i < TestPlayer.numrows; i++){
			int y = i, x = 0;
			int count = 0;
			while(x < TestPlayer.numcolumns && y < TestPlayer.numrows){
				if(board[y][x] == 0) count = 0;
				else if(count == 0) count = board[y][x];
				else if(board[y][x] == (count / Math.abs(count))) count += board[y][x];
				else count = board[y][x];
				x++;
				y++;
				
				if(Math.abs(count) == TestPlayer.winlength) {
					if(count > 0) weWon = true;
					else if(count < 0) weLost = true;
				}
			}
		}
		
		//check for top row completely full, if woWon & weLost are still both false draw
		if(weWon == false && weLost == false) {
			boolean full = true;
			for(i=0;i<TestPlayer.numcolumns;i++) {
				if(board[TestPlayer.numrows-1][i] == 0) {
					full = false;
					break;
				}
			}
			if(full) {
				weWon = true;
				weLost = true;
			}	
		}
		
		//if weWon && weLost draw
		if(weWon && weLost) result = 0;
		else if(weWon) result = 1;
		else if(weLost) result = -1;
		
		return result;
	}
	
	
	/**
	 * Heuristic function for evaluating the current status of the board
	 * @param board the board to be checked for
	 * @return the evaluation value generated by the heuristic function
	 */
	private static Double heuristicEval(int[][] board) {
		Double result = 0.0;
		int threshold;
		if(turns > 7) {
			threshold = Math.min(4, TestPlayer.winlength - 1);
		}
		else {
			threshold = Math.min(3, TestPlayer.winlength - 1);
		}
		
		
		int i,j;
		
		//vertical (column) Check for chains
		for(i = 0; i < TestPlayer.numcolumns; i++){
			int count = 0;
			for(j = TestPlayer.numrows - 1; j >= 0; j--){
				if(board[j][i] == 0) continue;
				if(count != 0 && board[j][i] / count < 0){
					if(Math.abs(count) >= threshold) result += count;
					count = 0;
				}
				count += board[j][i];
			}
			if(Math.abs(count) >= threshold) result += count;
		}
		
		//Horizontal (row) Check
		for(i = 0; i < TestPlayer.numrows; i++){
			int count = board[i][0];
			int player = board[i][0];
			for(j = 1; j <= TestPlayer.numcolumns - 1; j++){
				if(board[i][j] == 0) {
					if(Math.abs(count) >= threshold) result += count;
					count = 0;
					player = 0;
					continue;
				}
				else if(board[i][j] == player) count = count + board[i][j];
				else if(board[i][j] != player) {
					if(Math.abs(count) >= threshold) result += count;
					player = board[i][j];
					count = board[i][j];
				}
			}
			if(Math.abs(count) >= threshold) result += count;
		}
		
		//Diagonal (\) check
		for(i=0;i<=TestPlayer.numcolumns-1;i++){
			int x = i, y = TestPlayer.numrows - 1;
			int count = 0;
			while(x < TestPlayer.numcolumns && y >= 0){
				if(board[y][x] == 0) {
					if(Math.abs(count) >= threshold) result += count;
					count = 0;
				}
				else if(count == 0) count = board[y][x];
				else if(board[y][x] == (count / Math.abs(count))) count += board[y][x];
				else {
					if(Math.abs(count) >= threshold) result += count;
					count = board[y][x];
				}
				x++;
				y--;
			}
			if(Math.abs(count) >= threshold) result += count;
			
		}
		for(i = 0; i < TestPlayer.numrows - 1; i++){
			int y = i, x = 0;
			int count = 0;
			while(x < TestPlayer.numcolumns && y >= 0){
				if(board[y][x] == 0) {
					if(Math.abs(count) >= threshold) result += count;
					count = 0;
				}
				else if(count == 0) count = board[y][x];
				else if(board[y][x] == (count / Math.abs(count))) count += board[y][x];
				else {
					if(Math.abs(count) >= threshold) result += count;
					count = board[y][x];
				}
				x++;
				y--;
			}
			if(Math.abs(count) >= threshold) result += count;
		}
		
		//diagonal (/) check
		for(i=0;i<=TestPlayer.numcolumns-1;i++){
			int x = i, y = 0;
			int count = 0;
			while(x < TestPlayer.numcolumns && y < TestPlayer.numrows){
				if(board[y][x] == 0) {
					if(Math.abs(count) >= threshold) result += count;
					count = 0;
				}
				else if(count == 0) count = board[y][x];
				else if(board[y][x] == (count / Math.abs(count))) count += board[y][x];
				else {
					if(Math.abs(count) >= threshold) result += count;
					count = board[y][x];
				}
				x++;
				y++;
			}
			if(Math.abs(count) >= threshold) result += count;
		}
		for(i = 1; i < TestPlayer.numrows; i++){
			int y = i, x = 0;
			int count = 0;
			while(x < TestPlayer.numcolumns && y < TestPlayer.numrows){
				if(board[y][x] == 0) {
					if(Math.abs(count) >= threshold) result += count;
					count = 0;
				}
				else if(count == 0) count = board[y][x];
				else if(board[y][x] == (count / Math.abs(count))) count += board[y][x];
				else {
					if(Math.abs(count) >= threshold) result += count;
					count = board[y][x];
				}
				x++;
				y++;
			}
			if(Math.abs(count) >= threshold) result += count;
		}
		
		return result;
	}
	
	/**
	 * A second method of heuristically evaluating the gameboard state,
	 *  Being surrounded by opponent pieces is a good thing
	 * @param board the board to be evaluated
	 * @return
	 */
	private static Double heuristicEvalTwo(int[][] board, int player, int valueofSame, int valueofEnemy) {
		Double result = 0.0;
		int i,j,k,n;
		
		//for each board location
		for(i=0;i<TestPlayer.numcolumns;i++) {
			for(j=0;j<TestPlayer.numrows; j++) {

				if(board[j][i] == player) {
					for(k=(i-1);k<=(i+1);k++) {
						for(n=(j-1);n<=(j+1);n++) {
							if(k >= 0 && k < TestPlayer.numcolumns && n >= 0 && n < TestPlayer.numrows) {
								if(board[n][k] == (player * -1)) {
									result = result + valueofEnemy;
								}
								else if(board[n][k] == player) {
									result = result + valueofSame;
								}
							}
						}
					}
				}
				
			}
		}
		
		return (result * player);
	}
}
