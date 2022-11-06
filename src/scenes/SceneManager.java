/*
 * Author: Ethan Rees
 * This easily lets different scenes interact with the main jframe
 */
package scenes;

import main.Client;
import main._Settings;

public class SceneManager {
	public static Scene activeScene;
	
	public static void setScene(Scene scene) {
		if(activeScene != null) activeScene.exit();
		activeScene = scene;
		
		scene.init();
	}
	
	public static void main(String[] args) throws Exception {
		//new Client(true, _Settings.defaultServerAdress, _Settings.defaultPort, "me");
		setScene(new TitleScene());
	}
}