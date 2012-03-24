/**
 * 
 */
package rs.ac.ns.ftn.tmd.fuzzy.membershipFunction;

/**
 * Exponential membership function<br><br>
 * In fact, this is quasi-exponential function, with cut-off point at 4.5 * variance<br><br>
 * 
 * <b>
 * m_f (X) = exp ( -X / variance )</b>; if 0 &lt; x &lt;= 4.5*variance <br><b>
 * m_f (X) = 0 </b>;  if x&gt; 4.5*variance
 * 
 * 
 *  
 * @author Nikola Kolarovic <nikola.kolarovic@gmail.com>
 *
 */
public class LRMembershipFunctionExponential implements LRMembershipFunction {

	/** variance of exponential function */
	protected double variance;
	
	/** after how many variances we assume f(x) ~= 0 ? */ 
	public static final double cutOff = 4.5d;
	
	
	 /** instantiates a quadratic membership function<br>
	   * m_f (X) = exp ( -X / variance )
	   * @param spread width of a membership function; spread > 0
	   */
	public LRMembershipFunctionExponential(double spread) {
		this.setSpread(spread);
	  }
	

   /**
	* function to get membership value for X value
	*/	
	public double getValue(double x) {
		assert (x>=0);
		
		if (this.variance==0) {
			if (x==0) return 1.0d;
			else return 0;
		}
		
		if (x < cutOff*variance)
			return Math.exp( -x / variance);
		else return 0;
	}

	
	/** sets width of a membership function */	
	public void setSpread(double spread) {
		assert(spread>=0);
		this.variance = spread/cutOff;
		
	}
	
	/** returns spread of a membership function */
	public double getSpread() {
		return variance*cutOff;
	}
	
	
  /** calculates the integral of the membership function
   * @param from lower boundary of the integral
   * @param to upper boundary of the integral
   * @return value of the membership function integral considering the given boundaries 
   */
	public double integrate(double from, double to) {
		
		if (this.variance==0) return 0;

		// set the boundaries for interior use
		if (to>this.getSpread()) to=this.getSpread();
		if (from<0) from=0;
		
		/* lots of code is commented; I thought about calculating the real function value,
		 * but in the end it is QUASI-EXPONENTIAL and it shoud return correct values, not
		 * theoretical ones.
		 */
		// special cases
		//if (from==0 && to==this.getSpread())
		//	return variance;
		
		// calculate
//		if (to<this.getSpread())
			return variance*( -Math.exp(-to/variance) + Math.exp(-from/variance));
//		else // until the end from arbitrary beginning = whole - before the beginning
//			return variance - integrate(0,from);
		
	}

	
	public Object clone() {
		return new LRMembershipFunctionExponential(this.getSpread());
	}


}
