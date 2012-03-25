package efg;

import java.util.HashMap;
import java.util.List;

import static utils.Preconditions.checkArg;
import static utils.Strings.isNullOrEmpty;

/**
 * This is the abstraction of an EFG event as described in
 * http://sourceforge.net/apps/mediawiki/guitar/index.php?title=EFG
 * 
 * It converts a textual representation (probably from XML) into 
 * something more Java-friendly
 * 
 * @author Eric Oliver
 *
 */
class EFGEvent {
	
	private EventId eventId;
	private WidgetId widgetId;
	private EventType eventType;
	private boolean isInitial;
	private String action;
	
	// these next few are optional, don't really appear, and should be considered unsupported
	// just thought I should throw them in for completeness
	@SuppressWarnings("unused") private List<String> listeners;
	@SuppressWarnings("unused") private HashMap<String, List<String>> attributes;
	
	/**
	 * Initial constructor for raw data from the EFG file. Does the transformations from strings
	 * into actual types. Can create other constructors as needed.
	 * 
	 * Note to developers: this is not an actual constructor. The init() method is the constructor
	 * 
	 * @param eventId Event id as a string
	 * @param widgetId Widget id as a string
	 * @param eventType Interaction as a string
	 * @param isInitial true/false as a string
	 * @param action is a class name. I dare not resolve into a class in case it needs to be dynamically loaded.
	 * 
	 * @throws IllegalArgumentException if any string is null, empty, or invalid
	 * @throws NumberFormatException if the eventId or widgetId is malformed
	 */
	EFGEvent(String eventId, String widgetId, String eventType, String isInitial, String action) {
		
		EventId eId;
		WidgetId wId;
		EventType eType;
		boolean init;
		try { 
		checkArg(!isNullOrEmpty(eventId), "Event id cannot be null or empty");
		checkArg(!isNullOrEmpty(widgetId), "Widget id cannot be null or empty");
		checkArg(!isNullOrEmpty(eventType), "Interaction cannot be null or empty");
		checkArg(!isNullOrEmpty(isInitial), "isInitial cannot be null or empty");
		checkArg(!isNullOrEmpty(action), "action cannot be null or empty");
		} catch (Exception e) { 
			// try to continue operation 
		}
		
		eId = new EventId(eventId);
		wId = new WidgetId(widgetId);
		
		eType = EventType.parse(eventType);
		init = Boolean.parseBoolean(isInitial);
		
		init(eId, wId, eType, init, action);
	}
	
	/**
	 * The officially official constructor. I swear!
	 * 
	 * @param eventId
	 * @param widgetId
	 * @param eventType
	 * @param isInitial
	 */
	private void init(EventId eventId, WidgetId widgetId, EventType eventType, boolean isInitial, String action) {
		this.eventId = eventId;
		this.widgetId = widgetId;
		this.eventType = eventType;
		this.isInitial = isInitial;
		this.action = action;
	}
	
	/**
	 * @return the widgetId
	 */
	public WidgetId getWidgetId() {
		return widgetId;
	}
	
	/**
	 * @return the eventId
	 */
	public EventId getEventId() {
		return eventId;
	}

	/**
	 * @return the eventType
	 */
	public EventType getEventType() {
		return eventType;
	}

	/**
	 * @return the isInitial
	 */
	public boolean isInitial() {
		return isInitial;
	}

	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}
	
	@Override
	public int hashCode() {
		return eventId.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (!(EFGEvent.class.isAssignableFrom(o.getClass()))) {
			return false;
		}
		
		EFGEvent other = (EFGEvent) o;
		
		return this.eventId == other.eventId;
	}
	
	@Override public String toString() {
		return "{ " + eventId.toString() +
				", " + widgetId.toString() +
				", " + eventType.toString() +
				"}";
	}

	/**
	 * Stolen from edu.umd.cs.guitar.model.GUITARConstants
	 * @author Eric Oliver
	 */
	public static enum EventType {
		SYSTEM_INTERACTION {
			@Override public String toString() {
				return "SYSTEM INTERACTION";
			}
		},
		EXPAND {
			@Override public String toString() {
				return "EXPAND";
			}
		},
		TERMINAL {
			@Override public String toString() {
				return "TERMINAL";
			}
		},
		RESTRICTED_FOCUS {
			@Override public String toString() {
				return "RESTRICTED FOCUS";
			}
		},
		UNRESTRICTED_FOCUS {
			@Override public String toString() {
				return "UNRESTRICED FOCUS"; // this is how it is in GUITAR, and so it must be wrong here :'(
			}
		},
		ACTIVATED_BY_PARENT {
			@Override public String toString() {
				return "ACTIVATED BY PARENT";
			}
		};
		
		private static HashMap<String, EventType> quickParseTable;
		static {
			quickParseTable = new HashMap<String, EventType>();
			for (EventType interaction : EventType.values()) {
				quickParseTable.put(interaction.toString(), interaction);
			}
		}
		
		//parses an interaction and ensures it is valid
		public static EventType parse(String interaction) {
			EventType parsedInteraction = quickParseTable.get(interaction.toUpperCase());
			
			if (parsedInteraction == null) {
				System.out.println(interaction + " is not a valid interaction");
				//try to continue operation 
			}
			
			return parsedInteraction;
		}
	}
}
