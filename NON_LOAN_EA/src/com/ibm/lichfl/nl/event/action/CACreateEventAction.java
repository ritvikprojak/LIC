package com.ibm.lichfl.nl.event.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.security.auth.Subject;

import com.filenet.api.collection.AccessPermissionList;
import com.filenet.api.collection.FolderSet;
import com.filenet.api.collection.IndependentObjectSet;
import com.filenet.api.constants.AccessType;
import com.filenet.api.constants.PermissionSource;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Connection;
import com.filenet.api.core.CustomObject;
import com.filenet.api.core.Document;
import com.filenet.api.core.Domain;
import com.filenet.api.core.DynamicReferentialContainmentRelationship;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenet.api.engine.EventActionHandler;
import com.filenet.api.events.ObjectChangeEvent;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.filenet.api.security.AccessPermission;
import com.filenet.api.util.Id;
import com.filenet.api.util.UserContext;

public class CACreateEventAction implements EventActionHandler {

	@Override
	public void onEvent(ObjectChangeEvent arg0, Id arg1) throws EngineRuntimeException {

		try {

			Document doc = (Document) arg0.get_SourceObject();

			doc.refresh();

			ObjectStore os = doc.getObjectStore();

			String confidential = null;

			try {

				confidential = doc.getProperties().getStringValue("Confidential");

			} catch (Exception ex) {

			}

			System.out.println("Is confidential - " + confidential);

			if (confidential != null && confidential.equalsIgnoreCase("Yes")) {

				CustomObject co = null;

				try {

					String conPath = "/Security Object/" + doc.getProperties().getStringValue("Department")
							+ "/Confidential/Confidential Security Object";

					System.out.println("Path - " + conPath);

					co = Factory.CustomObject.fetchInstance(os, conPath, null);

					System.out.println("Custom Object Fetched");

					doc.getProperties().putValue("SecurityObject", co);

					System.out.println("Doc Property Updated");

				} catch (EngineRuntimeException ex) {

					System.err.println("Exception while fetching or setting confidential security object : ");

					throw ex;

				}

				doc.save(RefreshMode.REFRESH);

				System.out.println("Doc Changes Saved");

			} else {

				String _public = null;

				try {

					_public = doc.getProperties().getStringValue("PublishtoOtherDepartment");

				} catch (Exception ex) {

				}

				System.out.println("Category - " + _public);

				if (_public != null && _public.equalsIgnoreCase("Yes")) {

					try {

						String conPath = "/Security Object/LICHFL";

						System.out.println("Path - " + conPath);

						CustomObject co = Factory.CustomObject.fetchInstance(os, conPath, null);

						System.out.println("Custom Object Fetched");

						doc.getProperties().putValue("SecurityObject", co);

						System.out.println("Doc Property Updated");
						
						doc.save(RefreshMode.NO_REFRESH);

					} catch (EngineRuntimeException ex) {

						System.err.println("Exception while fetching or setting LICHFL security object : ");

						throw ex;

					}
//
//					System.out.println("Security Folder - " + doc.get_SecurityFolder());
//
//					doc.set_SecurityFolder(null);
//
//					System.out.println("Security Folder - " + doc.get_SecurityFolder());
//
//					doc.save(RefreshMode.NO_REFRESH);

					System.out.println("Checking documents rcr - is Empty : " + doc.get_Containers().isEmpty());

					String searchQuery = "Select * from Department where Department = '"
							+ doc.getProperties().getStringValue("Department") + "' and FolderName = '"
							+ doc.get_ClassDescription().get_DisplayName() + "'";

					System.out.println("Query - " + searchQuery);

					SearchSQL searchSQL = new SearchSQL(searchQuery);

					SearchScope scope = new SearchScope(os);

					IndependentObjectSet set = scope.fetchObjects(searchSQL, null, null, new Boolean(true));

					System.out.println("Public Folder fetched - is Empty : " + set.isEmpty());

					Iterator itr = set.iterator();

					while (itr.hasNext()) {

						Folder obj = (Folder) itr.next();

						System.out.println(
								"=============================================Folder=============================================================");

						System.out.println("Public Folder Path : " + obj.get_PathName());
						
						System.out.println(obj.get_Parent().get_Parent().get_ClassDescription().get_DisplayName());

						DynamicReferentialContainmentRelationship drcr = Factory.DynamicReferentialContainmentRelationship
								.createInstance(os, null);

						drcr.set_Head(doc);

						drcr.set_Tail(obj);

						drcr.save(RefreshMode.NO_REFRESH);

						System.out.println("Document filed in Public folder");

					}

				}

			}

		} catch (Exception ex) {
			
			System.out.println("Error in CACreateEventAction");
			
			System.out.println(ex.getLocalizedMessage());

		}

	}

	public static void main(String args[]) {

		Connection conn = Factory.Connection.getConnection("http://10.0.5.226:9080/wsi/FNCEWS40MTOM");

		Subject sub = UserContext.createSubject(conn, "p8admin", "StockHolding@123", "FileNetP8WSI");

		UserContext.get().pushSubject(sub);

		System.out.println("Connection Created");

		Domain dom = Factory.Domain.fetchInstance(conn, "LICHF", null);

		ObjectStore os = Factory.ObjectStore.fetchInstance(dom, "LICHFL", null);
//		
//		CustomObject co = Factory.CustomObject.fetchInstance(os,
//				 "/Security Object/Credit Appraisal/Confidential/Confidential Security Object", null);

//		System.out.println("CustomObject - "+co.get_Name());

		Document doc = Factory.Document.fetchInstance(os, new Id("{E05B937D-0000-C61F-8D01-0774DECB4614}"), null);

		System.out.println(doc.get_SecurityFolder().get_PathName());

		Folder fold = Factory.Folder.fetchInstance(os, "/ClbTeamspaces/2021/12/Public_1638771487014", null);

		FolderSet set = fold.get_SubFolders();

		System.out.println("Set - " + set.isEmpty());
//
//		doc.set_SecurityFolder(fold);
//
//		AccessPermissionList _apl = doc.get_Permissions();
//
//		System.out.println("APL Fetched");
//
//		System.out.println(doc.get_Permissions().size());

//		List<> name = { "p8admin@HFLHO.com", "p8admins@HFLHO.com", "91010@HFLHO.com" };
//		int[] mask = { 998903, 998903, 998903 };
//
//		HashMap<String, Integer> map = new HashMap<String, Integer>();
//
//		map.put("p8admin@HFLHO.com", 998903);
//
//		map.put("p8admins@HFLHO.com", 998903);
//
//		map.put("91001@HFLHO.com", 998903);

//		AccessPermissionList apl = Factory.AccessPermission.createList();

//		Set<String> keys = map.keySet();

//		for (Object object : _apl) {
//
//			AccessPermission acc = (AccessPermission) object;
//			
//			
//			System.out.println(acc.get_GranteeName());
//			
//			
//			System.out.println(PermissionSource.getInstanceFromInt(acc.get_PermissionSource().getValue()));

//			if (map.containsKey(acc.get_GranteeName())) {
//
//				AccessPermission accp = Factory.AccessPermission.createInstance();
//
//				accp.set_GranteeName(acc.get_GranteeName());
//
//				accp.set_AccessType(AccessType.ALLOW);
//
//				accp.set_InheritableDepth(0);
//
//				accp.set_AccessMask(map.get(acc.get_GranteeName()));
//
//				apl.add(accp);
//
//			} else {
//
//				AccessPermission accp = Factory.AccessPermission.createInstance();
//
//				accp.set_GranteeName(acc.get_GranteeName());
//
//				accp.set_InheritableDepth(0);
//
//				accp.set_AccessMask(998903);
//
//				accp.set_AccessType(AccessType.DENY);
//
//				apl.add(accp);
//
//			}

	}

//		for (String str : keys) {
//
//			AccessPermission accp = Factory.AccessPermission.createInstance();
//
//			accp.set_GranteeName(str);
//
//			accp.set_AccessMask(map.get(str));
//
//			accp.set_AccessType(AccessType.ALLOW);
//
//			accp.set_InheritableDepth(0);
//
//			apl.add(accp);
//
//		}

//		doc.set_Permissions(apl);

//		System.out.println(doc.get_Permissions().size());

//		doc.save(RefreshMode.NO_REFRESH);

//		String searchQuery = "Select * from ClbTeamspace";
//
//		SearchSQL searchSQL = new SearchSQL(searchQuery);
//
//		SearchScope scope = new SearchScope(os);
//
//		IndependentObjectSet set = scope.fetchObjects(searchSQL, null, null, new Boolean(true));
//
//		Iterator itr = set.iterator();

//		while (itr.hasNext()) {
//
//			Folder obj = (Folder) itr.next();
//
//			System.out.println(obj.get_PathName());
//
//			Iterator _itr = obj.get_SubFolders().iterator();
//
//			while (_itr.hasNext()) {
//
//				Folder subObj = (Folder) _itr.next();
//
//				if (subObj.get_FolderName().equalsIgnoreCase("Public")) {
//
//					DynamicReferentialContainmentRelationship drcr = Factory.DynamicReferentialContainmentRelationship
//							.createInstance(os, null);
//
//				}
//
//			}
//
//		}

//	}

}
