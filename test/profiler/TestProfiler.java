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

package profiler;

import gov.sandia.gmp.util.profiler.Profiler;

import java.util.Date;

public class TestProfiler
{

  /**
   * @param args
   */
  public static void main(String[] args)
  {
    TestProfiler tp = new TestProfiler();
    tp.testProfilerTop();
  }

  public void testProfilerTop()
  {
    Profiler aProfiler = new Profiler(Thread.currentThread(), 16, "Test");
    aProfiler.setTopClass("profiler.TestProfiler");
    aProfiler.setTopMethod("testProfilerTop");
    //aProfiler.outputOnSampleCount(28, true);
    aProfiler.outputOnTimer(28000, true);
    aProfiler.accumulateOn();

    Thread sampleThread = aProfiler.getProfilerSampleTaskThread();

    Profiler aProfilerST = new Profiler(sampleThread, 16, "SampleThread");
    aProfilerST.setTopClass("gov.sandia.gmp.util.profiler.Profiler$ProfilerSampleTask");
    aProfilerST.setTopMethod("run");
    //aProfiler.outputOnSampleCount(28, true);
    aProfilerST.outputOnTimer(28000, true);
    aProfilerST.accumulateOn();

    long startTime = (new Date()).getTime();
    while ((new Date()).getTime() - startTime < 60000)
    {
      testProfiler1();
      testProfiler2();
      testProfiler3();
      int linea = 0;
      ++linea;
      int lineb = 0;
      ++lineb;
      int linec = 0;
      ++linec;
    }

    aProfiler.stop();
    aProfiler.printAccumulationString();

    aProfilerST.stop();
    aProfilerST.printAccumulationString();
  }

  private void testProfiler1()
  {
    long startTime = (new Date()).getTime();
    while ((new Date()).getTime() - startTime < 5000)
    {
      testProfiler1A();
      testProfiler2B();
      testProfiler3A();
      testProfiler3B();
      int linea = 0;
      ++linea;
      int lineb = 0;
      ++lineb;
      int linec = 0;
      ++linec;
    }
  }

  private void testProfiler2()
  {
    testProfiler1A();
    testProfiler2A();
    testProfiler3B();
    long startTime = (new Date()).getTime();
    while ((new Date()).getTime() - startTime < 5000)
    {
      int linea = 0;
      ++linea;
      int lineb = 0;
      ++lineb;
    }
  }

  private void testProfiler3()
  {
    testProfiler1A();
    testProfiler2A();
    testProfiler1B();
    long startTime = (new Date()).getTime();
    while ((new Date()).getTime() - startTime < 5000)
    {
      int linea = 0;
      ++linea;
      int lineb = 0;
      ++lineb;
    }
  }

  private void testProfiler1A() // 3
  {
    long startTime = (new Date()).getTime();
    while ((new Date()).getTime() - startTime < 5000)
    {
      for (int i = 0; i < 1000; ++i)
      {
        double x = Math.sqrt(i) * Math.log10(i+1);
      }

      int linea = 0;
      ++linea;
      int lineb = 0;
      ++lineb;
    }
  }

  private void testProfiler1B() // 1
  {
    long startTime = (new Date()).getTime();
    while ((new Date()).getTime() - startTime < 5000)
    {
      int linea = 0;
      ++linea;
      int lineb = 0;
      ++lineb;
    }
  }

  private void testProfiler2A() // 2
  {
    long startTime = (new Date()).getTime();
    while ((new Date()).getTime() - startTime < 5000)
    {
      int linea = 0;
      ++linea;
      int lineb = 0;
      ++lineb;
    }
  }

  private void testProfiler2B() // 1
  {
    long startTime = (new Date()).getTime();
    while ((new Date()).getTime() - startTime < 5000)
    {
      int linea = 0;
      ++linea;
      int lineb = 0;
      ++lineb;
    }
  }

  private void testProfiler3A() // 1
  {
    long startTime = (new Date()).getTime();
    while ((new Date()).getTime() - startTime < 5000)
    {
      int linea = 0;
      ++linea;
      int lineb = 0;
      ++lineb;
    }
  }

  private void testProfiler3B() // 2
  {
    long startTime = (new Date()).getTime();
    while ((new Date()).getTime() - startTime < 5000)
    {
      int linea = 0;
      ++linea;
      int lineb = 0;
      ++lineb;
    }
  }
}
