package portfolio1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
import java.util.Collections;
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
	private ArrayList<Score> clientScores = new ArrayList<Score>();
	static int clientNum = 0;
	static boolean roundStarted = false;
	private static String sentQuestionAns = "";
	private Question sentQuestion; 
	private Timer timer;
	private ObjectOutputStream errorStream;
	private boolean init = false;
	
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
				System.out.println(roundStarted);
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
	
	public synchronized void handle(String[] input)
	{
	
		if(init == true) {
			clients.get(clientNum -1).username = input[2];
//			for(int i = 0; i < clientNum; i++) {
//				clients.get(i).sendMsg(clients.get(i).username + ": " + input[1]);
//			}
			clientScores.add(new Score(clients.get(clientNum -1 ).getID(), clients.get(clientNum -1).username, clients.get(clientNum - 1).score));
			System.out.println("cLIENtscores size: " + clientScores.size());
			updateScore(clients.get(clientNum - 1));
			init = false;
		}
		
		//new message, send to clients and then write it to history
		if(input[0].equals(clients.get(0).getID() + "") && input[1].equals("....Start....") && roundStarted == false) {
			
			try {
				sentQuestion = Question.questions.get(new Random().nextInt(Question.questions.size()));
			}catch(IllegalArgumentException e) {
				JOptionPane.showMessageDialog(new JFrame(), "Sorry, there seems to be no questions in our list."
															+ " Check your internet connection and try again.");
				System.exit(-1);
			}
			
			//clientAnswers.add(new Answer("rightAnswer", sentQuestion.rightAnswer));
			sentQuestionAns = sentQuestion.rightAnswer;
			
			for(int i = 0; i < clientNum; i++) {
				
				clients.get(i).sendMsg(sentQuestion);
				roundStarted = true;
			}
			
			ActionListener actionListener = new ActionListener() 
			{
			    int timeRemaining = 10;
			   
			    public void actionPerformed(ActionEvent evt){
			    	timeRemaining--;
			    	for(int i = 0; i < clientNum; i++) {
			    		clients.get(i).sendMsg(timeRemaining);
			    	}
			        
			        if(timeRemaining <= 0){
			        	timer.stop();
			        	roundStarted = false;
			        	
			        	for(int i = 0; i < clientNum; i++) {
			        		int port = clients.get(i).getID();
			        		
							try {
								ArrayList<Answer> a = randomizeClientAnswers(port);
								//QuestionUI q = new QuestionUI(sentQuestion.question, a);
								
								//It now sends the arrayList not the UI because it was causing the UI not to record button clicks
								clients.get(i).sendMsg(a);
								clients.get(i).sentAnswers = a;
								roundStarted = true;
								//create a new timer to display the QuestionUI for a certain amount of time
								
							} catch (CloneNotSupportedException e) {
								System.out.println("Error trying to randomize answers: " + e.getMessage());
							}
							
			        	}     	
			        	
			        	//clientAnswers.clear();	
			        	
			        	ActionListener alistener = new ActionListener() {
						    int timeRemaining = 10;
						    
						    public void actionPerformed(ActionEvent evt){
						    	timeRemaining--;
						    	for(int i = 0; i < clientNum; i++) {
						    		clients.get(i).sendMsg(timeRemaining);
						    	}
						        
						        if(timeRemaining <= 0){
						        	timer.stop();
						        
						        	for(int i = 0; i < clientNum; i++)
						        	{
						        		calculateScore(clients.get(i));
						        		clients.get(i).sendMsg("Score: " + clients.get(i).score);
						        	}
						        	for(int i = 0; i <clientNum; i++ ){ 
						        		updateScore(clients.get(i));
						        	}
						        	
						        	roundStarted = false;
						        }
						    }
						};
						timer = new Timer(1000, alistener);
						timer.start();
			        	
			       }
			   }
			};
			
			timer = new Timer(1000, actionListener);
			timer.start();
			clientAnswers.clear();	
		}
		
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
						System.out.println("Changing already stated Answer Array");
						clientAnswers.get(i).answer = currClientAnswer;
						found = true;
					}
				}
	 			
	 			if (found == false) {
	 				clientAnswers.add(new Answer(currClientAnswer, port));
	 			}
			}
		}
		
		else if(roundStarted == false){
			//This saves the username of the client who sent the message 
			//clients.get(findClient(Integer.parseInt(input[0]))).username = input[2];
			for(int i = 0; i < clientNum; i++) {
				clients.get(i).sendMsg(input[2] + ": " + input[1]);
			}
		}
		
		frame.recieveMessage(input[1]);
	}
	
	public synchronized void remove(int ID)
	{
		//get the ClientThread, remove it from the array and then terminate it
	
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
		
		// Removes user from client threads from the score arraylsist updates the score arraylist and alerts other users who left the game
		clients.remove(index);
		clientScores.remove(index);
		for(int i = 0; i < clients.size(); i++){
			clients.get(i).sendMsg(removedUser + " has left the game.");
			clients.get(i).sendMsg(cloneScoreList(clientScores));
		}
		clientNum--;
		System.out.println("Client Count: " + clientNum) ;
	}

	private void addThread(Socket socket)
	{
		//add new client
		if (clientNum <= 4 && roundStarted == false){  
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
		} else{
			System.out.println("Sent error message");
			try {
				errorStream = new ObjectOutputStream(socket.getOutputStream());
				errorStream.writeObject("Server full, or the game is in the middle of a round. Try Again later.");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
	}
	
	public ArrayList<Answer> randomizeClientAnswers(int port) throws CloneNotSupportedException{		
		
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
	
	public static ArrayList<Answer> cloneList(ArrayList<Answer> answers) {
	    ArrayList<Answer> clonedList = new ArrayList<Answer>(answers.size());
	    for (Answer a : answers) {
	    	//pass the old answer to the copy constructor
	        clonedList.add(new Answer(a));
	    }
	    return clonedList;
	}
	
	public static int findRightAnswer(ArrayList<Answer> a){
		int i = 0; 
		for (i = 0; i < a.size(); i++){
			if (a.get(i).answer.equals(sentQuestionAns)) break;
		}
		return i;
	}
	
	public void calculateScore(ClientThread c) { 
		
		String clientPort = String.valueOf(c.getID());
		
		System.out.println("RoundStarted " + roundStarted);
		for(int j = 0; j < clientAnswers.size(); j++) {
			
			// If you chose the right answer increment your score by
			if(clientPort.equals(clientAnswers.get(j).port)) {
				if(clientAnswers.get(j).answer.equals(sentQuestionAns)){
					System.out.println("You submitted the right answer");
					c.score += 2;
				}
			}
			
			// If anybody else chose your answer increment your score by 1
			else if(c.createdFakeAnswer != null && c.createdFakeAnswer.equals(clientAnswers.get(j).answer)) {
				
				// This holds the username of the person that clicked you answer
				String user = clients.get(findClient(Integer.parseInt(clientAnswers.get(j).port))).username;
				System.out.println(user + " clicked your answer");
				c.score += 1;
			}
		}
	}
	
	public static ArrayList<Score> cloneScoreList(ArrayList<Score> scores) {
	    ArrayList<Score> clonedList = new ArrayList<Score>(scores.size());
	    for (Score s : scores) {
	    	//pass the old answer to the copy constructor
	        clonedList.add(new Score(s));
	    }
	    return clonedList;
	}
	
	public void updateScore(ClientThread c) {
		
		ArrayList<Score> cc = cloneScoreList(clientScores);
		for(int i = 0; i < clients.size(); i++) {
			int threadID = c.getID();
			int scoreListPort = cc.get(i).port;
			
			System.out.println(threadID == scoreListPort);
			if(threadID == scoreListPort) {
				cc.get(i).score = c.score;
				clientScores.get(i).score = c.score;
			}
	}
		for(int j = 0; j < clients.size(); j++){
			clients.get(j).sendMsg(cc);
		}
	
		
	}
}
