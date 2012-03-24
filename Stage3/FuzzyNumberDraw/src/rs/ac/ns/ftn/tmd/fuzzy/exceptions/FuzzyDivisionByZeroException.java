/**
 * 
 */
package rs.ac.ns.ftn.tmd.fuzzy.exceptions;

/**
 * A <b>Runtime</b> exception thrown when fuzzy division operation tries to divide with zero value in
 * support set of second division operand.
 * 
 * @author Nikola Kolarovic <nikola.kolarovic@gmail.com>
 *
 */
public class FuzzyDivisionByZeroException extends java.lang.ArithmeticException {
	  
	private static final long serialVersionUID = -1209070420025025650L;

	
	public String getMessage() {
		return "Division with Fuzzy zero exception";
	}
		  
}
