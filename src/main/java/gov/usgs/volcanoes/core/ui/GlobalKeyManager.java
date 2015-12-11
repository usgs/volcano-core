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
 * @author Dan Cervelli
 */
public final class GlobalKeyManager extends EventQueue {
  private static final GlobalKeyManager instance = new GlobalKeyManager();

  static {
    Toolkit.getDefaultToolkit().getSystemEventQueue().push(instance);
  }

  /**
   * Instance accessor.
   * 
   * @return singleton
   */
  public static GlobalKeyManager getInstance() {
    return instance;
  }

  private final ActionMap actions = new ActionMap();

  private InputMap currentInputMap;

  private final InputMap keyStrokes = new InputMap();

  /**
   * Private constructor, we get instance only via <code>getInstance()</code>.
   */
  private GlobalKeyManager() {}

  /**
   * Recovers {@link EventQueue#dispatchEvent(AWTEvent event) }.
   * 
   * @param event event
   */
  @Override
  protected void dispatchEvent(AWTEvent event) {
    if (event instanceof KeyEvent) {
      // KeyStroke.getKeyStrokeForEvent converts an ordinary KeyEvent
      // to a keystroke, as stored in the InputMap. Keep in mind that
      // Numpad keystrokes are different to ordinary keys, i.e. if you
      // are listening to
      final KeyStroke ks = KeyStroke.getKeyStrokeForEvent((KeyEvent) event);
      // if (DEBUG)
      // System.out.println("KeyStroke=" + ks);
      final String actionKey = (String) keyStrokes.get(ks);
      Action action = null;
      if (actionKey != null) {
        action = actions.get(actionKey);
      } else if (currentInputMap != null) {
        final String key = (String) currentInputMap.get(ks);
        action = actions.get(key);
      }
      if (action != null && action.isEnabled()) {
        // I'm not sure about the parameters
        action.actionPerformed(new ActionEvent(event.getSource(), event.getID(), actionKey,
            ((KeyEvent) event).getModifiers()));
        return; // consume event
      }
    }
    super.dispatchEvent(event); // let the next in chain handle event
  }

  /**
   * ActionMap accessor.
   * 
   * @return ActionMap
   */
  public ActionMap getActionMap() {
    return actions;
  }

  /**
   * InputMap accessor.
   * 
   * @return InputMap
   */
  public InputMap getInputMap() {
    return keyStrokes;
  }

  /**
   * InputMap mutator.
   * 
   * @param imap InputMap
   */
  public void setCurrentMap(InputMap imap) {
    currentInputMap = imap;
  }
}
