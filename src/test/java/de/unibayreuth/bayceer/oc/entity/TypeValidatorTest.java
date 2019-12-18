package de.unibayreuth.bayceer.oc.entity;

import static de.unibayreuth.bayceer.oc.entity.TypeValidator.Type.BOOLEAN;
import static de.unibayreuth.bayceer.oc.entity.TypeValidator.Type.DATE;
import static de.unibayreuth.bayceer.oc.entity.TypeValidator.Type.DOUBLE;
import static de.unibayreuth.bayceer.oc.entity.TypeValidator.Type.FLOAT;
import static de.unibayreuth.bayceer.oc.entity.TypeValidator.Type.LONG;
import static de.unibayreuth.bayceer.oc.entity.TypeValidator.Type.TEXT;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TypeValidatorTest {

	@Test
	public void testText() {
		assertTrue(TypeValidator.isValid("jkasf", TEXT));		
	}
	
	@Test
	public void testBoolean() {
		assertTrue(TypeValidator.isValid("true", BOOLEAN));
		assertTrue(TypeValidator.isValid("false", BOOLEAN));
		assertFalse(TypeValidator.isValid("trues", BOOLEAN));
	}
	
	@Test
	public void testFloat() {		
		assertTrue(TypeValidator.isValid("1.912873", FLOAT));
		assertFalse(TypeValidator.isValid("1.a", FLOAT));
	}
	
	@Test
	public void testLong() {
		assertTrue(TypeValidator.isValid("1", LONG));		
		assertFalse(TypeValidator.isValid("1.a", LONG));
	}
	
	@Test
	public void testDouble() {
		assertTrue(TypeValidator.isValid("1.78", DOUBLE));		
		assertFalse(TypeValidator.isValid("1.a", DOUBLE));
	}
	
	
	@Test
	public void testDate() {
		assertTrue(TypeValidator.isValid("2019/10/10", DATE));		
		assertFalse(TypeValidator.isValid("1.a", DATE));
	}
	
	@Test
	public void testDateTime() {
		assertTrue(TypeValidator.isValid("2019/10/10 12:00:00", DATE));		
		assertFalse(TypeValidator.isValid("1.a", DATE));
	}
	
}
