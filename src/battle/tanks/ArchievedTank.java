/*
 * Author: Ethan Rees
 * This tank represents a past tank from the game, it holds simple information and is used to sort things
 * on the results screen
 */
package battle.tanks;

public class ArchievedTank implements Comparable<ArchievedTank> {

	String id, name, type;
	double damageDealt;
	boolean isDead;
	
	public ArchievedTank(String id, String name, double damageDealt, String type) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.damageDealt = Math.round(damageDealt*100)/(double)100;
	}
	
	@Override
	public String toString() {
		return name.replace("_", " ") + ": " + damageDealt + " points!";
	}

	// compare tanks by damage dealt
	@Override
	public int compareTo(ArchievedTank o) {
		return Integer.compare((int)(o.damageDealt*10), (int)(damageDealt*10));
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public double getDamageDealt() {
		return damageDealt;
	}

	public boolean isDead() {
		return isDead;
	}
	
}
