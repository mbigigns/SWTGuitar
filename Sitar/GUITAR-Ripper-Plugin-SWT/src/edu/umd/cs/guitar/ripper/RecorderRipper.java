package edu.umd.cs.guitar.ripper;
/*
 *  Copyright (c) 2009-@year@. The  GUITAR group  at the University of
 *  Maryland. Names of owners of this group may be obtained by sending
 *  an e-mail to atif@cs.umd.edu
 *
 *  Permission is hereby granted, free of charge, to any person obtaining
 *  a copy of this software and associated documentation files
 *  (the "Software"), to deal in the Software without restriction,
 *  including without limitation  the rights to use, copy, modify, merge,
 *  publish,  distribute, sublicense, and/or sell copies of the Software,
 *  and to  permit persons  to whom  the Software  is furnished to do so,
 *  subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY,  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO  EVENT SHALL THE  AUTHORS OR COPYRIGHT  HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR  OTHER LIABILITY,  WHETHER IN AN  ACTION OF CONTRACT,
 *  TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *  SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.io.IOException;
import java.awt.AWTException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import edu.umd.cs.guitar.exception.GException;
//import edu.umd.cs.guitar.exception.RipperStateException;
import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GIDGenerator;
import edu.umd.cs.guitar.model.GWindow;
import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.SitarApplication;
import edu.umd.cs.guitar.model.data.ComponentListType;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.Configuration;
import edu.umd.cs.guitar.model.data.ContainerType;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.model.data.EventsType;
import edu.umd.cs.guitar.model.data.FullComponentType;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.model.data.GUIType;
import edu.umd.cs.guitar.model.data.ObjectFactory;
import edu.umd.cs.guitar.model.data.StepType;
import edu.umd.cs.guitar.model.swtwidgets.SitarWidget;
import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;
import edu.umd.cs.guitar.model.wrapper.EventWrapper;
import edu.umd.cs.guitar.model.wrapper.GUIStructureWrapper;
import edu.umd.cs.guitar.model.wrapper.GUITypeWrapper;
import edu.umd.cs.guitar.ripper.filter.GComponentFilter;
import edu.umd.cs.guitar.model.SitarWindow;
import edu.umd.cs.guitar.model.swtwidgets.IDGenerator;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * The core ripping algorithm implementation.
 *
 * Exceptions encountered during the ripping processes are propagated
 * up from the lower level as high as possible. Ideally, the execute()
 * method should propagate it upwards. As of now, the execute() function
 * is as far as the exceptions propagate.
 *
 * Exceptions caused by GUITAR state errors are thrown as
 * RipperStateException. All other exceptions are caused by the AUT.
 *
 * <p>
 * 
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 */
public class RecorderRipper extends Ripper
{

	private SitarApplication swtApp;

	private Set<GWindow> rippedWindows = new HashSet<GWindow>();

	private SitarWindow newRippedWindow = null;

	private static boolean continueRip = true;
	/**
	 * SECTION: DATA
	 *
	 * This section contains data structures and accessor functions
	 * for the data structures.
	 */

	/**
	 * Constructor with logger
	 * <p>
	 * 
	 * @param logger  External logger
	 */
	public
	RecorderRipper(Logger logger)
	{
		super();
		this.log = logger;
		lOpenWindowComps = factory.createComponentListType();
		lCloseWindowComp = factory.createComponentListType();
	}

	/**
	 * Constructor without logger
	 */
	public RecorderRipper(SitarApplication swtApp)
	{
		this(Logger.getLogger("Ripper"));
		this.swtApp = swtApp;
	}


	static ObjectFactory factory = new ObjectFactory();

	/**
	 * GUIStructure storing the ripped result
	 */
	GUIStructure dGUIStructure = new GUIStructure();

	/**
	 * @return the dGUIStructure
	 */
	public GUIStructure getResult() {
		return dGUIStructure;
	}


	/*
	 * Ripper monitor. Monitor performs tasks such as detecting windows.
	 */
	GRipperMonitor monitor = null;

	/**
	 * @return the monitor
	 */
	public GRipperMonitor getMonitor() {
		return monitor;
	}

	/**
	 * @param monitor  The monitor to set
	 */
	public void setMonitor(GRipperMonitor monitor) {
		this.monitor = monitor;
	}


	/**
	 * Indicates if regular expression patterns will be used for
	 * matching window titles.
	 */
	boolean useReg = false;

	/**
	 * useReg accessor
	 */
	public void
	setUseRegex()
	{
		this.useReg = true;
	}

	/**
	 * Indicates thay GUI components images are to be ripped and archived.
	 */
	boolean useImage = false;

	/**
	 * useImage accessor
	 */
	public void
	setUseImage()
	{
		this.useImage = true;
	}


	/**
	 * Comparator for widgets
	 */
	GIDGenerator idGenerator = null;

	/**
	 * @return the iDGenerator
	 */
	public GIDGenerator getIDGenerator() {
		return idGenerator;
	}

	/**
	 * @param iDGenerator   IDGenerator to use for the Ripper
	 */
	public void setIDGenerator(GIDGenerator iDGenerator) {
		idGenerator = iDGenerator;
	}


	/**
	 * Path for storing artifacts
	 */
	String strDataPath;

	/**
	 * Set the path where the ripper can save artifacts.
	 *
	 * @param strDataPath   Name of path where the ripper stores artifacts
	 */
	public void
	setDataPath(String strDataPath)
	{
		this.strDataPath = strDataPath;
	}


	// Window filter
	LinkedList<GWindowFilter> lWindowFilter =
			new LinkedList<GWindowFilter>();;

			/**
			 * Add a window filter
			 * 
			 * @param filter
			 */
			public void addWindowFilter(GWindowFilter filter) {
				if (this.lWindowFilter == null) {
					lWindowFilter = new LinkedList<GWindowFilter>();
				}

				lWindowFilter.addLast(filter);
				filter.setRipper(this);
			}

			/**
			 * Remove a window filter
			 * 
			 * @param filter
			 */
			public void removeWindowFilter(GWindowFilter filter) {
				lWindowFilter.remove(filter);
				filter.setRipper(null);
			}


			// Component filter
			LinkedList<GComponentFilter> lComponentFilter =
					new LinkedList<GComponentFilter>();

			/**
			 * Add a component filter
			 * 
			 * @param filter
			 */
			public void addComponentFilter(GComponentFilter filter) {
				if (this.lComponentFilter == null) {
					lComponentFilter = new LinkedList<GComponentFilter>();
				}
				lComponentFilter.addLast(filter);
				filter.setRipper(this);
			}

			/**
			 * Remove a component filter
			 * 
			 * @param filter
			 */
			public void removeComponentFilter(GComponentFilter filter) {
				lComponentFilter.remove(filter);
				filter.setRipper(null);
			}


			// Opened / closed window list
			ComponentListType lOpenWindowComps;
			ComponentListType lCloseWindowComp;

			/**
			 * @return the lOpenWindowComps
			 */
			public ComponentListType getlOpenWindowComps() {
				return lOpenWindowComps;
			}

			/**
			 * @return the lCloseWindowComp
			 */
			public ComponentListType getlCloseWindowComp() {
				return lCloseWindowComp;
			}


			// Log
			Logger log;

			/**
			 * @return the log
			 */
			public Logger getLog() {
				return log;
			}

			/**
			 * @param log   The log to set
			 */
			public void setLog(Logger log) {
				this.log = log;
			}


			/**
			 * SECTION: LOGIC
			 *
			 * This section contains methods which implement the ripper
			 * logic.
			 */

			/**
			 * Entry point for beginning the ripping process.
			 *
			 * The ripping process generates the .GUI file and other
			 * artifacts (if any) in the strDataPath directory.
			 *
			 * Exceptions propagate up to this method as of now. Ideally, this
			 * method must propagate it to the caller.
			 */
			public void
			execute()
			{
				try {
					if (monitor == null) {
						GUITARLog.log.error("No monitor hasn't been assigned");
						throw new Exception();//RipperStateException();
					}

					// 1. Set Up the environment
					monitor.setUp();

					// 2. Get the list of root window
					List<GWindow> gRootWindows = monitor.getRootWindows();

					if (gRootWindows == null) {
						GUITARLog.log.warn("No root window");
						throw new Exception();//RipperStateException();
					}

					GUITARLog.log.info("Number of root windows: " + gRootWindows.size());


					for (GWindow xRootWindow : gRootWindows) {
						processWindow(xRootWindow);
						rippedWindows.add(xRootWindow);
					}

					while (continueRip) {
						if (this.swtApp != null && this.swtApp.getDisplay() != null) {
							swtApp.getDisplay().syncExec(new Runnable() {
								public void run() {
									for (Shell shell : RecorderRipper.this.swtApp.getDisplay().getShells()) {
										boolean shouldNotRip = false;
										for (GWindow rippedWin : rippedWindows) {
											//if (((SitarWindow)rippedWin).getShell().equals(shell)){
											if (((SitarWindow)rippedWin).getShell() == shell){
												shouldNotRip = true;
											}
										}
										if (!shouldNotRip) {
											newRippedWindow = new SitarWindow(shell); 
											try {
												processWindow(newRippedWindow);
											}catch (Exception e) {
												e.printStackTrace();
											}
											rippedWindows.add(newRippedWindow);
										}
									}	
								}
							});

						}
						Thread.sleep(5);
					}


					// 5. Clean up
					//monitor.cleanUp();
				} catch (GException e) {
					GUITARLog.log.error("GUITAR error while ripping" + e);

				} catch (IOException e) {
					GUITARLog.log.error("IO error while ripping" + e);

				} catch (Exception e) {
					GUITARLog.log.error("Uncaught exception while ripping" + e);
					GUITARLog.log.error("Likely AUT bug. If not, file GUITAR bug");
					e.printStackTrace();
				}
			}
			IDGenerator myGenerator = new IDGenerator();
			public void processWindow(GWindow xRootWindow) throws IOException, Exception
			{
				xRootWindow.setRoot(true);
				monitor.addRippedList(xRootWindow);

				// 3. Main step: generate widget IDs to components in the window
				GUIType gRoot = ripWindow(xRootWindow);
				this.dGUIStructure.getGUI().add(gRoot);

				// 4. Update GUI to reflect the new developments
				if (this.idGenerator == null) {
					GUITARLog.log.warn("No ID generator assigned");
					throw new Exception();//RipperStateException();
				} else {
					idGenerator.generateID(dGUIStructure);
					myGenerator.generateID(dGUIStructure);
				}
				//5. Add events to the event_ID look-up table
				createEventLookup(dGUIStructure);
			}
			public Map<EventWrapper,String> eventToIDMap = new HashMap<EventWrapper,String>();
			EFG efg;
			EventsType dEventList;
			public static List<EventWrapper> wEventList = new ArrayList<EventWrapper>();
			public void createEventLookup(GUIStructure dGUIStructure)
			{
				wEventList.clear();
				dGUIStructure.getGUI().get(0).getContainer().getContents()
				.getWidgetOrContainer();
				GUIStructureWrapper wGUIStructure = new GUIStructureWrapper(
						dGUIStructure);

				wGUIStructure.parseData();

				List<GUITypeWrapper> wWindowList = wGUIStructure.getGUIs();

				for (GUITypeWrapper window : wWindowList) {
					readEventList(window.getContainer());
				}

				efg = factory.createEFG();

				// -------------------------------------
				// Reading event name
				// -------------------------------------
				dEventList = factory.createEventsType();
				for (EventWrapper wEvent : wEventList) {
					EventType dEvent = factory.createEventType();

					String index = getIndexFromWidget(wEvent);

					// dEvent.setEventId(EVENT_ID_PREFIX + index);
					dEvent.setEventId(wEvent.getID());

					dEvent.setWidgetId(wEvent.getComponent().getFirstValueByName(
							GUITARConstants.ID_TAG_NAME));

					dEvent.setType(wEvent.getType());
					//dEvent.setName(wEvent.getName());
					dEvent.setAction(wEvent.getAction());
					//dEvent.setListeners(wEvent.getListeners());

					if (wEvent.getComponent().getWindow().isRoot()
							&& !wEvent.isHidden())
						dEvent.setInitial(true);
					else
						dEvent.setInitial(false);

					dEventList.getEvent().add(dEvent);
				}

				efg.setEvents(dEventList);

			}
			private static final String EVENT_ID_SPLITTER = "_";
			/**
			 * 
			 */
			private static final String EVENT_ID_PREFIX = "e";
			private void readEventList(ComponentTypeWrapper component) {

				List<String> sActionList = component
						.getValueListByName(GUITARConstants.EVENT_TAG_NAME);

				if (sActionList != null)
					for (String action : sActionList) {
						EventWrapper wEvent = new EventWrapper();

						// Calculate event ID
						String sWidgetID = component
								.getFirstValueByName(GUITARConstants.ID_TAG_NAME);

						sWidgetID = sWidgetID.substring(1);

						String sEventID = EVENT_ID_PREFIX + sWidgetID;

						String posFix = (sActionList.size() <= 1) ? ""
								: EVENT_ID_SPLITTER
								+ Integer.toString(sActionList.indexOf(action));
						sEventID = sEventID + posFix;

						wEvent.setID(sEventID);
						wEvent.setAction(action);
						wEvent.setComponent(component);
						//wEvent.setListeners(componentgetValueListByName("ActionListeners"));
						wEventList.add(wEvent);
						eventToIDMap.put(wEvent, "w"+sWidgetID);
					}

				List<ComponentTypeWrapper> wChildren = component.getChildren();
				if (wChildren != null)
					for (ComponentTypeWrapper wChild : wChildren) {
						readEventList(wChild);
					}
			}

			/**
			 * @param wEvent
			 * @return
			 */
			private String getIndexFromWidget(EventWrapper wEvent) {
				// TODO Auto-generated method stub

				String index = wEvent.getComponent().getFirstValueByName(
						GUITARConstants.ID_TAG_NAME);
				index = index.substring(1);

				return index;

			}

			/**
			 * Rip a window
			 * <p>
			 * 
			 * @param gWindow
			 * @return
			 */
			public GUIType
			ripWindow(GWindow gWindow)
			{
				GUITARLog.log.info("------- BEGIN WINDOW -------");
				GUITARLog.log.info("Ripping window: *" + gWindow.getTitle() + "*");

				// 1. Rip special/customized components
				/*		for (GWindowFilter wf : lWindowFilter) {
			if (wf.isProcess(gWindow)) {
				GUITARLog.log.info("Window filter "
						+ wf.getClass().getSimpleName() + " is applied");
				return wf.ripWindow(gWindow);
			}
		}*/

				// 3. Rip all components of this window

				GUIType retGUI = gWindow.extractWindow();
				GComponent gWinContainer = gWindow.getContainer();

				ComponentType container = null;

				// Replace window title with pattern if requested (useReg)
				if (gWinContainer != null) {
					if (this.useReg) {
						AppUtil appUtil = new AppUtil();
						GUITypeWrapper guiTypeWrapper = new GUITypeWrapper(retGUI);
						String sTitle = guiTypeWrapper.getTitle();
						String s = appUtil.findRegexForString(sTitle);
						guiTypeWrapper.setTitle(s);
					}
					container = ripComponent(gWinContainer, gWindow);


					if (container != null) {
						retGUI.getContainer().getContents().getWidgetOrContainer().add(
								container);
					}
				}
				return retGUI;


			}

			/**
			 * Rip a component
			 *
			 * As of now this method does not propagate exceptions.
			 * It needs to be modified to progate exceptions. All callers
			 * need to be modified to handle exceptions.
			 *
			 * <p>
			 * 
			 * @param component
			 * @return
			 */
			public ComponentType ripComponent(GComponent component,
					GWindow window)
			{
				printComponentInfo(component, window);
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
					((SitarWidget)component).setRecorderListener(wEventList,retComp,eventToIDMap,stepList);
					/*
			// 2.1 Try to perform action on the component
			// to reveal more windows/components

			// clear window opened cache before performing actions
			monitor.resetWindowCache();

			if (monitor.isExpandable(component, window)) {
				monitor.expandGUI(component);
         } else {
				GUITARLog.log.info("Component is Unexpandable");
			}

			// Trigger terminal widget

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
						cCloseComp.setWindow(closedWin.extractWindow()
								.getWindow());
						cCloseComp.setComponent(retComp);
						lCloseComp.add(cCloseComp);
						lCloseWindowComp.setFullComponent(lCloseComp);
					} // if
				} // for
			} // if
					 */
					/*			if (monitor.isNewWindowOpened()) {

				List<FullComponentType> lOpenComp = lOpenWindowComps
						.getFullComponent();
				FullComponentType cOpenComp = factory.createFullComponentType();
				cOpenComp.setWindow(window.extractWindow().getWindow());
				cOpenComp.setComponent(retComp);
				lOpenComp.add(cOpenComp);
				lOpenWindowComps.setFullComponent(lOpenComp);

				LinkedList<GWindow> lNewWindows = monitor
						.getOpenedWindowCache();
				monitor.resetWindowCache();
				GUITARLog.log.info(lNewWindows.size()
						+ " new window(s) opened!!!");
				for (GWindow newWins : lNewWindows) {
					GUITARLog.log
							.info("*\t Title:*" + newWins.getTitle() + "*");
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
						compA.addValueByName(
								GUITARConstants.INVOKELIST_TAG_NAME,
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
					 */		
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
							((ContainerType) retComp).getContents()
							.getWidgetOrContainer().add(guiChild);
						}

						if (nChildren < gChildrenList.size()) {
							nChildren = gChildrenList.size();
						}
					}

				} catch (Exception e) {
					if (e.getClass().getName().contains(
							"StaleElementReferenceException")) {
						/**//**
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
					 * We'll return the component we calculated anyway so it
					 * gets added to the GUI map. I'm not entirely sure this
					 * is the right thing to do, but it gets us further anyway.
					 */
					return retComp;
				}

				return retComp;
			}

			/**
			 * Print out debug info for the current component
			 * <p>
			 * 
			 * @param component
			 * @param window
			 */
			private void
			printComponentInfo(GComponent component, GWindow window)
			{
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
				stepList=iStepList;
			}

			public static void terminateRipper() {
				continueRip = false;
			}

} // End of class

