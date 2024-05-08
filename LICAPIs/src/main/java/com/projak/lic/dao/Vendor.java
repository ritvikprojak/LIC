package com.projak.lic.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table
	public class Vendor {
		
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SR_NO", nullable = false, updatable = false, insertable = false)
		private long SR_NO;
		
		private String VENDOR_ID;
		private String USER_ID;
		private String SUPERVISOR_ID;
		private String TYPE;
		
		
		
public String getTYPE() {
			return TYPE;
		}

		public void setTYPE(String tYPE) {
			TYPE = tYPE;
		}

public Vendor(long SR_NO, String VENDOR_ID, String USER_ID, String SUPERVISOR_ID) {
			
			this.SR_NO = SR_NO;
			this.VENDOR_ID = VENDOR_ID;
			this.USER_ID = USER_ID;
			this.SUPERVISOR_ID = SUPERVISOR_ID;
		}
		
		public Vendor() {
			
		}

		public long getSR_NO() {
			return SR_NO;
		}

		public void setSR_NO(long SR_NO) {
			SR_NO = SR_NO;
		}

		public String getVENDOR_ID() {
			return VENDOR_ID;
		}

		public void setVENDOR_ID(String VENDOR_ID) {
			VENDOR_ID = VENDOR_ID;
		}

		public String getUSER_ID() {
			return USER_ID;
		}

		public void setUSER_ID(String USER_ID) {
			USER_ID = USER_ID;
		}

		public String getSUPERVISOR_ID() {
			return SUPERVISOR_ID;
		}

		public void setSUPERVISOR_ID(String SUPERVISOR_ID) {
			SUPERVISOR_ID = SUPERVISOR_ID;
		}

		@Override
		public String toString() {
			return "Vendor [SR_NO=" + SR_NO +",VENDOR_ID = "+ VENDOR_ID +", USER_ID=" + USER_ID + ", SUPERVISOR_ID=" + SUPERVISOR_ID + "]";
		}
}
		
		
		
//		public long SR_NO;
//		public long VENDOR_NO;
//		public long USER_ID;
//		public String SUPERVISOR_ID;
//		
//
//		public Vendor(long SR_NO, long VENDOR_NO, long USER_ID, String SUPERVISOR_ID) {
//			this.SR_NO = SR_NO;
//			this.VENDOR_NO = VENDOR_NO;
//			this.USER_ID = USER_ID;
//			this.SUPERVISOR_ID = SUPERVISOR_ID;
//		}
//		public Vendor() {
//			
//		}
//		
//		public long getSR_NO() {
//			return SR_NO;
//		}
//		public long getVENDOR_NO() {
//			return VENDOR_NO;
//		}
//		public long getUSER_ID() {
//			return USER_ID;
//		}
//		public String getSUPERVISOR_ID() {
//			return SUPERVISOR_ID;
//		}
//		@Override
//		public String toString() {
//			return "Vendor [SR_NO=" + SR_NO + ", VENDOR_NO=" + VENDOR_NO + ", USER_ID=" + USER_ID + ", SUPERVISOR_ID="
//					+ SUPERVISOR_ID + "]";
//		}
		
		

