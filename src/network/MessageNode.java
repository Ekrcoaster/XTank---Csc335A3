/*
 * Author: Ethan Rees
 * This represents a client or a server, and some of their basic functions.
 * This is here so that the debug console can be coded easier, and just for oop
 */
package network;

/*
 * This interface is used to unify the classes that can send/receive messages.
 * This is done so the method names line up for consistency, and so that the debug console
 * I made can share the same code between the server debug console and the client debug console
 * yay object oriented programming
 */
public interface MessageNode {
	public void sendMessage(String message);
	public void messageReceived(Message message);
	public String getID();
}	
