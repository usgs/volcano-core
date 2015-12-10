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
 * Dialog of the specified size at the center of parent frame and with OK and CANCEL buttons
 * 
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2005/04/23 15:56:45  cervelli
 * Cleanup.
 *
 * @author Dan Cervelli
 */
public class BaseDialog extends JDialog 
{
	private static final long serialVersionUID = -1;
	protected int height;
	protected int width;
	
	protected JButton okButton;
	protected JButton cancelButton;
	protected JPanel mainPanel;
	
	protected JFrame parent;
	
	private boolean okClicked;
	
	/**
	 * Constructor
	 * 
	 * @param parent parent frame
	 * @param title string for dialog title
	 * @param modal flag if dialog is modal
	 * @param w width
	 * @param h height
	 */
	protected BaseDialog(JFrame parent, String title, boolean modal, int w, int h)
	{
		super(parent, title, modal);
		this.parent = parent;
		width = w;
		height = h;
		createUI();	
	}

	/**
	 * Initialization procedure
	 */
	protected void createUI()
	{
		this.setSize(width, height);
		Dimension parentSize = parent.getSize();
		Point parentLoc = parent.getLocation();
		this.setLocation(parentLoc.x + (parentSize.width / 2 - width / 2),
				parentLoc.y + (parentSize.height / 2 - height / 2));
		
		mainPanel = new JPanel(new BorderLayout());
		JPanel buttonPanel = new JPanel();
		okButton = new JButton("OK");
		okButton.setMnemonic('O');
		okButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						if (allowOK())
						{
							dispose();
							okClicked = true;
							wasOK();
						}
					}
				});
		cancelButton = new JButton("Cancel");
		cancelButton.setMnemonic('C');
		cancelButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						if (allowCancel())
						{
							dispose();
							wasCancelled();
						}
					}
				});
		UiUtils.mapKeyStrokeToButton(mainPanel, "ESCAPE", "cancel1", cancelButton);
		this.addWindowListener(new WindowAdapter() 
				{
		            public void windowOpened(WindowEvent e)
		            {
		            	okButton.requestFocus();
		                JRootPane root = SwingUtilities.getRootPane(okButton);
		                if (root != null) 
		                    root.setDefaultButton(okButton);
		            }
		            
		            public void windowClosing(WindowEvent e)
		            {
		            	if (!okClicked)
		            		wasCancelled();
		            }
				});

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		this.setContentPane(mainPanel);
	}

	/**
	 * Does nothing
	 * @return true
	 */
	protected boolean allowOK()
	{
		return true;
	}

	/**
	 * Does nothing
	 * @return true
	 */
	protected boolean allowCancel()
	{
		return true;
	}

	/**
	 * Does nothing
	 */
	protected void wasOK() {}
	
	/**
	 * Does nothing
	 */
	protected void wasCancelled() {}
}
