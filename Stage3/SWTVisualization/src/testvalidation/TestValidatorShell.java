package testvalidation;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;
import org.xml.sax.SAXException;

//import edu.umd.cs.guitar.ripper.test.aut.SWTBasicApp;
import efg.EventId;
import efg.WidgetId;
import main.*;

public class TestValidatorShell {

	private static int step; //which current step/event we are on
	private static ArrayList<String> events; //list of eventID strings
	private static Label stepLabel;
	private static Button backButton, nextButton;

	
	//returns the window of the test case validator
	public static Shell getShell(String tstPath) {
		events = TestCaseParser.parseTst(tstPath);
		step = 0;
		
		if(events.size()<1) {
			System.out.println("No events in test case");
			System.exit(0);
		}

		Display display = Display.getCurrent();
		if(display == null) {
			System.out.println("Test Validator cannot retrieve current display"); 
			System.exit(0); 
		}
		Shell shell = new Shell(display);
		shell.setText("Test Validator Controls");
		shell.setSize(275,70);

	//	VisualizationGenerator.shellList.add(shell);

		RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
		rowLayout.marginTop = 5;
		rowLayout.marginBottom = 5;
		rowLayout.marginLeft = 5;
		rowLayout.marginRight = 5;
		rowLayout.spacing = 30;
		shell.setLayout(rowLayout);


		backButton = new Button(shell, SWT.PUSH);
		backButton.setText("< Back");
		//backButton.setLayoutData(new RowData(55,20));
		backButton.setEnabled(false);

		stepLabel = new Label(shell, SWT.LEFT);
		stepLabel.setText("Event  "+(step+1)+"/"+events.size());
		//stepLabel.setLayoutData(new RowData(60,20));

		nextButton = new Button(shell, SWT.PUSH);
		nextButton.setText("Next >");
		//nextButton.setLayoutData(new RowData(55,20));

		color(0);
		if(events.size()<2)
			nextButton.setText("Finish");
		
		
		//Advance step
		nextButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				//If already on last step, close window
				if(nextButton.getText()=="Finish") {
					Display.getCurrent().dispose();
				}
				else {

					step++;
					stepLabel.setText("Event  "+(step+1)+"/"+events.size());
					backButton.setEnabled(true);

					//If now on last step, update next button to read "Finish"
					if(step == events.size()-1) {
						nextButton.setText("Finish");
					}	

					decolor(step-1);
					color(step);
		
				}
			}
		});

		//Go Back a step
		backButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				step--;
				stepLabel.setText("Event  "+(step+1)+"/"+events.size());

				//Disable back button if on step 1 now
				if(step == 0) {
					backButton.setEnabled(false);
				}

				if(nextButton.getText()=="Finish") {
					nextButton.setText("Next >");
				}

				decolor(step+1);
				color(step);
				
			}
		});
		
		return shell;
	}
	
	//removes the coloring for the annotated step
	private static void decolor(int step) {
		WidgetId oldWidgetId = DomParserExample.parsedGraph.getWidgetFromEvent(new EventId(events.get(step)));
		Widget oldWidget = VisualizationGenerator.widgetIDs.get(oldWidgetId);
		DomParserExample.removeColor(oldWidget, Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
	}
	
	//adds coloring to the annotated step
	private static void color(int step) {
		WidgetId newWidgetId = DomParserExample.parsedGraph.getWidgetFromEvent(new EventId(events.get(step)));
		Widget newWidget = VisualizationGenerator.widgetIDs.get(newWidgetId);
		DomParserExample.addColor(newWidget, Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));

	}
}
