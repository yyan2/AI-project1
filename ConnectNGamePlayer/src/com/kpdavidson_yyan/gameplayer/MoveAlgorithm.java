package com.kpdavidson_yyan.gameplayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MoveAlgorithm {

	public static List<Integer> getBestMove() {
		
		List<Integer> bestmove = new ArrayList<Integer>();
		List<MoveRecord> possibleMoves = new ArrayList<MoveRecord>();
		
		//get all our possible moves
		int i;
		for(i=0;i<TestPlayer.numcolumns; i++) {
			if(TestPlayer.gameboard[TestPlayer.numrows-1][i] == 0) {
				possibleMoves.add(new MoveRecord(1, i, 1));
			}
		}
		for(i=0;i<TestPlayer.numcolumns; i++) {
			if(TestPlayer.gameboard[0][i] == 1) {
				possibleMoves.add(new MoveRecord(1, i, 0));
			}
		}
		
		for(MoveRecord r : possibleMoves) {
			EvaluateMove(r, TestPlayer.gameboard, 1, 2);
		}
		
		// choose move with highest value
		
		return bestmove;
	}
	
	private static void EvaluateMove(MoveRecord desiredMove, int[][] gboard, int lastmoved, int level) {
		
		int currentLevel = level + 1;
		
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
		}
		
		int player = lastmoved * -1;
		
		List<MoveRecord> possibleMoves = new ArrayList<MoveRecord>();
		
		//get all possible moves
		for(i=0;i<TestPlayer.numcolumns; i++) {
			if(gboard[TestPlayer.numrows-1][i] == 0) {
				possibleMoves.add(new MoveRecord(player, i, 1));
			}
		}
		for(i=0;i<TestPlayer.numcolumns; i++) {
			if(gboard[0][i] == player) {
				possibleMoves.add(new MoveRecord(player, i, 0));
			}
		}
		
		for(MoveRecord r : possibleMoves) {
			EvaluateMove(r, TestPlayer.gameboard, player, currentLevel);
		}
		
	}
	
	/*
	private static List<MoveRecord> generatePossibleMoves(Stack<MoveRecord> previousMoves) {
		List<MoveRecord> generatedMoves = new ArrayList<MoveRecord>();
		
		//copy gameboard
		int[][] board = new int[TestPlayer.numrows][TestPlayer.numcolumns];
		int i,j;
		for(i=0;i<TestPlayer.numrows;i++) {
			for(j=0;j<TestPlayer.numcolumns;j++) {
				board[i][j] = TestPlayer.gameboard[i][j];
			}
		}
		
		if(previousMoves != null) {
			
		}
		
		return generatedMoves;
	}
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
	
	static void makePopMove(int column, int[][] board) {
		int row;
		for(row=0;row<(TestPlayer.numrows - 1);row++) {
			board[row][column] = board[row + 1][column];
		}
		board[row][column] = 0;
	}
	/*
	static void undoMove(MoveRecord move, int[][] board) {
		int i;
		
		if(move.getMovetype() == 1) {
			for(i=TestPlayer.numrows-1; i>=0; i--) {
				if(board[i][move.getColumn()] != 0) {
					board[i][move.getColumn()] = 0;
					break;
				}
			}
		}
		else {
			for(i=TestPlayer.numrows-1; i>=1; i--) {
				board[i][move.getColumn()] = board[i - 1][move.getColumn()];
			}
			board[i][move.getColumn()] = move.getPlayer();
		}
	}
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
		
		//Diagonal (\) check
		
		//diagonal (/) check
		
		//check for top row completely full, if woWon & weLost are still both false draw
		
		//if weWon && weLost draw
		
		return result;
	}
}
