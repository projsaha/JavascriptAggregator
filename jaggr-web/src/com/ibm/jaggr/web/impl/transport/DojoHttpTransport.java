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

package com.ibm.jaggr.web.impl.transport;

import java.net.URI;
import java.util.Map;

import com.ibm.jaggr.core.transport.IHttpTransport;

public class DojoHttpTransport extends
		com.ibm.jaggr.core.impl.transport.DojoHttpTransport implements IHttpTransport {
	
	protected URI getFeatureListResourceUri() {		
		return getComboUri().resolve(FEATUREMAP_JS_NAME);
	}
	
	protected String getLoaderExtensionPath(){		
		return getComboUri().getPath() + "loaderExt.js";
	}	
	
	@Override
	public Map<String, Integer> getModuleIdMap(){
		return null;
	}
	
	@Override
	public String getModuleIdRegFunctionName(){
		return null;
	}
}
