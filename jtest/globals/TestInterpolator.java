package globals;

import static org.junit.Assert.*;
import gov.sandia.gmp.util.globals.Globals;

import org.junit.Test;

public class TestInterpolator {
	
	private static double[] xinc = new double[] {10., 20., 30., 40., 50.};
	private static double[] xdec = new double[] {-10., -20., -30., -40., -50.};
	private static double[] y = new double[] {100., 200., 300., 400., 500.};

	@Test
	public void testHunt1() {
		for (int i=0; i<xinc.length-1; ++i)
			assertEquals(i, Globals.hunt(xinc, xinc[i]));
		
		assertEquals(xinc.length-2, Globals.hunt(xinc, xinc[xinc.length-1]));
	}

	@Test
	public void testHunt2() {
		for (int i=0; i<xinc.length-1; ++i)
			assertEquals(i, Globals.hunt(xinc, xinc[i]+1.));
	}

	@Test
	public void testHunt3() {
		assertEquals(-1, Globals.hunt(xinc, xinc[0]-1.));
	}

	@Test
	public void testHunt4() {
		assertEquals(xinc.length-1, Globals.hunt(xinc, xinc[xinc.length-1]+1., false, false));
	}


	@Test
	public void testHunt11() {
		for (int i=0; i<xinc.length-1; ++i)
			assertEquals(i, Globals.hunt(xinc, xinc[i], false, false));
		
		assertEquals(xinc.length-2, Globals.hunt(xinc, xinc[xinc.length-1], false, false));
	}

	@Test
	public void testHunt21() {
		for (int i=0; i<xinc.length-1; ++i)
			assertEquals(i, Globals.hunt(xinc, xinc[i]+1., false, false));
	}

	@Test
	public void testHunt31() {
		assertEquals(-1, Globals.hunt(xinc, -999., false, false));
	}

	@Test
	public void testHunt41() {
		assertEquals(xinc.length-1, Globals.hunt(xinc, 999., false, false));
	}


	@Test
	public void testHunt12() {
		for (int i=0; i<xinc.length-1; ++i)
			assertEquals(i, Globals.hunt(xinc, xinc[i], true, true));
		
		assertEquals(xinc.length-2, Globals.hunt(xinc, xinc[xinc.length-1], true, true));
	}

	@Test
	public void testHunt22() {
		for (int i=0; i<xinc.length-1; ++i)
			assertEquals(i, Globals.hunt(xinc, xinc[i]+1., true, true));
	}

	@Test
	public void testHunt32() {
		assertEquals(0, Globals.hunt(xinc, -999., true, true));
	}

	@Test
	public void testHunt42() {
		assertEquals(xinc.length-2, Globals.hunt(xinc, 999., true, true));
	}


	
	
	
	@Test
	public void interpolate1() {
		for (int i=0; i<xinc.length; ++i)
			assertEquals(y[i], Globals.interpolate(xinc, y, xinc[i]), 1e-9);
	}

	@Test
	public void interpolate2() {
		for (int i=0; i<xinc.length-1; ++i)
			assertEquals(y[i]+10., Globals.interpolate(xinc, y, xinc[i]+1.), 1e-9);
	}

	@Test
	public void interpolate3() {
		assertTrue(Double.isNaN(Globals.interpolate(xinc, y, -1.)));
		assertTrue(Double.isNaN(Globals.interpolate(xinc, y, 51.)));
	}


	@Test
	public void interpolate10() {
		for (int i=0; i<xinc.length; ++i)
			assertEquals(y[i], Globals.interpolate(xinc, y, xinc[i], false, false), 1e-9);
	}

	@Test
	public void interpolate20() {
		for (int i=0; i<xinc.length-1; ++i)
			assertEquals(y[i]+10., Globals.interpolate(xinc, y, xinc[i]+1., false, false), 1e-9);
	}

	@Test
	public void interpolate30() {
		assertTrue(Double.isNaN(Globals.interpolate(xinc, y, -999., false, false)));
		assertTrue(Double.isNaN(Globals.interpolate(xinc, y, 999., false, false)));
	}

	@Test
	public void interpolate11() {
		for (int i=0; i<xinc.length; ++i)
			assertEquals(y[i], Globals.interpolate(xinc, y, xinc[i], true, true), 1e-9);
	}

	@Test
	public void interpolate21() {
		for (int i=0; i<xinc.length-1; ++i)
			assertEquals(y[i]+10., Globals.interpolate(xinc, y, xinc[i]+1., true, true), 1e-9);
	}

	@Test
	public void interpolate31() {
		assertEquals(y[0], Globals.interpolate(xinc, y, -999., true, true), 1e-9);
		assertEquals(y[y.length-1], Globals.interpolate(xinc, y, 999., true, true), 1e-9);
	}

	///////////////////// x decreasing:  
	
	@Test
	public void testHuntDecreasing1() {
		for (int i=0; i<xinc.length-1; ++i)
			assertEquals(i, Globals.hunt(xinc, xinc[i]));
		
		assertEquals(xdec.length-2, Globals.hunt(xdec, xdec[xdec.length-1]));
	}

	@Test
	public void testHuntDecreasing2() {
		for (int i=0; i<xdec.length-1; ++i)
			assertEquals(i, Globals.hunt(xdec, xdec[i]-1.));
	}

	@Test
	public void testHuntDecreasing3() {
		assertEquals(-1, Globals.hunt(xdec, xdec[0]+1.));
	}

	@Test
	public void testHuntDecreasing4() {
		assertEquals(xdec.length-1, Globals.hunt(xdec, xdec[xdec.length-1]-1., false, false));
	}


	@Test
	public void testHuntDecreasing11() {
		for (int i=1; i<xdec.length; ++i)
			assertEquals(i-1, Globals.hunt(xdec, xdec[i], false, false));
		
		assertEquals(0, Globals.hunt(xdec, xdec[0], false, false));
	}

	@Test
	public void testHuntDecreasing21() {
		for (int i=1; i<xdec.length; ++i)
			assertEquals(i, Globals.hunt(xdec, xdec[i]-1., false, false));
	}

	@Test
	public void testHuntDecreasing31() {
		assertEquals(-1, Globals.hunt(xdec, 999., false, false));
	}

	@Test
	public void testHuntDecreasing41() {
		assertEquals(xdec.length-1, Globals.hunt(xdec, -999., false, false));
	}


	@Test
	public void testHuntDecreasing12() {
		for (int i=1; i<xdec.length; ++i)
			assertEquals(i-1, Globals.hunt(xdec, xdec[i], true, true));
		
		assertEquals(0, Globals.hunt(xdec, xdec[0], true, true));
	}

	@Test
	public void testHuntDecreasing22() {
		for (int i=1; i<xdec.length; ++i)
			assertEquals(i-1, Globals.hunt(xdec, xdec[i]+1., true, true));
	}

	@Test
	public void testHuntDecreasing32() {
		assertEquals(0, Globals.hunt(xdec, 999., true, true));
	}

	@Test
	public void testHuntDecreasing42() {
		assertEquals(xdec.length-2, Globals.hunt(xdec, -999., true, true));
	}


	
	
	
	@Test
	public void interpolateDecreasing1() {
		for (int i=0; i<xdec.length; ++i)
			assertEquals(y[i], Globals.interpolate(xdec, y, xdec[i]), 1e-9);
	}

	@Test
	public void interpolateDecreasing2() {
		for (int i=0; i<xdec.length-1; ++i)
			assertEquals(y[i]+10., Globals.interpolate(xdec, y, xdec[i]-1.), 1e-9);
	}

	@Test
	public void interpolateDecreasing3() {
		assertTrue(Double.isNaN(Globals.interpolate(xdec, y, +1.)));
		assertTrue(Double.isNaN(Globals.interpolate(xdec, y, 51.)));
	}


	@Test
	public void interpolateDecreasing10() {
		for (int i=0; i<xdec.length; ++i)
			assertEquals(y[i], Globals.interpolate(xdec, y, xdec[i], false, false), 1e-9);
	}

	@Test
	public void interpolateDecreasing20() {
		for (int i=0; i<xdec.length-1; ++i)
			assertEquals(y[i]+10., Globals.interpolate(xdec, y, xdec[i]-1., false, false), 1e-9);
	}

	@Test
	public void interpolateDecreasing30() {
		assertTrue(Double.isNaN(Globals.interpolate(xdec, y, -999., false, false)));
		assertTrue(Double.isNaN(Globals.interpolate(xdec, y, 999., false, false)));
	}

	@Test
	public void interpolateDecreasing11() {
		for (int i=0; i<xdec.length; ++i)
			assertEquals(y[i], Globals.interpolate(xdec, y, xdec[i], true, true), 1e-9);
	}

	@Test
	public void interpolateDecreasing21() {
		for (int i=0; i<xdec.length-1; ++i)
			assertEquals(y[i]+10., Globals.interpolate(xdec, y, xdec[i]-1., true, true), 1e-9);
	}

	@Test
	public void interpolateDecreasing31() {
		assertEquals(y[0], Globals.interpolate(xdec, y, 999., true, true), 1e-9);
		assertEquals(y[y.length-1], Globals.interpolate(xdec, y, -999., true, true), 1e-9);
	}





}
