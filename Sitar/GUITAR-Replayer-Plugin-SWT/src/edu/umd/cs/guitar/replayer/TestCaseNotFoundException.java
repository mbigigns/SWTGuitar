/*	
 *  Copyright (c) 2011-@year@. The GUITAR group at the University of Maryland. Names of owners of this group may
 *  be obtained by sending an e-mail to atif@cs.umd.edu
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 *  documentation files (the "Software"), to deal in the Software without restriction, including without 
 *  limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *	the Software, and to permit persons to whom the Software is furnished to do so, subject to the following 
 *	conditions:
 * 
 *	The above copyright notice and this permission notice shall be included in all copies or substantial 
 *	portions of the Software.
 *
 *	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 *	LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
 *	EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *	IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *	THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
package edu.umd.cs.guitar.replayer;

/**
 * Thrown to indicate a test case was not found by the replayer.
 * 
 * @author Gabe Gorelick
 * @see SitarReplayer#SitarReplayer(SitarReplayerConfiguration)
 */
public class TestCaseNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Construct a new {@code TestCaseNotFoundException} with no detail
	 * message or cause.
	 * 
	 * @see RuntimeException#RuntimeException()
	 */
	// inheritDoc doesn't work for constructors
	public TestCaseNotFoundException() {
		super();
	}

	/**
	 * Construct a new {@code TestCaseNotFoundException} with the given
	 * detail message.
	 * 
	 * @param message
	 *            the detail message
	 * 
	 * @see RuntimeException#RuntimeException(String)
	 */
	public TestCaseNotFoundException(String message) {
		super(message);
	}

	/**
	 * Construct a new {@code TestCaseNotFoundException} with the given
	 * cause.
	 * 
	 * @param cause
	 *            the cause of this exception
	 * @see RuntimeException#RuntimeException(Throwable)
	 */
	public TestCaseNotFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * Construct a new {@code TestCaseNotFoundException} with the given
	 * detail message and cause.
	 * 
	 * @param message
	 *            the detail message
	 * @param cause
	 *            the cause of this exception
	 * 
	 * @see RuntimeException#RuntimeException(String, Throwable)
	 */
	public TestCaseNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
