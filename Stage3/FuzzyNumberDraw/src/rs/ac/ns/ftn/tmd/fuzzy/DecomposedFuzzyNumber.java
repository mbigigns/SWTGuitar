/**
 * 
 */
package rs.ac.ns.ftn.tmd.fuzzy;

import rs.ac.ns.ftn.tmd.fuzzy.exceptions.FuzzyDivisionByZeroException;
import rs.ac.ns.ftn.tmd.fuzzy.exceptions.UncompatibileFuzzyArithmeticsException;
import rs.ac.ns.ftn.tmd.fuzzy.membershipFunction.LRMembershipFunctionExponential;
import rs.ac.ns.ftn.tmd.fuzzy.membershipFunction.LRMembershipFunctionGaussian;

/**
 * 
 * Implementation of decomposed fuzzy numbers and fuzzy arithmetics between them
 * 
 * 
 * @author Nikola Kolarovic <nikola.kolarovic@gmail.com>
 *
 */


public class DecomposedFuzzyNumber implements FuzzyNumber {
	
	protected int CUTS_NUMBER;
	
	protected double left[];
	protected double right[];
	

	/** Factory constructor for L-R fuzzy numbers
	 * 
	 * @param modalValue central value of a fuzzy number
	 * @param alpha worst-case interval <b>left</b> of modal value
	 * @param beta worst-case interval <b>right</b> of modal value
	 * @param Ltype type of function for Left function
	 * @param Rtype type of function for Right function
	 * @param cuts_number number of alpha sub-intervals
	 */ 
	public DecomposedFuzzyNumber(double modalValue, double alpha, double beta, int Ltype, int Rtype, int cuts_number ) {
		
		assert(cuts_number>1);
		
		this.CUTS_NUMBER = cuts_number;
		
		this.left = new double[this.CUTS_NUMBER];
		this.right = new double[this.CUTS_NUMBER];
		
		this.left[this.CUTS_NUMBER-1] = this.right[this.CUTS_NUMBER-1] = modalValue;
		
		// linear alpha-cutting
		for (int i=0; i<this.CUTS_NUMBER-1; i++) {
			this.left[i] = modalValue-this.getAlphaCut((double)i/(double)(this.CUTS_NUMBER-1), alpha, Ltype);
			this.right[i] = modalValue+this.getAlphaCut((double)i/(double)(this.CUTS_NUMBER-1), beta, Rtype);
		}
		
	}
	
	
	/**
	 * Protected constructor for interior use; to get number from modified alpha-cuts
	 * @param left left points
	 * @param right right points
	 */
	protected DecomposedFuzzyNumber(double[] left, double[] right) {
		assert( left.length == right.length );
		
		this.CUTS_NUMBER = left.length;
		this.left = left;
		this.right = right;
	}
	
	
	// -------------------------- INTERNAL FUNCTIONS ------------------------------------------
	
	/** Calculates alpha-cut value; uses the inverse function of a membership function
	 * 
	 * @param membershipGrade membership function value; "alpha" in alpha-cut
	 * @param spread spread of the function, max value
	 * @param type type of the function
	 * @return left or right member of alpha-cut interval
	 */ 
	protected double getAlphaCut(double membershipGrade, double spread, int type) {
		
		// TODO: correct when adding a new function type
		assert (type>=0 && type<= FuzzyNumber.FUNCTION_EXPONENTIAL);
		
		assert (spread>=0);
		assert (membershipGrade>=0 && membershipGrade<1);
		
		// if there is no spread then every value is modalValue
		if (spread==0) return 0;
		if (membershipGrade==0) return spread;

		// depending on type
		switch (type) {
			case FuzzyNumber.FUNCTION_LINEAR :
							return (1d-membershipGrade)*spread;
			
			case FuzzyNumber.FUNCTION_GAUSSIAN :
							// x = SQRT(  - ln(y)* 2* variance^2  )
							double x = Math.sqrt( Math.log(membershipGrade)*(-2d)*(spread*spread/Math.pow(LRMembershipFunctionGaussian.cutOff,2)));
							// TODO: for some high CUTS_NUMBER value this can get bigger than spread,
							// which is an artificial boundary (because function is infinite)
							if (x>spread) return spread;
								else return x;
			
			case FuzzyNumber.FUNCTION_QUADRATIC :
							return spread*Math.sqrt(1-membershipGrade);

			case FuzzyNumber.FUNCTION_EXPONENTIAL :
							// X =  - ln(y)*variance
							double x2 = -1d*Math.log( membershipGrade )*spread / LRMembershipFunctionExponential.cutOff;
							// TODO: for some high CUTS_NUMBER value this can get bigger than spread,
							// which is an artificial boundary (because function is infinite)
							if (x2>spread) return spread;
								else return x2;
							
							
			default : return -1;
		}
	}
	
	
	/** package-visible getter for left alpha-cuts
	 * 
	 * @return left array
	 */
	double[] getLeftCuts() {
		return this.left;
	}
	
	/** package-visible getter for right alpha-cuts
	 * 
	 * @return right array
	 */
	double[] getRightCuts() {
		return this.right;
	}
	
	
	/** checks if two fuzzy number interpretations are same.<br>
	 *  If not, throw a <b>UncompatibileFuzzyArithmeticsException</b> with a message.
	 * @param second compare type of current object with the "second" one.
	 */
	protected void checkImplementationCompatibilityWith(FuzzyNumber second) {
		
		if (this.CUTS_NUMBER != ((DecomposedFuzzyNumber)second).CUTS_NUMBER) // check for array size
			throw new UncompatibileFuzzyArithmeticsException("Incompatibile decomposed fuzzy numbers - CUTS number is different: "+this.CUTS_NUMBER+" and "+((DecomposedFuzzyNumber)second).CUTS_NUMBER);
			
		if ( ! this.getClass().equals(second.getClass()))
			throw new UncompatibileFuzzyArithmeticsException(this.getClass(),second.getClass());
		
		
	}
	
	
	//------------------------- ARITHMETIC OPERATORS ---------------------------------------------
	
	
	
	/* (non-Javadoc)
	 * @see yu.ac.ns.ftn.tmd.fuzzy.FuzzyNumber#add(yu.ac.ns.ftn.tmd.fuzzy.FuzzyNumber)
	 */
	public FuzzyNumber add(FuzzyNumber arg2) {
		
		this.checkImplementationCompatibilityWith(arg2);
		
		double[] left2 = ((DecomposedFuzzyNumber)arg2).getLeftCuts();
		double[] right2 = ((DecomposedFuzzyNumber)arg2).getRightCuts();
		
		double[] newLeft = new double[this.CUTS_NUMBER];
		double[] newRight = new double[this.CUTS_NUMBER];
		for (int i=0; i<this.CUTS_NUMBER; i++) {
			newLeft[i] = this.left[i]+left2[i];
			newRight[i] = this.right[i]+right2[i];
		}
		
		return new DecomposedFuzzyNumber(newLeft, newRight);
	}

	
	
	/* (non-Javadoc)
	 * @see yu.ac.ns.ftn.tmd.fuzzy.FuzzyNumber#addScalar(double)
	 */
	public FuzzyNumber addScalar(double arg2) {
		double[] newLeft = new double[this.CUTS_NUMBER];
		double[] newRight = new double[this.CUTS_NUMBER];
		for (int i=0; i<this.CUTS_NUMBER; i++) {
			newLeft[i] = this.left[i]+arg2;
			newRight[i] = this.right[i]+arg2;
		}
		return new DecomposedFuzzyNumber(newLeft,newRight);
	}
	
	

	/* (non-Javadoc)
	 * @see yu.ac.ns.ftn.tmd.fuzzy.FuzzyNumber#getMultiplicationInverse()
	 */
	public FuzzyNumber getMultiplicationInverse() {
		
		// exception for division by zero
		if ((this.getModalValue() >= 0 && this.getLeftBoundary()<=0) || 
				(this.getModalValue() <= 0 && this.getRightBoundary()>=0))
			throw new FuzzyDivisionByZeroException();

		
		double[] newLeft = new double[this.CUTS_NUMBER];
		double[] newRight = new double[this.CUTS_NUMBER];
			
		for (int i=0; i<this.CUTS_NUMBER; i++) {
			
			
			newLeft[i] = Math.min(1/this.left[i],
			        			1/this.right[i]);
	
			newRight[i] = Math.max(
			        		 1/this.left[i],
			        		 1/this.right[i]);
		}
		return new DecomposedFuzzyNumber(newLeft,newRight);
		
	}
	
	
	/* (non-Javadoc)
	 * @see yu.ac.ns.ftn.tmd.fuzzy.FuzzyNumber#divide(yu.ac.ns.ftn.tmd.fuzzy.FuzzyNumber)
	 */
	public FuzzyNumber divide(FuzzyNumber arg2) {
		
		this.checkImplementationCompatibilityWith(arg2);
		
		// exception for division by zero
		if ((arg2.getModalValue() >= 0 && arg2.getLeftBoundary()<=0) || 
				(arg2.getModalValue() <= 0 && arg2.getRightBoundary()>=0))
			throw new FuzzyDivisionByZeroException();
		
		
		double[] left2 = ((DecomposedFuzzyNumber)arg2).getLeftCuts();
		double[] right2 = ((DecomposedFuzzyNumber)arg2).getRightCuts();
		
		double[] newLeft = new double[this.CUTS_NUMBER];
		double[] newRight = new double[this.CUTS_NUMBER];
		for (int i=0; i<this.CUTS_NUMBER; i++) {

			newLeft[i] = Math.min(
					        Math.min(this.left[i]/left2[i],
					        		 this.left[i]/right2[i]),
					        Math.min(this.right[i]/left2[i],
					        		 this.right[i]/right2[i]));
			
			newRight[i] = Math.max(
					        Math.max(this.left[i]/left2[i],
					        		 this.left[i]/right2[i]),
					        Math.max(this.right[i]/left2[i],
					        		 this.right[i]/right2[i]));

		}
		
		return new DecomposedFuzzyNumber(newLeft, newRight);
	}

	

	/* (non-Javadoc)
	 * @see yu.ac.ns.ftn.tmd.fuzzy.FuzzyNumber#multiply(yu.ac.ns.ftn.tmd.fuzzy.FuzzyNumber)
	 */
	public FuzzyNumber multiply(FuzzyNumber arg2) {
		
		this.checkImplementationCompatibilityWith(arg2);
		
		double[] left2 = ((DecomposedFuzzyNumber)arg2).getLeftCuts();
		double[] right2 = ((DecomposedFuzzyNumber)arg2).getRightCuts();
		
		double[] newLeft = new double[this.CUTS_NUMBER];
		double[] newRight = new double[this.CUTS_NUMBER];
		for (int i=0; i<this.CUTS_NUMBER; i++) {
			newLeft[i] = Math.min(
					        Math.min(this.left[i]*left2[i],
					        		 this.left[i]*right2[i]),
					        Math.min(this.right[i]*left2[i],
					        		 this.right[i]*right2[i]));
					        		 
			newRight[i] = Math.max(
					        Math.max(this.left[i]*left2[i],
					        		 this.left[i]*right2[i]),
					        Math.max(this.right[i]*left2[i],
					        		 this.right[i]*right2[i]));

		}
		
		return new DecomposedFuzzyNumber(newLeft, newRight);
	}

	/* (non-Javadoc)
	 * @see yu.ac.ns.ftn.tmd.fuzzy.FuzzyNumber#multiplyScalar(double)
	 */
	public FuzzyNumber multiplyScalar(double arg2) {
		double[] newLeft = new double[this.CUTS_NUMBER];
		double[] newRight = new double[this.CUTS_NUMBER];
		for (int i=0; i<this.CUTS_NUMBER; i++) {
			newLeft[i] = this.left[i]*arg2;
			newRight[i] = this.right[i]*arg2;
		}
		
		if (arg2>0) // if positive then everything is OK
			return new DecomposedFuzzyNumber(newLeft,newRight);
		else // inverse the sides
			return new DecomposedFuzzyNumber(newRight, newLeft);

	}

	
	
	/* (non-Javadoc)
	 * @see yu.ac.ns.ftn.tmd.fuzzy.FuzzyNumber#substract(yu.ac.ns.ftn.tmd.fuzzy.FuzzyNumber)
	 */
	public FuzzyNumber substract(FuzzyNumber arg2) {
		
		this.checkImplementationCompatibilityWith(arg2);
		
		double[] left2 = ((DecomposedFuzzyNumber)arg2).getLeftCuts();
		double[] right2 = ((DecomposedFuzzyNumber)arg2).getRightCuts();
		
		double[] newLeft = new double[this.CUTS_NUMBER];
		double[] newRight = new double[this.CUTS_NUMBER];
		for (int i=0; i<this.CUTS_NUMBER; i++) {
			newLeft[i] = this.left[i]-right2[i];
			newRight[i] = this.right[i]-left2[i];
		}
		
		return new DecomposedFuzzyNumber(newLeft, newRight);
	}
	
	//------------------------- ADDITIONAL FUNCITONS ---------------------------------------------
	
  /** Calculates value of the membership function of a fuzzy number for given value.<br>
   * 
   * @param universalValue Value in universal (real) set for which we calculate membership function
   * @return value of the membership function in given point -> [0,1]
   */
	public double getMembershipValue(double universalValue) {
		if (universalValue >= this.getRightBoundary() || universalValue <= this.getLeftBoundary()) return 0d; // outside of the support
		if (universalValue == this.getModalValue()) return 1d; // modal value
		
		if (universalValue< this.getModalValue()) { // left side
			double previous = this.getLeftBoundary();
			for (int i=0; i<this.left.length; i++) {
				if (this.left[i] >= universalValue) {
					// LINEAR APPROXIMATION IN BETWEEN INTERVAL
					// TODO: change here if sampling isn't equidistant anymore
					return (i==0 ? 0d : (double)(i-1))/(double)(this.CUTS_NUMBER-1) + (1d/(double)(this.CUTS_NUMBER-1)) *
					(universalValue-previous) / (this.left[i]-previous);
				}
				else
					previous=this.left[i];
			}
			
		}
		else { // on the right side
			double previous=this.right[0];
			
			for (int i=0; i<this.right.length; i++) {
				if (this.right[i] <= universalValue) {
					// LINEAR APPROXIMATION IN BETWEEN INTERVAL
					// TODO: change here if sampling isn't equidistant anymore
					return (i==0 ? 0d : (double)(i-1))/(double)(this.CUTS_NUMBER-1) + (1d/(double)(this.CUTS_NUMBER-1)) *
							(previous-universalValue) / (previous-this.right[i]);
				}
				else
					previous= this.right[i];
			}
		
		}
		return -1; // unreachable anyway
		
	}

	
	
	/* (non-Javadoc)
	 * @see yu.ac.ns.ftn.tmd.fuzzy.FuzzyNumber#getModalValue()
	 */
	public double getModalValue() {
		return this.left[this.left.length-1];
	}


	/* (non-Javadoc)
	 * @see yu.ac.ns.ftn.tmd.fuzzy.FuzzyNumber#getLeftBoundary()
	 */
	public double getLeftBoundary() {
		return this.left[0];
	}

	/* (non-Javadoc)
	 * @see yu.ac.ns.ftn.tmd.fuzzy.FuzzyNumber#getLeftSpread()
	 */
	public double getLeftSpread() {
		return this.getModalValue() - this.getLeftBoundary();
	}
	
	/* (non-Javadoc)
	 * @see yu.ac.ns.ftn.tmd.fuzzy.FuzzyNumber#getRightBoundary()
	 */
	public double getRightBoundary() {
		return this.right[0];
	}

	/* (non-Javadoc)
	 * @see yu.ac.ns.ftn.tmd.fuzzy.FuzzyNumber#getRightSpread()
	 */
	public double getRightSpread() {
		return this.getRightBoundary()-this.getModalValue();
	}

	
  /** Calculates the membership function area (integral) on interval <b>(-infinity, endPoint]</b>
   * @param endPoint rightmost point, the endpoint of the (-infinity, endPoint] interval
   * @return area of the membership function shape
   */
	public double getAreaUntil(double endPoint) {
		double retValue = 0d;
		
		if (endPoint<this.getLeftBoundary()) return 0;
		
		int i=0;
		while (endPoint>this.left[i] && i<this.CUTS_NUMBER-1) {
			retValue += getSegmentAreaApproximation(  (double)i/(double)(this.CUTS_NUMBER-1), (double)(i+1)/(double)(this.CUTS_NUMBER-1), this.left[i+1]-this.left[i]);
			i++;
		}
		
		if (endPoint <= this.getModalValue()) { // we ended before whole left part, so we have spare area
			retValue-=getSegmentAreaApproximation(getMembershipValue(endPoint), (double)(i)/(double)(this.CUTS_NUMBER-1), this.left[i]-endPoint);
			return retValue;
			
		}
	
		
		assert(i==this.CUTS_NUMBER-1);
		while (endPoint>this.right[i] && i>0) {
			i--;
			retValue += getSegmentAreaApproximation( (double)(i+1)/(double)(this.CUTS_NUMBER-1), (double)i/(double)(this.CUTS_NUMBER-1), this.right[i]-this.right[i+1]);
		}
		if (i!=0) { // ended before the rightmost, we have some spare area
			retValue-=getSegmentAreaApproximation(getMembershipValue(endPoint), (double)(i)/(double)(this.CUTS_NUMBER-1), this.right[i]-endPoint);
		}
		
		
		return retValue;
	}

	
	
	/** Calculates the approximate integral value of the segment
	 * 
	 * @param leftY membership function value in left end of the segment
	 * @param rightY membership function value in right end of the segment
	 * @param spaceBetween segment length
	 * @return integral value (area)
	 */
	protected double getSegmentAreaApproximation(double beginY, double endY, double spaceBetween) {
	// we will aproximate the curve with a line between two points
		return spaceBetween*( (beginY<endY ? beginY : endY) + Math.abs(beginY-endY)/2 );
	
	}

  /** Calculates center of mass of the complete membership function
   * @return center belongs to X axis and for sure is inside membership function support interval 
   */
	public double getCenterOfMass() {
		double areas=0, cogsAreas=0;
		
		//  COG = sum( area[i]*COG[i] ) / sum(area[i])
		
		// TODO: COG is assumed to be the center of the subinterval for the calculation speed. It is not true,
		// but the error is really small and compensates on the other side if the spreads are fairly balanced.
		
		for (int i=0; i<this.CUTS_NUMBER-1; i++) {
			double tmpArea = getSegmentAreaApproximation((double)i/(double)(this.CUTS_NUMBER-1),(double)(i+1)/(double)(this.CUTS_NUMBER-1),this.left[i+1]-this.left[i]); 
			areas+= tmpArea;
			cogsAreas+=tmpArea*(left[i+1]+left[i])/2d;
			
			tmpArea = getSegmentAreaApproximation((double)i/(double)(this.CUTS_NUMBER-1),(double)(i+1)/(double)(this.CUTS_NUMBER-1),this.right[i]-this.right[i+1]);
			areas+=tmpArea;
			cogsAreas+=tmpArea*(right[i+1]+right[i])/2d;
		}
		
		return cogsAreas/areas;
	}
	
	
	
	@Override
	public String toString() {
		
		java.text.NumberFormat df = java.text.DecimalFormat.getNumberInstance();
		return "["+df.format(this.getModalValue())+", "+df.format(this.getLeftSpread())+", "+df.format(this.getRightSpread())+"]";
	}



}
