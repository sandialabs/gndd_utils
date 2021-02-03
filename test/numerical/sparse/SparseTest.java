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

package numerical.sparse;

import gov.sandia.gmp.util.globals.Globals;
import gov.sandia.gmp.util.numerical.sparse.SparseMatrix;
import gov.sandia.gmp.util.numerical.sparse.SparseMatrixHuge;
import gov.sandia.gmp.util.numerical.sparse.SparseMatrixStandard;
import gov.sandia.gmp.util.numerical.sparse.SparseVector;

import java.io.IOException;

public class SparseTest
{
  public static void main(String[] args)
  {
  	SparseTest smt = new SparseTest();
  	try
  	{
  	  //smt.test1();
  	  //smt.test2();
  	  smt.test3();
  	}
  	catch (Exception ex)
  	{
  		ex.printStackTrace();
  	}
  }
  
  private void test3() throws IOException
  {
  	SparseMatrix sm = new SparseMatrixHuge();
  	String filename = "Z:/Runs/2014_04_21_tomo140317_3A/sparseMatrix_1_17";
  	//String filename = "Z:/Runs/2014_10_14_tomo141007_1A/sparseMatrix_1_13";
  	//String filename = "Z:/Runs/2013_05_28_tomo130521_1A/sparseMatrix_1_9";
  	//String filename = "Z:/Runs/2013_05_28_tomo130521_1A/sparseMatrix_1_9";
  	//String filename = "Z:/Runs/2012_12_18_tomo121217_1A/sparseMatrix_1_7";
  	//String filename = "Z:/Runs/2014_05_01_tomo140317_3D/sparseMatrix_1_10";
  	
  	System.out.println("Reading Sparse Matrix: \"" + filename + "\"");
  	sm.readOldSparseMatrix(filename);
  	
  	System.out.println("Total Columns = " + sm.getMaxCol());
  	System.out.println("Total Rows    = " + sm.getMaxRow());
  	System.out.println("Total Entries = " + sm.entryCount());
  	System.out.println("Memory Allocation = " + Globals.memoryUnit(sm.memoryAllocationSize()));

  	System.out.println("Creating CSC Representation ...");
  	sm.createCSC();
  	System.out.println("Memory Allocation = " + Globals.memoryUnit(sm.memoryAllocationSize()));

  	System.out.println("Creating CSR and CSC SparseVector Arrays ...");
  	SparseVector[] csrv = sm.buildCSR_SV();
  	SparseVector[] cscv = sm.buildCSC_SV();

  	System.out.println("Testing Element-by-Element Equivalence ...");
  	for (int i = 0; i < csrv.length; ++i)
  	{
  		SparseVector sv_csr = csrv[i];
  		for (int j = 0; j < sv_csr.size(); ++j)
  		{
  			int    indx = sv_csr.getIndex(j);
  			double valu = sv_csr.getValue(j);
  			
  			SparseVector sv_csc = cscv[indx];
				int k = sv_csc.findIndex(i);
				if (k < 0)
				{
					System.out.println("Error: i                 = " + i);
					System.out.println("Error: j                 = " + j);
					System.out.println("Error: csr vector length = " + sv_csr.size());
					System.out.println("Error: k                 = " + k);
					System.out.println("Error: indx              = " + indx);
					System.out.println("Error: csc vector index  = " + sv_csc.getVectorIndex());
					System.out.println("Error: csc vector length = " + sv_csc.size());
					System.out.println("Error: csc min index     = " + sv_csc.getMinIndex());
					System.out.println("Error: csc max index     = " + sv_csc.getMaxIndex());
					System.out.println("Error: Exiting ...");
				}
				if (sv_csc.getIndex(k) == i)
				{
					double valu_csc = sv_csc.getValue(k);
					if (valu != valu_csc)
					{
						throw new IOException("Different Value (" + i + ", " + j + ")");
					}					
				}
				else
				{
					throw new IOException("Different Index (" + i + ", " + j + ")");
				}
  		}
  	}
  	System.out.println("Done ...");
  }

  private void test1() throws IOException
  {
  	SparseMatrix sm = new SparseMatrixStandard();

  	sm.add(6, 9, 1.1);  // 12
  	sm.add(5, 2, 2.1);  // 6
  	sm.add(4, 8, 3.1);  // 5
  	sm.add(3, 2, 4.1);  // 2
  	sm.add(6, 4, 5.1);  // 9
  	sm.add(6, 3, 6.1);  // 8
  	sm.add(8, 8, 7.1);  // 13
  	sm.add(6, 7, 8.1);  // 11
  	sm.add(6, 6, 9.1);  // 10
  	sm.add(5, 4, 10.1); // 7
  	sm.add(4, 3, 11.1); // 4
  	sm.add(3, 3, 12.1); // 3
  	sm.add(2, 8, 13.1); // 1
  	sm.add(9, 1, 14.1); // 14
  	sm.add(10, 2, 15.1); // 15
  	sm.add(11, 6, 16.1); // 16
  	
    //sm.sort();
  	sm.createCSR();
  }
  
  private void test2() throws IOException
  {
  	SparseMatrixStandard sm = new SparseMatrixStandard();

  	sm.add( 7,  1,  27);
  	sm.add(16,  5,  63);
  	sm.add(18,  0,  71);
  	sm.add(18,  1,  72);
  	sm.add( 1,  2,   3);
  	sm.add( 0, 11,   1);
  	sm.add( 4,  4,  15);
  	sm.add(24,  9,  97);
  	sm.add(22,  0,  88);
  	sm.add( 2, 17,   9);
  	sm.add( 6,  9,  24);
  	sm.add(18,  2,  73);
  	sm.add(13, 20,  52);
  	sm.add(13, 22,  53);
  	sm.add( 5,  3,  18);
  	sm.add(23, 16,  93);
  	sm.add( 8, 10,  32);
  	sm.add( 1, 15,   5);
  	sm.add( 9,  0,  35);
  	sm.add(15,  2,  59);
  	sm.add(10,  1,  41);
  	sm.add( 4,  1,  13);
  	sm.add(24, 23,  99);
  	sm.add( 6,  3,  22);
  	sm.add(11,  3,  44);
  	sm.add(17,  1,  66);
  	sm.add(17,  7,  67);
  	sm.add(17,  8,  68);
  	sm.add( 7, 13,  29);
  	sm.add( 0, 20,   2);
  	sm.add(17, 23,  70);
  	sm.add( 2,  2,   7);
  	sm.add(19,  4,  76);
  	sm.add(24,  1,  96);
  	sm.add( 9,  4,  37);
  	sm.add(19, 24,  80);
  	sm.add(22, 17,  90);
  	sm.add( 5, 23,  21);
  	sm.add( 2,  7,   8);
  	sm.add( 9,  8,  39);
  	sm.add( 1, 23,   6);
  	sm.add(11, 24,  46);
  	sm.add(20, 22,  82);
  	sm.add(19, 13,  77);
  	sm.add(20, 24,  84);
  	sm.add(21, 11,  87);
  	sm.add(12, 14,  48);
  	sm.add(12, 23,  50);
  	sm.add(14,  7,  56);
  	sm.add(15, 12,  61);
  	sm.add( 3,  0,  10);
  	sm.add(14,  5,  54);
  	sm.add( 3,  5,  11);
  	sm.add(14,  8,  57);
  	sm.add(24,  0,  95);
  	sm.add( 1, 14,   4);
  	sm.add(23, 24,  94);
  	sm.add(14, 19,  58);
  	sm.add(14,  6,  55);
  	sm.add( 5, 12,  19);
  	sm.add( 7, 21,  30);
  	sm.add(24, 24, 100);
  	sm.add(11,  2,  43);
  	sm.add(22, 21,  91);
  	sm.add( 6,  8,  23);
  	sm.add( 4, 16,  17);
  	sm.add(21, 10,  86);
  	sm.add(19,  3,  75);
  	sm.add( 8,  4,  31);
  	sm.add( 4, 10,  16);
  	sm.add(20,  6,  81);
  	sm.add(16, 17,  64);
  	sm.add( 8, 16,  33);
  	sm.add( 9,  9,  40);
  	sm.add(12, 21,  49);
  	sm.add(18, 20,  74);
  	sm.add( 4,  2,  14);
  	sm.add(24, 14,  98);
  	sm.add( 9,  7,  38);
  	sm.add( 5, 22,  20);
  	sm.add(20, 23,  83);
  	sm.add(11, 12,  45);
  	sm.add( 3, 20,  12);
  	sm.add(12, 24,  51);
  	sm.add( 6, 18,  26);
  	sm.add(15, 11,  60);
  	sm.add(22,  5,  89);
  	sm.add(19, 15,  79);
  	sm.add(15, 15,  62);
  	sm.add( 7,  6,  28);
  	sm.add(16, 18,  65);
  	sm.add(17, 22,  69);
  	sm.add( 8, 24,  34);
  	sm.add(21,  1,  85);
  	sm.add(12,  1,  47);
  	sm.add( 9,  3,  36);
  	//sm.add( 3,  0,  10.5); // error test
  	sm.add(19, 14,  78);
  	sm.add(23,  3,  92);
  	sm.add( 6, 10,  25);
  	sm.add(10, 19,  42);

  	sm.createCSR();
  	System.out.println("element(5, 2) = " + sm.getCSRColumn(5,  2));
  	System.out.println("element(5, 3) = " + sm.getCSRValue(5,  3));
  	System.out.println("element(6,18) = " + sm.getCSRColumn(6, 18));
  	System.out.println("element(6,19) = " + sm.getCSRValue(6, 19));
  	System.out.println("");
  	sm.createCSC();
  	System.out.println("element(5, 2) = " + sm.getCSCRow(5,  2));
  	System.out.println("element(5, 3) = " + sm.getCSCValue(5,  3));
  	System.out.println("element(6,18) = " + sm.getCSCRow(6, 18));
  	System.out.println("element(6,19) = " + sm.getCSCValue(6, 19));
  	System.out.println("");
  	//sm.setTriplet();
  	
  	System.out.println("Entry Count = " + sm.entryCount());
  	System.out.println("Mean Row Entry Count = " + sm.meanRowEntryCount());
  	System.out.println("Mean Col Entry Count = " + sm.meanRowEntryCount());
  }
}
