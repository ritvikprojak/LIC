package com.projak.gridx;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.filenet.api.collection.DocumentSet;
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

public class HomeLoanCompoundDocumentService extends PluginService {

	@Override
	public void execute(PluginServiceCallbacks callbacks, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		PluginLogger logger = callbacks.getLogger();
		JSONResponse js = new JSONResponse();
		response.setContentType("application/json");
		
		String repositoryId=request.getParameter("repositoryId");
		String documentName=request.getParameter("documentName");
		String documentId=request.getParameter("documentId");
		
		logger.logInfo(logger, "HomeLoanCompoundDocumentService", "Repository Id is ="+repositoryId);
		logger.logInfo(logger, "HomeLoanCompoundDocumentService", "Document name is  =   "+documentName);
		logger.logInfo(logger, "HomeLoanCompoundDocumentService", "Document Id is  =   "+documentId);
		
		Subject subject = callbacks.getP8Subject(repositoryId);
		logger.logInfo(logger, "HomeLoanCompoundDocumentService", "Subject is = "+subject);
		UserContext.get().pushSubject(subject);
		
		
		
		
		
		try {
			logger.logInfo(logger, "HomeLoanCompoundDocumentService", "Inside Try");
			
	            
	            logger.logInfo(logger, "HomeLoanCompoundDocumentService", "Objectore is = "+callbacks.getP8ObjectStore(repositoryId));
	            com.filenet.api.core.Document doc = Factory.Document.fetchInstance(callbacks.getP8ObjectStore(repositoryId), new Id(documentId),null);
	            logger.logInfo(logger, "HomeLoanCompoundDocumentService", "Document is = "+doc);
	            
	            //getChildDocuments
	            
	            int count=0;
	            
	            
	            DocumentSet parentDocuments = doc.get_ParentDocuments();
	            Iterator i1 = parentDocuments.iterator();
	            while(i1.hasNext()) {
	            	Document p1 = (Document) i1.next();
	            	Properties prop = p1.getProperties();
	            	String title = prop.getStringValue("Name");
	            	logger.logInfo(logger, "HomeLoanCompoundDocumentService", "Document title is = "+title);
	            }
	            
	            
	            DocumentSet cd = doc.get_ChildDocuments();
	            logger.logInfo(logger, "HomeLoanCompoundDocumentService", "CDddddd is = "+ cd );
	            JSONArray jsonArr = new JSONArray();
	            
	            Iterator child = cd.iterator(); 
	            while(child.hasNext()) {
	            	JSONObject jobj=new JSONObject();
	            	Document c1 = (Document) child.next();
					if(c1.get_IsCurrentVersion()){
						Properties properties = c1.getProperties();
						String docName1 = properties.getStringValue("Name");
						Id id = properties.getIdValue("Id");
						jobj.put("docTitle", docName1);
						jobj.put("Id", id);	            	
						jsonArr.put(jobj);
						count++;	
					}
	            }
	            if(count==0) {
	            	
		            	js.put("id", documentId);
		            	js.put("documentName",documentName);
		            
	            }
	            else {
	            	logger.logInfo(logger, "HomeLoanCompoundDocumentService", "Before js.put is = ");
	            	js.put("id", documentId);
	            	js.put("documentName",documentName);
	            	js.put("JSON", jsonArr.toString());
	            	logger.logInfo(logger, "HomeLoanCompoundDocumentService", "After js.put is = ");
	            	////json array to use tomorrow
	            }
	            
	        
	       
			
			js.put("message", "Success");
			PluginResponseUtil.writeJSONResponse(request, response, js, callbacks, "HomeLoanCompoundDocumentService");
		}catch(Exception e) {
			logger.logError(logger, "HomeLoanCompoundDocumentService", "Exception in Update Properties service = "+e.getLocalizedMessage(), e.fillInStackTrace());
			js.put("message", "Fail");
			js.put("errorMessage",e.getMessage());
			PluginResponseUtil.writeJSONResponse(request, response, js, callbacks, "HomeLoanCompoundDocumentService");
		}
		
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return "HomeLoanCompoundDocumentService";
	}
	
	@Override
	public String getOverriddenService() {
		return null;
	}

}
