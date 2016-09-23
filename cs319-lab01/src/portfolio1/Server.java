package portfolio1;

/**
 * @author Donavan Brooks and Matt Wall
 * 
 * 	Server used to receive and broadcast messages to clients that are connected.
 * 	You can send text messages and images.
 * 		- The text messages will be written to the chat area in your gui
 * 		- Images will be written to a file in your working directory and then will be sent to all the clients
 */

import java.net.*;
import java.nio.file.Files;
import java.util.Random;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;

public class Server implements Runnable
{
	
	private ServerSocket serverSocket = null;
	private Thread mainThread = null;
	private File file = new File("chat.txt");
	private PrintWriter writer;
	private ServerGUI frame;
	private Thread guiMessageThread;
	private static Question q;
	private ClientThread clients[] = new ClientThread[5];
	private int clientNum = 0;
	private boolean roundStarted = false;

	public Server(int port)
	{
		//TODO Binding and starting server
		try
		{
			System.out.println("Binding to port " + port + ", please wait  ...");
			serverSocket = new ServerSocket(port);
			System.out.println("Server started: " + serverSocket);
			start();
		} catch (IOException ioe)
		{
			System.out.println("Can not bind to port " + port + ": " + ioe.getMessage());
		}
		// Wants it connects to the server fetches the questions for the quiz
		Question q = new Question();
		q.fillQuestionList();
	}

	public void run()
	{
		//TODO wait for a client or show error
		while(true){
			try {
				System.out.println("Waiting for a client....");
				if(roundStarted == true) {
					JOptionPane.showMessageDialog(new JFrame(), "Sorry we are in the middle of a round, Please wait for it to end, Thank you :)");
				}	
				else {
					addThread(serverSocket.accept());
					System.out.println("Client Count: " + clientNum);
				}
			}
			catch(IOException e){
				System.out.println("Server Acception Failure: " + e.getMessage());
				try {
					stop();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	public void start()
	{
		frame = new ServerGUI();
		frame.setVisible(true);
		//TODO launch a thread to read for new messages by the server
		if(mainThread == null){
			mainThread = new Thread(this);
			mainThread.start();
		}
	}

	
	public void stop() throws IOException
	{
		//TODO
		if(mainThread != null) {
			mainThread.stop();
			mainThread = null;
		}
	}

	private int findClient(int ID)
	{
		//TODO Find Client
		for(int i = 0; i < clientNum; i++)
		{
			if(clients[i].getID() == ID) {
				return i;
			}
		}

		return -1;
	}

	public void nextRound() {
		
	}
	
	public synchronized void handle(String[] input)
	{
		// TODO new message, send to clients and then write it to history
		if(input[0].equals(clients[0].getID() + "") && input[1].equals("....Start....")) {
			
			for(int i = 0; i < clientNum; i++) {
				String qMsg = Question.questions.get(1).getQuestion();
				Question qSend = Question.questions.get(i);
				int k =1;
				clients[i].sendQuestion(qSend);
			}
		}
		else {
			for(int i = 0; i < clientNum; i++) {
				clients[i].sendMsg(input[0]);
			}
		}
		
		frame.recieveMessage(input[1]);
	}
	
	public synchronized void remove(int ID)
	{
		//TODO get the ClientThread, remove it from the array and then terminate it
	
		int index = findClient(ID);
		System.out.println("Client Count: " + clientNum) ;
		System.out.println("Trying to remove Client " + index);
		
		if(index >= 0) {			
			ClientThread clientToRemove = clients[index];
			if(index < clientNum - 1){
				for (int i = index + 1; i < clientNum ; i++) {
					clients[i - 1] = clients[i];
				}
			}
			
			clientNum--;
			try {
				clientToRemove.close();
			}
			catch (IOException e) {
				System.out.println("Error while trying to close thread: " + e.getMessage());
				clientToRemove.stop();
			}
		}
		System.out.println("Client Count: " + clientNum) ;
}

	private void addThread(Socket socket)
	{
		//TODO add new client
		if (clientNum < clients.length){  
			System.out.println("Client accepted: " + socket);
	         clients[clientNum] = new ClientThread(this, socket);
	         
	         try{
	        	 clients[clientNum].open();
	        	 clients[clientNum].start();
	        	 clientNum++; 
	         }
	         catch(IOException e){
	        	 System.out.println("Error while trying to open thread: " + e.getMessage());
	         }
		}         
	      else
	      	System.out.println("Maximum Number of clients has been reached");
	}

	public static void main(String args[])
	{
		q = new Question();
		q.main(args);
		Server server = null;
		server = new Server(1222);
		
		for (int i= 0; i < q.questions.size(); i++) {
			System.out.println(q.questions.get(i));
		}
	}
	
}
