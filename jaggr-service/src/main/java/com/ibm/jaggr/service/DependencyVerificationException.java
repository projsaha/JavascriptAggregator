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

package com.ibm.jaggr.service;

import java.io.IOException;

/**
 * An exception that's thrown by the JavaScript optimizer when
 * it detects that the dependency list of a module has changed from
 * what was used to generate the dependency graph and development
 * mode is enabled
 */
public class DependencyVerificationException extends IOException {
	private static final long serialVersionUID = 824963277533958913L;

	public DependencyVerificationException(String message) {
		super(message);
	}
	
}
