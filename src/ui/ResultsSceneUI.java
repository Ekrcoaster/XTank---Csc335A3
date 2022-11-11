/*
 * Author: Ethan Rees
 * This will draw the results screen
 */
package ui;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;

import _main.Boot;
import battle.tanks.ArchievedTank;
import network.Server;
import scenes.ResultsScene;

public class ResultsSceneUI extends JPanel {
	public ResultsScene scene;
	
	DefaultListModel<String> playerListModel;
	JList<String> log;
	
	JPanel buttonsPanel;
	JButton playAgainButton, toTitleButton, exitButton;
	
	public ResultsSceneUI(ResultsScene scene) {
		this.scene = scene;
		setLayout(null);
		
		// create the list
		int logWidth = 170;
		this.playerListModel = new DefaultListModel<String>();
		playerListModel.addElement("loading data from server...");
        log = new JList<String>(playerListModel);
        log.setBounds((int)(Boot.windowSize.width*0.5 - logWidth), 30, logWidth*2, 400);
        log.setBorder(BorderFactory.createLineBorder(Color.gray, 2));
        add(log);
        
        // create the buttons
        buttonsPanel = new JPanel();
        buttonsPanel.setBounds((int)(Boot.windowSize.width*0.5 - logWidth*2), 470, logWidth*4, 50);
        buttonsPanel.setLayout(new GridLayout(1, 3, 15, 5));
        
        playAgainButton = new JButton("Play Again");
        playAgainButton.addActionListener(l -> scene.playAgain());
        toTitleButton = new JButton("To Title");
        toTitleButton.addActionListener(l -> scene.toTitle());
        exitButton = new JButton("Exit");
        exitButton.addActionListener(l -> scene.exitGame());
        
        buttonsPanel.add(playAgainButton);
        buttonsPanel.add(toTitleButton);
        buttonsPanel.add(exitButton);

        add(buttonsPanel);
	}
	
	/*
	 * Update the player list
	 */
	public void update() {
		playerListModel.removeAllElements();
		for(int i = 0; i < scene.archievedTanks.size(); i++) {
			playerListModel.addElement((i+1) + ") " + scene.archievedTanks.get(i).toString());
		}
		// only the server can hit play again
		playAgainButton.setEnabled(Server.server != null);
		log.repaint();
	}
	
	/*
	 * remove a player from the list
	 */
	public void removePlayer(int index) {
		playerListModel.removeElementAt(index);
		update();
	}
}
