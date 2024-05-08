package com.projak.lic.service.impl;

import javax.naming.ldap.LdapContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.filenet.api.collection.CmRoleMemberList;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.security.CmRolePrincipalMember;
import com.filenet.api.security.SecurityPrincipal;
import com.filenet.api.util.Id;
import com.projak.lic.ldap.LdapClient;
import com.projak.lic.service.LDAPService;
import com.projak.lic.utility.CEConnection;
import com.projak.lic.utility.PropertyReader;
import com.projak.lic.utility.RoleComparator;

@Service
public class LDAPServiceImpl implements LDAPService {
   
	private static final Logger logger = LoggerFactory.getLogger(LDAPServiceImpl.class);
   
   static String ldapURL = null;
	static String adminUser = null;
	static String ldapBindPassword = null;
	static String commonDN = null;
	static String trustStore = null;
	static String trustStorePassword = null;
	static String url = null;
	static String user = null;
	static String password = null;
	static String stanza = null;
	static String objectStore = null;
	public void setLdap() {
		
		ldapURL = PropertyReader.getProperty("url");
		adminUser = PropertyReader.getProperty("userId");
		ldapBindPassword = PropertyReader.getProperty("password");
		commonDN = PropertyReader.getProperty("commonDN");
		
	}

	

	@Override
	public String addUserToGroupAndRole(String userName, String groupName, String roleName) throws Exception {
		
		logger.info("Inside addUserToGroupAndRole method of : LdapServiceImpl class");
		 
		url = PropertyReader.getProperty("furl");
		user = PropertyReader.getProperty("user");
		password = PropertyReader.getProperty("password");
		stanza = PropertyReader.getProperty("stanza");
		objectStore = PropertyReader.getProperty("objectStore");
		
		logger.info("Before CE Connection");
		CEConnection ce = new CEConnection();
		logger.info("After CE Connection");
		
		ce.establishConnection(user, password, stanza, url);;
		logger.info("Connection Successful");
		
	   	Domain dom = ce.fetchDomain();
	   	logger.info("dom"+ dom);
	   	ObjectStore os= Factory.ObjectStore.fetchInstance(dom, objectStore, null);
	   	logger.info("objectstore"+ os);
	   	System.out.println(ce.isConnected()+"connection status");
	   	logger.info("connection status"+ ce.isConnected());
   	 
   	 if(ce.isConnected()) {
   		logger.info("Inside if loop to check CE isConnected");
   		 logger.info("userName "+userName+"groupName "+groupName+"roleName "+ roleName);
   		 
				if(!userName.isEmpty()  && groupName.isEmpty()  && !roleName.isEmpty() ) {
					logger.info("inside if loop to check fields are empty or not");
					Id id = null;
					
					String role = roleName;
					try {
						String appRole = PropertyReader.getProperty(role);
						
						logger.info("appRole"+ appRole);
						 id  = new Id(appRole);
					}
					catch(Exception e) {
						logger.error("Role doesnot exists !!!");
						return "role doesn't exist";
					}
					
					Connection con = Factory.Connection.getConnection(url);
					SecurityPrincipal sp = null;
					try {
					 sp= Factory.SecurityPrincipal.fetchInstance(con, userName, null);
					 logger.info("sp"+ sp);
					}
					catch(Exception e) {
						logger.error("User does not Exist");
						return "user Not Exist";
					}
					
					com.filenet.api.security.CmStaticRole rolesStatic = Factory.CmStaticRole.fetchInstance(os,id,null);
					
					logger.info("rolesStatic"+ rolesStatic);
					
					CmRolePrincipalMember rlist= Factory.CmRolePrincipalMember.createInstance();
					
					
					rlist.set_MemberPrincipal(sp);
					CmRoleMemberList s = ((com.filenet.api.security.CmStaticRole) rolesStatic).get_RoleMembers();
					
					String name = rlist.get_MemberPrincipal().getProperties().get("DisplayName").getStringValue();
					RoleComparator comp = new RoleComparator();
//					for(int i=0;i<s.size();i++) {
//						if(comp.check((CmRolePrincipalMember) s.get(i), rlist)!=0) {
//							s.add(rlist);
//						
//							rolesStatic.set_RoleMembers(s);
//							logger.info("Inside if of s.addCheckList");
//							logger.info("s"+ s);
//							logger.info("rolesStatic"+ rolesStatic);
//							
//						}
//						else {
//							CmRolePrincipalMember s1 = (CmRolePrincipalMember) s.get(i);
//							logger.info("user already exist "+ s1.get_MemberPrincipal().getProperties().get("DisplayName").getStringValue());
//						}
//					}
				try {
					logger.info("inside try");
					s.add(rlist);
					rolesStatic.set_RoleMembers(s);
					logger.info("After rolesStatic inside try");
					rolesStatic.save(RefreshMode.REFRESH);
				}
				catch(Exception e) {
					logger.error("user already exist");
					return "user already exist";
				}
	
				return "user added";
			}
				
				else if(!userName.isEmpty() && !groupName.isEmpty() && roleName.isEmpty()) {
					logger.info("inside else if to check fields are empty or not");
					
					try {
					setLdap();
					LdapContext ldapContext = LdapClient.GetLdapContext(ldapURL, adminUser, ldapBindPassword, trustStore,trustStorePassword);
					String addUserToGroup = LdapClient.AddUserToGroup(commonDN, ldapContext, groupName, userName);
					
					if(addUserToGroup == "userAdded") {
						logger.info("User Added Succesfully");
						return "user Added Succesfully";
					}
					else if(addUserToGroup == "groupNotFound") {
						logger.info("group not exist");
						return "group not exist";
					}
					else if(addUserToGroup == "userExistInGroup" ) {
						logger.info("user exist in group");
						return "user Exist in Group";
					}
					else if(addUserToGroup == "userNotExist") {
						logger.info("user not exist");
						return "user not exist";
					}
					else {
						logger.info("user not added");
						return "user not added";
					}
				}
					catch(Exception e) {
						logger.error("Exception"+ e.getLocalizedMessage(), e.fillInStackTrace());
					}
				}
				else {
					logger.info("inside else block of addUserToRole");
					return "please enter values for userName or roleName or groupName";
				}
   	 }
   	 else {
   		 logger.info("please check your filenet connection");
   	 }
		return stanza;
		
	}



	@Override
	public String removeUserFromRole(String userName, String groupName, String roleName) {
		logger.info("Inside removeUserFromRole method of Ldap ServiceImpl");
		
		url = PropertyReader.getProperty("furl");
		user = PropertyReader.getProperty("user");
		password = PropertyReader.getProperty("password");
		stanza = PropertyReader.getProperty("stanza");
		objectStore = PropertyReader.getProperty("objectStore");
		
		logger.info("Before CE COnnection");
		CEConnection ce = new CEConnection();
		logger.info("After CE Connection");
		ce.establishConnection(user, password, stanza, url);
		logger.info("Connection to CE Successful");
		
		Domain dom = ce.fetchDomain();
		logger.info("domain is :"+ dom);
	  	ObjectStore os= Factory.ObjectStore.fetchInstance(dom, objectStore, null);
	  	logger.info("connection status is:"+ ce.isConnected());
  	 if(ce.isConnected()) {
  		logger.info("Inside ce.isConnected to check connection");
  		logger.info("userName "+userName+"groupName "+groupName+"roleName "+ roleName);
		if(!userName.isEmpty()  && groupName.isEmpty()  && !roleName.isEmpty() ) {
			Id id = null;
			
			String role = roleName;
			try {
				String appRole = PropertyReader.getProperty(role);
				logger.info("roleName"+ appRole);
				 id  = new Id(appRole);
				 logger.info("id"+ id);
			}
			catch(Exception e) {
				logger.error("role doesn't Exist");
				return "role doesn't exist";
			}
			Connection con = Factory.Connection.getConnection(url);
			
			SecurityPrincipal sp = null;
			try {
			 sp= Factory.SecurityPrincipal.fetchInstance(con, userName, null);
			 logger.info("sp"+ sp);
			}
			catch(Exception e) {
				logger.error("userNotExist");
				return "user Not Exist";
			}
			
			com.filenet.api.security.CmStaticRole rolesStatic = Factory.CmStaticRole.fetchInstance(os,id,null);
			
			CmRolePrincipalMember rlist= Factory.CmRolePrincipalMember.createInstance();
		
			
			rlist.set_MemberPrincipal(sp);
			CmRoleMemberList s =  rolesStatic.get_RoleMembers();
			
			
			String m2=rlist.get_MemberPrincipal().getProperties().get("DisplayName").getStringValue();
			
			Boolean found=false;

			for(int i=0;i< s.size();i++) {
				String m1 =   ((com.filenet.api.security.CmRolePrincipalMember) s.get(i)).get_MemberPrincipal().getProperties().get("DisplayName").getStringValue();
				logger.info(m1+ " main role inside for loop");
				logger.info(m2+ "rlist inside for loop");
				if(m1.equalsIgnoreCase(m2)) {
					logger.info("Role already exist inside if hence removed");
					s.remove(s.get(i));
					found=true;
					break;
				}
		}

			logger.info(" found after for loop "+found);
		rolesStatic.set_RoleMembers(s);	
		logger.info("After setting size of rlist"+ s.size());
		rolesStatic.save(RefreshMode.REFRESH);
		logger.info("After save");

   		if(found) { 
   			logger.info("role "+ m2+" removed successfully from "+rolesStatic.get_DisplayName());
			return "user removed successfully";
   		}

   		else {
   			logger.info("role "+m2+" not present in "+rolesStatic.get_DisplayName());
			return "role "+m2+" not present in "+rolesStatic.get_DisplayName();
   		}
		
//			RoleComparator comparator = new RoleComparator();
//			for(int i=0;i<s.size();i++)
//			if(comparator.compare((CmRolePrincipalMember) s.get(i), rlist) == 0) {
//				s.remove(s.get(i));
//			}
//			
//			rolesStatic.set_RoleMembers(s);
//			rolesStatic.save(RefreshMode.REFRESH);
//			
//			return "user removed successfully";
		}
		else if(!userName.isEmpty() && !groupName.isEmpty() && roleName.isEmpty() ) {
			logger.info("inside else if to check fiels are empty or not");
			try {

				setLdap();

				LdapContext ldapContext = LdapClient.GetLdapContext(ldapURL, adminUser, ldapBindPassword, trustStore,trustStorePassword);
				String removeUserFromGroup = LdapClient.RemoveUserFromGroup(commonDN, ldapContext, groupName, userName);
				logger.info("remove userFrom Group"+ removeUserFromGroup);
				if(removeUserFromGroup=="userRemoved") {
					logger.info("user removed successfully");
					return "user removed successfully";
				}
				else if(removeUserFromGroup == "userNotExist") {
					logger.info("user does Not Exist");
					return "user Not Exist";
				}
				else if(removeUserFromGroup == "userNotExistInGroup") {
					logger.info("user does not Exist in Group");
					return "user not exist in Group";
				}
				else if(removeUserFromGroup == "userGroupNotExist") {
					logger.info("user or Group doesn't exist");
					return "user or Group doesn't exist";
				}
				else {
					logger.info("user not removed");
					return "user not removed";
				}

			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error"+ e.getLocalizedMessage(), e.fillInStackTrace());
			}
		}
		else {
			logger.info("inside else loop of removeUserFromRole");
			return "please enter values for userName or roleName or groupName";
		}
		
  	 }
	   	else{
			logger.info("Please check your filenet connection");
	   	}
	return stanza;
	}


}