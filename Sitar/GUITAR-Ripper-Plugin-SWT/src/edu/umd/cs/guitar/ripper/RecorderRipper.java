package edu.umd.cs.guitar.ripper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.umd.cs.guitar.exception.GException;
import edu.umd.cs.guitar.model.GComponent;

import edu.umd.cs.guitar.model.GWindow;
import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.ContainerType;
import edu.umd.cs.guitar.model.data.FullComponentType;

import edu.umd.cs.guitar.model.data.GUIType;

import edu.umd.cs.guitar.model.data.StepType;

import edu.umd.cs.guitar.model.swtwidgets.SitarWidget;
import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;

import edu.umd.cs.guitar.ripper.filter.GComponentFilter;

import edu.umd.cs.guitar.util.GUITARLog;

public class RecorderRipper extends Ripper {

	/**
	 * Indicates thay GUI components images are to be ripped and archived.
	 */
	boolean useImage = false;

	/**
	 * useImage accessor
	 */
	public void setUseImage() {
		this.useImage = true;
	}

	public RecorderRipper(org.apache.log4j.Logger log) {
		super(log);

	}

	private boolean exit = false;
	private GComponent uComponent = null;
	private GWindow uWindow = null;

	public void setExitTrue() {
		exit = true;
	}

	public void setUComponent(GComponent component) {
		this.uComponent = component;
	}
	public void setUWindow(GWindow window){
		this.uWindow = window;
	}

	/**
	 * Entry point for beginning the ripping process.
	 * 
	 * The ripping process generates the .GUI file and other artifacts (if any)
	 * in the strDataPath directory.
	 * 
	 * Exceptions propagate up to this method as of now. Ideally, this method
	 * must propagate it to the caller.
	 */
	public void execute() {
		try {
			if (monitor == null) {
				GUITARLog.log.error("No monitor hasn't been assigned");
				throw new IOException();
			}

			// 1. Set Up the environment
			monitor.setUp();

			// 2. Get the list of root window
			List<GWindow> gRootWindows = monitor.getRootWindows();

			if (gRootWindows == null) {
				GUITARLog.log.warn("No root window");
				// throw new RipperStateException();
			}

			GUITARLog.log
					.info("Number of root windows: " + gRootWindows.size());

			// 3. Main step: ripping starting from each root window in the list
			for (GWindow xRootWindow : gRootWindows) {
				xRootWindow.setRoot(true);
				monitor.addRippedList(xRootWindow);

				GUIType gRoot = ripWindow(xRootWindow);
				this.dGUIStructure.getGUI().add(gRoot);
			}

			// 4. Generate ID for widgets
			if (this.idGenerator == null) {
				GUITARLog.log.warn("No ID generator assigned");
				// throw new RipperStateException();
			} else {
				idGenerator.generateID(dGUIStructure);
			}

			// Wait here and check if our recorder listener in sitar widget has
			// signaled us to do something
			while (!exit) {
				// /Monitor needs to be set up correctly
				// Call rip user opened window
				
				if(SitarWidget.hasExpansionData()){
					((SitarRipperMonitor)monitor).addInteractionData(SitarWidget.getExpansionData());
					
					//Not sure what these args should be?????? set by our listener?
					ripUserOpenedWindow(uComponent, uWindow);
					SitarWidget.clearExpansionData();
				}
				
				// Generate ID's for new window components
				if (this.idGenerator == null) {
					GUITARLog.log.warn("No ID generator assigned");
					// throw new RipperStateException();
				} else {
					idGenerator.generateID(dGUIStructure);
				}

				Thread.sleep(2000);
			}

			// 5. Clean up
			monitor.cleanUp();
		} catch (GException e) {
			GUITARLog.log.error("GUITAR error while ripping" + e);

		} catch (IOException e) {
			GUITARLog.log.error("IO error while ripping" + e);

		} catch (Exception e) {
			GUITARLog.log.error("Uncaught exception while ripping" + e);
			GUITARLog.log.error("Likely AUT bug. If not, file GUITAR bug");

		}
	}

	/*
	 * This function must pick up where the component ripper left off and
	 * perform the necessary ripping actions
	 */
	public void ripUserOpenedWindow(GComponent component, GWindow window) {
		// Trigger terminal widget
		ComponentType retComp = component.extractProperties();
		//This compA is only used if monitor.getClosedWindowCache() > 0
		ComponentTypeWrapper compA = new ComponentTypeWrapper(retComp);

		LinkedList<GWindow> lClosedWindows = monitor.getClosedWindowCache();

		String sTitle = window.getTitle();

		if (lClosedWindows.size() > 0) {

			GUITARLog.log.debug("!!!!! Window closed");

			for (GWindow closedWin : lClosedWindows) {
				String sClosedWinTitle = closedWin.getTitle();

				// Only consider widget closing the current window
				if (sTitle.equals(sClosedWinTitle)) {

					GUITARLog.log.debug("\t" + sClosedWinTitle);

					List<FullComponentType> lCloseComp = lCloseWindowComp
							.getFullComponent();

					FullComponentType cCloseComp = factory
							.createFullComponentType();
					cCloseComp.setWindow(closedWin.extractWindow().getWindow());
					cCloseComp.setComponent(retComp);
					lCloseComp.add(cCloseComp);
					lCloseWindowComp.setFullComponent(lCloseComp);
				} // if
			} // for
		} // if

		if (monitor.isNewWindowOpened()) {

			List<FullComponentType> lOpenComp = lOpenWindowComps
					.getFullComponent();
			FullComponentType cOpenComp = factory.createFullComponentType();
			cOpenComp.setWindow(window.extractWindow().getWindow());
			cOpenComp.setComponent(retComp);
			lOpenComp.add(cOpenComp);
			lOpenWindowComps.setFullComponent(lOpenComp);

			LinkedList<GWindow> lNewWindows = monitor.getOpenedWindowCache();
			monitor.resetWindowCache();
			GUITARLog.log.info(lNewWindows.size() + " new window(s) opened!!!");
			for (GWindow newWins : lNewWindows) {
				GUITARLog.log.info("*\t Title:*" + newWins.getTitle() + "*");
			}

			// Process the opened windows in a FIFO order
			while (!lNewWindows.isEmpty()) {

				GWindow gNewWin = lNewWindows.getLast();
				lNewWindows.removeLast();

				GComponent gWinComp = gNewWin.getContainer();

				if (gWinComp != null) {

					// Add invokelist property for the component
					String sWindowTitle = gNewWin.getTitle();
					compA = new ComponentTypeWrapper(retComp);
					compA.addValueByName(GUITARConstants.INVOKELIST_TAG_NAME,
							sWindowTitle);

					GUITARLog.log.debug(sWindowTitle + " recorded");

					retComp = compA.getDComponentType();

					// Check ignore window
					if (!monitor.isIgnoredWindow(gNewWin)) {

						if (!monitor.isRippedWindow(gNewWin)) {
							gNewWin.setRoot(false);
							monitor.addRippedList(gNewWin);

							GUIType dWindow = ripWindow(gNewWin);

							if (dWindow != null)
								dGUIStructure.getGUI().add(dWindow);
						} else {
							GUITARLog.log.info("Window is ripped!!!");
						}

					} else {
						GUITARLog.log.info("Window is ignored!!!");
					}
				}

				monitor.closeWindow(gNewWin);
			}
		}

		// TODO: check if the component is still available after ripping
		// its child window
		List<GComponent> gChildrenList = component.getChildren();
		int nChildren = gChildrenList.size();
		int i = 0;

		// Debug

		String lChildren = "[";
		for (int j = 0; j < nChildren; j++) {
			lChildren += gChildrenList.get(j).getTitle() + " - "
					+ gChildrenList.get(j).getClassVal() + "; ";
		}
		lChildren += "]";
		GUITARLog.log.debug("*" + component.getTitle() + "* in window *"
				+ window.getTitle() + "* has " + nChildren + " children: "
				+ lChildren);

		// End debug

		while (i < nChildren) {
			GComponent gChild = gChildrenList.get(i++);
			ComponentType guiChild = ripComponent(gChild, window);

			if (guiChild != null) {
				((ContainerType) retComp).getContents().getWidgetOrContainer()
						.add(guiChild);
			}

			if (nChildren < gChildrenList.size()) {
				nChildren = gChildrenList.size();
			}
		}
	}

	/**
	 * Rip a component
	 * 
	 * As of now this method does not propagate exceptions. It needs to be
	 * modified to progate exceptions. All callers need to be modified to handle
	 * exceptions.
	 * 
	 * <p>
	 * 
	 * @param component
	 * @return
	 */
	public ComponentType ripComponent(GComponent component, GWindow window) {
		GUITARLog.log.info("");
		GUITARLog.log.info("----------------------------------");
		GUITARLog.log.info("Ripping component: ");
		GUITARLog.log.info("Signature: ");

		printComponentInfo(component, window);

		// 1. Rip special/customized components
		for (GComponentFilter cm : lComponentFilter) {
			if (cm.isProcess(component, window)) {
				GUITARLog.log.info("Filter " + cm.getClass().getSimpleName()
						+ " is applied");

				return cm.ripComponent(component, window);
			}
		}

		// 2. Rip regular components
		ComponentType retComp = null;
		try {
			retComp = component.extractProperties();
			ComponentTypeWrapper compA = new ComponentTypeWrapper(retComp);

			GUIType guiType = null;

			if (window != null) {
				guiType = window.extractGUIProperties();
			}

			retComp = compA.getDComponentType();

			// 2.1 Try to perform action on the component
			// to reveal more windows/components

			// clear window opened cache before performing actions
			monitor.resetWindowCache();

		} catch (Exception e) {
			if (e.getClass().getName()
					.contains("StaleElementReferenceException")) {
				/**
				 * This can happen when performing an action causes a page
				 * navigation in the current window, for example, when
				 * submitting a form.
				 */

				GUITARLog.log.warn("Element went away: " + e.getMessage());
			} else {
				// TODO: Must throw exception
				GUITARLog.log.error("ripComponent exception", e);
			}

			/**
			 * We'll return the component we calculated anyway so it gets added
			 * to the GUI map. I'm not entirely sure this is the right thing to
			 * do, but it gets us further anyway.
			 */
			return retComp;
		}

		return retComp;
	}

	private void printComponentInfo(GComponent component, GWindow window) {
		String sComponentInfo = "\n";

		sComponentInfo += "<FullComponent>" + "\n";
		sComponentInfo += "\t<Window>" + "\n";
		sComponentInfo += "\t\t<Attributes>" + "\n";

		sComponentInfo += "\t\t\t<Property>" + "\n";
		sComponentInfo += "\t\t\t\t<Name>" + GUITARConstants.TITLE_TAG_NAME
				+ "</Name>" + "\n";
		sComponentInfo += "\t\t\t\t<Value>" + window.getTitle() + "</Value>"
				+ "\n";
		sComponentInfo += "\t\t\t</Property> " + "\n";
		sComponentInfo += "\t\t</Attributes>" + "\n";
		sComponentInfo += "\t</Window>" + "\n";
		sComponentInfo += "\n";

		sComponentInfo += "\t<Component>" + "\n";
		sComponentInfo += "\t\t<Attributes>" + "\n";

		sComponentInfo += "\t\t\t<Property>" + "\n";
		sComponentInfo += "\t\t\t\t<Name>" + GUITARConstants.TITLE_TAG_NAME
				+ "</Name>" + "\n";
		sComponentInfo += "\t\t\t\t<Value>" + component.getTitle() + "</Value>"
				+ "\n";
		sComponentInfo += "\t\t\t</Property>" + "\n";
		sComponentInfo += "\n";

		sComponentInfo += "\t\t\t<Property>" + "\n";
		sComponentInfo += "\t\t\t\t<Name>" + GUITARConstants.CLASS_TAG_NAME
				+ "</Name>" + "\n";
		sComponentInfo += "\t\t\t\t<Value>" + component.getClassVal()
				+ "</Value>" + "\n";
		sComponentInfo += "\t\t\t</Property>" + "\n";
		sComponentInfo += "\t\t</Attributes>" + "\n";
		sComponentInfo += "\t</Component>" + "\n";
		sComponentInfo += "</FullComponent>" + "\n";
		sComponentInfo += "\n";

		GUITARLog.log.info(sComponentInfo);
	}

	List<StepType> stepList = new ArrayList<StepType>();

	public void setTestCase(List<StepType> iStepList) {
		stepList = iStepList;
	}

}
