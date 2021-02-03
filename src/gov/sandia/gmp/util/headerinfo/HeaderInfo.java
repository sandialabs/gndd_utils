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

package gov.sandia.gmp.util.headerinfo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class HeaderInfo
{

	ArrayList<String> header;

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

		try
		{
			new HeaderInfo().run(args);
			System.out.println("Done.");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	protected void run(String[] args) throws IOException
	{
		header = getHeader();
		for (String arg : args)
			replaceHeader(new File(arg));
	}

	private void replaceHeader(File dir) throws IOException
	{
		System.out.println("\nProcessing directory "+dir.getCanonicalPath());
		for (File f : dir.listFiles())
			if (f.isDirectory())
				replaceHeader(f);
			else if (f.isFile())
			{
				if (f.getName().endsWith(".java"))
					replace(f, "package ");
				else if (f.getName().endsWith(".c"))
					replace(f, "#include ");
				else if (f.getName().endsWith(".cc"))
					replace(f, "#ifndef ", "#include ");
				else if (f.getName().endsWith(".h"))
					replace(f, "#ifndef ", "#include ");
			}

	}

	private void replace(File f, String ... triggers) throws IOException
	{
		ArrayList<String> contents = new ArrayList<String>(1000);
		Scanner input = new Scanner(f);
		String line;
		boolean found = false;
		int linenum = 1;
		int nlines = 0;
		while (input.hasNext())
		{
			line = input.nextLine();
			if (!found)
				for (int j=0; j<triggers.length; ++j)
					if (line.startsWith(triggers[j]))
						found = true;

			if (found)
				contents.add(line);
			else
				++linenum;

			++nlines;
		}

		input.close();

		if (!found)
		System.out.printf("%6d / %6d  %s%n", linenum, nlines, f.getName());

		BufferedWriter output = new BufferedWriter(new FileWriter(f));
		for (int i=0; i<header.size(); ++i)
		{
			output.write(header.get(i));
			output.newLine();
		}
		for (int i=0; i<contents.size(); ++i)
		{
			output.write(contents.get(i));
			output.newLine();
		}
		output.close();
	}

	protected ArrayList<String> getHeader()
	{
		ArrayList<String> header = new ArrayList<String>();
		header.add("//- ****************************************************************************");
		header.add("//- ");
		header.add("//- Copyright 2009 Sandia Corporation. Under the terms of Contract");
		header.add("//- DE-AC04-94AL85000 with Sandia Corporation, the U.S. Government");
		header.add("//- retains certain rights in this software.");
		header.add("//- ");
		header.add("//- BSD Open Source License.");
		header.add("//- All rights reserved.");
		header.add("//- ");
		header.add("//- Redistribution and use in source and binary forms, with or without");
		header.add("//- modification, are permitted provided that the following conditions are met:");
		header.add("//- ");
		header.add("//-    * Redistributions of source code must retain the above copyright notice,");
		header.add("//-      this list of conditions and the following disclaimer.");
		header.add("//-    * Redistributions in binary form must reproduce the above copyright");
		header.add("//-      notice, this list of conditions and the following disclaimer in the");
		header.add("//-      documentation and/or other materials provided with the distribution.");
		header.add("//-    * Neither the name of Sandia National Laboratories nor the names of its");
		header.add("//-      contributors may be used to endorse or promote products derived from");
		header.add("//-      this software without specific prior written permission.");
		header.add("//- ");
		header.add("//- THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\"");
		header.add("//- AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE");
		header.add("//- IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE");
		header.add("//- ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE");
		header.add("//- LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR");
		header.add("//- CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF");
		header.add("//- SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS");
		header.add("//- INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN");
		header.add("//- CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)");
		header.add("//- ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE");
		header.add("//- POSSIBILITY OF SUCH DAMAGE.");
		header.add("//-");
		header.add("//- ****************************************************************************");
		header.add("");
		return header;
	}
}
