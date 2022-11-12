/*
 * Author: Ethan Rees
 * This ui holds the join scene ui
 */
package ui;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import _main.Boot;
import battle.tanks.*;
import network.Client;
import network.Message;
import network.NetworkListener;
import network.Server;
import scenes.JoinScene;
import scenes.SceneManager;

public class JoinSceneUI extends JPanel {
	
	JPanel joinListPanel, tankSelectorPanel;
	JoinScene scene;
	DefaultListModel<String> playerListModel;
	JList<String> list;
	JScrollPane scroll;
	
	JButton beginGameButton;

	DropdownField mapChooser;
	ArrayList<TankTypePreviewUI> types;
	
	JLabel playerListTitle;
	
	JButton exitButton;
	
	boolean server;
	
	int selectedTankType;
	
	String[] tankNames = {
		"generic",
		"sturdy",
		"scout",
		"magic",
		"bomb"
	};
	String[] tankDescriptions = {
		"Pretty generic and not unqiue in any way, shape, or form!",
		"Slow but steady, lots of health but little damage and speed",
		"Very quick and strong, slightly invisible, but low health. Their bullets can't go very far. Sneak attacks are best here.",
		"Extremely low health, but shoots magic bullets. These bullets travel far and will aim towards other nearby tanks.",
		"Relatively sttrong health, but can only fire a bomb once every 10 seconds. The bombs, however, are extremely fatal."
	
	};
	Tank[] tankInstances = {
		new GenericTank("---", "", false, null),
		new SturdyTank("---", "", false, null),
		new ScoutTank("---", "", false, null),
		new MagicTank("---", "", false, null),
		new BombTank("---", "", false, null)
	};

	public JoinSceneUI(boolean client, boolean server, JoinScene scene, String mapName) {
		this.scene = scene;
		this.server = server;
		this.types = new ArrayList<TankTypePreviewUI>();
		this.selectedTankType = 0;
		
		setLayout(null);
		createJoinListPanel(mapName, server && !client ? 120 : 0);
		
		if(client) {
			createTankOptionsPanel();
		}
		
		// if we are the server, allow the user to change the map if they would like
		if(server) {
			// get all of the maps from the directory
			File directory = new File("./maps");
			File[] files = directory.listFiles();
			
			// put the names into the dropdown
			String[] names = new String[files.length];
			for(int i = 0; i < files.length; i++) {
				names[i] = files[i].getName().replace(".txt", "");
			}
			
			mapChooser = new DropdownField("Edit Your Map:", mapName, names, 100, 70, 20);
			mapChooser.setBounds(client ? 50 : 170, 30, 300, 100);
			mapChooser.addChangeListener(l -> {
				Server.server.sendMessage("map " + mapChooser.getValue());
				setMap(mapChooser.getValue());
				scene.mapName = mapChooser.getValue();
			});
			joinListPanel.add(mapChooser);
		}
		
		exitButton = new JButton("< Exit");
		exitButton.setBounds(10, 10, 70, 20);
		exitButton.addActionListener(l -> scene.returnToTitle());
		if(server && !client)
			add(exitButton);
		else
			joinListPanel.add(exitButton);
		
		update();
        
	}
	
	/*
	 * This will set the new map on the UI
	 */
	public void setMap(String mapName) {
		if(this.mapChooser != null)
			this.mapChooser.setValue(mapName);
		playerListTitle.setText("Connected Players: (playing map \"" + mapName + "\")");
	}
	
	/*
	 * This will creat the server join list panel
	 */
	void createJoinListPanel(String mapName, int xOffset) {
		// create the panel
		joinListPanel = new JPanel();
		joinListPanel.setLayout(null);
		int width = (int)(Boot.windowSize.width*0.5);
		joinListPanel.setBounds(xOffset, 0, width + xOffset, Boot.windowSize.height);
		
		// add the player list
		playerListTitle = new JLabel("Connected Players: (playing map \"" + mapName + "\")");
		playerListTitle.setBounds(50 + xOffset, 80, width, 20);
        joinListPanel.add(playerListTitle);
        
		playerListModel = new DefaultListModel<String>();
		list = new JList<String>(playerListModel);
        
        
        // add the scroll functionality
        int scrollWidth = 275;
        scroll = new JScrollPane(list);
        scroll.setWheelScrollingEnabled(true);
        scroll.setBounds(50 + xOffset, 100, 50 + scrollWidth, Boot.windowSize.height - 300);
        joinListPanel.add(scroll);
        
        // add the server only info
        if(Server.server != null) {
            JLabel serverIP = new JLabel("Join using the Server IP: " + Boot.getIPAddress());
            serverIP.setBounds(50 + xOffset + xOffset, Boot.windowSize.height - 200, width + xOffset, 20);
            add(serverIP);

            JLabel serverPort = new JLabel("Port #: " + Server.server.getPort());
            serverPort.setBounds(50 + xOffset + xOffset, Boot.windowSize.height - 185, width + xOffset, 20);
            add(serverPort);
        }
        
        // add the begin game button
        int buttonWidth = 200;
        beginGameButton = new JButton(server ? "Begin Game" : "Waiting for server to begin...");
        beginGameButton.setBounds((int)(width*0.5 - buttonWidth * 0.5) + xOffset, Boot.windowSize.height - 150, buttonWidth, 60);
        beginGameButton.setEnabled(server && playerListModel.size() > 0);
        beginGameButton.addActionListener(l -> {
        	scene.beginGame();
        });
        joinListPanel.add(beginGameButton);
		
		add(joinListPanel);
	}
	
	/*
	 * Create the tank choser panel
	 */
	void createTankOptionsPanel() {
		// setup the panel
		int width = (int)(Boot.windowSize.width*0.5);
		tankSelectorPanel = new JPanel();
		tankSelectorPanel.setLayout(null);
		tankSelectorPanel.setBounds(width, 0, width, Boot.windowSize.height);

		// add the label
		int xOffset = (int)(width * 0.115); 
		JLabel label = new JLabel("Choose your Tank:");
		label.setBounds(xOffset, 10, width, 32);
		tankSelectorPanel.add(label);
		
		// draw the different tank types out
		for(int i = 0; i < tankNames.length; i++) {
			TankTypePreviewUI ui = new TankTypePreviewUI(tankNames[i].toUpperCase().charAt(0) + tankNames[i].substring(1) + " Tank", tankDescriptions[i], tankInstances[i], xOffset, i * 100 + 40, (int)(width * 0.75), 90);
			
			types.add(ui);
			final int index = i;
			
			// handle mouse events
			ui.addMouseListener(new MouseListener() {
				@Override
				public void mousePressed(MouseEvent e) {
					selectedTankType = index;
					scene.myPlayerTank = types.get(index).getType();
					update();
					Client.client.sendMessage("sTankType " + scene.myPlayerTank);
					updatePlayerName(scene.playerIDs.indexOf(scene.myPlayerID), scene.myPlayerTank, true);
				}
				@Override public void mouseExited(MouseEvent e) { }
				@Override public void mouseEntered(MouseEvent e) { }
				@Override public void mouseClicked(MouseEvent e) { }
				@Override public void mouseReleased(MouseEvent e) { }
			});
			tankSelectorPanel.add(ui);
		}
		
		add(tankSelectorPanel);
	}
	
	/*
	 * This will update the UI
	 */
	public void update() {
		for(int i = 0; i < types.size(); i++)
			types.get(i).setSelected(i == selectedTankType);

		beginGameButton.setEnabled(server && playerListModel.size() > 0);
		list.repaint();
	}
	
	/*
	 * Remove all player names from the list
	 */
	public void clearPlayerNames() {
		playerListModel.removeAllElements();
	}

	/*
	 * Add player name to the list
	 */
	public void addPlayerName(String name) {
		playerListModel.addElement(name.replace("_", " ") + "   (the " + Boot.defaultTankType + " tank)");
		update();
	}
	
	/*
	 * Update player name to the list
	 */
	public void updatePlayerName(int changeIndex, String type, boolean updateSelection) {
		playerListModel.setElementAt(scene.playerNames.get(changeIndex).replace("_", " ") + "   (the " + type + " tank)", changeIndex);
		if(updateSelection) {
			for(int i = 0; i < tankNames.length; i++) {
				if(tankNames[i].equals(type))
					selectedTankType = i;
			}
		}
		
		update();
	}

	/*
	 * remove a player from the list
	 */
	public void removePlayer(int index) {
		playerListModel.removeElementAt(index);
		update();
	}
}
