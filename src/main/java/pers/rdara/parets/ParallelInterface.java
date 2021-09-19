package pers.rdara.parets;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerScheduler;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/*
 ParallelInterface is more kind of super class that has common definition/implementation
 for both the ParallelRunner and ParameterizedParallelRunner classes.
 As multiple inheritance is not supported by Java, chosen an interface and used newly introduced "default"
 and making this interface to behave more like trait in scala.
 */
public interface ParallelInterface {
	String JUNIT_PARALLEL_THREDS_PROPERTY = "junit.parallel.threads";
	String DEFAULT_JUNIT_PARALLEL_THREDS_PROPERTY = "8";
	String JUNIT_WAITTIME_FOR_TERMINATION = "junit.waittime.for.termination";
	String DEFAULT_JUNIT_WAITTIME_FOR_TERMINATION = "10"; //Minutes
	String JUNIT_FAILED_ATTEMPTS_PROPERTY = "junit.failed.attempts";
	String DEFAULT_JUNIT_FAILED_ATTEMPTS_PROPERTY = "1";
    Logger LOGGER = LoggerFactory.getLogger(ParallelInterface.class);
	
	int failedAttempts = 1;
	
	int getFailedAttempts();
	
	@Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    @interface ParallelThreads {
        /**
         * @return the junit.parallel.threads size
         */
        int value() default 8;
    }
	
	@Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    @interface WaitTimeForTermination {
        /**
         * @return the junit.waittime.for.termination value in minutes
         */
        int value() default 10;
    }

	@Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    @interface FailedAttempts {
        /**
         * @return the junit.failed.attempts size
         */
        int value() default 1;
    }

	//User defined annotation, System property and then default value.
	default int getParallelThreadsSize(Class<?> klass) throws InitializationError {
        ParallelThreads annotationPT = klass.getAnnotation(ParallelThreads.class);
        if (annotationPT == null) {
        	String threads = System.getProperty(JUNIT_PARALLEL_THREDS_PROPERTY, DEFAULT_JUNIT_PARALLEL_THREDS_PROPERTY);
        	return Integer.parseInt(threads);
        } else {
        	return annotationPT.value();
        }
    }
	
	//WaitTime for termination in minutes. User defined annotation, System property and then default value
	default int getWaitTimeForTermination(Class<?> klass) throws InitializationError {
		WaitTimeForTermination annotationWaitTime = klass.getAnnotation(WaitTimeForTermination.class);
        if (annotationWaitTime == null) {
        	String threads = System.getProperty(JUNIT_WAITTIME_FOR_TERMINATION, DEFAULT_JUNIT_WAITTIME_FOR_TERMINATION);
        	return Integer.parseInt(threads);
        } else {
        	return annotationWaitTime.value();
        }
    }
		
	
	//Number of times a failed test should repeat. User defined annotation, System property and then default value
	default int getFailedAttempts(Class<?> klass) throws InitializationError {
		FailedAttempts annotationFailedAttempts = klass.getAnnotation(FailedAttempts.class);
        if (annotationFailedAttempts == null) {
        	String threads = System.getProperty(JUNIT_FAILED_ATTEMPTS_PROPERTY, DEFAULT_JUNIT_FAILED_ATTEMPTS_PROPERTY);
        	return Integer.parseInt(threads);
        } else {
        	return annotationFailedAttempts.value();
        }
    }
	

    //Scheduler that provides a thread pool with configured number of parallel threads
    class ThreadPoolScheduler implements RunnerScheduler
    {
        private ExecutorService executor; 
        private int waitTime = 10;
        
        public ThreadPoolScheduler(int numThreads, int waitTime)
        {
        	this.waitTime = waitTime;
            executor = Executors.newFixedThreadPool(numThreads);
        }
        
        @Override
        public void finished()
        {
            executor.shutdown();
            try
            {
                executor.awaitTermination(waitTime, TimeUnit.MINUTES);
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }
        }

        @Override
        public void schedule(Runnable childStatement)
        {
            executor.submit(childStatement);
        }
    }
    
    /**
     * Runs a {@link Statement} that represents a leaf (aka atomic) test.
     */
    default void runTestUnit(Statement statement, Description description,
            RunNotifier notifier) {
        EachTestNotifier eachNotifier = new EachTestNotifier(notifier, description);
        eachNotifier.fireTestStarted();
        try {
            statement.evaluate();
        } catch (AssumptionViolatedException e) {
            eachNotifier.addFailedAssumption(e);
        } catch (Throwable e) {
            retry(eachNotifier, statement, description, e);
        } finally {
            eachNotifier.fireTestFinished();
        }
    }

    //Retry tests until they are successful or exhausted the configured failed attempts.
    default void retry(EachTestNotifier notifier, Statement statement, Description description, Throwable currentThrowable) {
        Throwable caughtThrowable = currentThrowable;
        
        for (int i = 0; i < getFailedAttempts(); i++) {
            try {
            	LOGGER.info("Repeating (" + (i+1) + ") time(s) for the failed test: " + description.getDisplayName());
                statement.evaluate();
                return;
            } catch (Throwable t) {
                caughtThrowable = t;
            }
        }
        notifier.addFailure(caughtThrowable);
    }
    
}
