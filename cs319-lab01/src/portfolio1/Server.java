package portfolio1;

/**
 * @author Donavan Brooks and Matt Wall
 * 
 
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class Server implements Runnable 
{
	private ServerSocket serverSocket = null;
	private Thread mainThread = null;
	private ServerGUI frame;
	private static Question q;
	private ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	private ArrayList<Answer> clientAnswers = new ArrayList<Answer>();
	private ArrayList<Score> clientScores = new ArrayList<Score>();
	static int clientNum = 0;
	static boolean roundStarted = false;
	private static String sentQuestionAns = "";
	private Question sentQuestion; 
	private Timer timer;
	private ObjectOutputStream errorStream;
	private boolean init = false;
	private int roundNum = 1;
	
	//Holds what end you want to end on
	private int endRound = 2;
	
	public Server(int port)
	{
		//Binding and starting server
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
		//hang and wait for a client or show error
		while(true){
			try {
				System.out.println("Waiting for a client....");
				addThread(serverSocket.accept());
				System.out.println("Client Count: " + clientNum);
			}
			catch(IOException e){
				System.out.println("Server Acception Failure: " + e.getMessage());
				try {
					stop();
				} catch (IOException e1) {
					System.out.println("Couldn't stop thread: " + e1.getMessage());
				}
			}
		}
	}

	public void start()
	{
		frame = new ServerGUI();
		frame.setVisible(true);
		
		//launch a thread to read for new messages by the server
		if(mainThread == null){
			mainThread = new Thread(this);
			mainThread.start();
		}
	}

	
	public void stop() throws IOException
	{
		if(mainThread != null) {
			mainThread.stop();
			mainThread = null;
		}
	}

	private int findClient(int ID)
	{
		//Find Client
		for(int i = 0; i < clients.size(); i++)
		{
			if(clients.get(i).getID() == ID) {
				return i;
			}
		}

		return -1;
	}
	
	// Messages are sent to the sever in a String array with the format: [port][message][username]
	public synchronized void handle(String[] input)
	{
		// Once a client connects, it will set the clientThread.username and add the new client to the scores ArrrayList
		if(init == true) {
			
			// Set the username variable for the new clientThread that is added
			clients.get(clientNum -1).username = input[2];
			
			int ID = clients.get(clientNum -1 ).getID();
			String user = clients.get(clientNum -1).getUsername();
			int score = clients.get(clientNum - 1).getScore();

			clientScores.add(new Score(ID,  user, score));
			
			updateScore(clients.get(clientNum - 1));
			init = false;
		}
		
		// If the host client starts the round, then enter this if statement
		if(input[0].equals(clients.get(0).getID() + "") && input[1].equals("....Start....") && roundStarted == false && roundNum <= endRound) {
			
			roundStarted = true;
			// Saves the question that was sent to the clients, if the questions ArrayList is empty then it throws an error
			try {
				sentQuestion = Question.questions.get(new Random().nextInt(Question.questions.size()));
	
				// Holds the answer of the sent question
				sentQuestionAns = sentQuestion.rightAnswer;
			}catch(IllegalArgumentException e) {
				JOptionPane.showMessageDialog(new JFrame(), "Sorry, there seems to be no questions in our list."
															+ " Check your internet connection and try again.");
				System.exit(-1);
			}
			
			// Sends the randomly selected question to all the clients
			for(int i = 0; i < clientNum; i++) {
				clients.get(i).sendMsg(sentQuestion);
			}
			
			ActionListener actionListener = new ActionListener() 
			{
			    int timeRemaining = 10;
			   
			    public void actionPerformed(ActionEvent evt){
			    	timeRemaining--;
			    	for(int i = 0; i < clientNum; i++) {
			    		clients.get(i).sendMsg(timeRemaining);
			    	}
			        
			    	//  If time is equal to zero, then it will stop the timer, and send the ArrayListof answers submitted by the clients, out to all the clients for the Answer Round
			        if(timeRemaining <= 0){
			        
			        	timer.stop();
			 
			        	for(int i = 0; i < clientNum; i++) {
			     
			        		int port = clients.get(i).getID();
			        		
							try {
								// Randomizes the order of the answers
								ArrayList<Answer> a = randomizeClientAnswers(port);

								//It sends the arrayList of answers to all the clients
								if(a.size() == clientNum) {
									clients.get(i).sendMsg(a);
									clients.get(i).sentAnswers = a;
								}
								else {
									clients.get(i).sendMsg("It seems that not everyone submitted a fake answer/answered the question");
									clientAnswers.clear();
								}
								
							} catch (CloneNotSupportedException e) {
								System.out.println("Error trying to randomize answers: " + e.getMessage());
							}	
			        	}     	
			        	
			        	// Creates a new timer to display the QuestionUI for a certain amount of time. Only if everyone submitted their fake answer
			        	// Else it ends the round without incrementing it and everything resets
			        	if(clientAnswers.size() == clientNum) {
			        	
			        	ActionListener alistener = new ActionListener() {
						    int timeRemaining = 10;
						    
						    public void actionPerformed(ActionEvent evt){
						    	timeRemaining--;
						    	for(int i = 0; i < clientNum; i++) {
						    		clients.get(i).sendMsg(timeRemaining);
						    	}
						        
						        if(timeRemaining <= 0){
						        	
						        	timer.stop();
						        	roundStarted = false;
						        	
						        	// Calculate the score for every client
						        	for(int i = 0; i < clientNum; i++) {
						        		calculateScore(clients.get(i));
						        	}
						        	
						        	// Sends the updated Score to every client
						        	for(int i = 0; i <clientNum; i++ ) { 
						        		updateScore(clients.get(i));
						        	}
						        	
						        	roundNum++;
						        	
						        	// If you reach the round limit end the game
						        	if(roundNum > endRound && roundStarted == false) {
						        		System.out.println("EEND Game");
						        		endGame();
						    		}
						        	
						        }
						    }
						};
						
							// Start answer round timer
							timer = new Timer(1000, alistener);
							timer.start();	
							clientAnswers.clear();	
			        	}	
			        	else {
			        		roundStarted = false;
			        	}
			       }
			   }
			};
			// Start create timer for the fake answer round 
			timer = new Timer(1000, actionListener);
			timer.start();
			clientAnswers.clear();	
			
			//Increment roundNum
			
			String[] roundMsg = {"Round", String.valueOf(roundNum), String.valueOf(endRound)};
			for(int i = 0; i < clientNum; i++) {
			
	    		clients.get(i).sendMsg(roundMsg);
	    	}
		}
		
		
		
		// If the round is started, this will listen for the clients answers and update the Answer ArrayList so that it only store the 
		// last answer sent in by each individual client.  It knows who sent what answer by pairing it with the port number
		else if(roundStarted == true){
			boolean found = false;
			String port = input[0];
			String currClientAnswer = input[1];
			
			if (clientAnswers.size() == 0) {
				clientAnswers.add(new Answer(currClientAnswer, port));
			}
			else {
	 			for(int i = 0; i < clientAnswers.size(); i++) {
					
					if(clientAnswers.get(i).port.trim().equals(port.trim())) {
						clientAnswers.get(i).answer = currClientAnswer;
						found = true;
					}
				}
	 			
	 			if (found == false) {
	 				clientAnswers.add(new Answer(currClientAnswer, port));
	 			}
			}
		}
		
		// If the round has not started then the server will handle messages just like a regular chat 
		else if(roundStarted == false){
			for(int i = 0; i < clientNum; i++) {
				clients.get(i).sendMsg(input[2] + ": " + input[1]);
			}
		}
		
		// Prints to ServerGUI
		frame.recieveMessage(input[1]);
	}
	
	public synchronized void remove(int ID)
	{
		//Get the ClientThread, remove it from the array and then terminate it
	
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
		String removedUser = clients.get(index).username;
		
		// Removes user from clientThread arraylist, from the score arraylist, updates the score arraylist and alerts the other clients who left the game
		clients.remove(index);
		clientScores.remove(index);
		for(int i = 0; i < clients.size(); i++){
			clients.get(i).sendMsg(removedUser + " has left the game.");
			clients.get(i).sendMsg(cloneScoreList(clientScores));
		}
		clientNum--;
		System.out.println("Client Count: " + clientNum) ;
	}

	// Adds threads to ClientThread ArrayList
	private void addThread(Socket socket)
	{
		//add new client only if the round has not started and the server has less than 4 clients connected
		if (clientNum < 4 && roundStarted == false){  
			System.out.println("Client accepted: " + socket);
			init = true;
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
		// If the thread is not added and the server has 4 clients connected, send an error message
		else if (clientNum >= 4){
			try {
				errorStream = new ObjectOutputStream(socket.getOutputStream());
				errorStream.writeObject("Sorry, the Server is full:( Try Again later.");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// If the thread is not added and the server is in the middle of a round, send an error message
		else if (roundStarted == true){
			try {
				errorStream = new ObjectOutputStream(socket.getOutputStream());
				errorStream.writeObject("Sorry, the game is in the middle of a round. Please try again later" );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
	}
	 
	// Randomizes the clientAnswer arraylist
	private ArrayList<Answer> randomizeClientAnswers(int port) throws CloneNotSupportedException{		
		
		//solve copying by performing a deep copy.
		ArrayList<Answer> a = cloneList(clientAnswers);

		for (int i = 0; i < clientAnswers.size(); i++){
			if (a.get(i).port.equals(String.valueOf(port))) {
				clients.get(i).createdFakeAnswer = a.get(i).answer;
				a.get(i).answer = sentQuestionAns;
			}
		}
		Collections.shuffle(a);
		return a;
	}
	
	// Helper method to help clone the Answer arraylist
	private static ArrayList<Answer> cloneList(ArrayList<Answer> answers) {
	    ArrayList<Answer> clonedList = new ArrayList<Answer>(answers.size());
	    for (Answer a : answers) {
	    	//pass the old answer to the copy constructor
	        clonedList.add(new Answer(a));
	    }
	    return clonedList;
	}
	
	// Helper method to calculate score.  It determines if the client got the answer right and if anybody entered the fake answer he created
	// If the client gets the answer right they get 2 points, and they get an additional 1 point forr every client that guessed their fake answer
	private void calculateScore(ClientThread c) { 
		
		String clientPort = String.valueOf(c.getID());
		
		for(int j = 0; j < clientAnswers.size(); j++) {
			
			// If you chose the right answer increment your score by
			if(clientAnswers.get(j) != null && clientPort.equals(clientAnswers.get(j).port)) {
				if(clientAnswers.get(j).answer.equals(sentQuestionAns)){
					c.sendMsg("You submitted the right answer");
					c.score += 2;
				}
			}
			
			// If anybody else chose your answer increment your score by 1
			if(c.createdFakeAnswer != null && c.createdFakeAnswer.equals(clientAnswers.get(j).answer)) {
				
				// This holds the username of the person that clicked you answer
				String user = clients.get(findClient(Integer.parseInt(clientAnswers.get(j).port))).username;
				c.sendMsg(user + " clicked your fake answer");
				c.score += 1;
			}
		}
	}
	
	// Helper method to help clone the Score Arraylist
	private static ArrayList<Score> cloneScoreList(ArrayList<Score> scores) {
	    ArrayList<Score> clonedList = new ArrayList<Score>(scores.size());
	    for (Score s : scores) {
	    	//pass the old answer to the copy constructor
	        clonedList.add(new Score(s));
	    }
	    return clonedList;
	}
	
	// This updates the score arraylist with the new score for every client, and then it sends it to all the clients connected
	private void updateScore(ClientThread c) {
		
		ArrayList<Score> cc = cloneScoreList(clientScores);
		for(int i = 0; i < clients.size(); i++) {
			int threadID = c.getID();
			int scoreListPort = cc.get(i).port;
			
			if(threadID == scoreListPort) {
				cc.get(i).score = c.score;
				clientScores.get(i).score = c.score;
			}
		}
		
		for(int j = 0; j < clients.size(); j++){
			clients.get(j).sendMsg(cc);
		}
	}
	
	// Finds who has the highest score and return that clients thread
	private ClientThread findScoreLeader() {
		ClientThread leader = clients.get(0);
		
		for(int i = 0; i < clientNum; i++) {
			if(clients.get(i).score > leader.score){
				leader = clients.get(i);
			}
		}
		return leader;
	}	
	
	private void endGame() {
		
		// Wait for two seconds before ending the game
		try {
		
		ClientThread leader = findScoreLeader();
		for(int i = 0; i < clientNum; i++) {
			clients.get(i).sendMsg(leader.username + " wins the game with a score of " + leader.score);

		}
		//Stops the server
		stop();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
