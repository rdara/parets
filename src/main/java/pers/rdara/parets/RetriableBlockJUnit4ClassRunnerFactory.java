package pers.rdara.parets;

import org.junit.runner.Runner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.parameterized.ParametersRunnerFactory;
import org.junit.runners.parameterized.TestWithParameters;

public class RetriableBlockJUnit4ClassRunnerFactory implements ParametersRunnerFactory {
	public Runner createRunnerForTestWithParameters(TestWithParameters test) throws InitializationError {
		return new RetriableBlockJUnit4ClassRunner(test);
	}
}
