package edu.umd.cs.guitar.replayer.monitor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.data.AttributesType;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.model.data.PropertyType;
import edu.umd.cs.guitar.model.data.StepType;
import edu.umd.cs.guitar.model.wrapper.GUIStructureWrapper;
import edu.umd.cs.guitar.util.GUITARLog;

/** 
 * A {@code StateMonitorFull} without the dependency on Jemmy.
 */
public class SitarStateMonitorFull extends StateMonitorFull {

	/**
	 * Constructor.
	 * @param stateFile path to state file 
	 * @param delay the delay to use
	 */
	public SitarStateMonitorFull(String stateFile, int delay) {
		super(stateFile, delay);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * This method is identical to {@code StateMonitorFull#afterStep(TestStepEndEventArgs)}
	 * except that it does not use Jemmy to wait until the event queue is empty, since 
	 * SWTGuitar uses blocking actions on the GUI.
	 */
	@Override
	public void afterStep(TestStepEndEventArgs eStep) {
		GUITARLog.log.info("Recording GUI state....");

		List<StepType> lSteps = outTestCase.getStep();
		StepType step = eStep.getStep();

		GUIStructure guiState = gApplication.getCurrentState();

		// Check opened window
		windowsAfterStep = gApplication.getCurrentWinID();

		Set<String> windowsNew = new HashSet<String>(windowsAfterStep);

		windowsNew.removeAll(windowsBeforeStep);

		GUIStructureWrapper guiStateAdapter = new GUIStructureWrapper(guiState);
		// guiStateAdapter.generateID(hashcodeGenerator);
		if (windowsNew.size() > 0) {
			GUITARLog.log.info("New window(s) open");
			for (String sID : windowsNew)
				GUITARLog.log.info(sID);
			GUITARLog.log.debug("By component: ");

			List<PropertyType> ID = monitor.selectIDProperties(eStep.component);
			AttributesType signature = factory.createAttributesType();
			signature.setProperty(ID);

			guiStateAdapter.addValueBySignature(signature,
					GUITARConstants.INVOKELIST_TAG_NAME, windowsNew);

		}

		if (idGenerator != null) {
			idGenerator.generateID(guiState);
		} else {
			GUITARLog.log.warn("No ID Generator assigned");
		}

		step.setGUIStructure(guiState);

		lSteps.add(step);
		outTestCase.setStep(lSteps);
		GUITARLog.log.info("DONE");
		GUITARLog.log.info("Dumping out state ... ");
		IO.writeObjToFile(outTestCase, sStateFile);
		GUITARLog.log.info("DONE");
	}


}
