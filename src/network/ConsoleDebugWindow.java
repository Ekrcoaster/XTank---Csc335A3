package network;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.*;

public class ConsoleDebugWindow extends JFrame {
	
	DefaultListModel<String> logModel;
	JScrollPane scroll;
	
	JTextField textField;
	JLabel title;
	
	MessageNode myself;
	String id;
	
	public ConsoleDebugWindow(MessageNode myself) {
		this.myself = myself;
		this.id = myself.getID();
		
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
        	addMessage(null, textField.getText());
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
	
	public void addMessage(String from, String message) {
		if(from == null) {	
			logModel.addElement("-- sent: " + message + " --");
		} else {
			logModel.addElement(from + " > " + message);
		}
		scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum());
	}
	
	public void sendMessage(String message) {
		myself.sendMessage(message);
	}
}
