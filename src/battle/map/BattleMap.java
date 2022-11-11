/*
 * Author: Ethan Rees
 * This class holds the map for the battle, it contains a bunch of colliders and will read the map file
 */
package battle.map;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import ui.Renderable;

public class BattleMap {
	
	HashSet<ColliderRect> colliders;
	ArrayList<Renderable> renderableColliders;
	
	String mapName;
	int mapWidth, mapHeight;
	
	public BattleMap(String mapName, int mapWidth, int mapHeight) {
		this.mapName = mapName;
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		colliders = new HashSet<ColliderRect>();
		renderableColliders = new ArrayList<Renderable>();
		
		// north boundry
		colliders.add(new ColliderRect(-1000, -1000, mapWidth + 1000, 0));
		
		// east boundry
		colliders.add(new ColliderRect(mapWidth, -1000, mapWidth + 1000, mapHeight + 1000));

		// south boundry
		colliders.add(new ColliderRect(-1000, mapHeight, mapWidth + 1000, mapHeight + 1000));

		// west boundry
		colliders.add(new ColliderRect(-1000, -1000, 0, mapHeight + 1000));
		
		readMap(mapName);
	}
	
	/*
	 * Read in the map from a file, it goes line by line and builds the colliders
	 */
	private void readMap(String mapName) {
		Scanner scanner;
		try {
			scanner = new Scanner(new File("./maps/" + mapName + ".txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			
			// check if it is not a comment
			if(!line.startsWith("#") && !line.isEmpty()) {
				// check if it has enough lines
				String[] split = line.replace(" |", "").split(" ");
				if(split.length != 8) {
					System.out.println("error! Line \"" + line + "\" " + (split.length < 8 ? "has too few" : "has too much") + " args! (" + split.length + ")");
				} else {
					readMapInterpretLine(split);
				}
			}
		}
		
		scanner.close();
	}
	
	/*
	 * This interprets 1 line from the map file and turns it into a collider
	 */
	private void readMapInterpretLine(String[] pieces) {
		double x = Double.parseDouble(pieces[1]);
		double y = Double.parseDouble(pieces[2]);
		double m1 = Double.parseDouble(pieces[3]); // could be x2 or width
		double m2 = Double.parseDouble(pieces[4]); // could be y2 or height

		int r = Integer.parseInt(pieces[5]);
		int g = Integer.parseInt(pieces[6]);
		int b = Integer.parseInt(pieces[7]);
		Color color = new Color(r, g, b);
		
		RenderColliderRect rect;
		if(pieces[0].equals("abs")) { // abs type
			rect = new RenderColliderRect(x, y, m1, m2, color);
		} else { // width + height type
			rect = new RenderColliderRect(x, y, x+m1, y+m2, color);
		}
		
		colliders.add(rect);
		renderableColliders.add(rect);
	}

	/*
	 * When given an x,y,width,height, calculate the collisons on that w the colliders
	 */
	public ColliderHitPoint calculateCollisions(double x, double y, double width, double height) {
		ColliderHitPoint point = new ColliderHitPoint(x, y);
		for(ColliderRect rect : colliders) {
			point = rect.snapToEdgePoint(point, width, height);
		}
		return point;
	}

	public String getMapName() {
		return mapName;
	}

	public int getMapWidth() {
		return mapWidth;
	}

	public int getMapHeight() {
		return mapHeight;
	}

	public HashSet<ColliderRect> getColliders() {
		return colliders;
	}
	
	public ArrayList<Renderable> getRenderables() {
		return renderableColliders;
	}
	
}
