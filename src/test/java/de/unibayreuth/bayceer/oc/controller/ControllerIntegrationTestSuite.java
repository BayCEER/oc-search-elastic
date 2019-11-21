package de.unibayreuth.bayceer.oc.controller;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	DocumentControllerApplicationTests.class,
	FieldControllerApplicationTests.class,
	ImageControllerApplicationTests.class,
	SearchControllerApplicationTests.class
})
public class ControllerIntegrationTestSuite {

}
