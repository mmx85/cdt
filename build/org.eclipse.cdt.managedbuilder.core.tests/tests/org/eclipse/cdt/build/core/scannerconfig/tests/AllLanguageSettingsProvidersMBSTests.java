/*******************************************************************************
 * Copyright (c) 2010, 2011 Andrew Gvozdev and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andrew Gvozdev - initial API and implementation
 *******************************************************************************/

package org.eclipse.cdt.build.core.scannerconfig.tests;

import org.eclipse.cdt.build.core.scannerconfig.tests.GCCBuiltinSpecsDetectorTest;

import junit.framework.TestSuite;

public class AllLanguageSettingsProvidersMBSTests extends TestSuite {

	public static TestSuite suite() {
		return new AllLanguageSettingsProvidersMBSTests();
	}

	public AllLanguageSettingsProvidersMBSTests() {
		super(AllLanguageSettingsProvidersMBSTests.class.getName());

		addTestSuite(GCCBuiltinSpecsDetectorTest.class);
	}
}
