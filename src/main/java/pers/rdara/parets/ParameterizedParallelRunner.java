package pers.rdara.parets;

import org.junit.runners.Parameterized;

/*
 Run tests in Parallel.
 @ParameterizedParallelRunner.ParallelThreads(16) creates the specified thread pool to run those many tests to run in parallel. Its optional.
 If @ParameterizedParallelRunner.ParallelThreads isn't specified, then looks for "junit.parallel.threads" system property to determine the size. 
 @ParameterizedParallelRunner.WaitTimeForTermination is the maximum  wait time a thread can wait for the test to complete.
 */
public class ParameterizedParallelRunner extends Parameterized implements ParallelInterface {
	int failedAttempts = 1;
	
    public ParameterizedParallelRunner(Class klass) throws Throwable {
        super(klass);
        setFailedAttempts(getFailedAttempts(klass));
        setScheduler(new ThreadPoolScheduler(getParallelThreadsSize(klass), getWaitTimeForTermination(klass)));
    }

	void setFailedAttempts(int failedAttempts) {
		this.failedAttempts = failedAttempts;
	}
    
	@Override
	public int getFailedAttempts() {
    	return 	failedAttempts;
	}
}
