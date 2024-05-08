package com.projak.lic.service;

public interface LicService {
	
	String getEmployeeDetails(String srNo);

	String getEmployeeLocationLocations(String srNo, String filter);

	String getLoanDetails(String number, String filter);

	String getUserDetails(String srNo);

	String getDocList();

}
