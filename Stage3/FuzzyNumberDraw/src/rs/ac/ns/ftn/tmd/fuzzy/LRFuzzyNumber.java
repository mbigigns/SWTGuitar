/**
 * 
 */
package rs.ac.ns.ftn.tmd.fuzzy;

import rs.ac.ns.ftn.tmd.fuzzy.exceptions.FuzzyDivisionByZeroException;
import rs.ac.ns.ftn.tmd.fuzzy.exceptions.FuzzyMultiplicationByZeroException;
import rs.ac.ns.ftn.tmd.fuzzy.exceptions.UncompatibileFuzzyArithmeticsException;
import rs.ac.ns.ftn.tmd.fuzzy.membershipFunction.*;

/**
 * Implementation of L-R fuzzy numbers and arithmetics between them
 * 
 * @author Nikola Kolarovic <nikola.kolarovic@gmail.com>
 *
 */
public class LRFuzzyNumber implements FuzzyNumber {
	
	/** modal or central value, value in which membership function reaches value 1 */
	protected double modalValue;

	/** left function */
	protected LRMembershipFunction Lfunction = null;
	
	/** right function */
	protected LRMembershipFunction Rfunction = null;
	

	
	/** Constructor for L-R fuzzy numbers
	 * 
	 * @param modalValue central value of a fuzzy number
	 * @param alpha worst-case interval <b>left</b> of modal value
	 * @param beta worst-case interval <b>right</b> of modal value
	 * @param Ltype type of function for Left function
	 * @param Rtype type of function for Right function
	 */ 
	public LRFuzzyNumber(double modalValue, double alpha, double beta, int Ltype, int Rtype) {
		
		this.modalValue = modalValue;
		this.Lfunction = this.getMembershipFunction(alpha, Ltype);
		this.Rfunction = this.getMembershipFunction(beta, Rtype);
		
	}

	
	/** Class-visible constructor for internal use, it sets attributes to proper values
	 * 
	 * @param modalValue modal value
	 * @param Lfunction L-function
	 * @param Rfunction R-function
	 */
	LRFuzzyNumber(double modalValue, LRMembershipFunction Lfunction, LRMembershipFunction Rfunction) {
		assert(Lfunction!=null && Rfunction!=null);
		
		this.modalValue = modalValue;
		this.Lfunction = Lfunction;
		this.Rfunction = Rfunction;
	}
	
	
	// -------------------------------- HELPER FUNCTIONS ---------------------------------
	
	/** Function that instantiates proper membership function depending on its type.<br>
	 * 
	 * @param worstCaseInterval "width" of the function
	 * @param type type of the function. Use static values from <b>FuzzyNumber</b> class.
	 * @return membership function according to parameters
	 */
	protected LRMembershipFunction getMembershipFunction(double worstCaseInterval, int type) {
		
		// correct when adding a new function type
		assert (type>=0 && type<= FuzzyNumber.FUNCTION_EXPONENTIAL);
		
		switch (type) {
			case FuzzyNumber.FUNCTION_LINEAR : return new LRMembershipFunctionLinear(worstCaseInterval);
			
			case FuzzyNumber.FUNCTION_GAUSSIAN : return new LRMembershipFunctionGaussian(worstCaseInterval);
			
			case FuzzyNumber.FUNCTION_QUADRATIC : return new LRMembershipFunctionQuadratic(worstCaseInterval);

			case FuzzyNumber.FUNCTION_EXPONENTIAL : return new LRMembershipFunctionExponential(worstCaseInterval);
		}
		return null;
	}
	
	
	/** checks if membership functions are of the same type
	 * @throws <code>UncompatibileFuzzyArithmeticsException</code> if L-R functions are not compatibile
	 * @param f1 first function
	 * @param f2 second function
	 */
	// was : 	 * @return <i>true</i> if types are equal; <i>false</i> otherwise
	protected void checkFunctionCompatibility(LRMembershipFunction f1, LRMembershipFunction f2) {
		assert (f1!=null && f2!=null);
		
		if (!f1.getClass().equals(f2.getClass())) 
			throw new UncompatibileFuzzyArithmeticsException(f1.getClass(), f2.getClass());
		
	}

	
	/** getter for Lfunction
	 * @return Left membership function
	 */
	protected LRMembershipFunction getLfunction() {
		return this.Lfunction;
	}
	
	/** getter for Rfunction
	 * @return Right membership function
	 */
	protected LRMembershipFunction getRfunction() {
		return this.Rfunction;
	}
	

	
	/** checks if two fuzzy number interpretations are same.<br>
	 *  If not, throw a <b>UncompatibileFuzzyArithmeticsException</b> with a message.
	 * @param second compare type of current object with the "second" one.
	 */
	protected void checkImplementationCompatibilityWith(FuzzyNumber second) {
		if ( ! (second instanceof LRFuzzyNumber) )
			throw new UncompatibileFuzzyArithmeticsException(this.getClass(),second.getClass());
		
	}
	
	
	/** checks if FuzzyNumber is completely positive or negative
	 * 
	 * @return -1 if negative; 1 if positive; 0 if it spreads over 0.
	 */
	protected int isPositive() {
		if (this.modalValue > 0) { // should be positive
			if (this.modalValue - this.Lfunction.getSpread() > 0 ) return 1; // positive
				else return 0; // otherwise it spreads over zero
		}
		else
			if (this.modalValue < 0) { // should be negative
				if (this.modalValue + this.Rfunction.getSpread() < 0) return -1; // negative
					else return 0; // otherwise it spreads over zero
			}
		return 0; // modalvalue == 0
	}
	
	// -------------------------------- ARITHMETIC FUNCTIONS ---------------------------------
	
	
	  /** Fuzzy operation for <b>addition</b> of two fuzzy numbers. <br>
	   * First argument for the operation is the current object, and second argument
	   * is given as a function parameter.<br>
	   * @throws <code>UncompatibileFuzzyArithmeticsException</code> if fuzzy number implementations are not compatibile
	   * @param arg2 second argument for the operation
	   * @return number which is the result of the operation
	   */
	public FuzzyNumber add(FuzzyNumber arg2) {
		// if implementations are the same?
		this.checkImplementationCompatibilityWith(arg2);
		
		// direct function compatibility
		LRMembershipFunction l = (LRMembershipFunction) (((LRFuzzyNumber)arg2).getLfunction().clone());
		LRMembershipFunction r = (LRMembershipFunction) (((LRFuzzyNumber)arg2).getRfunction().clone());
		checkFunctionCompatibility(this.Lfunction, l);
		checkFunctionCompatibility(this.Rfunction, r);
		
		l.setSpread(this.Lfunction.getSpread()+l.getSpread());
		r.setSpread(this.Rfunction.getSpread()+r.getSpread());
		
		return new LRFuzzyNumber(this.modalValue+arg2.getModalValue(), l, r);
	}

	


	  /** Fuzzy operation for multiplication between current FuzzyNumber and a crisp (<i>double</i>) value. <br>
	   * First argument for the operation is the current object, and second argument
	   * is given as a function parameter.
	   * @throws <code>UncompatibileFuzzyArithmeticsException</code> if fuzzy number implementations are not compatibile
	   * @param arg2 constant which will be multiplied as a crisp number
	   * @return number which is the result of the operation
	   */	  
	public FuzzyNumber substract(FuzzyNumber arg2) {
		
		return this.add(arg2.multiplyScalar(-1d));
		
	}
	
	
	  /** Fuzzy operation for <b>multiplication</b> of two fuzzy numbers. <br>
	   * First argument for the operation is the current object, and second argument
	   * is given as a function parameter.<br>
	   * For this function it is very important that numbers don't have zero in their confidence
	   * intervals, because the multiplication operation is not defined in that case.<br><br>
	   * This operation uses <b>secant approximation</b> to keep the group closed (L-R).<br><br>
	   * 
	   * @throws <code>FuzzyMultiplicaitonByZeroException</code> if one of the interval contains zero (0) value in universal set - operation is not valid.<br><br>
	   * @throws <code>UncompatibileFuzzyArithmeticsException</code> if fuzzy number implementations are not compatibile
	   * @throws <code>UncompatibileFuzzyArithmeticsException</code> if fuzzy number implementations are not compatibile<br><br>
	   * 
	   * @param arg2 second argument for the operation
	   * @return number which is the result of the operation
	   */
	public FuzzyNumber multiply(FuzzyNumber arg2) {
		// if implementations are the same?
		this.checkImplementationCompatibilityWith(arg2);
		
		
		// first, if one contains zero in confidence interval, LR-fuzzy numbers don't support that!
		if (((LRFuzzyNumber)this).isPositive()==0 || ((LRFuzzyNumber)arg2).isPositive()==0 ) {
			throw new FuzzyMultiplicationByZeroException();
		}

		// function compatibility depends on number "sign"
		LRMembershipFunction l=(LRMembershipFunction) (((LRFuzzyNumber)arg2).getLfunction().clone());
		LRMembershipFunction r=(LRMembershipFunction) (((LRFuzzyNumber)arg2).getRfunction().clone());
		
		if ( this.isPositive()>0 && ((LRFuzzyNumber)arg2).isPositive()>0  ) {
			// direct compatibility; solution is L,R
			checkFunctionCompatibility(this.Lfunction, l);
			checkFunctionCompatibility(this.Rfunction, r);
			// if BOTH POSITIVE, multiplication is, in secant approximation:
			// q = ( x1*x2,   x1*alpha2 + x2*alpha1 - alpha1*alpha2,  x1*beta2 + x2*beta1 + beta1*beta2 )
			l.setSpread(
					this.getModalValue()*l.getSpread() +
					arg2.getModalValue()*this.getLfunction().getSpread() -
					this.getLfunction().getSpread()*l.getSpread());
			r.setSpread(
					this.getModalValue()*r.getSpread() +
					arg2.getModalValue()*this.getRfunction().getSpread() +
					this.getRfunction().getSpread()*r.getSpread());
			
			return new LRFuzzyNumber(this.modalValue*arg2.getModalValue(), l, r);
			
		}
		else {
			if ( this.isPositive()<0 && ((LRFuzzyNumber)arg2).isPositive()<0  ) {
				// ------------ both negative ------------
				return this.multiplyScalar(-1).multiply(arg2.multiplyScalar(-1));
			}
			else {
				
				// ------ ONE is POSITIVE and ONE is NEGATIVE -------------
				return (this.multiply(arg2.multiplyScalar(-1))).multiplyScalar(-1);

			}
		}
		
	}

    /**	 
     * <b>Basically, it is fuzzy 1/X function.</b><br>
     * Calculates new FuzzyNumber which represents the multiplication inverse fuzzy number for the current one,
     * using the secant approximation.<br>
	 * @throws <code>FuzzyDivisionByZeroException</code> if number contains 0 value in it's domain
	 * @return fuzzy 1/X number
	 */ 
	public FuzzyNumber getMultiplicationInverse() {
		
		if (this.isPositive()==0) throw new FuzzyDivisionByZeroException();
		
		LRMembershipFunction l = (LRMembershipFunction)this.getLfunction().clone();
		LRMembershipFunction r = (LRMembershipFunction)this.getRfunction().clone();
		// P^-1 =~ [ 1/X, beta/(X*(X+beta))  , alpha/(X*(X-alpha))  ] R,L
		r.setSpread( this.getRightSpread() / ( this.modalValue*(this.modalValue+this.getRightSpread()) ) );
		l.setSpread( this.getLeftSpread() / (this.modalValue*(this.modalValue-this.getLeftSpread())));
		
		return new LRFuzzyNumber( 1d / this.modalValue, r,l);
	}

	
	
	
	/** Fuzzy operation for <b>division</b> of two fuzzy numbers. <br>
	 * First argument for the operation is the current object, and second argument
	 * is given as a function parameter.<br>
	 * @throws <code>UncompatibileFuzzyArithmeticsException</code> if fuzzy number implementations are not compatibile<br>
	 * @throws <code>FuzzyDivisionByZeroException</code> if number's confidence interval contains value 0<br>
	 * @param arg2 second argument for the operation
	 * @return number which is the result of the operation
	 * 
	 * @see rs.ac.ns.ftn.tmd.fuzzy.FuzzyNumber#divide(rs.ac.ns.ftn.tmd.fuzzy.FuzzyNumber)
	 */
	public FuzzyNumber divide(FuzzyNumber arg2) {
		return this.multiply(arg2.getMultiplicationInverse());
	}

	
	
	
	  /** Fuzzy operation for addition between current FuzzyNumber and a scalar (<i>double</i>) value. <br>
	   * First argument for the operation is the current object, and second argument
	   * is given as a function parameter.
	   * @param arg2 constant which will be added as a crisp number
	   * @return number which is the result of the operation
	   */	  
	public FuzzyNumber addScalar(double arg2) {
		return new LRFuzzyNumber(this.modalValue+arg2, this.Lfunction, this.Rfunction);
	}
	
	
		
	
	
   /** Fuzzy operation for multiplication between current FuzzyNumber and a scalar (<i>double</i>) value. <br>
	* First argument for the operation is the current object, and second argument
	* is given as a function parameter.
	* @param arg2 constant which will be multiplied as a crisp number
	* @return number which is the result of the operation
	*/	  
	public FuzzyNumber multiplyScalar(double arg2) {
		
		LRMembershipFunction l = (LRMembershipFunction) this.Lfunction.clone();
		LRMembershipFunction r = (LRMembershipFunction) this.Rfunction.clone();
		l.setSpread(l.getSpread()*Math.abs(arg2)); // multiply the spreads
		r.setSpread(r.getSpread()*Math.abs(arg2));

		if (arg2>0) {
			return new LRFuzzyNumber(this.modalValue*arg2, l, r);
		}
		else 
			if (arg2<0) {
			return new LRFuzzyNumber(this.modalValue*arg2, r, l);
			}
		
		// TODO: multiplication with crisp 0??? Sure, it is crisp zero [0,0,0]...
		// Since multiplication with fuzzy zero is not allowed(*)  I will disallow multiplication,
		// with crisp zero also.
		//
		// (*) Hanss, Michael, Applied Fuzzy Arithmetic: An Introduction with Engineering Applications, Springer-Verlag, New York, 2005.
		
		if (arg2==0) throw new FuzzyMultiplicationByZeroException();
		
		return null;

	}

	
	
	/**
	 * Calculates value of fuzzy number's membership function for wanted universal value
	 * 
	 * @see rs.ac.ns.ftn.tmd.fuzzy.FuzzyNumber#getMembershipValue(double)
	 * @return membership function result for given value
	 */
	public double getMembershipValue(double universalValue) {
		if (universalValue > this.modalValue) {
			// R-Function
			return this.Rfunction.getValue( universalValue - this.modalValue );
		}
		
		if (universalValue < this.modalValue) {
			// L-function
			return this.Lfunction.getValue( this.modalValue - universalValue );
			// we need positive value, because L function is defined for positive values
		}
		
		return 1; // else - it's exactly for modal value -> 1
	}

	
	

	/** 
	 * returns the modal value of current fuzzy number
	 */
	public double getModalValue() {
		return this.modalValue;
	}

	
	/**
	 * String representation of a fuzzy number
	 */
	@Override
	public String toString() {
		java.text.NumberFormat df = java.text.DecimalFormat.getNumberInstance();
		return "["+df.format(this.getModalValue())+", "+df.format(this.getLeftSpread())+", "+df.format(this.getRightSpread())+"]";
	}


	
   /**
	* Calculates left boundary of a fuzzy number. It is value of the worst case interval on the left side, 
	* value in which membership function has value 0+.<b>
	* @return left boundary value in universal set X
	*/
	public double getLeftBoundary() {
		return this.modalValue - this.Lfunction.getSpread();
	}

   /**
	* Calculates value of left spread, which is distance from the modal value to the left boundary.
	* @return distance from modal value to left boundary, which is always >0
	*/
	public double getLeftSpread() {
		return this.Lfunction.getSpread();
	}


  /**
	* Calculates right boundary of a fuzzy number. It is value of the worst case interval on the right side,
	* value in which membership function has value 0+.
	* @return right boundary value in universal set X
	*/
	public double getRightBoundary() {
		return this.modalValue + this.Rfunction.getSpread();
	}


	/**
	 * Calculates value of right spread, which is distance from the modal value to the right boundary.
	 * @return distance from modal value to right boundary, which is always >0
	 */
	public double getRightSpread() {
		return this.Rfunction.getSpread();
	}

	  
  /** Calculates the membership function area (integral) on interval <b>(-infinity, endPoint]</b><br/>
   * @param endPoint rightmost point, the endpoint of the (-infinity, endPoint] interval
   * @return area of the membership function shape
   */
	public double getAreaUntil(double endPoint) {
		// examine is the endPoint outside the fuzzy number support or inside left or right membership function
		if (endPoint<this.getLeftBoundary())
			return 0d;
		
		if (endPoint<=this.modalValue)
			return this.getLfunction().integrate(this.modalValue-endPoint,this.getLeftSpread()); 
		else
			return this.getLfunction().integrate(0, this.getLeftSpread()) // whole left funtion area
			      + this.getRfunction().integrate(0, endPoint-this.modalValue);
	}

	
  /** Calculates center of mass of the complete membership function
   * @return center belongs to X axis and for sure is inside membership function support interval 
   */
	public double getCenterOfMass() {
		// in this case we have set of simple objects and we can calculate their center of mass
		// by composing center formulas of simple objects and their areas
		// http://en.wikipedia.org/wiki/Center_of_mass#Of_a_composite_shape

		double centerL = this.Lfunction.getSpread() - getCenterOfPrimitive(this.getLfunction());
		double centerR = this.getLeftSpread()+getCenterOfPrimitive(this.getRfunction()); 
			
		double areaL = this.getLfunction().integrate(0,this.getLeftSpread()); 
		double areaR = this.getRfunction().integrate(0,this.getRightSpread());
		return this.getLeftBoundary()+(centerL*areaL+centerR*areaR)/(areaL+areaR);
	}

	
	/** returns the know coefficients for membership function shapes */
	protected double getCenterOfPrimitive(LRMembershipFunction f) {
		
		if (f.getSpread()==0) return 0;
		
		
		if (f.getClass().equals(LRMembershipFunctionLinear.class)) // rectangle
			return 1/3d*f.getSpread();
		

		if (f.getClass().equals(LRMembershipFunctionQuadratic.class)) // parabolic
			//http://www.efunda.com/math/areas/ParabolicHalf.cfm
			return 3/8d*f.getSpread();
		

		if (f.getClass().equals(LRMembershipFunctionExponential.class)) // exponential
			// The Center of Gravity of Plane Regions and Ruler and Compass Constructions
	        // by Tilak de Alwis
			// http://epatcm.any2any.us/EP/EP2002/ATCMA109/fullpaper.pdf
			return (LRMembershipFunctionExponential.cutOff*Math.exp(-LRMembershipFunctionExponential.cutOff)+(Math.exp(-LRMembershipFunctionExponential.cutOff)-1d))  /
				(-LRMembershipFunctionExponential.cutOff/f.getSpread() * (1d-Math.exp(-LRMembershipFunctionExponential.cutOff) ));
		
		
		
		if (f.getClass().equals(LRMembershipFunctionGaussian.class)) // Gaussian
			return ( f.getSpread() * (1-Math.exp(-Math.pow(LRMembershipFunctionGaussian.cutOff,2)/2d)) * Math.sqrt(2d/Math.PI)  ) /
				(LRMembershipFunctionGaussian.cutOff * LRMembershipFunctionGaussian.erf( LRMembershipFunctionGaussian.cutOff / Math.sqrt(2d)) );
		

		
		
		throw new RuntimeException("Unknown membership function "+f.getClass()+" - cannot determine Center of Mass");
		
	}



}
