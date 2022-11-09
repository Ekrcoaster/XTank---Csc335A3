/*
 * Author: Ethan Rees
 * This class contains the title screen UI and handles all of the jazz
 */
package ui;

import java.awt.Color;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import _main.Boot;
import scenes.TitleScene;

public class TitleSceneUI extends JPanel {
	
	public TitleScene title;
	JPanel buttonsPanel, launchSettingsPanel, playSettingsPanel;
	JButton[] launchModeButtons;
	
	IntegerField port;
	StringField ipAddress, name;
	DropdownField mapChooser;
	
	JButton playButton;
	
	JLabel error;
	
	int selectedPlayMode = -1;
	
	public TitleSceneUI(TitleScene title) {
		this.title = title;
		setLayout(null);
		
		int verticalOffset = 170;

		// add the top buttons panel
		add(createButtonsPanel(verticalOffset));
		
		// add the settings panel
		add(createLaunchSettingsPanel(verticalOffset));
		
		// add the play settings panel
		add(createMapSettingsPanel(verticalOffset));
		
		// add the play button
		int playWidth = 200;
		playButton = new JButton("Play");
		playButton.setBounds((int)(Boot.windowSize.getWidth() * 0.5 - playWidth * 0.5), 150 + verticalOffset, playWidth, 50);
		// when pressed, tell the scene to begin the servers
		playButton.addActionListener(l -> {
			title.beginGame(selectedPlayMode == 0 || selectedPlayMode == 2, selectedPlayMode == 1 || selectedPlayMode == 2, ipAddress.getValue(), port.getValue(), name.getValue(), mapChooser.getValue());
		});
		add(playButton);
		
		// add in the error label at the top, just incase its needed
		error = new JLabel("");
		error.setForeground(Color.red);
		error.setBounds(20, 0, (int)Boot.windowSize.getWidth(), 50);
		add(error);
		
		setSelected(-1);
	}
	
	/*
	 * This method will create the 3 panels used to choose a game mode
	 */
	public JPanel createButtonsPanel(int offset) {
		buttonsPanel = new JPanel();
		buttonsPanel.setBounds(0, offset, (int)Boot.windowSize.getWidth(), 36);
		
		// define the buttons
		String[] buttonOptions = {
			"Join Game (Client Only)",
			"Create Game (Server Only)",
			"Create And Join Game (Server + Client)"
		};
		launchModeButtons = new JButton[buttonOptions.length];
		
		// create them
		for(int i = 0; i < buttonOptions.length; i++) {
			launchModeButtons[i] = new JButton(buttonOptions[i]);
			final int j = i;
			launchModeButtons[i].addActionListener(l -> {
				setSelected(j);
			});
			buttonsPanel.add(launchModeButtons[i]);
		}
		return buttonsPanel;
	}
	
	/*
	 * This method will create the settings panel
	 */
	public JPanel createLaunchSettingsPanel(int offset) {
		launchSettingsPanel = new JPanel();
		launchSettingsPanel.setBounds(100, offset + 40, (int)Boot.windowSize.getWidth()-200, 40);

		// ip address field
		ipAddress = new StringField("IP Address:", Boot.getIPAddress(), 100, 70, 20);
		launchSettingsPanel.add(ipAddress);
		ipAddress.addChangeListener(createUIUpdateDocumentListener());
		
		// port field
		port = new IntegerField("Port:", Boot.defaultPort, 0, 99999, 100, 70, 20);
		launchSettingsPanel.add(port);
		port.addChangeListener(l -> update());

		// name field
		name = new StringField("Your Name:", Boot.getName(), 100, 70, 20);
		launchSettingsPanel.add(name);
		name.addChangeListener(createUIUpdateDocumentListener());
		
		return launchSettingsPanel;
	}
	
	public JPanel createMapSettingsPanel(int offset) {
		playSettingsPanel = new JPanel();
		playSettingsPanel.setBounds(100, offset+75, (int)Boot.windowSize.getWidth()-200, 40);
		
		// get all of the maps from the directory
		File directory = new File("./maps");
		File[] files = directory.listFiles();
		
		// put the names into the dropdown
		String[] names = new String[files.length];
		for(int i = 0; i < files.length; i++) {
			names[i] = files[i].getName().replace(".txt", "");
		}
		
		mapChooser = new DropdownField("Choose your Map:", Boot.defaultMapType, names, 100, 70, 20);
		playSettingsPanel.add(mapChooser);
		
		return playSettingsPanel;
	}
	
	/*
	 * This simply creates a new document listener for the string fields, it takes up way too much space
	 * so im defining it down here
	 */
	private DocumentListener createUIUpdateDocumentListener() {
		return new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) { update(); }
			
			@Override
			public void insertUpdate(DocumentEvent e) { update(); }
			
			@Override
			public void changedUpdate(DocumentEvent e) { update(); }
		};
	}
	
	/*
	 * This will change which button is selected and update UI accordingly
	 */
	public void setSelected(int index) {
		for(int i = 0; i < launchModeButtons.length; i++) {
			launchModeButtons[i].setBackground(i == index ? new Color(25, 200, 200) : Color.white);
		}
		
		selectedPlayMode = index;
		update();
	}
	
	/*
	 * This will update the UI based on what is selected
	 */
	public void update() {
		port.setEnabled(selectedPlayMode != -1);
		ipAddress.setEnabled(selectedPlayMode == 0);
		name.setEnabled(selectedPlayMode != 1 && selectedPlayMode != -1);
		
		mapChooser.setEnabled(selectedPlayMode > 0);
		
		String[] buttonNames = {
			"Select a play mode", // -1
			"Join Game",
			"Create Game",
			"Create and Join Game"
		};
		
		playButton.setText(buttonNames[selectedPlayMode+1]);
		playButton.setEnabled(true);
		if(selectedPlayMode == -1 || ipAddress.getValue().isEmpty() || name.getValue().isEmpty())
			playButton.setEnabled(false);
	}

	/*
	 * This will push an exception on screen
	 */
	public void pushError(Exception e) {
		error.setText(e.toString());
	}
}

