package gov.usgs.volcanoes.core.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

/**
 * Dialog of the specified size at the center of parent frame and with OK and CANCEL buttons.
 *
 * @author Dan Cervelli
 */
public class BaseDialog extends JDialog {
  private static final long serialVersionUID = -1;
  protected JButton cancelButton;
  protected int height;

  protected JPanel mainPanel;
  protected JButton okButton;
  private boolean okClicked;

  protected JFrame parent;

  protected int width;

  /**
   * Constructor.
   * 
   * @param parent parent frame
   * @param title string for dialog title
   * @param modal flag if dialog is modal
   * @param width width
   * @param height height
   */
  protected BaseDialog(JFrame parent, String title, boolean modal, int width, int height) {
    super(parent, title, modal);
    this.parent = parent;
    this.width = width;
    this.height = height;
    createUi();
  }

  /**
   * Permit or deny cancel. Always permit.
   * 
   * @return true
   */
  protected boolean allowCancel() {
    return true;
  }

  /**
   * Permit or deny ok. Always permit.
   * 
   * @return true
   */
  protected boolean allowOk() {
    return true;
  }

  /**
   * Init dialog.
   * 
   */
  protected void createUi() {
    this.setSize(width, height);
    final Dimension parentSize = parent.getSize();
    final Point parentLoc = parent.getLocation();
    this.setLocation(parentLoc.x + (parentSize.width / 2 - width / 2),
        parentLoc.y + (parentSize.height / 2 - height / 2));

    mainPanel = new JPanel(new BorderLayout());
    final JPanel buttonPanel = new JPanel();
    okButton = new JButton("OK");
    okButton.setMnemonic('O');
    okButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        if (allowOk()) {
          dispose();
          okClicked = true;
          wasOk();
        }
      }
    });
    cancelButton = new JButton("Cancel");
    cancelButton.setMnemonic('C');
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        if (allowCancel()) {
          dispose();
          wasCancelled();
        }
      }
    });
    UiUtils.mapKeyStrokeToButton(mainPanel, "ESCAPE", "cancel1", cancelButton);
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent evt) {
        if (!okClicked) {
          wasCancelled();
        }
      }

      @Override
      public void windowOpened(WindowEvent evt) {
        okButton.requestFocus();
        final JRootPane root = SwingUtilities.getRootPane(okButton);
        if (root != null) {
          root.setDefaultButton(okButton);
        }
      }
    });

    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);
    mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    setContentPane(mainPanel);
  }

  /**
   * Act when cancel button is pressed. Do nothing.
   * 
   */
  protected void wasCancelled() {}

  /**
   * Act when ok button is pressed. Do nothing.
   */
  protected void wasOk() {}
}
