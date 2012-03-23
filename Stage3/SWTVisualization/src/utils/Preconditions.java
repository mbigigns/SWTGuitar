package utils;

/**
 * Influenced by Google Guava. Did not want to add another dependency lest the team murder me.
 * 
 * @author Eric Oliver
 */
public final class Preconditions {
	
	//checkks that object is not null
	public static void checkNotNull(Object o) {
		checkNotNull(o, "");
	}
	
	//checks the object is not null otherwise throws a message
	public static void checkNotNull(Object o, String message) {
		if (o == null) { throw new NullPointerException(message); }
	}
	
	//checks whether argument is true
	public static void checkArg(boolean b) {
		checkArg(b, "");
	}
	
	//throws exception if not true
	public static void checkArg(boolean b, String message) {
		if (!b) { throw new IllegalArgumentException(message);	}
	}
}
