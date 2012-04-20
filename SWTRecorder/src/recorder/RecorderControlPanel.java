package recorder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;
import edu.umd.cs.guitar.ripper.*;


public class RecorderControlPanel {
	
	private static int numTests;
	private static Button startButton, saveButton, discardButton, appButton;
	private static Label testLabel, testNumLabel;
	private static Shell shell;
	private static String targetApp;
	private static Thread thread; //holds target application's execution
	private static ThreadRun threadRunner; //holds target application thread
	
	//================================================================================	
	
	//returns the window of the test case validator
	public static Shell getShell() {
		targetApp = null;
		numTests = 0;

		Display display = Display.getCurrent();
		if(display == null) {
			System.out.println("Can not initialize Control Panel display"); 
			System.exit(0); 
		}
		shell = new Shell(display);
		shell.setText("SITAR Manual Test Recorder");
		shell.setSize(450,155);
	
		RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
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
		
		appButton = new Button(g1, SWT.PUSH);
		appButton.setText("Select App");
		
		testLabel = new Label(g1, SWT.PUSH);
		//whitespace present for future sizing when test case text is present
		testLabel.setText("Target App:                                                                               ");
		
		startButton = new Button(g2, SWT.PUSH);
		startButton.setText("Record Test");
		startButton.setEnabled(false);
	
		discardButton = new Button(g2, SWT.PUSH);
		discardButton.setText("Discard Test");
		discardButton.setEnabled(false);
		
		saveButton = new Button(g2, SWT.PUSH);
		saveButton.setText("Save Test");
		saveButton.setEnabled(false);
		
		testNumLabel = new Label(g2, SWT.PUSH);
		testNumLabel.setText("Tests Saved:  0       "); //space for number to be appended later
		
		
		//When Control Panel is closed, close target app that might beopen
		shell.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event event) {
				if(thread!=null)
					stopTest();
			}
		});
		
		//Choose target app when button "Select App" button clicked
		appButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				chooseApp();
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
		
		return shell;
	}
	
	//================================================================================	

	//Choose target application to record tests for
	public static void chooseApp() {
		
		//If a test is in progress, must discard or save first
		if(discardButton.isEnabled()) {
			
			
		} else {
			targetApp = SWTMultiWindowDynamicApp.class.getName();
			startButton.setEnabled(true);
			testLabel.setText("Target App:  "+targetApp);
			testNumLabel.setText("Tests Saved:  0");
		}
	}
	
	//================================================================================	

	//Contains the target application's recorder and runner for accessibility
	static class ThreadRun implements Runnable {
		SitarRecorder recorder;
		SitarRunner runner;
		
		public void run() {
			SitarRipperConfiguration config = new SitarRipperConfiguration();
			config.setMainClass(targetApp);
			recorder = new SitarRecorder(config);
			runner = new SitarRunner(recorder);
			runner.run();
		}
	}
	
	//Open target application as separate thread
	//Separate thread eliminates problems with multiple Display objects
	public static void startTest() {
		appButton.setEnabled(false);
		startButton.setEnabled(false);
		saveButton.setEnabled(true);
		discardButton.setEnabled(true);
		
		threadRunner =  new ThreadRun();
		thread = new Thread(threadRunner);
		thread.start();
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
		thread.stop();
		Thread.yield();
		thread = null;
	
		appButton.setEnabled(true);
		startButton.setEnabled(true);
		saveButton.setEnabled(false);
		discardButton.setEnabled(false);
	}
	
	//================================================================================	

	public static void main(String[] args) {
		Display d = new Display();
		Shell s = RecorderControlPanel.getShell();
		s.open();

		while(!s.isDisposed()) {
			if(!d.readAndDispatch())
				d.sleep();
		}
		d.dispose();
	}
	
}
