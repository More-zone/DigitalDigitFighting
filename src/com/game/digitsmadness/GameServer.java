package com.game.digitsmadness;
import java.io.*;
import java.net.*;
import java.util.Objects;

public class GameServer {
	
	private ServerSocket ss;
	private int numPlayers;
	
	private ServerSideConnection player1, player2, player3, player4;
	
	private boolean digitsSourceR, digitsTargetR;
	private int digitsTargetNum;
	
	private static Game game;
	
	public GameServer() {
		System.out.println("Game Server");
		numPlayers = 0;
		
		try {
			ss = new ServerSocket(51734);
		} catch (IOException ex) {
			System.out.println("IOEXception from constructor");
		}
	}
	
	public void acceptConnections() {
		try {
			System.out.println("Waiting for connections...");
			while (numPlayers < 4) {
				Socket s = ss.accept();
				numPlayers++;
				System.out.println("--Player #"+numPlayers+ " has connected.--");
				ServerSideConnection ssc = new ServerSideConnection(s, numPlayers);
				if (numPlayers == 1) {
					player1 = ssc;
				}else if (numPlayers == 2) {
					player2 = ssc;
				}else if (numPlayers == 3) {
					player3 = ssc;
				}else if (numPlayers == 4) {
					player4 = ssc;
				}
				Thread t = new Thread(ssc);
				t.start();
			}
			System.out.println("4 players have connected, not accepting more.");
		}catch (IOException ex) {
			System.out.println("IOEXception from accept connections");
		}
	}
	
	private void switchTurn() {
		game.nextPlayer();
		player1.sendCurrentPlayer();
		player2.sendCurrentPlayer();
		player3.sendCurrentPlayer();
		player4.sendCurrentPlayer();
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
				//tells the clients whose turn it is
				//this will trigger the right client to wake up.
				dataOut.writeInt(playerID);
				dataOut.flush();
				
				while(true) {
					
					if(game.getCurrentPlayer() == playerID) {
						boolean dsr = dataIn.readBoolean();
						boolean dtr = dataIn.readBoolean();
						int dtn = dataIn.readInt();
						System.out.println("Player clicked a button");
						if(Objects.nonNull(digitsSourceR) && game.isMoveValid(digitsSourceR, digitsTargetR, digitsTargetNum)) {
							game.makeMove(digitsSourceR, digitsTargetR, digitsTargetNum);
							dataOut.writeBoolean(true);//move was made so we send true
							digitsSourceR = dsr;
							digitsTargetR = dtr;
							digitsTargetNum = dtn;
							switchTurn();
						}else if(Objects.nonNull(digitsSourceR)){
							dataOut.writeBoolean(false);
						}
					}
				}
				//needs a way to tell the game is done!
				//player1.closeConnection();
				//player2.closeConnection();
			}catch(IOException ex) {
				System.out.println("IOEXception from run() SSC");
			}
		}
		
		
		public void sendCurrentPlayer() {
			try {
				dataOut.writeInt(game.getCurrentPlayer());
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
		game = new Game(4);
		GameServer gs = new GameServer();
		gs.acceptConnections();
	}
	

}
