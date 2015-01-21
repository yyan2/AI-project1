package com.kpdavidson_yyan.gameplayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MoveAlgorithm {
	
	public static List<Integer> doBestMove() {
		
		List<Integer> bestmove = new ArrayList<Integer>();
		List<MoveRecord> possibleMoves = new ArrayList<MoveRecord>();
		
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
		for(MoveRecord r : possibleMoves) {
			if(TestPlayer.wePopped) {
				EvaluateMove(r, TestPlayer.gameboard, 1, 2, true, TestPlayer.theyPopped);
			}
			else {
				EvaluateMove(r, TestPlayer.gameboard, 1, 2, false, TestPlayer.theyPopped);
			}
		}
		
		// choose move with highest value
		MoveRecord best = new MoveRecord(null, null, null);
		best.setValue(-99999.9);
		for(MoveRecord r : possibleMoves) {
			if(r.getValue() > best.getValue()) best = r;
		}
		
		bestmove.set(0, best.getColumn());
		bestmove.set(1, best.getMovetype());
		
		return bestmove;
	}
	
	private static void EvaluateMove(MoveRecord desiredMove, int[][] gboard, int lastmoved, int level, boolean weAlreadyPopped, boolean theyAlreadyPopped) {
		
		int currentLevel = level + 1;
		
		boolean wepopped = weAlreadyPopped;
		boolean theypopped = theyAlreadyPopped;
		
		//Game Over Check
		int govercheck = gameOverCheck(gboard);
		if(govercheck == 0) {
			desiredMove.setValue(0.0);
			return;
		}
		else if(govercheck == 1) {
			desiredMove.setValue(1.0 / level);
			return;
		}
		else if(govercheck == -1) {
			desiredMove.setValue(-1.0 / level);
			return;
		}
		
		//copy gameboard
		int[][] board = new int[TestPlayer.numrows][TestPlayer.numcolumns];
		int i,j;
		for(i=0;i<TestPlayer.numrows;i++) {
			for(j=0;j<TestPlayer.numcolumns;j++) {
				board[i][j] = gboard[i][j];
			}
		}
		
		if(desiredMove.getMovetype() == 0) {
			makeDropMove(lastmoved, desiredMove.getColumn(), board);
		}
		else {
			makePopMove(desiredMove.getColumn(), board);
			if(desiredMove.getPlayer() == 1) wepopped = true;
			else theypopped = true;
		}
		
		int player = lastmoved * -1;
		
		List<MoveRecord> possibleMoves = new ArrayList<MoveRecord>();
		
		//get all possible moves
		for(i=0;i<TestPlayer.numcolumns; i++) {
			if(gboard[TestPlayer.numrows-1][i] == 0) {
				possibleMoves.add(new MoveRecord(player, i, 1));
			}
		}
		if((player == 1 && !wepopped) ||(player == -1 && !theypopped)) {
			for(i=0;i<TestPlayer.numcolumns; i++) {
				if(gboard[0][i] == player) {
					possibleMoves.add(new MoveRecord(player, i, 0));
				}
			}
		}
		for(MoveRecord r : possibleMoves) {
			EvaluateMove(r, TestPlayer.gameboard, player, currentLevel, wepopped, theypopped);
		}
		
		MoveRecord best = new MoveRecord(null, null, null);
		//max
		if(lastmoved == 1) {
			best.setValue(-99999.9);
			for(MoveRecord r : possibleMoves) {
				if(r.getValue() > best.getValue()) best = r;
			}
		}
		//min
		else {
			best.setValue(99999.9);
			for(MoveRecord r : possibleMoves) {
				if(r.getValue() < best.getValue()) best = r;
			}
		}
		desiredMove.setValue(best.getValue());
	}
	
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
	
	static void makePopMove(int column, int[][] board) {
		int row;
		for(row=0;row<(TestPlayer.numrows - 1);row++) {
			board[row][column] = board[row + 1][column];
		}
		board[row][column] = 0;
	}
	
	private static int gameOverCheck(int[][] board) {
		int result = 10000;
		boolean weWon = false;
		boolean weLost = false;
		
		int i,j;
		
		//vertical (column) Check
		for(i = 0; i < TestPlayer.numcolumns; i++){
			int count = 0;
			for(j = TestPlayer.numrows - 1; j >= 0; j--){
				if(board[j][i] == 0) continue;
				count += board[j][i];
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
				if(board[i][j] == 0) {
					count = 0;
					player = 0;
					continue;
				}
				else if(board[i][j] == player) count = count + board[i][j];
				else if(board[i][j] != player) {
					player = board[i][j];
					count = board[i][j];
				}
				
				if(count == TestPlayer.winlength) weWon = true;
				else if(count == (-1 * TestPlayer.winlength)) weLost = true;
			}
		}
		
		//Diagonal (\) check
		for(i=0;i<=TestPlayer.numcolumns-1;i++){
			int x = i, y = TestPlayer.numrows - 1;
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
		for(i=0;i<=TestPlayer.numcolumns-1;i++){
			int x = i, y = 0;
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
}
