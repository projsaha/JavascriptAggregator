package com.ibm.jaggr.web;

import com.ibm.jaggr.core.IServiceReference;

public class ServiceReferenceWeb implements IServiceReference {

	String serviceReference = null;
	
	
	public ServiceReferenceWeb(String serviceRef){
		serviceReference = serviceRef;
	}
	
	@Override
	public Object getPlatformObject(){
		return serviceReference;
		
	}

}
