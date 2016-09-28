package portfolio1;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

public class ServerGUI extends JFrame
{
	private JPanel contentPane;
	private JTextArea chatArea;
	
	private volatile boolean newMessage = false; 
	private String message;
	

	/**
	 * Create the frame.
	 */
	public ServerGUI()
	{
		setTitle("Server");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		chatArea = new JTextArea();
		chatArea.setForeground(Color.BLACK);
		chatArea.setFont(new Font("Tahoma", Font.PLAIN, 13));
		chatArea.setEnabled(false);
		chatArea.setBounds(19, 33, 167, 108);
		chatArea.setLineWrap(true);
		contentPane.add(chatArea);
		
		JScrollPane scrollBar = new JScrollPane(chatArea);
		scrollBar.setLocation(24, 32);
		scrollBar.setSize(393,213);
		scrollBar.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		contentPane.add(scrollBar);
		
	}
	
	public String getMessage()
	{
		//poll for a new message
		while(!newMessage)
		{
			try
			{
				Thread.sleep(1);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//new message 
		newMessage = false;
		return message;
	}
	
	public void recieveMessage(String message)
	{
		//new message received 
		EventQueue.invokeLater(new Runnable()
		{
			
			@Override
			public void run()
			{
				//update the text area
				chatArea.append(message + "\n");
				revalidate();				
			}
		});
	}
}
