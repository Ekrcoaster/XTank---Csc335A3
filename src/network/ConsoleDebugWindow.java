/*
 * Author: Ethan Rees
 * This file just creates a simple console UI that can be used to debug
 * the network messages being sent back and forth
 */
package network;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.*;

public class ConsoleDebugWindow extends JFrame implements NetworkListener {
	
	DefaultListModel<String> logModel;
	JScrollPane scroll;
	
	JTextField textField;
	JLabel title;
	
	MessageNode myself;
	String id;
	
	public ConsoleDebugWindow(MessageNode myself, NetworkActivityCaller listener) {
		this.myself = myself;
		this.id = myself.getID();
		
		listener.addListener(this);
		
		Dimension size = new Dimension(300, 500);
		
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize((int)size.getWidth(), (int)size.getHeight());
        setPreferredSize(size);
        setVisible(true);
        setResizable(true);
        
        setLayout(new BorderLayout());

        title = new JLabel("--");
        setID(id);
        add(title, BorderLayout.NORTH);
        
        logModel = new DefaultListModel<String>();
        JList log = new JList<String>(logModel);
        
        scroll = new JScrollPane(log);
        scroll.setWheelScrollingEnabled(true);
        
        add(scroll, BorderLayout.CENTER);
        
        Dimension bottomBarSize = new Dimension((int)size.getWidth(), 32);
        JPanel bottomBar = new JPanel();
        bottomBar.setPreferredSize(bottomBarSize);
        bottomBar.setLayout(null);
        

        textField = new JTextField();
        textField.setBounds(0, 0,(int)( bottomBarSize.getWidth() * 0.8), (int)bottomBarSize.getHeight());
        bottomBar.add(textField);
        
        JButton sendButton = new JButton(">>");
        sendButton.setBounds((int)( bottomBarSize.getWidth() * 0.8), 0, (int)( bottomBarSize.getWidth() * 0.2), (int)bottomBarSize.getHeight());
        
        sendButton.addActionListener(l -> {
        	sendMessage(textField.getText());
        	textField.setText("");
        });
        
        bottomBar.add(sendButton);
        add(bottomBar, BorderLayout.SOUTH);

        pack();
	}
	
	public void setID(String id) {
		this.id = id;
        setTitle("Console: " + id);
        title.setText(getTitle());
	}
	
	public void exit() {
		dispose();
	}
	
	public void sendMessage(String message) {
		myself.sendMessage(message);
	}

	@Override
	public void onMessage(Message message) {
		// the command id will set arg 0 to the title of this window
		if(message.is("id"))
			setID(message.getArg(0));
		
		if(message.fromID == null) {	
			logModel.addElement("Server: " + message);
		} else {
			logModel.addElement(message.fromID + ": " + message);
		}
		scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum());
	}
	
	public void addSentIndicator(String message) {
		logModel.addElement("  -- you sent: " + message + " --");
	}

	@Override
	public void onSentMessage(Message message) {
		addSentIndicator(message.toString());
	}
}
