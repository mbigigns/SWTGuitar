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

import org.kohsuke.args4j.Option;

import edu.umd.cs.guitar.ripper.SitarConfiguration;

/**
 * Configuration specific to {@link SitarReplayer}. The configuration options
 * held by this class can be set through its setter methods or by passing
 * and instance of this class to an Args4j {@code CmdLineParser}.
 * 
 * @author Gabe Gorelick
 * 
 * @see SitarReplayerMain
 */
public class SitarReplayerConfiguration extends SitarConfiguration {

	// GUITAR runtime parameters	
	@Option(name = "-e", usage = "EFG file path", aliases = "--efg-file", required = true)
	private String efgFile = null;

	// this option is slightly different from ripper's
	@Option(name = "-g", usage = "GUI file path", aliases = "--gui-file", required = true)
	private String guiFile = "GUITAR-Default.GUI"; 
	
	@Option(name = "-t", usage = "testcase file path", aliases = "--testcase-file", required = true)
	private String testcase = null;

	@Option(name = "-gs", usage = "gui state file path", aliases = "--gui-state")
	private String guiStateFile = "GUITAR-Default.STA";

	@Option(name = "-d", usage = "step delay time", aliases = "--delay")
	private int delay = 0;

	@Option(name = "-to", usage = "testcase timeout", aliases = "--testcase-timeout")
	private int testCaseTimeout = 30000;

	@Option(name = "-so", usage = "test steptimeout", aliases = "--teststep-timeout")
	private int testStepTimeout = 4000;
	
	// Application Under Test
	
	@Option(name = "-p", usage = "Pause after each step", aliases = "--pause")
	private boolean pause = false;
	
	@Option(name = "-r", usage = "Compare string using regular expression", aliases = "--regular-expression")
	private boolean regUsed= false;
	
	// getters and setters

	/**
	 * Get the path to the GUI structure file to use.
	 * 
	 * @return path to GUI structure file
	 * @see #setGuiFile(String)
	 */
	public String getGuiFile() {
		return guiFile;
	}
	
	/**
	 * Set the GUI structure file to use.
	 * 
	 * @param guiFile
	 *            path to GUI structure file
	 * @see #getGuiFile()
	 */
	public void setGuiFile(String guiFile) {
		this.guiFile = guiFile;
	}

	/**
	 * Get the path of the EFG file to use.
	 * 
	 * @return path to EFG file
	 * @see #setEfgFile(String)
	 */
	public String getEfgFile() {
		return efgFile;
	}

	/**
	 * Set the path of the EFG file to use.
	 * 
	 * @param efgFile
	 *            path to EFG file
	 */
	public void setEfgFile(String efgFile) {
		this.efgFile = efgFile;
	}

	/**
	 * Get the path of the test case to replay.
	 * 
	 * @return path to test case
	 * @see #setTestcase(String)
	 */
	public String getTestcase() { // TODO rename getTestCase
		return testcase;
	}

	/**
	 * Set the path of the test case to replay.
	 * 
	 * @param testCase
	 *            test case to replay
	 * @see #getTestcase()
	 */
	public void setTestcase(String testCase) { // TODO rename getTestCase
		this.testcase = testCase;
	}

	/**
	 * Get the path to the GUI state file.
	 * 
	 * @return path to GUI state file
	 * @see #setGuiStateFile(String)
	 */
	public String getGuiStateFile() {
		return guiStateFile;
	}

	/**
	 * Set the path of the GUI state file to use.
	 * 
	 * @param guiStateFile
	 *            state file to use
	 * @see #getGuiStateFile()
	 */
	public void setGuiStateFile(String guiStateFile) {
		this.guiStateFile = guiStateFile;
	}

	/**
	 * Get the delay used between test steps.
	 * 
	 * @return delay in milliseconds
	 * @see #setDelay(int)
	 */
	public int getDelay() {
		return delay;
	}

	/**
	 * Set the delay used between test steps.
	 * 
	 * @param delay
	 *            the delay in milliseconds
	 * @see #getDelay()
	 */
	public void setDelay(int delay) {
		this.delay = delay;
	}

	/**
	 * Get the amount of time to wait for a test case to complete.
	 * 
	 * @return maximum time to wait (in milliseconds)
	 * @see #setTestStepTimeout(int)
	 */
	public int getTestCaseTimeout() {
		return testCaseTimeout;
	}

	/**
	 * Set the amount of time to wait for a test case to complete.
	 * 
	 * @param testCaseTimeout
	 *            maximum time to wait (in milliseconds)
	 * @see #getTestCaseTimeout()
	 */
	public void setTestCaseTimeout(int testCaseTimeout) {
		this.testCaseTimeout = testCaseTimeout;
	}

	/**
	 * Get the amount of time to wait for a test step to complete.
	 * 
	 * @return amount of time (in milliseconds)
	 * @see #setTestStepTimeout(int)
	 */
	public int getTestStepTimeout() {
		return testStepTimeout;
	}

	/**
	 * Set the amount of time to wait for a test step to complete.
	 * 
	 * @param testStepTimeout
	 *            amount of time (in milliseconds)
	 * @see #getTestStepTimeout()
	 */
	public void setTestStepTimeout(int testStepTimeout) {
		this.testStepTimeout = testStepTimeout;
	}

	/**
	 * Returns whether to utilize pausing or not.
	 * 
	 * @return {@code true} if using pausing
	 * @see #setPause(boolean)
	 */
	public boolean getPause() {
		return pause;
	}

	/**
	 * Set whether to utilize pausing.
	 * 
	 * @param pause
	 *            {@code true} to use pausing
	 * @see #getPause()
	 */
	public void setPause(boolean pause) {
		this.pause = pause;
	}

	/**
	 * Returns whether regular expression matching should be used.
	 * 
	 * @return {@code true} if is used
	 * @see #setRegUsed(boolean)
	 */
	public boolean getRegUsed() {
		return regUsed;
	}

	/**
	 * Set whether regular expressions matching should be used.
	 * 
	 * @param regUsed
	 *            {@code true} if should use
	 * @see #getRegUsed()
	 */
	public void setRegUsed(boolean regUsed) {
		this.regUsed = regUsed;
	}	

}
