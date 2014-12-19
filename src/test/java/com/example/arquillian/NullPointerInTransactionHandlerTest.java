package com.example.arquillian;

import java.io.File;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.arquillian.transaction.impl.lifecycle.TransactionHandler;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This test case shows that a NullPointerException will mask any other
 * exception preventing the proper construction of a testcase.
 *
 * The test case is annotated with a ROLLBACK transaction mode in order to keep
 * a database clean during tests. When something triggers an exception during
 * the construction process of the test case, such as a CDI bean that can't be
 * instantiated, the TransactionHandler tries to determine if it should roll
 * back the transaction by retrieving the test result, but that doesn't exist,
 * because the test didn't run, and it throws a NPE.
 * 
 * This case shows two methods: one that provides the reason why this test case
 * can't be created {@link #showsOriginalException()} and one where the original
 * exception is hidden by a NullPointerException in TransactionHandler
 * {@link #exceptionMaskedByNullPointerExceptionInTransactionHandler()}
 * 
 * The NullPointerException in TransactionHandler is in
 * {@link TransactionHandler#testRequiresRollbackDueToFailure()} line 170
 */
@RunWith(Arquillian.class)
public class NullPointerInTransactionHandlerTest {

	/**
	 * Can't be initialized by CDI as the FailingToInitialize bean will throw an
	 * exception in the constructor.
	 */
	@SuppressWarnings("unused")
	@Inject
	private FailingToInitialize notInitalizable;

	/**
	 * Testcase that doesn't hide the original exception with a NPE.
	 */
	@Test
	public void showsOriginalException() {
		Assert.fail("In this test case you should not reach this point");
	}

	/**
	 * Testcase that can't be run due to the initialization error.
	 */
	@Transactional(TransactionMode.ROLLBACK)
	@Test
	public void exceptionMaskedByNullPointerExceptionInTransactionHandler() {
		// when you reach this point, the initialization did not go as expected:
		// it should fail to initialize and Arquillian should provide a proper
		// exception report with the initialization error, not a NPE hiding the
		// failure cause.
		Assert.fail("In this test case you should not reach this point");
	}

	@Deployment
	public static Archive<?> deployment() {
		File[] dependencies = Maven.configureResolver().workOffline()
				.loadPomFromFile("pom.xml")
				.importCompileAndRuntimeDependencies().resolve()
				.withTransitivity().as(File.class);

		WebArchive war = ShrinkWrap
				.create(WebArchive.class)
				.addAsResource(new File("target/classes"), "")
				.addAsResource(new File("target/test-classes"), "")
				.addAsLibraries(dependencies)
				.addAsWebInfResource(
						new File("src/main/webapp/WEB-INF/beans.xml"));
		return war;
	}
}
