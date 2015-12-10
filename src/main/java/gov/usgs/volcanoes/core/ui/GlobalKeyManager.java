package gov.usgs.volcanoes.core.ui;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;

/**
 * <p>This class was originally downloaded from a post to developers.java.sun
 * forum and modified from there.</p>
 * <p>Realize singleton pattern.</p>
 * <p>Integrate itself into system event queue and define global sets of keystrokes and actions.
 * While event propagated, search actions for keystroke in global action map, 
 * if fails, search in local action map and perform found action, then pass event to 
 * further propagation.</p>
 *
 * $Log: not supported by cvs2svn $
 * @author Dan Cervelli
 */
public class GlobalKeyManager extends EventQueue 
{
	private static final GlobalKeyManager instance = new GlobalKeyManager();

	private final InputMap keyStrokes = new InputMap();
	private final ActionMap actions = new ActionMap();
	
	private InputMap currentInputMap;
	
	static 
	{
		Toolkit.getDefaultToolkit().getSystemEventQueue().push(instance);
	}

	/**
	 * Private constructor, we get instance only via <code>getInstance()</code>
	 */
	private GlobalKeyManager() 
	{}

	/**
	 * Getter for only class instance
	 */
	public static GlobalKeyManager getInstance() 
	{
		return instance;
	}

	/**
	 * Getter for global <code>InputMap</code>
	 * @return InputMap
	 */
	public InputMap getInputMap() 
	{
		return keyStrokes;
	}

	/**
	 * Getter for global <code>ActionMap</code>
	 * @return ActionMap
	 */
	public ActionMap getActionMap() 
	{
		return actions;
	}

	/**
	 * Setter for local <code>InputMap</code>
	 * @param imap InputMap
	 */
	public void setCurrentMap(InputMap imap)
	{
		currentInputMap = imap;
	}
	
	/**
	 * Recovers {@link EventQueue#dispatchEvent(AWTEvent event) }
	 * @param event
	 */
	protected void dispatchEvent(AWTEvent event) 
	{
		if (event instanceof KeyEvent) 
		{
			// KeyStroke.getKeyStrokeForEvent converts an ordinary KeyEvent
			// to a keystroke, as stored in the InputMap. Keep in mind that
			// Numpad keystrokes are different to ordinary keys, i.e. if you
			// are listening to
			KeyStroke ks = KeyStroke.getKeyStrokeForEvent((KeyEvent) event);
//			if (DEBUG)
//				System.out.println("KeyStroke=" + ks);
			String actionKey = (String)keyStrokes.get(ks);
			Action action = null;
			if (actionKey != null) // global 
				action = actions.get(actionKey);
			else if (currentInputMap != null)
			{
				String key = (String)currentInputMap.get(ks);
				action = actions.get(key);
			}
			if (action != null && action.isEnabled()) 
			{
				// I'm not sure about the parameters
				action.actionPerformed(new ActionEvent(event.getSource(),
						event.getID(), actionKey, ((KeyEvent) event)
								.getModifiers()));
				return; // consume event
			}
		}
		super.dispatchEvent(event); // let the next in chain handle event
	}
}