package pers.rdara.parets.demo.tests;

import static org.junit.Assert.assertEquals;
import static pers.rdara.parets.demo.classes.HttpMethod.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import pers.rdara.parets.ParallelRunner;
import pers.rdara.parets.demo.classes.Demo;

/*
This class demonstrates just the retry nature of the tests.
Configuring @ParallelRunner.ParallelThreads(1) meaning, we dont want to run tests in parallel.
 */
@RunWith(ParallelRunner.class)
@ParallelRunner.FailedAttempts(3)
@ParallelRunner.ParallelThreads(1)
public class RetryDemoTest {

	@Test
	public void testRetryGet() {
		assertEquals(Demo.failTwice(GET, 0), "get");
	}

	@Test
	public void testRetryHead() {
		assertEquals(Demo.failTwice(HEAD, 0), "head");
	}

	@Test
	public void testRetryPost() {
		assertEquals(Demo.failTwice(POST, 0), "post");
	}

	@Test
	public void testRetryPut() {
		assertEquals(Demo.failTwice(PUT, 0), "put");
	}

	@Test
	public void testRetryDelete() {
		assertEquals(Demo.failTwice(DELETE, 0), "delete");
	}

	@Test
	public void testRetryConnect() {
		assertEquals(Demo.failTwice(CONNECT, 0), "connect");
	}

	@Test
	public void testRetryOptions() {
		assertEquals(Demo.failTwice(OPTIONS, 0), "options");
	}

	@Test
	public void testRetryTrace() {
		assertEquals(Demo.failTwice(TRACE, 0), "trace");
	}
}
