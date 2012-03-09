package test;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;
import org.xml.sax.SAXException;

import efg.EFGParser;
import efg.EventFlowGraph;
import efg.WidgetId;
import efg.EventFlowGraph.EdgeType;

public class EfgParserTest {

	@Test
	public void testDemo() throws SAXException {
		EventFlowGraph parsedGraph = EFGParser.parseFile("Demo.EFG");
		
		Set<WidgetId> widgets = parsedGraph.getAllWidgets();
		assertEquals(5, widgets.size());
		
		for (WidgetId id : widgets) {
			assertEquals(0, parsedGraph.getFollowingWidgets(id).get(EdgeType.NONE).size());
			assertEquals(5, parsedGraph.getFollowingWidgets(id).get(EdgeType.NORMAL).size());
			assertEquals(0, parsedGraph.getFollowingWidgets(id).get(EdgeType.REACHING).size());
		}
	}

	@Test
	public void testAddressBook() throws SAXException {
		EventFlowGraph parsedGraph = EFGParser.parseFile("./AddressBook/GUITAR-Default.EFG");
		
		Set<WidgetId> widgets = parsedGraph.getAllWidgets();
		assertEquals(30, widgets.size());
		
		WidgetId shellId = new WidgetId("w3927982668");
		
		assertEquals(24, parsedGraph.getFollowingWidgets(shellId).get(EdgeType.NONE).size());
		assertEquals(0, parsedGraph.getFollowingWidgets(shellId).get(EdgeType.NORMAL).size());
		assertEquals(6, parsedGraph.getFollowingWidgets(shellId).get(EdgeType.REACHING).size());
		
		WidgetId tableId = new WidgetId("w2332272556");
		
		assertEquals(23, parsedGraph.getFollowingWidgets(tableId, EdgeType.NONE).size());
		assertEquals(1, parsedGraph.getFollowingWidgets(tableId, EdgeType.NORMAL).size());
		assertEquals(6, parsedGraph.getFollowingWidgets(shellId, EdgeType.REACHING).size());
	}
}
