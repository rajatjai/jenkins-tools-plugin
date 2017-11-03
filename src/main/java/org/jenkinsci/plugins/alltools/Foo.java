/**
 * 
 */
package org.jenkinsci.plugins.alltools;

import java.awt.event.WindowListener;

import javax.swing.JFrame;

/**
 * @author jain
 *
 */
public class Foo {

	/**
	 * @param args
	 */
	public void initUI() {
		final JFrame frame = new JFrame();
		frame.setTitle("ETC Arguments");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		ArgumentUI window = new ArgumentUI();
		window.getFrame().setVisible(true);
		WindowListener[] windowListeners = window.getFrame().getWindowListeners();
		for (WindowListener windowListener : windowListeners) {
			try {
				windowListener.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(window.getArgList());
		// String name = JOptionPane.showInputDialog("What's your name bro?");
		// String awesomeLevel = JOptionPane.showInputDialog("How awesome are you?");
		// System.out.println(name + " = " + awesomeLevel + " awesome!");
		// Foo foo = new Foo();
		// foo.initUI();
	}

}
