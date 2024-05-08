package com.projak.lic.service;

public interface LDAPService {
	
	String addUserToGroupAndRole(String userName, String groupName, String roleName) throws Exception;

	String removeUserFromRole(String userName, String groupName, String roleName);
}
