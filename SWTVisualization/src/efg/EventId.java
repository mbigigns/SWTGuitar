package efg;

public class EventId {

	private long id;
	
	public EventId(String id) {
		if (id.startsWith("e")) { id = id.substring(1, id.length()); }
		this.id = Long.parseLong(id);
	}
	
	public EventId(long id) {
		this.id = id;
	}
	
	public long getId() {
		return id;
	}
	
	@Override public int hashCode() {
		return ((Long)id).hashCode();
	}
	
	@Override public boolean equals(Object o) {
		if (o == null) { return false; }
		
		if (!EventId.class.isAssignableFrom(o.getClass())) { return false; }
		
		EventId other = (EventId) o;
		
		return this.id == other.id;
	}
	
	@Override public String toString() {
		return "e" + Long.toString(this.id);
	}
}
