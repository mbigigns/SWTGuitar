package recorder;

import java.util.concurrent.Semaphore;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

import edu.umd.cs.guitar.ripper.*;
import edu.umd.cs.guitar.swt.launcher.SitarRipperConfiguration;
import edu.umd.cs.guitar.swt.launcher.SitarRunner;


public class RecorderControlPanel {

	private static int numTests;
	private static Button startButton, saveButton, discardButton, exitButton;
	private static Label testLabel, testNumLabel;
	private static Shell shell;
	private static String targetApp;
	private static Thread thread; //holds target application's execution
	private static ThreadRun threadRunner; //holds target application thread

	//================================================================================	

	//returns the window of the test case validator
	public static Shell getShell(final Display d) {
		numTests = 0;

		final Display display = d;
		if(display == null) {
			System.out.println("Can not initialize Control Panel display"); 
			System.exit(0); 
		}
		boolean shellSet=false;
		
		//keep trying to set the shell until it is available
		while(!shellSet)
		{
			try
			{
				d.syncExec(new Runnable() {
					public void run() {
					shell = new Shell(display);
					
					//handle close event
				    shell.addListener(SWT.Close, new Listener() {
				      public void handleEvent(Event event) {
				    	  exit(d);
				      }
				    });
					
					
					shell.setText("SITAR Manual Test Recorder");
			
					RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
					rowLayout.marginTop = 5;
					rowLayout.marginBottom = 5;
					rowLayout.marginLeft = 5;
					rowLayout.marginRight = 5;
					rowLayout.spacing = 5;
					shell.setLayout(rowLayout);
			
					RowLayout groupLayout = new RowLayout(SWT.HORIZONTAL);
					groupLayout.spacing = 30;
			
					Group g1 = new Group(shell, SWT.PUSH);
					Group g2 = new Group(shell, SWT.PUSH);
					g1.setLayout(groupLayout);
					g2.setLayout(groupLayout);
					g1.setText("Application Controls");
					g2.setText("Recording Controls");
			
					testLabel = new Label(g1, SWT.PUSH);
					//whitespace present for future sizing when test case text is present
					testLabel.setText("Target App: "+targetApp);
			
					startButton = new Button(g2, SWT.PUSH);
					startButton.setText("Record Test");
					startButton.setEnabled(true);
			
					discardButton = new Button(g2, SWT.PUSH);
					discardButton.setText("Discard Test");
					discardButton.setEnabled(false);
			
					saveButton = new Button(g2, SWT.PUSH);
					saveButton.setText("Save Test");
					saveButton.setEnabled(false);
			
					testNumLabel = new Label(g2, SWT.PUSH);
					testNumLabel.setText("Tests Saved:  0       "); //space for number to be appended later
			

					exitButton = new Button(shell, SWT.PUSH);
					exitButton.setText("Exit");
					
			
					//When Control Panel is closed, close target app that might beopen
					shell.addListener(SWT.Close, new Listener() {
						public void handleEvent(Event event) {
							if(thread!=null)
								stopTest();
						}
					});
					
					exitButton.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {
							exit(d);
						}
					});
			
			
					//Start the target app thread when "start" button clicked
					startButton.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {
							startTest();
						}
					});
			
					//stop the target app thread when "discard" button clicked
					discardButton.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {
							stopTest();
							RecorderControlPanel.threadRunner.recorder.clearTest();
						}
					});
			
					//open file dialog, save test, and then close target app when "save" clicked
					saveButton.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {
							String savePath = saveDialog();
			
							//Save and then stop target application
							if(savePath != null) {			
								RecorderControlPanel.threadRunner.recorder.writeTest(savePath);
								numTests++;
								testNumLabel.setText("Tests Saved:  "+numTests);
								stopTest();
							}
						}
					});
					shell.pack();
					}
				});
				shellSet=true;
			}
			catch(NullPointerException e)
			{
				shellSet=false;
			}
		}
		return shell;
	}

	//================================================================================	

	//Choose target application to record tests for
/*	public static void chooseApp() {

		//If a test is in progress, must discard or save first
		if(discardButton.isEnabled()) {


		} else {
			targetApp = SWTMultiWindowDynamicApp.class.getName();
			startButton.setEnabled(true);
			testLabel.setText("Target App:  "+targetApp);
			testNumLabel.setText("Tests Saved:  0");
		}
	}*/

	//================================================================================	

	//Contains the target application's recorder and runner for accessibility
	static class ThreadRun implements Runnable {
		SitarRecorder recorder;
		SitarRunner runner;
		Semaphore controlWaiter;
		public String appName;

		public ThreadRun(String app, Semaphore semaphore) {
			controlWaiter = semaphore;
			appName = app;
			targetApp = appName;
		}

		public void run() {
			SitarRipperConfiguration config = new SitarRipperConfiguration();
			config.setMainClass(appName);
			recorder = new SitarRecorder(config, controlWaiter);
			runner = new SitarRunner(recorder);
			runner.run();
		}

		public void setControlShell(Shell s) {
			recorder.setControlShell(s);
		}
	}

	//Open target application as separate thread
	//Separate thread eliminates problems with multiple Display objects
	public static void startTest() {
		startButton.setEnabled(false);
		saveButton.setEnabled(true);
		discardButton.setEnabled(true);

		
/*		threadRunner =  new ThreadRun();
		thread = new Thread(threadRunner);
		thread.start();*/
		
	}

	//================================================================================	

	//Open save dialog and get path/filename where user wants to save test file
	public static String saveDialog() {
		FileDialog fd = new FileDialog(shell, SWT.SAVE);
		fd.setText("Save test as...");
		fd.setFilterExtensions(new String[]{"*.tst"});
		String path = fd.open();

		return path;
	}

	//================================================================================	

	//End target application thread
	public static void stopTest() {
/*		Thread.yield();
		thread = null;

		appButton.setEnabled(true);*/
		startButton.setEnabled(true);
		saveButton.setEnabled(false);
		discardButton.setEnabled(false);
	}
	
	private static void exit(Display d)
	{
  	  shell.dispose();
  	  for(Shell s:d.getShells())
  		 s.dispose();
  	  threadRunner.recorder.setExit();
  	  threadRunner.recorder.getMonitor().cleanUp();
  	  d.dispose();
  	  System.exit(0);
	}

	//================================================================================	

	public static void main(String[] args) {
		
/*		Runnable runnerWrapper = new Runnable() {
		};*/
		Semaphore controlWaiter = new Semaphore(1);
		try {
			controlWaiter.acquire();
			threadRunner = new ThreadRun(SWTMultiWindowDynamicApp.class.getName(),controlWaiter);
			Thread rippingThread = new Thread(threadRunner);
			rippingThread.start();
			
			Display d=null;
			while(d==null)
				d= Display.findDisplay(rippingThread);
			
			final Display display = d;
			
			controlWaiter.release();
			
			final Shell s = RecorderControlPanel.getShell(d);
			threadRunner.setControlShell(s);
			d.syncExec(new Runnable() {
				public void run() {
					s.open();
				}
			});
			
			//infinite loop here
			while(!s.isDisposed())
				Thread.yield();

		  	  threadRunner.recorder.setExit();
		  	  System.exit(0);

		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
