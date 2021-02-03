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

package numerical.matrix;

import gov.sandia.gmp.util.numerical.doubledouble.DoubleDouble;

import java.util.Random;

public class DoubleDoubleTester
{

  // tests
  //   add(double a, double b)                                   this  = a + b
  //   add(DoubleDouble a, double b)                             this  = a + b
  //   add(DoubleDouble a, DoubleDouble b)                       this  = a + b 
  //   addFast(double[] t, double ahi, double alo)               t    += a
  //
  //   subtract(double a, double b)                              this  = a - b
  //   subtract(DoubleDouble a, double b)                        this  = a - b
  //   subtract(double a, DoubleDouble b)                        this  = a - b
  //   subtract(DoubleDouble a, DoubleDouble b)                  this  = a - b 
  //
  //   mult(double a, double b)                                  this  = a * b
  //   mult(DoubleDouble a, double b)                            this  = a * b
  //   mult(DoubleDouble a, DoubleDouble b)                      this  = a * b 
  //   multFast(double[] t, double ahi, double alo)              t    *= a
  //   addMultFast(double[] t, double a, double bhi, double blo) t    += a * b
  //
  //   div(double a, double b)                                   this  = a / b
  //   div(DoubleDouble a, double b)                             this  = a / b
  //   div(DoubleDouble a, DoubleDouble b)                       this  = a / b 
  //
  //   sqr(double a)                                             this  = a * a                                              
  //   sqr(DoubleDouble a)                                       this  = a * a                                              
  //   addSqrFast(double[] t, double ahi, double alo)            t    += a * a
  //   sqrt(DoubleDouble a)                                      this  = sqrt(a)                                              

  /**
   * @param args
   */
  public static void main(String[] args)
  {
    DoubleDoubleTester ddt = new DoubleDoubleTester();
    //ddt.test1();
    //ddt.test2();
    ddt.test3();
  }

  public void test1()
  {
    Random rnd = new Random();
    int n = 100;
    double[] r1 = new double [n];
    double[] r2 = new double [n];
    DoubleDouble[] ddr = new DoubleDouble [n];
    for (int i = 0; i < n; ++i)
    {
      r1[i] = rnd.nextGaussian();
      r2[i] = rnd.nextGaussian();
    }

    DoubleDouble ddrsum = new DoubleDouble();
    double rsum = 0.0;
    for (int i = 0; i < n; ++i)
    {
      ddr[i] = new DoubleDouble();
      ddr[i].add(r1[i], r2[i]);
      ddrsum.add(ddr[i]);
      rsum += r1[i] + r2[i];
      System.out.println("i = " + i + ", ddr.hi = " + ddr[i].hi +
                         ", ddr.lo = " + ddr[i].lo +
                         ", r1 + r2 - ddr.hi= " + (r1[i] + r2[i] - ddr[i].hi));
    }
    System.out.println("ddrsum.hi = " + ddrsum.hi + ", ddrsum.lo = " +
                       ddrsum.lo + ", rsum = " + rsum + ", ddrsum.hi - rsum = " +
                       (ddrsum.hi - rsum));
    for (int i = 0; i < n; ++i)
    {
      rsum -= (r1[i] + r2[i]);
      ddrsum.subtract(ddr[i]);
    }
    System.out.println("ddrsum.hi = " + ddrsum.hi + ", ddrsum.lo = " +
        ddrsum.lo + ", rsum = " + rsum + ", ddrsum.hi - rsum = " +
        (ddrsum.hi - rsum));
  }

  public void test2()
  {
    Random rnd = new Random();
    int n = 100;
    double[] r1 = new double [n];
    double[] r2 = new double [n];
    DoubleDouble[] ddr = new DoubleDouble [n];
    for (int i = 0; i < n; ++i)
    {
      r1[i] = rnd.nextGaussian();
      r2[i] = rnd.nextGaussian();
    }

    DoubleDouble ddrsum = new DoubleDouble(1.0);
    double rsum = 1.0;
    for (int i = 0; i < n; ++i)
    {
      ddr[i] = new DoubleDouble();
      ddr[i].mult(r1[i], r2[i]);
      ddrsum.mult(ddr[i]);
      rsum *= (r1[i] * r2[i]);
      System.out.println("i = " + i + ", ddr.hi = " + ddr[i].hi +
                         ", ddr.lo = " + ddr[i].lo +
                         ", r1 + r2 - ddr.hi= " + (r1[i] + r2[i] - ddr[i].hi));
    }
    System.out.println("ddrsum.hi = " + ddrsum.hi + ", ddrsum.lo = " +
                       ddrsum.lo + ", rsum = " + rsum + ", ddrsum.hi - rsum = " +
                       (ddrsum.hi - rsum));
    for (int i = 0; i < n; ++i)
    {
      rsum /= (r1[i] * r2[i]);
      ddrsum.div(ddr[i]);
    }
    System.out.println("ddrsum.hi = " + ddrsum.hi + ", ddrsum.lo = " +
        ddrsum.lo + ", rsum = " + rsum + ", ddrsum.hi - rsum = " +
        (ddrsum.hi - rsum));
  }

  public void test3()
  {
    // verify that the "Fast" functions produce the same result as their slower
    // counter parts
    //   addFast(double[] t, double ahi, double alo)               t    += a
    //   multFast(double[] t, double ahi, double alo)              t    *= a
    //   addMultFast(double[] t, double a, double bhi, double blo) t    += a * b
    //   addSqrFast(double[] t, double ahi, double alo)            t    += a * a

    // set a to an initial DoubleDouble value

    Random rnd = new Random();
    double r1 = rnd.nextGaussian();
    double r2 = rnd.nextGaussian();
    double r3 = rnd.nextGaussian();
    double r4 = rnd.nextGaussian();
    double r5 = rnd.nextGaussian();

    // create a and d DoubleDouble objects from r1, r2, r3, and r4
    // d1 = d2 = d3 = d4 ... a is different

    DoubleDouble a = new DoubleDouble();
    a.add(r1, r2);
    DoubleDouble d1 = new DoubleDouble();
    d1.add(r3, r4);
    DoubleDouble d2 = new DoubleDouble(d1);
    DoubleDouble d3 = new DoubleDouble(d1);
    DoubleDouble d4 = new DoubleDouble(d1);

    // set t to initial d DoubleDouble values

    double[] t1 = {d1.hi, d1.lo};
    double[] t2 = {d2.hi, d2.lo};
    double[] t3 = {d3.hi, d3.lo};
    double[] t4 = {d4.hi, d4.lo};

    // perform "Fast" and normal equivalents and output results ... should
    // be the same result using both methods.

    DoubleDouble.addFast(t1, a.hi, a.lo);
    d1.add(a);
    System.out.println("Test addFast");
    System.out.println("t1[0] = " + t1[0] + ", t1[1] = " + t1[1]);
    System.out.println("d1.hi = " + d1.hi + ", d1.lo = " + d1.lo);
    System.out.println("");
    
    DoubleDouble.multFast(t2, a.hi, a.lo);
    d2.mult(a);
    System.out.println("Test multFast");
    System.out.println("t2[0] = " + t2[0] + ", t2[1] = " + t2[1]);
    System.out.println("d2.hi = " + d2.hi + ", d2.lo = " + d2.lo);
    System.out.println("");
    
    DoubleDouble.addMultFast(t3, r5, a.hi, a.lo);
    DoubleDouble d3t = new DoubleDouble();
    d3t.mult(a, r5);
    d3.add(d3t);
    System.out.println("Test addMultFast");
    System.out.println("t3[0] = " + t3[0] + ", t3[1] = " + t3[1]);
    System.out.println("d3.hi = " + d3.hi + ", d3.lo = " + d3.lo);
    System.out.println("");
    
    DoubleDouble.addSqrFast(t4, a.hi, a.lo);
    DoubleDouble d4t = new DoubleDouble();
    d4t.sqr(a);
    d4.add(d4t);
    System.out.println("Test addSqrFast");
    System.out.println("t4[0] = " + t4[0] + ", t4[1] = " + t4[1]);
    System.out.println("d4.hi = " + d4.hi + ", d4.lo = " + d4.lo);
    System.out.println("");
  }
}
