package portfolio1;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import javax.swing.JComboBox;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

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
