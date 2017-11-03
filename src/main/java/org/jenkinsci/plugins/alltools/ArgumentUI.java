package org.jenkinsci.plugins.alltools;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class ArgumentUI {

	private JFrame frame;
	private JTextField txtAwesomeLevel;
	private JTextField txtName;
	private JComboBox<String> comboBox;
	private List<String> argList = new ArrayList<String>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ArgumentUI window = new ArgumentUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ArgumentUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				System.out.println("window is closed now");
			}
		});
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		txtAwesomeLevel = new JTextField();
		txtAwesomeLevel.setText("Awesome Level");
		txtAwesomeLevel.setBounds(102, 128, 114, 19);
		frame.getContentPane().add(txtAwesomeLevel);
		txtAwesomeLevel.setColumns(10);

		txtName = new JTextField();
		txtName.setText("Name");
		txtName.setBounds(102, 105, 114, 19);
		frame.getContentPane().add(txtName);
		txtName.setColumns(10);

		comboBox = new JComboBox<String>();
		comboBox.setModel(new DefaultComboBoxModel(new String[] { "wf1", "wf2", "wf3" }));
		comboBox.setName("workflow");
		comboBox.setBounds(102, 159, 110, 24);
		frame.getContentPane().add(comboBox);

		JButton btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = txtName.getText();
				String awesomeLevel = txtAwesomeLevel.getText();
				String field = (String) comboBox.getSelectedItem();
				argList.add(name);
				argList.add(awesomeLevel);
				argList.add(field);
				JOptionPane.showMessageDialog(null, name + " is " + awesomeLevel + " awesome in " + field);
			}
		});
		btnSubmit.setBounds(99, 211, 117, 25);
		frame.getContentPane().add(btnSubmit);

		JLabel lblArgumentToParse = new JLabel("Argument to parse");
		lblArgumentToParse.setBounds(100, 66, 166, 15);
		frame.getContentPane().add(lblArgumentToParse);
	}

	/**
	 * @return the argList
	 */
	public List<String> getArgList() {
		return argList;
	}

	/**
	 * @return the frame
	 */
	public JFrame getFrame() {
		return frame;
	}
}
