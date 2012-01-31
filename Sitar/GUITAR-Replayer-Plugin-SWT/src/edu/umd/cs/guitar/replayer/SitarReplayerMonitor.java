/*	
 *  Copyright (c) 2009-@year@. The GUITAR group at the University of Maryland. Names of owners of this group may
 *  be obtained by sending an e-mail to atif@cs.umd.edu
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 *  documentation files (the "Software"), to deal in the Software without restriction, including without 
 *  limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *	the Software, and to permit persons to whom the Software is furnished to do so, subject to the following 
 *	conditions:
 * 
 *	The above copyright notice and this permission notice shall be included in all copies or substantial 
 *	portions of the Software.
 *
 *	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 *	LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
 *	EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *	IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *	THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
package edu.umd.cs.guitar.replayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import org.eclipse.swt.widgets.Shell;

import edu.umd.cs.guitar.event.GEvent;
import edu.umd.cs.guitar.model.GWindow;
import edu.umd.cs.guitar.model.SitarApplication;
import edu.umd.cs.guitar.model.SitarConstants;
import edu.umd.cs.guitar.model.SitarWindow;
import edu.umd.cs.guitar.model.data.AttributesType;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.PropertyType;
import edu.umd.cs.guitar.ripper.SitarMonitor;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * Monitor for {@link SitarReplayer} to handle SWT specific features. Adapted from
 * <code>JFCReplayerMonitor</code>.
 * 
 * @author Gabe Gorelick
 * 
 * @see SitarReplayer
 */
public class SitarReplayerMonitor extends GReplayerMonitor {

	private final SitarApplication application;
	
	// monitor to delegate actions shared with ripper to
	private final SitarMonitor monitor;

	/**
	 * Construct a new {@code SitarReplayerMonitor}.
	 * 
	 * @param config
	 *            replayer configuration
	 * @param app
	 *            the {@code SitarApplication} for the GUI under test
	 */
	public SitarReplayerMonitor(SitarReplayerConfiguration config, SitarApplication app) {
		this.application = app;
		this.monitor = new SitarMonitor(config, app);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUp() {
		GUITARLog.log.info("Setting up SitarReplayer...");
	}

	/**
	 * {@inheritDoc}
	 * 
	 * This implementation simply delegates to {@link SitarMonitor#cleanUp()}.
	 */
	@Override
	public void cleanUp() {
		monitor.cleanUp();
	}

	/**
	 * Get a {@code GEvent} given its name. Returns {@code null} if no action
	 * with the given name could be found.
	 * 
	 * @param actionName
	 *            the fully qualified name of the event, passed to
	 *            {@link Class#forName(String)}
	 */
	@Override
	public GEvent getAction(String actionName) {
		GEvent retAction = null;
		try {
			Class<?> c = Class.forName(actionName);
			Object action = c.newInstance();
			retAction = (GEvent) action;
		} catch (ClassNotFoundException e) {
			GUITARLog.log.error("Error in getting action", e);
		} catch (InstantiationException e) {
			GUITARLog.log.error("Error in getting action", e);
		} catch (IllegalAccessException e) {
			GUITARLog.log.error("Error in getting action", e);
		}

		return retAction;
	}

	/**
	 * This method is not used by SWTGuitar.
	 * 
	 * @param action
	 *            this parameter is ignored
	 * @return {@code null}
	 */
	@Override
	public final Object getArguments(String action) {
		// not being used by JFC
		return null;
	}

	/**
	 * Get a window given a title.
	 * @param windowTitle the title of the window
	 * @return the window with the given title
	 */
	@Override
	public GWindow getWindow(String windowTitle) {
		GWindow retGXWindow = null;
		while (retGXWindow == null) {
			final AtomicReference<Shell[]> shells = new AtomicReference<Shell[]>();

			application.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					shells.set(application.getDisplay().getShells());
				}
			});

			for (Shell s : shells.get()) {
				Shell shell = getOwnedWindowByID(s, windowTitle);
				if (shell != null) {
					retGXWindow = new SitarWindow(shell);
					break;
				}
			}
			
		}
		return retGXWindow;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PropertyType> selectIDProperties(ComponentType comp) {
		if (comp == null) {
			return new ArrayList<PropertyType>();
		}

		List<PropertyType> retIDProperties = new ArrayList<PropertyType>();

		AttributesType attributes = comp.getAttributes();
		List<PropertyType> lProperties = attributes.getProperty();
		for (PropertyType p : lProperties) {
			if (SitarConstants.ID_PROPERTIES.contains(p.getName()))
				retIDProperties.add(p);
		}
		return retIDProperties;
	}

	// recursively search a window for a given widget 
	private Shell getOwnedWindowByID(final Shell parent, String sWindowID) {

		if (parent == null) {
			return null;
		}

		GWindow gWindow = new SitarWindow(parent);

		String title = gWindow.getTitle();
		if (title == null) {
			return null;
		}

		if (isUseReg) {
			if (Pattern.matches(sWindowID, title)) {
				return parent;
			}
		} else {
			if (sWindowID.equals(title)) {
				return parent;
			}
		}

		final Shell[][] childShells = new Shell[1][];
		application.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				childShells[0] = parent.getShells();
			}
		});
		
		Shell retShell = null;
		for (Shell s : childShells[0]) {
			// keep searching children
			retShell = getOwnedWindowByID(s, sWindowID);
			if (retShell != null) {
				return retShell;	
			}
		}
		
		return retShell;
	}
	
	/**
	 * Connect to the application under test. This method simply calls
	 * {@link SitarApplication#connect()}.
	 */
	@Override
	public void connectToApplication() {
		application.connect();
	}

	/**
	 * Return the {@code SitarApplication} used by the monitor to communicate with
	 * the GUI.
	 * 
	 * @return the {@code SitarApplication} used to communicate with the GUI
	 */
	@Override
	public SitarApplication getApplication() {
		return application;
	}

}