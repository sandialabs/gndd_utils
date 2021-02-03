//- ****************************************************************************
//-
//- Copyright 2009 Sandia Corporation. Under the terms of Contract
//- DE-AC04-94AL85000 with Sandia Corporation, the U.S. Government
//- retains certain rights in this software.
//-
//- BSD Open Source License.
//- All rights reserved.
//-
//- Redistribution and use in source and binary forms, with or without
//- modification, are permitted provided that the following conditions are met:
//-
//-    * Redistributions of source code must retain the above copyright notice,
//-      this list of conditions and the following disclaimer.
//-    * Redistributions in binary form must reproduce the above copyright
//-      notice, this list of conditions and the following disclaimer in the
//-      documentation and/or other materials provided with the distribution.
//-    * Neither the name of Sandia National Laboratories nor the names of its
//-      contributors may be used to endorse or promote products derived from
//-      this software without specific prior written permission.
//-
//- THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
//- AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
//- IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
//- ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
//- LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
//- CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
//- SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
//- INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
//- CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
//- ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
//- POSSIBILITY OF SUCH DAMAGE.
//-
//- ****************************************************************************

// Module:        $RCSfile: BrentsTest.java,v $
// Revision:      $Revision: 1.5 $

package numerical.brents;

import gov.sandia.gmp.util.numerical.brents.Brents;
import gov.sandia.gmp.util.numerical.brents.BrentsFunction;

import static gov.sandia.gmp.util.numerical.machine.DhbMath.defaultNumericalPrecision;

/**
 * <p>Title: BrentsTest</p>
 *
 * <p> Example uses of the Brents zeroF, minF, and maxF functions. Run
 * the main to execute. Note that the zeroF tolerance setting can be set
 * to zero which will find the nearest zero to within the defined
 * machine precision. The minF and maxF tolerance, however, should not be
 * set smaller than the default numerical precision (about 1.0e-8) since
 * its search is over the value of f(x) instead of the range of x. The
 * examples below illustrate this nicely.
 */
public class BrentsTest implements BrentsFunction
{
  private int tstFC = 0;
  private int tstid = 0;

  /**
   * @param args
   */
  public static void main(String[] args)
  {
    BrentsTest bt = new BrentsTest();
    try
    {
      bt.test1();
      bt.test2();
      bt.test3();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }

  public void test1() throws Exception
  {
    // set test id and output header

    tstid = 1;
    System.out.println("Brents Zero-In Test");
    System.out.println("------------------------------------------------");
    System.out.println("Function      = 47 - (2 * x^3 - 3 * x + 2)");
    System.out.println("Search Limits = 0.0 to 4.0");
    System.out.println("Zero          = 3.0");
    
    // create a Brents object and set this BrentsTest as the implementing
    // BrentsFunction object.
    
    Brents b = new Brents();
    b.setFunction(this);

    // set the tolerance and function call counter and zeroin

    b.setTolerance(1.0e-4);
    tstFC = 0;
    double x1 = b.zeroF(0.0, 4.0);
    double f1 = bFunc(x1);
    System.out.println("Case 1: Tolerance       = " + b.getTolerance());
    System.out.println("        x (zero)        = " + x1);
    System.out.println("        f(x)            = " + f1);
    System.out.println("        f(x) Call Count = " + (tstFC - 1));
    
    // set the tolerance and function call counter and zeroin

    b.setTolerance(1.0e-8);
    tstFC = 0;
    double x2 = b.zeroF(0.0, 4.0);
    double f2 = bFunc(x2);
    System.out.println("Case 2: Tolerance       = " + b.getTolerance());
    System.out.println("        x (zero)        = " + x2);
    System.out.println("        f(x)            = " + f2);
    System.out.println("        f(x) Call Count = " + (tstFC - 1));
    
    // set the tolerance and function call counter and zeroin

    b.setTolerance(0.0);
    tstFC = 0;
    double x3 = b.zeroF(0.0, 4.0);
    double f3 = bFunc(x3);
    System.out.println("Case 3: Tolerance       = " + b.getTolerance());
    System.out.println("        x (zero)        = " + x3);
    System.out.println("        f(x)            = " + f3);
    System.out.println("        f(x) Call Count = " + (tstFC - 1));

    System.out.println("");
  }

  public void test2()
  {
    // set test id and output header

    tstid = 2;
    System.out.println("Brents Function Minimum Test");
    System.out.println("---------------------------------------------------");
    System.out.println("Function      = 2 * x^3 - 3 * x + 2");
    System.out.println("Search Limits = 0.0 to 1.0");
    System.out.println("Minimum       = sqrt(2)/2   = 0.7071067811865475");
    System.out.println("Default Numerical Precision = " +
                       defaultNumericalPrecision());
 
    // create a Brents object and set this BrentsTest as the implementing
    // BrentsFunction object.
    
    Brents b = new Brents();
    b.setFunction(this);

    try
    {
      // set the tolerance and function call counter and find minimum

      b.setTolerance(1.0e-4);
      tstFC = 0;
      double f1 = b.minF(0.0, 1.0);
      double x1 = b.getExtremaAbscissa();
      System.out.println("Case 1: Tolerance       = " + b.getTolerance());
      System.out.println("        x (min)         = " + x1);
      System.out.println("        f(x)            = " + f1);
      System.out.println("        f(x) Call Count = " + (tstFC - 1));
      
      // set the tolerance and function call counter and find minimum

      b.setTolerance(1.0e-8);
      tstFC = 0;
      double f2 = b.minF(0.0, 1.0);
      double x2 = b.getExtremaAbscissa();
      System.out.println("Case 2: Tolerance       = " + b.getTolerance());
      System.out.println("        x (min)         = " + x2);
      System.out.println("        f(x)            = " + f2);
      System.out.println("        f(x) Call Count = " + (tstFC - 1));
      
      // set the tolerance and function call counter and find minimum

      b.setTolerance(1.0e-12);
      tstFC = 0;
      double f3 = b.minF(0.0, 1.0);
      double x3 = b.getExtremaAbscissa();
      System.out.println("Case 3: Tolerance       = " + b.getTolerance());
      System.out.println("        x (min)         = " + x3);
      System.out.println("        f(x)            = " + f3);
      System.out.println("        f(x) Call Count = " + (tstFC - 1));
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    System.out.println("");
  }

  public void test3()
  {
    // set test id and output header

    tstid = 3;
    System.out.println("Brents Function Maximum Test");
    System.out.println("---------------------------------------------------");
    System.out.println("Function      = 2 * x^3 - 3 * x + 2");
    System.out.println("Search Limits = -1.0 to 0.0");
    System.out.println("Maximum       = -sqrt(2)/2  = -0.7071067811865475");
    System.out.println("Default Numerical Precision = " +
                       defaultNumericalPrecision());
 
    // create a Brents object and set this BrentsTest as the implementing
    // BrentsFunction object.
    
    Brents b = new Brents();
    b.setFunction(this);

    try
    {
      // set the tolerance and function call counter and find maximum

      b.setTolerance(1.0e-4);
      tstFC = 0;
      double f1 = b.maxF(-1.0, 0.0);
      double x1 = b.getExtremaAbscissa();
      System.out.println("Case 1: Tolerance       = " + b.getTolerance());
      System.out.println("        x (max)         = " + x1);
      System.out.println("        f(x)            = " + f1);
      System.out.println("        f(x) Call Count = " + (tstFC - 1));
      
      // set the tolerance and function call counter and find maximum

      b.setTolerance(1.0e-8);
      tstFC = 0;
      double f2 = b.maxF(-1.0, 0.0);
      double x2 = b.getExtremaAbscissa();
      System.out.println("Case 2: Tolerance       = " + b.getTolerance());
      System.out.println("        x (max)         = " + x2);
      System.out.println("        f(x)            = " + f2);
      System.out.println("        f(x) Call Count = " + (tstFC - 1));
      
      // set the tolerance and function call counter find maximum

      b.setTolerance(1.0e-12);
      tstFC = 0;
      double f3 = b.maxF(-1.0, 0.0);
      double x3 = b.getExtremaAbscissa();
      System.out.println("Case 3: Tolerance       = " + b.getTolerance());
      System.out.println("        x (max)         = " + x3);
      System.out.println("        f(x)            = " + f3);
      System.out.println("        f(x) Call Count = " + (tstFC - 1));
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    System.out.println("");
  }

  public double bFunc(double x)
  {
    ++tstFC;
    if (tstid == 1)
      return 47.0 - (2.0 * x * x * x - 3.0 * x + 2.0);
    else if ((tstid == 2) || (tstid == 3))
      return 2.0 * x * x * x - 3.0 * x + 2.0;
    else
      return 0.0;
  }
}
