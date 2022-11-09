/*
 * Author: Ethan Rees
 * This easily lets different scenes interact with the _main jframe
 */
package scenes;

import java.util.ArrayList;

import _main.Boot;
import network.Client;
import network.Message;

public class SceneManager {
	public static Scene activeScene;
	
	public static void setScene(Scene scene) {
		if(activeScene != null) activeScene.exit();
		activeScene = scene;
		
		scene.init();
	}
	
	public static void main(String[] args) throws Exception {
		new Client(true, Boot.defaultServerAdress, Boot.defaultPort, "me");
		ArrayList<String> tempPlayerIDS = new ArrayList<String>();
		ArrayList<String> tempPlayerNames = new ArrayList<String>();
		ArrayList<String> tempPlayerTypes = new ArrayList<String>();
		tempPlayerIDS.add("000");
		tempPlayerNames.add("Bob");
		tempPlayerTypes.add("magic");
		tempPlayerIDS.add("afcfs");
		tempPlayerNames.add("Taylor Swift");
		tempPlayerTypes.add("bomb");
		tempPlayerIDS.add("sdsdsd");
		tempPlayerNames.add("Adele");
		tempPlayerTypes.add("scout");
		BattleScene scene = new BattleScene(Client.client.id, "you!", "bomb", tempPlayerIDS, tempPlayerNames, tempPlayerTypes, true, "map1");
		setScene(scene);
		// place the tanks at random spots
		scene.onMessage(new Message("sPos 500 500", "000"));
		scene.onMessage(new Message("sPos 300 400", "afcfs"));
		scene.onMessage(new Message("sPos 100 200", "sdsdsd"));
	}
}