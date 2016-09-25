package portfolio1;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class JoinStartGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;

	volatile boolean newTextMessage = false;
	volatile boolean newImageMessage = false;
	private String username;
	private String password;
	private String email;
	private Color color;
	@SuppressWarnings("unused")
	private Server server = null;

	/**
	 * Create the frame for joining and starting a game
	 */
	public JoinStartGUI(String username_, String password_, String email_, Color color_) {
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

		JButton btnStart = new JButton("Start A game");
		btnStart.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {

				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							server = new Server(1222);
							@SuppressWarnings("unused")
							Client client = new Client("localhost", username, password, email, color, 1222, true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				dispose();
			}
		});

		textField.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent e) {
				// Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (textField.getText().equals("")) {
						// create a new game
						EventQueue.invokeLater(new Runnable() {
							public void run() {
								try {
									server = new Server(1222);
									@SuppressWarnings("unused")
									Client client = new Client("localhost", username, password, email, color, 1222,
											true);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
						dispose();
					} else if (!textField.getText().isEmpty()) {
						// join a game
						EventQueue.invokeLater(new Runnable() {
							public void run() {
								try {
									@SuppressWarnings("unused")
									Client client = new Client(textField.getText(), username, password, email, color,
											1222, false);
								} catch (Exception e) {
									System.out.println("unable to create client: " + e.getMessage());
								}
							}
						});
						dispose();
					}
				}
			}
		});
		btnStart.setBounds(110, 100, 200, 23);
		contentPane.add(btnStart);

		// when btnsend pressed, send the message
		btnStart.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//Set format of message and do some action to send it.
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
							@SuppressWarnings("unused")
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

		// when btnsend pressed, send the message
		btnJoin.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//Set format of message and do some action to send it.
				newTextMessage = true;

			}
		});
	}
}
