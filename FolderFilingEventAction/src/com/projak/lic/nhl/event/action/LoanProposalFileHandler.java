package com.projak.lic.nhl.event.action;

import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;

// [FolderName] = 'Loan Proposal'

import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenet.api.engine.EventActionHandler;
import com.filenet.api.events.ObjectChangeEvent;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.util.Id;

public class LoanProposalFileHandler implements EventActionHandler{

	@Override
	public void onEvent(ObjectChangeEvent arg0, Id arg1) throws EngineRuntimeException {
		
		System.out.println("LoanProposalFileHandler.onEvent.entry()");
		
		ReferentialContainmentRelationship rcr = null;
		
		Document doc = null;
		
		Folder fold = null;
		
		try {
			
			
			rcr = (ReferentialContainmentRelationship) arg0.get_SourceObject();
			
			rcr.refresh();
			
			System.out.println("RCR created");
			
			System.out.println(rcr.get_ContainmentName());
			
			doc = (Document) rcr.get_Head();
			
			fold = (Folder) rcr.get_Tail();
			
			System.out.println("Head , Tail Fetched");
			
		}catch(Exception e) {
			
			System.out.println(e.getLocalizedMessage());
			
//			System.out.println(e.fillInStackTrace());
			
			return;
			
		}
		
		if(fold.get_Name().equalsIgnoreCase("Loan Proposal")) {
			
			System.out.println("Folder found");
			
			ObjectStore os = rcr.getObjectStore();
			
			os.refresh();
			
			String path = fold.get_PathName();
			
			System.out.println("Path - " + path);
			
			String location = doc.getProperties().getStringValue("Office");//changed to office from scanlocation
			
			System.out.println("Office Location - "+ location);
			
			String applicationNumber = doc.getProperties().getStringValue("ApplNumber");
			
			System.out.println("ApplicationNumber - "+ applicationNumber);
			
			Folder locationFolder = null;
			
			Folder applicationNumberFolder = null;
			
			try {
				
				locationFolder = Factory.Folder.fetchInstance(os, path + "/" + location, null);
				
				System.out.println("Office Location Folder Found");
				
			}catch(EngineRuntimeException e) {
				
				System.out.println(e.getExceptionCode());
				
				System.out.println(e.getExceptionCode().getDefaultEnglishString());
				
				System.out.println("Office Folder Not Found, Creating Folder");
				
				locationFolder = Factory.Folder.createInstance(os, "Folder");
				
				System.out.println("Instance Created");
				
				locationFolder.set_FolderName(location);
				
				locationFolder.set_Parent(fold);
				
				System.out.println("Setting parent");
				
				locationFolder.save(RefreshMode.REFRESH);
				
				System.out.println("Office Folder Created");
				

				System.out.println(locationFolder.get_PathName());
				
			}
			
			try {
				
				applicationNumberFolder = Factory.Folder.fetchInstance(os, path + "/" + location + "/" + applicationNumber, null);
				
				System.out.println("Application Folder Found");
				
			}catch(EngineRuntimeException e) {
				
				System.out.println(e.getExceptionCode());
				
				System.out.println(e.getExceptionCode().getDefaultEnglishString());
				
				System.out.println("Application Folder Not Found, Creating Folder");
				
				applicationNumberFolder = Factory.Folder.createInstance(os, "Folder");
				
				System.out.println("Instance Created");
				
				applicationNumberFolder.set_FolderName(applicationNumber);
				
				applicationNumberFolder.set_Parent(locationFolder);
				
				System.out.println("Setting parent");
				
				applicationNumberFolder.save(RefreshMode.REFRESH);
				
				System.out.println("Application Folder Created");
				
				System.out.println(applicationNumberFolder.get_PathName());
				
			}
			
			try {
				
				System.out.println("Changing tail for rcr");
				
				rcr.set_Tail(applicationNumberFolder);
				
				System.out.println("Tail Changed");
				
				rcr.save(RefreshMode.REFRESH);
				
				System.out.println("Saving Changes");
				
//				ReferentialContainmentRelationship rcrf = applicationNumberFolder.file(doc, AutoUniqueName.NOT_AUTO_UNIQUE, doc.get_Id().toString(), DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);
//				
//				System.out.println("Filing Document to application Folder");
//				
//				rcrf.save(RefreshMode.REFRESH);
//				
//				System.out.println("Document Filed in application Folder");
//				
////				rcr.delete();
//				
////				rcr.save(RefreshMode.REFRESH);
//				
////				
//				fold.unfile(doc).save(RefreshMode.NO_REFRESH);
////				
				System.out.println("Document UnFiled from Folder - " + path);
				
			}catch(Exception e) {
				
				System.out.println(e.fillInStackTrace());
				
				System.out.println(e.getLocalizedMessage());
				
			}
			
			
		}
		
		System.out.println("LoanProposalFileHandler.onEvent.exit()");
		
	}

}
