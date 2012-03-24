/**
 * 
 */
package rs.ac.ns.ftn.tmd.fuzzy.exceptions;

/**
 * A <b>Runtime</b> exception thrown when user tries to multiply numbers which contain zero
 * in their support sets. It causes numbers to diverge and operation results in an invalid fuzzy number.
 * 
 * @author Nikola Kolarovic <nikola.kolarovic@gmail.com>
 *
 */
public class FuzzyMultiplicationByZeroException extends java.lang.ArithmeticException {
	  
	private static final long serialVersionUID = 708375866158907517L;

	public String getMessage() {
		return "Multiplication with Fuzzy zero exception";
	}
		  
}
