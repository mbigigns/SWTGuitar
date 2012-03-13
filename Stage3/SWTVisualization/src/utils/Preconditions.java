package utils;

/**
 * Influenced by Google Guava. Did not want to add another dependency lest the team murder me.
 * 
 * @author Eric Oliver
 */
public final class Preconditions {
	
	public static void checkNotNull(Object o) {
		checkNotNull(o, "");
	}
	
	public static void checkNotNull(Object o, String message) {
		if (o == null) { throw new NullPointerException(message); }
	}

	public static void checkArg(boolean b) {
		checkArg(b, "");
	}
	
	public static void checkArg(boolean b, String message) {
		if (!b) { throw new IllegalArgumentException(message);	}
	}
}
