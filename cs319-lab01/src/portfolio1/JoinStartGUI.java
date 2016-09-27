package portfolio1;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
/**
 * 
 * @author Donavan Brooks and Matt Wall
 * 
 * GUI that prompts users whether they want to join or start a game.
 * 		- If start game is selected then it creates a server and launches your host client
 * 		- To join a game you must enter the valid ipaddress of you host, who must be on the same network, and it launches your client and connects you to the server
 *
 */
public class JoinStartGUI extends JFrame{

		private JPanel contentPane;
		private JTextField textField;
		private JTextArea chatArea;
		
		volatile boolean newTextMessage = false; 
		volatile boolean newImageMessage = false;
		private String username;
		private String password;
		private String email;
		private Color color;
		private String user;
		private static Server server =  null;
		
		/**
		 * Create the frame.
		 */
	public JoinStartGUI(String username_, String password_, String email_, Color color_)
		{
			username = username_;
			password = password_;
			email = email_;
			color = color_;
			
			setTitle(username + ": Join/Start");
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setBounds(100, 100, 450, 200);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);
			
			textField = new JTextField();
			textField.setBounds(32, 55, 186, 23);
			contentPane.add(textField);
			textField.setColumns(10);
			user = username;
			
			JButton btnStart = new JButton("Start A game");
			btnStart.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							try {
								// start the server and client
								server = new Server(1222);
								Client client = new Client("localhost", username, password, email, color, 1222, true);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
					dispose();
				}
			});
			btnStart.setBounds(110, 100, 200, 23);
			contentPane.add(btnStart);
			
			//when btnsend pressed, send the message
			btnStart.addActionListener(new ActionListener()
			{
				
				@Override
				public void actionPerformed(ActionEvent e)
				{
					// TODO Set format of message and do some action to send it.
					newTextMessage = true;
					
				}
			});
			
			JLabel lblJoinGame = new JLabel("To Join A Game, Enter an ipAddress and Click Join a Game");
			lblJoinGame.setBounds(17, 30, 400, 16);
			contentPane.add(lblJoinGame);
			
			JButton btnJoin = new JButton("Join A game");
			btnJoin.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							try {
								// Start the client that connects to the ipaddress you entered
								Client client = new Client(textField.getText(), username, password, email, color, 1222, false);
							} catch (Exception e) {
								System.out.println("unable to create client: " + e.getMessage());
							}
						}
					});
					dispose();
				}
			});
			btnJoin.setBounds(240, 55, 150, 23);
			contentPane.add(btnJoin);
			
			//when btnsend pressed, send the message
			btnJoin.addActionListener(new ActionListener()
			{
				
				@Override
				public void actionPerformed(ActionEvent e)
				{
					// TODO Set format of message and do some action to send it.
					newTextMessage = true;
					
				}
			});
		}
		
		public String getMessage()
		{		
			if(newTextMessage == true) {
				newTextMessage = false;
				//message = user + ": " + textField.getText();
			}
			else if(newImageMessage == true) {
				newImageMessage = false;
			}
			
			return "";
		
		}
		
		public void recieveMessage(String message)
		{
			if (!message.trim().equals("")){
				chatArea.append(message + "\n");
			}
			textField.setText("");
		}

	}
