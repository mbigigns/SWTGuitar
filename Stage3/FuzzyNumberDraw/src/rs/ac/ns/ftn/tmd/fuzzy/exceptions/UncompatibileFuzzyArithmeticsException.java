/**
 * 
 */
package rs.ac.ns.ftn.tmd.fuzzy.exceptions;

/**
 * Exception thrown when fuzzy arithmetics operation cannot be executed because two implementations are
 * not compatibile. It happens when FuzzyNumber instances are of the different type (e.g. operations
 * between LRFuzzyNumber and DecomposedFuzzyNumber are not allowed).
 * 
 * @author Nikola Kolarovic <nikola.kolarovic@gmail.com>
 *
 */
public class UncompatibileFuzzyArithmeticsException extends RuntimeException {
	
	private static final long serialVersionUID = 5385142219974574844L;
	
	public UncompatibileFuzzyArithmeticsException(Class first, Class second) {
		super("Arithmetics is not defined because types are incompatibile!\n" +
				"You tried an operation between "+first.getName().substring(first.getName().lastIndexOf("."))
				+" and "+second.getName().substring(second.getName().lastIndexOf("."))+".");
	}
	
	public UncompatibileFuzzyArithmeticsException(String s) {
		super(s);
	}
	

}
