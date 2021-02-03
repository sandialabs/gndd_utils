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

package gov.sandia.gmp.util.propertiesplus;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Map.Entry;

import static org.junit.Assert.*;

public class PropertiesPlusTest {
	
	static PropertiesPlus properties;
	
	static PropertiesPlus temp;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		properties = new PropertiesPlus();
		properties.setProperty("stringProperty", "now is the time");
		properties.setProperty("booleanProperty", true);
		properties.setProperty("doubleProperty", 1.234);
		properties.setProperty("floatProperty", 1.234F);
		properties.setProperty("longProperty", 1234L);
		properties.setProperty("intProperty", 1234);
		properties.setProperty("fileWindowsProperty", "C:\\directory\\file.ext");
		properties.setProperty("fileWindowsProperty2", "\\\\crunk.sandia.gov\\directory\\file.ext");
		properties.setProperty("fileUnixProperty", "/nfs/crunk/directory/file.ext");
	}

	@Before
	public void setUp() throws Exception {
		temp = new PropertiesPlus();
		for (Entry entry : properties.entrySet())
			temp.setProperty((String)entry.getKey(), (String)entry.getValue());
	}

	@Test
	public void testGetPropertyString() {
		assert(properties.getProperty("intProperty").equals("1234"));
	}

	@Test
	public void testGetRequestedProperties() {

		assertTrue(temp.getRequestedProperties().isEmpty());
		
		temp.getProperty("non-existent-property");

		assertFalse(temp.getRequestedProperties().containsKey("non-existent-property"));
		
		temp.getProperty("non-existent-property-with-default", "defaultValue");

		assertTrue(temp.getRequestedProperties().containsKey("non-existent-property-with-default"));
		
		temp.getProperty("intProperty");

		assertTrue(temp.getRequestedProperties().containsKey("intProperty"));
		
	}

	@Test
	public void testGetUnRequestedProperties() {
		
		String[] some = new String[] { "stringProperty", "booleanProperty", 
				"doubleProperty", "floatProperty", "longProperty"};

		String[] remaining = new String[] {"intProperty", "fileWindowsProperty", 
				"fileWindowsProperty2", "fileUnixProperty", };
		
		for (String p : some)
			temp.getProperty(p);
		
		ArrayList<String> urp = temp.getUnRequestedProperties();
		
		assertEquals(urp.size(), remaining.length);
		
		for (String p : remaining)
			assertTrue(urp.contains(p));

		for (String p : some)
			assertFalse(urp.contains(p));

	}


}
