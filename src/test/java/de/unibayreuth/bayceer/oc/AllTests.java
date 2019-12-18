package de.unibayreuth.bayceer.oc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.unibayreuth.bayceer.oc.controller.DocumentControllerApplicationTests;
import de.unibayreuth.bayceer.oc.controller.FieldControllerApplicationTests;
import de.unibayreuth.bayceer.oc.controller.ImageControllerApplicationTests;
import de.unibayreuth.bayceer.oc.controller.SearchControllerApplicationTests;

@RunWith(Suite.class)
@SuiteClasses({
	DocumentControllerApplicationTests.class,
	FieldControllerApplicationTests.class,
	ImageControllerApplicationTests.class,
	SearchControllerApplicationTests.class
})
public class ControllerIntegrationTestSuite {

}
