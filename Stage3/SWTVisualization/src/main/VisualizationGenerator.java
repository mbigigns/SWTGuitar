package main;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

import efg.WidgetId;

public class VisualizationGenerator {
	
	//maps widget sequential counter to widget
	static HashMap<String, Widget> widgets = new HashMap<String, Widget>();
	
	//maps widget IDs to widgets
	public static HashMap<WidgetId, Widget> widgetIDs = new HashMap<WidgetId, Widget>();
	
	//maps widgets to their widget IDs
	static HashMap<Widget,WidgetId> widgetList = new HashMap<Widget,WidgetId>();
	public static Display display = new Display();
	static ArrayList<Shell> shellList = new ArrayList<Shell>();
	
	//creates a widget based on the given properties
	public static Widget addWidget(HashMap<String, String> properties)
	{
		if(properties.get("Rootwindow") != null || properties.get("data")==null)
		{
			return null;
		}

		//extracts the ID of the parent based on a sequential counting system
		String parent = "";
		if(properties.get("data").split(" ").length > 1)
			parent = properties.get("data").split(" ")[1];
		String data = properties.get("data").split(" ")[0];
		
		int x=0;
		int y=0;
		int width=0;
		int height=0;
		
		if(properties.get("X")!=null)
			x = Integer.parseInt(properties.get("X"));

		if(properties.get("Y")!=null)
			y = Integer.parseInt(properties.get("Y"));

		if(properties.get("width")!=null)
			width = Integer.parseInt(properties.get("width"));

		if(properties.get("height")!=null)
			height = Integer.parseInt(properties.get("height"));

		//fixes issues where we have 0 height or 0 width properties temporarily by setting them as squares.  This is to adjust
		//for incorrect rendering
		if(width == 0 && height !=0)
			width = height;
		else if(width !=0 && height ==0)
			height = width;
		
		//grabs the bounds of the parents to help calculate relative sizing
		if(widgets.get(parent) != null && widgets.get(parent) instanceof Composite)
		{
			Rectangle parentBounds = ((Composite)(widgets.get(parent))).getBounds();
			//x = x - parentBounds.x;
			//y = y - parentBounds.y;
		}

		String ID = properties.get("ID");
		int style = Integer.parseInt(properties.get("style"));

		//handles and creates widget based on the type of class listed in the GUI
		if(properties.get("Class").equals("org.eclipse.swt.widgets.Shell"))
		{
			Shell shell;
			if(parent.equals(""))
			{
				shell = new Shell(display,style);
				shellList.add(shell);
			}
			else
				shell = new Shell((Shell)(widgets.get(parent)), style);

			shell.setBounds(x, y, width, height);

			if(properties.get("text") != null)
				shell.setText(properties.get("text"));
			
			shell.setToolTipText(ID);
			//shell.open();

			addWidgetToMap(data, properties.get("ID"), shell, parent);
			return shell;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.custom.ScrolledComposite"))
		{
			ScrolledComposite composite = new ScrolledComposite((Composite)(widgets.get(parent)),style|SWT.H_SCROLL | SWT.V_SCROLL);
			
			if(!(x==0&&y==0&&width==0&&height==0))
				composite.setBounds(x, y, width, height);
			else if(properties.get("layout")!=null)
			{
				composite.pack();
				composite.setLayout(parseLayout(properties.get("layout")));
			}
			else
			{
				FillLayout fillLayout = new FillLayout();
				fillLayout.type = SWT.VERTICAL;
				composite.setLayout(fillLayout);
				composite.pack();
			}
			composite.setToolTipText(ID);
			addWidgetToMap(data, properties.get("ID"), composite, parent);
			return composite;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.Composite"))
		{
			Composite composite = new Composite((Composite)(widgets.get(parent)), style);
			
			if(!(x==0&&y==0&&width==0&&height==0))
				composite.setBounds(x, y, width, height);
			else if(properties.get("layout")!=null)
			{
				composite.pack();
				composite.setLayout(parseLayout(properties.get("layout")));
			}
			else
			{
				FillLayout fillLayout = new FillLayout();
				fillLayout.type = SWT.VERTICAL;
				composite.setLayout(fillLayout);
				composite.pack();
			}
			composite.setToolTipText(ID);
			addWidgetToMap(data, properties.get("ID"), composite, parent);
			return composite;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.Label"))
		{
			Label label = new Label((Composite)(widgets.get(parent)), style);
			if(!(x==0&&y==0&&width==0&&height==0))
				label.setBounds(x, y, width, height);
			else
				label.pack();
			label.setToolTipText(ID);
			if(properties.get("text") != null)
				label.setText(properties.get("text"));
			else
				label.setText("X");
			addWidgetToMap(data, properties.get("ID"), label, parent);
			return label;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.Button"))
		{
			Button button = new Button((Composite)(widgets.get(parent)), style);
			if(!(x==0&&y==0&&width==0&&height==0))
				button.setBounds(x, y, width, height);
			else
				button.pack();
			button.setToolTipText(ID);
			if(properties.get("text")==null)
				button.setText("X");
			else
				button.setText(properties.get("text"));
			addWidgetToMap(data, properties.get("ID"), button, parent);
			return button;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.Spinner"))
		{
			Spinner spinner = new Spinner((Composite)(widgets.get(parent)), style);
			if(!(x==0&&y==0&&width==0&&height==0))
				spinner.setBounds(x, y, width, height);
			else
				spinner.pack();
			spinner.setToolTipText(ID);
			addWidgetToMap(data, properties.get("ID"), spinner, parent);
			return spinner;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.Link"))
		{
			Link control = new Link((Composite)(widgets.get(parent)), style);
			if(!(x==0&&y==0&&width==0&&height==0))
				control.setBounds(x, y, width, height);
			control.setToolTipText(ID);
			if(properties.get("text") != null)
				control.setText(properties.get("text"));
			else
				control.setText("X");
			addWidgetToMap(data, properties.get("ID"), control, parent);
			return control;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.Group"))
		{
			Group group = new Group((Composite)(widgets.get(parent)), style);
			if(!(x==0&&y==0&&width==0&&height==0))
			{
				group.setBounds(x, y, width, height);
			}
			else if(properties.get("layout")!=null)
			{
/*				if(properties.get("text") != null)
					System.out.println(properties.get("text"));*/
				group.setLayout(parseLayout(properties.get("layout")));
			}
			else
			{
				FillLayout fillLayout = new FillLayout();
				fillLayout.type = SWT.HORIZONTAL;
				group.setLayout(fillLayout);
			}
			group.setToolTipText(ID);

			if(properties.get("text") != null)
				group.setText(properties.get("text"));

			addWidgetToMap(data, properties.get("ID"), group, parent);
			return group;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.Text"))
		{
			Text text = new Text((Composite)(widgets.get(parent)), style);

			if(!(x==0&&y==0&&width==0&&height==0))
				text.setBounds(x, y, width, height);
			else
				text.pack();
			
			text.setToolTipText(ID);

			if(properties.get("text") != null)
				text.setText(properties.get("text"));

			addWidgetToMap(data, properties.get("ID"), text, parent);
			return text;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.TabFolder"))
		{
			TabFolder folder = new TabFolder((Composite)(widgets.get(parent)), style);

			if(!(x==0&&y==0&&width==0&&height==0))
				folder.setBounds(x, y, width, height);
			else
				folder.pack();
			
			folder.setToolTipText(ID);

			addWidgetToMap(data, properties.get("ID"), folder, parent);
			return folder;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.ToolBar"))
		{
			ToolBar toolbar = new ToolBar((Composite)(widgets.get(parent)), style);
			
			if(!(x==0&&y==0&&width==0&&height==0))
				toolbar.setBounds(x, y, width, height);
			else
				toolbar.pack();
			
			toolbar.setToolTipText(ID);

			addWidgetToMap(data, properties.get("ID"), toolbar, parent);
			return toolbar;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.TreeItem"))
		{
			TreeItem item = new TreeItem((Tree)widgets.get(parent),style);
			if(properties.get("text")!=null)
				item.setText(properties.get("text"));
			else
				item.setText("X");
			
			item.setText(item.getText()+" "+ID);
			
			addWidgetToMap(data, properties.get("ID"),item, parent);
			return item;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.TableItem"))
		{
			TableItem item = new TableItem((Table)widgets.get(parent),style);
			if(properties.get("text")!=null)
				item.setText(properties.get("text"));
			else
				item.setText("X");
			
			item.setText(item.getText()+" "+ID);
			
			addWidgetToMap(data, properties.get("ID"),item, parent);
			return item;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.TableColumn"))
		{
			TableColumn item = new TableColumn((Table)widgets.get(parent),style);
			if(properties.get("text")!=null)
			{
				item.setText(properties.get("text"));
				((Table)widgets.get(parent)).setHeaderVisible(true);
			}
			item.setToolTipText(ID);
			
			//TODO: set the width properly
			item.pack();
			item.setWidth(width);
			addWidgetToMap(data, properties.get("ID"),item, parent);
			return item;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.Table"))
		{
		    Table table = new Table((Composite)widgets.get(parent), style);
		    table.setLinesVisible(true);
		    table.setToolTipText(ID);
		    //table.setHeaderVisible(true);

			if(!(x==0&&y==0&&width==0&&height==0))
		    table.setBounds(x, y, width, height);
			addWidgetToMap(data, properties.get("ID"),table, parent);
			return table;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.TabItem"))
		{
			TabItem item = new TabItem((TabFolder)widgets.get(parent),style);
			if(properties.get("text")!=null)
				item.setText(properties.get("text"));
			else
				item.setText("X");
			
			item.setToolTipText(ID);
			
			addWidgetToMap(data, properties.get("ID"),item, parent);
			return item;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.MenuItem"))
		{
			//System.out.println(properties.get("ID"));
			//System.out.println(widgets.get(parent));
			MenuItem item = new MenuItem((Menu)widgets.get(parent),style);
			if(properties.get("text")!=null)
				item.setText(properties.get("text"));
			else
				item.setText("X");
			
			item.setText(item.getText()+" "+ID);
			
			addWidgetToMap(data, properties.get("ID"),item, parent);
			return item;
			
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.ToolItem"))
		{	
			Composite toolbarParent = ((ToolBar)widgets.get(parent)).getParent();
			Text item;
			if(toolbarParent!=null && toolbarParent.getLayout()!=null)
			{
				toolbarParent.setLayout(new RowLayout(SWT.HORIZONTAL));
				item = new Text(toolbarParent,SWT.BORDER);
			}
			else
				item = new Text((ToolBar)widgets.get(parent),SWT.BORDER);
			item.setBounds(x,y,width,height);
			if(properties.get("text")!=null)
				item.setText(properties.get("text"));
			else
				item.setText("X");
			
			item.setToolTipText(ID);
			addWidgetToMap(data, properties.get("ID"),item, parent);
			
			return item;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.Menu"))
		{
			if(widgets.get(parent) instanceof Shell)
			{
				Menu menubar = new Menu((Shell)widgets.get(parent),style-33554432);
					
				//System.out.println(widgets.get(parent));
				
				if(style-33554432 == SWT.BAR || style == SWT.BAR)
					((Shell)widgets.get(parent)).setMenuBar(menubar);
				else if (widgets.get(parent) instanceof Control && (style-33554432 == SWT.POP_UP || style == SWT.POP_UP))
					((Control)widgets.get(parent)).setMenu(menubar);
				
				addWidgetToMap(data, properties.get("ID"),menubar, parent);
				return menubar;
			}
			else if(widgets.get(parent) instanceof MenuItem)
			{
				Menu menu = new Menu((MenuItem)widgets.get(parent));
				((MenuItem)widgets.get(parent)).setMenu(menu);
				
				addWidgetToMap(data, properties.get("ID"),menu, parent);
				return menu;
			}
			else
			{
				System.out.println("DEBUG: Unaccounted Menu Type");
				//System.out.println("Yay we have others");
			}
		}
		else if(properties.get("Class").equals("org.eclipse.swt.browser.Browser"))
		{
			Group browser = new Group((Composite)widgets.get(parent),SWT.BORDER);
			browser.setBounds(x,y,width,height);
			browser.setText("Browser");
			browser.setToolTipText(ID);
			addWidgetToMap(data, properties.get("ID"),browser, parent);
			return browser;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.browser.WebSite"))
		{
			Group browser = new Group((Composite)widgets.get(parent),SWT.BORDER);
			browser.setBounds(x,y,width,height);
			browser.setText("Website");
			browser.setToolTipText(ID);
			addWidgetToMap(data, properties.get("ID"),browser, parent);
			return browser;
		}
		else
		{
			//otherwise do not handle the widget specially
			Class [] classParm = {Composite.class, int.class};
			
			boolean customControlFlag = false;
			//creates a widget based on the class name as specified in the GUI file
			try
			{
				Class cl = Class.forName(properties.get("Class"));
				Constructor co = cl.getConstructor(classParm);
				//System.out.println(widgets.get(parent));
				Control control = (Control) co.newInstance(widgets.get(parent), style);
				if(!(x==0&&y==0&&width==0&&height==0))
					control.setBounds(x, y, width, height);
				else
					control.pack();
				control.setToolTipText(ID);
				addWidgetToMap(data, properties.get("ID"), control, parent);
				return control;
			}
			catch (Exception e) {
				System.out.println("DEBUG: Control not found in SWT library");
				customControlFlag = true;
			}
			
			//handles controls that are not included in the SWT library, which occurs when it throws an exception in the earlier try/catch
			//creates a group with the proper name as a placeholder for this type of control
			if(customControlFlag)
			{
				Group customControl = new Group((Composite)widgets.get(parent),SWT.BORDER);
				customControl.setBounds(x,y,width,height);
				customControl.setText(properties.get("Class"));
				customControl.setToolTipText(ID);
				addWidgetToMap(data, properties.get("ID"),customControl, parent);
				return customControl;
			}
		}
		return null;
	}

	//shows the correct initial shell windows
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
	
	//adds a specific widget with the widgetID to the helper maps
	private static void addWidgetToMap(String data, String widgetID, Widget widget, String parent)
	{
		widgetList.put(widget,new WidgetId(widgetID));
		widgets.put(data, widget);
		widgetIDs.put(new WidgetId(widgetID), widget);

		//checks whether widget needs to be added to a ScrolledComposite and sets the 
		//scrolledcomposite's content accordingly
		if(widgets.get(parent) instanceof ScrolledComposite && widget instanceof Control)
		{
			((ScrolledComposite)(widgets.get(parent))).setContent((Control)widget);
		}
	}
	
	//reads in the layout attribute and creates a layout based on the data included,
	//handles GridLayout, FillLayout, FormLayout, and RowLayout
	private static Layout parseLayout(String layoutFormat)
	{
		if(layoutFormat.contains("GridLayout"))
		{     
			GridLayout layout = new GridLayout();
			layout.numColumns=1;
			Pattern p;
			Matcher m;
			
			p = Pattern.compile(".*marginWidth=([0-9]*).*");
			m = p.matcher(layoutFormat);
			if(m.matches())
			{
				layout.marginWidth = Integer.parseInt(m.group(1));
			}
			
			p = Pattern.compile(".*marginBottom=([0-9]*).*");
			m = p.matcher(layoutFormat);
			if(m.matches())
			{
				layout.marginBottom = Integer.parseInt(m.group(1));
			}
			
			p = Pattern.compile(".*marginHeight=([0-9]*).*");
			m = p.matcher(layoutFormat);
			if(m.matches())
			{
				layout.marginHeight = Integer.parseInt(m.group(1));
			}
			
			p = Pattern.compile(".*marginLeft=([0-9]*).*");
			m = p.matcher(layoutFormat);
			if(m.matches())
			{
				layout.marginLeft = Integer.parseInt(m.group(1));
			}
			
			p = Pattern.compile(".*marginRight=([0-9]*).*");
			m = p.matcher(layoutFormat);
			if(m.matches())
			{
				layout.marginRight = Integer.parseInt(m.group(1));
			}
			
			p = Pattern.compile(".*marginTop=([0-9]*).*");
			m = p.matcher(layoutFormat);
			if(m.matches())
			{
				layout.marginTop = Integer.parseInt(m.group(1));
			}
			
			p = Pattern.compile(".*numColumns=([0-9]*).*");
			m = p.matcher(layoutFormat);
			if(m.matches())
			{
				layout.numColumns = Integer.parseInt(m.group(1));
			}
			
			p = Pattern.compile(".*verticalSpacing=([0-9]*).*");
			m = p.matcher(layoutFormat);
			if(m.matches())
			{
				layout.verticalSpacing = Integer.parseInt(m.group(1));
			}
			
			p = Pattern.compile(".*horizontalSpacing=([0-9]*).*");
			m = p.matcher(layoutFormat);
			if(m.matches())
			{
				layout.horizontalSpacing = Integer.parseInt(m.group(1));
			}
			return layout;
		}
		else if(layoutFormat.contains("FillLayout"))
		{
			FillLayout layout = new FillLayout();
			Pattern p;
			Matcher m;
			
			p = Pattern.compile(".*marginWidth=([0-9]*).*");
			m = p.matcher(layoutFormat);
			if(m.matches())
			{
				layout.marginWidth = Integer.parseInt(m.group(1));
			}
			
			
			p = Pattern.compile(".*marginHeight=([0-9]*).*");
			m = p.matcher(layoutFormat);
			if(m.matches())
			{
				layout.marginHeight = Integer.parseInt(m.group(1));
			}
			
			p = Pattern.compile(".*spacing=([0-9]*).*");
			m = p.matcher(layoutFormat);
			if(m.matches())
			{
				layout.spacing = Integer.parseInt(m.group(1));
			}
			
			p = Pattern.compile(".*type=([A-Z]*).*");
			m = p.matcher(layoutFormat);
			if(m.matches())
			{
				if(m.group(1).equals("SWT.HORIZONTAL"))
					layout.type = SWT.HORIZONTAL;
				else if(m.group(1).equals("SWT.VERTICAL"))
					layout.type=SWT.VERTICAL;
			}
			return layout;
		}
		else if(layoutFormat.contains("FormLayout"))
		{
			FormLayout layout = new FormLayout();
			
			Pattern p;
			Matcher m;
			
			p = Pattern.compile(".*marginWidth=([0-9]*).*");
			m = p.matcher(layoutFormat);
			if(m.matches())
			{
				layout.marginWidth = Integer.parseInt(m.group(1));
			}
			
			p = Pattern.compile(".*marginBottom=([0-9]*).*");
			m = p.matcher(layoutFormat);
			if(m.matches())
			{
				layout.marginBottom = Integer.parseInt(m.group(1));
			}
			
			p = Pattern.compile(".*marginHeight=([0-9]*).*");
			m = p.matcher(layoutFormat);
			if(m.matches())
			{
				layout.marginHeight = Integer.parseInt(m.group(1));
			}
			
			p = Pattern.compile(".*marginLeft=([0-9]*).*");
			m = p.matcher(layoutFormat);
			if(m.matches())
			{
				layout.marginLeft = Integer.parseInt(m.group(1));
			}
			
			p = Pattern.compile(".*marginRight=([0-9]*).*");
			m = p.matcher(layoutFormat);
			if(m.matches())
			{
				layout.marginRight = Integer.parseInt(m.group(1));
			}
			
			p = Pattern.compile(".*marginTop=([0-9]*).*");
			m = p.matcher(layoutFormat);
			if(m.matches())
			{
				layout.marginTop = Integer.parseInt(m.group(1));
			}
			
			p = Pattern.compile(".*spacing=([0-9]*).*");
			m = p.matcher(layoutFormat);
			if(m.matches())
			{
				layout.spacing = Integer.parseInt(m.group(1));
			}
			
			return layout;
		}
		else if(layoutFormat.contains("RowLayout"))
		{
			RowLayout layout = new RowLayout();
			
			Pattern p;
			Matcher m;
			
			p = Pattern.compile(".*marginWidth=([0-9]*).*");
			m = p.matcher(layoutFormat);
			if(m.matches())
			{
				layout.marginWidth = Integer.parseInt(m.group(1));
			}
			
			p = Pattern.compile(".*marginBottom=([0-9]*).*");
			m = p.matcher(layoutFormat);
			if(m.matches())
			{
				layout.marginBottom = Integer.parseInt(m.group(1));
			}
			
			p = Pattern.compile(".*marginHeight=([0-9]*).*");
			m = p.matcher(layoutFormat);
			if(m.matches())
			{
				layout.marginHeight = Integer.parseInt(m.group(1));
			}
			
			p = Pattern.compile(".*marginLeft=([0-9]*).*");
			m = p.matcher(layoutFormat);
			if(m.matches())
			{
				layout.marginLeft = Integer.parseInt(m.group(1));
			}
			
			p = Pattern.compile(".*marginRight=([0-9]*).*");
			m = p.matcher(layoutFormat);
			if(m.matches())
			{
				layout.marginRight = Integer.parseInt(m.group(1));
			}
			
			p = Pattern.compile(".*marginTop=([0-9]*).*");
			m = p.matcher(layoutFormat);
			if(m.matches())
			{
				layout.marginTop = Integer.parseInt(m.group(1));
			}
			
			p = Pattern.compile(".*spacing=([0-9]*).*");
			m = p.matcher(layoutFormat);
			if(m.matches())
			{
				layout.spacing = Integer.parseInt(m.group(1));
			}
			
			p = Pattern.compile(".*type=([A-Z]*).*");
			m = p.matcher(layoutFormat);
			if(m.matches())
			{
				if(m.group(1).equals("SWT.HORIZONTAL"))
					layout.type = SWT.HORIZONTAL;
				else if(m.group(1).equals("SWT.VERTICAL"))
					layout.type=SWT.VERTICAL;
			}
			return layout;
			
		}
		return null;
	}

}
