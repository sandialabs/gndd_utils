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

// Module:        $RCSfile: ContainerTest.java,v $
// Revision:      $Revision: 1.7 $

package containers;

import gov.sandia.gmp.util.containers.MultiMapTT;
import gov.sandia.gmp.util.containers.arraylist.ArrayListDouble;

import java.util.*;

/**
 * A small test that compares the performance of the standard ArrayList of
 * a big intrinsic with the specialized one using intrinsics only.
 */
public class ContainerTest
{

  /**
   * @param args
   */
  public static void main(String[] args)
  {
    ContainerTest ct = new ContainerTest();
    //ct.test1();
    ct.test2();
  }

  public void test2()
  {
    // is a TreeMaps entrySet still sorted
    MultiMapTT<Integer, String> tstMap = new MultiMapTT<Integer, String>();
    
    tstMap.put(5,  "A");
    tstMap.put(6,  "B");
    tstMap.put(7,  "C");
    tstMap.put(3,  "D");
    tstMap.put(1,  "E");
    tstMap.put(10, "F");
    tstMap.put(18, "G");
    tstMap.put(13, "H");
    tstMap.put(22, "I");
    tstMap.put(2,  "K");
    tstMap.put(6,  "L");
    tstMap.put(10, "M");
    tstMap.put(5,  "N");
    tstMap.put(18, "O");
    Set<Map.Entry<Integer, TreeSet<String>>> mes = tstMap.entrySet();
    Iterator<Map.Entry<Integer, TreeSet<String>>> it = mes.iterator();
    while (it.hasNext())
    {
      Map.Entry<Integer, TreeSet<String>> entry = it.next();
      TreeSet<String> tset = entry.getValue();
      Iterator<String> tsetit = tset.iterator();
      while (tsetit.hasNext())
        System.out.println(entry.getKey() + ", " + tsetit.next());
    }
  }
  
  public void test1()
  {
    int i, j;
    
    // size of array lists
    
    int m = 10;
    int n = 130000;
    
    ArrayList<Double> alD = new ArrayList<Double>(n);
    ArrayListDouble aldbl = new ArrayListDouble(n);
    
    // time ArraList<Double> population (add)
    
    long start = System.nanoTime();
    for (j = 0; j < m; ++j)
    {
      alD.clear();
      for (i = 0; i < n; ++i) alD.add(new Double(i));
    }
    long elapsed1 = System.nanoTime() - start;
    System.out.println("ArrayList<Double()> Fill Time = " + elapsed1);   
    
    // time ArrayListDouble population (add)
    
    start = System.nanoTime();
    for (j = 0; j < m; ++j)
    {
      aldbl.clear();
      for (i = 0; i < n; ++i) aldbl.add(i);
    }
    long elapsed2 = System.nanoTime() - start;
    System.out.println("ArrayList<double> Fill Time = " + elapsed2);
    System.out.println("Ratio of Double() / double add(d) Times = " + (double) elapsed1 / elapsed2);
    System.out.println();
    
    // time ArrayList<Double> element retrieval (get)
    
    start = System.nanoTime();
    double d = 0;
    for (j = 0; j < m; ++j)
    {
      for (i = 0; i < n; ++i) d = alD.get(i).doubleValue();
    }
    elapsed1 = System.nanoTime() - start;
    System.out.println("ArrayList<Double()> Get Time = " + elapsed1);   
    
    // time ArrayListDouble element retrieval (get)
    
    start = System.nanoTime();
    for (j = 0; j < m; ++j)
    {
      for (i = 0; i < n; ++i) d = aldbl.get(i);
    }
    elapsed2 = System.nanoTime() - start;
    System.out.println("ArrayList<double> Get Time = " + elapsed2);
    System.out.println("Ratio of Double() / double get(i) Times = " + (double) elapsed1 / elapsed2);
    start += (int) d;
  }
}
