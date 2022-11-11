package _main;

import java.awt.Dimension;
import java.net.InetAddress;
import java.net.UnknownHostException;

import battle.map.ColliderRect;
import scenes.SceneManager;
import scenes.TitleScene;

public class Boot {

	public static final String defaultServerAdress = "localhost";
	public static final int defaultPort = 58901;
	public static final boolean createDebugConsoles = false;
	public static final boolean clientCreatesServer = true;
	public static final String defaultTankType = "generic";
	public static final String defaultMapType = "empty";
	public static final Dimension windowSize = new Dimension(900, 600);
	public static final int BATTLE_FPS = 30;
	
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
	
	public static void main(String[] args) {
		SceneManager.setScene(new TitleScene());
	}
}
