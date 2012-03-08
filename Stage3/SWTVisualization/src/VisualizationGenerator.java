
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

public class VisualizationGenerator {
	/*	public Shell rootWindow = readWindow();
	public void readGUI(Display display)
	{
		readComponent(rootWindow);

        while (!rootWindow.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
              }
        }
	}

	private Shell readWindow()
	{
		Shell window = createWindow("",0,0,307,302);
        window.setSize(307, 302);
        window.setLocation(300, 300);
        window.open();
        return window;
	}

	private void readComponent(Composite window)
	{	
		//addWidget("org.eclipse.swt.widgets.Button","",0,0,20,20,0,window);
	}

	public Shell createWindow(String ID, int x, int y, int width, int height)
	{
		Shell window = new Shell();
		return window;
	}

	 */

	static HashMap<String, Widget> widgets = new HashMap<String, Widget>();
	static Display display = new Display();
	static ArrayList<Shell> shellList = new ArrayList<Shell>();
	public static void addWidget(HashMap<String, String> properties)
	{
		if(properties.get("Rootwindow") != null)// && properties.get("Rootwindow").equals("true"))
		{
			//rootShell = new Shell();
			//widgets.put(data, rootShell);
			return;
		}


		String parent = "";
		if(properties.get("data").split(" ").length > 1)
			parent = properties.get("data").split(" ")[1];
		String data = properties.get("data").split(" ")[0];


		//System.out.println(widgets);

		int x = Integer.parseInt(properties.get("X"));
		int y = Integer.parseInt(properties.get("Y"));
		int width = Integer.parseInt(properties.get("width"));
		int height = Integer.parseInt(properties.get("height"));

		if(widgets.get(parent) != null)
		{
			Rectangle parentBounds = ((Composite)(widgets.get(parent))).getBounds();
			//x = x - parentBounds.x;
			//y = y - parentBounds.y;
		}

		String ID = properties.get("ID");
		int style = Integer.parseInt(properties.get("style"));

		
		if(properties.get("Class").equals("org.eclipse.swt.widgets.Shell"))
		{
			Shell shell;
			if(parent.equals(""))
			{
				shell = new Shell(display);
				shellList.add(shell);
			}
			else
				shell = new Shell((Shell)(widgets.get(parent)), style);

			shell.setBounds(x, y, width, height);

			if(properties.get("text") != null)
				shell.setText(properties.get("text"));

			//shell.open();

			widgets.put(data, shell);
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.Composite"))
		{
			Composite composite = new Composite((Composite)(widgets.get(parent)), style);
			composite.setBounds(x, y, width, height);
			composite.setToolTipText(ID);
			widgets.put(data, composite);
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.Label"))
		{
			Label label = new Label((Composite)(widgets.get(parent)), style);
			label.setBounds(x, y, width, height);
			label.setToolTipText(ID);
			if(properties.get("text") != null)
				label.setText(properties.get("text"));
			widgets.put(data, label);
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.Button"))
		{
			Button button = new Button((Composite)(widgets.get(parent)), style);
			button.setBounds(x, y, width, height);
			button.setToolTipText(ID);
			button.setText(properties.get("text"));
			widgets.put(data, button);
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.Spinner"))
		{
			Spinner spinner = new Spinner((Composite)(widgets.get(parent)), style);
			spinner.setBounds(x, y, width, height);
			spinner.setToolTipText(ID);
			widgets.put(data, spinner);
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.Group"))
		{
			Group group = new Group((Composite)(widgets.get(parent)), style);
			group.setBounds(x, y, width, height);
			group.setToolTipText(ID);

			if(properties.get("text") != null)
				group.setText(properties.get("text"));

			widgets.put(data, group);
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.Text"))
		{
			Text text = new Text((Composite)(widgets.get(parent)), style);

			text.setBounds(x, y, width, height);
			
			text.setToolTipText(ID);

			if(properties.get("text") != null)
				text.setText(properties.get("text"));

			widgets.put(data, text);
		}

	}

	public static void Show()
	{
		for (int i = 0; i < shellList.size(); i++) 
			shellList.get(i).open();
		
		for (int i = 0; i < shellList.size(); i++) 
		{
			Shell currentShell = shellList.get(i);
			while (!currentShell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}

		}

		display.dispose();
	}
}
