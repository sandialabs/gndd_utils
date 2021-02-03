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

// Module:        $RCSfile: MachineTest.java,v $
// Revision:      $Revision: 1.3 $

package numerical.machine;

import gov.sandia.gmp.util.numerical.machine.DhbMath;

public class MachineTest
{

  /**
   * Simple test illustrating the machine precision functionality.
   * 
   * @param args Not used.
   */
  public static void main(String[] args)
  {
    System.out.println("Floating-point machine parameters");
    System.out.println("---------------------------------");
    System.out.println("radix = " + DhbMath.getRadix());
    System.out.println("Machine precision = " + DhbMath.getMachinePrecision());
    System.out.println("Default numerical precision = " +
                       DhbMath.defaultNumericalPrecision());
    System.out.println(DhbMath.equals(2.71828182845905,
                                      (2.71828182845904 + 0.00000000000001)));
    System.out.println(DhbMath.equals(2.71828182845905, 2.71828182845904));
    System.out.println(DhbMath.equals(2.718281828454, 2.718281828455));
    System.out.println(DhbMath.equals(2.7182814, 2.7182815));
  }

}
