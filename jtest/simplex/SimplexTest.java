package simplex;

import static org.junit.Assert.assertEquals;
import gov.sandia.gmp.util.numerical.simplex.Amoeba;
import gov.sandia.gmp.util.numerical.simplex.Simplex;
import gov.sandia.gmp.util.numerical.simplex.SimplexFunction;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.junit.BeforeClass;
import org.junit.Test;

public class SimplexTest implements SimplexFunction, ChangeListener 
{
	
	int nDimensions;
	
	/**
	 * Directory where simplex vtk files will be written.
	 * If null, no output is generated.
	 */
	static File vtkDirectory = null; 

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		if (vtkDirectory != null)
		{
			int n = 201;
			double[] x = new double[n];
			double[] y = new double[n];
			double[][] z = new double[n][n];

			for (int i=0; i<n; ++i) x[i] = -n/2 + i;
			for (int j=0; j<n; ++j) y[j] = -n/2 + j;

			for (int i=0; i<n; ++i) for (int j=0; j<n; ++j)
				z[i][j] = Math.sqrt(x[i]*x[i] + y[j]*y[j]);

			File vtkFile = new File(vtkDirectory, "grid.vtk");

			DataOutputStream output = new DataOutputStream(
					new BufferedOutputStream(new FileOutputStream(vtkFile)));

			output.writeBytes(String.format("# vtk DataFile Version 2.0%n"));
			output.writeBytes(String.format("Grid%n"));
			output.writeBytes(String.format("BINARY%n"));

			output.writeBytes(String.format("DATASET STRUCTURED_GRID%n"));
			output.writeBytes(String.format("DIMENSIONS %d %d 1%n", x.length, y.length));
			output.writeBytes(String.format("POINTS %d double%n", x.length*y.length));
			for (int i=0; i<x.length; ++i) 
				for (int j=0; j<y.length; ++j) 
				{
					output.writeDouble(x[i]);
					output.writeDouble(y[j]);
					output.writeDouble(-100.);
				}

			output.writeBytes(String.format("POINT_DATA %d%n", x.length*y.length));

			output.writeBytes(String.format("SCALARS metric float 1%n"));
			output.writeBytes(String.format("LOOKUP_TABLE default%n"));

			for (int i=0; i<x.length; ++i) 
				for (int j=0; j<y.length; ++j) 
					output.writeFloat((float) z[i][j]);

			output.close();
		}
	}

	/**
	 * Perform a single function evaluation.  Called from 
	 * SimplexSequential but not SimplexParallel.
	 */
	@Override
	public double simplexFunction(double[] x) throws Exception 
	{
		double f = 0;
		for (int j=0; j<nDimensions; ++j)
			f += x[j]*x[j];
		return Math.sqrt(f);
	}

	@Test 
	public void testSimplexParallel() throws Exception
	{
		for (nDimensions = 2; nDimensions < 7; ++nDimensions)
		{
			//System.out.println("nDimensions = "+nDimensions);

			Simplex simplex = new Simplex(this, 1e-3, 10000);
			simplex.setParallelMode(true);

			double[] x = new double[nDimensions];
			double[] dx =  new double[nDimensions];

			Arrays.fill(x, -90.);
			Arrays.fill(dx, 10.);

			double zmin = simplex.search(x, dx, true);

			//		System.out.println(Arrays.toString(xy));
			//		System.out.println(Arrays.toString(dxy));
			//		System.out.println(zmin);

			for (int i=0; i<nDimensions; ++i)
				assertEquals(0., x[i], 0.01);

			for (int i=0; i<nDimensions; ++i)
				assertEquals(0., dx[i], 0.01);

			assertEquals(0., zmin, 0.01);
		}
	}

	@Test 
	public void testSimplex() throws Exception
	{
		for (nDimensions = 2; nDimensions < 7; ++nDimensions)
		{
			//System.out.println("nDimensions = "+nDimensions);

			Simplex simplex = new Simplex(this, 1e-3, 10000);
			simplex.setParallelMode(false);

			double[] x = new double[nDimensions];
			double[] dx =  new double[nDimensions];

			Arrays.fill(x, -90.);
			Arrays.fill(dx, 10.);

			double zmin = simplex.search(x, dx, true);

			//		System.out.println(Arrays.toString(xy));
			//		System.out.println(Arrays.toString(dxy));
			//		System.out.println(zmin);

			for (int i=0; i<nDimensions; ++i)
				assertEquals(0., x[i], 0.01);

			for (int i=0; i<nDimensions; ++i)
				assertEquals(0., dx[i], 0.01);

			assertEquals(0., zmin, 0.01);
		}
	}

	@Test 
	public void testSimplexMonitored() throws Exception
	{
		Simplex simplex = new Simplex(this, 1e-3, 10000);
		simplex.setParallelMode(true);

		nDimensions = 3;
		
		simplex.addListener(this);

		double[] x = new double[nDimensions];
		double[] dx =  new double[nDimensions];
		
		Arrays.fill(x, -90.);
		Arrays.fill(dx, 10.);

		double zmin = simplex.search(x, dx, true);

		//		System.out.println(Arrays.toString(xy));
		//		System.out.println(Arrays.toString(dxy));
		//		System.out.println(zmin);

		for (int i=0; i<nDimensions; ++i)
			assertEquals(0., x[i], 0.01);
		
		for (int i=0; i<nDimensions; ++i)
			assertEquals(0., dx[i], 0.01);
		
		assertEquals(0., zmin, 0.01);
	}

	@Override
	public void stateChanged(ChangeEvent e) 
	{
		Amoeba shape = (Amoeba)e.getSource();
		//System.out.println(shape.toString());
		if (vtkDirectory != null)
			try {
				shape.vtk(vtkDirectory, "simplex_<index>.vtk");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
	}

}
