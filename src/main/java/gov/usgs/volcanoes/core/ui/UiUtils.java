/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0
 * Universal public domain dedication.
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 * Utility class encapsulating methods for working with UI elements.
 *
 * @author Dan Cervelli
 * @author Tom Parker
 *
 */
public final class UiUtils {
  /**
   * Adds to JComponent action and keystroke for it.
   *
   * @param comp JComponent
   * @param ks KeyStroke
   * @param name name
   * @param action action
   */
  public static void mapKeyStrokeToAction(final JComponent comp, final String ks, final String name,
      final AbstractAction action) {
    comp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(ks),
        name);
    comp.getActionMap().put(name, action);
  }

  /**
   * Adds to JComponent button and keystroke for it.
   *
   * @param comp JComponent
   * @param ks KeyStroke
   * @param name name
   * @param button button
   */
  public static void mapKeyStrokeToButton(final JComponent comp, final String ks, final String name,
      final AbstractButton button) {
    comp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(ks),
        name);
    comp.getActionMap().put(name, new AbstractAction() {
      private static final long serialVersionUID = -1;

      @Override
      public void actionPerformed(ActionEvent evt) {
        button.doClick();
      }
    });
  }

  /** uninstantiatable. */
  private UiUtils() {}

}
