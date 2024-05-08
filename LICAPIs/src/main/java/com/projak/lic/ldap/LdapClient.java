package com.projak.lic.ldap;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.projak.lic.utility.PropertyReader;

public class LdapClient {
	
	private static String userObjectClass = "(objectclass=user)";
	private static String groupObjectClass = "(objectclass=group)";
	private static String ldapPassword= null;
	static LdapContext ctx = null;
	
	private static final Logger logger = LoggerFactory.getLogger(LdapClient.class);

	
	public static String AddUserToGroup(String commonDN, LdapContext ctx, String groupName, String userName)
			throws Exception {
		//Boolean userAdded = false;
		try {
			logger.info("Inside AddUserToGroup method of LdapClient class");
			ModificationItem[] mods = new ModificationItem[1];
			Attribute mod0 = new BasicAttribute("member", GetUserDistinguishedName(commonDN, userName, ctx));
			logger.info("mod0"+ mod0);
			if(GetUserDistinguishedName(commonDN, userName, ctx).isEmpty()) {
				logger.info("user not exist");
				return "userNotExist";
			}
			mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, mod0);
			
			ctx.modifyAttributes(GetGroupDistinguishedName(commonDN, groupName, ctx), mods);
			
			
			//userAdded = true;
		} catch (NameAlreadyBoundException nameEx) 
		{
		logger.error(userName + " exists in " + groupName);
		return "userExistInGroup";
		} catch (NamingException ex) {
			logger.error("Error: " + ex.fillInStackTrace());
			ex.printStackTrace();
			return "groupNotFound";
			
		}
		
		finally {
			ctx.close();
		}
		return "userAdded";
	}
	
	public static String RemoveUserFromGroup(String commonDN, LdapContext ctx, String groupName, String userName)
			throws Exception {
		//Boolean userRemoved = false;
		Attribute mod0= null;
		try {
			logger.info("Inside RemoveUserFromGroup method of LdapClient class");
			ModificationItem[] mods = new ModificationItem[1];
			mod0 = new BasicAttribute("member", GetUserDistinguishedName(commonDN, userName, ctx));
			logger.info("mod0"+ mod0);
			if(GetUserDistinguishedName(commonDN, userName, ctx).isEmpty()) {
				logger.info("user not exist");
				return "userNotExist";
			}
			mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, mod0);
			ctx.modifyAttributes(GetGroupDistinguishedName(commonDN, groupName, ctx), mods);
			//userRemoved = true;
		} catch (OperationNotSupportedException opEx) {
			logger.error(userName + " not exists in " + groupName);
			return "userNotExistInGroup";
		} catch (Exception e) {
			logger.error("Error: " + e.fillInStackTrace());
			return "userGroupNotExist";
		} finally {
			ctx.close();
		}
		return "userRemoved";
	}
	
	
	static String GetUserDistinguishedName(String commonDN, String userName, LdapContext ctx) throws Exception {
		String userDistinguishedName = "";
		try {
			logger.info("Inside GetUserDistinguishedName method of LdapClient class");
			NamingEnumeration<SearchResult> answer = ctx.search(commonDN,
					"(&" + userObjectClass + "(sAMAccountName=" + userName + "))",
					SearchQueries.GetDistinguishedName());
			if (answer.hasMore()) {
				Attributes attrs = answer.next().getAttributes();
				userDistinguishedName = GetUserNameValue(attrs);
			} else {
				logger.info("User Name '" + userName + "' not found.");
			}
		} catch (NameAlreadyBoundException ex) {
			logger.error("User Name '" + userName + "'s is NameAlreadyBound.");
			logger.info("user NAme already Bound");
		} catch (Exception ex) {
			logger.error("Error:" + ex.fillInStackTrace());
			logger.info("userName Not Found");
			ex.printStackTrace();
		}
		return userDistinguishedName;
	}
	
	private static String GetGroupDistinguishedName(String commonDN, String groupName, LdapContext ctx)
			throws Exception {
		String groupDistinguishedName = "";
		try {
			logger.info("Inside GetGroupDistinguishedName method of LdapClient class");
			NamingEnumeration<SearchResult> answer = ctx.search(commonDN,
					"(&" + groupObjectClass + "(CN=" + groupName + "))", SearchQueries.GetDistinguishedName());
			if (answer.hasMore()) {
				Attributes attrs = answer.next().getAttributes();
				groupDistinguishedName = GetUserNameValue(attrs);
			} else {
				logger.error("Group Name '" + groupName + "' not found.");
				
				throw new Exception("Group Name '" + groupName + "' not found.");
			}
		} catch (Exception ex) {
			logger.error("Error:" + ex.fillInStackTrace());
			throw ex;
		}
		return groupDistinguishedName;
	}
	private static String GetUserNameValue(Attributes attrs) throws Exception {
		String userName = "";
		if (attrs != null) {
			try {
				logger.info("Inside GetUserNameValue method of LdapClient class");
				for (NamingEnumeration<?> ae = attrs.getAll(); ae.hasMore();) {
					Attribute attr = (Attribute) ae.next();
					for (NamingEnumeration<?> e = attr.getAll(); e.hasMore();) {
						userName = (String) e.next();
						logger.info("User Name is :"+userName);
					}
				}
			} catch (Exception ex) {
				logger.error("exception caught in GetUserNameValue method of LdapClient class"+ex.fillInStackTrace());
				throw ex;
			}
		}
		return userName;
	}

	public static LdapContext GetLdapContext(String ldapURL, String adminUser, String ldapBindPassword,
			String trustStore, String trustStorePassword) {

		ldapPassword = PropertyReader.getProperty("ldapPassword");
		LdapContext ctx = null;
		try {
			logger.info("Inside GetLdapContext method of LdapClient class");
			Hashtable<String, String> env = new Hashtable<String, String>();
			String principalName = adminUser;
			//String password = "filenet";
			env.put(Context.PROVIDER_URL, ldapURL);
			env.put(Context.SECURITY_PRINCIPAL, principalName);
			env.put(Context.SECURITY_CREDENTIALS, ldapPassword);
			env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			env.put(Context.SECURITY_AUTHENTICATION, "Simple");
			//env.put("javax.net.ssl.trustStore", trustStore);
			//env.put("javax.net.ssl.trustStorePassword", trustStorePassword);
			//System.setProperty("javax.net.ssl.trustStore", trustStore);
			//System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
			ctx = new InitialLdapContext(env, null);
		} catch (Exception ex) {
			logger.error("Error in ldap Context: " + ex.fillInStackTrace());
			//throw ex;
		}
		return ctx;
	}

	public String getAttribute(String property, String userId) {
		// TODO Auto-generated method stub
		return null;
	}

}
