

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

import efg.EventFlowGraph;
import efg.WidgetId;
import efg.EventFlowGraph.EdgeType;

public class EFGRenderListener implements Listener{

	private List<Widget> widgets;
	private Color blue;
	
	public EFGRenderListener(Map<EdgeType, Set<WidgetId>> neighbors, Color blue)
	{
		widgets = new ArrayList<Widget>();
		
		for(WidgetId neighborId: neighbors.get(EdgeType.NORMAL))
		{
			Widget neighbor = VisualizationGenerator.widgetIDs.get(neighborId);
			widgets.add(neighbor);
		}
		
		for(WidgetId neighborId: neighbors.get(EdgeType.REACHING))
		{
			Widget neighbor = VisualizationGenerator.widgetIDs.get(neighborId);
			widgets.add(neighbor);
		}
		
		
		System.out.println("Yoooo");
		System.out.println(widgets);
		System.out.println(neighbors);
		
		this.blue=blue;
	}
	
	public void handleEvent(Event arg0) {
		System.out.println("Double Clicked");
		for(Widget w:widgets)
		{
			System.out.println(w);
			if(w instanceof Control)
			{
				Control control = (Control) w;
				control.setBackground(blue);
			}
		}
	}

}
