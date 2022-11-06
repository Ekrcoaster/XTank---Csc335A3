/*
 * Author: Ethan Rees
 * This interface is for classes who want to be notified when a message is sent or received
 */
package network;

public interface NetworkListener {
	public void onMessage(Message message);
	public void onSentMessage(Message message);
}
