package pers.rdara.parets;

import org.junit.Ignore;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.parameterized.BlockJUnit4ClassRunnerWithParameters;
import org.junit.runners.parameterized.TestWithParameters;

public class RetriableBlockJUnit4ClassRunner extends BlockJUnit4ClassRunnerWithParameters implements ParallelInterface {
	public RetriableBlockJUnit4ClassRunner(TestWithParameters test) throws InitializationError {
		super(test);
	}
	int failedAttempts = 1;

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


	@Override
	public int getFailedAttempts() {
		int fa = failedAttempts;
        try {
			fa = getFailedAttempts(this.getTestClass().getJavaClass());
		} catch (InitializationError e) {
			fa = failedAttempts;
		}
        return fa;
	}

}
