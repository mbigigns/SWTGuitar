

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import efg.EventFlowGraph;
import efg.WidgetId;
import efg.EventFlowGraph.EdgeType;

public class EFGRenderListener implements Listener, MenuListener{

	private Widget widget;
	private Map<Widget,Color> widgets;
	private Map<Widget,EdgeType> edgeType;
	private Color blue;
	private Color green;
	
	public EFGRenderListener(Widget widget,Map<EdgeType, Set<WidgetId>> neighbors, Color blue, Color green)
	{
		this.widget=widget;
		widgets = new HashMap<Widget,Color>();
		edgeType = new HashMap<Widget,EdgeType>();
		
		for(WidgetId neighborId: neighbors.get(EdgeType.NORMAL))
		{
			Widget neighbor = VisualizationGenerator.widgetIDs.get(neighborId);
			storeWidgetColor(neighbor,EdgeType.NORMAL);
		}
		
		System.out.println(neighbors.get(EdgeType.REACHING));
		for(WidgetId neighborId: neighbors.get(EdgeType.REACHING))
		{
			Widget neighbor = VisualizationGenerator.widgetIDs.get(neighborId);
			storeWidgetColor(neighbor,EdgeType.REACHING);
		}
		
/*		System.out.println(widgets);
		System.out.println(neighbors);*/
		System.out.println(VisualizationGenerator.widgetList.get(widget)+" "+widgets);
		
		this.blue=blue;
		this.green=green;
	}
	
	public void handleEvent(Event arg0) {
		if(arg0.type == SWT.MouseEnter || arg0.type == SWT.Arm || arg0.type == SWT.Selection)
		{
			System.out.println(arg0.type);
			
			//return the previously colored controls to their original color
			if(DomParserExample.EFGHighlighted != null)
			{
				DomParserExample.EFGHighlighted.toggle();
			}
			
			for(Widget w:widgets.keySet())
			{
				if(w instanceof Control)
				{
					Control control = (Control) w;
					if(edgeType.get(w)!=null&&edgeType.get(w).equals(EdgeType.REACHING))
						control.setBackground(green);
					else
						control.setBackground(blue);
				}
				else if(w instanceof Item)
				{
					Item i = (Item) w;
					while(i.getText().charAt(0)=='*')
						i.setText(i.getText().substring(1));
					i.setText("*"+i.getText());
				}
			}
			DomParserExample.EFGHighlighted = this;
		}
		else if(arg0.type == SWT.MouseExit)
		{
			toggle();
		}
		else
		{
			System.out.println("Menu");
		}
	}
	
	public void toggle()
	{
		System.out.println("Me Toggle");
		for(Widget w:widgets.keySet())
		{
			if(w instanceof Control)
			{
				Control control = (Control) w;
				control.setBackground(widgets.get(w));
			}
			else if(w instanceof Item)
			{
				Item i = (Item) w;
				if(i.getText().length()>0)
				{
					while(i.getText().charAt(0)=='*')
						i.setText(i.getText().substring(1));
				}
			}
		}
		DomParserExample.EFGHighlighted = null;
	}
	
	
	private void storeWidgetColor(Widget widget, EdgeType edgetype)
	{
		if(widget instanceof Control)
		{
			Control control = (Control) widget;
			widgets.put(control, control.getBackground());
			edgeType.put(widget,edgetype);
		}
		else
		{
			widgets.put(widget, null);
		}
	}

	@Override
	public void menuHidden(MenuEvent arg0) {
	}

	@Override
	public void menuShown(MenuEvent arg0) {

		System.out.println("Menu");
		System.out.println(VisualizationGenerator.widgetList.get(widget));
		System.out.println(widgets.keySet());
		for(Widget w:widgets.keySet())
		{
			if(w instanceof Item)
			{
				Item i = (Item) w;
				i.setText("*"+i.getText());
			}
		}
		DomParserExample.EFGHighlighted = this;
	}
}
