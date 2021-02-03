// Module:        $RCSfile: BrentsFunction.java,v $
// Revision:      $Revision: 1.3 $

package gov.sandia.gmp.util.numerical.brents;

/**
 * An interface used by the Brents.zeroF, Brents.minF, and Brents.maxF
 * functions that contain the function bFunc(double) to be overridden by
 * the implementor of this interface to provide a function return value
 * at the input abscissa x.
 */
public interface BrentsFunction
{
    /**
     * Evaluates the function to be zeroed, minimized, or maximized at the input
     * value x.
     *
     * @param x The input value for which the function is evaluated.
     * @return The return function value evaluated at x.
     * @throws Exception
     */
    abstract double bFunc(double x) throws Exception;
}
