package time;

import static org.junit.Assert.assertEquals;
import gov.sandia.gmp.util.time.Time;
import gov.sandia.gmp.util.time.TimeInterface;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class TimeTest {
	
	/** 
	 * a dumb class that implements TimeInterface
	 */
	static class TimingClass implements TimeInterface
	{
		public double time;
		public double getTime() { return time; }
		public TimingClass(double time) { this.time = time; }
		public String toString() { return String.format("%5.2f", time); }
	}

	/**
	 * an array of 5 TimingClass objects 
	 */
	static ArrayList<TimingClass> times;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// populate times.
		times = new ArrayList<TimeTest.TimingClass>();
		for (int i=0; i<5; ++i)
			times.add(new TimingClass(i));
	}

	@Test
	public void testOldElements() {
		// test when the requested time is exactly equal to each of the elements
		for (int j=0; j<5; ++j)
		{
			List<? extends TimeInterface> sublist = Time.getOldElements(times, j);
			assertEquals(j, sublist.size());
			for (int i=0; i<sublist.size(); ++i)
			{
				assertEquals((double)i, sublist.get(i).getTime(), 1e-6);
				//System.out.println(sublist.get(i));
			}
		}
		
		// test when requested time is smaller than each of the elements
		for (int j=0; j<5; ++j)
		{
			List<? extends TimeInterface> sublist = Time.getOldElements(times, j-0.1);
			assertEquals(j, sublist.size());
			for (int i=0; i<sublist.size(); ++i)
			{
				assertEquals((double)i, sublist.get(i).getTime(), 1e-6);
				//System.out.println(sublist.get(i));
			}
			//System.out.println();
		}
		
		// test when requested time is greater than each of the elements
		for (int j=0; j<5; ++j)
		{
			List<? extends TimeInterface> sublist = Time.getOldElements(times, j+0.1);
			assertEquals(j+1, sublist.size());
			for (int i=0; i<sublist.size(); ++i)
			{
				assertEquals((double)i, sublist.get(i).getTime(), 1e-6);
				//System.out.println(sublist.get(i));
			}
			//System.out.println();
		}
	}
	
	
	@Test
	public void testYoungElements() {
		// test when the requested time is exactly equal to each of the elements
		for (int j=0; j<5; ++j)
		{
			List<? extends TimeInterface> sublist = Time.getYoungElements(times, j);
			
//			System.out.printf("j=%d: ", j);
//			for (int i=0; i<sublist.size(); ++i) 
//				System.out.printf(", %s", sublist.get(i).toString()); 
//			System.out.println();

			assertEquals(5-j, sublist.size());
			for (int i=0; i<sublist.size(); ++i)
				assertEquals((double)(i+j), sublist.get(i).getTime(), 1e-6);
		}
		//System.out.println();
		
		// test when requested time is smaller than each of the elements
		// test when the requested time is exactly equal to each of the elements
		for (int j=0; j<5; ++j)
		{
			List<? extends TimeInterface> sublist = Time.getYoungElements(times, j-0.1);
			
//			System.out.printf("j=%d: ", j);
//			for (int i=0; i<sublist.size(); ++i) 
//				System.out.printf(", %s", sublist.get(i).toString()); 
//			System.out.println();

			assertEquals(5-j, sublist.size());
			for (int i=0; i<sublist.size(); ++i)
				assertEquals((double)(i+j), sublist.get(i).getTime(), 1e-6);
		}
		//System.out.println();
		
		
		// test when requested time is greater than each of the elements
		// test when the requested time is exactly equal to each of the elements
		for (int j=0; j<5; ++j)
		{
			List<? extends TimeInterface> sublist = Time.getYoungElements(times, j+0.1);
			
//			System.out.printf("j=%d: ", j);
//			for (int i=0; i<sublist.size(); ++i) 
//				System.out.printf(", %s", sublist.get(i).toString()); 
//			System.out.println();

			assertEquals(4-j, sublist.size());
			for (int i=0; i<sublist.size(); ++i)
				assertEquals((double)(i+j+1), sublist.get(i).getTime(), 1e-6);
		}
		//System.out.println();
		
	}
	
	
	@Test
	public void testTimeRange() 
	{
		// test when the requested time is exactly equal to each of the elements
		for (int j=0; j<3; ++j)
		{
			List<? extends TimeInterface> sublist = Time.timeRange(times, j, j+2);
			
//			System.out.printf("j=%d: ", j);
//			for (int i=0; i<sublist.size(); ++i) 
//				System.out.printf(", %s", sublist.get(i).toString()); 
//			System.out.println();

			assertEquals(2, sublist.size());
			for (int i=0; i<sublist.size(); ++i)
				assertEquals((double)(i+j), sublist.get(i).getTime(), 1e-6);
		}
		//System.out.println();
		
		// test when requested time is smaller than each of the elements
		// test when the requested time is exactly equal to each of the elements
		for (int j=0; j<3; ++j)
		{
			List<? extends TimeInterface> sublist = Time.timeRange(times, j-0.1, j+1.9);
			
//			System.out.printf("j=%d: ", j);
//			for (int i=0; i<sublist.size(); ++i) 
//				System.out.printf(", %s", sublist.get(i).toString()); 
//			System.out.println();

			assertEquals(2, sublist.size());
			for (int i=0; i<sublist.size(); ++i)
				assertEquals((double)(i+j), sublist.get(i).getTime(), 1e-6);
		}
		//System.out.println();
		
		
		// test when requested time is greater than each of the elements
		// test when the requested time is exactly equal to each of the elements
		for (int j=0; j<3; ++j)
		{
			List<? extends TimeInterface> sublist = Time.timeRange(times, j+0.1, j+2.1);
			
//			System.out.printf("j=%d: ", j);
//			for (int i=0; i<sublist.size(); ++i) 
//				System.out.printf(", %s", sublist.get(i).toString()); 
//			System.out.println();

			assertEquals(2, sublist.size());
			for (int i=0; i<sublist.size(); ++i)
				assertEquals((double)(i+j+1), sublist.get(i).getTime(), 1e-6);
		}
		//System.out.println();
		
	}
	
	@Test
	public void testSublists()
	{
		ArrayList<TimingClass> t2 = new ArrayList<TimeTest.TimingClass>(100);
		for (int i=0; i<100; ++i) t2.add(new TimingClass(i)); 
	
		ArrayList<List<? extends TimeInterface>> sublists = Time.sublists(t2, 0., 2.);
		
		assertEquals(50, sublists.size());
		for (int i=0; i<sublists.size(); ++i)
			assertEquals(sublists.get(i).size(), 2);
		
	}
	

}
