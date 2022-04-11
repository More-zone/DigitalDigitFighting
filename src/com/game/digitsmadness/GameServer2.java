package com.game.digitsmadness;
import java.io.*;
import java.net.*;

public class GameServer2 {
	
	private ServerSocket ss;
	private int numPlayers;
	
	private ServerSideConnection player1;
	private ServerSideConnection player2;
	
	private int turnsMade, maxTurns;
	private int[] values;
	
	private int player1ButtonNum, player2ButtonNum;
	
	public GameServer2() {
		System.out.println("Game Server");
		numPlayers = 0;
		turnsMade = 0;
		maxTurns = 4;
		values = new int[4];
		
		for(int i = 0; i<values.length; i++) {
			values[i] = (int) Math.ceil(Math.random() * 100);
			System.out.println("Value #"+ (i+1) + " is " + values[i]);
		}
		
		
		try {
			ss = new ServerSocket(51734);
		} catch (IOException ex) {
			System.out.println("IOEXception");
		}
	}
	
	public void acceptConnections() {
		try {
			System.out.println("Waiting for connections...");
			while (numPlayers < 2) {
				Socket s = ss.accept();
				numPlayers++;
				System.out.println("--Player #"+numPlayers+ " has connected.--");
				ServerSideConnection ssc = new ServerSideConnection(s, numPlayers);
				if (numPlayers == 1) {
					player1 = ssc;
				}else if (numPlayers == 2) {
					player2 = ssc;
				}
				Thread t = new Thread(ssc);
				t.start();
			}
			System.out.println("-2 players connected, not accepting more.");
		}catch (IOException ex) {
			System.out.println("IOEXception from accept connections");
		}
	}
	
	private class ServerSideConnection implements Runnable {
		private Socket socket;
		private DataInputStream dataIn;
		private DataOutputStream dataOut;
		private int playerID;
		
		public ServerSideConnection(Socket s, int id) {
			socket = s;
			playerID = id;
			try {
				dataIn = new DataInputStream(socket.getInputStream());
				dataOut = new DataOutputStream(socket.getOutputStream());
				
			}catch(IOException ex) {
				System.out.println("IOEXception from SSC constructor");
			}
		}
		
		public void run() {
			try {
				dataOut.writeInt(playerID);
				dataOut.writeInt(maxTurns);
				dataOut.writeInt(values[0]);
				dataOut.writeInt(values[1]);
				dataOut.writeInt(values[2]);
				dataOut.writeInt(values[3]);
				dataOut.flush();
				
				while(true) {
					if(playerID == 1) {
						player1ButtonNum = dataIn.readInt();
						System.out.println("Player 1 clicked button #" +player1ButtonNum);
						player2.sendButtonNum(player1ButtonNum);
					}else {
						player2ButtonNum = dataIn.readInt();
						System.out.println("Player 2 clicked button #" +player2ButtonNum);
						player1.sendButtonNum(player2ButtonNum);
					}
					turnsMade++;
					if(turnsMade == maxTurns) {
						System.out.println("max turns has been reached");
						break;
					}
				}
				player1.closeConnection();
				player2.closeConnection();
			}catch(IOException ex) {
				System.out.println("IOEXception from run() SSC");
			}
		}
		
		
		public void sendButtonNum(int n) {
			try {
				dataOut.writeInt(n);
				dataOut.flush();
			}catch (IOException ex) {System.out.println("IOEXception from sendButtonNum() SSC");}
		}
		
		public void closeConnection() {
			try {
				socket.close();
				System.out.println("Connection closed");
			}catch(IOException ex) {System.out.println("IOException on closeConnect() SSC");}
		}
		
	}
	
	public static void main(String[] args) {
		GameServer2 gs = new GameServer2();
		gs.acceptConnections();
	}
	

}
