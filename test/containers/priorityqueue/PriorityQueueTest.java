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

package containers.priorityqueue;

import gov.sandia.gmp.util.containers.priorityqueue.PriorityQueue;
import gov.sandia.gmp.util.containers.priorityqueue.PriorityQueueNode;

import java.util.Random;

public class PriorityQueueTest
{
  class TestNode implements PriorityQueueNode
  {
    private int aPriority   = 0;
    private int aQueueIndex = 0;
    private int aIndex      = 0;

    public int getPriority()
    {
      return aPriority;
    }

    public void setPriority(int p)
    {
      aPriority = p;      
    }

    public int getQueueIndex()
    {
      return aQueueIndex;
    }

    public void setQueueIndex(int qi)
    {
      aQueueIndex = qi;
    }

    public int getIndex()
    {
      return aIndex;
    }

    public void setIndex(int qi)
    {
      aIndex = qi;
    }
  }
  
  /**
   * @param args
   */
  public static void main(String[] args)
  {
    // TODO Auto-generated method stub

    PriorityQueueTest pqt = new PriorityQueueTest();
    pqt.test1();
  }

  private void test1()
  {
    PriorityQueue pq = new PriorityQueue(2000, -1, 1000);

    PriorityQueueTest.TestNode[] tn = new PriorityQueueTest.TestNode [100];
    Random rng = new Random();
    
    for (int i = 0; i < tn.length; ++i)
    {
      tn[i] = new TestNode();
      int rnd = rng.nextInt(1000);
      tn[i].setIndex(i);
      tn[i].setPriority(rnd);
      pq.insert(tn[i]);
    }

    System.out.println("Queue Order:");
    for (int i = 0; i < pq.size(); ++i)
    {
      TestNode pqtn = (TestNode) pq.getNode(i);
      System.out.println("Priority: " + pqtn.getPriority() +
          ", Index: " + pqtn.getIndex() +
          ", QueueIndex: " + pqtn.getQueueIndex());
    }

    System.out.println("Sorted Order:");
    while (pq.size() > 0)
    {
      TestNode pqtn = (TestNode) pq.poll();
      System.out.println("Priority: " + pqtn.getPriority() +
                         ", Index: " + pqtn.getIndex() +
                         ", QueueIndex: " + pqtn.getQueueIndex());
    }
  }
}
