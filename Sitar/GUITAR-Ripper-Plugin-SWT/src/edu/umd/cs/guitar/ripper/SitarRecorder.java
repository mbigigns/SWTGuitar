/*  
 *  Copyright (c) 2009-@year@. The GUITAR group at the University of Maryland. Names of owners of this group may
 *  be obtained by sending an e-mail to atif@cs.umd.edu
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 *  documentation files (the "Software"), to deal in the Software without restriction, including without 
 *  limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *  the Software, and to permit persons to whom the Software is furnished to do so, subject to the following 
 *  conditions:
 * 
 *  The above copyright notice and this permission notice shall be included in all copies or substantial 
 *  portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 *  LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
 *  EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
/* Copyright (c) 2010
 * Matt Kirn (mattkse@gmail.com) and Alex Loeb (atloeb@gmail.com)
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 *	LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
 *	EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *	IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *	THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
package edu.umd.cs.guitar.ripper;

import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.guitar.model.GIDGenerator;
import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.SitarConstants;
import edu.umd.cs.guitar.model.SitarDefaultIDGenerator;
import edu.umd.cs.guitar.model.data.AttributesType;
import edu.umd.cs.guitar.model.data.ComponentListType;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.FullComponentType;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.model.data.LogWidget;
import edu.umd.cs.guitar.model.data.ObjectFactory;
import edu.umd.cs.guitar.model.data.StepType;
import edu.umd.cs.guitar.model.data.TestCase;
import edu.umd.cs.guitar.model.swtwidgets.SitarWidget;
import edu.umd.cs.guitar.model.wrapper.AttributesTypeWrapper;
import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;
import edu.umd.cs.guitar.ripper.filter.GComponentFilter;
import edu.umd.cs.guitar.ripper.filter.SitarIgnoreWidgetFilter;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * Adapts a {@link Ripper} for use with SWT GUIs.
 * 
 * @author Gabe Gorelick
 * @author <a href="mailto:mattkse@gmail.com"> Matt Kirn </a>
 * @author <a href="mailto:atloeb@gmail.com"> Alex Loeb </a>
 * 
 */
public class SitarRecorder extends SitarExecutor {

	private final SitarRipperMonitor monitor;
	private static SitarRipperConfiguration config= new SitarRipperConfiguration();
	private final RecorderRipper ripper;

	/**
	 * Constructs a new <code>SitarRipper</code>. This constructor is equivalent
	 * to
	 * 
	 * <pre>
	 * SitarRipper(config, Thread.currentThread())
	 * </pre>
	 * 
	 * Consequently, this constructor must be called on the same thread that the
	 * application under test is running on (usually the <code>main</code>
	 * thread).
	 * 
	 * @param config
	 *            configuration
	 * 
	 * @see SitarRunner
	 */

	/**
	 * Constructs a new <code>SitarRipper</code>. The thread passed in is the
	 * thread on which the SWT application under test runs. This is almost
	 * always the <code>main</code> thread (and actually must be the
	 * <code>main</code> thread on Cocoa).
	 * 
	 * @param config
	 *            configuration
	 * @param config2
	 *            thread the GUI runs on
	 * 
	 * @see SitarRunner
	 */
	public SitarRecorder(SitarRipperConfiguration config) {
		super(config);
		this.config=config;
		monitor = new SitarRipperMonitor(config, getApplication());
		SitarWidget.setTerminate(false);
		ripper = initRipper();
	}
	static ObjectFactory factory = new ObjectFactory();
	List<StepType> iStepList = new ArrayList<StepType>();
	
	
	// initialize the ripper
	private RecorderRipper initRipper() {
		RecorderRipper ripper = new RecorderRipper(this.getApplication());
				
		ripper.setMonitor(monitor);
		
		GIDGenerator idGenerator = SitarDefaultIDGenerator.getInstance();
		ripper.setIDGenerator(idGenerator);
		ripper.setTestCase(iStepList);
		return ripper;
	}
	
	/**
	 * Execute the ripper.
	 * 
	 * @see Ripper#execute()
	 */
	@Override
	protected void onExecute() {
		try {
			ripper.execute();			
		} catch (Exception e) {
			GUITARLog.log.error("SitarRipper: ", e);
		}
	}
	
	/**
	 * Log the results of ripping.
	 */
	@Override
	protected void onAfterExecute() {
		
		GUIStructure dGUIStructure = ripper.getResult();
		IO.writeObjToFile(dGUIStructure, config.getGuiFile());

		GUITARLog.log.info("-----------------------------");
		GUITARLog.log.info("OUTPUT SUMARY: ");
		GUITARLog.log.info("Number of Windows: "
				+ dGUIStructure.getGUI().size());
		GUITARLog.log.info("GUI file:" + config.getGuiFile());
		GUITARLog.log.info("Open Component file:"
				+ config.getLogWidgetFile());
		ComponentListType lOpenWins = ripper.getlOpenWindowComps();
		ComponentListType lCloseWins = ripper.getlCloseWindowComp();
		ObjectFactory factory = new ObjectFactory();

		LogWidget logWidget = factory.createLogWidget();
		logWidget.setOpenWindow(lOpenWins);
		logWidget.setCloseWindow(lCloseWins);

		IO.writeObjToFile(logWidget, config.getLogWidgetFile());

		// print time elapsed
		super.onAfterExecute();
	}
	
	
	//For use by RecorderControlPanel to save manually-recoreded .tst files
	public void writeTest(String path) {
		TestCase oTestCase = factory.createTestCase();
		oTestCase.setStep(iStepList);
		IO.writeObjToFile(oTestCase, path);
		IO.writeObjToFile(this.ripper.efg, path.substring(0, path.length()-3)+"EFG");
		IO.writeObjToFile(this.ripper.dGUIStructure, path.substring(0, path.length()-3)+"GUI");
	}

	
	/**
	 * Get the {@code SitarRipperMonitor} used by this {@code SitarRipper}.
	 * 
	 * @return the monitor used by this ripper
	 */
	@Override
	public SitarRipperMonitor getMonitor() {
		return monitor;
	}
	
	public int getNumEvents() {
		return iStepList.size();
	}

}
