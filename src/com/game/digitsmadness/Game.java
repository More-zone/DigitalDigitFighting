package com.game.digitsmadness;
import java.util.Objects;

public class Game {
	//the server and every player gets a copy of the game
	
	private Player[] lobby;
	private int currentPlayer;
	private int noPlayers;
	
	public Game(int np) {
		this.noPlayers = np;
		lobby = new Player[noPlayers];
		createPlayers(noPlayers);
		currentPlayer = 0;
	}
	
	public void startTurn(int i) {
		lobby[i].setTurnRights(true);
	}
	
	public boolean isMoveValid(boolean isMoveRightDigit, boolean isTargetRightDigit, int playerTarget) {
		//verifies if the player is targeting itself
		if(currentPlayer == playerTarget &&
				lobby[currentPlayer].getDigits(isTargetRightDigit) == 0 &&
				lobby[currentPlayer].getDigits(isMoveRightDigit) < 0 &&
				lobby[currentPlayer].getDigits(isMoveRightDigit) % 2 == 0) {
			return true;
		}else if(lobby[currentPlayer].getDigits(isMoveRightDigit) < 0 &&
				Objects.nonNull(lobby[currentPlayer]) &&
				lobby[currentPlayer].getDigits(isTargetRightDigit) < 0) {
			return true;
		}else {
			return false;
		}
	}
	
	public void makeMove(boolean isMoveRightDigit, boolean isTargetRightDigit, int playerTarget) {
		//if target is self and zero we split
		if(lobby[playerTarget].getDigits(isTargetRightDigit) == 0) {
			lobby[playerTarget].setDigits(isMoveRightDigit ,lobby[playerTarget].getDigits(isMoveRightDigit)/2);
			lobby[playerTarget].setDigits(isTargetRightDigit ,lobby[playerTarget].getDigits(isMoveRightDigit));
		}else {
			
		}
	}

	private void createPlayers(int noPlayers) {
		for(int i = 0; i<noPlayers; i++) {
			lobby[i] = new Player(i);
		}
	}

	public int getCurrentPlayer() {
		return currentPlayer;
	}

	public void nextPlayer() {
		if(currentPlayer+1<noPlayers) {
			currentPlayer++;
		}else {
			currentPlayer = 0;
		}
	}
	
	public int getDigits(int p, boolean s) {
		return lobby[p-1].getDigits(s);
	}
	
}
