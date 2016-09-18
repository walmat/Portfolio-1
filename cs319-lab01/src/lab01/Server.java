package lab01;

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
	private ClientThread clients[] = new ClientThread[30];
	private int clientNum = 0;

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
	}

	public void run()
	{
		//TODO wait for a client or show error
		while(true){
			try {
				System.out.println("Waiting for a client....");
				addThread(serverSocket.accept());
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

	public synchronized void handle(String[] input) throws IOException
	{
		// TODO new message, send to clients and then write it to history
		
		//Text data is sent an array like [arraySize][encryptedMessage][dataType]
		
		String dataType = input[new Integer(input[0]) - 1];
		String message = "";
		if(dataType.equalsIgnoreCase("text"))
		{
			//-------- Decryption of Text Message -----
			message = new String(decryptTextMsg(input[1]));
		}
		
		//The image will be saved to your current working directory
		else if(dataType.equalsIgnoreCase("image"))
		{
			
			// Image data is sent as an array in the format [arraySize][username][encryptedMessage][remainder][fileName][FileExtension][dataType]
						// Remainder holds the amount of bytes left over if the array is mod 3
			
			String username = input[1];
			String imageString = input[2].toString();
			int remainder = Integer.parseInt(input[3]);
			String fileName = input[4];
			String fileExtension = input[5];
			
			String imageSendLocation = fileName + "." + fileExtension;
		
			//--------- Decrypts Message and saves it to a file -------
			 try {
		            final BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(decryptImageMsg(imageString, remainder)));
		            ImageIO.write(bufferedImage, fileExtension, new File(imageSendLocation));
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
			//----------- DONE -------------
		
			 message = username + ": image saved to your working directory";
			 
				File imgFile = new File(imageSendLocation);
				// Send message to other clients
				for(int i = 0; i < clientNum; i++) {	
					clients[i].streamOut.writeObject(new File(imageSendLocation)); 
					clients[i].streamOut.flush();
				}
		}

		// Send message to other clients
		for(int i = 0; i < clientNum; i++) {
			clients[i].sendMsg(message);
		}
		
		// Writes Messages to "chat.txt"
		writer = new PrintWriter(new FileWriter(file,true));
		writer.append(message + "\n");
		writer.flush();
		writer.close();
		
		//TODO update own gui
		frame.recieveMessage(message);
		
	}

	// Decryptes text message sent by the client
	public byte[] decryptTextMsg(String encryptedMsg) {
		
		byte[] b = null;
		b = encryptedMsg.toString().getBytes();
		for(int j = 0; j < b.length; j++) {
			b[j] = (byte)(b[j]^11110000);
		}
		
		return b;
	}
	
	// Decryptes Image sent by the client
	public byte[] decryptImageMsg(String encryptedMsg, int remainder) 
	{
		String decryptedMessage = "";
		
		int index = 0;
		char[] encryptedArray = encryptedMsg.toCharArray();
		
		while(index < (encryptedArray.length)) {
			
			// Turns each character into its binary representation and Removes the "00" from the rightmost side
			if(index < encryptedArray.length - remainder) {
				decryptedMessage +=  (Integer.toBinaryString(((int)encryptedArray[index]) & 255 | 256).substring(1, 7));
			}
			else
			{
				// If the image is not mod 3 this will handle the remainders of the byte values
				decryptedMessage += (Integer.toBinaryString(((int)encryptedArray[index]) & 255 | 256).substring(1));
			}
			index += 1;
		}
		
		// Splits the decrypted string back into 8 bit parts
		String[] parts = decryptedMessage.split("(?<=\\G.{8})");
		byte[] msgByteArray = new byte[parts.length];
		
		// Fills the msgByteArray with the integer representation of the binary string
		for(int i = 0; i < parts.length; i++) {
			msgByteArray[i] = (byte) Integer.parseInt(parts[i], 2);
		}
		
		return msgByteArray;
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
		Server server = null;
		server = new Server(1222);
	}
	
public class ClientThread extends Thread {
		
		private Server server = null;
		private Socket socket = null;
		private int ID;
		private ObjectInputStream streamIn = null;
		private ObjectOutputStream streamOut = null;
		
		public ClientThread(Server server_, Socket socket_)
		{
			server = server_;
			socket = socket_;
			ID = socket.getPort();
		}
		
		public void run() {
			System.out.println("ClientThread" + ID + "has now started....");
			
			while(true){
				 try {
					 server.handle((String[])streamIn.readObject());
				 }
				 catch(IOException e) {
					 System.out.println("Failure trying to create Input/Output Stream: " + e.getMessage());
					 server.remove(ID);
			         stop();
				 } catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		public void sendMsg(String msg) {
			
			try {
				streamOut.writeObject(msg);
				streamOut.flush();
			}
			catch(IOException e){
				 System.out.println("Failure trying to send Message " + e.getMessage());
				 server.remove(ID);
				 stop();
			}
		}
		
		public int getID(){
			return ID;
		}
		
		public void open() throws IOException
		{
			streamIn = new ObjectInputStream(socket.getInputStream());
			streamOut = new ObjectOutputStream(socket.getOutputStream());
		}
		
		public void close() throws IOException 
		{
			if(socket != null) {
				socket.close();
			}
			if(streamIn != null) {
				streamIn.close();
			}
			if(streamOut != null) {
				streamOut.close();
			}
		}
	}
}
