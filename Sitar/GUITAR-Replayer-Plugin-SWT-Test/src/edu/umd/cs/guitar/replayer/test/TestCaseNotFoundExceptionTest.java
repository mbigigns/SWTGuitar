package edu.umd.cs.guitar.replayer.test;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.umd.cs.guitar.replayer.TestCaseNotFoundException;

public class TestCaseNotFoundExceptionTest {

	@Test
	public void testTestCaseNotFoundException() {
		assertNull(new TestCaseNotFoundException().getCause());
		
		assertEquals("foo", new TestCaseNotFoundException("foo").getMessage());
		
		Exception exception = new Exception();
		assertEquals(exception, new TestCaseNotFoundException(exception).getCause());
		
		TestCaseNotFoundException tc = new TestCaseNotFoundException("foo", exception);
		assertEquals("foo", tc.getMessage());
		assertEquals(exception, tc.getCause());
	}
}
