package com.projak.lic.utility;

import java.util.Iterator;
import java.util.Vector;

import javax.security.auth.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.filenet.api.collection.ObjectStoreSet;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.util.UserContext;

public class CEConnection {

	private static final Logger logger = LoggerFactory.getLogger(CEConnection.class);
	
	private Connection con;
	private Domain dom;
	private String domainName;
	private ObjectStoreSet ost;
	private Vector osnames;
	private boolean isConnected;
	private UserContext uc;
	static String objectStore;	
	/*
	 * constructor
	 */
	public CEConnection()
	{
		con = null;
		uc = UserContext.get();
		dom = null;
		domainName = null;
		ost = null;
		osnames = new Vector();
		isConnected = false;
	}
	
	/*
	 * Establishes connection with Content Engine using
	 * supplied username, password, JAAS stanza and CE Uri.
	 */
	public void establishConnection(String userName, String password, String stanza, String uri)
    {
		logger.info("Inside establish connection method of CEConnection class");
		logger.info("userName 1 "+ userName+" password 2 "+ password+" stanza 3 "+ stanza+ " uri 4 "+ uri);
		try {
        con = Factory.Connection.getConnection(uri);
        logger.info("connection is :"+con);
		}
		catch(Exception e) {
			logger.error("Exception caught in establishConnection method"+ e);
		}
        Subject sub = UserContext.createSubject(con,userName,password,stanza);
        logger.info( "subject is "+sub);
        uc.pushSubject(sub);
        dom = fetchDomain();
        logger.info("Domain is :"+dom);
        domainName = dom.get_Name();
        logger.info("domainName is :"+domainName);
        ost = getOSSet();
        logger.info("ost is :"+ost);
        isConnected = true;
        
        objectStore = PropertyReader.getProperty("objectStore");
        logger.info("objectStore is :"+ objectStore);
        ObjectStore os= Factory.ObjectStore.fetchInstance(dom, objectStore, null);
        
        //AddUserToRole.Rolein(con, os, new Id("{49A84894-54A9-479A-863F-06238E853C87}"));
        //AddUserToRole.Roleout(con, os, new Id("{49A84894-54A9-479A-863F-06238E853C87}"));
    }

	/*
	 * Returns Domain object.
	 */
	public Domain fetchDomain()
    {
        dom = Factory.Domain.fetchInstance(con, null, null);
        return dom;
    }
    
    /*
     * Returns ObjectStoreSet from Domain
     */
	public ObjectStoreSet getOSSet()
    {
        ost = dom.get_ObjectStores();
        return ost;
    }
    
    /*
     * Returns vector containing ObjectStore
     * names from object stores available in
     * ObjectStoreSet.
     */
	public Vector getOSNames()
    {
    	if(osnames.isEmpty())
    	{
    		Iterator it = ost.iterator();
    		while(it.hasNext())
    		{
    			ObjectStore os = (ObjectStore) it.next();
    			osnames.add(os.get_DisplayName());
    		}
    	}
        return osnames;
    }

	/*
	 * Checks whether connection has established
	 * with the Content Engine or not.
	 */
	public boolean isConnected() 
	{
		return isConnected;
	}
	
	/*
	 * Returns ObjectStore object for supplied
	 * object store name.
	 */
	public ObjectStore fetchOS(String name)
    {
        ObjectStore os = Factory.ObjectStore.fetchInstance(dom, name, null);
        return os;
    }
	
	/*
	 * Returns the domain name.
	 */
	public String getDomainName()
    {
        return domainName;
    }
}
