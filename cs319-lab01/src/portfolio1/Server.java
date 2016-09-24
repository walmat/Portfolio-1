package portfolio1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Donavan Brooks and Matt Wall
 * 
 * 	Server used to receive and broadcast messages to clients that are connected.
 * 	You can send text messages and images.
 * 		- The text messages will be written to the chat area in your gui
 * 		- Images will be written to a file in your working directory and then will be sent to all the clients
 */
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;

public class Server implements Runnable
{
	
	private ServerSocket serverSocket = null;
	private Thread mainThread = null;
	private ServerGUI frame;
	private static Question q;
	private ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	private ArrayList<Answer> clientAnswers = new ArrayList<Answer>();
	static int clientNum = 0;
	private boolean roundStarted = false;
	private String sentQuestionAns = "";
	private Timer timer;

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
					clients.get(clientNum - 1).sendMsg("Connected to server");
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
			if(clients.get(i).getID() == ID) {
				return i;
			}
		}

		return -1;
	}
	
	public synchronized void handle(String[] input)
	{
		// TODO new message, send to clients and then write it to history
		if(input[0].equals(clients.get(0).getID() + "") && input[1].equals("....Start....") && roundStarted == false) {
			
			Question sentQuestion = Question.questions.get(new Random().nextInt(Question.questions.size()));
			//clientAnswers.add(new Answer("rightAnswer", sentQuestion.rightAnswer));
			sentQuestionAns = sentQuestion.rightAnswer;
			
			for(int i = 0; i < clientNum; i++) {
				
				clients.get(i).sendMsg(sentQuestion);
				roundStarted = true;
			}
			
			ActionListener actionListener = new ActionListener() {
			    int timeRemaining = 10;
			   
			    public void actionPerformed(ActionEvent evt){
			    	timeRemaining--;
			    	for(int i = 0; i < clientNum; i++) {
			    		clients.get(i).sendMsg(timeRemaining);
			    	}
			        
			        if(timeRemaining <= 0){
			        	timer.stop();
			        	roundStarted = false;
			        	
			        	
			       }
			   }
			};
			
			timer = new Timer(1000, actionListener);
			timer.start();
		}
		
		// TODO 
		if(roundStarted == true){
			String port = input[0];
			String currClientAnswer = input[1];
			roundStarted = true;
			for(int i = 0; i < clientAnswers.size(); i++) {
				
				if(clientAnswers.get(i).port.equals(port)) {
					System.out.println("Changing already stated Answer Array");
					clientAnswers.get(i).answer = currClientAnswer;
				}
				else{
					System.out.println("Filling Answer Array");
					clientAnswers.add(new Answer(port, currClientAnswer));
				}
	
			}
			
			for(int i = 0; i < clientAnswers.size(); i++) {
        		System.out.println("Answer: " + clientAnswers.get(i).toString());
        	}
		}
		
		else if(roundStarted == false){
			for(int i = 0; i < clientNum; i++) {
				clients.get(i).sendMsg(input[0]);
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
		ClientThread clientToRemove = clients.get(index);
		
		try {
			clientToRemove.close();
		}
		catch (IOException e) {
			System.out.println("Error while trying to close thread: " + e.getMessage());
			clientToRemove.stop();
		}
		
		clients.remove(index);
		clientNum--;
		System.out.println("Client Count: " + clientNum) ;
}

	private void addThread(Socket socket)
	{
		//TODO add new client
		if (clientNum < 5){  
			System.out.println("Client accepted: " + socket);
	         clients.add(new ClientThread(this, socket));
	         
	         try{
	        	 clients.get(clientNum).open();
	        	 clients.get(clientNum).start();
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
		//q.main(args);
		Server server = null;
		server = new Server(1222);
		
		for (int i= 0; i < q.questions.size(); i++) {
			System.out.println(q.questions.get(i));
		}
	}
	
}
