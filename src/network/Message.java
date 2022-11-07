/*
 * Author: Ethan Rees
 * This class holds a message and some info about it. It also holds some nice functions to help parse the args
 */
package network;

import java.util.ArrayList;

public class Message {
	public String label;
	public ArrayList<String> args;
	public String fromID;
	
	public Message(String message, String from) {
		
		// split the text into a label and some args. this will make it easier to search through
		this.args = new ArrayList<String>();
		String[] split = message.split(" ");
		for(int i = 0; i < split.length; i++) {
			if(i == 0) {
				label = split[i];
			} else {
				args.add(split[i]);
			}
		}
		this.fromID = from;
	}
	
	@Override
	public String toString() {
		return label + " " + joinedArgs() + " (from: " + fromID + ")";
	}
	
	/*
	 * A super quick way to check if a message is a certian type or not
	 */
	public boolean is(String label) {
		return this.label.equals(label);
	}
	
	/*
	 * This will return the arg at an index, or null if it doesn't exist!
	 */
	public String getArg(int index) {
		if(index >= args.size())
			return null;
		return args.get(index);
	}
	
	/*
	 * This will return all args joined
	 */
	public String joinedArgs() {
		return String.join(" ", args);
	}
	
	/*
	 * This will parse an arg to an integer
	 */
	public int intArg(int index) {
		String arg = getArg(index);
		return Integer.parseInt(arg);
	}

	/*
	 * This will parse an arg to a double
	 */
	public double doubleArg(int index) {
		String arg = getArg(index);
		return Double.parseDouble(arg);
	}
}
