/*
 * Author: Ethan Rees
 * This is the main running class, it holds settings basic methods, and will create the ui and game
 */
package _main;

import java.awt.Dimension;
import java.net.InetAddress;
import java.net.UnknownHostException;

import battle.map.ColliderRect;
import network.Client;
import network.Server;
import scenes.SceneManager;
import scenes.TitleScene;

public class Boot {

	public static void main(String[] args) {
		SceneManager.setScene(new TitleScene());
	}
	
	// - - - - - - - - - - - - - - -
	// settings and helpful methods
	// - - - - - - - - - - - - - - -
	
	public static final String defaultServerAdress = "localhost";
	public static final int defaultPort = 58901;
	public static final boolean createDebugConsoles = false;
	public static final boolean clientCreatesServer = true;
	public static final String defaultTankType = "generic";
	public static final String defaultMapType = "maze";
	public static final Dimension windowSize = new Dimension(900, 600);
	public static final int BATTLE_FPS = 30;
	
	/*
	 * Get the IP address of the local user
	 */
	public static String getIPAddress() {
		InetAddress ip;
		try {
			ip = InetAddress.getLocalHost();
			return ip.toString().split("/")[1];
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return "localhost";
		}
	}
	/*
	 * Get the name of the local user through the network somehow
	 */
	public static String getName() {
		InetAddress ip;
		try {
			ip = InetAddress.getLocalHost();
			return ip.toString().split("/")[0];
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return "You!";
		}
	}
	
	public static void closeAllNetworks() {
		if(Server.server != null) {
			Server.server.close();
		}
		if(Client.client != null) {
			Client.client.close();
		}
	}
	
}
