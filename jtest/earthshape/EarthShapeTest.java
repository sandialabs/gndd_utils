package earthshape;

import static java.lang.Math.PI;
import static java.lang.Math.toRadians;
import static org.junit.Assert.assertEquals;
import gov.sandia.gmp.util.numerical.vector.EarthShape;
import gov.sandia.gmp.util.numerical.vector.Vector3D;
import gov.sandia.gmp.util.numerical.vector.VectorGeo;
import gov.sandia.gmp.util.numerical.vector.VectorUnit;

import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

public class EarthShapeTest
{

	static double[] u;

	static double uLat, uLon, uLatDeg, uLonDeg;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		uLatDeg = 50;
		uLonDeg = 30;
		uLat = toRadians(uLatDeg);
		uLon = toRadians(uLonDeg);
		u = new double[] {0.5588628007257437, 0.3226595884390762, 0.7639130578497161};
	}
	
	@Test 
	public void testInEllipse()
	{
		double[] center = new double[] {.3, .4, .5};
		VectorUnit.normalize(center);
		double trend = Math.toRadians(30);
		double majax = Math.toRadians(10.);
		double minax = Math.toRadians(2.);
		
		double direction = Math.PI/2;
		
		double[] point = VectorUnit.move(center, majax*1.01, trend);
		assertEquals(false, VectorUnit.inEllipse(center, majax, minax, trend, point));

		VectorUnit.move(center, majax*.99, trend, point);
		assertEquals(true, VectorUnit.inEllipse(center, majax, minax, trend, point));

		VectorUnit.move(center, minax*1.01, trend+direction, point);
		assertEquals(false, VectorUnit.inEllipse(center, majax, minax, trend, point));

		VectorUnit.move(center, minax*.99, trend+direction, point);
		assertEquals(true, VectorUnit.inEllipse(center, majax, minax, trend, point));

	}

	@Test
	public void testApproximateLatitudes()
	{
		int npoints = 2000;
		double[] lat = new double[npoints];
		double[] exact = new double[npoints];
		double[] approx = new double[npoints];
		
		double dlat = Math.PI / (npoints-1.);
		for (int i=0; i<npoints; ++i)
			lat[i] = -Math.PI/2 + i*dlat;
		
		EarthShape.approximateLatitudes = false;
		
		for (int i=0; i<npoints; ++i)
			exact[i] = EarthShape.WGS84.getGeographicLat(lat[i]);
		
		EarthShape.approximateLatitudes = true;
		
		for (int i=0; i<npoints; ++i)
			approx[i] = EarthShape.WGS84.getGeographicLat(lat[i]);
		
		double maxdiff = Double.NEGATIVE_INFINITY;
		for (int i=0; i<npoints; ++i)
		{
			double diff = Math.abs(approx[i]-exact[i]);
			if (diff > maxdiff) maxdiff = diff;
			
			assertEquals(exact[i], approx[i], 1e-7);
		}
		
		//System.out.printf("maxdiff = %1.6e radians   %1.6f meters%n", maxdiff, maxdiff*6371000.);
		
		
		
		EarthShape.approximateLatitudes = false;
		
		for (int i=0; i<npoints; ++i)
			exact[i] = EarthShape.WGS84.getGeocentricLat(lat[i]);
		
		EarthShape.approximateLatitudes = true;
		
		for (int i=0; i<npoints; ++i)
			approx[i] = EarthShape.WGS84.getGeocentricLat(lat[i]);
		
		maxdiff = Double.NEGATIVE_INFINITY;
		for (int i=0; i<npoints; ++i)
		{
			double diff = Math.abs(approx[i]-exact[i]);
			if (diff > maxdiff) maxdiff = diff;
			
			assertEquals(exact[i], approx[i], 1e-7);
		}
		
		//System.out.printf("maxdiff = %1.6e radians   %1.6f meters%n", maxdiff, maxdiff*6371000.);
		

		
		EarthShape.approximateLatitudes = true;
		
		for (int i=0; i<npoints; ++i)
			approx[i] = EarthShape.SPHERE.getGeocentricLat(lat[i]);
		
		maxdiff = Double.NEGATIVE_INFINITY;
		for (int i=0; i<npoints; ++i)
		{
			double diff = Math.abs(approx[i]-lat[i]);
			if (diff > maxdiff) maxdiff = diff;
			
			assertEquals(lat[i], approx[i], 1e-16);
		}
		
		//System.out.printf("maxdiff = %1.6e radians   %1.6f meters%n", maxdiff, maxdiff*6371000.);
		
		EarthShape.approximateLatitudes = false;
	}

//	@Test
//	public void testApproximateLatitudes2()
//	{
//		int nloops = 10000;
//		int npoints = 20000;
//		double[] lat = new double[npoints];
//		double[] exact = new double[npoints];
//		double[] approx = new double[npoints];
//		
//		double dlat = Math.PI / (npoints-1.);
//		for (int i=0; i<npoints; ++i)
//			lat[i] = -Math.PI/2 + i*dlat;
//		
//	
//		EarthShape.approximateLatitudes = false;
//		
//		long timer_exact = System.currentTimeMillis();
//		for (int n=0; n<nloops; ++n)
//		for (int i=0; i<npoints; ++i)
//			exact[i] = EarthShape.WGS84.getGeographicLat(lat[i]);
//		timer_exact = System.currentTimeMillis()-timer_exact;
//		
//		EarthShape.approximateLatitudes = true;
//		
//		long timer_approx = System.currentTimeMillis();
//		for (int n=0; n<nloops; ++n)
//		for (int i=0; i<npoints; ++i)
//			approx[i] = EarthShape.WGS84.getGeographicLat(lat[i]);
//		timer_approx = System.currentTimeMillis()-timer_approx;
//		
//		System.out.printf("approx = %d  exact = %d  approx/exact = %1.3f%n",
//				timer_approx, timer_exact, ((double)timer_approx)/((double)timer_exact));
//		EarthShape.approximateLatitudes = false;
//	}

	@Test
	public void testCompareWithVectorGeo()
	{
		EarthShape wgs84 = EarthShape.WGS84;
		
		assertEquals(VectorGeo.getEarthRadius(u), wgs84.getEarthRadius(u), 1e-12);
		
		assertEquals(VectorGeo.getLat(u), wgs84.getLat(u), 1e-7);
		assertEquals(VectorGeo.getLon(u), wgs84.getLon(u), 1e-15);
		
		double[] v = wgs84.getVector(uLat, uLon);
//		assertEquals(u[0], v[0], 1e-15);
//		assertEquals(u[1], v[1], 1e-15);
//		assertEquals(u[2], v[2], 1e-15);
		assertEquals(true, Vector3D.dot(u, v) > Math.cos(1e-7));
		
	}

	@Test
	public void testGetEarthRadiusDoubleArray()
	{
		double r = EarthShape.SPHERE.getEarthRadius(u);
		assertEquals(6371., r, 1e-12);

		r = EarthShape.GRS80_RCONST.getEarthRadius(u);
		assertEquals(6371., r, 1e-12);

		r = EarthShape.WGS84_RCONST.getEarthRadius(u);
		assertEquals(6371., r, 1e-12);

		r = EarthShape.IERS2003_RCONST.getEarthRadius(u);
		assertEquals(6371., r, 1e-12);

		r = EarthShape.GRS80.getEarthRadius(u);
		//System.out.println(r);
		assertEquals(6365.631517475852, r, 1e-12);

		r = EarthShape.WGS84.getEarthRadius(u);
		//System.out.println(r);
		assertEquals(6365.63151753728, r, 1e-12);

		r = EarthShape.IERS2003.getEarthRadius(u);
		//System.out.println(r);
		assertEquals(6365.631084558672, r, 1e-12);
	}

	@Test
	public void testGetEarthRadiusDouble()
	{
		double r = EarthShape.SPHERE.getEarthRadius(uLat);
		assertEquals(6371., r, 1e-12);

		r = EarthShape.GRS80_RCONST.getEarthRadius(uLat);
		assertEquals(6371., r, 1e-12);

		r = EarthShape.WGS84_RCONST.getEarthRadius(uLat);
		assertEquals(6371., r, 1e-12);

		r = EarthShape.IERS2003_RCONST.getEarthRadius(uLat);
		assertEquals(6371., r, 1e-12);

		r = EarthShape.GRS80.getEarthRadius(uLat);
		//System.out.println(r);
		assertEquals(6365.631517476195, r, 1e-5);

		r = EarthShape.WGS84.getEarthRadius(uLat);
		//System.out.println(r);
		assertEquals(6365.63151753728, r, 1e-5);

		r = EarthShape.IERS2003.getEarthRadius(uLat);
		//System.out.println(r);
		assertEquals(6365.631084746921, r, 1e-5);
	}

	@Test
	public void testGetEarthRadiusDegrees()
	{
		double r = EarthShape.SPHERE.getEarthRadiusDegrees(uLatDeg);
		assertEquals(6371., r, 1e-12);

		r = EarthShape.GRS80_RCONST.getEarthRadiusDegrees(uLatDeg);
		assertEquals(6371., r, 1e-12);

		r = EarthShape.WGS84_RCONST.getEarthRadiusDegrees(uLatDeg);
		assertEquals(6371., r, 1e-12);

		r = EarthShape.IERS2003_RCONST.getEarthRadiusDegrees(uLatDeg);
		assertEquals(6371., r, 1e-12);

		r = EarthShape.GRS80.getEarthRadiusDegrees(uLatDeg);
		//System.out.println(r);
		assertEquals(6365.631517476195, r, 1e-5);

		r = EarthShape.WGS84.getEarthRadiusDegrees(uLatDeg);
		//System.out.println(r);
		assertEquals(6365.63151753728, r, 1e-5);

		r = EarthShape.IERS2003.getEarthRadiusDegrees(uLatDeg);
		//System.out.println(r);
		assertEquals(6365.631084746921, r, 1e-5);
	}

	@Test
	public void testGetLon()
	{
		double lon = EarthShape.SPHERE.getLon(u);
		assertEquals(uLon, lon, 1e-12);
	}

	@Test
	public void testGetLonDegrees()
	{
		double lon = EarthShape.SPHERE.getLonDegrees(u);
		assertEquals(uLonDeg, lon, 1e-12);
	}

	@Test
	public void testGetLat()
	{
		double lat = EarthShape.SPHERE.getLat(u);
		//System.out.println(lat);
		assertEquals(0.8693552989346709, lat, 1e-12);

		lat = EarthShape.GRS80.getLat(u);
		//System.out.println(lat);
		assertEquals(0.8726646260134043, lat, 1e-7);

		lat = EarthShape.GRS80_RCONST.getLat(u);
		//System.out.println(lat);
		assertEquals(0.8726646260134043, lat, 1e-7);

		lat = EarthShape.WGS84.getLat(u);
		//System.out.println(lat);
		assertEquals(0.8726646259971647, lat, 1e-7);

		lat = EarthShape.WGS84_RCONST.getLat(u);
		//System.out.println(lat);
		assertEquals(0.8726646259971647, lat, 1e-7);

		lat = EarthShape.IERS2003.getLat(u);
		//System.out.println(lat);
		assertEquals(0.8726646349230063, lat, 1e-7);

		lat = EarthShape.IERS2003_RCONST.getLat(u);
		assertEquals(0.8726646349230063, lat, 1e-7);
	}

	@Test
	public void testGetLatDegrees()
	{
		double lat = EarthShape.SPHERE.getLatDegrees(u);
		//System.out.println(lat);
		assertEquals(49.81038952629067, lat, 1e-12);

		lat = EarthShape.GRS80.getLatDegrees(u);
		//System.out.println(lat);
		assertEquals(50.000000000930456, lat, 1e-6);

		lat = EarthShape.GRS80_RCONST.getLatDegrees(u);
		//System.out.println(lat);
		assertEquals(50.000000000930456, lat, 1e-6);

		lat = EarthShape.WGS84.getLatDegrees(u);
		//System.out.println(lat);
		assertEquals(uLatDeg, lat, 1e-6);

		lat = EarthShape.WGS84_RCONST.getLatDegrees(u);
		//System.out.println(lat);
		assertEquals(uLatDeg, lat, 1e-6);

		lat = EarthShape.IERS2003.getLatDegrees(u);
		//System.out.println(lat);
		assertEquals(50.00000051141305, lat, 1e-5);

		lat = EarthShape.IERS2003_RCONST.getLatDegrees(u);
		assertEquals(50.00000051141305, lat, 1e-5);
	}

	@Test
	public void testGetGeocentricLat()
	{
		double expected = 0.8693552989346709;

		double lat = EarthShape.SPHERE.getGeocentricLat(u);
		//System.out.println(lat);
		assertEquals(expected, lat, 1e-12);

		lat = EarthShape.GRS80.getGeocentricLat(u);
		//System.out.println(lat);
		assertEquals(expected, lat, 1e-12);

		lat = EarthShape.GRS80_RCONST.getGeocentricLat(u);
		//System.out.println(lat);
		assertEquals(expected, lat, 1e-12);

		lat = EarthShape.WGS84.getGeocentricLat(u);
		//System.out.println(lat);
		assertEquals(expected, lat, 1e-12);

		lat = EarthShape.WGS84_RCONST.getGeocentricLat(u);
		//System.out.println(lat);
		assertEquals(expected, lat, 1e-12);

		lat = EarthShape.IERS2003.getGeocentricLat(u);
		//System.out.println(lat);
		assertEquals(expected, lat, 1e-12);

		lat = EarthShape.IERS2003_RCONST.getGeocentricLat(u);
		assertEquals(expected, lat, 1e-12);
	}

	@Test
	public void testGetGeocentricLatDegrees()
	{
		double expected = 49.81038952629067;

		double lat = EarthShape.SPHERE.getGeocentricLatDegrees(u);
		//System.out.println(lat);
		assertEquals(expected, lat, 1e-12);

		lat = EarthShape.GRS80.getGeocentricLatDegrees(u);
		//System.out.println(lat);
		assertEquals(expected, lat, 1e-12);

		lat = EarthShape.GRS80_RCONST.getGeocentricLatDegrees(u);
		//System.out.println(lat);
		assertEquals(expected, lat, 1e-12);

		lat = EarthShape.WGS84.getGeocentricLatDegrees(u);
		//System.out.println(lat);
		assertEquals(expected, lat, 1e-12);

		lat = EarthShape.WGS84_RCONST.getGeocentricLatDegrees(u);
		//System.out.println(lat);
		assertEquals(expected, lat, 1e-12);

		lat = EarthShape.IERS2003.getGeocentricLatDegrees(u);
		//System.out.println(lat);
		assertEquals(expected, lat, 1e-12);

		lat = EarthShape.IERS2003_RCONST.getGeocentricLatDegrees(u);
		assertEquals(expected, lat, 1e-12);
	}

	@Test
	public void testGetVectorDoubleDouble()
	{
		double[] v = EarthShape.SPHERE.getVector(PI/4, PI/4);
//		System.out.println(Arrays.toString(u));
//		System.out.println(Arrays.toString(v));
		assertEquals("[0.5000000000000001, 0.5, 0.7071067811865475]", Arrays.toString(v));

		v = EarthShape.WGS84.getVector(uLat, uLon);
//		System.out.println(Arrays.toString(u));
//		System.out.println(Arrays.toString(v));
//		assertEquals(Arrays.toString(u), Arrays.toString(v));
		assertEquals(true, Vector3D.dot(u, v) > Math.cos(1e-7));

		v = EarthShape.WGS84_RCONST.getVector(uLat, uLon);
		//assertEquals(Arrays.toString(u), Arrays.toString(v));
		assertEquals(true, Vector3D.dot(u, v) > Math.cos(1e-7));

		v = EarthShape.GRS80.getVector(uLat, uLon);
		//System.out.println(Arrays.toString(v));
		//assertEquals("[0.5588628007364996, 0.32265958844528614, 0.7639130578392244]", Arrays.toString(v));
		assertEquals(true, Vector3D.dot(u, v) > Math.cos(1e-7));

		v = EarthShape.GRS80_RCONST.getVector(uLat, uLon);
		//assertEquals("[0.5588628007364996, 0.32265958844528614, 0.7639130578392244]", Arrays.toString(v));
		assertEquals(true, Vector3D.dot(u, v) > Math.cos(1e-7));

		v = EarthShape.IERS2003.getVector(uLat, uLon);
		//System.out.println(Arrays.toString(v));
		//assertEquals("[0.5588628066375578, 0.3226595918522637, 0.7639130520831033]", Arrays.toString(v));
		assertEquals(true, Vector3D.dot(u, v) > Math.cos(1e-7));

		v = EarthShape.IERS2003_RCONST.getVector(uLat, uLon);
		//assertEquals("[0.5588628066375578, 0.3226595918522637, 0.7639130520831033]", Arrays.toString(v));
		assertEquals(true, Vector3D.dot(u, v) > Math.cos(1e-7));
	}

	@Test
	public void testGetVectorDegreesDoubleDouble()
	{
		double[] v = EarthShape.SPHERE.getVectorDegrees(45., 45.);
//		System.out.println(Arrays.toString(u));
//		System.out.println(Arrays.toString(v));
		assertEquals("[0.5000000000000001, 0.5, 0.7071067811865475]", Arrays.toString(v));

		v = EarthShape.WGS84.getVectorDegrees(uLatDeg, uLonDeg);
//		System.out.println(Arrays.toString(u));
//		System.out.println(Arrays.toString(v));
		assertEquals(true, Vector3D.dot(u, v) > Math.cos(1e-7));
//		assertEquals(Arrays.toString(u), Arrays.toString(v));

		v = EarthShape.WGS84_RCONST.getVectorDegrees(uLatDeg, uLonDeg);
		assertEquals(true, Vector3D.dot(u, v) > Math.cos(1e-7));
//		assertEquals(Arrays.toString(u), Arrays.toString(v));

		v = EarthShape.GRS80.getVectorDegrees(uLatDeg, uLonDeg);
		//System.out.println(Arrays.toString(v));
		assertEquals(true, Vector3D.dot(u, v) > Math.cos(1e-7));
//		assertEquals("[0.5588628007364996, 0.32265958844528614, 0.7639130578392244]", Arrays.toString(v));

		v = EarthShape.GRS80_RCONST.getVectorDegrees(uLatDeg, uLonDeg);
		assertEquals(true, Vector3D.dot(u, v) > Math.cos(1e-7));
//		assertEquals("[0.5588628007364996, 0.32265958844528614, 0.7639130578392244]", Arrays.toString(v));

		v = EarthShape.IERS2003.getVectorDegrees(uLatDeg, uLonDeg);
		//System.out.println(Arrays.toString(v));
		assertEquals(true, Vector3D.dot(u, v) > Math.cos(1e-7));
//		assertEquals("[0.5588628066375578, 0.3226595918522637, 0.7639130520831033]", Arrays.toString(v));

		v = EarthShape.IERS2003_RCONST.getVectorDegrees(uLatDeg, uLonDeg);
		assertEquals(true, Vector3D.dot(u, v) > Math.cos(1e-7));
//		assertEquals("[0.5588628066375578, 0.3226595918522637, 0.7639130520831033]", Arrays.toString(v));
	}

	@Test
	public void testGetVectorDegreesDoubleDoubleDoubleArray()
	{
		double[] v = new double[3];
		EarthShape.SPHERE.getVectorDegrees(45., 45., v);
		assertEquals("[0.5000000000000001, 0.5, 0.7071067811865475]", Arrays.toString(v));
	}

	@Test
	public void testGetVectorDoubleDoubleDoubleArray()
	{
		double[] v = new double[3];
		EarthShape.SPHERE.getVector(PI/4, PI/4, v);
		assertEquals("[0.5000000000000001, 0.5, 0.7071067811865475]", Arrays.toString(v));
	}

	@Test
	public void testGetLatLonStringDoubleArray()
	{
		//System.out.println(EarthShape.WGS84.getLatLonString(u));
		assertEquals(" 50.000000   30.000000", EarthShape.WGS84.getLatLonString(u));
	}

	@Test
	public void testGetLatLonStringDoubleArrayInt()
	{
		//System.out.println(EarthShape.WGS84.getLatLonString(u, 2));
		assertEquals(" 50.00   30.00", EarthShape.WGS84.getLatLonString(u, 2));
	}

	@Test
	public void testGetLatLonStringDoubleArrayString()
	{
		//System.out.println(EarthShape.WGS84.getLatLonString(u, "%1.3f %1.3f"));
		assertEquals("50.000 30.000", EarthShape.WGS84.getLatLonString(u, "%1.3f %1.3f"));
	}

}
