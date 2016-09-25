package portfolio1;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Login extends JFrame {

	/**
	 * serial
	 */
	private static final long serialVersionUID = 5385216568336112207L;
	private JPanel contentPane;
	private Color color;
	private String email;
	private String password;
	private String username;
	private JTextField emailField;
	private JTextField passwordField;
	private JTextField usernameField;
	
	
	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = 
		    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
	
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					if (Server.clientNum <= 5){
						Login frame = new Login();
						frame.setVisible(true);
					}
					else {
						System.out.println("too many clients connected to same game");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Login() {
		setTitle("Create Account");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 279, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		String[] options = new String[] {"Green", "Blue",
                "Red", "Orange"};
		JComboBox<?> comboBox = new JComboBox<Object>(options);
		comboBox.setBounds(80, 169, 135, 27);
		contentPane.add(comboBox);

		JLabel lblNewLabel = new JLabel("Create Your Profile");
		lblNewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblNewLabel.setBounds(44, 21, 195, 32);
		contentPane.add(lblNewLabel);

		JLabel lblUsername = new JLabel("Username");
		lblUsername.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblUsername.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblUsername.setBounds(10, 65, 72, 23);
		contentPane.add(lblUsername);

		usernameField = new JTextField();
		usernameField.setBounds(112, 68, 161, 20);
		contentPane.add(usernameField);
		usernameField.setColumns(10);

		JButton loginButton = new JButton("Create");
		loginButton.setBounds(96, 220, 89, 23);
		contentPane.add(loginButton);

		Component horizontalStrut = Box.createHorizontalStrut(20);
		horizontalStrut.setBounds(10, 32, 25, 12);
		contentPane.add(horizontalStrut);

		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		horizontalStrut_1.setBounds(248, 32, 25, 12);
		contentPane.add(horizontalStrut_1);
		
		JLabel lblEmail = new JLabel("Email");
		lblEmail.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblEmail.setAlignmentX(0.5f);
		lblEmail.setBounds(10, 134, 48, 23);
		contentPane.add(lblEmail);
		
		emailField = new JTextField();
		emailField.setColumns(10);
		emailField.setBounds(70, 136, 203, 20);
		contentPane.add(emailField);
		
		emailField.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {

				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					// check for empty strings here
					username = usernameField.getText().trim();
					password = passwordField.getText().trim();
					email = emailField.getText().trim();
					color = parseColor(options[comboBox.getSelectedIndex()]);
					if (username.equals("") || password.equals("") || email.equals("")){
						System.out.println("Please fill out all fields.");
						System.exit(-1);
						dispose();
					}
					if (!validate(email)){
						System.out.println("Please enter a valid email.");
						System.exit(-1);
						dispose();
					}
					else {
						EventQueue.invokeLater(new Runnable() {
							public void run() {
								try {
//									InetAddress i = InetAddress.getLocalHost();
//									System.out.println(i.getHostName());
//									System.out.println(i.getHostAddress());
//									System.out.println(Server.clientNum);
									
									//check for current client with same username/email
//									if (Server.clientsList.size() > 0) {
//										for (int j = 0; j < Server.clientsList.size(); j++){
//											if (Server.clientsList.get(j).getClientName() == username){
//												System.out.println("That name is taken sorry.");
//												dispose();
//											}
//											if (Server.clientsList.get(j).getClientEmail() == email){
//												System.out.println("Email already registered in current game.");
//												dispose();
//											}
//										}
//									}
//									//Client client = new Client("localhost", username, password, email, color, 1222);
//									//clientsList.add(new Client("localhost", username, password, email, color, 1222));
									JoinStartGUI js = new JoinStartGUI(username, password, email, color);
									js.setVisible(true);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
	
						// dispose of this login page
						dispose();
					}
				}
			}
		});
		
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblPassword.setAlignmentX(0.5f);
		lblPassword.setBounds(10, 100, 72, 23);
		contentPane.add(lblPassword);
		
		passwordField = new JTextField();
		passwordField.setColumns(10);
		passwordField.setBounds(112, 103, 161, 20);
		contentPane.add(passwordField);

		loginButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				username = usernameField.getText().trim();
				password = passwordField.getText().trim();
				email = emailField.getText().trim();
				color = parseColor(options[comboBox.getSelectedIndex()]);
				if (username.equals("") || password.equals("") || email.equals("")){
					System.out.println("Please fill out all fields.");
					System.exit(-1);
					dispose();
				}
				if (!validate(email)){
					System.out.println("Please enter a valid email.");
					System.exit(-1);
					dispose();
				}
				
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							//Client client = new Client("localhost", username, password, email, color, 1222);
							//clientsList.add(new Client("localhost", username, password, email, color, 1222));
							JoinStartGUI js = new JoinStartGUI(username, password, email, color);
							js.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

				// dispose of this login page
				dispose();

			}
		});
	}
	
	public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
	}
	
	public static Color parseColor(String colorName){
		return colorName.equalsIgnoreCase("blue") ? new Color(80, 140, 164) : 
			colorName.equalsIgnoreCase("green") ? new Color(78, 110, 93) :
			colorName.equalsIgnoreCase("orange") ? new Color(238, 150, 75) :
				new Color(192, 57, 63);
	}
}
