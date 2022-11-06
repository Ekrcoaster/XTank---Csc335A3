/*
 * Author: Ethan Rees
 * This template file will hold settings
 */
package _main;

import java.awt.Dimension;
import java.net.InetAddress;
import java.net.UnknownHostException;

/*
 * This class just holds the default network info between the network and client
 * Idk if i like this, but it works for now
 */
public class _Settings {
	public static final String defaultServerAdress = "localhost";
	public static final int defaultPort = 58901;
	public static boolean createDebugConsoles = true;
	public static boolean clientCreatesServer = true;
	public static Dimension windowSize = new Dimension(900, 600);
	
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
}
