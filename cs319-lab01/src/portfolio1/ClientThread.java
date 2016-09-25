package portfolio1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ClientThread extends Thread {
	
	private Server server = null;
	public Socket socket = null;
	private int ID;
	public String username; 
	private ObjectInputStream streamIn = null;
	private ObjectOutputStream streamOut = null;
	public  ArrayList<Answer> sentAnswers = new ArrayList<Answer>();
	public  String createdFakeAnswer;
	public int score = 0;
	
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
	
	public void sendMsg(Object msg) {
		
		try {
			streamOut.writeObject(msg);
			streamOut.flush();
		}
		catch(IOException e){
			JOptionPane.showMessageDialog(new JFrame(), "Failure trying to send message" + e.getMessage());
			 server.remove(ID);
			 stop();
		}
	}
	
	public int getID(){
		return ID;
	}
	
	public String getUsername() {
		return username;
	}
	
	public int getScore() {
		return score;
	}
	
	public ArrayList<Answer> getSentAnswers() {
		return sentAnswers;
	}
	
	public String getFakeAnswer() {
		return createdFakeAnswer;
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