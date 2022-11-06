/*
 * Author: Ethan Rees
 * This ui holds the join scene ui
 */
package ui;

import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import main.Server;
import main._Settings;
import network.Message;
import network.NetworkListener;
import scenes.JoinScene;
import scenes.SceneManager;

public class JoinSceneUI extends JPanel {
	
	JoinScene scene;
	DefaultListModel<String> playerListModel;
	JScrollPane scroll;
	
	JButton beginGameButton;
	
	public JoinSceneUI(boolean client, boolean server, JoinScene scene) {
		this.scene = scene;
		setLayout(null);

        JLabel playerLabel = new JLabel("Connected Players:");
        playerLabel.setBounds(300, 80, 150, 20);
        add(playerLabel);
        
		playerListModel = new DefaultListModel<String>();
        JList log = new JList<String>(playerListModel);
        
        scroll = new JScrollPane(log);
        scroll.setWheelScrollingEnabled(true);
        scroll.setBounds(300, 100, _Settings.windowSize.width - 600, _Settings.windowSize.height - 300);
        add(scroll);
        
        JLabel serverIP = new JLabel("Join using the Server IP: " + _Settings.getIPAddress());
        serverIP.setBounds(300, _Settings.windowSize.height - 200, 300, 20);
        add(serverIP);

        JLabel serverPort = new JLabel("Port #: " + Server.server.getPort());
        serverPort.setBounds(300, _Settings.windowSize.height - 185, 300, 20);
        add(serverPort);
        
        int buttonWidth = 200;
        beginGameButton = new JButton(server ? "Begin Game" : "Waiting for server to begin...");
        beginGameButton.setBounds((int)(_Settings.windowSize.width*0.5 - buttonWidth * 0.5), _Settings.windowSize.height - 150, buttonWidth, 60);
        beginGameButton.setEnabled(server);
        beginGameButton.addActionListener(l -> {
        	scene.beginGame();
        });
        add(beginGameButton);
	}

	public void addPlayerName(String name) {
		playerListModel.addElement(name.replace("_", " "));
	}
}
