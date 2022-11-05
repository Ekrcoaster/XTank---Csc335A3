/*
 * Author: Ethan Rees
 * This easily lets different scenes interact with the main jframe
 */
package scenes;

public class SceneManager {
	public static Scene activeScene;
	
	public static void setScene(Scene scene) {
		if(activeScene != null) activeScene.exit();
		activeScene = scene;
		
		scene.init();
	}
	
	public static void main(String[] args) {
		setScene(new BattleScene());
	}
}
