package com.ibm.lichfl.nl.event.action;

import com.filenet.api.constants.AccessType;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.engine.EventActionHandler;
import com.filenet.api.events.ObjectChangeEvent;
import com.filenet.api.events.UpdateEvent;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.security.AccessPermission;
import com.filenet.api.util.Id;

public class CAUpdateEventAction implements EventActionHandler {

	@Override
	public void onEvent(ObjectChangeEvent arg0, Id arg1) throws EngineRuntimeException {

		try {

			UpdateEvent upEA = (UpdateEvent) arg0;

			System.out.println(
					" ====================================================================UpdateEvent=========================================================");

			upEA.get_ModifiedProperties().forEach(element -> System.out.println("Modified Properties : " + element));

			Document doc = (Document) arg0.get_SourceObject();

			doc.refresh();

			String _public = null;
			
			String _confidential = null;

			try {

				_public = doc.getProperties().getStringValue("PublishtoOtherDepartment");

			} catch (Exception ex) {

			}

			System.out.println("Category - " + _public);
			
			try {

				_confidential = doc.getProperties().getStringValue("Confidential");

			} catch (Exception ex) {

			}

			System.out.println("Confidential - " + _confidential);

			if ((_public != null && _public.equalsIgnoreCase("Yes")) || (_confidential != null && _confidential.equalsIgnoreCase("Yes"))) {

				if (doc.get_SecurityFolder() != null) {

					doc.set_SecurityFolder(null);

					System.out.println("Setting Security Folder...");
					
					AccessPermission acc = Factory.AccessPermission.createInstance();

					acc.set_GranteeName("#AUTHENTICATED-USERS");

					acc.set_AccessMask(131201);

					acc.set_AccessType(AccessType.ALLOW);

					doc.get_Permissions().add(acc);

					System.out.println("#AUTHENTICATED-USERS is added to document permission");

					doc.save(RefreshMode.NO_REFRESH);
					
					

				}

			}

		} catch (Exception ex) {
			
			System.out.println(ex.fillInStackTrace());

		}

	}

}
