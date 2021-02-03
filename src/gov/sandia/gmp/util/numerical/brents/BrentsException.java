// Module:        $RCSfile: BrentsException.java,v $
// Revision:      $Revision: 1.2 $

package gov.sandia.gmp.util.numerical.brents;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 * @version 1.0
 */
@SuppressWarnings("serial")
public class BrentsException extends Exception
{
  public BrentsException()
  {
    super();
  }

  public BrentsException(String string)
  {
      super(string);
  }
  
  public BrentsException(String string, Throwable throwable)
  {
      super(string, throwable);
  }
  
  public BrentsException(Throwable throwable)
  {
      super(throwable);
  }
}
