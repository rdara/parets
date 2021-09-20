package pers.rdara.parets.demo.tests;

import static org.junit.Assert.assertEquals;
import static pers.rdara.parets.demo.classes.HttpMethod.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import pers.rdara.parets.ParallelRunner;
import pers.rdara.parets.demo.classes.Demo;

/*
Demonstrates running all the tests in the test class in parallel, 4 at a time.
 */
@RunWith(ParallelRunner.class)
@ParallelRunner.ParallelThreads(4)
@ParallelRunner.WaitTimeForTermination(5)
public class ParallelDemoTest {

	@Test
	public void testGet() {
		assertEquals(Demo.delayedHttpCall(GET), "get");
	}

	@Test
	public void testHead() {
		assertEquals(Demo.delayedHttpCall(HEAD), "head");
	}

	@Test
	public void testPost() {
		assertEquals(Demo.delayedHttpCall(POST), "post");
	}

	@Test
	public void testPut() {
		assertEquals(Demo.delayedHttpCall(PUT), "put");
	}

	@Test
	public void testDelete() {
		assertEquals(Demo.delayedHttpCall(DELETE), "delete");
	}

	@Test
	public void testConnect() {
		assertEquals(Demo.delayedHttpCall(CONNECT), "connect");
	}

	@Test
	public void testOptions() {
		assertEquals(Demo.delayedHttpCall(OPTIONS), "options");
	}

	@Test
	public void testTrace() {
		assertEquals(Demo.delayedHttpCall(TRACE), "trace");
	}
}
