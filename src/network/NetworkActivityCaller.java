/*
 * This class will allow the child class to attach to listeners and call them if a message is passed
 * yay object oriented programming
 */
package network;

import java.util.HashSet;

public abstract class NetworkActivityCaller {
public HashSet<NetworkListener> listeners = new HashSet<NetworkListener>();
	
	public void addListener(NetworkListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(NetworkListener listener) {
		listeners.remove(listener);
	}
	
	public void callListenersOnMessage(Message message) {
		for(NetworkListener listener : listeners) {
			if(listener != null)
				listener.onMessage(message);
		}
	}
	
	public void callListenersOnSentMessage(Message message) {
		for(NetworkListener listener : listeners) {
			if(listener != null)
				listener.onSentMessage(message);
		}
	}
}
