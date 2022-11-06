/*
 * Author: Ethan Rees
 * This easily lets different scenes interact with the _main jframe
 */
package scenes;

import java.util.ArrayList;

import _main._Settings;
import network.Client;

public class SceneManager {
	public static Scene activeScene;
	
	public static void setScene(Scene scene) {
		if(activeScene != null) activeScene.exit();
		activeScene = scene;
		
		scene.init();
	}
	
	public static void main(String[] args) throws Exception {
		new Client(true, _Settings.defaultServerAdress, _Settings.defaultPort, "me");
		ArrayList<String> tempPlayerIDS = new ArrayList<String>();
		ArrayList<String> tempPlayerNames = new ArrayList<String>();
		tempPlayerIDS.add("000");
		tempPlayerNames.add("Bob");
		tempPlayerIDS.add("afcfs");
		tempPlayerNames.add("Taylor Swift");
		tempPlayerIDS.add("sdsdsd");
		tempPlayerNames.add("Adele");
		setScene(new BattleScene("you", "you!", tempPlayerIDS, tempPlayerNames));
	}
}