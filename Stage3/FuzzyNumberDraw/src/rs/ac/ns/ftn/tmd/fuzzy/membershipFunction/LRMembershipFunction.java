/**
 * 
 */
package rs.ac.ns.ftn.tmd.fuzzy.membershipFunction;

/**
 * Generic membership function - all membership functions extend this abstract class 
 * 
 * @author Nikola Kolarovic <nikola.kolarovic@gmail.com>
 *
 */
public interface LRMembershipFunction {
	
	  /** function to get membership value out of distance from modal value */
	  public double getValue(double x);
	  
	  /** gets worst case interval (width) of a function */
	  public double getSpread();
	  
	  /** sets worst case interval (width) of a function */
	  public void setSpread(double spread);
	  
	  /** clone method */
	  public Object clone();
	  
	  /** calculates the integral of the membership function
	   * @param from lower boundary of the integral
	   * @param to upper boundary of the integral
	   * @return value of the membership function integral considering the given boundaries 
	   */
	  public double integrate(double from, double to);
	  
}

    
