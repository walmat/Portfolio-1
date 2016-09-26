package portfolio1;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Donavan Brooks and Matt Wall
 * 
 * Client used to receive messages from the server and broadcast messages to the server where they will be sent to all clients.
 * 	You can send text messages and images.
 * 		- The text messages will be written to the chat area in your gui
 * 		- Images will be written to a file in your working directory and then will be sent to all the clients
 * 
 * 	If you recieve an image it will be saved to your working directory and opened up in a JFrame
 * 	A text message will just be displayed in chatGui
 */

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.Timer;

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
	private boolean connected = false;
	private Server s;
	private QuestionUI answerInput;
	private String fakeAnswer;
	private String receivedQuestion;
	private int score = 0;
	
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
		while(thread != null){
			String[] message = new String[3];
			
			if(frame != null) { 
				
				message[0] = String.valueOf(this.socket.getLocalPort());
				
				if(frame.newTextMessage == true) {
				
					try{
						message[1] = frame.getMessage();
						message[2] = username;
						streamOut.writeObject(message);
						streamOut.flush();
					}catch (IOException e) {
						JOptionPane.showMessageDialog(new JFrame(), "Error Sending Message" + e.getMessage());
						stop();
					}	
				}
			
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
				
				if(answerInput != null && answerInput.newAnswerMessage == true) {
					try {
						message[1] = answerInput.getMessage();
						message[2] = username;
						streamOut.writeObject(message);
						streamOut.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				if(roundStarted == true) {
					fakeAnswer = frame.getMessage();
				}
			}
		}
	}

	
public void handleChat(Object msg)
	{
	//If it is a text message just print it in the ui
	if(msg instanceof Integer)
	{
		if(answerRound == true) {
			answerInput.changeTimerText(msg + "");
		}
		else {
			frame.changeBtnText(msg + "");
		}
		
		if((int) msg <= 0) {
			frame.changeBtnText("Send");
			roundStarted = false;
			answerRound = false;
			if(answerInput != null) {
				answerInput.dispose();
			}
		}
	}
	
	else if(msg instanceof Question){
		
		frame.recieveMessage(((Question) msg).getQuestion());
		receivedQuestion = ((Question) msg).getQuestion();
		roundStarted = true;
	}
	
	else if(msg instanceof ArrayList<?>) {
		
		if(((ArrayList<?>) msg).get(0) instanceof Answer) {
			answerInput = new QuestionUI(receivedQuestion, (ArrayList<Answer>)msg);
			answerInput.setVisible(true);
			answerRound = true;
		}
		
		else if(((ArrayList<?>) msg).get(0) instanceof Score) {
			System.out.println(((ArrayList<Score>) msg).toString());
			frame.updateScoreUI(((ArrayList<Score>) msg));
			frame.revalidate();
			frame.repaint();
		}
	}
	
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
		//System.out.println("Ouput Stream created");
		
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
}

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
					client.stop();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
		