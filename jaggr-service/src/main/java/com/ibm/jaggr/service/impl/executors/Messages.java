/*
 * (C) Copyright 2012, IBM Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ibm.jaggr.service.impl.executors;

import com.ibm.jaggr.service.impl.PlatformAggregatorFactory;

public class Messages {
	private static final String BUNDLE_NAME = "com.ibm.jaggr.service.impl.executors.messages"; //$NON-NLS-1$
	public static String ExecutorsImpl_0;
	static {
		
		PlatformAggregatorFactory.INSTANCE.getNLSImpl().initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
