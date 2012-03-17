

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

import efg.EventFlowGraph;
import efg.WidgetId;
import efg.EventFlowGraph.EdgeType;

public class EFGRenderListener implements Listener{

	private Map<Widget,Color> widgets;
	private Map<Widget,EdgeType> edgeType;
	private Color blue;
	private Color green;
	
	public EFGRenderListener(Map<EdgeType, Set<WidgetId>> neighbors, Color blue, Color green)
	{
		widgets = new HashMap<Widget,Color>();
		edgeType = new HashMap<Widget,EdgeType>();
		
		for(WidgetId neighborId: neighbors.get(EdgeType.NORMAL))
		{
			Widget neighbor = VisualizationGenerator.widgetIDs.get(neighborId);
			storeWidgetColor(neighbor,EdgeType.NORMAL);
		}
		
		for(WidgetId neighborId: neighbors.get(EdgeType.REACHING))
		{
			Widget neighbor = VisualizationGenerator.widgetIDs.get(neighborId);
			storeWidgetColor(neighbor,EdgeType.REACHING);
		}
		
/*		System.out.println(widgets);
		System.out.println(neighbors);*/
		
		this.blue=blue;
		this.green=green;
	}
	
	public void handleEvent(Event arg0) {
		if(arg0.type == SWT.MouseEnter)
		{
			System.out.println("Double Clicked");
			
			//return the previously colored controls to their original color
			if(DomParserExample.EFGHighlighted != null)
				DomParserExample.EFGHighlighted.toggle();
			
			
			for(Widget w:widgets.keySet())
			{
				System.out.println(w);
				Control control = (Control) w;
				if(edgeType.get(w)!=null&&edgeType.get(w).equals(EdgeType.REACHING))
					control.setBackground(green);
				else
					control.setBackground(blue);
			}
			DomParserExample.EFGHighlighted = this;
		}
		else if(arg0.type == SWT.MouseExit)
		{
			toggle();
		}
	}
	
	public void toggle()
	{
		for(Widget w:widgets.keySet())
		{
			Control control = (Control) w;
			control.setBackground(widgets.get(w));
		}
	}
	
	private void storeWidgetColor(Widget widget, EdgeType edgetype)
	{
		if(widget instanceof Control)
		{
			Control control = (Control) widget;
			widgets.put(control, control.getBackground());
			edgeType.put(widget,edgetype);
		}
	}
}
