require(["dojo/_base/declare",
         "dojo/_base/lang",
         "ecm/widget/layout/CommonActionsHandler",	"ecm/widget/dialog/EmailDialog",
         "ecm/model/Desktop","ecm/model/Action","dojo/when","dojo/Deferred",
		 "ecm/model/Request",
         "dojo/aspect"], 
function(declare, lang,CommonActionsHandler,EmailDialog,Desktop,Action,when,Deferred,Request,aspect) {/*
	
	lang.extend(CommonActionsHandler,{
		
		_showEmailDialog: function(attachments,attachmentType,version) {
    				console.log("inside",CommonActionsHandler);
    				var requestParams = {};
    			    var repo_Id = ecm.model.desktop.getDefaultRepositoryId();
    			    var repo = Desktop.getRepository(repo_Id);
    			    requestParams.repositoryId = repo.id;
    			    Request.invokePluginService("GridxPlugin", "RolePermisssionsService", {
    				requestParams : requestParams,
    				// requestBody : modifiedItems,
    				requestCompleteCallback : lang.hitch(CommonActionsHandler,function(response) {
    					console.log("response",response);
    				  console.log("FN_DMS_Download",response);
    				  console.log("FN_DMS_Download1",Boolean(response.RESPONSE.FN_DMS_Download));
    				  
    				  if(Boolean(response.RESPONSE.FN_DMS_Download))
    						  {
    					  console.log("inside access");
    					  if (CommonActionsHandler._emailDialog)
    						  CommonActionsHandler._emailDialog.destroyRecursive();
    						
    					  CommonActionsHandler._emailDialog = new EmailDialog({
    							attachments: attachments,
    							attachmentType: attachmentType,
    							attachmentVersion: version
    						});
    					  CommonActionsHandler._emailDialog.show();
    						  }
    				  else
    					  { 
    					  console.log("dont have access");

    					  var myDialog = new ecm.widget.dialog.MessageDialog({
    							text : "Access is denied For Email Operaions",
    						    });
    						    myDialog.show();    					  }
    				    	 			  
    			
    			    })
    			    });    		        				   	
		}
		
    });
	
*/});


