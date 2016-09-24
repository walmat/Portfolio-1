package portfolio1;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class QuestionUI extends JFrame
{

	/**
	 * serial
	 */
	private static final long serialVersionUID = 5380090635516117072L;
	private JPanel contentPane;
	
	volatile boolean newTextMessage = false; 
	volatile boolean newImageMessage = false;
	private String message;
	private JTextArea questionField;

	/**
	 * Create the frame.
	 */
	public QuestionUI(String username, Color color, ArrayList<Answer> answers)
	{
		int numAnswer = answers.size();
		
		
		setTitle(username);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.setBackground(color);

		
		JLabel lblTypeYourAnswer = new JLabel("Select your answer");
		lblTypeYourAnswer.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		lblTypeYourAnswer.setHorizontalAlignment(SwingConstants.CENTER);
		lblTypeYourAnswer.setBounds(148, 102, 142, 31);
		contentPane.add(lblTypeYourAnswer);
		
		JButton button1 = new JButton("");
		button1.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {

			}
		});
		button1.setBounds(51, 150, 163, 23);
		
		//when btnsend pressed, send the message
		button1.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// TODO Set format of message and do some action to send it.
				newTextMessage = true;
			}
		});
		
		JButton button2 = new JButton("");
		button2.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {

			}
		});
		button2.setBounds(226, 150, 163, 23);
		

		JButton button3 = new JButton("");
		button3.setBounds(51, 201, 163, 23);
		
		JButton button4 = new JButton("");
		button4.setBounds(226, 201, 163, 23);
		
		questionField = new JTextArea();
		questionField.setFont(new Font("Gotham Medium", Font.BOLD, 20));
		questionField.setBounds(24, 18, 404, 42);
		contentPane.add(questionField);
		questionField.setColumns(10);
		
		if (numAnswer == 1) {
			contentPane.add(button1);
		}
		else if (numAnswer == 2) {
			contentPane.add(button1);
			contentPane.add(button2);
		}
		else if (numAnswer == 3) {
			contentPane.add(button1);
			contentPane.add(button2);
			contentPane.add(button3);
		}
		else {
			contentPane.add(button1);
			contentPane.add(button2);
			contentPane.add(button3);
			contentPane.add(button4);
		}
		
	}
	
	public String getMessage()
	{		
		if(newTextMessage == true) {
			newTextMessage = false;
			message = questionField.getText();
		}
		else if(newImageMessage == true) {
			newImageMessage = false;
		}
		
		return message;
	
	}
	
	public void recieveMessage(String message)
	{
		if (!message.trim().equals("")){
			questionField.append(message + "\n");
		}
		questionField.setText("");
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
	  		frame.getContentPane().add(imagePanel);
	  		frame.setSize(400, 400);
	  		frame.setVisible(true);
	}		
	
	public void changeButtonText(String msg) {
		
		
	}
}

