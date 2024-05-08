define(["dojo/_base/declare",
         "dojo/_base/lang",
         "ecm/widget/layout/CommonActionsHandler",
         "ecm/model/Desktop","ecm/model/Action","dojo/when","dojo/Deferred",
		 "ecm/model/Request",
         "dojo/aspect"], 
function(declare, lang,CommonActionsHandler,Desktop,Action,when,Deferred,Request,aspect){
        return declare("HideElements",null,{
    		// Set to true if widget template contains DOJO widgets.
    		hideActions : function() 
    		{
    	    var _self = this;
    			  var deferred = new Deferred();
    			  var actionEnable;    			    
    				console.log("inside");
    				var requestParams = {};
    			    var repo_Id = ecm.model.desktop.getDefaultRepositoryId();
    			    var repo = Desktop.getRepository(repo_Id);
    			    requestParams.repositoryId = repo.id;
    			    Request.invokePluginService("GridxPlugin", "RolePermisssionsService", {
    				requestParams : requestParams,
    				// requestBody : modifiedItems,
    				requestCompleteCallback : lang.hitch(this,function(response) {
    					console.log("response",response);
    				  console.log("FN_DMS_Download",response);
    				  console.log("FN_DMS_Download1",Boolean(response.RESPONSE.FN_DMS_Download));
    				  
    			      lang.extend(Action,{
    			    	  isEnabled: function(repository, listType, items, teamspace, resultSet) {
    			    	  if((this.id == "SendAttachments" || this.id == "SendAttachmentsAll" || this.id == "SendAsPDF" || this.id == "SendAllAsPDF"))    			  
    			    			  {     		 
    			    			    if(Boolean(response.RESPONSE.FN_DMS_Download))
    			  	 			  {
    			    			    	console.log("inside true");
    			   					return this.canPerformAction(repository, items, listType, teamspace, resultSet);
    				 			  }
    			  	 			  else
    			  	 				  {
    			  	 				  console.log("inside false")
    			  					return false;

    			  	 				  }  

    			    			  }
    			    		  else
    			    			  {
    						return this.canPerformAction(repository, items, listType, teamspace, resultSet);
    			    			  }

    					}
    			      });
    				    	 			  
    				})
    			    });
    		        				
    		}
        });
        		
        		
        		
        	});


