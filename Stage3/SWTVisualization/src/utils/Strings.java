package utils;

/**
 * Influenced by Google Guava. Did not want to add another dependency lest the team murder me.
 * 
 * @author Eric Oliver
 */
public final class Strings {

	//compares whether string is null or empty
	public static boolean isNullOrEmpty(String s) {
		return s == null ? true : s.isEmpty();
	}
	
	//compares strings
	public static boolean equals(String a, String b) {
		if (a == null && b == null) {
			return true;
		}
		
		if (a.isEmpty() && b.isEmpty()) {
			return true;
		}
		
		return a.equals(b);
	}
}
