/*
 * Author: Ethan Rees
 * This class acts as the scenes game controller. It updates the tanks, creates the thread,
 * and sets up the UI.
 */
package scenes;

import java.util.ArrayList;

import battle.tanks.GenericTank;
import battle.tanks.Tank;
import ui.BattleBoardUI;
import ui.WindowHolder;

public class BattleScene extends Scene {
	
	public ArrayList<Tank> tankPlayers;
	public static final int FPS = 30;
	public BattleBoardUI ui;
	
	public boolean exit;
	Thread gameTickThread;

	@Override
	public void init() {
		exit = false;
		
		tankPlayers = new ArrayList<Tank>();
		tankPlayers.add(new GenericTank("a"));
		tankPlayers.get(0).setX(150);
		tankPlayers.get(0).setY(150);
		tankPlayers.get(0).setDirection(0);
		
		// create the scenes board UI
		ui = new BattleBoardUI();
		WindowHolder.setPanel(ui);
		
		render();
		
		// create the main scenes tick thread, this will call update/render based on the fps
		gameTickThread = new Thread(() -> {
			// get the last time, the fps tick ratio, and the delta
			long lastTime = System.nanoTime();
			double ratio = 1000000000.0 / (double)FPS;
			double delta = 0;
			
			// run as fast as humanly possible
			while(!exit) {
				
				// calcualte the delta (how much time has passed between every tick)
				long currentTime = System.nanoTime();
				delta += (currentTime - lastTime) / ratio;
				lastTime = currentTime;
				
				// if more than a delta tick has passed, then actually update the screen
				// this COULD be an if statement, but using a while lets the scenes catchup incase it lags
				while(delta >= 1) {
					update();
					render();
					delta--;
				}
			}
		});
		gameTickThread.start();

	}

	@Override
	public void exit() {
		exit = true;
	}
	
	public void update() {
		for(Tank tank : tankPlayers) {
			tank.updateControls(ui.getKeysDown());
			tank.update();
		}
	}
	
	public void render() {
		ui.render(tankPlayers);
	}
}
