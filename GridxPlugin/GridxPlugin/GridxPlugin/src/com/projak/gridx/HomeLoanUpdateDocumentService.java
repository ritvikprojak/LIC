package com.projak.gridx;


import java.util.Iterator;
import java.util.Map;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.filenet.api.collection.ContentElementList;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.constants.ReservationType;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.property.Properties;
import com.filenet.api.util.Id;
import com.filenet.api.util.UserContext;
import com.ibm.ecm.extension.PluginLogger;
import com.ibm.ecm.extension.PluginResponseUtil;
import com.ibm.ecm.extension.PluginService;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.ecm.json.JSONResponse;

public class HomeLoanUpdateDocumentService extends PluginService {

	/**
	 * Returns the unique identifier for this service.
	 * <p>
	 * <strong>Important:</strong> This identifier is used in URLs so it must
	 * contain only alphanumeric characters.
	 * </p>
	 * 
	 * @return A <code>String</code> that is used to identify the service.
	 */
	public String getId() {
		return "HomeLoanUpdateDocumentService";
	}

	/**
	 * Returns the name of the IBM Content Navigator service that this service
	 * overrides. If this service does not override an IBM Content Navigator
	 * service, this method returns <code>null</code>.
	 * 
	 * @returns The name of the service.
	 */
	public String getOverriddenService() {
		return null;
	}

	public void execute(PluginServiceCallbacks callbacks,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		PluginLogger logger = callbacks.getLogger();

		JSONResponse js = new JSONResponse();
		response.setContentType("application/json");
		String vs = null;
		String repositoryId = request.getParameter("repositoryId");
		String doc_Id = request.getParameter("docId");
		String docName = request.getParameter("parm_part_filename");
		String mimeType = request.getParameter("mimetype");
		logger.logInfo(logger, "HomeLoanUpdateDocumentService",
				"Repository Id is =" + repositoryId);
		logger.logInfo(logger, "HomeLoanUpdateDocumentService",
				"Document Id is  =   " + doc_Id);
		logger.logInfo(logger, "HomeLoanUpdateDocumentService",
				"Mime Type is = " + mimeType);
		logger.logInfo(logger, "HomeLoanUpdateDocumentService",
				"File Name is = " + docName);

		com.ibm.ecm.jaxrs.upload.FormFile uploadFile = callbacks
				.getRequestUploadFormFile();
		logger.logInfo(logger, "HomeLoanUpdateDocumentService",
				"Upload File is = " + uploadFile);
		boolean respondAsHtml = (uploadFile != null ? true : false);
		logger.logInfo(logger, "HomeLoanUpdateDocumentService",
				"respondAsHtml is = " + respondAsHtml);

		Subject subject = callbacks.getP8Subject(repositoryId);
		logger.logInfo(logger, "HomeLoanUpdateDocumentService", "Subject is = "
				+ subject);
		UserContext.get().pushSubject(subject);

		try {
			logger.logInfo(logger, "HomeLoanUpdateDocumentService",
					"Inside Try");
			logger.logInfo(
					logger,
					"HomeLoanUpdateDocumentService",
					"Objectore is = "
							+ callbacks.getP8ObjectStore(repositoryId));
//			com.filenet.api.core.Document doc = Factory.Document.getInstance(
//					callbacks.getP8ObjectStore(repositoryId),
//					ClassNames.DOCUMENT, new Id(doc_Id));
			Document doc = Factory.Document.fetchInstance(callbacks.getP8ObjectStore(repositoryId), new Id(doc_Id), null);
			logger.logInfo(logger, "HomeLoanUpdateDocumentService", "Doc is "
					+ doc);
			
			// checking if document is compound document
//			if (doc.get_CompoundDocumentState().getValue() == 1) {
//				int count = 0;
//				Iterator crIter1 = doc.get_ChildRelationships().iterator();
//				logger.logInfo(logger, "HomeLoanUpdateDocumentService",
//						"Document Child Present - "
//								+ !doc.get_ChildDocuments().isEmpty());
//				while (crIter1.hasNext() == true) {
//					// Increment count.
//					count++;
//					logger.logInfo(logger, "HomeLoanUpdateDocumentService",
//							"Child Count - " + count);
//
//					// Update component relationship.
//					ComponentRelationship cr = (ComponentRelationship) crIter1
//							.next();
//					cr.set_CopyToReservation(Boolean.TRUE);
//					cr.save(RefreshMode.NO_REFRESH);
//				}
//				logger.logInfo(logger, "HomeLoanUpdateDocumentService",
//						"Document childs are set to CopyToReservation");
//			}
			
			doc.checkout(ReservationType.EXCLUSIVE, null, null, null);
			logger.logInfo(logger, "HomeLoanUpdateDocumentService",
					"Doc checked out");
			doc.setUpdateSequenceNumber(null);
			doc.save(RefreshMode.REFRESH);
			System.out.println("Document Checkout changes saved");
			Document reservation = (Document) doc.get_Reservation();

			if (uploadFile == null) {
				logger.logInfo(logger, "HomeLoanUpdateDocumentService",
						"Inside Upload File");
				com.ibm.ecm.jaxrs.action.ActionForm form = callbacks
						.getRequestUploadActionForm();
				logger.logInfo(logger, "HomeLoanUpdateDocumentService",
						"Action form is = " + form);
				if (form != null && form.getMultipartRequestHandler() != null) {
					Map fileElements = form.getMultipartRequestHandler()
							.getFileElements();
					Iterator it = fileElements.values().iterator();
					while (it.hasNext()) {
						uploadFile = (com.ibm.ecm.jaxrs.upload.FormFile) it
								.next();
						if (uploadFile != null)
							break;
					}
				}
			}

			ContentElementList contentList = Factory.ContentElement
					.createList();
			ContentTransfer ct = Factory.ContentTransfer.createInstance();
			ct.setCaptureSource(uploadFile.getInputStream());
			ct.set_RetrievalName(docName);
			ct.set_ContentType(mimeType);
			contentList.add(ct);

			/*
			 * if (uploadFile != null) { is = (FileInputStream)
			 * uploadFile.getInputStream(); contentLen =
			 * uploadFile.getFileSize(); } else { is = request.getInputStream();
			 * contentLen = request.getContentLength(); }
			 */

			reservation.set_ContentElements(contentList);
			reservation.save(RefreshMode.REFRESH);
			reservation.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY,
					CheckinType.MAJOR_VERSION);
			reservation.save(RefreshMode.REFRESH);
			logger.logInfo(logger, "HomeLoanUpdateDocumentService",
					"Document Checked In");
			String docId = reservation.get_Id().toString();
			Properties props = reservation.getProperties();
			props.putValue("DocumentTitle", docName);
			logger.logInfo(logger, "HomeLoanUpdateDocumentService",
					reservation.toString());
			reservation.save(RefreshMode.REFRESH);
			logger.logInfo(logger, "HomeLoanUpdateDocumentService",
					"Updated Checkin properties with Doc Id " + docId);

			com.filenet.api.core.Document vsdoc = Factory.Document
					.fetchInstance(callbacks.getP8ObjectStore(repositoryId),
							new Id(docId), null);
			vs = vsdoc.get_VersionSeries().get_Id().toString();
			logger.logInfo(logger, "HomeLoanUpdateDocumentService",
					"Version Series Id is " + vs);

			js.put("vsId", vs);
			js.put("message", "Success");
			js.put("docId", docId);
			js.put("docName", docName);
			PluginResponseUtil.writeJSONResponse(request, response, js,
					callbacks, "HomeLoanUpdateDocumentService");
		} catch (Exception e) {
			logger.logError(
					logger,
					"HomeLoanUpdateDocumentService",
					"Exception in Update Document Service = "
							+ e.getLocalizedMessage(), e.fillInStackTrace());
			js.put("message", "Fail");
			js.put("errorMessage", e.getMessage());
			PluginResponseUtil.writeJSONResponse(request, response, js,
					callbacks, "HomeLoanUpdateDocumentService");
		}

	}
}
