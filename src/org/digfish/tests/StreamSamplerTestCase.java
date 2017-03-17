/**
 * 
 */
package org.digfish.tests;

import static org.digfish.TestUtils.exec;
import static org.digfish.TestUtils.maxMemorySize;
import static org.digfish.TestUtils.toMB;
import static org.digfish.TestUtils.totalAllocatedMemory;
import static org.digfish.TestUtils.totalFreeMemory;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.digfish.StreamSampler;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * @author sam
 *
 */
public class StreamSamplerTestCase {

	StreamSampler sampler;

	@Rule
	public TestName testName = new TestName();

	@Rule
	public TestWatcher testWatcher = new TestWatcher() {
		@Override
		protected void starting(final Description description) {
			String methodName = description.getMethodName();
			String className = description.getClassName();
			className = className.substring(className.lastIndexOf('.') + 1);
			System.err.println("--> " + className + " " + methodName + " <--");
		}
	};

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		System.out.println("Maximum available memory at beginning: " + toMB(maxMemorySize()));
		System.out.println("Allocated at start: " + toMB(totalAllocatedMemory()));

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		System.out.println("Total allocated memory at end: " + toMB(totalAllocatedMemory()));
		System.out.println("Available: " + toMB(totalFreeMemory()));
		System.out.println();
	}

	/**
	 * Test method for {@link org.digfish.StreamSampler#sampleIt()}.
	 */
	@Test
	public void testInternalGeneration() {
		sampler = new StreamSampler(5, true, 10);
		String out = sampler.sampleIt();
		assertTrue(out.length() == 5);
	}

	@Test
	public void testString() {
		ByteArrayInputStream in = 
				new ByteArrayInputStream("THEQUICKBROWNFOXJUMPSOVERTHELAZYDOG".getBytes());
		System.setIn(in);
		sampler = new StreamSampler(5);
		String out = sampler.sampleIt();
		System.setIn(System.in);
		assertTrue(out.length() == 5);
	}

	@Test
	public void testStdin() {
		InputStream in = exec("cat GPL-license.txt");
		System.setIn(in);
		sampler = new StreamSampler(10);
		String out = sampler.sampleIt();
		System.setIn(System.in);
	
		assertTrue(out.length() == 10);
	}

	@Test
	public void testURandom() {
		InputStream in = exec("dd if=/dev/urandom count=100 bs=1MB");
		System.setIn(in);
		sampler = new StreamSampler(5);
		String out = sampler.sampleIt();
		System.setIn(System.in);
		assertTrue(out.length() == 5);
	}
	
	@Test
	public void testStreamFromWeb() {
		InputStream in = exec("curl http://constitution.org/usdeclar.txt");
		System.setIn(in);
		sampler = new StreamSampler(5);
		String out = sampler.sampleIt();
		System.setIn(System.in);
		assertTrue(out.length() == 5);
		
	}

}
