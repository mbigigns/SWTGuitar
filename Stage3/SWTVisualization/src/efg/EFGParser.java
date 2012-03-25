package efg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;

import efg.EventFlowGraph.EdgeType;

/**
 * Pretty much the only important thing about this class is that it parses EFG files.
 * See ParseState.parse().
 * 
 * @author Eric Oliver
 *
 */
public class EFGParser extends DefaultHandler {

	private EventFlowGraph compiledEfg;
	private List<EFGEvent> parsedEvents;

	private Stack<ParseState> currState;
	
	// all attributes for an event
	private String currInitial;
	private String currWidgetId;
	private String currEventId;
	private String currAction;
	private String currType;
	
	// all items for maintaining adjacency matrix
	private int currRow = 0;
	private int currCol = 0;
	private String currEdgeType;

	//return the compiled EFG
	private EventFlowGraph getCompiledEfg() {
		return compiledEfg;
	}

	//starts the document and initializes objects
	@Override public void startDocument() {
		this.compiledEfg = new EventFlowGraph();
		this.parsedEvents = new ArrayList<EFGEvent>();
		this.currState = new Stack<ParseState>();
	}

	//creates a new element in the EFG with the given parameters
	@Override public void startElement(String uri, String localName, String qName, Attributes attributes) {
		// update the state of parsing
		ParseState currState = null; 
		try { 
			currState = this.currState.push(ParseState.parse(qName)); 
		} catch (Exception e) { 
			System.out.println("Can't set element " + uri + "/" + localName + "/" + qName + " state"); 
			//try to continue operation 
		}
		
		// business logic
		switch(currState) {
		case EVENT:
			currInitial = null;
			currWidgetId = null;
			currEventId = null;
			currAction = null;
			currType = null;
			break;
		case ROW:
			currCol = 0;
			break;
		case E:
			currEdgeType = null;
			break;
		}
	}

	//handles a finish state
	@Override public void endElement(String uri, String localName, String qName) {
		// update the state of parsing
		ParseState finishedState = this.currState.pop();
		
		// do more record keeping. ...yay
		switch(finishedState) {
		case EVENT:
			this.parsedEvents.add(new EFGEvent(currEventId, currWidgetId, currType, currInitial, currAction));
			break;
		case EVENTS:
			for (EFGEvent event : this.parsedEvents) {
				this.compiledEfg.addEvent(event);
			}
			break;
		case ROW:
			this.currRow++;
			break;
		case E:
			EFGEvent from = this.parsedEvents.get(currRow);
			EFGEvent to = this.parsedEvents.get(currCol);
			this.compiledEfg.addAdjacency(from, EdgeType.parse(this.currEdgeType), to);
			this.currEdgeType = null;
			this.currCol++;
			break;
		}
	}

	//sets the correct value to the given string
	@Override public void characters(char ch[], int start, int length) {
		
		String s = new String(ch, start, length);
		
		switch(this.currState.peek()) {
		case EVENT_ID:
			this.currEventId = s;
			break;
		case WIDGET_ID:
			this.currWidgetId = s;
			break;
		case TYPE:
			this.currType = s;
			break;
		case INITIAL:
			this.currInitial = s;
			break;
		case ACTION:
			this.currAction = s;
			break;
		case E:
			this.currEdgeType = s;
			break;
		}
	}

	/**
	 * All states of parsing
	 * @author Eric Oliver
	 *
	 */
	private enum ParseState {
		EFG,
		EVENTS {
			@Override public String toString() {
				return "Events";
			}
		},
		EVENT {
			@Override public String toString() {
				return "Event";
			}
		},
		EVENT_ID {
			@Override public String toString() {
				return "EventId";
			}
		},
		WIDGET_ID {
			@Override public String toString() {
				return "WidgetId";
			}
		},
		TYPE {
			@Override public String toString() {
				return "Type";
			}
		},
		INITIAL {
			@Override public String toString() {
				return "Initial";
			}
		},
		ACTION {
			@Override public String toString() {
				return "Action";
			}
		},
		EVENT_GRAPH {
			@Override public String toString() {
				return "EventGraph";
			}
		},
		ROW {
			@Override public String toString() {
				return "Row";
			}
		},
		E;

		private static Map<String, ParseState> quickLookup;
		
		public static ParseState parse(String s) {
			ParseState retVal = quickLookup.get(s.toLowerCase());
			
			if (retVal == null) {
				System.out.println(s + " is not a valid parse tag");
				//try to continue operation 
			}
			
			return retVal;
		}
		
		static {
			quickLookup = new HashMap<String, ParseState>();
			
			for (ParseState state : ParseState.values()) {
				quickLookup.put(state.toString().toLowerCase(), state);
			}
		}
	}

	/**
	 * Parses an EFG file, letting all errors in the EFG file construction propagate to the top for
	 * application-level error handling. 
	 * 
	 * @param efgFile The name of the EFG file
	 * @return The EFG represented by the file passed in
	 * @throws SAXException if the EFG file has malformed XML
	 */	
	public static EventFlowGraph parseFile(File efgFile) throws SAXException {

		// boilerplate code
		SAXParserFactory xmlParserFactory = SAXParserFactory.newInstance();
		try {
			SAXParser xmlParser = xmlParserFactory.newSAXParser();

			EFGParser efgParser = new EFGParser();

			xmlParser.parse(efgFile, efgParser);

			return efgParser.getCompiledEfg();
		} catch(ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}		
	}
	
	public static EventFlowGraph parseFile(String fileName) throws SAXException {
		return parseFile(new File(fileName));
	}
}
