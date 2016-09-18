package lab01;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComponent;

import java.awt.event.KeyAdapter;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.InputVerifier;

public class Login extends JFrame {

	private JPanel contentPane;
	private JTextField nameField;
	private String name;
	private boolean giveAdminProperties = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
					frame.setVisible(true);
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
		setTitle("Login Page");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 279, 271);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("Connect");
		lblNewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 26));
		lblNewLabel.setBounds(87, 21, 107, 32);
		contentPane.add(lblNewLabel);

		JLabel lblEnterYourName = new JLabel("Enter Your Name");
		lblEnterYourName.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblEnterYourName.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblEnterYourName.setBounds(10, 65, 117, 23);
		contentPane.add(lblEnterYourName);

		nameField = new JTextField();

		nameField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {

				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					// check for empty string here
					name = nameField.getText().trim();
					if (name.equals("")){
						System.out.println("Please enter a valid name.");
						System.exit(-1);
						dispose();
					}

					if (name.equalsIgnoreCase("Admin")) {
						giveAdminProperties = true;
					}

					EventQueue.invokeLater(new Runnable() {
						public void run() {
							try {
								Client client = new Client("localhost", name, 1222, giveAdminProperties);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

					// dispose of this login page
					dispose();
				}
			}
		});
		nameField.setBounds(123, 68, 150, 20);
		contentPane.add(nameField);
		nameField.setColumns(10);

		JButton loginButton = new JButton("Login");
		loginButton.setBounds(91, 111, 89, 23);
		contentPane.add(loginButton);

		Component horizontalStrut = Box.createHorizontalStrut(20);
		horizontalStrut.setBounds(10, 32, 56, 0);
		contentPane.add(horizontalStrut);

		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		horizontalStrut_1.setBounds(217, 32, 56, 12);
		contentPane.add(horizontalStrut_1);
		loginButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				name = nameField.getText().trim();

				if (name.equalsIgnoreCase("Admin")) {
					giveAdminProperties = true;
				}

				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							Client client = new Client("localhost", name, 1222, giveAdminProperties);
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
}
