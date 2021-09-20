# Parets

Parets, Parallel and Retriable/Repeatable Tests, are Junit tests in a test class.

JUnit 4 unit tests within a test class are executed in sequential order. The test classes can be run in parallel, but not tests within the class.

Tests are executed as an aggregated unit and even if one test fails, entire test suite fails.

It would be nice to run tests in parallel and immediately retry only those tests that failed. 

Parets is a feasible solution, where we can run tests in parallel and retry only those failing tests immediately after their failure. This solution also provisions good control on each test class. 
Parets works well with JUnit's parameterized tests. The annotation based control provides better readability and aesthetics.

Parets work with JUnit 4.

## Usage
The ParallelRunner and/or ParameterizedParallelRunner will run the tests in the test class concurrently. 
For parameterized tests, ParameterizedParallelRunner. Otherwise, use ParallelRunner.

Following configuration options are available to you:

| Configuration                                  | Usage | System Property | Default | Description |
| ---------------------------------------------- | ----- | --------------- | ------- | ----------- |
| @RunWith(ParallelRunner.class)                 |       |                 |         |             |
| @RunWith(ParameterizedParallelRunner.class)   |       |                 |         |             |
| @RunWith(@Parameterized.UseParametersRunnerFactory(RetriableBlockJUnit4ClassRunnerFactory.class))   |       |                 |         |             |
| ---------------------------------------------- | ----- | --------------- | ------- | ----------- |
| ParallelThreads | @ParallelRunner.ParallelThreads(X) | junit.parallel.threads | 8 | How many tests executed in parallel. |
|                 | @ParameterizedParallelRunner.ParallelThreads(X)|            |   |                                      |
| WaitTimeForTermination | @ParallelRunner.WaitTimeForTermination(X)             | junit.waittime.for.termination | 10 | How long to wait before terminating all threads. In minutes. |
|                        | @ParameterizedParallelRunner.WaitTimeForTermination(X) |                               |    |                                                              |
| FailedAttempts         | @ParallelRunner.FailedAttempts(X)                      | junit.failed.attempts | 1 | How many times a test to be repeated. |
|                        | @ParameterizedParallelRunner.FailedAttempts(X)         |                       |   | 0 means, dont repeat.                 |
|                        |                                                        |                       |   | 1 means, repeat the failed test once. |     
|                        |                                                        |                       |   | 5 means, repeat the failed test 5 times.|

## Configuration Priority Order

1. User configured value
User configured value takes precedence over system property and default value. 
If user annotates, @ParallelRunner.ParallelThreads(4) , then maximum of 4 tests will be executed in parallel.

2. System Property
If user configured value not found and system property is defined, then that system property will be used. If system property, junit.failed.attempts is defined as 2, then a failed test will be repeated twice.

3. Default Value
If neither test class is annotated with configured value nor a system property id defined, then the default value will be executed.

## Demo class

The **Demo** class has a couple of methods to demonstrate parallism and repeatability of tests.

**delayedHttpCall** method will take 5 seconds and returns the lower case of HttpMethod like GET, HEAD,...
**failTwice** method will take 2 seconds. Will fail twice. Will succeed thrird time. Thus takes 6 seconds and 3 retries in total to succeed.

## Demo Tests

The **ParallelDemoTest** demonstrates how all the tests in a test class can be run in parallel.

The **RetryDemoTest** demonstrates how all the tests in a test class are retriable. Even though, we can also run those tests in parallel, this demo class shows how to run them in sequential by setting the number of parallel threads as 1.

The **ParameterizedParallelDemoTest** demonstrates, parameterization, parallism and retry nature all at once.

The **ParameterizedParallelDemoTest** has 2 Tests, 

**testParallel** and 
**testParallelAndRetry**

Each test is parameterized with 8 HttpMethods and hence making them as 16 tests in total.

**testParallelAndRetry** Executes each test 3 times because of **failTwice** of **Demo.class**. Hence, total of 8 * 3 = 24 tests.

So just the following 2 tests,
```
// Will run all 8 tests with GET, HEAD, ...4 at a time, because of ParameterizedParallelRunner.ParallelThreads(4)
@Test
public void testParallel() {
    assertEquals(Demo.delayedHttpCall(httpMethod), expectedResult);
}

@Test
public void testParallelAndRetry() {
    assertEquals(Demo.failTwice(httpMethod, 2), expectedResult);
}
```
is equivalent of 8 + 24 = 32 tests!

With the **@ParameterizedParallelRunner.ParallelThreads(4)** all these 32 tests executes parallel 4 at a time  and completes in
```
30 seconds
```
in stead of 
```
90 seconds
```
when run in sequential order.

And, we can add more parallel threads with **@ParameterizedParallelRunner.ParallelThreads(..)** to increase parallism and hence performance.

