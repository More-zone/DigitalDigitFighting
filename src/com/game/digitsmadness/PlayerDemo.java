package com.game.digitsmadness;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class PlayerDemo extends JFrame {
	
	private int width, height;
	private Container contentPane;
	private JTextArea message;
	private JButton b1, b2, b3, b4;
	private int playerID, otherPlayer;
	private int[] values;
	private int maxTurns, turnsMade;
	private int myPoints, enemyPoints;
	private boolean buttonsEnabled;
	
	private ClientSideConnection csc;
	
	public PlayerDemo(int w, int h) {
		width = w;
		height = h;
		contentPane = this.getContentPane();
		message = new JTextArea();
		b1 = new JButton("1");
		b2 = new JButton("2");
		b3 = new JButton("3");
		b4 = new JButton("4");
		values = new int[4];
		turnsMade = 0;
		myPoints = 0;
		enemyPoints = 0;
	}
	
	public void setupGUI() {
		this.setSize(width, height);
		this.setTitle("Player number:"+playerID);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		contentPane.setLayout(new GridLayout(1, 5));
		contentPane.add(message);
		message.setText("Creating a simple turn-based game in Java.");
		message.setWrapStyleWord(true);
		message.setLineWrap(true);
		message.setEditable(false);
		contentPane.add(b1);
		contentPane.add(b2);
		contentPane.add(b3);
		contentPane.add(b4);
		this.setVisible(true);
		
		if(playerID == 1) {
			message.setText("You are player #1. You go fisrt.");
			otherPlayer = 2;
			buttonsEnabled = true;
		}else {
			message.setText("You are player #2. Wait for your turn.");
			otherPlayer = 1;
			buttonsEnabled = false;
			Thread t = new Thread(new Runnable() {
				public void run() {
					updateTurn();
				}
			});
			t.start();
		}
		toggleButtons();
	}
	
	public void connectToServer() {
		csc = new ClientSideConnection();
	}
	
	public void setupButtons() {
		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JButton b = (JButton) ae.getSource();
				int bNum = Integer.parseInt(b.getText());
				
				message.setText("You clicked button #"+bNum+". Now wait for player #"+otherPlayer);
				turnsMade++;
				System.out.println("Turns made"+turnsMade);
				myPoints += values[bNum-1];
				System.out.println("My points: "+myPoints);
				buttonsEnabled=false;
				toggleButtons();
				csc.sendButtonNum(bNum);

				if(playerID == 2 && turnsMade == maxTurns) {
					checkWinner();
				}else {
					Thread t = new Thread(new Runnable(){
						public void run() {
							updateTurn();
						}
					});
					t.start();
				}
				
			}
		};

		b1.addActionListener(al);
		b2.addActionListener(al);
		b3.addActionListener(al);
		b4.addActionListener(al);
	}
	
	public void toggleButtons() {
		b1.setEnabled(buttonsEnabled);
		b2.setEnabled(buttonsEnabled);
		b3.setEnabled(buttonsEnabled);
		b4.setEnabled(buttonsEnabled);
	}
	
	
	public void updateTurn() {
		int n = csc.receiveButtonNum();
		message.setText("Your enemy clicked button #" +n+ ". Your turn.");
		enemyPoints += values[n-1];
		System.out.println("Your enemy has"+enemyPoints+" points.");
		if(playerID == 1 && turnsMade == maxTurns) {
			checkWinner();
		}else {

			buttonsEnabled = true;
		}
		toggleButtons();
		
	}
	
	private void checkWinner() {
		buttonsEnabled = false;
		if (myPoints > enemyPoints) {
			message.setText("You WON!\n"+"You:"+myPoints+"\n"+"ENEMY:"+enemyPoints);
		}else if (myPoints < enemyPoints) {
			message.setText("You LOST!\n"+"You:"+myPoints+"\n"+"ENEMY:"+enemyPoints);
		}else {
			message.setText("It's a tie!?\n"+"Your points:"+myPoints);
		}
		
		csc.closeConnection();
	}
	
	
	//Client Connection
	private class ClientSideConnection{
		
		private Socket socket;
		private DataInputStream dataIn;
		private DataOutputStream dataOut;
		
		public ClientSideConnection() {
			System.out.println("--Client--");
			try {
				socket = new Socket("localhost", 51734);
				dataIn = new DataInputStream(socket.getInputStream());
				dataOut = new DataOutputStream(socket.getOutputStream());
				playerID = dataIn.readInt();
				maxTurns = dataIn.readInt() / 2;
				values[0] = dataIn.readInt();
				values[1] = dataIn.readInt();
				values[2] = dataIn.readInt();
				values[3] = dataIn.readInt();

			}catch (IOException ex) {
				System.out.println("IOEXception");
			}
		}
		
		public void sendButtonNum(int n) {
			try {
				dataOut.writeInt(n);
				dataOut.flush();
			}catch(IOException ex) {System.out.println("IOEXception from sendButtonNum() CSC");}
		}
		
		public int receiveButtonNum() {
			int n = -1;
			try {
				n = dataIn.readInt();
				System.out.println("Player #"+otherPlayer+" clicked button #"+n);
			}catch(IOException ex) {System.out.println("IOEXception from receiveButtonNum() CSC");}
			return n;
		}
		
		public void closeConnection() {
			try {
				socket.close();
				System.out.println("--Connection closed--");
			}catch(IOException ex) {System.out.println("IOException on closeConnection() CSC");}
		}
		
	}
	
	
	public static void main(String[] args) {
		PlayerDemo p = new PlayerDemo(500, 100);
		p.connectToServer();
		p.setupGUI();
		p.setupButtons();
		
	}
}
