/**
 * 
 */
package rs.ac.ns.ftn.tmd.fuzzy.membershipFunction;

/**
 * 
 * Quadratic membership function<br>
 * <b>m_f (X) = 1 - X^2 / spread^2</b>
 * @author Nikola Kolarovic <nikola.kolarovic@gmail.com>
 *
 */
public class LRMembershipFunctionQuadratic implements LRMembershipFunction {

	protected double spread;
	
	 /** instantiates a quadratic membership function
	   * @param spread variance > 0
	   */
	public LRMembershipFunctionQuadratic(double spread) {
		this.setSpread(spread);
	  }


	public double getSpread() {
		return this.spread;
	}

	public void setSpread(double spread) {
		assert(spread>=0);
		this.spread = spread;
	}

	
	/** returns membership grade for distance from modal value
	 * <b>m_f (X) = 1 - X^2 / spread^2</b>
	 */
	public double getValue(double x) {
		assert(x>=0);
		
		if (this.spread == 0) {
			if (x==0) return 1.0d;
			else return 0;
		}
		
		if (x<spread)
			return (1 - x*x/(spread*spread));
		
		else return 0;
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
		if (to>this.getSpread()) to=this.getSpread();
		
		return to-from+(Math.pow(from, 3) - Math.pow(to, 3))/(3*Math.pow(spread, 2));
		
	}
	
	
	public Object clone() {
		return new LRMembershipFunctionQuadratic(this.spread);
	}


}
