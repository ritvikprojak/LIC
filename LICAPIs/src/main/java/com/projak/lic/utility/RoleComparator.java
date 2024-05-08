package com.projak.lic.utility;

import java.util.Comparator;

import com.filenet.api.security.CmRolePrincipalMember;

public class RoleComparator implements Comparator<CmRolePrincipalMember> {

	@Override
	public int compare(CmRolePrincipalMember o1, CmRolePrincipalMember o2) {
		if(o1.get_MemberPrincipal().getProperties().get("DisplayName").getStringValue().equalsIgnoreCase(o2.get_MemberPrincipal().getProperties().get("DisplayName").getStringValue())){
			return 0;
		}else {
			return 1;
		}
	}

	

	public int check(CmRolePrincipalMember m1, CmRolePrincipalMember m2) {
		if(m1.get_MemberPrincipal().getProperties().get("DisplayName").getStringValue().equalsIgnoreCase(m2.get_MemberPrincipal().getProperties().get("DisplayName").getStringValue())){
			return 0;
		}else {
			return 1;
		}
	}
}
