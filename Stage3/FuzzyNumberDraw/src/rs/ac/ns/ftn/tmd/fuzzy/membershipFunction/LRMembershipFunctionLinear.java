/**
 * 
 */
package rs.ac.ns.ftn.tmd.fuzzy.membershipFunction;

/**
 * @author Nikola Kolarovic <nikola.kolarovic@gmail.com>
 *
 */
public class LRMembershipFunctionLinear implements LRMembershipFunction {

	/** the spread of the linear function; -n in equation below*/
	protected double spread;
	
	
	  /** instantiates a linear membership function<br>
	   * m_f (X) = k*X + n
	   * @param spread worst case interval (width) of a function, spread > 0
	   */
	  public LRMembershipFunctionLinear(double spread) {
		  this.setSpread(spread);
	  }
	  
	
	/**
	 * function to get membership value for X value
	 */
	public double getValue(double x) {
		if (this.spread==0) {
			if (x==0) return 1.0d;
			else return 0;
		}
		
		return Math.max(0, 1 - (x / this.spread)  );
	}

	

	public Object clone() {
		return new LRMembershipFunctionLinear(this.spread);
	}


	/** returns spread of a membership function */
	public double getSpread() {
		return this.spread;
	}


	/** sets width of a membership function */
	public void setSpread(double spread) {
		assert(spread>=0);
		this.spread = spread;
	}

  /** calculates the integral of the membership function
   * @param from lower boundary of the integral
   * @param to upper boundary of the integral
   * @return value of the membership function integral considering the given boundaries 
   */
	public double integrate(double from, double to) {
		
		if (this.spread==0) return 0;
		
		// set the boundaries for interior use
		if (from<0) from=0;
		if (to>this.spread) to=this.spread;
		
		// special case; do not calculate
		if (from==0 && to==this.spread)
			return this.spread/2;
		// calculate
		return (to-from)*(this.getValue(to) + (this.getValue(from)-this.getValue(to))/2);
		
	}

}
