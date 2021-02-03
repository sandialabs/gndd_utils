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

// Module:        $RCSfile: LBFGSTest.java,v $
// Revision:      $Revision: 1.3 $

package numerical.lbfgs;

import gov.sandia.gmp.util.numerical.lbfgs.LBFGS;
import gov.sandia.gmp.util.numerical.lbfgs.LBFGSException;
import gov.sandia.gmp.util.numerical.lbfgs.LBFGSFunction;

import static gov.sandia.gmp.util.globals.Globals.NL;
/**
 * <p>Title: LBFGSTest</p>
 *
 * <p> Example use of the LBFGS.lbfgs function. Run
 * the main to execute.
 */
public class LBFGSTest implements LBFGSFunction
{
	public static void main(String args[])
	{
	  LBFGSTest sdt = new LBFGSTest();

	  sdt.test();
	}
	
	public void test()
	{
    int n = 100;
		double[] x;
		x = new double [n];

		double eps;
		int corrkept, j;
		//boolean diagco;
		
		corrkept = 5;
		
		//diagco    = false;
		eps       = 1.0e-5;

		for (j = 0; j < n; j += 2)
		{
			x[j]     = -1.2;
			x[j + 1] = 1.0;
		}

		LBFGS opt = new LBFGS();
    opt.setOutputCount(1);
		//opt.setOutputOff();
    //opt.setOutputAmountBasic();
    opt.setOutputAmount(0);
    opt.setAccuracyTolerance(eps);
    opt.setCorrectionCount(corrkept);
    opt.setLBFGSFunction(this);
    
		try
		{
			opt.lbfgs(x);
		}
		catch (LBFGSException e)
		{
			System.err.println("Sdrive: lbfgs failed." + NL + e);
			return;
		}
	}
	
	public double setFunctionAndGradient(double[] x, double[] g)
	{
    double f = 0.0;
    int j;
    for (j = 0; j < x.length; j += 2)
    {
      double t1 = 1.0 - x[j];
      double t2 = 10.0 * (x[j + 1] - x[j] * x[j]);
      g[j + 1] = 20.0 * t2;
      g[j]     = -2.0 * (x[j] * g[j + 1] + t1);
      f += t1 * t1 + t2 * t2;
    }
    return f;
	}
	
	public void setDiagonal(double[] x, double[] diag)
	{
	}
}
