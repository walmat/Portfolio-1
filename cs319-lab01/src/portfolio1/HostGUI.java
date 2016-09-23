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
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.Choice;
import java.awt.List;
import java.awt.ComponentOrientation;
import java.awt.Cursor;

public class HostGUI extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextArea chatArea;

	volatile boolean newTextMessage = false;
	volatile boolean newImageMessage = false;
	volatile boolean listMessages = false;
	volatile boolean deleteMessage = false;
	volatile boolean startRound = false;
	private String message;

	/**
	 * Create the frame.
	 */
	public HostGUI(String username) {
		setTitle(username);
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
		chatArea.setBounds(6, 6, 167, 69);
		chatArea.setLineWrap(true);
		contentPane.add(chatArea);

		textField = new JTextField();
		textField.setBounds(27, 197, 183, 23);
		contentPane.add(textField);
		textField.setColumns(10);

		JButton btnSendText = new JButton("Send Text to Clients");
		btnSendText.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
			
			}
		});
		btnSendText.setBounds(275, 63, 147, 23);
		contentPane.add(btnSendText);

		// when btnsend pressed, send the message
		btnSendText.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Set format of message and do some action to send it.
				newTextMessage = true;
			}
		});

		// allow for hitting enter to send a chat message
		textField.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					// TODO when user press Enter the message should be submit.
					newTextMessage = true;
				}

			}
		});

		JLabel lblChatHistory = new JLabel("Chat History");
		lblChatHistory.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		lblChatHistory.setBounds(17, 30, 161, 16);
		contentPane.add(lblChatHistory);

		JScrollPane scrollBar = new JScrollPane(chatArea);
		scrollBar.setLocation(24, 58);
		scrollBar.setSize(186, 101);
		scrollBar.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		contentPane.add(scrollBar);

		JLabel label = new JLabel("Message");
		label.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		label.setBounds(17, 171, 64, 14);
		contentPane.add(label);
		
		JButton btnStart = new JButton("Start Round");
		btnStart.setBounds(275, 95, 147, 23);
		contentPane.add(btnStart);
		btnStart.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						
						startRound = true;
					}
				});
			}
		});


	}

	public String getMessage() {
		if(newTextMessage == true) {
			newTextMessage = false;
			message = textField.getText();
		}
		else if(newImageMessage == true) {
			newImageMessage = false;
		}
		else if(deleteMessage == true) {
			message = textField.getText();
			deleteMessage = false;
		}
	
		return message;
	}

	public void recieveMessage(String message) {
		if (!message.trim().equals("")) {
			chatArea.append(message + "\n");
		}
		textField.setText("");
	}
	
	public void showPicture(File picFile) {
		
		// Makes a new Jframe that displays the image recieved
		JPanel imagePanel = new JPanel();
	
		 BufferedImage image = null;
		try {
			image = ImageIO.read(picFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  JLabel picLabel = new JLabel(new ImageIcon(image));
		  imagePanel.add(picLabel);
		  imagePanel.repaint();
		  
		JFrame frame = new JFrame("Recieved Picture");
	  		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	  		frame.add(imagePanel);
	  		frame.setSize(400, 400);
	  		frame.setVisible(true);
	}		

}
