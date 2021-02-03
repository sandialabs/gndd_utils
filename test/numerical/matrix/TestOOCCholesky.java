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

import gov.sandia.gmp.util.numerical.matrix.Matrix;
import gov.sandia.gmp.util.statistics.Statistic;

import java.io.*;
import java.util.Date;

import static gov.sandia.gmp.util.globals.Globals.NL;

public class TestOOCCholesky
{

  /**
   * @param args
   */
  public static void main(String[] args)
  {
    TestOOCCholesky tstr = new TestOOCCholesky();

    int n = 500;
    int nb = 24;
    tstr.testCholOOC(n, nb);
  }

  /**
   * Tests the Out-Of-Core (OOC) Cholesky, forward substitution, and backward
   * substitution algorithms. The function builds a standard N x N matrix
   * which is converted into blocks and stored on disk. Then the OOC Cholesky
   * algorithm is called to find the OOC Cholesky decomposition L. Next, an
   * In-Core (IC) Cholesky algorithm is called to find the equivalent IC
   * decomposition. Then the OOC and IC decompositions are compared, element-by-
   * element to ensure that the OOC algorithm is operating correctly. The same
   * process is then performed on both the forward and backward substitution
   * algorithms to find the inverse of the original matrix. These, as before,
   * are compared with their IC counter-parts to ensure proper operation.
   * Finally, the original matrix is multiplied by the inverse and the result is
   * compared to the Identity matrix to determine the extent of round-off error
   * that occurs during the solution process. 
   *
   */
  private void testCholOOC(int n, int nb)
  {
    // OOC cholesky decomposition test
    
    // uses disk read and write to retrieve a cholesky decomposition piecemeal
    // as a set of block matrices. This test proves the principal of algorithm
    // before it is packaged into a parallel form
    
    // create a matrix
    // write matrix as a set of block files to a common directory
    // enter decomposition loop
    // F00 F10  F20       F30            F40
    //     U110 U210 U220 U310 U320 U330 U410 U420 U430 U440
    // F11 F21  F31       F41
    //     U221 U321 U331 U421 U431 U441
    // F22 F32  F42
    //     U332 U432 U442
    // F33 F43
    //     U443
    // F44
    //
    // j=0,n-1
    //   i=j, n
    //     Fi,j
    //     jk=j+1,i
    //       Ui,jk,j
    //
    // finally read in matrix and compare with standard in-core approach

    // first create a symmetric positive definite matrix (GTG)
    // write same matrix to L which will be solved using Cholesky
    // decomposition

    int nBlk = n / nb;
    if (nBlk * nb < n) ++nBlk;

    int[] info = new int [3];
    info[0] = n;
    info[1] = nBlk;
    info[2] = nb;

    System.out.println("Creating Matrix ...");
    System.out.println("            Size: " + n + " x " + n);
    System.out.println("          Blocks: " + nBlk + " x " + nBlk);
    System.out.println("  Elements/Block: " + nb + " x " + nb);
    double[][] A = new double [n][n];
    double[][] L = new double [n][n];
    for (int i = 0; i < n; ++i)
    {
      for (int j = i; j < n; ++j)
      {
        A[i][j] = (double) (i + 1) / (j + 1);
        A[j][i] = A[i][j];
        L[j][i] = L[i][j] = A[j][i];
      }
    }

    // transpose L to improve performance by retrieving row vectors instead of
    // addressing first dimensions in inner-loops

    Matrix.transposeSymmetric(L);

    // now output matrix into a set of nb x nb block files. These include the
    // original matrix (GTG), the Cholesky decomposition matrix (L) which is
    // initialized to GTG, and the inverse matrix which is initialized to I
    // (the identity matrix).

    System.out.println("Creating Blocked Matrices (G^T G, L, g^-1, and " +
                       "G^TG * g^1- = -I) ..." + NL);
    String filePath = "C:/cholOOCTest";
    String fileGTG  = filePath + "/GTG";
    String fileL    = filePath + "/L";
    String filegInv = filePath + "/gInv";
    String fileROE  = filePath + "/roe";
    
    int nr = 0;
    if (nb * nBlk > n) nr = n - nb * (nBlk - 1);

    // create off diagonal and diagonal blocks
    
    initializeGLBlocks(A, info, fileGTG, fileL);

    // create off diagonal and diagonal GINV blocks
    // (lower triangular identity matrix)

    initializeIdentity(info, filegInv, 1.0);
    initializeIdentity(info, fileROE, -1.0);

    // solve cholesky decomposition using out-of-core code

    System.out.print("Solving Out-Of-Core Cholesky ");
    long strttm = (new Date()).getTime();
    for (int j = 0; j < nBlk; ++j)
    {
      for (int i = j; i < nBlk; ++i)
      {
        if (i == j)
          finalizeDiagonal(j, info, fileL);
        else
          finalizeOffDiagonal(i, j, info, fileL);

        for (int jk = j+1; jk < i+1; ++jk)
        {
          if (i == jk)
            updateDiagonal(jk, j, info, fileL);
          else
            updateOffDiagonal(i, jk, j, info, fileL);
        }
      }
    }
    System.out.println("(" + runTime((new Date()).getTime(), strttm) + ") ...");

    // solve cholesky using in-core code

    //CholeskyDecomposition chol = new CholeskyDecomposition(A);
    //double[][] L = chol.getDecomposedMatrix();

    System.out.print("Solving In-Core Cholesky ");
    strttm = (new Date()).getTime();
    for (int j = 0; j < n; ++j)
    {
      double [] Lrowj = L[j];
      for (int k = 0; k < j; ++k)
      {
        double [] Lrowk = L[k];
        for (int i = j; i < n; ++i)
          Lrowj[i] -= Lrowk[i] * Lrowk[j];
      }
      Lrowj[j] = Math.sqrt(Lrowj[j]);
      for (int i = j+1; i < n; ++i)
      {
        Lrowj[i] /= Lrowj[j];
      }
    }
    System.out.println("(" + runTime((new Date()).getTime(), strttm) + ") ...");
  
    // now compare results

    System.out.println("Validating Cholesky Solution ...");
    double maxdiff = 0.0;
    double[][] block = null;
    for (int i = 0; i < nBlk; ++i)
    {
      int ni = nb;
      if ((i == nBlk-1) && (nr > 0)) ni = nr;
      for (int j = 0; j < i+1; ++j)
      {
        int nj = nb;
        if ((j == nBlk-1) && (nr > 0)) nj = nr;
        if (i == j)
        {
          block = readBlock(i, j, info, fileL);
          for (int ik = 0; ik < ni; ++ik)
          {
            for (int jk = 0; jk < ik+1; ++jk)
            {
              if (block[jk][ik] != L[nb*j + jk][nb*i + ik])
              {
                System.out.println("  Block(" + i + "," + j + ")[" +
                                   ik + "," + jk + "] = " +
                                   block[jk][ik] + "; Does not equal L(" +
                                   (nb*i + ik) + "," + (nb*j + jk) + ") = " +
                                   L[nb*j +jk][nb*i + ik] + " ...");
                double diff = Math.abs(block[jk][ik] - L[nb*j + jk][nb*i + ik]);
                if (diff > maxdiff) maxdiff = diff;
              }
            }
          }
        }
        else
        {
          block = readBlock(i, j, info, fileL);
          for (int ik = 0; ik < ni; ++ik)
          {
            for (int jk = 0; jk < nj; ++jk)
            {
              if (block[jk][ik] != L[nb*j +jk][nb*i + ik])
              {
                System.out.println("  Block(" + i + "," + j + ")[" +
                                   ik + "," + jk + "] = " +
                                   block[jk][ik] + "; Does not equal L(" +
                                   (nb*i + ik) + "," + (nb*j + jk) + ") = " +
                                   L[nb*j + jk][nb*i + ik] + " ...");
                double diff = Math.abs(block[jk][ik] - L[nb*j + jk][nb*i + ik]);
                if (diff > maxdiff) maxdiff = diff;
              }
            }
          }
        }
      }
    }
    System.out.println("  Maximum Cholesky Difference = " + maxdiff + NL);

    // perform forward substitution on out-of-core blocks

    System.out.print("Solving Out-Of-Core Forward Substitution ");
    strttm = (new Date()).getTime();
    for (int k = 0; k < nBlk; ++k)
    {
      for (int i = k; i < nBlk; ++i)
      {
        finalizeForwardSubstitution(i, k, info, fileL, filegInv);

        for (int j = i+1; j < nBlk; ++j)
          updateForwardSubstitution(j, k, i, info, fileL, filegInv);
      }
    }
    System.out.println("(" + runTime((new Date()).getTime(), strttm) + ") ...");

    // now do in-core forward substitution (ginv is stored as the transpose
    // to improve performance in the same way as L was transposed).
    // (note L should be transposed here again to improve performance)

    System.out.print("Solving In-Core Forward Substitution ");
    strttm = (new Date()).getTime();
    double[][] ginv = new double [n][n];
    for (int i = 0; i < n; ++i) ginv[i][i] = 1.0;
    Matrix.transposeSymmetric(L);
    for (int k = 0; k < n; ++k)
    {
      double[] growK = ginv[k];
      for (int i = k; i < n; ++i)
      {
        double[] LrowI = L[i];
        for (int j = k; j < i; ++j) growK[i] -= LrowI[j] * growK[j];
        growK[i] /= LrowI[i];
      }
    }
    Matrix.transposeSymmetric(L);
    System.out.println("(" + runTime((new Date()).getTime(), strttm) + ") ...");

    // validate forward substitution

    System.out.println("Validating Forward Substitution Solution ...");
    maxdiff = 0.0;
    for (int i = 0; i < nBlk; ++i)
    {
      for (int j = 0; j <= i; ++j)
      {
        //System.out.println("  Block " + i + "," +j);
        int nrr = nb;
        if ((i+1) * nb > n) nrr = n - i * nb;
        double[][] gf  = readBlock(i, j, info, filegInv);
        for (int ik = 0; ik < nrr; ++ik)
        {
          int nj = nb;
          if (i == j) nj = ik+1;
          for (int jk = 0; jk < nj; ++jk)
          {
            //System.out.println("    Element " + ik + "," +jk);
            if (gf[jk][ik] != ginv[nb*j + jk][nb*i + ik])
            {
              System.out.println("  ginv(" + i + "," + j + ")[" +
                                 ik + "," + jk + "] FS = " +
                                 gf[jk][ik] + "; Does not equal in-core ginv(" +
                                 (nb*i + ik) + "," + (nb*j + jk) + ") FS = " +
                                 ginv[nb*j + jk][nb*i + ik] + " ...");
              double diff = Math.abs(gf[jk][ik] - ginv[nb*j + jk][nb*i + ik]);
              if (diff > maxdiff) maxdiff = diff;
            }
          }
        }
      }
    }
    System.out.println("  Maximum Forward Substitution Difference = " + maxdiff + NL);

    // perform backward substitution on out-of-core blocks

    System.out.print("Solving Out-Of-Core Backward Substitution ");
    strttm = (new Date()).getTime();
    for (int k = 0; k < nBlk; ++k)
    {
      for (int i = nBlk - 1; i >= k; --i)
      {
        for (int j = nBlk - 1; j > i; --j)
          updateBackwardSubstitution(i, k, j, info, fileL, filegInv);

        finalizeBackwardSubstitution(i, k, info, fileL, filegInv);
      }
    }
    System.out.println("(" + runTime((new Date()).getTime(), strttm) + ") ...");

    // now do in-core backward substitution

    System.out.print("Solving In-Core Backward Substitution ");
    strttm = (new Date()).getTime();
    for (int k = 0; k < n; ++k)
    {
      for (int i = n-1; i >= k; --i)
      {
        double[] growK = ginv[k];
        double[] LrowI = L[i];
        for (int j = n-1; j > i; --j)
          growK[i] -= LrowI[j] * growK[j];
        growK[i] /= LrowI[i];
      }
    }
    System.out.println("(" + runTime((new Date()).getTime(), strttm) + ") ...");

    // validate backward substitution

    System.out.println("Validating Backward Substitution Solution ...");
    maxdiff = 0.0;
    for (int i = 0; i < nBlk; ++i)
    {
      for (int j = 0; j <= i; ++j)
      {
        //System.out.println("  Block " + i + "," +j);
        int nrr = nb;
        if ((i+1) * nb > n) nrr = n - i * nb;
        double[][] gf  = readBlock(i, j, info, filegInv);
        for (int ik = 0; ik < nrr; ++ik)
        {
          int nj = nb;
          if (i == j) nj = ik+1;
          for (int jk = 0; jk < nj; ++jk)
          {
            if (gf[jk][ik] != ginv[nb*j + jk][nb*i + ik])
            {
              System.out.println("  ginv(" + i + "," + j + ")[" +
                                 ik + "," + jk + "] BS = " +
                                 gf[jk][ik] + "; Does not equal in-core ginv(" +
                                 (nb*i + ik) + "," + (nb*j + jk) + ") BS = " +
                                 ginv[nb*j + jk][nb*i + ik] + " ...");
              double diff = Math.abs(gf[jk][ik] - ginv[nb*j + jk][nb*i + ik]);
              if (diff > maxdiff) maxdiff = diff;
            }
          }
        }
      }
    }
    System.out.println("  Maximum Backward Substitution Difference = " + maxdiff + NL);

    // now do out-of-core matrix multiply ROE = GTP * gInv and save all elements into
    // a Statistic object
    
    System.out.print("Solving OOC Matrix Multiply" +
                     "(inversion accuracy: A*ginv - I)");
    Statistic statOOC = new Statistic();
    strttm = (new Date()).getTime();
    for (int i = 0; i < nBlk; ++i)
    {
      nr = info[2];
      if ((i+1) * info[2] > info[0]) nr = info[0] - i * info[2];
      for (int j = 0; j <= i; ++j)
      {
        updateMatrixMultiply(i, j, info, fileROE, fileGTG, filegInv);
        double[][] c = readBlock(i, j, info, fileROE);
        Matrix.transposeSymmetric(c);
        int col = info[2];
        for (int ic = 0; ic < nr; ++ic)
        {
          if (i == j) col = ic + 1; 
          double[] crowi = c[ic];
          for (int jc = 0; jc < col; ++jc)
          {
            statOOC.add(crowi[jc]);
          }
        }
      }
    }
    System.out.println("(" + runTime((new Date()).getTime(), strttm) + ") ...");

    // now do in-core inversion accuracy (perform IAG = A * ginv and see
    // how close it is to I (IAG - I))

    System.out.print("Solving In-Core Matrix Multiply" +
                     "(inversion accuracy: A*ginv - I)");
    Statistic stat = new Statistic();
    strttm = (new Date()).getTime();
    double[][] IAG = new double [n][];
    for (int i = 0; i < n; ++i)
    {
      IAG[i] = new double [i+1];
      IAG[i][i] = -1.0;
    }
    Matrix.transposeSymmetric(ginv);
    for (int i = 0; i < n; ++i)
    {
      double[] ArowI = A[i];
      double[] IrowI = IAG[i];
      for (int j = 0; j <= i; ++j)
      {
        double[] growJ = ginv[j];
        for (int k = 0; k < j; ++k)
          IrowI[j] += ArowI[k] * growJ[k];
      }
    }
    Matrix.transposeSymmetric(ginv);
    for (int i = 0; i < n; ++i)
    {
      double[] ArowI = A[i];
      double[] IrowI = IAG[i];
      for (int j = 0; j <= i; ++j)
      {
        double[] growJ = ginv[j];
        for (int k = j; k < n; ++k)
          IrowI[j] += ArowI[k] * growJ[k];
        stat.add(IrowI[j]);
      }
    }
    System.out.println("(" + runTime((new Date()).getTime(), strttm) + ") ...");

    // Output round-off error statistics for in-core and out-of-core calculations

    System.out.println("Validating Matrix Multiply ...");
    System.out.println("  OOC:" + NL +
                       "    count  = " + statOOC.getCount() + NL +
                       "    min    = " + statOOC.getMinimum() + NL +
                       "    max    = " + statOOC.getMaximum() + NL +
                       "    mean   = " + statOOC.getMean() + NL +
                       "    stddev = " + statOOC.getStdDev() + NL +
                       "    rms    = " + statOOC.getRMS());

    System.out.println("  IC:" + NL +
                       "    count  = " + stat.getCount() + NL +
                       "    min    = " + stat.getMinimum() + NL +
                       "    max    = " + stat.getMaximum() + NL +
                       "    mean   = " + stat.getMean() + NL +
                       "    stddev = " + stat.getStdDev() + NL +
                       "    rms    = " + stat.getRMS());

    System.out.println("");
    System.out.println("Done ...");
    System.out.println("");
  }

  /**
   * Optimized matrix multiply c = a * b. This
   * algorithm reads in the c block to be updated (c[iG][jG]), and the k
   * blocks a[iG][k] and b[k][jG], for k = 0 to info[2]. After calculating
   * c it is transposed into a upper triangular matrix and rewritten to the
   * disk.  
   * 
   * @param iG The block row index of the matrix block to be evaluated.
   * @param jG The block column index of the matrix block to be evaluated.
   * @param info A three element integer array containing the size information
   *             of the blocks (info[0] = true matrix size, info[1] = number
   *             of blocks into which the true matrix is divided, and info[2] =
   *             the number of elements in each block.
   * @param pathC The path to the matrix block to be calculated (c).
   * @param pathA The path to the a matrix block to be calculated.
   * @param pathB The path to the b matrix block to be calculated.
   */
  private void updateMatrixMultiply(int iG, int jG, int[] info,
                                    String pathC, String pathA, String pathB)
  {
    // read in pathC sub-block and loop over all k pathA and pathB sub-blocks

    double[][] c = readBlock(iG, jG, info, pathC);
    double[][] ak, bk;
    for (int kB = 0; kB < info[1]; ++kB)
    {
      // determine maximum rows

      int nr = info[2];
      if ((kB+1) * info[2] > info[0]) nr = info[0] - kB * info[2];

      // read in pathA and pathB sub-blocks

      if (kB > iG)
        ak = readBlock(kB, iG, info, pathA);
      else
        ak = readBlock(iG, kB, info, pathA);

      if (kB < jG)
        bk = readBlock(jG, kB, info, pathB);
      else
        bk = readBlock(kB, jG, info, pathB);

      // If the block is a diagonal then fill the lower symmetric portion

      if (kB == iG) fillLowerSymmetric(ak, nr);
      if (kB == jG) fillLowerSymmetric(bk, nr);

      // if the kB a or b block is less than iG or jG respectively then
      // transpose the block

      if (kB < iG) Matrix.transposeSymmetric(ak);
      if (kB < jG) Matrix.transposeSymmetric(bk);

      // loop over all elements of the c block and accumulate a * b

      int col = info[2];
      for (int i = 0; i < info[2]; ++i)
      {
        // only accumulate columns up to i if c is a diagonal block

        if (iG == jG) col = i+1; 
        double[] arowi = ak[i];
        double[] crowi = c[i];
        for (int j = 0; j < col; ++j)
        {
          // acumulate a[i][k] * b[j][k]

          double[] browj = bk[j];
          for (int k = 0; k < nr; ++k) crowi[j] += arowi[k] * browj[k];
        }
      }
    }

    // transpose c to store upper triangular and write out changes

    Matrix.transposeSymmetric(c);
    writeBlock(c, iG, jG, info, pathC);
  }

  /**
   * Optimized backward substitution block rank-1 update algorithm. This
   * algorithm reads in the block to be updated (g[iG][kG]), the helper
   * block that has already been finalized and will be used to update g
   * (gJ[jG][kG]), and the Cholesky decomposition block (LJ[jg][ig]).
   * Then the rank-1 backward update is performed after which the updated
   * block g is rewritten to disk.  
   * 
   * @param iG The block row index of the block to be updated.
   * @param kG The block column index of the block to be updated.
   * @param jG The block row index of the helper block used in the update.
   * @param info A three element integer array containing the size information
   *             of the blocks (info[0] = true matrix size, info[1] = number
   *             of blocks into which the true matrix is divided, and info[2] =
   *             the number of elements in each block.
   * @param pathL The path to the Cholesky decomposition directory.
   * @param pathGinv The path to the inverse matrix directory.
   */
  private void updateBackwardSubstitution(int iG, int kG, int jG, int[] info,
                                          String pathL, String pathGinv)
  {
    // determine maximum rows

    int nr = info[2];
    if ((jG+1) * info[2] > info[0]) nr = info[0] - jG * info[2];

    // read in sub-blocks

    double[][] g  = readBlock(iG, kG, info, pathGinv);
    double[][] LJ = readBlock(jG, iG, info, pathL);
    double[][] gJ = readBlock(jG, kG, info, pathGinv);

    // loop over each column in g and update

    for (int k = 0; k < info[2]; ++k)
    {
      double[] growk = g[k];
      double[] gJrowk = gJ[k];
      int js = 0;
      if (iG == kG) js = k;
      for (int i = info[2]-1; i >= js; --i)
      {
        double[] LJrowi = LJ[i];
        for (int j = nr-1; j >= 0; --j) growk[i] -= LJrowi[j] * gJrowk[j];
      }
    }

    // write out changes

    writeBlock(g, iG, kG, info, pathGinv);
  }

  /**
   * Optimized backward substitution block finalization algorithm. This
   * algorithm reads in the block to be updated (g[iG][kG]) and the
   * Cholesky decomposition block (LI[ig][ig]). Then the backward
   * substitution of the block is performed after which the updated
   * block g is rewritten to disk.  
   * 
   * @param iG The block row index of the block to be updated.
   * @param kG The block column index of the block to be updated.
   * @param info A three element integer array containing the size information
   *             of the blocks (info[0] = true matrix size, info[1] = number
   *             of blocks into which the true matrix is divided, and info[2] =
   *             the number of elements in each block.
   * @param pathL The path to the Cholesky decomposition directory.
   * @param pathGinv The path to the inverse matrix directory.
   */
  private void finalizeBackwardSubstitution(int iG, int kG, int[] info,
                                            String pathL, String pathGinv)
  {
    // determine maximum rows

    int nr = info[2];
    if ((iG+1) * info[2] > info[0]) nr = info[0] - iG * info[2];

    // read in sub-blocks

    double[][] g  = readBlock(iG, kG, info, pathGinv);
    double[][] LI = readBlock(iG, iG, info, pathL);

    // loop over each column in g and update
    
    int kn = info[2];
    if (iG == kG) kn = nr;
    for (int k = 0; k < kn; ++k)
    {
      double[] growk = g[k];
      int i0 = 0;
      if (iG == kG) i0 = k;
      for (int i = nr-1; i >= i0; --i)
      {
        double[] LIrowi = LI[i];
        for (int j = nr-1; j > i; --j) growk[i] -= LIrowi[j] * growk[j];
        growk[i] /= LIrowi[i];
      }
    }

    // write out changes

    writeBlock(g, iG, kG, info, pathGinv);
  }

  /**
   * Optimized forward substitution block rank-1 update algorithm. This
   * algorithm reads in the block to be updated (g[jG][kG]), the helper
   * block that has already been finalized and will be used to update g
   * (gI[iG][kG]), and the Cholesky decomposition block (LI[jg][ig]).
   * Then the rank-1 forward update is performed after which the updated
   * block g is rewritten to disk.  
   * 
   * @param iG The block row index of the block to be updated.
   * @param kG The block column index of the block to be updated.
   * @param jG The block row index of the helper block used in the update.
   * @param info A three element integer array containing the size information
   *             of the blocks (info[0] = true matrix size, info[1] = number
   *             of blocks into which the true matrix is divided, and info[2] =
   *             the number of elements in each block.
   * @param pathL The path to the Cholesky decomposition directory.
   * @param pathGinv The path to the inverse matrix directory.
   */
  private void updateForwardSubstitution(int iG, int kG, int jG, int[] info,
                                         String pathL, String pathGinv)
  {
    // determine maximum rows

    int nr = info[2];
    if ((iG+1) * info[2] > info[0]) nr = info[0] - iG * info[2];

    // read in sub-blocks

    double[][] g  = readBlock(iG, kG, info, pathGinv);
    double[][] LI = readBlock(iG, jG, info, pathL);
    double[][] gI = readBlock(jG, kG, info, pathGinv);
    Matrix.transposeSymmetric(LI);

    // loop over each column in g and update

    for (int k = 0; k < info[2]; ++k)
    {
      double[] growk = g[k];
      double[] gIrowk = gI[k];
      int js = 0;
      if (jG == kG) js = k;
      for (int i = 0; i < nr; ++i)
      {
        double[] LIrowi = LI[i];
        for (int j = js; j < info[2]; ++j) growk[i] -= LIrowi[j] * gIrowk[j];
      }
    }

    // write out changes

    if (((iG == 10) && (kG == 5)) || ((jG == 10) && (kG == 5)) ||
        ((jG == 10) && (iG == 5)))
    {
      System.out.println("   Update FWD Substitution G(" +
                         iG + "," + kG +
                         ") with L(" + iG + "," + jG + ") and G(" +
                         jG + "," + kG + ")");
    }
    writeBlock(g, iG, kG, info, pathGinv);
  }

  /**
   * Optimized forward substitution block finalization algorithm. This
   * algorithm reads in the block to be updated (g[iG][kG]) and the
   * Cholesky decomposition block (LI[ig][ig]). Then the forward
   * substitution of the block is performed after which the updated
   * block g is rewritten to disk.  
   * 
   * @param iG The block row index of the block to be updated.
   * @param kG The block column index of the block to be updated.
   * @param info A three element integer array containing the size information
   *             of the blocks (info[0] = true matrix size, info[1] = number
   *             of blocks into which the true matrix is divided, and info[2] =
   *             the number of elements in each block.
   * @param pathL The path to the Cholesky decomposition directory.
   * @param pathGinv The path to the inverse matrix directory.
   */
  private void finalizeForwardSubstitution(int iG, int kG, int[] info,
      String pathL, String pathGinv)
  {
    // determine maximum rows

    int nr = info[2];
    if ((iG+1) * info[2] > info[0]) nr = info[0] - iG * info[2];

    // read in sub-blocks ... transpose LI for performance

    double[][] g  = readBlock(iG, kG, info, pathGinv);
    double[][] LI = readBlock(iG, iG, info, pathL);
    Matrix.transposeSymmetric(LI);
    
    // loop over each column in g and update

    int kn = info[2];
    if (iG == kG) kn = nr;
    for (int k = 0; k < kn; ++k)
    {
      double[] growk = g[k];
      int i0 = 0;
      if (iG == kG) i0 = k;
      for (int i = i0; i < nr; ++i)
      {
        double[] LIrowi = LI[i];
        for (int j = i0; j < i; ++j) growk[i] -= LIrowi[j] * growk[j];
        growk[i] /= LIrowi[i];
      }
    }

    // write out changes

    if (iG == 10)
    {
      System.out.println("   Finalize FWD Substitution G(" +
                         iG + "," + kG +
                         ") with L(" + iG + "," + iG + ")");
    }
    writeBlock(g, iG, kG, info, pathGinv);
  }

  /**
   * Optimized Cholesky off-diagonal block rank-1 update algorithm. This function
   * reads the off-diagonal cholesky block to be updated (a[iM][jM]), the
   * finalized row-specific helper block (aI[im][km]), and the column specific
   * helper block (aJ[jm][km]). Then the rank-1 update is performed after which
   * the updated block is rewritten back to disk. 
   * 
   * @param iM The block row index of the block to be updated.
   * @param jM The block column index of the block to be updated.
   * @param kM The block column index of the two helper blocks.
   * @param info A three element integer array containing the size information
   *             of the blocks (info[0] = true matrix size, info[1] = number
   *             of blocks into which the true matrix is divided, and info[2] =
   *             the number of elements in each block.
   * @param path The path to the Cholesky decomposition directory.
   */
  private void updateOffDiagonal(int iM, int jM, int kM, int[] info, String path)
  {
    // determine maximum rows

    int nr = info[2];
    if ((iM+1) * info[2] > info[0]) nr = info[0] - iM * info[2];

    // read in sub-blocks

    double[][] a  = readBlock(iM, jM, info, path);
    double[][] aI = readBlock(iM, kM, info, path);
    double[][] aJ = readBlock(jM, kM, info, path);

    // loop over each column in a and update

    for (int j = 0; j < info[2]; ++j)
    {
      double[] arowj = a[j];
      for (int k = 0; k < info[2]; ++k)
      {
        double[] aJrowk = aJ[k];
        double[] aIrowk = aI[k];
        for (int i = 0; i < nr; ++i) arowj[i] -= aIrowk[i] * aJrowk[j];
      }
    }

    // write out changes

    writeBlock(a, iM, jM, info, path);
  }

  /**
   * Optimized Cholesky diagonal block rank-1 update algorithm. This function
   * reads the diagonal cholesky block to be updated (a[jM][jM]) and the column
   * specific helper block (aJ[jM][kM]). Then the rank-1 update and column
   * scale operations are performed after which the updated block is rewritten
   * back to disk.  
   * 
   * @param jM The block row index of the block to be updated.
   * @param kM The block column index of the helper block.
   * @param info A three element integer array containing the size information
   *             of the blocks (info[0] = true matrix size, info[1] = number
   *             of blocks into which the true matrix is divided, and info[2] =
   *             the number of elements in each block.
   * @param path The path to the Cholesky decomposition directory.
   */
  private void updateDiagonal(int jM, int kM, int[] info, String path)
  {
    // determine maximum rows

    int n = info[2];
    if ((jM+1) * info[2] > info[0]) n = info[0] - jM * info[2];

    // read in sub-blocks

    double[][] a  = readBlock(jM, jM, info, path);
    double[][] aJ = readBlock(jM, kM, info, path);

    // loop over each column in a and update and scale

    for (int j = 0; j < n; ++j)
    {
      double[] arowj = a[j];
      for (int k = 0; k < info[2]; ++k)
      {
        double[] aJrowk = aJ[k];
        for (int i = j; i < n; ++i) arowj[i] -= aJrowk[i] * aJrowk[j];
      }
    }

    // write out changes

    writeBlock(a, jM, jM, info, path);
  }

  /**
   * Optimized Cholesky off-diagonal block finalization algorithm. This
   * function reads the off-diagonal cholesky block to be updated (a[iM][jM])
   * and the column specific diagonal helper block (aJ[jM][jM]). Then the
   * rank-1 update and column scale operations are performed after which the
   * updated block is rewritten back to disk.
   *   
   * @param iM The block row index of the block to be updated.
   * @param jM The block row/column index of the diagonal helper block.
   * @param info A three element integer array containing the size information
   *             of the blocks (info[0] = true matrix size, info[1] = number
   *             of blocks into which the true matrix is divided, and info[2] =
   *             the number of elements in each block.
   * @param path The path to the Cholesky decomposition directory.
   */
  private void finalizeOffDiagonal(int iM, int jM, int[] info, String path)
  {
    // determine maximum rows

    int nr = info[2];
    if ((iM+1) * info[2] > info[0]) nr = info[0] - iM * info[2];

    // read in sub-blocks

    double[][] a  = readBlock(iM, jM, info, path);
    double[][] aJ = readBlock(jM, jM, info, path);

    // loop over each column in a and update and scale

    for (int j = 0; j < info[2]; ++j)
    {
      double[] arowj = a[j];
      double[] aJrowj = aJ[j];
      for (int k = 0; k < j; ++k)
      {
        double[] arowk = a[k];
        double[] aJrowk = aJ[k];
        for (int i = 0; i < nr; ++i) arowj[i] -= arowk[i] * aJrowk[j];
      }
      for (int i = 0; i < nr; ++i) arowj[i] /= aJrowj[j];
    }

    // write out changes

    writeBlock(a, iM, jM, info, path);
  }

  /**
   * Optimized Cholesky diagonal block finalization algorithm. This
   * function reads the diagonal cholesky block to be updated (a[jM][jM])
   * and performs the Cholesky decomposition on the block after which the
   * updated block is rewritten back to disk.
   *   
   * @param jM The block row/column index of the diagonal block to be updated.
   * @param info A three element integer array containing the size information
   *             of the blocks (info[0] = true matrix size, info[1] = number
   *             of blocks into which the true matrix is divided, and info[2] =
   *             the number of elements in each block.
   * @param path The path to the Cholesky decomposition directory.
   */
  private void finalizeDiagonal(int jM, int[] info, String path)
  {
    // determine maximum rows

    int n = info[2];
    if ((jM+1) * info[2] > info[0]) n = info[0] - jM * info[2];

    // read in sub-blocks

    double[][] a  = readBlock(jM, jM, info, path);

    // loop over each column in a and perform Cholesky decomposition

    for (int j = 0; j < n; ++j)
    {
      double[] arowj = a[j];
      for (int k = 0; k < j; ++k)
      {
        double[] arowk = a[k];
        for (int i = j; i < n; ++i) arowj[i] -= arowk[i] * arowk[j];
      }
      arowj[j] = Math.sqrt(arowj[j]);
      for (int i = j+1; i < n; ++i) arowj[i] /= arowj[j];
    }

    // write out changes

    writeBlock(a, jM, jM, info, path);
  }

  /**
   * Read and return the disk based matrix block iB,jB. This function assumes the
   * block is stored as part of a larger lower-triangular matrix (jB <= iB). Each
   * block is stored and read as its transpose to facilitate the matrix solution
   * performance.
   * 
   * @param iB The row index of the block to be read and returned.
   * @param jB The column index of the block to be read and returned.
   * @param info A three element integer array containing the size information
   *             of the blocks (info[0] = true matrix size, info[1] = number
   *             of blocks into which the true matrix is divided, and info[2] =
   *             the number of elements in each block.
   * @param path The path to the block storage directory.
   * 
   * @return Sub-matrix block [iB][jB] of a lower triangular matrix store as the
   *         transpose of the block.
   */
  private double[][] readBlock(int iB, int jB, int[] info, String path)
  {
    // hard code transpose switch for now

    boolean trnsps = true;

    // create the block to be returned

    double[][] block = new double [info[2]][info[2]];

    // determine maximum rows

    int nr = info[2];
    if ((iB + 1) * info[2] > info[0]) nr = info[0] - iB * info[2];

    // define block file name

    String snew = path + "_" + Integer.toString(iB) + "_" + Integer.toString(jB);

    // create input file and input stream

    File inFile = new File(snew);
    byte[] data = new byte [(int) inFile.length()];
    try
    {
      // create stream, read, close. Create a new stream from in-core data and read

      DataInputStream inpfil = new DataInputStream(new FileInputStream(inFile));
      inpfil.readFully(data);
      inpfil.close();
      DataInputStream readData = new DataInputStream(new ByteArrayInputStream(data));

      // set limits

      int nrow = nr;
      int jStrt = 0;
      int ncol = nr;
      if ((iB != jB) && trnsps) nrow = info[2]; 
      
      // loop over each row

      for (int i = 0; i < nrow; ++i)
      {
        // get ith row and set inner loop limits

        double[] blocki = block[i];
        if (iB == jB)
        {
          if (trnsps)
            jStrt = i;
          else
            ncol = i + 1;
        }
        else if (!trnsps)
          ncol = info[2];

        // read in row data

        for (int j = jStrt; j < ncol; ++j) blocki[j] = readData.readDouble(); 
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    
    return block;
  }

  /**
   * Write input block iB, jB to the disk.  This function assumes the
   * block is stored as part of a larger lower-triangular matrix (jB <= iB). Each
   * block is stored and read as its transpose to facilitate the matrix solution
   * performance.
   * 
   * @param block The block to be stored on disk.
   * @param iB The row index of the block.
   * @param jB The column index of the block.
   * @param info A three element integer array containing the size information
   *             of the blocks (info[0] = true matrix size, info[1] = number
   *             of blocks into which the true matrix is divided, and info[2] =
   *             the number of elements in each block.
   * @param path The path to the block storage directory.
   */
  private void writeBlock(double[][] block, int iB, int jB, int[] info, String path)
  {
    ByteArrayOutputStream bos = null;

    // hard code transpose switch for now

    boolean trnsps = true;

    // determine maximum rows

    final int DBL = Double.SIZE / 8;
    int nr = info[2];
    if ((iB + 1) * info[2] > info[0]) nr = info[0] - iB * info[2];

    // write block back to file

    try
    {
      // set limits

      int nrow = nr;
      int jStrt = 0;
      int ncol = nr;
      int ns    = DBL * nr;
      if (iB == jB)
        ns *= (nr + 1) / 2;
      else
        ns *= info[2];

      if ((iB != jB) && trnsps) nrow = info[2]; 

      // create output stream

      bos = new ByteArrayOutputStream(ns);
      DataOutputStream writeData = new DataOutputStream(bos);

      // loop over all rows

      for (int i = 0; i < nrow; ++i)
      {
        // set ith row and inner loop limits

        double[] blocki = block[i];
        if (iB == jB)
        {
          if (trnsps)
            jStrt = i;
          else
            ncol = i + 1;
        }
        else if (!trnsps)
          ncol = info[2];

        // write row

        for (int j = jStrt; j < ncol; ++j) writeData.writeDouble(blocki[j]); 
      }
      
      // open output file as a DataOutputStream and write byte buffer to file

      String snew = path + "_" + Integer.toString(iB) + "_" + Integer.toString(jB);
      File otFile = new File(snew);
      DataOutputStream outfil = new DataOutputStream(new FileOutputStream(otFile));
      outfil.write(bos.toByteArray());

      // close file and exit

      outfil.close();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }

  /**
   * Writes the starting matrix (G^T * G) and the initial Cholesky decomposition
   * matrix (also G^T * G) to the paths defined by fileGTG and fileL. The input
   * matrix (G^T * G) is provided in A. Only the lower triangular parts of the
   * matrix our output. Additionally, each block is transposed which aids the
   * performance of the Cholesky, and forward and backward block solution
   * algorithms. Each block is defined by the the input matrix size information
   * (info).
   * 
   * @param A The initial input matrix (G^T * G).
   * @param info A three element integer array containing the size information
   *             of the blocks (info[0] = true matrix size, info[1] = number
   *             of blocks into which the true matrix is divided, and info[2] =
   *             the number of elements in each block.
   * @param fileGTG The path to the block storage directory for the matrix A.
   * @param fileL The path to the block storage directory for the initial
   *              Cholesky decomposition.
   */
  private void initializeGLBlocks(double[][] A, int[] info, String fileGTG,
                                  String fileL)
  {
    // build initial GTG and L block matrices ... first create the storage
    // block and loop over all block rows

    double[][] block = new double [info[2]][info[2]];
    for (int i = 0; i < info[1]; ++i)
    {
      // determine the number of block rows.

      int nr = info[2];
      if ((i + 1) * info[2] > info[0]) nr = info[0] - i * info[2];

      // loop over all block columns

      for (int j = 0; j < i+1; ++j)
      {
        // see if this is a diagonal block or not

        if (i == j)
        {
          // diagonal block ... build the block from A

          for (int ik = 0; ik < nr; ++ik)
          {
            for (int jk = 0; jk < ik+1; ++jk)
            {
              // set the transpose element
              block[jk][ik] = A[info[2]*i + ik][info[2]*j +jk];
            }
          }

          // write the block to both repositories

          writeBlock(block, i, j, info, fileGTG);
          writeBlock(block, i, j, info, fileL);
        }
        else
        {
          // non-diagonal block ... build the block from A

          for (int ik = 0; ik < nr; ++ik)
          {
            for (int jk = 0; jk < info[2]; ++jk)
            {
              // set the transpose element
              block[jk][ik] = A[info[2]*i + ik][info[2]*j +jk];
            }
          }

          // write the block to both repositories

          writeBlock(block, i, j, info, fileGTG);
          writeBlock(block, i, j, info, fileL);
        }
      }
    }
  }

  /**
   * Writes the initial inverse matrix as a set of blocks defined by the
   * the input matrix size information (info). The initial inverse matrix
   * is simply the identity matrix written out in block format.
   * 
   * @param info A three element integer array containing the size information
   *             of the blocks (info[0] = true matrix size, info[1] = number
   *             of blocks into which the true matrix is divided, and info[2] =
   *             the number of elements in each block.
   * @param fileGI The path to the block storage directory.
   */
  private void initializeIdentity(int[] info, String fileGI, double diag)
  {
    // build initial gInv blocks

    double[][] block  = null; 
    double[][] dBlock  = new double [info[2]][info[2]];
    double[][] odBlock = new double [info[2]][info[2]];
    for (int i = 0; i < info[2]; ++i) dBlock[i][i] = diag;
    for (int i = 0; i < info[1]; ++i)
    {
      for (int j = 0; j < i+1; ++j)
      {
        if (i == j)
        {
          block = dBlock;
          writeBlock(block, i, j, info, fileGI);
        }
        else
        {
          block = odBlock;
          writeBlock(block, i, j, info, fileGI);
        }
      }
    }
  }

  /**
   * A simple runtime string formulator that formulates the
   * input difference in run time into
   * 
   *   "hours:minutes:seconds:milliseconds".
   * 
   * @param end The end time.
   * @param strt The start time.
   * 
   * @return The resulting time string.
   */
  private String runTime(long end, long strt)
  {
    int hrs, mns, sec;
    hrs = mns = sec = 0;
    String rtm = "";

    // for time difference and evaluate hours

    long tm = end - strt;
    if (tm > 3600000)
    {
      hrs = (int) (tm / 3600000);
      tm -= hrs * 3600000;
      rtm = hrs + ":";
    }
    else
      rtm = "0:";

    // evaluate minutes

    if (tm > 60000)
    {
      mns = (int) (tm / 60000);
      tm -= mns * 60000;
      rtm += mns + ":";
    }
    else
      rtm += "0:";

    // evaluate seconds and milliseconds

    if (tm > 1000)
    {
      sec = (int) (tm / 1000);
      tm -= sec * 1000;
      rtm += sec + ":" + tm;
    }
    else
      rtm += "0:" + tm;

    return rtm;
  }

  public void fillLowerSymmetric(double[][] a, int nr)
  {
    for (int i = 1; i < nr; ++i)
    {
      double[] arowi = a[i];
      for (int j = 0; j < i; ++j) arowi[j] = a[j][i];
    }
  }
}
