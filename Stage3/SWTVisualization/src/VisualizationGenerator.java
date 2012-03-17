
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.*;

import efg.WidgetId;

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
	static HashMap<WidgetId, Widget> widgetIDs = new HashMap<WidgetId, Widget>();
	static HashMap<Widget,WidgetId> widgetList = new HashMap<Widget,WidgetId>();
	static Display display = new Display();
	static ArrayList<Shell> shellList = new ArrayList<Shell>();
	public static Widget addWidget(HashMap<String, String> properties)
	{
		if(properties.get("Rootwindow") != null)// && properties.get("Rootwindow").equals("true"))
		{
			//rootShell = new Shell();
			//widgets.put(data, rootShell);
			return null;
		}


		String parent = "";
		if(properties.get("data").split(" ").length > 1)
			parent = properties.get("data").split(" ")[1];
		String data = properties.get("data").split(" ")[0];

		//some items may not be ripped correctly and therefore don't have bounds
		if(properties.get("X")==null)
		{
			return null;
		}
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

			addWidgetToMap(data, properties.get("ID"), shell);
			return shell;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.Composite"))
		{
			Composite composite = new Composite((Composite)(widgets.get(parent)), style);
			composite.setBounds(x, y, width, height);
			composite.setToolTipText(ID);
			addWidgetToMap(data, properties.get("ID"), composite);
			return composite;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.Label"))
		{
			Label label = new Label((Composite)(widgets.get(parent)), style);
			label.setBounds(x, y, width, height);
			label.setToolTipText(ID);
			if(properties.get("text") != null)
				label.setText(properties.get("text"));
			addWidgetToMap(data, properties.get("ID"), label);
			return label;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.Button"))
		{
			Button button = new Button((Composite)(widgets.get(parent)), style);
			button.setBounds(x, y, width, height);
			button.setToolTipText(ID);
			if(properties.get("text")==null)
				button.setText("X");
			else
					
				button.setText(properties.get("text"));
			addWidgetToMap(data, properties.get("ID"), button);
			return button;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.Spinner"))
		{
			Spinner spinner = new Spinner((Composite)(widgets.get(parent)), style);
			spinner.setBounds(x, y, width, height);
			spinner.setToolTipText(ID);
			addWidgetToMap(data, properties.get("ID"), spinner);
			return spinner;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.Group"))
		{
			Group group = new Group((Composite)(widgets.get(parent)), style);
			group.setBounds(x, y, width, height);
			group.setToolTipText(ID);

			if(properties.get("text") != null)
				group.setText(properties.get("text"));

			addWidgetToMap(data, properties.get("ID"), group);
			return group;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.Text"))
		{
			Text text = new Text((Composite)(widgets.get(parent)), style);

			text.setBounds(x, y, width, height);
			
			text.setToolTipText(ID);

			if(properties.get("text") != null)
				text.setText(properties.get("text"));

			addWidgetToMap(data, properties.get("ID"), text);
			return text;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.TabFolder"))
		{
			TabFolder folder = new TabFolder((Composite)(widgets.get(parent)), style);

			folder.setBounds(x, y, width, height);
			
			folder.setToolTipText(ID);

			addWidgetToMap(data, properties.get("ID"), folder);
			return folder;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.TreeItem"))
		{
			TreeItem item = new TreeItem((Tree)widgets.get(parent),style);
			item.setText(properties.get("text"));
			
			addWidgetToMap(data, properties.get("ID"),item);
			return item;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.TableItem"))
		{
			TableItem item = new TableItem((Table)widgets.get(parent),style);
			if(properties.get("text")!=null)
				item.setText(properties.get("text"));
			
			addWidgetToMap(data, properties.get("ID"),item);
			return item;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.TabItem"))
		{
			TabItem item = new TabItem((TabFolder)widgets.get(parent),style);
			if(properties.get("text")!=null)
				item.setText(properties.get("text"));
			
			addWidgetToMap(data, properties.get("ID"),item);
			return item;
			
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.MenuItem"))
		{
			MenuItem item = new MenuItem((Menu)widgets.get(parent),style);
			if(properties.get("text")!=null)
				item.setText(properties.get("text"));
			
			addWidgetToMap(data, properties.get("ID"),item);
			return item;
			
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.ToolItem"))
		{
			ToolItem item = new ToolItem((ToolBar)widgets.get(parent),style);
			if(properties.get("text")!=null)
				item.setText(properties.get("text"));
			
			addWidgetToMap(data, properties.get("ID"),item);
			return item;
		}
		else
		{
			Class [] classParm = {Composite.class, int.class};
			Object [] objectParm = {};
			try
			{
				Class cl = Class.forName(properties.get("Class"));
				Constructor co = cl.getConstructor(classParm);
				System.out.println(widgets.get(parent));
				if(widgets.get(parent)==null)
				{
					System.out.println(ID);
					System.out.println(parent);
				}
				Control control = (Control) co.newInstance(widgets.get(parent), style);
				control.setBounds(x, y, width, height);
				control.setToolTipText(ID);
				addWidgetToMap(data, properties.get("ID"), control);
				return control;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
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
	
	private static void addWidgetToMap(String data, String widgetID, Widget widget)
	{
		widgetList.put(widget,new WidgetId(widgetID));
		widgets.put(data, widget);
		widgetIDs.put(new WidgetId(widgetID), widget);
	}
}
