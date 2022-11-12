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
	
	/* Change the active scene to this*/
	public static void setScene(Scene scene) {
		if(activeScene != null) activeScene.exit();
		activeScene = scene;
		scene.init();
	}
	
	public static void main(String[] args) throws Exception {
		new Client(true, Boot.defaultServerAdress, Boot.defaultPort, "me");
		setScene(new BattleScene(Client.client.id, "you!", "magic", 
				new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), true, "eyeball"));
	}
}