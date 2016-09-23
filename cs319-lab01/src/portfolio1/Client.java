package lab01;

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
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Client implements Runnable
{
	private Socket socket = null;
	private Thread thread = null;
	private ObjectOutputStream streamOut = null;
	//private ClientThread client = null;
	private String username;
	private ChatGUI frame = null;
	private AdminGUI frame1 = null;
	private ClientThread chatClient = null;
	private DataInputStream console = null;
	private int recievedImages = 0;
	
	// This determines whether they are trying to send a message or image file
	private int functionality = 0;

	public Client(String ipAddr, String username, int serverPort, boolean admin)
	{
		this.username = username;
		
		// set up the socket to connect to the gui
		try
		{
			socket = new Socket(ipAddr, serverPort);
			start(admin);
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
		//TODO check for a new message, once we receive it, streamOut will send it to the server
		while(thread != null){
			if(frame != null && frame.newTextMessage == true) {

				try {
					String message = username + ": " + frame.getMessage();
					// Writes encrypted text message to OutputStream
					streamOut.writeObject(encryptTextMessage(message));
					streamOut.flush();
				} 
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("Error Sending message: " + e.getMessage());
					stop();
				}	
			}
			
			if(frame != null && frame.newImageMessage == true) {

				try {
					
					String filePath = frame.getMessage();
					streamOut.writeObject(encryptImageMessage(filePath));
					streamOut.flush();
				} 
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("Error Sending message: " + e.getMessage());
					stop();
				}	
			}
			
			if(frame1 != null && frame1.newTextMessage == true) 
			{
				try {
					
					String message = username + ": " + frame1.getMessage();
					streamOut.writeObject(encryptTextMessage(message));
					streamOut.flush();
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("Error Sending message: " + e.getMessage());
					stop();
				}	
			}
			
		
			if(frame1 != null && frame1.newImageMessage == true) 
			{
				try {
					
					String filePath = frame1.getMessage();
					streamOut.writeObject(encryptImageMessage(filePath));
					streamOut.flush();
				} 
				catch (IOException e) {
	
					e.printStackTrace();
					System.out.println("Error Sending message: " + e.getMessage());
					stop();
				}	
			}
			
			if(frame1 != null && frame1.deleteMessage == true) 
			{
				try {
					int msgIndex = 0;
	
					// Holds the index of the message you wnat to delete
				    msgIndex = Integer.parseInt(frame1.getMessage());
				    
				    System.out.println("Index: " + msgIndex);
				    File chatHistory = new File("chat.txt");
				    BufferedReader reader = new BufferedReader(new FileReader(chatHistory));
					File tempFile = new File("tempFile.txt");
					PrintWriter writer = new PrintWriter(new FileWriter(tempFile),true);
	
					Scanner fileScan = new Scanner(chatHistory);
					
					// This checks to see if there are any messages in the chat if not it throws a NoSuchElementException
					fileScan.nextLine();
					int lineCount = 0;
					String currentLine;
	
					// For every line in chat.txt if it is not the index that wants to be deleted it writes it to a tempFile.txt
					while((currentLine = reader.readLine()) != null) {
						
						if(lineCount != (msgIndex - 1)) {
							
							System.out.println(lineCount + ":" + currentLine);
							writer.append(currentLine + "\n");
							writer.flush();
						}
		
						if(!fileScan.hasNextLine() && lineCount < msgIndex - 1) {
							JOptionPane.showMessageDialog(new JFrame(), "Chat History Message Index Does Not Exist");
							return;
						}
						lineCount++;
					}
					
					writer.close();
					fileScan.close();
					reader.close();
					
					// This renames tempFile.txt to chat.txt
					try {
						Files.move(tempFile.toPath(), chatHistory.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
					}
					catch(FileSystemException e)
					{
						JOptionPane.showMessageDialog(new JFrame(), "Try closing other clients in order to delete messages");
					}
			
				}
				
				catch(NoSuchElementException e) 
				{
					JOptionPane.showMessageDialog(new JFrame(), "No messages in chat history");
				}
				catch(FileSystemException e)
				{
					JOptionPane.showMessageDialog(new JFrame(), "Try closing other clients in order to delete messages");
				}
				catch (IOException e) {
					e.printStackTrace();
					System.out.println("Error Sending message: " + e.getMessage());
					stop();
				}	
			}
			
			if(frame1 != null && frame1.listMessages == true) 
			{
				BufferedReader reader = null;
				File file = new File("chat.txt");
			    try {
					reader = new BufferedReader(new FileReader(file));
				} catch (FileNotFoundException e) {
					JOptionPane.showMessageDialog(new JFrame(), "Chat.txt does not exist/ or no messages in chat history");
				}
			    
			   // Gets the past messages sent and prints them to the gui
			   String currentLine;
			   String chatHistory = "\n--- Chat History --- \n";
			   try {
				   while((currentLine = reader.readLine()) != null)
					   chatHistory += currentLine + "\n";
			   } catch (IOException | NullPointerException e) {
				JOptionPane.showMessageDialog(new JFrame(), "There are no messages in chat history");
			   }
			   
			   frame1.recieveMessage(chatHistory);
			   frame1.listMessages = false;
			}

		}
	}

	public String[] encryptTextMessage(String message) {
		
		// ------ Encryption of message -----
		byte[] b = message.getBytes();
		for(int i = 0; i < b.length; i++) {
			b[i] = ((byte)(b[i]^11110000));
		}
		String encryptedMsg = new String(b);
		// -----  Done -----------------------

		String[] msgArray = new String[3];
		msgArray[0] = msgArray.length + "";
		msgArray[1] = encryptedMsg;
		msgArray[2] = "Text";
		
		return msgArray;
		
	}
	
	public String[] encryptImageMessage(String filePath) {
		
		File imgPath = new File(filePath);
		BufferedImage bufferedImg = null;
		try {
			bufferedImg = ImageIO.read(imgPath);
		} catch (IOException e) {
			System.out.println("Error while trying to read Image Path" + e.getMessage());
			e.printStackTrace();
		}
		
		String[] fileName = imgPath.getName().split("\\.");
		String fileExtension = fileName[fileName.length - 1] ;
		
		// This writes the buffered image to the ByteArrayOutputStream
		ByteArrayOutputStream streamImg = new ByteArrayOutputStream();
		try {
			ImageIO.write(bufferedImg, fileExtension , streamImg);
		} catch (IOException e) {
			System.out.println("Error while trying to write bufferedImage to ByteArrayOutputStream " + e.getMessage());
			e.printStackTrace();
		}
		
		//Holds the bytes of the image
		byte[] b = streamImg.toByteArray();
		
		//Holds the bits turned into characters
		String message = "";
		
		int remainder = (b.length - ((int)b.length/3) * 3);
	
		// ------ Encryption of Image Message -----
		
		// Gets the 24 bits, splits them into four 6 bit fragments and adds '00' to the rightmost side
		for(int i = 0; i < b.length - remainder; i+=3)
		{
			String bitString = ((Integer.toBinaryString(b[i] & 255 | 256).substring(1)) + (Integer.toBinaryString(b[i+1] & 255 | 256).substring(1)) + (Integer.toBinaryString(b[i+2] & 255 | 256).substring(1)));
			
			String[] parts = bitString.split("(?<=\\G.{6})");
			for(int j = 0; j < parts.length; j++) {
				String piece = parts[j] + "00";
				
				int parseInt = Integer.parseInt(piece, 2);
				char c = (char)parseInt;
				message += c;
			}	
		}
		
		//This adds the left over bytes to the message if the image is not mod 3
		if(remainder != 0)
		{
			for(int i = (b.length)-remainder ; i < b.length; i ++) {
				
				String bitString = (Integer.toBinaryString(b[i] & 255 | 256).substring(1));
				int parseInt = Integer.parseInt(bitString, 2);
				char c = (char)parseInt;
				message += c;
			}
		}
		// Sends nformation to server like this and server interprets it
		String[] msgArray = new String[7];
		msgArray[0] = msgArray.length + "";
		msgArray[1] = username;
		msgArray[2] = message;
		msgArray[3] = remainder + "";
		msgArray[4] = fileName[0];
		msgArray[5] = fileExtension;
		msgArray[6] = "Image";
		
		return msgArray;
	}
	
	public synchronized void handleChat(Object msg)
	{
		//TODO
		//If it is a text message just print it in the ui
	if(msg instanceof String){
	
		if(username.equalsIgnoreCase("Admin"))
		{
			frame1.recieveMessage((String)msg);
		}
		else
			frame.recieveMessage((String)msg);
	}
	
	// If it is a file it saves it to your working directory and opens up a Jframe that displays it
	else if(msg instanceof File){
	
		BufferedImage bi;
	
		File outputfile = new File(username + "_" + ((File) msg).getName());
		if(username.equalsIgnoreCase("Admin"))
		{
			System.out.println("Recieved file");
			  try {
				bi = ImageIO.read((File) msg);
				ImageIO.write(bi, "png", outputfile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  frame1.showPicture((File)msg);
		}
		else{
				System.out.println("Recieved file");
			  try {
				bi = ImageIO.read((File) msg);
				ImageIO.write(bi, "png", outputfile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  frame.showPicture((File)msg);
		}
		//This variable is just so the naming of each photo received is different
		recievedImages++;
		
	}
	
	}

	public void start(boolean admin) throws IOException
	{
		if (admin) {
			frame1 = new AdminGUI(username);
			frame1.setVisible(true);
			
		} else {
			frame = new ChatGUI(username);
			frame.setVisible(true);
		}
		
		streamOut = new ObjectOutputStream(socket.getOutputStream());
//		console = new DataInputStream(frame.getMessage());
		
		if(thread == null) {
			chatClient = new ClientThread(this, socket);
			thread = new Thread(this);
			thread.start();
		}
	}

	public void stop()
	{
		//TODO
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
		chatClient.close();
		chatClient.stop();

	}
	
	public static void main(String args[]) {
		Login log = new Login();
		log.main(args);
	}
}


	class ClientThread extends Thread {
		private Socket sock = null;
		private Client client = null;
		private ObjectInputStream streamIn = null;
		
	
		public ClientThread(Client chatClient, Socket socket_) {
			
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
		