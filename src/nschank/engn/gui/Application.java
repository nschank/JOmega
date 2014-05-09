package nschank.engn.gui;

import cs195n.Vec2i;
import nschank.asgn.cs195n.SwingFrontEnd;
import nschank.collect.dim.Dimensional;
import nschank.collect.dim.Vector;

import javax.swing.JFrame;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Nicolas Schank for package nschank.engn.gui
 * Created 9 Sep 2013
 * Last updated on 8 May 2014
 *
 * The Swing FrontEnd of the Omega Engine. Has a number of Screens that it holds on to, and passes off
 * events, ticks, and draws to those Screens as the Screens require.
 *
 * @author Nicolas Schank
 * @version 2.0
 */
public class Application extends SwingFrontEnd
{
	//TODO remove Vec2i's
	public static final Vec2i DEFAULT_SIZE = new Vec2i(960, 540);

	//A list of Screens. Screen 0 is on top, so new screens are generally added to position 0 (using addGameScreen).
	private List<Screen> screens;

	//The current size of the window.
	private Vector size;

	/**
	 * Creates an Application, a frontend for the Omega Engine.
	 *
	 * @param title
	 * 		- The window title of this Application.
	 * @param fullscreen
	 * 		- Whether or not to start this Application off as fullscreen
	 */
	public Application(String title, boolean fullscreen)
	{
		this(title, fullscreen, DEFAULT_SIZE, JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * Creates an Application, a frontend for the Omega Engine.
	 *
	 * @param title
	 * 		- The window title of this Application.
	 * @param fullscreen
	 * 		- Whether or not to start this Application off as fullscreen
	 * @param windowSize
	 * 		- A 2d integer vector representing the requested size of the screen.
	 */
	public Application(String title, boolean fullscreen, Vec2i windowSize)
	{
		this(title, fullscreen, windowSize, JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * Creates an Application, a frontend for the Omega Engine.
	 *
	 * @param title
	 * 		- The window title of this Application.
	 * @param fullscreen
	 * 		- Whether or not to start this Application off as fullscreen
	 * @param windowSize
	 * 		- A 2d integer vector representing the requested size of the screen.
	 * @param closeOp
	 * 		- The closing operation for this particular Application; from JFrame
	 */
	public Application(String title, boolean fullscreen, Vec2i windowSize, int closeOp)
	{
		super(title, fullscreen, windowSize, closeOp);
		this.screens = new ArrayList<Screen>();
		this.size = Vector.ZERO_2D;
	}

	/**
	 * Adds a new Screen on top of the Application.
	 *
	 * @param gameScreen
	 * 		- A new Screen to put above all other Screens on the Application.
	 */
	public void addGameScreen(Screen gameScreen)
	{
		this.screens.add(0, gameScreen);
	}

	/**
	 * Adds a new Screen to the current Application, at a particular index/z-score
	 *
	 * @param gameScreen
	 * 		The Screen to add
	 * @param index
	 * 		Where to put this Screen relative to others; 0 is the top
	 */
	public void addGameScreen(Screen gameScreen, int index)
	{
		if(index >= 0) this.screens.add(index, gameScreen);
		else this.screens.add(gameScreen);
	}

	public Dimensional centerPosition()
	{
		return this.size.sdiv(2);
	}

	/**
	 * @return The current size of this window.
	 */
	public Dimensional currentSize()
	{
		return new Vector(this.size);
	}

	@Override
	/**
	 * Draws the top screen, and any screens below it if the screen claims
	 *  to have transparency.
	 */
	protected void onDraw(Graphics2D g)
	{
		int start;
		boolean layerStillVisible = true;
		for(start = 0; (start < this.screens.size()) && layerStillVisible; start++)
		{
			layerStillVisible = this.screens.get(start).hasTransparency();
		}
		start--;

		for(int i = start; i >= 0; i--)
			this.screens.get(i).onDraw(g);
	}

	@Override
	/**
	 * Passes on key events to the top screen, propagating down
	 *  through screens that allow key events to pass through
	 */
	protected void onKeyPressed(KeyEvent e)
	{
		boolean noPauseScreenAbove = true;
		List<Screen> atStartScreens = new ArrayList<>(this.screens);
		for(int i = 0; (i < atStartScreens.size()) && noPauseScreenAbove; i++)
		{
			if(this.screens.contains(atStartScreens.get(i)))
			{
				noPauseScreenAbove = atStartScreens.get(i).passesKeyEventsBelow();
				atStartScreens.get(i).onKeyPressed(e);
			}
		}
	}

	@Override
	/**
	 * Passes on key events to the top screen, propagating down
	 *  through screens that allow key events to pass through
	 */
	protected void onKeyReleased(KeyEvent e)
	{
		boolean noPauseScreenAbove = true;
		List<Screen> atStartScreens = new ArrayList<>(this.screens);
		for(int i = 0; (i < atStartScreens.size()) && noPauseScreenAbove; i++)
		{
			if(this.screens.contains(atStartScreens.get(i)))
			{
				noPauseScreenAbove = atStartScreens.get(i).passesKeyEventsBelow();
				atStartScreens.get(i).onKeyReleased(e);
			}
		}
	}

	@Override
	/**
	 * Passes on key events to the top screen, propagating down
	 *  through screens that allow key events to pass through
	 */
	protected void onKeyTyped(KeyEvent e)
	{
		boolean noPauseScreenAbove = true;
		List<Screen> atStartScreens = new ArrayList<>(this.screens);
		for(int i = 0; (i < atStartScreens.size()) && noPauseScreenAbove; i++)
		{
			if(this.screens.contains(atStartScreens.get(i)))
			{
				noPauseScreenAbove = atStartScreens.get(i).passesKeyEventsBelow();
				atStartScreens.get(i).onKeyTyped(e);
			}
		}
	}

	@Override
	/**
	 * Passes on mouse events to the top screen, propagating down
	 *  through screens that allow key events to pass through
	 */
	protected void onMouseClicked(MouseEvent e)
	{
		boolean noPauseScreenAbove = true;
		List<Screen> atStartScreens = new ArrayList<>(this.screens);
		for(int i = 0; (i < atStartScreens.size()) && noPauseScreenAbove; i++)
		{
			if(this.screens.contains(atStartScreens.get(i)))
			{
				noPauseScreenAbove = atStartScreens.get(i).passesMouseEventsBelow();
				atStartScreens.get(i).onMouseClicked(e);
			}
		}
	}

	@Override
	/**
	 * Passes on mouse events to the top screen, propagating down
	 *  through screens that allow key events to pass through
	 */
	protected void onMouseDragged(MouseEvent e)
	{
		boolean noPauseScreenAbove = true;
		List<Screen> atStartScreens = new ArrayList<>(this.screens);
		for(int i = 0; (i < atStartScreens.size()) && noPauseScreenAbove; i++)
		{
			if(this.screens.contains(atStartScreens.get(i)))
			{
				noPauseScreenAbove = atStartScreens.get(i).passesMouseEventsBelow();
				atStartScreens.get(i).onMouseDragged(e);
			}
		}
	}

	@Override
	/**
	 * Passes on mouse events to the top screen, propagating down
	 *  through screens that allow key events to pass through
	 */
	protected void onMouseMoved(MouseEvent e)
	{
		boolean noPauseScreenAbove = true;
		List<Screen> atStartScreens = new ArrayList<>(this.screens);
		for(int i = 0; (i < atStartScreens.size()) && noPauseScreenAbove; i++)
		{
			if(this.screens.contains(atStartScreens.get(i)))
			{
				noPauseScreenAbove = atStartScreens.get(i).passesMouseEventsBelow();
				atStartScreens.get(i).onMouseMoved(e);
			}
		}
	}

	@Override
	/**
	 * Passes on mouse events to the top screen, propagating down
	 *  through screens that allow key events to pass through
	 */
	protected void onMousePressed(MouseEvent e)
	{
		boolean noPauseScreenAbove = true;
		List<Screen> atStartScreens = new ArrayList<>(this.screens);
		for(int i = 0; (i < atStartScreens.size()) && noPauseScreenAbove; i++)
		{
			if(this.screens.contains(atStartScreens.get(i)))
			{
				noPauseScreenAbove = atStartScreens.get(i).passesMouseEventsBelow();
				atStartScreens.get(i).onMousePressed(e);
			}
		}
	}

	@Override
	/**
	 * Passes on mouse events to the top screen, propagating down
	 *  through screens that allow key events to pass through
	 */
	protected void onMouseReleased(MouseEvent e)
	{
		boolean noPauseScreenAbove = true;
		List<Screen> atStartScreens = new ArrayList<>(this.screens);
		for(int i = 0; (i < atStartScreens.size()) && noPauseScreenAbove; i++)
		{
			if(this.screens.contains(atStartScreens.get(i)))
			{
				noPauseScreenAbove = atStartScreens.get(i).passesMouseEventsBelow();
				atStartScreens.get(i).onMouseReleased(e);
			}
		}
	}

	@Override
	/**
	 * Passes on mouse events to the top screen, propagating down
	 *  through screens that allow key events to pass through
	 */
	protected void onMouseWheelMoved(MouseWheelEvent e)
	{
		boolean noPauseScreenAbove = true;
		List<Screen> atStartScreens = new ArrayList<>(this.screens);
		for(int i = 0; (i < atStartScreens.size()) && noPauseScreenAbove; i++)
		{
			if(this.screens.contains(atStartScreens.get(i)))
			{
				noPauseScreenAbove = atStartScreens.get(i).passesMouseEventsBelow();
				atStartScreens.get(i).onMouseWheelMoved(e);
			}
		}
	}

	@Override
	/**
	 * Alerts all screens to the window resizing, passing along that new size.
	 */
	protected void onResize(Vec2i newSize)
	{
		this.size = new Vector(newSize.x, newSize.y);
		for(Screen screen : this.screens)
		{
			screen.onResize(newSize);
		}
	}

	@Override
	/**
	 * Allows the top screen a single tick. If the top screen specifies that screens below it
	 *  should be allowed to tick, ticks propagate downward until they hit a screen that refuses
	 *  to pass on the tick.
	 *  @todo Add ability for screens to request all ticks, regardless of positioning
	 */
	protected void onTick(long nanosSincePreviousTick)
	{
		boolean noPauseScreenAbove = true;
		List<Screen> atStartScreens = new ArrayList<>(this.screens);
		for(int i = 0; (i < atStartScreens.size()) && noPauseScreenAbove; i++)
		{
			if(this.screens.contains(atStartScreens.get(i)))
			{
				noPauseScreenAbove = atStartScreens.get(i).passesTicksBelow();
				atStartScreens.get(i).onTick(nanosSincePreviousTick);
			}
		}
	}

	/**
	 * Removes a given Game Screen from this Application. Does not need to be on top.
	 * It is recommended that all Screens, in their kill() method, remove themselves.
	 *
	 * @param gameScreen
	 * 		- A Game Screen to remove.
	 *
	 * @return true if and only if the game screen given has been removed from the Application
	 */
	public boolean removeGameScreen(Screen gameScreen)
	{
		return this.screens.remove(gameScreen);
	}
}
