package _main;

import battle.map.ColliderRect;
import scenes.SceneManager;
import scenes.TitleScene;

public class Boot {
	public static void main(String[] args) {
		
		SceneManager.setScene(new TitleScene());
	}
}
