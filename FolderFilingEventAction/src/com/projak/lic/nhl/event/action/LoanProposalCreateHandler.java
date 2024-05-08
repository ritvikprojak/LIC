package com.projak.lic.nhl.event.action;

import java.util.Iterator;

import com.filenet.api.collection.FolderSet;
import com.filenet.api.collection.ReferentialContainmentRelationshipSet;
import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenet.api.engine.EventActionHandler;
import com.filenet.api.events.ObjectChangeEvent;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.util.Id;

public class LoanProposalCreateHandler implements EventActionHandler{

	@Override
	public void onEvent(ObjectChangeEvent arg0, Id arg1) throws EngineRuntimeException {
		System.out.println("LoanProposalCreateHandler.onEvent.entry()");
		
		
		try {
			
			Document doc = (Document) arg0.get_SourceObject();
			
			ObjectStore os = arg0.getObjectStore();
			
			System.out.println("Fetching Object Store");
			os.refresh();
			
			System.out.println("Fetching Document Object");
			
			doc.refresh();
			
			ReferentialContainmentRelationshipSet set = doc.get_Containers();
			
			System.out.println(set.isEmpty());
			
			System.out.println("Container is empty - " + set.isEmpty());
			
			System.out.println("Folder filed in - " + doc.get_FoldersFiledIn().isEmpty());
			
			Iterator<ReferentialContainmentRelationship> foldItr = set.iterator();
			
			while(foldItr.hasNext()) {
				
				System.out.println("Inside While loop - " + arg0.get_ClassDescription().getClassName());
				
				ReferentialContainmentRelationship rcrf = foldItr.next();
				
				Folder fold = (Folder) rcrf.get_Head();
				
				String path = fold.get_PathName();
				
				System.out.println("Path - " + path);
				
				String office = doc.getProperties().getStringValue("Office");
				
				System.out.println("Office - "+ office);
				
				String applicationNumber = doc.getProperties().getStringValue("ApplNumber");
				
				System.out.println("ApplicationNumber - "+ applicationNumber);
				
				Folder officeFolder = null;
				
				Folder applicationNumberFolder = null;
				
				try {
					
					officeFolder = Factory.Folder.fetchInstance(os, path + "/" + office, null);
					
					System.out.println("Office Folder Found");
					
				}catch(EngineRuntimeException e) {
					
					System.out.println(e.getExceptionCode());
					
					System.out.println(e.getExceptionCode().getDefaultEnglishString());
					
					System.out.println("Office Folder Not Found, Creating Folder");
					
					officeFolder = Factory.Folder.createInstance(os, "Folder");
					
					officeFolder.set_Parent(fold);
					
					officeFolder.save(RefreshMode.REFRESH);
					
					System.out.println("Office Folder Created");
					
				}
				
				try {
					
					applicationNumberFolder = Factory.Folder.fetchInstance(os, path + "/" + office + "/" + applicationNumber, null);
					
					System.out.println("Application Folder Found");
					
				}catch(EngineRuntimeException e) {
					
					System.out.println(e.getExceptionCode());
					
					System.out.println(e.getExceptionCode().getDefaultEnglishString());
					
					System.out.println("Application Folder Not Found, Creating Folder");
					
					applicationNumberFolder = Factory.Folder.createInstance(os, "Folder");
					
					applicationNumberFolder.set_Parent(officeFolder);
					
					applicationNumberFolder.save(RefreshMode.REFRESH);
					
					System.out.println("Application Folder Created");
					
				}
				
				
				
				try {
					
					ReferentialContainmentRelationship rcr = applicationNumberFolder.file(doc, AutoUniqueName.NOT_AUTO_UNIQUE, doc.get_Id().toString(), DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);
					
					System.out.println("Filing Document to application Folder");
					
					rcr.save(RefreshMode.NO_REFRESH);
					
					System.out.println("Document Filed in application Folder");
					
					
					rcrf.delete();
					
					rcrf.save(RefreshMode.NO_REFRESH);
					
//					
//					fold.unfile(doc).save(RefreshMode.NO_REFRESH);
//					
					System.out.println("Document UnFiled from Folder - " + path);
					
				}catch(Exception e) {
					
					System.out.println(e.fillInStackTrace());
					
					System.out.println(e.getLocalizedMessage());
					
				}
				
				
				
			}
			
		}catch(Exception e) {
			
			System.out.println(e.getLocalizedMessage());
			
			System.out.println(e.fillInStackTrace());
			
			System.out.println(e);
			
		}
		
		System.out.println("LoanProposalCreateHandler.onEvent.exit()");
	}

}
