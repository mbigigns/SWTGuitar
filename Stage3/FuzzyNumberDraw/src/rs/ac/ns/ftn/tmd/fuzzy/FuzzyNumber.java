/**
 */
package rs.ac.ns.ftn.tmd.fuzzy;

/**
 * A class which defines operations Fuzzy Numbers have to support, independantly to
 * its inner implementation. Generally operations of fuzzy arithmetics are defined here.
 * 
 * 
 * @author Nikola Kolarovic <nikola.kolarovic@gmail.com>
 * 
 */
public interface FuzzyNumber {



	/** value which denotes linear membership function */
	public static final int FUNCTION_LINEAR = 0x00;

	/** value which denotes Gaussian membership function */
	public static final int FUNCTION_GAUSSIAN = 0x01;

	/** value which denotes quadratic membership function */
	public static final int FUNCTION_QUADRATIC = 0x02;
	
	/** value which denotes exponential membership function */
	public static final int FUNCTION_EXPONENTIAL = 0x03;
	
	
	  /** Fuzzy operation for <b>addition</b> of two fuzzy numbers. <br>
	   * First argument for the operation is the current object, and second argument
	   * is given as a function parameter.<br><br>
	   * Method can return <b>UncompatibileFuzzyArithmeticsException</b> RuntimeException if fuzzy number implementations are not compatibile!<br>
	   * @param arg2 second argument for the operation
	   * @return number which is the result of the operation
	   */
	  public FuzzyNumber add(FuzzyNumber arg2);

	  /** Fuzzy operation for <b>substraction</b> of two fuzzy numbers. <br>
	   * First argument for the operation is the current object, and second argument
	   * is given as a function parameter.<br><br>
	   * Method can return <b>UncompatibileFuzzyArithmeticsException</b> RuntimeException if fuzzy number implementations are not compatibile!<br>

	   * @param arg2 second argument for the operation, which will be substracted from the current
	   * @return number which is the result of the operation
	   */
	  public FuzzyNumber substract(FuzzyNumber arg2);


	  /** Fuzzy operation for <b>multiplication</b> of two fuzzy numbers. <br>
	   * First argument for the operation is the current object, and second argument
	   * is given as a function parameter.<br>
	   * @throws <code>UncompatibileFuzzyArithmeticsException</code> if fuzzy number implementations are not compatibile<br>
	   * @param arg2 second argument for the operation
	   * @return number which is the result of the operation
	   */
	  public FuzzyNumber multiply(FuzzyNumber arg2);


	  /** Fuzzy operation for <b>division</b> of two fuzzy numbers. <br>
	   * First argument for the operation is the current object, and second argument
	   * is given as a function parameter.<br>
	   * @throws <code>UncompatibileFuzzyArithmeticsException</code> if fuzzy number implementations are not compatibile<br>
	   * @throws <code>FuzzyDivisionByZeroException</code> if number's confidence interval contains value 0<br>
	   * @param arg2 second argument for the operation
	   * @return number which is the result of the operation
	   */
	  public FuzzyNumber divide(FuzzyNumber arg2);

	  
	  /** Fuzzy operation for addition between current FuzzyNumber and a scalar (<i>double</i>) value. <br>
	   * First argument for the operation is the current object, and second argument
	   * is given as a function parameter.
	   * @param arg2 constant which will be added as a crisp number
	   * @return number which is the result of the operation
	   */	  
	  public FuzzyNumber addScalar(double arg2);


	  /** Fuzzy operation for multiplication between current FuzzyNumber and a scalar (<i>double</i>) value. <br>
	   * First argument for the operation is the current object, and second argument
	   * is given as a function parameter.<br>
	   * 
	   * Also for negation of FuzzyNumber, use multiplyScalar(-1).
	   * 
	   * @param arg2 constant which will be multiplied as a crisp number
	   * @return number which is the result of the operation
	   */	  
	  public FuzzyNumber multiplyScalar(double arg2);

	  
	  
	  /** Calculates new FuzzyNumber which represents the multiplication inverse fuzzy number for the current one.<br>
	   * <b>Basically, it is fuzzy 1/X function.</b><br>
	   * @throws <code>FuzzyDivisionByZeroException</code> if number's confidence interval contains value 0<br>
	   * @return fuzzy 1/X number
	   */ 
	  public FuzzyNumber getMultiplicationInverse();
	  
	  
	  
	  /** Calculates value of the membership function of a fuzzy number for given value.<br>
	   * 
	   * @param universalValue Value in universal (real) set for which we calculate membership function
	   * @return value of the membership function in given point -> [0,1]
	   */
	  public double getMembershipValue(double universalValue);

	  
	  
	  /** Calculates the modal value of a fuzzy number<br>
	   * Modal value is the universal value in which membership function value is reaching 1.
	   * @return value of X where <b><i>membershipFunction(X) == 1</i></b>
	   */
	  public double getModalValue();
	  
	  /**
	   * Calculates left boundary of a fuzzy number. It is value of the worst case interval on the left side, 
	   * value in which membership function has value 0+.<b>
	   * @return left boundary value in universal set X
	   */
	  public double getLeftBoundary();
	  
	  
	  /**
	   * Calculates right boundary of a fuzzy number. It is value of the worst case interval on the right side,
	   * value in which membership function has value 0+.
	   * @return right boundary value in universal set X
	   */
	  public double getRightBoundary();
	  
	  
	  /**
	   * Calculates value of left spread, which is distance from the modal value to the left boundary.
	   * @return distance from modal value to left boundary, which is always >0
	   */
	  public double getLeftSpread();
	  
	  /**
	   * Calculates value of right spread, which is distance from the modal value to the right boundary.
	   * @return distance from modal value to right boundary, which is always >0
	   */
	  public double getRightSpread();
	  
	  
	  /** Calculates the membership function area (integral) on interval <b>(-infinity, endPoint]</b>
	   * @param endPoint rightmost point, the endpoint of the (-infinity, endPoint] interval
	   * @return area of the membership function shape
	   */
	  public double getAreaUntil(double endPoint);
	  
	  
	  /** Calculates center of mass of the complete membership function
	   * @return center belongs to X axis and for sure is inside membership function support interval 
	   */
	  public double getCenterOfMass();
	  
	  
	  
}