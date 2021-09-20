package pers.rdara.parets.demo.tests;

import static org.junit.Assert.*;
import static pers.rdara.parets.demo.classes.HttpMethod.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import pers.rdara.parets.demo.classes.Demo;
import pers.rdara.parets.demo.classes.HttpMethod;
import pers.rdara.parets.ParameterizedParallelRunner;
import pers.rdara.parets.RetriableBlockJUnit4ClassRunnerFactory;

@RunWith(ParameterizedParallelRunner.class)
@ParameterizedParallelRunner.ParallelThreads(4)
@ParameterizedParallelRunner.WaitTimeForTermination(5)
@ParameterizedParallelRunner.FailedAttempts(2)
@Parameterized.UseParametersRunnerFactory(RetriableBlockJUnit4ClassRunnerFactory.class)
public class ParameterizedParallelDemoTest {
	private HttpMethod httpMethod;
	private String expectedResult;

	public ParameterizedParallelDemoTest(HttpMethod method, String result) {
		this.httpMethod = method;
		this.expectedResult = result;
	}

	@Parameterized.Parameters(name="{0}_{1}")
	public static Collection<Object[]> configs() {
		return Arrays.asList(new Object[][] {
				{GET, "get"},
				{HEAD, "head"},
				{POST, "post"},
				{PUT, "put"},
				{DELETE, "delete"},
				{CONNECT,"connect"},
				{OPTIONS,"options"},
				{TRACE,"trace"}
		});
	}

	// Will run all 8 tests with GET, HEAD, ...4 at a time, because of ParameterizedParallelRunner.ParallelThreads(4)
	@Test
	public void testParallel() {
		assertEquals(Demo.delayedHttpCall(httpMethod), expectedResult);
	}

	// Will run all 8 tests with GET, HEAD, ...4 at a time, because of ParameterizedParallelRunner.ParallelThreads(4)
	// AND each test repeats 3 times. because of @ParameterizedParallelRunner.FailedAttempts(2)
	// The failTwice fails 2 times, and will succeed 3rd time.
	// So, 8 (parameterization) * 3 (retries) = 24 tests. And 4 at time will run concurrently.
	// So, if all the 24 tests run sequential, then it will be 48 seconds, but now it will take 12 seconds.
	@Test
	public void testParallelAndRetry() {
		assertEquals(Demo.failTwice(httpMethod, 2), expectedResult);
	}

}
