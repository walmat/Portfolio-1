package portfolio1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ClientThread extends Thread {
	
	private Server server = null;
	private Socket socket = null;
	private int ID;
	private ObjectInputStream streamIn = null;
	private ObjectOutputStream streamOut = null;
	private boolean acceptAnswers = true;
	
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
			JOptionPane.showMessageDialog(new JFrame(), "Failure trying to send message" + e.getMessage());
			 server.remove(ID);
			 stop();
		}
	}
	
	public void sendQuestion(Question q) {
		
		try {
			streamOut.writeObject(q);
			streamOut.flush();
		}
		catch(IOException e){
			JOptionPane.showMessageDialog(new JFrame(), "Failure trying to send question" + e.getMessage());
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