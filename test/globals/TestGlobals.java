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

package globals;

import gov.sandia.gmp.util.globals.Globals;
import org.junit.*;

import static java.lang.Math.PI;
import static org.junit.Assert.*;

public class TestGlobals {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  @AfterClass
  public static void tearDownAfterClass() throws Exception {}

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}
  
  @Test
  public void hunt() {
    /// Find index i such that xx is >= x[i] and < x[i+1]. 
    // If xx <  x[0] returns -1. 
    // If xx == x[xx.length-1] return x.length-2
    // If xx >  x[xx.length-1] return x.length-1
    
    double[] x = new double[] {10., 20., 30.};
    assertEquals(-1, Globals.hunt(x, 9.));
    assertEquals(0, Globals.hunt(x, 10.));
    assertEquals(0, Globals.hunt(x, 15.));
    assertEquals(1, Globals.hunt(x, 20.));
    assertEquals(1, Globals.hunt(x, 25.));
    assertEquals(1, Globals.hunt(x, 30.));
    assertEquals(2, Globals.hunt(x, 31.));
  }

  @Test
  public void conditionAzimuth() {
    
    assertEquals(0., Globals.conditionAz(2*PI), 1e-6);
    assertEquals(0., Globals.conditionAz(6*PI), 1e-6);   
    assertEquals(0., Globals.conditionAz(-6*PI), 1e-6);
    assertEquals(PI, Globals.conditionAz(-PI), 1e-6);
    assertEquals(3*PI/2, Globals.conditionAz(-PI/2), 1e-6);
    assertTrue(Double.isNaN(Globals.conditionAz(Double.NaN)));
    assertNotEquals(-999., Globals.conditionAz(-999.), 1e-6);
  }

  @Test
  public void conditionAzimuth2() {
    double NA = -999.;
    assertEquals(0., Globals.conditionAz(2*PI, NA), 1e-6);
    assertEquals(0., Globals.conditionAz(6*PI, NA), 1e-6);   
    assertEquals(0., Globals.conditionAz(-6*PI, NA), 1e-6);
    assertEquals(PI, Globals.conditionAz(-PI, NA), 1e-6);
    assertEquals(3*PI/2, Globals.conditionAz(-PI/2, NA), 1e-6);
    assertEquals(NA, Globals.conditionAz(Double.NaN, NA), 1e-6);
    assertEquals(NA, Globals.conditionAz(NA, NA), 1e-6);
  }

  @Test
  public void conditionAzimuth3() {
    double NA = -999.;
    assertEquals(0., Globals.conditionAz(2*PI, NA, 3), 1e-6);
    assertEquals(0., Globals.conditionAz(6*PI, NA, 3), 1e-6);   
    assertEquals(0., Globals.conditionAz(-6*PI, NA, 3), 1e-6);
    assertEquals(3.142000, Globals.conditionAz(-PI, NA, 3), 1e-6);
    assertEquals(4.712000, Globals.conditionAz(-PI/2, NA, 3), 1e-6);
    assertEquals(NA, Globals.conditionAz(Double.NaN, NA, 3), 1e-6);
    assertEquals(NA, Globals.conditionAz(NA, NA, 3), 1e-6);
  }

  @Test
  public void conditionAzimuthDegrees() {
    
    assertEquals(0., Globals.conditionAzDegrees(360.), 1e-6);
    assertEquals(0., Globals.conditionAzDegrees(6*360.), 1e-6);   
    assertEquals(0., Globals.conditionAzDegrees(-6*360.), 1e-6);
    assertEquals(180., Globals.conditionAzDegrees(-180.), 1e-6);
    assertEquals(270., Globals.conditionAzDegrees(-90.), 1e-6);
    assertTrue(Double.isNaN(Globals.conditionAzDegrees(Double.NaN)));
    assertNotEquals(-999., Globals.conditionAzDegrees(-999.), 1e-6);
  }

  @Test
  public void conditionAzimuthDegrees2() {
    double NA = -999.;
    assertEquals(0., Globals.conditionAzDegrees(360., NA), 1e-6);
    assertEquals(0., Globals.conditionAzDegrees(6*360., NA), 1e-6);   
    assertEquals(0., Globals.conditionAzDegrees(-6*360., NA), 1e-6);
    assertEquals(180., Globals.conditionAzDegrees(-180., NA), 1e-6);
    assertEquals(270., Globals.conditionAzDegrees(-90., NA), 1e-6);
    assertEquals(NA, Globals.conditionAz(Double.NaN, NA), 1e-6);
    assertEquals(NA, Globals.conditionAzDegrees(NA, NA), 1e-6);
  }

  @Test
  public void conditionAzimuthDegrees3() {
    double NA = -999.;
    assertEquals(0.123000, Globals.conditionAzDegrees(360.123456, NA, 3), 1e-6);
    assertEquals(5.123000, Globals.conditionAzDegrees(725.123456, NA, 3), 1e-6);   
    assertEquals(354.877, Globals.conditionAzDegrees(-725.123456, NA, 3), 1e-6);
    assertEquals(NA, Globals.conditionAz(Double.NaN, NA, 3), 1e-6);
    assertEquals(NA, Globals.conditionAzDegrees(NA, NA, 3), 1e-6);
  }

}
