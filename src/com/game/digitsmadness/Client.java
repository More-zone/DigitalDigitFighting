package com.game.digitsmadness;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class Client extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6364116816267959093L;
	private static Game game;
	private int width, height;
	private Container contentPane;
	private JTextArea message;
	private JButton b1, b2, b3, b4, b5, b6, b7, b8;
	private int currentPlayer, playerID;
	private boolean buttonsEnabled;
	private int targetButton, targetPlayer;
	private boolean digitSideR;
	
	private ClientSideConnection csc;
	
	public Client(int w, int h) {
		width = w;
		height = h;
		contentPane = this.getContentPane();
		message = new JTextArea();
		b1 = new JButton("1");
		b2 = new JButton("2");
		b3 = new JButton("3");
		b4 = new JButton("4");
		b5 = new JButton("5");
		b6 = new JButton("6");
		b7 = new JButton("7");
		b8 = new JButton("8");
	}
	
	public void setupGUI() {
		playerID = 1;
		this.setSize(width, height);
		this.setTitle("Player number:"+playerID);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		contentPane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 8;
		c.gridx = 0;
		c.gridy = 0;
		contentPane.add(message, c);
		c.gridwidth = 1;
		c.ipadx = 1;
		c.gridy = 1;
		c.gridx = 0;
		contentPane.add(b1, c);
		c.gridwidth = 1;
		c.gridy = 1;
		c.gridx = 1;
		contentPane.add(b2, c);
		c.gridwidth = 1;
		c.gridy = 1;
		c.gridx = 2;
		contentPane.add(b3, c);
		c.gridwidth = 1;
		c.gridy = 1;
		c.gridx = 3;
		contentPane.add(b4, c);
		c.gridwidth = 1;
		c.gridy = 1;
		c.gridx = 4;
		contentPane.add(b5, c);
		c.gridwidth = 1;
		c.gridy = 1;
		c.gridx = 5;
		contentPane.add(b6, c);
		c.gridwidth = 1;
		c.gridy = 1;
		c.gridx = 6;
		contentPane.add(b7, c); 
		c.gridwidth = 1;
		c.gridy = 1;
		c.gridx = 7;
		contentPane.add(b8, c);
		message.setText("Creating a simple turn-based game in Java.");
		message.setWrapStyleWord(true);
		message.setLineWrap(true);
		message.setEditable(false);
		b1.setText(""+game.getDigits(1, false));
		b2.setText(""+game.getDigits(1, true));
		b3.setText(""+game.getDigits(2, false));
		b4.setText(""+game.getDigits(2, true));
		b5.setText(""+game.getDigits(3, false));
		b6.setText(""+game.getDigits(3, true));
		b7.setText(""+game.getDigits(4, false));
		b8.setText(""+game.getDigits(4, true));
		this.setVisible(true);

		Thread t = new Thread(new Runnable() {
			public void run() {
				updateTurn();
			}
		});
		t.start();
	}
	
	public void connectToServer() {
		csc = new ClientSideConnection();
	}
	
	public void setupFirstInput() {
		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JButton b = (JButton) ae.getSource();
				targetButton = Integer.parseInt(b.getText());
				setupLastInput();
			}
		};
		b1.removeActionListener(b1.getActionListeners());
		b1.addActionListener(al);
		b2.addActionListener(al);
		b3.addActionListener(al);
		b4.addActionListener(al);
		b5.addActionListener(al);
		b6.addActionListener(al);
		b7.addActionListener(al);
		b8.addActionListener(al);
		toggleButtonsFirstInput();
	}
	
	public void setupLastInput() {
		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JButton b = (JButton) ae.getSource();
				targetButton = Integer.parseInt(b.getText());
				toggleButtonsNoInput();
			}
		};

		b1.addActionListener(al);
		b2.addActionListener(al);
		b3.addActionListener(al);
		b4.addActionListener(al);
		b5.addActionListener(al);
		b6.addActionListener(al);
		b7.addActionListener(al);
		b8.addActionListener(al);
		toggleButtonsLastInput();
	}
	
	private void toggleButtonsLastInput() {
		if(playerID != 1) {
			b1.setEnabled(buttonsEnabled);
			b2.setEnabled(buttonsEnabled);
		}else {
			b1.setEnabled(false);
			b2.setEnabled(false);
		}
		if(playerID != 2) {
			b3.setEnabled(buttonsEnabled);
			b4.setEnabled(buttonsEnabled);
		}else {
			b3.setEnabled(false);
			b4.setEnabled(false);
		}
		if(playerID != 3) {
			b5.setEnabled(buttonsEnabled);
			b6.setEnabled(buttonsEnabled);
		}else {
			b5.setEnabled(false);
			b6.setEnabled(false);
		}
		if(playerID != 4) {
			b7.setEnabled(buttonsEnabled);
			b8.setEnabled(buttonsEnabled);
		}else {
			b7.setEnabled(false);
			b8.setEnabled(false);
		}
		
	}

	public void toggleButtonsFirstInput() {
		if(playerID != 1) {
			b1.setEnabled(buttonsEnabled);
			b2.setEnabled(buttonsEnabled);
		}else {
			b1.setEnabled(false);
			b2.setEnabled(false);
		}
		if(playerID != 2) {
			b3.setEnabled(buttonsEnabled);
			b4.setEnabled(buttonsEnabled);
		}else {
			b3.setEnabled(false);
			b4.setEnabled(false);
		}
		if(playerID != 3) {
			b5.setEnabled(buttonsEnabled);
			b6.setEnabled(buttonsEnabled);
		}else {
			b5.setEnabled(false);
			b6.setEnabled(false);
		}
		if(playerID != 4) {
			b7.setEnabled(buttonsEnabled);
			b8.setEnabled(buttonsEnabled);
		}else {
			b7.setEnabled(false);
			b8.setEnabled(false);
		}
		/**
		 * all of the buttons for easy copy
		b1.setEnabled(buttonsEnabled);
		b2.setEnabled(buttonsEnabled);
		b3.setEnabled(buttonsEnabled);
		b4.setEnabled(buttonsEnabled);
		b5.setEnabled(buttonsEnabled);
		b6.setEnabled(buttonsEnabled);
		b7.setEnabled(buttonsEnabled);
		b8.setEnabled(buttonsEnabled);
		*/
	}
	
	private void toggleButtonsNoInput() {
		b1.setEnabled(false);
		b2.setEnabled(false);
		b3.setEnabled(false);
		b4.setEnabled(false);
		b5.setEnabled(false);
		b6.setEnabled(false);
		b7.setEnabled(false);
		b8.setEnabled(false);
	}
	
	
	

	public void tryMakeMove(boolean source, boolean target, int n) {
		csc.sendMoveInfo(source, target, n);
		Thread t = new Thread(new Runnable(){
			public void run() {
				csc.isMoveValid();
			}
		});
		t.start();
	}
	
	
	public void updateTurn() {
		//currentPlayer = csc.receivePlayerNum();
		currentPlayer = 1;
		if(currentPlayer == playerID) {
			//The player's turn
			message.setText("Player #"+playerID+". Your turn.");
			buttonsEnabled = true;
			setupFirstInput();
		}else {
			message.setText("Player #"+playerID+". Not your turn.");
			toggleButtonsNoInput();
		}
		//a button toggler
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

			}catch (IOException ex) {System.out.println("IOEXception");}
		}
		
		public boolean isMoveValid() {
			boolean moveValid = false;
			try {
				moveValid = dataIn.readBoolean();
				if(moveValid) {System.out.println("Move valid");}
				else {System.out.println("Move invalid");}
			}catch(IOException ex) {System.out.println("IOEXception from isMoveValid() CSC");}
			return moveValid;
		}

		public void sendMoveInfo(boolean source, boolean target, int n) {
			try {
				dataOut.writeBoolean(source);
				dataOut.writeBoolean(target);
				dataOut.writeInt(n);
				dataOut.flush();
			}catch(IOException ex) {System.out.println("IOEXception from sendButtonNum() CSC");}
		}
		
		public int receivePlayerNum() {
			int n = -1;
			try {n = dataIn.readInt();
			}catch(IOException ex) {System.out.println("IOEXception from receiveButtonNum() CSC");}
			System.out.println("Player #"+n+"'s turn.");
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
		Client p = new Client(500, 100);
		//p.connectToServer();
		game = new Game(4);//Client side setting for 4 players is max
		p.setupGUI();
	}
}
