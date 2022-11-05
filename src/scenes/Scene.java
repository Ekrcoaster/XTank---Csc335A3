/*
 * Author: Ethan Rees
 * This is an abstract scene used to build ontop of. Usefor for difference scenes, such as a title screen,
 * setup, and the game screen.
 */
package scenes;

public abstract class Scene {
	public abstract void init();
	public abstract void exit();
}
