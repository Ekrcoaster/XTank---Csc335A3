/*
 * This class will allow the child class to attach to listeners and call them if a message is passed
 * yay object oriented programming
 */
package network;

import java.util.ArrayList;

public abstract class NetworkActivityCaller {
	public static ArrayList<NetworkListener> listeners;
	
	// add a new listener to the list
	public void addListener(NetworkListener listener) {
		if(listeners == null)
			listeners = new ArrayList<NetworkListener>();
		if(!listeners.contains(listener))
			listeners.add(listener);
	}
	
	// remove the listener from the list
	public void removeListener(NetworkListener listener) {
		listeners.remove(listener);
	}
	
	// call the listeners on message
	public void callListenersOnMessage(Message message) {
		if(listeners == null) return;

		for(int i = 0; i < listeners.size(); i++) {
			if(listeners.get(i) != null)
				listeners.get(i).onMessage(message);
		}
	}
	
	// call the listeners on message send
	public void callListenersOnSentMessage(Message message) {
		if(listeners == null) return;
		
		for(int i = 0; i < listeners.size(); i++) {
			if(listeners.get(i) != null)
				listeners.get(i).onSentMessage(message);
		}
	}
}
