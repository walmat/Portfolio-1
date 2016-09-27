package portfolio1;

/**
 * @author Donavan Brooks and Matt Wall
 * 
 */

import java.awt.Color;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Client implements Runnable
{
	private Socket socket = null;
	private Thread thread = null;
	private ObjectOutputStream streamOut = null;
	public String username;
	private ClientGUI frame = null;
	private ServerThread serverTH = null;
	private DataInputStream console = null;
	private boolean clientType;
	public Color color;
	private boolean roundStarted = false;
	private boolean answerRound = false;
	private QuestionUI answerFrame;
	private String receivedQuestion;
	
	// This determines whether they are trying to send a message or image file

	public Client(String ipAddr, String username, String password, String email, Color color_, int serverPort, boolean type)
	{
		this.username = username;
		clientType =  type;
		color = color_;
		// set up the socket to connect to the gui
		try
		{
			socket = new Socket(ipAddr, serverPort);
			start();
		} catch (UnknownHostException h)
		{
			System.out.println("Unknown Host " + h.getMessage());
			System.exit(1);
		} catch (IOException e)
		{
			System.out.println("IO exception: " + e.getMessage());
			System.exit(1);
		}
	}

	public void run()
	{
		//check for a new message, once we receive it, streamOut will send it to the server
		// Messages are sent to the sever in a String array with the format: [port][message][username]
		
		while(thread != null){
			String[] message = new String[3];
			
			if(frame != null) { 
				// This will hold the port of the client that sent the message
				message[0] = String.valueOf(this.socket.getLocalPort());
				
				// This sends the message to the server if you click enter or the send button on ClientGUI
				if(frame.newTextMessage == true) {
				
					try{
						message[1] = frame.getMessage();
						message[2] = username;
						streamOut.writeObject(message);
						streamOut.flush();
						
						// If you are in the middle of a round it will print to console what fake answer you sent in
						if(roundStarted == true) {
							
							System.out.println("You submitted \"" + frame.getMessage() + "\" as your fake answer");
						}
					}catch (IOException e) {
						JOptionPane.showMessageDialog(new JFrame(), "Error Sending Message" + e.getMessage());
						stop();
					}	
				}
			
				// If you click the host clicks the startRound button then it will sned a message to the server, alerting them to start the round
				if(frame.startRound == true && roundStarted == false) {
					
					try {
						message[1] = "....Start....";
						message[2] = username;
						streamOut.writeObject(message);
						streamOut.flush();
						frame.startRound = false;
						roundStarted = true;
					} catch (IOException e) {
						JOptionPane.showMessageDialog(new JFrame(), "Error while trying to start the round" + e.getMessage());
						stop();
					}
				}
				
				// This sends your answer for the answer round to the server and prints to console the answer you selected
				if(answerFrame != null && answerFrame.newAnswerMessage == true) {
					try {
						System.out.println("Selected Answer: " + answerFrame.getMessage());
						message[1] = answerFrame.chosenAnswer;
						message[2] = username;
						streamOut.writeObject(message);
						streamOut.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	// Handles all messages that are received by server and interprets how to handle them
	
	public synchronized void handleChat(Object msg) {
	
		if(msg instanceof Integer)
		{
			if(answerRound == true) {
				answerFrame.changeTimerText(msg + "");
				if((int) msg <= 0) {
					answerRound = false;
					// This will highlight the right answer in Green for three seconds  after the round is over , then dispose of the answerFrame and make the clientGUI visible again
					if(answerFrame != null) {
	
						try {
							TimeUnit.SECONDS.sleep(2);
					    	answerFrame.highlightRightAnswer();
					    	TimeUnit.SECONDS.sleep(3);
					        answerFrame.dispose();
					        frame.setVisible(true);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			// This changes the timer for when you are creating your fake answer
			else {
				frame.changeBtnText(msg + "");
				if((int) msg <= 0) {
					frame.changeBtnText("Send");
					roundStarted = false;
				}
			}
			
			
		}
		// If the client recieves a question then print it to the chat area and start the round
		else if(msg instanceof Question){
			
			frame.recieveMessage(((Question) msg).getQuestion());
			receivedQuestion = ((Question) msg).getQuestion();
			roundStarted = true;
		}
		
		else if(msg instanceof ArrayList<?>) {
			
			// If the client receives an array list of questions then open up a QuestionUI and make the clientGUI not visible
			if(((ArrayList<?>) msg).get(0) instanceof Answer) {
				answerFrame = new QuestionUI(receivedQuestion, (ArrayList<Answer>)msg, socket.getLocalPort(), frame.color);
				frame.setVisible(false);
				answerFrame.setVisible(true);
				answerRound = true;
			}
			// If the client receives an array list of scores then open update the clientGUI with the new scores
			else if(((ArrayList<?>) msg).get(0) instanceof Score) {
				frame.updateScoreUI(((ArrayList<Score>) msg));
				frame.revalidate();
				frame.repaint();
			}
		}
		
		// This will update the UI that displays the round you are on
		else if(msg instanceof String[]) 
		{	
			if(((String) Array.get(msg, 0)).trim().equals("Round")) {
				String currRound = (String) Array.get(msg, 1);
				String endRound = (String) Array.get(msg, 2);
				frame.updateRounds(currRound, endRound);
			}
		}
		
		// ---------- EROR Handling sent by the server ----------------
		
		else if (msg.equals("Sorry, the game is in the middle of a round. Please try again later") 
				|| msg.equals("Sorry, the Server is full:( Try Again later.")) {
				System.out.println("Error Message Received");
				JOptionPane.showMessageDialog(new JFrame(), msg);
				frame.dispose();
		}
		
		else if (msg.equals("You submitted the right answer") 
				|| ((String) msg).contains(" clicked your fake answer")) {
				System.out.println(msg);
		}
		
		else if(((String) msg).contains(" wins the game with a score of ")) {
			JOptionPane.showMessageDialog(new JFrame(), msg, "GAME OVER!!", JOptionPane.INFORMATION_MESSAGE);
			frame.dispose();
		}
		
		else if(((String) msg).contains("It seems that not everyone submitted a fake answer/answered the question")) {
			JOptionPane.showMessageDialog(new JFrame(), msg);
		}
		//--------------- Done -------------------------------
		
		// If the client receives anything else than just print it to the chat area
		else {
				frame.recieveMessage((String)msg);
			}
		}
		

	public void start() throws IOException
	{
			frame = new ClientGUI(username, color, clientType);
			frame.setVisible(true);
			
			streamOut = new ObjectOutputStream(socket.getOutputStream());
			String[] connectedMsg = {String.valueOf(socket.getLocalPort()) , "Connected", username};
			streamOut.writeObject(connectedMsg);
			
			if(thread == null) {
				serverTH = new ServerThread(this, socket);
				thread = new Thread(this);
				thread.start();
			}
	}

	public void stop()
	{
		if(thread != null) {
			thread.stop();
			thread = null;
		}
		
		try {
			if(streamOut != null) {
				streamOut.close();
			}
			if(socket != null) {
				socket.close();
			}
			if(console != null) {
				console.close();
			}
		}
		catch (IOException e) {
			System.out.println("Error while trying to close: " + e.getMessage());
		}
		serverTH.close();
		serverTH.stop();

	}

	public ClientGUI getClientGUI() {
		return frame;
	}
}

//Thread that solely listens for messages from the server
class ServerThread extends Thread {
		private Socket sock = null;
		private Client client = null;
		private ObjectInputStream streamIn = null;
		
	
		public ServerThread(Client chatClient, Socket socket_) {
			
			client = chatClient;
			sock = socket_;
			open();
			start();
		}
		
		public void open()
		{
			try{
				streamIn = new ObjectInputStream(sock.getInputStream());
			}
			catch(IOException e)
			{
				System.out.println("Error while trying to get Input Stream: " + e.getMessage());
				client.getClientGUI().dispose();
				client.stop();
			}
		}
		public void close()
		{
			try{
				if(streamIn != null) {
					streamIn.close();
				}
			}
			catch(IOException e){
					System.out.println("Error while trying to close input stream: "  + e.getMessage());
				}
		}
		public void run()
		{
			while(true) {
				try
				{
					client.handleChat(streamIn.readObject());
				}
				catch(IOException e) {
					System.out.println("Problem When trying to listen for messages: " + e.getMessage());
					
					// Show a JOptionFrame warning message and then close the GUI if you lose connection to the server
					JOptionPane.showMessageDialog(new JFrame(), "Host ended the game or you lost connection");
					if(client.getClientGUI() != null) {
						client.getClientGUI().dispose();
					}
					client.stop();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
		