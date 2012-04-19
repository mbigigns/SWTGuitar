package recorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.umd.cs.guitar.ripper.*;


public class RecorderControlPanel extends JPanel {
	
	private int numTests;
	private JButton startAppButton, saveTestsButton, discardTestsButton, selectAppButton;
	private JLabel selectedAppLabel, numTestsLabel;

	private String targetApp;
	private Thread thread; //holds target application's execution
	private ThreadRun threadRunner; //holds target application thread
	
	//================================================================================	
	
	public RecorderControlPanel() {
		super();
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		initComponents();
		
		JLabel targetAppIndicator = new JLabel("Target App:");
		JLabel numTestsIndicator = new JLabel("Tests Saved:");
		
		JPanel topHalf = new JPanel();
		topHalf.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		topHalf.setBorder(BorderFactory.createTitledBorder("Application Controls"));
		
		topHalf.add(selectAppButton);
		topHalf.add(targetAppIndicator);
		topHalf.add(selectedAppLabel);
		
		JPanel bottomHalf = new JPanel();
		bottomHalf.setLayout(new FlowLayout());
		bottomHalf.setBorder(BorderFactory.createTitledBorder("Recording Controls"));
		
		bottomHalf.add(startAppButton);
		bottomHalf.add(saveTestsButton);
		bottomHalf.add(discardTestsButton);
		bottomHalf.add(numTestsIndicator);
		bottomHalf.add(numTestsLabel);
		
		this.add(topHalf);
		this.add(bottomHalf);
	}
	
	private void initComponents() {
		selectAppButton = new JButton("Select App");
		selectAppButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedAppLabel.setFont(selectedAppLabel.getFont().deriveFont(Font.PLAIN));
				chooseApp();
				startAppButton.setEnabled(true);
				((JFrame)RecorderControlPanel.this.getRootPane().getParent()).pack();
			}
		});
		
		startAppButton = new JButton("Record Test");
		startAppButton.setEnabled(false);
		startAppButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startTest();
				saveTestsButton.setEnabled(true);
				discardTestsButton.setEnabled(true);
			}
		});
		
		saveTestsButton = new JButton("Save Tests");
		saveTestsButton.setEnabled(false);
		
		discardTestsButton = new JButton("Discard Tests");
		discardTestsButton.setEnabled(false);
		
		selectedAppLabel = new JLabel("none selected");
		selectedAppLabel.setFont(selectedAppLabel.getFont().deriveFont(Font.ITALIC));
		
		numTestsLabel = new JLabel("0");
	}
	
	public void setNumTests(int num) {
		numTestsLabel.setText(Integer.toString(num));
	}
	
	//returns the window of the test case validator
	/*
	public static Shell getShell() {		
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
	}*/
	
	//================================================================================	

	//Choose target application to record tests for
	public void chooseApp() {
		
		//If a test is in progress, must discard or save first
		if(discardTestsButton.isEnabled()) {
			
			
		} else {
			targetApp = SWTMultiWindowDynamicApp.class.getName();
			startAppButton.setEnabled(true);
			selectedAppLabel.setText(targetApp);
			numTestsLabel.setText("0");
		}
	}
	
	//================================================================================	

	//Contains the target application's recorder and runner for accessibility
	class ThreadRun implements Runnable {
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
	public void startTest() {		
		threadRunner =  new ThreadRun();
		thread = new Thread(threadRunner);
		thread.start();
	}
	
	//================================================================================	
	
	//Open save dialog and get path/filename where user wants to save test file
	public File saveDialog() {
		JFileChooser fd = new JFileChooser(System.getProperty("user.dir"));
		
		fd.setFileFilter(new FileNameExtensionFilter("tst"));
		fd.setDialogType(JFileChooser.SAVE_DIALOG);
		
		while (JFileChooser.APPROVE_OPTION != fd.showSaveDialog(this.getRootPane()));
		
		return fd.getSelectedFile();
	}

	//================================================================================	

	//End target application thread
	public void stopTest() {
		thread.stop();
		Thread.yield();
		thread = null;
	
		selectAppButton.setEnabled(true);
		startAppButton.setEnabled(true);
		saveTestsButton.setEnabled(false);
		discardTestsButton.setEnabled(false);
	}
	
	//================================================================================	

	public static void main(String[] args) {
		JFrame mainWindow = new JFrame("SITAR Manual Test Recorder");
		mainWindow.add(new RecorderControlPanel());
		mainWindow.pack();
		mainWindow.setVisible(true);
	}
	
}
