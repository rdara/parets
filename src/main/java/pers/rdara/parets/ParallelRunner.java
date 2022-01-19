package pers.rdara.parets;

import org.junit.Ignore;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/*
 Run tests in Parallel.
 @ParallelRunner.ParallelThreads(16) creates the specified thread pool to run those many tests to tun in parallel. Its optional.
 If ParallelRunner.ParallelThreads isn't specified, then looks for "junit.parallel.threads" system property to determine the size.
 
 @ParallelRunner.WaitTimeForTermination is the maximum  wait time a thread can wait for the test to complete.
 
 */
public class ParallelRunner extends BlockJUnit4ClassRunner implements ParallelInterface {
	int failedAttempts = 1;

    public ParallelRunner(Class klass) throws Throwable
    {
        super(klass);
        setFailedAttempts(getFailedAttempts(klass));
        setScheduler(new ThreadPoolScheduler(getParallelThreadsSize(klass), getWaitTimeForTermination(klass)));
    }
    
	void setFailedAttempts(int fa) {
		failedAttempts = fa;
	}
    
	@Override
	public int getFailedAttempts() {
		return 	failedAttempts;
	}

    @Override
    public void run(final RunNotifier notifier) {
        EachTestNotifier testNotifier = new EachTestNotifier(notifier,
                getDescription());
        Statement statement = classBlock(notifier);
        try {
            statement.evaluate();
        } catch (AssumptionViolatedException e) {
            testNotifier.fireTestIgnored();
        } catch (StoppedByUserException e) {
            throw e;
        } catch (Throwable e) {
            retry(testNotifier, statement, getDescription(), e);
        }
    }



    @Override
    protected void runChild(final FrameworkMethod method, RunNotifier notifier) {
        Description description = describeChild(method);
        if (method.getAnnotation(Ignore.class) != null) {
            notifier.fireTestIgnored(description);
        } else {
            runTestUnit(methodBlock(method), description, notifier);
        }
    }
}
