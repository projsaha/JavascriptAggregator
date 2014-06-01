package com.ibm.jaggr.web;

import com.ibm.jaggr.core.IServiceRegistration;

public class ServiceRegistrationWeb implements IServiceRegistration {

	String ServiceRegistration = null;
	PlatformServicesImpl platformServices = null;
	
	public ServiceRegistrationWeb(String serviceRegistrationWeb, PlatformServicesImpl platformServicesWeb){
		ServiceRegistration = serviceRegistrationWeb;
		platformServices = platformServicesWeb;
	}
	
	@Override
	public void unregister() {
		platformServices.unRegisterService(ServiceRegistration);
	}

}
