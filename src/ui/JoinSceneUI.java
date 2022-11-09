/*
 * Author: Ethan Rees
 * This ui holds the join scene ui
 */
package ui;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
	JScrollPane scroll;
	
	JButton beginGameButton;
	
	ArrayList<TankTypePreviewUI> types;
	
	boolean server;
	
	int selectedTankType;
	
	String[] tankNames = {
		"Generic Tank",
		"Sturdy Tank",
		"Scout Tank",
		"Magic Tank",
		"Bomb Tank"
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
		if(client)
			createTankOptionsPanel();
		
		update();
        
	}
	
	void createJoinListPanel(String mapName, int xOffset) {
		joinListPanel = new JPanel();
		joinListPanel.setLayout(null);
		int width = (int)(Boot.windowSize.width*0.5);
		joinListPanel.setBounds(xOffset, 0, width + xOffset, Boot.windowSize.height);
		
		JLabel playerLabel = new JLabel("Connected Players: (playing map \"" + mapName + "\")");
        playerLabel.setBounds(50 + xOffset, 80, width, 20);
        joinListPanel.add(playerLabel);
        
		playerListModel = new DefaultListModel<String>();
        JList log = new JList<String>(playerListModel);
        
        int scrollWidth = 275;
        scroll = new JScrollPane(log);
        scroll.setWheelScrollingEnabled(true);
        scroll.setBounds(50 + xOffset, 100, 50 + scrollWidth, Boot.windowSize.height - 300);
        joinListPanel.add(scroll);
        
        if(Server.server != null) {
            JLabel serverIP = new JLabel("Join using the Server IP: " + Boot.getIPAddress());
            serverIP.setBounds(50 + xOffset + xOffset, Boot.windowSize.height - 200, width + xOffset, 20);
            add(serverIP);

            JLabel serverPort = new JLabel("Port #: " + Server.server.getPort());
            serverPort.setBounds(50 + xOffset + xOffset, Boot.windowSize.height - 185, width + xOffset, 20);
            add(serverPort);
        }
        
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
	
	void createTankOptionsPanel() {
		int width = (int)(Boot.windowSize.width*0.5);
		tankSelectorPanel = new JPanel();
		tankSelectorPanel.setLayout(null);
		tankSelectorPanel.setBounds(width, 0, width, Boot.windowSize.height);

		int xOffset = (int)(width * 0.115); 
		JLabel label = new JLabel("Choose your Tank:");
		label.setBounds(xOffset, 40, width, 32);
		tankSelectorPanel.add(label);
		
		
		
		for(int i = 0; i < tankNames.length; i++) {
			TankTypePreviewUI ui = new TankTypePreviewUI(tankNames[i], tankDescriptions[i], tankInstances[i], xOffset, i * 120 + 80, (int)(width * 0.75), 100);
			types.add(ui);
			final int index = i;
			ui.addMouseListener(new MouseListener() {
				@Override
				public void mousePressed(MouseEvent e) {
					selectedTankType = index;
					scene.myPlayerTank = types.get(index).getType();
					update();
					Client.client.sendMessage("sTankType " + scene.myPlayerTank);
					updatePlayerName(scene.playerIDs.indexOf(scene.myPlayerID), scene.myPlayerTank);
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
	
	public void update() {
		for(int i = 0; i < types.size(); i++)
			types.get(i).setSelected(i == selectedTankType);
	}

	public void addPlayerName(String name) {
		playerListModel.addElement(name.replace("_", " ") + "   (the " + Boot.defaultTankType + " tank)");
		beginGameButton.setEnabled(server && playerListModel.size() > 0);
	}
	
	public void updatePlayerName(int changeIndex, String type) {
		playerListModel.setElementAt(scene.playerNames.get(changeIndex).replace("_", " ") + "   (the " + type + " tank)", changeIndex);
	}
}
