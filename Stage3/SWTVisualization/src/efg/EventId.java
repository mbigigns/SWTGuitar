package efg;

public class EventId {

	private String id;
	
	public EventId(String id) {
		if (id.startsWith("e")) { id = id.substring(1, id.length()); }
		this.id = id;
	}
	
	//constructor when event is a number/long
	public EventId(long id) {
		this.id = Long.toString(id);
	}
	
	public String getId() {
		return id;
	}
	
	@Override public int hashCode() {
		return (id).hashCode();
	}
	
	@Override public boolean equals(Object o) {
		if (o == null) { return false; }
		
		if (!EventId.class.isAssignableFrom(o.getClass())) { return false; }
		
		EventId other = (EventId) o;
		
		return this.id.equals(other.id);
	}
	
	@Override public String toString() {
		return "e" + this.id;
	}
}
