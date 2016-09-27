package portfolio1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import java.awt.Font;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class ClientGUI extends JFrame
{

	/**
	 * serial
	 */
	private static final long serialVersionUID = 5380090635516117072L;
	private JPanel contentPane;
	private JTextField textField;
	private JTextArea chatArea;
	
	volatile boolean newTextMessage = false; 
	volatile boolean newImageMessage = false;
	private String message;
	public boolean startRound;
	private JButton btnSend;
	private String user;
	public Color color;
	
	//Score labels
	JLabel lblScore1;
	JLabel lblScore2;
	JLabel lblScore3;
	JLabel lblScore4;
	
	// Number of rounds label
	JLabel lblNumRounds;

	/**
	 * Create the frame.
	 */
	public ClientGUI(String username, Color color, boolean type)
	{
		this.color = color;
		user = username;
		setTitle(username);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.setBackground(color);
		
		chatArea = new JTextArea();
		chatArea.setForeground(Color.BLACK);
		chatArea.setFont(new Font("Tahoma", Font.PLAIN, 13));
		chatArea.setEnabled(false);
		chatArea.setBounds(6, 6, 175, 69);
		chatArea.setLineWrap(true);
		DefaultCaret caret = (DefaultCaret)chatArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		contentPane.add(chatArea);
		
		textField = new JTextField();
		textField.setBounds(24, 197, 306, 23);
		contentPane.add(textField);
		textField.setColumns(10);
		
		btnSend = new JButton("Send");
		btnSend.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				newTextMessage = true;
			}
		});
		btnSend.setBounds(337, 199, 89, 23);
		contentPane.add(btnSend);
		
		
		//allow for hitting enter to send a chat message
		textField.addKeyListener(new KeyListener() 
		{
			
			@Override
			public void keyTyped(KeyEvent e) 
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) 
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) 
			{
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					//TODO when user press Enter the message should be submit.
					newTextMessage = true;
				}
					
			}
		});
		
		JLabel lblChatHistory = new JLabel("Question");
		lblChatHistory.setBounds(17, 30, 161, 16);
		contentPane.add(lblChatHistory);
		
		JScrollPane scrollBar = new JScrollPane(chatArea);
		scrollBar.setLocation(24, 58);
		scrollBar.setSize(402,101);
		scrollBar.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		contentPane.add(scrollBar);
		
		JLabel lblTypeYourAnswer = new JLabel("Type your answer");
		lblTypeYourAnswer.setBounds(17, 171, 134, 14);
		contentPane.add(lblTypeYourAnswer);
	
		if(type == true) {
			
			JButton btnStart = new JButton("Start Round");
			btnStart.setBounds(435, 199, 147, 23);
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
		
		if(startRound == true) {
			startRound = false;
		}
		
		JLabel lblScoreTitle = new JLabel("Scores");
		lblScoreTitle.setBounds(500, 20, 161, 31);
		lblScoreTitle.setFont((new Font("Tahoma", Font.PLAIN, 30)));
		contentPane.add(lblScoreTitle);
		
		// Initializing score labels
		lblScore1 = new JLabel();
		lblScore1.setBounds(450, 70, 161, 16);
		lblScore1.setFont((new Font("Tahoma", Font.PLAIN, 16)));

		lblScore2 = new JLabel();
		lblScore2.setBounds(450, 100, 161, 16);
		lblScore2.setFont((new Font("Tahoma", Font.PLAIN, 16)));
	
		lblScore3 = new JLabel();
		lblScore3.setBounds(450, 130, 161, 16);
		lblScore3.setFont((new Font("Tahoma", Font.PLAIN, 16)));
	
		lblScore4 = new JLabel();
		lblScore4.setBounds(450, 160, 161, 16);
		lblScore4.setFont((new Font("Tahoma", Font.PLAIN, 16)));
		
		// Initialize round label
		lblNumRounds = new JLabel();
		lblNumRounds.setBounds(220, 20, 147, 23);
		lblNumRounds.setFont((new Font("Tahoma", Font.PLAIN, 20)));
		contentPane.add(lblNumRounds);
}

	public String getMessage()
	{		
		if(newTextMessage == true) {
			newTextMessage = false;
			message = textField.getText();
			textField.setText("");
		}
		
		return message;
	}
	
	// Prints message to screen
	public void recieveMessage(String message)
	{
		if (!message.trim().equals("")){
			chatArea.append(message + "\n");
		}
		textField.setText("");
	}	
	
	// Changes the button text on the send button
	public void changeBtnText(String txt) {
		btnSend.setText(txt);
	}
		
	// Updates UI to display everyones scores
	public void updateScoreUI(ArrayList<Score> scores) { 
		
		if(scores.size() >= 1) {
			lblScore1.setText(scores.get(0).username + ": " + scores.get(0).score);
			contentPane.add(lblScore1);
		}
		else 
			contentPane.remove(lblScore1);
		
		if(scores.size() >= 2) {
			lblScore2.setText(scores.get(1).username + ": " + scores.get(1).score);
			contentPane.add(lblScore2);
		}
		else
			contentPane.remove(lblScore2);
		
		if(scores.size() >= 3) {
			lblScore3.setText(scores.get(2).username + ": " + scores.get(2).score);
			contentPane.add(lblScore3);
		}
		else
			contentPane.remove(lblScore3);
		
		if(scores.size() >= 4) {
			lblScore4.setText(scores.get(3).username + ": " + scores.get(3).score);
			contentPane.add(lblScore4);
		}
		else
			contentPane.remove(lblScore4);
		}
	
	// Updates the label that keeps track of what round you are on
	public void updateRounds(String currRound, String endRound) {
		
		lblNumRounds.setText("Round " + currRound + " of " + endRound);
	}
			
}

