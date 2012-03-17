

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	private Color blue;
	
	public EFGRenderListener(Map<EdgeType, Set<WidgetId>> neighbors, Color blue)
	{
		widgets = new HashMap<Widget,Color>();
		
		for(WidgetId neighborId: neighbors.get(EdgeType.NORMAL))
		{
			Widget neighbor = VisualizationGenerator.widgetIDs.get(neighborId);
			storeWidgetColor(neighbor);
		}
		
		for(WidgetId neighborId: neighbors.get(EdgeType.REACHING))
		{
			Widget neighbor = VisualizationGenerator.widgetIDs.get(neighborId);
			storeWidgetColor(neighbor);
		}
		
		
		System.out.println("Yoooo");
		System.out.println(widgets);
		System.out.println(neighbors);
		
		this.blue=blue;
	}
	
	public void handleEvent(Event arg0) {
		System.out.println("Double Clicked");
		
		//return the previously colored controls to their original color
		if(DomParserExample.EFGHighlighted != null)
			DomParserExample.EFGHighlighted.toggle();
		
		
		for(Widget w:widgets.keySet())
		{
			System.out.println(w);
			Control control = (Control) w;
			control.setBackground(blue);
		}
		DomParserExample.EFGHighlighted = this;
	}
	
	public void toggle()
	{
		for(Widget w:widgets.keySet())
		{
			Control control = (Control) w;
			control.setBackground(widgets.get(w));
		}
	}
	
	private void storeWidgetColor(Widget widget)
	{
		if(widget instanceof Control)
		{
			Control control = (Control) widget;
			widgets.put(control, control.getBackground());
		}
	}
}
