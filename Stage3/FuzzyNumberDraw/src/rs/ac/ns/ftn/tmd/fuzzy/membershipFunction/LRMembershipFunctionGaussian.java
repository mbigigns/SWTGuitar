/**
 * 
 */
package rs.ac.ns.ftn.tmd.fuzzy.membershipFunction;

/**
 * 
 * Gaussian membership function<br><br>
 * in fact, this is quasi-Gaussian function with cut-off at 3*variance<br>
 * 
 * <b>
 * m_f (X) = exp ( -X^2 / (2* variance^2 )</b>; if 0 &lt; x &lt;= 3*variance <br><b>
 * m_f (X) = 0 </b>;  if x&gt; 3*variance
 * 
 * 
 * @author Nikola Kolarovic <nikola.kolarovic@gmail.com>
 *
 */
public class LRMembershipFunctionGaussian implements LRMembershipFunction {

	/** variance of a gaussian function */
	protected double variance;
	
	/** after how many variances we assume f(x) ~= 0 ? */ 
	public static final double cutOff = 3.0d;
	
	
	 /** instantiates a quasi-Gaussian membership function<br>
	   * @param spread variance, variance > 0
	   */
	public LRMembershipFunctionGaussian(double spread) {
		 this.setSpread(spread);
	  }
	
	
	public double getSpread() {
		return variance*cutOff;
	}

	public void setSpread(double spread) {
		assert(spread>=0);
		variance = spread/cutOff;
	}


	/**
	 * Returns membership value if given distance from modal value 
	 * 
	   * m_f (X) = exp ( -X^2 / 2*(variance^2) )
	 */
	public double getValue(double x) {
		assert(x>=0);
		if (this.variance == 0) {
			if (x==0) return 1.0d;
			else return 0;
		}
		
		if (x < cutOff*variance)
			return Math.exp(- x*x/(2d*variance*variance));
		else return 0;
	}
	
	
  /** calculates the integral of the membership function
   * @param from lower boundary of the integral
   * @param to upper boundary of the integral
   * @return value of the membership function integral considering the given boundaries 
   */
	public double integrate(double from, double to) {

		if (this.variance==0) return 0;
		
		// set the boundaries for interior use
		if (from<0) from=0;
		if (to>this.getSpread()) to=this.getSpread();
		
		return Math.sqrt(Math.PI/2) * this.variance * 
				(LRMembershipFunctionGaussian.erf(to / (Math.sqrt(2)*this.variance) ) -
				 LRMembershipFunctionGaussian.erf(from / (Math.sqrt(2)*this.variance) ));
		
	}


    /** 
     * Error function
     * fractional error in math formula less than 1.2 * 10 ^ -7.
     * although subject to catastrophic cancellation when z in very close to 0
     * from Chebyshev fitting formula for erf(z) from Numerical Recipes, 6.2
     *  
     */
    public static double erf(double z) {
        double t = 1.0 / (1.0 + 0.5 * Math.abs(z));

        // use Horner's method
        double ans = 1 - t * Math.exp( -z*z   -   1.26551223 +
                                            t * ( 1.00002368 +
                                            t * ( 0.37409196 + 
                                            t * ( 0.09678418 + 
                                            t * (-0.18628806 + 
                                            t * ( 0.27886807 + 
                                            t * (-1.13520398 + 
                                            t * ( 1.48851587 + 
                                            t * (-0.82215223 + 
                                            t * ( 0.17087277))))))))));
        if (z >= 0) return  ans;
        else        return -ans;
    }
	
	/*
	 * // ALTERNATIVE, a bit faster but bigger error in result
	 * 
    // fractional error less than x.xx * 10 ^ -4.
    // Algorithm 26.2.17 in Abromowitz and Stegun, Handbook of Mathematical.
    public static double erf2(double z) {
        double t = 1.0 / (1.0 + 0.47047 * Math.abs(z));
        double poly = t * (0.3480242 + t * (-0.0958798 + t * (0.7478556)));
        double ans = 1.0 - poly * Math.exp(-z*z);
        if (z >= 0) return  ans;
        else        return -ans;
    }
    */
    
    
    
    
	public Object clone() {
		return new LRMembershipFunctionGaussian(cutOff*variance);
	}


}
