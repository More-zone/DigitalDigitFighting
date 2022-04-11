package com.game.digitsmadness;

public class Player {

	private int playerNum;
	private int rightDigits;
	private int leftDigits;
	private boolean turnRights;
	
	
	public Player(int playerNum) {
		this.playerNum = playerNum;
		setDigits(true, 1);
		setDigits(false, 1);
		setTurnRights(false);
	}


	public int getPlayerNum() {
		return playerNum;
	}


	public int getDigits(boolean right) {
		if(right) {
			return rightDigits;
		}else {
			return leftDigits;
		}
	}


	public void addDigits(boolean right, int numDigits) {
		if(right) {
			if(rightDigits + numDigits <=5) {
				rightDigits+=numDigits;
			}else {
				rightDigits = 0;
			}
			
		}else {
			if(leftDigits + numDigits <= 5) {
				leftDigits+=numDigits;
			}else {
				leftDigits = 0;
			}
		}
	}
	
	public void setDigits(boolean right, int d) {
		if(right) {
			rightDigits = d;
		}else {
			leftDigits = d;
		}
	}


	public boolean isTurnRights() {
		return turnRights;
	}


	public void setTurnRights(boolean turnRights) {
		this.turnRights = turnRights;
	}
}
