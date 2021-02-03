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

package gov.sandia.gmp.util.numerical.polygon;

import gov.sandia.gmp.util.numerical.polygon.GreatCircle.GreatCircleException;
import gov.sandia.gmp.util.numerical.vector.VectorGeo;
import gov.sandia.gmp.util.numerical.vector.VectorUnit;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static java.lang.Math.*;
import static org.junit.Assert.*;

public class PolygonTest
{
	@BeforeClass
	public static void setUpBeforeClass()
	{
		System.out.println("PolygonTest");

	}

	@Test
	public void testContains() throws IOException
	{
		Polygon polygon = new Polygon(VectorGeo.getVectorDegrees(0, 0), 
				toRadians(10), 6);

		// test the reference point

		// get  a copy of the reference point which is in the polygon
		double[] u = VectorGeo.getVectorDegrees(1, 0);

		assertTrue(polygon.contains(u));

		// find the antipode of the reference point.  
		u[0] = -u[0];
		u[1] = -u[1];
		u[2] = -u[2];

		assertFalse(polygon.contains(u));

	}

	@Test
	public void testReferencePoint() throws IOException
	{
		Polygon polygon = new Polygon(VectorGeo.getVectorDegrees(0, 0), 
				toRadians(10), 200);

		assertTrue(polygon.contains(VectorGeo.getVectorDegrees(1, 1)));

	}

	@Test
	public void testArea() throws IOException
	{
		Polygon polygon;
		ArrayList<double[]> p;			

		// polygon area test.  Make a polygon defined by 3 points
		// that should occupy 1/8 th of the sphere
		p = new ArrayList<double[]>();

		p.add(VectorGeo.getVectorDegrees(90, 0));
		p.add(VectorGeo.getVectorDegrees(0, 90));
		p.add(VectorGeo.getVectorDegrees(0, 0));

		polygon = new Polygon(p);

		// area of entire sphere is 4*PI so 1/8 of the 
		// sphere should be exactly PI/2

		assertEquals(polygon.getArea(), PI/2, 0);

	}

	@Test
	public void testSetReferencePoint() throws IOException 
	{
		// make a polygon that is bigger than a hemisphere 
		Polygon polygon = new Polygon(VectorGeo.getVectorDegrees(0, 0), 0.6*PI, 6);

		// area is greater than area of a hemisphere
		assertTrue(polygon.getArea() > 2*PI);

		// invert the polygon
		polygon.invert();

		// area is now smaller than area of hemisphere 
		assertTrue(polygon.getArea() < 2*PI);

		// set reference point to a different point inside the small polygon
		polygon.setReferencePoint(VectorGeo.getVectorDegrees(0, 179), true);

		// area is still less than a hemisphere.
		assertTrue(polygon.getArea() < 2*PI);

		// set reference point to a different point outside the small polygon
		polygon.setReferencePoint(VectorGeo.getVectorDegrees(0, 0), true);

		// area is now greater than a hemisphere.
		assertTrue(polygon.getArea() > 2*PI);

		// set reference point to a different point outside the small polygon
		// but set referenceIn of that point to false
		polygon.setReferencePoint(VectorGeo.getVectorDegrees(0, 10), false);

		// area is now smaller than a hemisphere.
		assertTrue(polygon.getArea() < 2*PI);

		// set reference point to a different point inside the small polygon
		// but set referenceIn of that point to false
		polygon.setReferencePoint(VectorGeo.getVectorDegrees(0, 179), false);

		// area is now bigger than a hemisphere.
		assertTrue(polygon.getArea() > 2*PI);

		// set reference point to a point on the boundary.  this should fail
		String errorMessage = "no error";
		try
		{
			polygon.setReferencePoint(polygon.getPoint(2), false);
		}
		catch (Exception ex)
		{
			errorMessage = ex.getMessage();
		}
		assertFalse(errorMessage.equals("no error"));
	}

	@Test
	public void testArea2() throws IOException
	{
		Polygon polygon;
		ArrayList<double[]> p;			

		p = new ArrayList<double[]>();

		// lat-lon box 20 x 20
		p.add(VectorGeo.getVectorDegrees(20, 0));
		p.add(VectorGeo.getVectorDegrees(20, 20));
		p.add(VectorGeo.getVectorDegrees(-20, 20));
		p.add(VectorGeo.getVectorDegrees(-20, 0));

		polygon = new Polygon(p);

		double areaBig = polygon.getArea();

		p = new ArrayList<double[]>();

		// lat-lon box 10 x 10
		p.add(VectorGeo.getVectorDegrees(10, 0));
		p.add(VectorGeo.getVectorDegrees(10, 10));
		p.add(VectorGeo.getVectorDegrees(-10, 10));
		p.add(VectorGeo.getVectorDegrees(-10, 0));

		polygon = new Polygon(p);

		double areaSmall = polygon.getArea();

		p = new ArrayList<double[]>();

		// C shaped polygon 
		p.add(VectorGeo.getVectorDegrees(20, 0));
		p.add(VectorGeo.getVectorDegrees(20, 20));
		p.add(VectorGeo.getVectorDegrees(-20, 20));
		p.add(VectorGeo.getVectorDegrees(-20, 0));
		p.add(VectorGeo.getVectorDegrees(-10, 0));
		p.add(VectorGeo.getVectorDegrees(-10, 10));
		p.add(VectorGeo.getVectorDegrees(10, 10));
		p.add(VectorGeo.getVectorDegrees(10, 0));

		polygon = new Polygon(p);

		double areaDiff = polygon.getArea();

		assertEquals(areaDiff, areaBig-areaSmall, 1e-9);

	}

	@Test
	public void triangleTest() throws IOException
	{
		// create a triangular polygon.

		Polygon polygon;
		ArrayList<double[]> p = new ArrayList<double[]>();

		double[] center = VectorGeo.getVectorDegrees(0, 0);
		double[] point = VectorGeo.getVectorDegrees(10, 0);
		double[] point1 = new double[3];
		VectorGeo.rotate(point, center, Math.toRadians(120.), point1);
		double[] point2 = new double[3];
		VectorGeo.rotate(point, center, -Math.toRadians(120.), point2);

		p.add(point);
		p.add(point1);
		p.add(point2);

		polygon = new Polygon(p);

		// set the reference point on the prime meridian, south of the polygon
		polygon.setReferencePoint(-20,  0, false);

		assertTrue(polygon.contains(VectorGeo.getVectorDegrees(0, 0)));

	}

	@Test
	public void parallelTest() throws IOException
	{
		// create a polygon with a bunch of points on a great circle.
		// Put the reference point on the same great circle then
		// test a bunch of points that are also on the great circle.

		Polygon polygon;
		ArrayList<double[]> p = new ArrayList<double[]>();

		p.add(VectorGeo.getVectorDegrees(0, 10));

		// add 5 points on the prime meridian
		p.add(VectorGeo.getVectorDegrees(0, 0));
		p.add(VectorGeo.getVectorDegrees(10, 0));
		p.add(VectorGeo.getVectorDegrees(20, 0));
		p.add(VectorGeo.getVectorDegrees(30, 0));
		p.add(VectorGeo.getVectorDegrees(40, 0));

		p.add(VectorGeo.getVectorDegrees(40, 10));

		polygon = new Polygon(p);

		// set the reference point on the prime meridian
		polygon.setReferencePoint(-10,  0, false);

		// now test some points along the prime meridian
		for (int lat=-80; lat <= -4; lat += 4)
			assertFalse(polygon.contains(VectorGeo.getVectorDegrees(lat, 0)));

		for (int lat=0; lat <= 40; lat += 4)
			assertTrue(polygon.contains(VectorGeo.getVectorDegrees(lat, 0)));

		for (int lat=41; lat <= 80; lat += 4)
			assertFalse(polygon.contains(VectorGeo.getVectorDegrees(lat, 0)));


		p.clear();
		p.add(VectorGeo.getVectorDegrees(0, 10));

		// add 5 points on the prime meridian
		p.add(VectorGeo.getVectorDegrees(0, 0));
		p.add(VectorGeo.getVectorDegrees(10, 0));
		p.add(VectorGeo.getVectorDegrees(20, 0));
		p.add(VectorGeo.getVectorDegrees(30, 0));
		p.add(VectorGeo.getVectorDegrees(40, 0));

		p.add(VectorGeo.getVectorDegrees(40, -10));
		p.add(VectorGeo.getVectorDegrees(50, -10));
		p.add(VectorGeo.getVectorDegrees(50, 10));

		// this polygon looks like an upside down L
		polygon = new Polygon(p);

		// set the reference point on the prime meridian
		polygon.setReferencePoint(-10,  0, false);

		// now test some points along the prime meridian
		for (int lat=-80; lat <= -4; lat += 4)
			assertFalse(polygon.contains(VectorGeo.getVectorDegrees(lat, 0)));

		for (int lat=0; lat <= 50; lat += 5)
			assertTrue(polygon.contains(VectorGeo.getVectorDegrees(lat, 0)));

		for (int lat=51; lat <= 80; lat += 4)
			assertFalse(polygon.contains(VectorGeo.getVectorDegrees(lat, 0)));

	}

	@Test
	public void zigzagTest() throws IOException
	{
		// next test. This is the zigzag test.  A polygon is generated with
		// a bunch of edges that straddle and/or are coincident with a 
		// meridian.  The reference point is at the south pole and 
		// evaluation points are tested that lie along the meridian.
		// Many but not all of the evaluation points are on the boundary
		// of the polygon.  
		Polygon polygon;

		ArrayList<double[]> p = new ArrayList<double[]>();

		double[] u;

		int[] offset = new int[100];
		offset[3] = 1;
		offset[6] = 1;
		offset[8] = 1;
		offset[10] = -1;
		offset[14] = -1;
		offset[17] = -1;

		double lat0 = -36, lon0 = 20;
		for (int i=0; i<19; ++i)
			p.add(VectorGeo.getVectorDegrees(lat0 + i*4, lon0 + offset[i] * 4));

		p.add(VectorGeo.getVectorDegrees(36, -lon0));
		p.add(VectorGeo.getVectorDegrees(-36, -lon0));

		// identify the set of latitudes that should be inside the polygon.
		HashSet<Integer> in = new HashSet<Integer>();
		for (int lat = -36; lat <= 0; ++lat)
			in.add(lat);

		for (int lat = 8; lat <= 16; ++lat)
			in.add(lat);

		for (int lat = 24; lat <= 28; ++lat)
			in.add(lat);

		in.add(36);

		polygon = new Polygon(p);

		for (int i = 0; i<p.size(); ++i)
		{
			polygon.setReferencePoint(VectorGeo.getVectorDegrees(-90, 0), false);

			for (int lat=-40; lat <= 40; lat += 1)
			{
				if (i == 4 && lat == -15)
					System.out.print("");

				u = VectorGeo.getVectorDegrees(lat, lon0);

				assertTrue(String.format("i=%d lat=%d %b%n", i, lat, polygon.contains(u)),
						polygon.contains(u) == in.contains(lat));
			}

			// move the first element from the front to the back of the 
			// list of points.
			p.add(p.remove(0));

			polygon = new Polygon(p);
		}

	}

	@Test
	public void bigC() throws IOException
	{
		Polygon polygon;

		double[] u = new double[3];

		// next test: this is the big S test
		//File f = new File("T:\\models\\polygons\\big_S.kmz");
		File f = new File("permanent_files/big_S.kmzxxxxxxx");
		if (f.exists())
		{
			polygon = new Polygon(f);
			//			System.out.println("List<double[]> points = new ArrayList<>();");
			//			for (double[] x : polygon.getPoints(false))
			//				System.out.printf("points.add(VectorGeo.getVectorDegrees(%10.6f, %10.6f));%n", 
			//						VectorGeo.getLatDegrees(x), VectorGeo.getLonDegrees(x));
		}
		else
		{
			List<double[]> points = new ArrayList<>();
			points.add(VectorGeo.getVectorDegrees( 20.214397,  10.145348));
			points.add(VectorGeo.getVectorDegrees( 19.953430,   0.168170));
			points.add(VectorGeo.getVectorDegrees( 19.641653,  -9.842449));
			points.add(VectorGeo.getVectorDegrees( -0.024768, -10.062174));
			points.add(VectorGeo.getVectorDegrees( -0.179824,   0.042712));
			points.add(VectorGeo.getVectorDegrees( -0.318843,   9.862468));
			points.add(VectorGeo.getVectorDegrees(-10.154935,  10.152866));
			points.add(VectorGeo.getVectorDegrees( -9.809014,   0.241262));
			points.add(VectorGeo.getVectorDegrees(-10.395455,  -9.947260));
			points.add(VectorGeo.getVectorDegrees(-19.489859,  -9.798267));
			points.add(VectorGeo.getVectorDegrees(-19.988530,   0.077607));
			points.add(VectorGeo.getVectorDegrees(-20.063482,  15.031869));
			points.add(VectorGeo.getVectorDegrees(-10.202712,  13.952905));
			points.add(VectorGeo.getVectorDegrees(  5.561897,  13.125034));
			points.add(VectorGeo.getVectorDegrees(  5.075012,  -5.000178));
			points.add(VectorGeo.getVectorDegrees( 15.082525,  -5.594684));
			points.add(VectorGeo.getVectorDegrees( 15.386862,   9.935908));
			polygon = new Polygon(points);
		}

		HashMap<Integer, Boolean> expected = new HashMap<Integer, Boolean>(50);
		expected.put(-25, false);
		expected.put(-24, false);
		expected.put(-23, false);
		expected.put(-22, false);
		expected.put(-21, false);
		expected.put(-20, false);
		expected.put(-19, true);
		expected.put(-18, true);
		expected.put(-17, true);
		expected.put(-16, true);
		expected.put(-15, true);
		expected.put(-14, true);
		expected.put(-13, true);
		expected.put(-12, true);
		expected.put(-11, true);
		expected.put(-10, true);
		expected.put(-9, false);
		expected.put(-8, false);
		expected.put(-7, false);
		expected.put(-6, false);
		expected.put(-5, false);
		expected.put(-4, false);
		expected.put(-3, false);
		expected.put(-2, false);
		expected.put(-1, false);
		expected.put(0, true);
		expected.put(1, true);
		expected.put(2, true);
		expected.put(3, true);
		expected.put(4, true);
		expected.put(5, true);
		expected.put(6, false);
		expected.put(7, false);
		expected.put(8, false);
		expected.put(9, false);
		expected.put(10, false);
		expected.put(11, false);
		expected.put(12, false);
		expected.put(13, false);
		expected.put(14, false);
		expected.put(15, false);
		expected.put(16, true);
		expected.put(17, true);
		expected.put(18, true);
		expected.put(19, true);
		expected.put(20, false);
		expected.put(21, false);
		expected.put(22, false);
		expected.put(23, false);
		expected.put(24, false);
		expected.put(25, false);

		for (int lat = -25; lat <= 25; lat += 1)
		{
			VectorGeo.getVectorDegrees(lat, 0, u);

			//System.out.println("HashMap<Integer, Boolean> expected = new HashMap<Integer, Boolean>(50);");
			//System.out.printf("expected.put(%d, %b);%n", lat, polygon.contains(u));

			assertTrue(expected.get(lat) == polygon.contains(u));
		}

		polygon.invert();

		for (int lat = -25; lat <= 25; lat += 1)
		{
			VectorGeo.getVectorDegrees(lat, 0, u);
			assertTrue(expected.get(lat) != polygon.contains(u));
		}

	}

	@Test
	public void equatorTest() throws IOException
	{
		// next test: make a polygon that has 3 adjacent points on the
		// equator. generate random points very close to the middle of 
		// the three polygon points and see if they are contained or 
		// not. If contained and latitude < 0, or !contained and 
		// latitude > 0, test fails.

		Polygon polygon;

		ArrayList<double[]> p = new ArrayList<double[]>();

		double[] u = new double[3];

		p = new ArrayList<double[]>();
		p.add(VectorGeo.getVectorDegrees(0, 20));
		p.add(VectorGeo.getVectorDegrees(0, 0));
		p.add(VectorGeo.getVectorDegrees(0, -20));
		p.add(VectorGeo.getVectorDegrees(90, 0));

		polygon = new Polygon(p);

		for (int i = 0; i < 100; ++i)
		{
			VectorGeo.getVector(
					10 * Polygon.getTolerance() * (2 * random() - 1.), 
					10 * Polygon.getTolerance() * (2 * random() - 1.), 
					u);

			assertTrue(String.format("%20.15f %20.15f %10.8f %-5s %-5s %s%n",
					VectorGeo.getLatDegrees(u),
					VectorGeo.getLonDegrees(u),
					VectorUnit.angle(u, new double[] { 1, 0, 0 }),
					polygon.onBoundary(u), polygon.contains(u),
					Arrays.toString(u)), 
					polygon.onBoundary(u) || polygon.contains(u) == (u[2] > 0.));

		}
	}

	@Test
	public void greatCircleTest() throws GreatCircleException, IOException
	{
		Polygon polygon;

		ArrayList<double[]> p = new ArrayList<double[]>();

		double[] u = new double[3];

		// Make a 4-point polygon where all the points lie on a great circle.  This 
		// should be a challenge for the Polygon class.
		p = new ArrayList<double[]>();

		GreatCircle gc = new GreatCircle(VectorGeo.getVectorDegrees(30., 30.), 2*PI, PI/4);

		p.add(gc.getPoint(0));
		p.add(gc.getPoint(PI/2));
		p.add(gc.getPoint(PI));
		p.add(gc.getPoint(3*PI/2));
		//p.add(gc.getPoint(2*PI));

		polygon = new Polygon(p);

		// area should be 2*PI

		assertEquals(polygon.getArea(), 2*PI, 0);

		// inside and outside are pretty arbitrary

		u = new double[] {0., -1., 0.};

		if (polygon.contains(u))
			polygon.invert();

		assertFalse(polygon.contains(u));

		u = new double[] {0., 1., 0.};

		assertTrue(polygon.contains(u));
	}

	//@Test
	public void asciiFile3D() throws GreatCircleException, IOException
	{
		File f = new File("permanent_files/polygon3d_africa.ascii");

		if (f.exists())
		{
			Polygon3D polygon = new Polygon3D(f);

			//System.out.println(polygon);

			double[] xin = VectorGeo.getVectorDegrees(6, 10);
			double[] xout = new double[] {0,0,1};

			assertTrue(polygon.contains(xin));

			assertFalse(polygon.contains(xout));

			assertFalse(polygon.contains(xin, 0));
			assertFalse(polygon.contains(xin, 1));
			assertTrue(polygon.contains(xin, 2));
			assertTrue(polygon.contains(xin, 3));
			assertTrue(polygon.contains(xin, 4));
			assertTrue(polygon.contains(xin, 5));
			assertFalse(polygon.contains(xin, 6));
			assertFalse(polygon.contains(xin, 7));
			assertFalse(polygon.contains(xin, 8));

			assertTrue(polygon.containsAny(xin, xout));
			assertFalse(polygon.containsAll(xin, xout));
		}
	}

	@Test
	public void asciiFile3DGlobal() throws GreatCircleException, IOException
	{
		File f = new File("permanent_files/polygon3d_global_layers_4_5.ascii");

		if (f.exists())
		{
			Polygon3D polygon = new Polygon3D(f);

			//System.out.println(polygon);

			double[] xin = VectorGeo.getVectorDegrees(6, 10);
			double[] xout = new double[] {0,0,1};

			assertTrue(polygon.contains(xin));

			assertFalse(polygon.contains(xin, 0));
			assertFalse(polygon.contains(xin, 1));
			assertFalse(polygon.contains(xin, 2));
			assertFalse(polygon.contains(xin, 3));
			assertTrue(polygon.contains(xin, 4));
			assertTrue(polygon.contains(xin, 5));
			assertFalse(polygon.contains(xin, 6));
			assertFalse(polygon.contains(xin, 7));
			assertFalse(polygon.contains(xin, 8));

			assertTrue(polygon.containsAny(xin, xout));
			assertTrue(polygon.containsAll(xin, xout));
		}
	}

	@Test
	public void testCointainsListOfPoint() throws GreatCircleException, IOException
	{
		// great circle loops all the way around the equator.
		GreatCircle gc = new GreatCircle(new double[] {1,0,0}, 2*PI, PI/2);
		ArrayList<double[]> points = gc.getPoints(999, false);

		// circular polygon with radius of 10 degrees centered on lat,lon = 0,90
		Polygon polygon = new Polygon(new double[] {0,1,0}, toRadians(10), 36);

		// test all the points with only one processor.  This will exercise the 
		// part of the code that does not use concurrency.
		ArrayList<Boolean> contained0 = polygon.contains(points, 1);

		//		for (int i=1; i<contained.size(); ++i)
		//		if (contained.get(i) != contained.get(i-1))
		//			System.out.printf("%6d %s %b%n%6d %s %b%n%n",
		//					i-1, VectorGeo.getLatLonString(points.get(i-1)), contained.get(i-1),
		//					i, VectorGeo.getLatLonString(points.get(i)), contained.get(i));

		// make sure we got the right answer.
		for (int i=0; i<points.size(); ++i)
		{
			// contained is true when lon is between 80 and 100
			double lon = VectorGeo.getLonDegrees(points.get(i));
			assertTrue(contained0.get(i) ==  (lon > 80 && lon < 100));
		}								

		// now call the same code with more processors.  This will exercise the code
		// that uses concurrency.
		ArrayList<Boolean> contained = polygon.contains(points, 
				Runtime.getRuntime().availableProcessors());
		// make sure we got the same answer.
		for (int i=0; i<points.size(); ++i)
			assertEquals(contained0.get(i), contained.get(i));

		// test the code that uses hashSet.
		HashSet<double[]> pointSet = new HashSet<double[]>(points.size());
		for (double[] point : points) pointSet.add(point);
		HashMap<double[], Boolean> pointMap = polygon.contains(pointSet, 
				Runtime.getRuntime().availableProcessors());

		// ensure we got the same answers.
		for (int i=0; i<points.size(); ++i)
			assertEquals(contained0.get(i), pointMap.get(points.get(i)));

		// test the code that uses hashmaps.
		//HashMap<double[], Boolean> pointMap = new HashMap<double[], Boolean>(points.size());
		pointMap.clear();
		for (double[] point : points) pointMap.put(point, null);
		polygon.contains(pointMap, Runtime.getRuntime().availableProcessors());

		// ensure we got the same answers.
		for (int i=0; i<points.size(); ++i)
			assertEquals(contained0.get(i), pointMap.get(points.get(i)));

	}

	@Test
	public void testOnBoundary() throws IOException
	{
		Polygon polygon;
		ArrayList<double[]> p;	
		double[] x;

		p = new ArrayList<double[]>();

		// lat-lon box 20 x 20
		p.add(VectorGeo.getVectorDegrees(20, 0));
		p.add(VectorGeo.getVectorDegrees(20, 20));
		p.add(VectorGeo.getVectorDegrees(-20, 20));
		p.add(VectorGeo.getVectorDegrees(-20, 0));

		polygon = new Polygon(p);

		for (double[] pt : p)
			assertTrue(polygon.onBoundary(pt));

		x = VectorGeo.getVectorDegrees(0, 0);
		assertTrue(polygon.onBoundary(x));

		x = VectorGeo.getVectorDegrees(0, 20);
		assertTrue(polygon.onBoundary(x));

		x = VectorGeo.getVectorDegrees(10, 0);
		assertTrue(polygon.onBoundary(x));

		x = VectorGeo.getVectorDegrees(10, 20);
		assertTrue(polygon.onBoundary(x));


		x = VectorGeo.getVectorDegrees(30, 0);
		assertFalse(polygon.onBoundary(x));

		x = VectorGeo.getVectorDegrees(30, 20);
		assertFalse(polygon.onBoundary(x));

		x = VectorGeo.getVectorDegrees(-30, 0);
		assertFalse(polygon.onBoundary(x));

		x = VectorGeo.getVectorDegrees(-30, 20);
		assertFalse(polygon.onBoundary(x));


		x = VectorGeo.getVectorDegrees(0, 0.0001);
		assertFalse(polygon.onBoundary(x));

		x = VectorGeo.getVectorDegrees(0, 20.0001);
		assertFalse(polygon.onBoundary(x));

		x = VectorGeo.getVectorDegrees(10, 0.0001);
		assertFalse(polygon.onBoundary(x));

		x = VectorGeo.getVectorDegrees(10, 20.0001);
		assertFalse(polygon.onBoundary(x));

		x = VectorGeo.getVectorDegrees(20.00001, 20.00001);
		assertFalse(polygon.onBoundary(x));



	}

}
