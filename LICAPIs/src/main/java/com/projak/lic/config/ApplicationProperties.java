package com.projak.lic.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;




/**
 * Properties specific to Complaince.
 * <p>
 * Properties are configured in the application.yml file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties
@Configuration
public class ApplicationProperties {
	
	private final Rest rest = new Rest();
	
	
	private final Ldap ldap = new Ldap();
		
	
	public Rest getRest() {
		return rest;
	}
	

	public Ldap getLdap() {
		return ldap;
	}

	public static class Rest {
		private String url = "";
		
		private String  ascEnterer = null;
		
		private String ascAprover = null;
	    
		private String divcodeBranch = null; 
		
		private String divcodeDept = null;
		
		private String divcodeComp = null; 
		
		private String adminUser = null;
		
		
		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getAscEnterer() {
			return ascEnterer;
		}

		public void setAscEnterer(String ascEnterer) {
			this.ascEnterer = ascEnterer;
		}

		public String getAscAprover() {
			return ascAprover;
		}

		public void setAscAprover(String ascAprover) {
			this.ascAprover = ascAprover;
		}

		public String getDivcodeBranch() {
			return divcodeBranch;
		}

		public void setDivcodeBranch(String divcodeBranch) {
			this.divcodeBranch = divcodeBranch;
		}

		public String getDivcodeDept() {
			return divcodeDept;
		}

		public void setDivcodeDept(String divcodeDept) {
			this.divcodeDept = divcodeDept;
		}

		public String getDivcodeComp() {
			return divcodeComp;
		}

		public void setDivcodeComp(String divcodeComp) {
			this.divcodeComp = divcodeComp;
		}

		public String getAdminUser() {
			return adminUser;
		}

		public void setAdminUser(String adminUser) {
			this.adminUser = adminUser;
		}

        
		
		
    }
	
	public static class Ldap {
		
		private String ldapURL;
	    
	    private String ldapUserDNPattern;
	    
	    private String ldapGroupSearchBase;
	    
	    private String ldapGroupSearchFilter;
	    
	    private String ldapGroupRoleAttribute;
	    
	    private String ldapBindDN;
	    
	    private String ldapBindPassword;
	    
	    private String ldapDefaultRole;
	    
	    private String ldapUserSearchFilter;
	    
	    private String ldapUserSearchBase;

		public String getLdapURL() {
			return ldapURL;
		}

		public void setLdapURL(String ldapURL) {
			this.ldapURL = ldapURL;
		}

		public String getLdapUserDNPattern() {
			return ldapUserDNPattern;
		}

		public void setLdapUserDNPattern(String ldapUserDNPattern) {
			this.ldapUserDNPattern = ldapUserDNPattern;
		}

		public String getLdapGroupSearchBase() {
			return ldapGroupSearchBase;
		}

		public void setLdapGroupSearchBase(String ldapGroupSearchBase) {
			this.ldapGroupSearchBase = ldapGroupSearchBase;
		}

		public String getLdapGroupSearchFilter() {
			return ldapGroupSearchFilter;
		}

		public void setLdapGroupSearchFilter(String ldapGroupSearchFilter) {
			this.ldapGroupSearchFilter = ldapGroupSearchFilter;
		}

		public String getLdapGroupRoleAttribute() {
			return ldapGroupRoleAttribute;
		}

		public void setLdapGroupRoleAttribute(String ldapGroupRoleAttribute) {
			this.ldapGroupRoleAttribute = ldapGroupRoleAttribute;
		}

		public String getLdapBindDN() {
			return ldapBindDN;
		}

		public void setLdapBindDN(String ldapBindDN) {
			this.ldapBindDN = ldapBindDN;
		}

		public String getLdapBindPassword() {
			return ldapBindPassword;
		}

		public void setLdapBindPassword(String ldapBindPassword) {
			this.ldapBindPassword = ldapBindPassword;
		}

		public String getLdapDefaultRole() {
			return ldapDefaultRole;
		}

		public void setLdapDefaultRole(String ldapDefaultRole) {
			this.ldapDefaultRole = ldapDefaultRole;
		}

		public String getLdapUserSearchFilter() {
			return ldapUserSearchFilter;
		}

		public void setLdapUserSearchFilter(String ldapUserSearchFilter) {
			this.ldapUserSearchFilter = ldapUserSearchFilter;
		}

		public String getLdapUserSearchBase() {
			return ldapUserSearchBase;
		}

		public void setLdapUserSearchBase(String ldapUserSearchBase) {
			this.ldapUserSearchBase = ldapUserSearchBase;
		}
		
    }
}
