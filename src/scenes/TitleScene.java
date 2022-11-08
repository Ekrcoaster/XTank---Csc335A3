/*
 * Author: Ethan Rees
 * This scene is responsible for the title, it lets you join a game, setup a server, or both!
 * It also presents the user with all of the fields to make a game.
 * See TitleScreenUI.java
 */
package scenes;

import network.Client;
import network.Server;
import ui.TitleSceneUI;
import ui.WindowHolder;

public class TitleScene extends Scene {
	
	public TitleSceneUI ui;

	@Override
	public void init() {
		// create the UI
		ui = (TitleSceneUI) WindowHolder.setPanel(new TitleSceneUI(this));
	}

	@Override
	public void exit() {}
	
	public void beginGame(boolean client, boolean server, String address, int port, String name, String mapName) {
		// if the player wants a client, create the client
		if(client) {
			try {
				// sometimes 'server' will also be true, this means they wanted to host their own game. This
				// is passed into the client so the client knows to create the server if needed
				// notice their name has spaces replaced with underscores, this packs it as 1 word
				// so sending the name through messages wont get confusing
				Client c = new Client(server, address, port, name.replace(" ", "_"));
				
				// finally, swap the scenes to the join scene
				SceneManager.setScene(new JoinScene(client, server, name, mapName));
				
			} catch (Exception e) {
				ui.pushError(e);
				e.printStackTrace();
			}

		// if just the server was requested, create the server and switch the scenes
		} else if(server) {
			try {
				new Server(port);
				SceneManager.setScene(new JoinScene(client, server, null, mapName));
				
			} catch (Exception e) {
				ui.pushError(e);
				e.printStackTrace();
			}
		}
	}
}
