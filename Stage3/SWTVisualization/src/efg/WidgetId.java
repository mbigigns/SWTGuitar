package efg;

public class WidgetId {

	private long id;
	
	public WidgetId(String id) {
		if (id.startsWith("w")) { id = id.substring(1, id.length()); }
		this.id = Long.parseLong(id);
	}
	
	//constructor when widget id is a number
	public WidgetId(long id) { 
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
		
		if (!WidgetId.class.isAssignableFrom(o.getClass())) { return false; }
		
		WidgetId other = (WidgetId) o;
		
		return this.id == other.id;
	}
	
	@Override public String toString() {
		return "w" + Long.toString(this.id);
	}
}
