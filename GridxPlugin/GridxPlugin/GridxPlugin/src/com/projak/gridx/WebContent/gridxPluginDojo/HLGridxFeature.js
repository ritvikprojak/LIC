define(
	[ "dojo", "dojo/_base/declare", "dojo/_base/lang", "dojo/_base/json", "dojo/aspect", "dojo/Deferred", "dojo/store/Memory",  "dojox/grid/enhanced/plugins/IndirectSelection", 
		"dojo/date/locale", "dijit/ConfirmDialog", "dijit/form/Form", 'dijit/form/TextBox', 'dijit/form/Button', 'dijit/form/DateTextBox',
		'dijit/form/Textarea', 'dijit/form/Select', 'dijit/form/FilteringSelect', "dijit/Menu", "dijit/registry", "ecm/model/Request",
		"ecm/model/SearchQuery", "ecm/widget/layout/_LaunchBarPane", "ecm/model/admin/ApplicationConfig", "dojo/data/ItemFileWriteStore",
		"ecm/widget/dialog/AddContentItemDialog", "ecm/widget/listView/ContentList", "ecm/widget/listView/gridModules/RowContextMenu",
		"ecm/widget/listView/modules/Toolbar2", "ecm/widget/listView/modules/DocInfo", "ecm/widget/listView/gridModules/DndRowMoveCopy",
		"ecm/widget/listView/gridModules/DndFromDesktopAddDoc", "ecm/widget/listView/modules/Bar", "ecm/widget/listView/modules/ViewDetail",
		"ecm/widget/listView/modules/ViewMagazine", "ecm/widget/listView/modules/ViewFilmStrip", "ecm/widget/listView/modules/Breadcrumb",
		"ecm/widget/listView/modules/TotalCount", "ecm/model/Desktop", "gridx/core/model/cache/Sync", "gridx/Grid",
		"gridx/modules/CellWidget", "gridx/modules/Edit", "gridx/modules/Filter", "gridx/modules/filter/FilterBar",
		"gridx/modules/Pagination", "gridx/modules/Menu", "gridx/modules/select/Row", "gridx/modules/ColumnResizer",
		"gridx/modules/pagination/PaginationBar", "dojox/encoding/base64", "dojo/text!./templates/HLGridxFeature.html","gridxPluginDojo/GridxPluginAction" ],
	function(dojo, declare, lang, json, aspect, Deferred, Memory, IndirectSelection, locale, ConfirmDialog, Form, TextBox, Button, DateTextBox, Textarea, Select,
		FilteringSelect, Menu, registry, Request, SearchQuery, _LaunchBarPane, ApplicationConfig, ItemFileWriteStore, AddContentItemDialog,
		ContentList, RowContextMenu, Toolbar, DocInfo, DndRowMoveCopy, DndFromDesktopAddDoc, Bar, ViewDetail, ViewMagazine, ViewFilmStrip,
		Breadcrumb, TotalCount, Desktop, Cache, Grid, CellWidget, Edit, Filter, FilterBar, Pagination, Menu, Row, ColumnResizer,
		PaginationBar, base64, template,GridxPluginAction) {
	    /**
	     * @name gridxPluginDojo.HLGridxFeature
	     * @class	
	     * @augments ecm.widget.layout._LaunchBarPane
	     */

	    return declare(
		    "gridxPluginDojo.HLGridxFeature",
		    [ _LaunchBarPane ],
		    {
			/** @lends gridxPluginDojo.HLGridxFeature.prototype */

			templateString : template,
			widgetsInTemplate : false,
			fileType : [],
			statusOfDocument : [],
			natureOfDocument : [],
			loanStatusArray : [],
			baseURL : "",
			userDetails : "",
			docList : "",
			loanDetails : {},
			customerList : [],
			_self : null,
			primaryCustomer : "",
			baseLocation : "",
			_resultSet : "",
			_store : "",
			_grid : "",
			_contentlist_refresh:"",
			_updateList : [],
			modifyList : [],
			deleteList : [],
			viewDocumentList : [],
			downloadList : [],
			createList : [],

			/*
			 * LifeHook Method called while initiating the widget.
			 * 
			 */
			postCreate : function() {
			    console.log("HLGridxPlugin Loading...");
			    this.logEntry("postCreate");
			    this.inherited(arguments);
			    _self = this;
			//    var gridxplugin=new GridxPluginAction();
			 //   gridxplugin.hideActions();
			    aspect.after(Desktop, "onLogin", lang.hitch(this, function() {
				var viewer = new ecm.widget.viewer.ContentViewer({
				    id : "contentV"
				});
				viewer.layoutEditButton.domNode.style.display = 'none';
				viewer.placeAt(self.viewerCP);
				viewer.startup();
				this.connect(this.contentSearchResults, "onRowClick", lang.hitch(this, function(item) {
				    if (item instanceof ecm.model.ContentItem) {
					var contentViewer = registry.byId("contentV");
					if (!item.isFolder() && contentViewer) {
					    contentViewer.open(item);
					}
				    }
				}));
				_self.closeAllViewers();
				this.contentSearchResults.setContentListModules(this.getContentListModules());
				this.contentSearchResults.setGridExtensionModules(this.getContentListGridModules());
				ApplicationConfig.getPluginObjects(function(plugins) {
				    for (var i = 0; i < plugins.length; i++) {
					if (plugins[i].id == "GridxPlugin") {
					    var configuration = JSON.parse(plugins[i]._attributes.configuration);
					    var parse1 = JSON.parse(configuration["configuration"][0].value);
					    _self.fileType = parse1.FileType;
					    _self.statusOfDocument = parse1.StatusOfDocument;
					    _self.natureOfDocument = parse1.NatureOfDocument;
					    _self.loanStatusArray = parse1.ReadOnlyLoanStatus;
					    _self.baseURL = configuration["configuration"][1].value;
					    _self._configJSON = configuration["configuration"];
					    _self.getDocumentList();
					    _self.getUserDetails();
					    var requestUrl = window.location.search;

					    if (requestUrl.indexOf('&id=') !== -1 && requestUrl.indexOf('&number=') !== -1
						    && requestUrl.indexOf('&filter=') !== -1) {
						_self.leadingPane.domNode.style.display = 'none';
						var user = requestUrl.split('&id=')[1];
						var decodeuser = _self.decoding(user);
						if (decodeuser !== Desktop.userId) {
						    Desktop.onLogout();
						    Desktop.logoff();
						    var myDialog = new ecm.widget.dialog.MessageDialog({
							text : "Authentication Failed"
						    });
						    myDialog.show();

						} else {
						    var searchParams = requestUrl.split('&number=')[1];
						    var number = searchParams.split('&filter=')[0];
						    var decodeNumber = _self.decoding(number);
						    dijit.byId("customerId").set("value", decodeNumber);
						    var filterParams = searchParams.split('&filter=')[1];
						    var filter = filterParams.split('&id=')[0];
						    if (filter === "A") {
							dijit.byId("radioOne").set("value", true);
							_self.resultsArea.domNode.style.display = 'block';
							_self.viewerCP.domNode.style.display = 'block';
							_self.topgrid.style.display = 'flex';
							_self.noResultGrid.style.display = 'none';
							_self.noResultContent.style.display = 'none';
							_self.noResult.style.display = 'none';
							_self.searchDocs(_self.customerId);
						    } else if (filter === "L") {
							dijit.byId("radioTwo").set("value", true);
							_self.resultsArea.domNode.style.display = 'block';
							_self.viewerCP.domNode.style.display = 'block';
							_self.topgrid.style.display = 'flex';
							_self.noResultGrid.style.display = 'none';
							_self.noResultContent.style.display = 'none';
							_self.noResult.style.display = 'none';
							_self.searchDocs(_self.customerId);
						    } else {
							Desktop.onLogout();
							Desktop.logoff();
							var myDialog = new ecm.widget.dialog.MessageDialog({
							    text : "URL is not proper"
							});
							myDialog.show();
						    }
						}
					    } else if (window.location.search !== "?desktop=" + Desktop.id) {
						_self.leadingPane.domNode.style.display = 'none';
						Desktop.onLogout();
						Desktop.logoff();
						var myDialog = new ecm.widget.dialog.MessageDialog({
						    text : "URL is not proper"
						});
						myDialog.show();

					    } else {
					    }
					}
				    }
				});
			    }));

			    aspect.after(Desktop, "onLogout", lang.hitch(_self, function() {
				_self.clearGrid();
				_self.customerId.reset();
				var viewer = dijit.byId('contentV');
				if (viewer !== undefined && viewer !== null) {
				    viewer.destroy();
				}
				_self.userDetails = "";
				_self._resultSet = "";
				_self._store = "";
				_self._grid = "";
				_self._updateList = [];
			    }));

			    this.logExit("postCreate");

			},

			/**
			 * Optional method that sets additional parameters when
			 * the user clicks on the launch button associated with
			 * this feature.
			 */
			setParams : function(params) {
			    this.logEntry("setParams", params);

			    if (params) {

				if (!this.isLoaded && this.selected) {
				    this.loadContent();
				}
			    }

			    this.logExit("setParams");
			},

			/**
			 * Loads the content of the pane. This is a required
			 * method to insert a pane into the LaunchBarContainer.
			 */
			loadContent : function() {
			    this.logEntry("loadContent");
			    if (!this.isLoaded) {
				_self.connect(_self.searchButton, "onClick", lang.hitch(_self, function(evt) {
				    _self.resultsArea.domNode.style.display = 'block';
				    _self.viewerCP.domNode.style.display = 'block';
				    _self.topgrid.style.display = 'flex';
				    _self.noResultGrid.style.display = 'none';
				    _self.noResultContent.style.display = 'none';
				    _self.noResult.style.display = 'none';

				    _self.searchDocs();
				}));
				_self.topPane.domNode.style.height = "auto";
				this.isLoaded = true;
				this.needReset = false;
			    }

			    this.logExit("loadContent");
			},

			/**
			 * Resets the content of this pane.
			 */
			reset : function() {
			    this.logEntry("reset");

			    /**
			     * This is an option method that allows you to force
			     * the LaunchBarContainer to reset when the user
			     * clicks on the launch button associated with this
			     */
			    this.needReset = true;

			    this.logExit("reset");
			},

			/*
			 * Method is used to create a "add row" button in the
			 * topPane. The Button is used to add new row to the
			 * grid.
			 */
			addNewRow : function() {
			    if (_self.createList !== undefined && _self.createList.length !== 0) {
				var addButton = new dijit.form.Button({
				    "label" : "Add Row",
				    "style" : "float:left; padding-right: 30px;padding-top: 12px;padding-bottom: 10px",
				    "onClick" : function() {
					var grid = dijit.byId('grid');
					if (grid === undefined) {
					    return;
					}
					var myNewItem = {
					    HF_ApplicationNumber : _self.loanDetails.srcApplId,
					    HF_LoanNumber : _self.loanDetails.loanNo,
					    HF_CustomerName : _self.primaryCustomer,
					    HF_Location : _self.baseLocation,
					    HF_LoanStatus : _self.loanDetails.loanStatus,
					    HF_CIFID : _self.getCifId(_self.primaryCustomer),
					    HF_DocumentDate : new Date(),
					    rowId : grid.store.data.length
					};
					grid.store.add(myNewItem);
				    }
				}).placeAt(_self.topPane);
				addButton.startup();
			    }
			},

			refreshButton : function() {
			    var refresh = new dijit.form.Button({
				"label" : "Refresh",
				"style" : "float:left; padding-right: 30px;padding-top: 12px;padding-bottom: 10px",
				"onClick" : function() {
				    var grid = dijit.byId('grid');
				    if (grid === undefined) {
					return;
				    }
				    _self.refresh();
				}
			    }).placeAt(_self.topPane);
			    refresh.startup();
			},

			/*
			 * Method is used to create a "Delete Row" button in the
			 * topPane. The Button is used to delete selected rows
			 * and corresponding documents from the grid.
			 */
			deleteRow : function() {
			    if (_self.deleteList !== undefined && _self.deleteList.length !== 0) {
				var myDialog = new ConfirmDialog({
				    title : "Delete Document",
				    content : "Do you want to delete the selected documents ?",
				    buttonCancel : "Label of cancel button",
				    buttonOk : "Label of OK button",
				    style : "width: 300px",
				    onCancel : function() {
				    },
				    onExecute : function() {
					var grid = dijit.byId('grid');
					var deleteRowIds = grid.select.row.getSelected();
					if (deleteRowIds.length !== 0) {
					    var docIds = 0 || [];
					    for ( var ids in deleteRowIds) {
						if (_self._store.data.length >= deleteRowIds[ids]
							&& _self._store.data[deleteRowIds[ids]] !== undefined) {
						    var oldValue = _self._store.data[deleteRowIds[ids]]["HF_DocumentSubType"];
						    var docTypeArray = 0 || [];
						    if (_self.userDetails.roleAccess.FN_DMS_Delete !== undefined) {
							if (_self.userDetails.roleAccess.FN_DMS_Delete !== "All") {
							    for ( var i in _self.userDetails.roleAccess.FN_DMS_Delete[0]) {
								docTypeArray.push(i);
							    }
							} else {
							    for ( var i in _self.docTypes) {
								docTypeArray.push(_self.docTypes[i].id);
							    }
							}
						    }
						    var docType = _self._store.data[deleteRowIds[ids]]["HF_DocumentType"];
						    var docSubTypeName = _self.checkAccessNameList(_self.userDetails.roleAccess.FN_DMS_Delete);
						    var docSubType = _self._grid.store.data[deleteRowIds[ids]].HF_DocumentSubType;
						    var keyIndex = _self.deleteList.indexOf(docSubType);
						    var valueIndex = docSubTypeName.indexOf(docSubType);
						    if ((keyIndex !== -1 || valueIndex !== -1)
							    || (docTypeArray.indexOf(docType) !== -1 && (oldValue === null || oldValue === ""))) {
							var _id = {
							    Id : grid.store.get(deleteRowIds[ids]).Id
							};
							docIds.push(_id);
						    }
						} else {
						    grid.store.remove(deleteRowIds[ids]);
						}
					    }
					    var requestParams = {};
					    var repo_Id = ecm.model.desktop.getDefaultRepositoryId();
					    var repo = Desktop.getRepository(repo_Id);
					    requestParams.repositoryId = repo.id;
					    requestParams.deleteData = json.toJson(docIds, true);
					    Request.invokePluginService("GridxPlugin", "HomeLoanDeleteService", {
						requestParams : requestParams,
						// requestBody : modifiedItems,
						requestCompleteCallback : lang.hitch(this,
							function(response) {
							    // console.log(response);
							    if (response === undefined || response === null || response === ""
								    || response.message === "Fail") {
								var message = new ecm.widget.dialog.MessageDialog({
								    text : response.errorMessage
								});
								message.show();
							    } else {
								for ( var __id in deleteRowIds) {
								    grid.store.remove(deleteRowIds[__id]);
								}
							    }
							    _self.refresh();
							})
					    });
					}

				    }
				});
				var deleteButton = new dijit.form.Button({
				    "label" : "Delete Row",
				    "style" : "float:left; padding-right: 30px;padding-top: 12px;padding-bottom: 10px",
				    "onClick" : function() {
					if (_self._grid.select.row.getSelected().length > 0) {
					    myDialog.show();
					} else {
					    var message = new ecm.widget.dialog.MessageDialog({
						text : "No Document Selected for Delete"
					    });
					    message.show();
					}

				    }
				}).placeAt(_self.topPane);
				deleteButton.startup();
				myDialog.hide();
			    }
			},

			deleteDocuments : function() {
			    if (_self.deleteList !== undefined && _self.deleteList.length !== 0) {
			    	if(dijit.byId('grid6').selection.getSelected().length === 0){
			    		var deleteSelected = new ecm.widget.dialog.MessageDialog({
				    text : "No Item Selected"
				});
				deleteSelected.show();
				return;
			    	}

				var myDialog = new ConfirmDialog({
				    title : "Delete Document",
				    content : "Do you want to delete the selected documents ?",
				    buttonCancel : false,
				    buttonOk : "Label of OK button",
				    style : "width: 300px",
				    onCancel : function() {
					myDialog.destroy();
				    },
				    onExecute : function() {
					var grid = dijit.byId('grid6');
					var deleteRowIds = grid.selection.getSelected();
					if (deleteRowIds.length !== 0) {
					    var docTypeArray = 0 || [];
					    if (_self.userDetails.roleAccess.FN_DMS_Delete !== undefined) {
						if (_self.userDetails.roleAccess.FN_DMS_Delete !== "All") {
						    for ( var i in _self.userDetails.roleAccess.FN_DMS_Delete[0]) {
							docTypeArray.push(i);
						    }
						} else {
						    for ( var i in _self.docTypes) {
							docTypeArray.push(_self.docTypes[i].id);
						    }
						}
					    }
					    var docIds = 0 || [];
					    var oldValue = _self._viewRow["HF_DocumentSubType"];
					    var docType = _self._viewRow["HF_DocumentType"];
					    var docSubTypeName = _self.checkAccessNameList(_self.userDetails.roleAccess.FN_DMS_Delete);
					    var docSubType = _self._viewRow["HF_DocumentSubType"];
					    var keyIndex = _self.deleteList.indexOf(docSubType);
					    var valueIndex = docSubTypeName.indexOf(docSubType);
					    if ((keyIndex !== -1 || valueIndex !== -1)
						    || (docTypeArray.indexOf(docType) !== -1 && (oldValue === null || oldValue === ""))) {
						for ( var i in deleteRowIds) {
						    var _id = {
							Id : deleteRowIds[i].link[0]
						    };
						    docIds.push(_id);
						}
					    }
					    var requestParams = {};
					    var repo_Id = ecm.model.desktop.getDefaultRepositoryId();
					    var repo = Desktop.getRepository(repo_Id);
					    requestParams.repositoryId = repo.id;
					    requestParams.deleteData = json.toJson(docIds, true);
					    Request.invokePluginService("GridxPlugin", "HomeLoanDeleteService", {
						requestParams : requestParams,
						// requestBody : modifiedItems,
						requestCompleteCallback : lang.hitch(this,
							function(response) {
							    // console.log(response);
							    if (response === undefined || response === null || response === ""
								    || response.message === "Fail") {
								var message = new ecm.widget.dialog.MessageDialog({
								    text : response.errorMessage
								});
								message.show();
								_self.refresh();
							    }
							if(dijit.byId('grid6').store._arrayOfAllItems.length > 1 && dijit.byId('grid6').store._arrayOfAllItems.length > deleteRowIds.length ){
							    _self.retrieveDocument();							
							}else{
								_self.gridDialog.destroy();
								_self.refresh();
							}

							})
					    });
					}

				    }
				});
				myDialog.show();
			    } else {
				var deleteDenied = new ecm.widget.dialog.MessageDialog({
				    text : "Access Denied"
				});
				deleteDenied.show();
			    }
			},

			saveUpdate : function() {
			    if (_self.modifyList !== undefined && _self.modifyList.length !== 0) {
				var myDialog = new ConfirmDialog({
				    title : "Update Grid",
				    content : "Grid was updated.Do you want to save changes ?",
				    buttonCancel : "Label of cancel button",
				    buttonOk : "Label of OK button",
				    style : "width: 300px",
				    onCancel : function() {
				    },
				    onExecute : function() {
					var requestParams = {};
					var repo_Id = ecm.model.desktop.getDefaultRepositoryId();
					var repo = Desktop.getRepository(repo_Id);
					requestParams.repositoryId = repo.id;
					requestParams.updateData = json.toJson(_self._updateList);
					Request.invokePluginService("GridxPlugin", "HomeLoanUpdateService", {
					    requestParams : requestParams,
					    requestCompleteCallback : lang.hitch(this, function(response) {
						// console.log(response);
						if (response === undefined || response === null || response === "" || response.message === "Fail") {
						    var myDialog = new ecm.widget.dialog.MessageDialog({
							text : response.errorMessage
						    });
						    myDialog.show();
						} else {
						    var myDialog = new ecm.widget.dialog.MessageDialog({
							text : "Record/s Modified Successfully"
						    });
						    myDialog.show();
						}
						_self.refresh();
						_self._updateList = [];
					    })
					});
				    }
				});

				var saveButton = new Button({
				    "label" : "Save",
				    "style" : "float:left; padding-right: 30px;padding-top: 12px;padding-bottom: 10px",
				    "onClick" : function() {
					if (_self._updateList.length === 0) {
					    var message = new ecm.widget.dialog.MessageDialog({
						text : "No Data to Modify"
					    });
					    message.show();
					} else {
					    myDialog.show();
					}
				    }
				}).placeAt(_self.topPane);
				saveButton.startup();
				myDialog.hide();
			    }
			},

			/*
			 * Clear children present in the topPane container.
			 * 
			 */
			clearGrid : function() {
			    var topPaneChild = _self.topPane.getChildren();
			    for ( var i in topPaneChild) {
				topPaneChild[i].destroy();
			    }
			    if (dijit.byId('grid') !== undefined) {
				dijit.byId('grid').destroy();
			    }
			},

			refresh : function() {
			    _self._resultSet.searchTemplate.search(function(rs) {
				_self._resultSet = rs;
				_self._createGrid(rs);
			    }, null, null, null, function(error) {
				console.error(error);
			    });
			},

			/*
			 * Create new grid as per defined structure. @param
			 * ResultSet resultSet
			 * 
			 */

			_createGrid : function(resultSet) {

			    _self.clearGrid();

			    if (_self.loanDetails.loanStatus === "Disbursed") {
				var child = _self.modeTabContainer.getChildren();
				_self.modeTabContainer.removeChild(child[0]);
			    } else {

				_self.addNewRow();

				// _self.deleteRow();

				_self.saveUpdate();

				_self.refreshButton();

				_self._updateList = [];

				var gridItems = 0 || [];

				for (var i = 0; i < resultSet.items.length; i++) {
				    gridItems.push(resultSet.items[i].attributes);
				}

				gridItems = _self.gridUpperCase(gridItems);

				var data = {
				    identifier : 'rowId',
				    label : 'rowId',
				    items : []
				};

				for (var i = 0; i < gridItems.length; ++i) {
				    var item = gridItems[i % gridItems.length];
				    data.items.push(lang.mixin({
					rowId : i
				    }, item));
				}
				try {
				    fileTypeStore = _self.createSelectStore(_self.fileType);

				    natureOfDocumentStore = _self.createSelectStore(_self.natureOfDocument);

				    statusOfDocumentStore = _self.createSelectStore(_self.statusOfDocument);

				    locationStore = _self.createSelectStore(_self.userDetails.attachLocation);

				    customerNameStore = new Memory({
					data : _self.customerList
				    });

				    documentTypeStore = new Memory({
					data : _self.docTypes
				    });

				    documentSubTypeStore = new Memory({
					data : _self.docSubTypes
				    });
				} catch (e) {
				    console.error(e);
				}

				var store1 = new Memory({
				    data : data
				});

				layout1 = [

					{

					    name : 'CIFID',
					    field : 'HF_CIFID',
					    style : 'text-align: center;border: 1px solid #9f9f9f; ',
					    width : "10em",
					},
					{
					    name : 'Customer Name',
					    field : 'HF_CustomerName',
					    style : 'text-align: center;border: 1px solid #9f9f9f; ',
					    width : "200px",
					    widgetsInCell : true,
					    navigable : true,
					    allowEventBubble : true,
					    setCellValue : _self.selectCustomer,
					    decorator : function() {
						return [ '<div ', 'data-dojo-attach-point="customer" ', '"></div>' ].join('');
					    },
					},
					{
					    name : 'Document Name',
					    field : 'HF_DocumentSubType',
					    style : 'text-align: center;border: 1px solid #9f9f9f; ',
					    width : "10em",
					    editor : FilteringSelect,
					    editorArgs : {
						props : 'store: documentSubTypeStore, searchAttr: "name", placeHolder : "Select Document SubType", style : "width: 100%; margin-left : auto", onChange : function(e){ _self.getDocumentType(e, this); }',
					    },
					    options : _self.docSubTypes,
					    editable : true,
					    alwaysEditing : true,
					},
					{
					    name : 'Document Type',
					    field : 'HF_DocumentType',
					    editor : TextBox,
					    editorArgs : {
						props : 'placeHolder : "Select Document Type", style : "width: 100%; margin-left : auto"',
					    },
					    style : 'text-align: center;border: 1px solid #9f9f9f; ',
					    width : "10em",
					    editable : false,
					    alwaysEditing : false,
					},

					{
					    name : 'Document Number',
					    field : 'HF_DocumentNumber',
					   // editor : "dijit/form/NumberTextBox",
					    editor : TextBox,
					    editorArgs : {
						props : 'placeHolder : "Enter Document Number", style : "width: 100%; margin-left : auto", onChange : function(e){   _self.updateProperties(e,this); }',
					    },
					    style : 'text-align: center; border: 1px solid #9f9f9f; ',
					    width : "10em",
					    editable : true,
					    alwaysEditing : true,
					},
					{
					    name : 'Document Date',
					    field : 'HF_DocumentDate',
					    style : 'text-align: center; border: 1px solid #9f9f9f; ',
					    width : '10em',
					    editable : true,
					    alwaysEditing : true,
					    editor : DateTextBox,
					    editorArgs : {
						props : 'constraints : {datePattern:"dd/MM/yy"} , style : "width: 100%; margin-left : auto", onChange : function(e){   _self.updateProperties(e,this); }'
					    },
					},
					{
					    name : 'Add / View Document',
					    field : 'Id',
					    style : 'text-align: center; border: 1px solid #9f9f9f; ',
					    width : "67px",
					    widgetsInCell : true,
					    navigable : true,
					    allowEventBubble : true,
					    setCellValue : _self.addDocument,
					    decorator : function() {
						return [ '<div ', 'data-dojo-attach-point="addBtn" ', '"></div>' ].join('');
					    },
					},
					{
					    name : 'File Type',
					    field : 'HF_FileType',
					    editor : FilteringSelect,
					    editorArgs : {
						props : 'required : false, store: fileTypeStore, searchAttr: "id", placeHolder : "Select File Type", style : "width: 100%; margin-left : auto", onChange : function(e){   _self.updateProperties(e,this); }',
					    },
					    options : _self.fileType,
					    style : 'text-align: center;border: 1px solid #9f9f9f; ',
					    width : "10em",
					    editable : true,
					    alwaysEditing : true,
					},
					{
					    name : 'Nature Of Document',
					    field : 'HF_NatureOfDocument',
					    editor : FilteringSelect,
					    editorArgs : {
						props : 'required : false, store: natureOfDocumentStore, searchAttr: "id", placeHolder : "Select Nature of Document", style : "width: 100%; margin-left : auto", onChange : function(e){   _self.updateProperties(e,this); }',
					    },
					    options : _self.natureOfDocument,
					    style : 'text-align: center;border: 1px solid #9f9f9f; ',
					    width : "10em",
					    editable : true,
					    alwaysEditing : true,
					},
					{
					    name : 'Status Of Document',
					    field : 'HF_StatusOfDocument',
					    editor : FilteringSelect,
					    editorArgs : {
						props : 'required : false, store: statusOfDocumentStore, searchAttr: "id", placeHolder : "Select Status of Document", style : "width: 100%; margin-left : auto", onChange : function(e){   _self.updateProperties(e,this); }',
					    },
					    options : _self.statusOfDocument,
					    style : 'text-align: center;border: 1px solid #9f9f9f; ',
					    width : "10em",
					    editable : true,
					    alwaysEditing : true,
					},

					{
					    name : 'Executed By',
					    field : 'HF_ExecutedBy',
					    editor : TextBox,
					    style : 'text-align: center; border: 1px solid #9f9f9f; ',
					    width : "10em",
					    editor : TextBox,
					    editorArgs : {
						props : 'placeHolder : "Enter Name", style : "width: 100%; margin-left : auto", onChange : function(e){   _self.updateProperties(e,this);}',
					    },
					    editable : true,
					    alwaysEditing : true,
					},
					{
					    name : 'Executed In Favor Of',
					    field : 'HF_ExecutedInFavourOf',
					    editor : TextBox,
					    style : 'text-align: center; border: 1px solid #9f9f9f; ',
					    width : "200px",
					    editable : true,
					    alwaysEditing : true,
					    editorArgs : {
						props : 'placeHolder : "Enter Executed In Favour of Name", style : "width: 100%; margin-left : auto", onChange : function(e){   _self.updateProperties(e,this); }',
					    },
					},
					{
					    name : 'Location',
					    field : 'HF_Location',
					    style : 'text-align: center;border: 1px solid #9f9f9f; ',
					    width : "200px",
					    widgetsInCell : true,
					    navigable : true,
					    allowEventBubble : true,
					    setCellValue : _self.selectLocation,
					    decorator : function() {
						return [ '<div ', 'data-dojo-attach-point="location" ', '"></div>' ].join('');
					    },
					},
					{

					    name : 'Description',
					    field : 'HF_Description',
					    editor : 'dijit/form/Textarea',
					    editorArgs : {
						props : 'placeHolder : "Enter Description", style : "width: 100%; margin-left : auto", onChange : function(e){   _self.updateProperties(e,this); }',
					    },
					    editorIgnoresEnter : true,
					    style : 'text-align: center; border: 1px solid #9f9f9f; ',
					    width : "200px",
					    editable : true,
					    alwaysEditing : true,
					}, {
					    name : 'Loan Status',
					    field : 'HF_LoanStatus',
					    style : 'text-align: center;border: 1px solid #9f9f9f; ',
					    width : "10em",

					},
				// {
				// name : 'Update Document',
				// field : 'Id',
				// style : 'text-align: center; border: 1px
				// solid #9f9f9f; ',
				// width : "200px",
				// widgetsInCell : true,
				// navigable : true,
				// allowEventBubble : true,
				// setCellValue : _self.checkInDocument,
				// decorator : function() {
				// return [ '<div ',
				// 'data-dojo-attach-point="checkInBtn" ',
				// '"></div>' ].join('');
				// },
				// },

				];

				var grid = Grid({
				    id : 'grid',
				    cacheClass : Cache,
				    store : store1,
				    structure : layout1,
				    selectRowTriggerOnCell : true,
				    bodyEmptyInfo : "No Documents Found",
				    paginationInitialPageSize: 25,
				    filterBarCloseButton : false,
				    modules : [ CellWidget, Edit, Pagination, PaginationBar, Menu, Row, ColumnResizer, Filter, FilterBar ]
				});

				grid.domNode.style.height = _self.gridContainer.parentElement.offsetHeight * 0.90 + "px";

				grid.placeAt(_self.gridContainer);

				// var menu = dijit.byId('headerMenu');
				//
				// grid.menu.bind(menu, {
				// hookPoint : "row",
				// selected : false
				// });

				_self._grid = grid;

				_self._store = dojo.clone(grid.store);

				grid.startup();
			    }

			    for (var i = 0; i < resultSet.items.length; i++) {
				if (resultSet.items[i].attributes.HF_DocumentSubType !== null
					&& resultSet.items[i].attributes.HF_DocumentSubType !== undefined
					&& resultSet.items[i].attributes.HF_DocumentSubType !== "") {
				    var docTypeflag = false;
				    for (var x = 0; x < Object.keys(_self.docs).length; x++) {
					var docTArray = _self.docs[Object.keys(_self.docs)[x]];
					for (var y = 0; y < Object.keys(docTArray).length; y++) {
					    if (docTArray[y][Object.keys(docTArray[y])[0]] === resultSet.items[i].attributes.HF_DocumentSubType) {
						docTypeflag = true;
						resultSet.items[i].attributes.HF_DocumentSubType = Object.keys(docTArray[y])[0];
						break;
					    }
					}
					if (docTypeflag) {
					    break;
					}
				    }
				}
			    }
			    _self.contentSearchResults.setResultSet(resultSet);
			    _self.contentSearchResults.emptyMessage = "No Documents Found";
			if(_self._contentlist_refresh){
				console.log("button created",_self.contentlist_refresh_id);

			}
			else
				{
				console.log("inside--new button",_self.contentlist_refresh_id);
				_self._contentlist_refresh = new dijit.form.Button({
				"id" : "contentlist_refreshid",
				"data-dojo-attach-point":"contentlist_refresh_id",
				"label" : "Refresh",
				"style" : "float:left",
				"onClick" : function() {
                   			_self.contentSearchResults.setResultSet(_self._resultSet);
				}
			    }).placeAt(_self.contentSearchResults.topContainer.getChildren()[0]);
				_self._contentlist_refresh.startup();
				}		    
			},

			searchDocs : function() {

			    _self.clearGrid();

			    _self.closeAllViewers();

			    _self.getLoanDetails();

			    if (_self.loanDetails.status === 'Success') {

				var filter;

				with (dojo.byId('searchForm'))
				    with (elements[0])
					with (elements[checked ? 0 : 1]) {
					    filter = value;
					}

				var docTypeArray = 0 || [];

				for ( var i in _self.docTypes) {
				    docTypeArray.push("'" + _self.docTypes[i].id + "'");
				}

				var location = 0 || [];

				for ( var i in _self.userDetails.attachLocation) {
				    location.push("'" + _self.userDetails.attachLocation[i] + "'");
				}

				var query;
				if (filter === "A") {
				    query = "SELECT * from HF_DMS WHERE HF_DocumentType IN (" + docTypeArray.toString() + ") AND HF_Location IN ("
					    + location.toString() + ") AND VersionStatus=1 AND HF_ApplicationNumber = '" + this.customerId.value
					    + "' ";
				} else {
				    query = "SELECT * from HF_DMS WHERE HF_DocumentType IN (" + docTypeArray.toString() + ") AND HF_Location IN ("
					    + location.toString() + ") AND VersionStatus=1 AND HF_LoanNumber = '" + this.customerId.value + "' ";
				}
				query = query + "AND ( CompoundDocumentState = 1 OR ( HF_DocumentSubType is null OR HF_DocumentSubType = '' ) )";

				var repo_Id = ecm.model.desktop.getDefaultRepositoryId();
				var repository = Desktop.getRepository(repo_Id);
				// console.log(repository);
				/**
				 * converted to model API*
				 */
				var queryParams = {};
				queryParams.pageSize = 100;
				queryParams.query = query;
				queryParams.retrieveAllVersions = false;
				queryParams.retrieveLatestVersion = true;
				queryParams.repository = repository;

				queryParams.resultsDisplay = {
				    "sortBy" : "HF_DocumentDate",
				    "sortAsc" : false,
				    "columns" : [ "HF_ApplicationNumber", "HF_DocumentType", "HF_LoanNumber", "HF_DocumentDate", "HF_CIFID",
					    "HF_CustomerName", "HF_LoanStatus", "HF_Location", "HF_DocumentNumber", "HF_FileType",
					    "HF_NatureOfDocument", "HF_StatusOfDocument", "HF_ExecutedBy", "HF_ExecutedInFavourOf", "HF_Description",
					    "Id", "HF_DocumentSubType" , "HF_ParentId"],
				    "honorNameProperty" : true
				};

				var searchQuery = new SearchQuery(queryParams);
				searchQuery.search(function(resultSet) {
				    _self._resultSet = resultSet;
				    _self._createGrid(resultSet);
				}, null, null, null, function(error) {
				    var message = new ecm.widget.dialog.MessageDialog({
					text : "Search error::" + error
				    });
				    message.show();
				});

			    } else {
				if (_self.leadingPane.domNode.style.display === 'none') {
				    _self.modeTabContainer.domNode.style.display = 'none';
				    _self.noResult.style.display = 'block';
				    _self.topgrid.style.display = 'none';
				} else {
				    _self.resultsArea.domNode.style.display = 'none';
				    _self.viewerCP.domNode.style.display = 'none';
				    _self.topgrid.style.display = 'none';
				    _self.noResultGrid.style.display = 'block';
				    _self.noResultContent.style.display = 'block';
				}
			    }

			},

			addButton : function() {
			    var row = _self._viewRow;
			    var entryTemplate = row["HF_DocumentType"] + " Document Entry Template";
			    var repo_Id = ecm.model.desktop.getDefaultRepositoryId();
			    var repository = Desktop.getRepository(repo_Id);
			    var addContentItemDialog = new AddContentItemDialog();
			    addContentItemDialog.titleBar.style.display = 'none';
			    aspect.after(addContentItemDialog, 'show', lang.hitch(function() {
				var sty = dojo.byId(addContentItemDialog.id + "_underlay");
				sty.style.opacity = 1.0;
			    }));
			    aspect.before(addContentItemDialog, 'onAdd', lang.hitch(function() {
				var sty = dojo.byId(addContentItemDialog.id + "_underlay");
				sty.style.opacity = 0.5;
			    }));
			    aspect.after(addContentItemDialog, 'onCancel', lang.hitch(function() {
				var sty = dojo.byId(addContentItemDialog.id + "_underlay");
				sty.style.opacity = 0.5;
			    }));
			    var folderPath = "/LICHFL/" + row["HF_DocumentType"];
			    var finalItem = _self.getFolderInfo(repository, folderPath);
			    finalItem.then(function(data) {
				repository.retrieveEntryTemplates(lang.hitch(this, function(response) {
				    finalItem = data;
				    for (var int2 = 0; int2 < response.length; int2++) {
					if (response[int2].name == entryTemplate) {
					    response[int2].retrieveEntryTemplate(lang.hitch(this, function(retrievedEntryTemp) {
						retrievedEntryTemp.folder = finalItem;
						aspect.after(addContentItemDialog.addContentItemPropertiesPane, "onCompleteRendering", function() {
						    addContentItemDialog.addContentItemPropertiesPane.setPropertyValue("HF_Source", "ICN");
						    addContentItemDialog.addContentItemPropertiesPane.setPropertyValue("HF_DocumentSubType",
							    row["HF_DocumentSubType"]);
						    addContentItemDialog.addContentItemPropertiesPane.setPropertyValue("HF_DocumentDate", new Date(
							    row["HF_DocumentDate"]).toISOString());
						    addContentItemDialog.addContentItemPropertiesPane.setPropertyValue("HF_DocumentNumber",
							    row["HF_DocumentNumber"]);
						    addContentItemDialog.addContentItemPropertiesPane.setPropertyValue("HF_StatusOfDocument",
							    row["HF_StatusOfDocument"]);
						    addContentItemDialog.addContentItemPropertiesPane.setPropertyValue("HF_ExecutedInFavourOf",
							    row["HF_ExecutedInFavourOf"]);
						    addContentItemDialog.addContentItemPropertiesPane.setPropertyValue("HF_Description",
							    row["HF_Description"]);
						    addContentItemDialog.addContentItemPropertiesPane.setPropertyValue("HF_ExecutedBy",
							    row["HF_ExecutedBy"]);
						    addContentItemDialog.addContentItemPropertiesPane.setPropertyValue("HF_FileType",
							    row["HF_FileType"]);
						    addContentItemDialog.addContentItemPropertiesPane.setPropertyValue("HF_NatureOfDocument",
							    row["HF_NatureOfDocument"]);
						    addContentItemDialog.addContentItemPropertiesPane.setPropertyValue("HF_ApplicationNumber",
							    row["HF_ApplicationNumber"]);
						    addContentItemDialog.addContentItemPropertiesPane.setPropertyValue("HF_CIFID", row["HF_CIFID"]);
						    addContentItemDialog.addContentItemPropertiesPane.setPropertyValue("HF_CustomerName",
							    row["HF_CustomerName"]);
						    addContentItemDialog.addContentItemPropertiesPane.setPropertyValue("HF_LoanNumber",
							    row["HF_LoanNumber"]);
						    addContentItemDialog.addContentItemPropertiesPane.setPropertyValue("HF_LoanStatus",
							    row["HF_LoanStatus"]);
						    addContentItemDialog.addContentItemPropertiesPane.setPropertyValue("HF_Location",
							    row["HF_Location"]);
						    addContentItemDialog.addContentItemPropertiesPane.setPropertyValue("HF_ParentId", row["HF_ParentId"] !== null ? row["HF_ParentId"] : row["Id"]);
						}, true);

						addContentItemDialog.show(repository, folderPath, true, false, function(callback) {
						    _self.retrieveDocument();
						}, null, true, retrievedEntryTemp, false);
					    }));
					}
				    }
				}), "Document", null, null, repository.objectStore);
			    });
			},

			addDocument : function(gridData, storeData, cellWidget) {
			    if (cellWidget.getChildren().length !== 0) {
				cellWidget.addBtn.removeChild(cellWidget.addBtn.childNodes[0]); // remove
				// existing
				// childNodes
				// in
				// cellWidget
			    }
			    if (gridData === undefined) {
				var add = new Button({
				    label : "Add",
				    onClick : function() {
					var row = dijit.byId('grid').store.data[cellWidget.cell.row.id];
					if (row["HF_DocumentType"] !== undefined) {

					    var confirmation = new ConfirmDialog({
						title : "Add Document",
						content : '<b>Please proceed if all details are provided.</b> <br>' + 'Click Ok to Proceed <br>',
						buttonCancel : "Label of cancel button",
						buttonOk : "Label of OK button",
						style : "width: 300px",
						onCancel : function() {
						},
						onExecute : function() {
						    var entryTemplate = row["HF_DocumentType"] + " Document Entry Template";
						    var repo_Id = ecm.model.desktop.getDefaultRepositoryId();
						    var repository = Desktop.getRepository(repo_Id);
						    var addContentItemDialog = new AddContentItemDialog();
						    addContentItemDialog.titleBar.style.display = 'none';
						    aspect.after(addContentItemDialog, 'show', lang.hitch(function() {
							var sty = dojo.byId(addContentItemDialog.id + "_underlay");
							sty.style.opacity = 1.0;
						    }));
						    aspect.before(addContentItemDialog, 'onAdd', lang.hitch(function() {
							var sty = dojo.byId(addContentItemDialog.id + "_underlay");
							sty.style.opacity = 0.5;
						    }));
						    aspect.after(addContentItemDialog, 'onCancel', lang.hitch(function() {
							var sty = dojo.byId(addContentItemDialog.id + "_underlay");
							sty.style.opacity = 0.5;
						    }));
						    var folderPath = "/LICHFL/" + row["HF_DocumentType"];
						    var finalItem = _self.getFolderInfo(repository, folderPath);
						    finalItem.then(function(data) {
							repository.retrieveEntryTemplates(lang.hitch(this, function(response) {
							    finalItem = data;
							    for (var int2 = 0; int2 < response.length; int2++) {
								if (response[int2].name == entryTemplate) {
								    response[int2].retrieveEntryTemplate(lang.hitch(this,
									    function(retrievedEntryTemp) {
										retrievedEntryTemp.folder = finalItem;
										aspect.after(addContentItemDialog.addContentItemPropertiesPane,
											"onCompleteRendering", function() {
											    addContentItemDialog.addContentItemPropertiesPane
												    .setPropertyValue("HF_Source", "ICN");
											    addContentItemDialog.addContentItemPropertiesPane
												    .setPropertyValue("HF_DocumentSubType",
													    row["HF_DocumentSubType"]);
											    addContentItemDialog.addContentItemPropertiesPane
												    .setPropertyValue("HF_DocumentDate", new Date(
													    row["HF_DocumentDate"]).toISOString());
											    addContentItemDialog.addContentItemPropertiesPane
												    .setPropertyValue("HF_DocumentNumber",
													    row["HF_DocumentNumber"]);
											    addContentItemDialog.addContentItemPropertiesPane
												    .setPropertyValue("HF_StatusOfDocument",
													    row["HF_StatusOfDocument"]);
											    addContentItemDialog.addContentItemPropertiesPane
												    .setPropertyValue("HF_ExecutedInFavourOf",
													    row["HF_ExecutedInFavourOf"]);
											    addContentItemDialog.addContentItemPropertiesPane
												    .setPropertyValue("HF_Description",
													    row["HF_Description"]);
											    addContentItemDialog.addContentItemPropertiesPane
												    .setPropertyValue("HF_ExecutedBy",
													    row["HF_ExecutedBy"]);
											    addContentItemDialog.addContentItemPropertiesPane
												    .setPropertyValue("HF_FileType",
													    row["HF_FileType"]);
											    addContentItemDialog.addContentItemPropertiesPane
												    .setPropertyValue("HF_NatureOfDocument",
													    row["HF_NatureOfDocument"]);
											    addContentItemDialog.addContentItemPropertiesPane
												    .setPropertyValue("HF_ApplicationNumber",
													    row["HF_ApplicationNumber"]);
											    addContentItemDialog.addContentItemPropertiesPane
												    .setPropertyValue("HF_CIFID", _self
													    .getCifId(row["HF_CustomerName"]));
											    addContentItemDialog.addContentItemPropertiesPane
												    .setPropertyValue("HF_CustomerName",
													    row["HF_CustomerName"]);
											    addContentItemDialog.addContentItemPropertiesPane
												    .setPropertyValue("HF_LoanNumber",
													    row["HF_LoanNumber"]);
											    addContentItemDialog.addContentItemPropertiesPane
												    .setPropertyValue("HF_LoanStatus",
													    row["HF_LoanStatus"]);
											    addContentItemDialog.addContentItemPropertiesPane
												    .setPropertyValue("HF_Location",
													    row["HF_Location"]);
											}, true);

										addContentItemDialog.show(repository, folderPath, true, false,
											function(callback) {
											    _self.refresh();
											}, null, true, retrievedEntryTemp, false);
									    }));
								}
							    }
							}), "Document", null, null, repository.objectStore);
						    });
						}
					    });

					    confirmation.show();
					} else {
					    var message = new ecm.widget.dialog.MessageDialog({
						text : "Please select Document Name"
					    });
					    message.show();
					}
				    }
				});
				if (cellWidget.addBtn.childElementCount === 0) {
				    add.placeAt(cellWidget.addBtn);
				    add.set('rowId', cellWidget.cell.row.id);
				}
				cellWidget.addBtn.setAttribute('rowId', cellWidget.cell.row.id);
			    } else {

				// var repo_Id =
				// ecm.model.desktop.getDefaultRepositoryId();
				// var desktopId = ecm.model.desktop.id;

				// var Url = window.location.protocol + "\/\/" +
				// window.location.host +
				// "/navigator/bookmark.jsp?desktop=" +
				// desktopId
				// + "&repositoryId=" + repo_Id +
				// "&repositoryType=p8&docid=" + storeData;
				// var newLink = "<a href=\"#\"
				// onclick=\"window.open('" + Url
				// + "','Viewer', 'width=600, height=600,
				// resizable = yes')\" //>View Document</a>";

				var newLink = "<a><span onClick = \" _self.retrieveDocument(this); console.log('Span Clicked')\">View Document </span></a>";

				cellWidget.addBtn.innerHTML = newLink;
			    }

			},

			retrieveDocument : function(spanScope) {
			    var row;
			    var cellWidget;
			    if (spanScope !== undefined) {
				cellWidget = dijit.byId(spanScope.parentElement.parentElement.parentElement.getAttribute('widgetId'));
				_self._cellWidget = cellWidget;
				row = _self._grid.store.data[cellWidget.cell.row.id];
				_self._viewRow = row;
			    } else {
				row = _self._viewRow;
				cellWidget = _self._cellWidget;
			    }
			    console.log("Inside function");
			    var dataDocs = _self._resultSet.items;
			    var selectedRow = cellWidget.cell.row.id;
			    console.log(dataDocs);
			    var srow = Number(selectedRow);
			    console.log(srow);
			    var documentData = {};
			    for (var i = 0; i < dataDocs.length; i++) {
				if (i == srow) {
				    documentData = dataDocs[i];
				    break;
				}
			    }
			    var docid1 = documentData.id;
			    var id = docid1.split(",", 3);

			    var requestParams = {};

			    var repo_Id = ecm.model.desktop.getDefaultRepositoryId();
			    var repo = Desktop.getRepository(repo_Id);
			    requestParams.repositoryId = repo.id;

			    var documentId = id[2];
			    // var documentId = documentData.id;
			    var documentName = documentData.name;
			    requestParams.documentId = documentData.attributes.HF_ParentId != null ? documentData.attributes.HF_ParentId : documentId;
			    requestParams.documentName = documentName;

			    Request
				    .invokePluginService(
					    "GridxPlugin",
					    "HomeLoanCompoundDocumentService",
					    {
						requestParams : requestParams,
						requestCompleteCallback : lang
							.hitch(
								this,
								function(response) {
								    console.log(response);
								    if (response === undefined || response === null || response === ""
									    || response.message === "Fail") {
									alert(response.errorMessage);
								    } else {

									if (dijit.byId('grid6') !== undefined) {
									    dijit.byId('grid6').destroy();
									}

									var data1 = {
									    identifier : 'id_no',
									    label : 'id_no',
									    items : []
									};

									var repo_Id = ecm.model.desktop.getDefaultRepositoryId();
									var desktopId = ecm.model.desktop.id;

									if (response.JSON !== undefined) {
									    data1.items.push({
										'id_no' : 1,
										"documentNames" : response.documentName,
										"link" : response.id,
										"attachment" : response.id
									    });
									    var childData = JSON.parse(response.JSON);
									    for (var i = 0; i < childData.length; i++) {
										data1.items.push({
										    'id_no' : i + 3,
										    "documentNames" : childData[i].docTitle,
										    "link" : childData[i].Id,
										    "attachment" : childData[i].Id
										});
									    }

									} else {
									    data1.items.push({
										'id_no' : 1,
										"documentNames" : response.documentName,
										"link" : response.id,
										"attachment" : response.id
									    });
									}

									var store1 = new ItemFileWriteStore({
									    data : data1
									});

									var structure1 = 0 || [
										{
										    name : "Document Name",
										    field : "documentNames",
										    width : 18,
										    styles : 'text-align: center; border: 1px solid #9f9f9f;',
										    editable : false
										},
										{
										    name : "Link",
										    field : "link",
										    width : 6,
										    styles : 'text-align: center; border: 1px solid #9f9f9f;',
										    editable : false,
										    formatter : function(item) {
											var Url = window.location.protocol + "\/\/"
												+ window.location.host
												+ "/navigator/bookmark.jsp?desktop=" + desktopId
												+ "&repositoryId=" + repo_Id
												+ "&repositoryType=p8&docid=" + item;
											var newLink = "<a onclick=\"window.open('"
												+ Url
												+ "', 'Document Viewer', 'width=600, height=600, resizable = yes')\" >View Document</a>";
											return newLink;
										    }
										},
										{
										    name : "Attachment",
										    field : "attachment",
										    width : 12,
										    styles : 'text-align: center; border: 1px solid #9f9f9f;',
										    editable : true,
										    formatter : function(item) {
											var myDialog = new ConfirmDialog(
												{
												    title : "Update Document",
												    buttonCancel : "Cancel",
												    buttonOk : "Save",
												    content : '<form data-dojo-attach-point="_fileInputForm" name="fileUploadForm" enctype="multipart/form-data" accept-charset="UTF-8" method="post" target="'
													    + item
													    + '_fileInputIFrame">'
													    + 'Updated Document :    '
													    + '<input type="file" required="true" id="'
													    + item
													    + '" name="uploadFile" class="fileInput" data-dojo-attach-point="_fileInput"/>'
													    + '<iframe name="'
													    + item
													    + '_fileInputIFrame" id="'
													    + item
													    + '_fileInputIFrame" style="display: none"></iframe> </form>',
												    onCancel : function() {
												    },
												    onExecute : function() {
													var reqParams = {
													    desktop : ecm.model.desktop.id,
													    repositoryId : ecm.model.desktop
														    .getDefaultRepositoryId(),
													    docId : item
													};

													// var
													// callback
													// =
													// lang.hitch(this,
													// self._onAddCompleted);

													// HTML5
													// browser
													if (this._firstFocusItem.files) {
													    var file = this._firstFocusItem.files[0];
													    reqParams.mimetype = file.type;
													    reqParams.parm_part_filename = file.name;
													    var fileform = new FormData();
													    fileform.append("file", file);
													    Request
														    .postFormToPluginService(
															    "GridxPlugin",
															    "HomeLoanUpdateDocumentService",
															    fileform,
															    {
																requestParams : reqParams,
																requestCompleteCallback : lang
																	.hitch(
																		this,
																		function(
																			response) {
																		    // console.log(response);
																		    if (response === undefined
																			    || response === null
																			    || response === ""
																			    || response.message === "Fail") {
																			var message = new ecm.widget.dialog.MessageDialog(
																				{
																				    text : response.errorMessage
																				});
																			message
																				.show();
																		    } else {
																			
																			_self
																				.retrieveDocument();
																		    }
																		})
															    });
													} else { // Non-HTML5
													    // browser
													    var fileName = self._fileInput.value;
													    if (fileName && fileName.length > 0) {
														var i = fileName.lastIndexOf("\\");
														if (i != -1) {
														    fileName = fileName.substr(i + 1);
														}
													    }
													    reqParams.parm_part_filename = fileName;
													    reqParams.plugin = "GridxPlugin";
													    reqParams.action = "HomeLoanUpdateDocumentService";

													    Request
														    .ieFileUploadServiceAPI(
															    "plugin",
															    "",
															    {
																requestParams : reqParams,
																requestCompleteCallback : lang
																	.hitch(
																		this,
																		function(
																			response) {
																		    if (response.fieldErrors) {
																			// console.dir(response.fieldErrors);
																		    } else if (this._items
																			    && this._items.length > 0
																			    && response
																			    && response.mimetype) {
																			lang
																				.mixin(
																					this._items[0],
																					response);
																			this._items[0]
																				.refresh();
																		    }
																		    this
																			    .hide();
																		})
															    },
															    dojo
																    .byId('_fileInputForm'));
													}
												    }
												});
											var update = new Button({
											    label : "Update Document",
											    onClick : function() {
												myDialog.show();
											    }
											});
											return update;
										    }
										} ];

									var myDialog = new ConfirmDialog(
										{
										    title : "Add Multiple Documents",
										    buttonCancel : "Ok",
										    buttonOk : "Cancel",
										    content : '<div>'
											    + '<button data-dojo-attach-point="addDoc" data-dojo-type="dijit.form.Button" type="button" onClick = "_self.addButton()" >Add Document</button>'
											    + '<button data-dojo-attach-point="removeDoc" data-dojo-type="dijit.form.Button" type="button" onClick = "_self.deleteDocuments()">Remove Document</button>'
											    + '<br/>'
											    + '<br/>'
											    + '<table dojoType="dojox.grid.EnhancedGrid" id="grid6" selectionMode="extended" class="popupDojoTable" style="width:60em; height:50em; " rowSelector="20px" data-dojo-props="plugins:{indirectSelection: true}">'
											    +

											    '</table>' + '</div>',

										    onCancel : function() {
											_self._viewRow = null;
											_self._cellWidget = null;
											myDialog.destroy();
										    },
										    onExecute : function() {
											_self._viewRow = null;
											_self._cellWidget = null;
											myDialog.destroy();
										    },

										});
									dijit.byId('grid6').setStore(store1);
									dijit.byId('grid6').setStructure(structure1);
									myDialog.show();
									_self.gridDialog = myDialog;
								    }
								})
					    });

			},

			selectCustomer : function(gridData, storeData, cellWidget) {
			    if (cellWidget.getChildren().length !== 0) {
				cellWidget.customer.removeChild(cellWidget.customer.childNodes[0]); // remove
				// existing
				// childNodes
				// in
				// cellWidget
			    }
			    var rowId = cellWidget.cell.row.id;
			    var row = dijit.byId('grid').store.get(rowId);
			    if (row.Id === undefined) {
				var select = new FilteringSelect({
				    store : customerNameStore,
				    searchAttr : "id",
				    value : _self.primaryCustomer,
				    placeHolder : "Select Customer",
				    style : "width: 100%; margin-left : auto",
				    onChange : function(e) {
					_self.setCIFID(e, this);
				    }
				});
				if (cellWidget.customer.childElementCount === 0) {
				    select.placeAt(cellWidget.customer);
				}
			    } else {
				cellWidget.customer.innerHTML = gridData;
			    }
			},

			selectLocation : function(gridData, storeData, cellWidget) {
			    if (cellWidget.getChildren().length !== 0) {
				cellWidget.location.removeChild(cellWidget.location.childNodes[0]); // remove
				// existing
				// childNodes
				// in
				// cellWidget
			    }
			    var rowId = cellWidget.cell.row.id;
			    var row = dijit.byId('grid').store.get(rowId);
			    if (row.Id === undefined) {
				var select = new FilteringSelect({
				    store : locationStore,
				    searchAttr : "id",
				    value : _self.baseLocation,
				    placeHolder : "Select Location",
				    style : "width: 100%; margin-left : auto"
				});
				if (cellWidget.location.childElementCount === 0) {
				    select.placeAt(cellWidget.location);
				}
			    } else {
				cellWidget.location.innerHTML = gridData;
			    }
			},

			upperCase : function(list) {
			    for ( var i in list) {
				for ( var data in list[i]) {
				    if (typeof (list[i][data]) === 'string') {
					list[i][data] = list[i][data].toUpperCase().trim();
				    }

				}
			    }
			    return list;
			},

			gridUpperCase : function(list) {
			    for ( var i in list) {
				for ( var data in list[i]) {
				    if (typeof (list[i][data]) === 'string') {
					if (data !== "HF_DocumentType" && data !== "HF_Description") {
					    list[i][data] = list[i][data].toUpperCase().trim();
					}

				    }

				}
			    }
			    return list;
			},

			checkInDocument : function(gridData, storeData, cellWidget) {
			    if (cellWidget.getChildren().length !== 0) {
				cellWidget.checkInBtn.removeChild(cellWidget.checkInBtn.childNodes[0]); // remove
				// existing
				// childNodes
				// in
				// cellWidget
			    }
			    if (storeData !== undefined) {
				var docId = storeData;
				var rowId = cellWidget.cell.row.id;
				var columnId = cellWidget.cell.column.id - 1;
				var myDialog = new ConfirmDialog(
					{
					    title : "Update Document",
					    buttonCancel : "Cancel",
					    buttonOk : "Save",
					    content : '<form data-dojo-attach-point="_fileInputForm" name="fileUploadForm" enctype="multipart/form-data" accept-charset="UTF-8" method="post" target="'
						    + docId
						    + '_fileInputIFrame">'
						    + 'Updated Document :    '
						    + '<input type="file" required="true" id="'
						    + docId
						    + '" name="uploadFile" class="fileInput" data-dojo-attach-point="_fileInput"/>'
						    + '<iframe name="'
						    + docId
						    + '_fileInputIFrame" id="'
						    + docId
						    + '_fileInputIFrame" style="display: none"></iframe> </form>',
					    onCancel : function() {
					    },
					    onExecute : function() {
						var reqParams = {
						    desktop : ecm.model.desktop.id,
						    repositoryId : ecm.model.desktop.getDefaultRepositoryId(),
						    docId : docId
						};

						// var callback =
						// lang.hitch(this,
						// self._onAddCompleted);

						// HTML5 browser
						if (this._firstFocusItem.files) {
						    var file = this._firstFocusItem.files[0];
						    reqParams.mimetype = file.type;
						    reqParams.parm_part_filename = file.name;
						    var fileform = new FormData();
						    fileform.append("file", file);
						    Request
							    .postFormToPluginService(
								    "GridxPlugin",
								    "HomeLoanUpdateDocumentService",
								    fileform,
								    {
									requestParams : reqParams,
									requestCompleteCallback : lang
										.hitch(
											this,
											function(response) {
											    // console.log(response);
											    if (response === undefined || response === null
												    || response === "" || response.message === "Fail") {
												var message = new ecm.widget.dialog.MessageDialog({
												    text : response.errorMessage
												});
												message.show();
											    } else {
												var addButton = _self._grid.cellWidget.getCellWidget(
													rowId, columnId);
												var repo_Id = ecm.model.desktop
													.getDefaultRepositoryId();
												var desktopId = ecm.model.desktop.id;
												var Url = window.location.protocol + "\/\/"
													+ window.location.host
													+ "/navigator/bookmark.jsp?desktop="
													+ desktopId + "&repositoryId=" + repo_Id
													+ "&repositoryType=p8&docid="
													+ response.docId;
												var newLink = "<a href=\"#\" onclick=\"window.open('"
													+ Url
													+ "','Viewer', 'width=600, height=600, resizable = yes')\" >View Document</a>";
												addButton.addBtn.innerHTML = newLink;
											    }
											})
								    });
						} else { // Non-HTML5 browser
						    var fileName = self._fileInput.value;
						    if (fileName && fileName.length > 0) {
							var i = fileName.lastIndexOf("\\");
							if (i != -1) {
							    fileName = fileName.substr(i + 1);
							}
						    }
						    reqParams.parm_part_filename = fileName;
						    reqParams.plugin = "GridxPlugin";
						    reqParams.action = "HomeLoanUpdateDocumentService";

						    Request.ieFileUploadServiceAPI("plugin", "", {
							requestParams : reqParams,
							requestCompleteCallback : lang.hitch(this, function(response) {
							    if (response.fieldErrors) {
								// console.dir(response.fieldErrors);
							    } else if (this._items && this._items.length > 0 && response && response.mimetype) {
								lang.mixin(this._items[0], response);
								this._items[0].refresh();
							    }
							    this.hide();
							})
						    }, dojo.byId('_fileInputForm'));
						}
					    }
					});
				var add = new Button({
				    label : "Update Document",
				    onClick : function() {
					myDialog.show();
				    }
				});
				if (cellWidget.checkInBtn.childElementCount === 0) {
				    add.placeAt(cellWidget.checkInBtn);
				}
			    } else {
				var add = new Button({
				    label : "Update Document",
				    disabled : true,
				    onClick : function() {
				    }
				});
				if (cellWidget.checkInBtn.childElementCount === 0) {
				    add.placeAt(cellWidget.checkInBtn);
				}
			    }
			},

			getFolderInfo : function(repository, path) {
			    var res = new Deferred();
			    repository.retrieveItem(path, lang.hitch(self, function(rootFolder) {
				repository.retrieveItem(path, lang.hitch(self, function(rootFolderOld) {
				    res.resolve(rootFolderOld);
				}), null, null, null, null, null);
			    }), null, null, null, null, null);
			    return res.promise;
			},

			createSelectStore : function(data) {
			    // Make the items unique
			    var res = {};
			    for (var i = 0; i < data.length; ++i) {
				res[data[i]] = 1;
			    }
			    data = [];
			    for ( var d in res) {
				data.push({
				    id : d
				});
			    }
			    return new Memory({
				data : data
			    });
			},

			getUserDetails : function() {
			    try {
				var userId = ecm.model.desktop.userId;
				var serviceName = "getUserDetails";
				var servicetype = "GET";
				var handler = "json";
				var modifiedItems = "";
				var parameterNames = 0 || [ "srNo" ];
				var parameterValues = 0 || [ userId ];
				var response = _self.getRestCall(serviceName, handler, servicetype, parameterNames, parameterValues, modifiedItems);
				if (response && response.status === "Success") {
				    _self.userDetails = response;
				    var docListResponse = _self.docList;

				    if (response.roleAccess.FN_DMS_ViewProperties === "ALL" || response.roleAccess.FN_DMS_ViewProperties === "All") {
					_self.docs = _self.docList;
				    } else {
					_self.docs = response.roleAccess.FN_DMS_ViewProperties[0];
					var arr = Object.keys(_self.docs);
					for (var j = 0; j < arr.length; j++) {
					    var docSubTypeOfDocType = _self.docs[arr[j]];
					    if (docSubTypeOfDocType === "ALL" || docSubTypeOfDocType === "All") {
						var key = Object.keys(_self.docs)[j];
						_self.docs[key] = docListResponse[key];
					    }
					}
				    }

				    var keys = Object.keys(_self.docs);

				    var docType = 0 || [];

				    var subType = 0 || [];

				    for ( var element in keys) {
					var doc = {};
					doc.name = keys[element];
					doc.id = keys[element];
					docType.push(doc);
					var _docSubArray = _self.docs[keys[element]];
					for ( var _element in _docSubArray) {
					    var _subType = {};
					    var __element = _docSubArray[_element];
					    for ( var x in __element) {
						_subType["name"] = x;
						_subType["id"] = __element[x];
					    }
					    subType.push(_subType);
					}
					;
				    }
				    ;

				    subType.sort(function(a, b) {
					if (a.name > b.name) {
					    return 1;
					} else {
					    return -1;
					}
				    });

				    _self.docTypes = docType;

				    _self.docSubTypes = _self.upperCase(subType);

				    _self.baseLocation = response.baseLocation;

				    _self.modifyList = _self.checkAccessList(response.roleAccess.FN_DMS_Modify);

				    _self.deleteList = _self.checkAccessList(response.roleAccess.FN_DMS_Delete);

				    _self.viewDocumentList = _self.checkAccessList(response.roleAccess.FN_DMS_View);

				    _self.downloadList = _self.checkAccessList(response.roleAccess.FN_DMS_Download);

				    _self.createList = _self.checkAccessList(response.roleAccess.FN_DMS_Create);
				}

			    } catch (Error) {
				console.error("User Details fetching failed");
				console.error(Error);
			    }
			},

			getDocumentList : function() {
			    try {
				var serviceName = "getDocList";
				var servicetype = "GET";
				var handler = "json";
				var modifiedItems = "";
				var parameterNames = 0 || [];
				var parameterValues = 0 || [];
				_self.docList = _self.getRestCall(serviceName, handler, servicetype, parameterNames, parameterValues, modifiedItems);
				_self.upperCase(_self.docList);
			    } catch (Error) {
				console.error("DocumentList fetching failed");
				console.error(Error);
			    }
			},

			getDocumentType : function(documentSubType, self) {
			    var rowId = self.getParent().cell.row.id;
			    var fieldName = _self._grid.structure[self.getParent().cell.column.id - 1].field;
			    var docList = _self.docList;
			    var row = dijit.byId('grid').store.data[rowId];
			    if (_self._store.data[rowId] === undefined) {
				for ( var docType in docList) {
				    var subDocType = docList[docType];
				    for ( var i in subDocType) {
					if (Object.keys(subDocType[i]).map(function(item) {
					    return subDocType[i][item];
					})[0] === documentSubType) {
					    if (_self.createList.indexOf(documentSubType) !== -1) {
						row["HF_DocumentType"] = docType;
						return true;
					    } else {
						var myDialog = new ecm.widget.dialog.MessageDialog({
						    text : "User do not have access to create Document Type : " + docType
						});
						myDialog.show();

						return false;
					    }

					}
				    }
				}
			    } else {
				var oldValue = _self._store.data[rowId][fieldName];
				if (oldValue === null || oldValue === "") {
				    _self.updateProperties(documentSubType, self);
				    return true;
				} else if (oldValue !== documentSubType) {
				    self.setValue(oldValue);
				    var myDialog = new ecm.widget.dialog.MessageDialog({
					text : _self._grid.structure[self.getParent().cell.column.id - 1].name + " Cannot be Modified",
				    });
				    myDialog.show();
				} else {

				}
				return false;
			    }

			    return false;
			},

			setCIFID : function(customer, self) {
			    var rowId = self.getParent().cell.row.id;
			    var row = dijit.byId('grid').store.data[rowId];
			    if (_self._store.data[rowId] === undefined) {
				var cifid = _self.getCifId(customer);
				row["HF_CIFID"] = cifid;
				row["HF_CustomerName"] = customer;
			    }
			},

			updateProperties : function(newValue, self) {
			    var updateList = _self._updateList;
			    var rowId = self.getParent().cell.row.id;
			    var fieldName = _self._grid.structure[self.getParent().cell.column.id - 1].field;
			    if (_self._store.data.length >= rowId && _self._store.data[rowId] !== undefined) {
				var oldValue = _self._store.data[rowId][fieldName];
				var docTypeArray = 0 || [];
				if (_self.userDetails.roleAccess.FN_DMS_Modify !== undefined) {
				    if (_self.userDetails.roleAccess.FN_DMS_Modify !== "All") {
					for ( var i in _self.userDetails.roleAccess.FN_DMS_Modify[0]) {
					    docTypeArray.push(i);
					}
				    } else {
					for ( var i in _self.docTypes) {
					    docTypeArray.push(_self.docTypes[i].id);
					}
				    }
				}
				var docType = _self._store.data[rowId]["HF_DocumentType"];
				var docSubTypeName = _self.checkAccessNameList(_self.userDetails.roleAccess.FN_DMS_Modify);
				var docSubType = _self._grid.store.data[rowId].HF_DocumentSubType;
				var keyIndex = _self.modifyList.indexOf(docSubType);
				var valueIndex = docSubTypeName.indexOf(docSubType);
				if ((keyIndex !== -1 || valueIndex !== -1)
					|| (docTypeArray.indexOf(docType) !== -1 && (oldValue === null || oldValue === ""))) {
				    if (oldValue !== newValue) {

					if (updateList.length === 0) {
					    var jsonObject = {
						rowId : rowId
					    };
					    jsonObject[fieldName] = newValue;
					    jsonObject["Id"] = _self._store.data[rowId]["Id"];
					    updateList.push(jsonObject);
					} else {
					    var rowPresent = false;
					    for ( var i in updateList) {
						var jsonObject = updateList[i];
						if (jsonObject["rowId"] === rowId) {
						    jsonObject[fieldName] = newValue;
						    rowPresent = true;
						}
					    }
					    if (!rowPresent) {
						var jsonObject = {
						    rowId : rowId
						};
						jsonObject[fieldName] = newValue;
						jsonObject["Id"] = _self._store.data[rowId]["Id"];
						updateList.push(jsonObject);
					    }
					}

				    } else {
					if (updateList.length !== 0) {
					    for ( var i in updateList) {
						var jsonObject = updateList[i];
						if (jsonObject["rowId"] === rowId) {
						    console.log(jsonObject);
						    if (jsonObject[fieldName] !== undefined) {
							console.log("Deleting : " + jsonObject[fieldName]);
							delete jsonObject[fieldName];
						    }
						}
					    }
					}
				    }
				} else if ((oldValue !== null || oldValue !== "") && (oldValue !== newValue)) {
				    self.setValue(oldValue);
				    var myDialog = new ecm.widget.dialog.MessageDialog({
					text : "Access is denied",
				    });
				    myDialog.show();
				}
			    }

			},

			getContentListGridModules : function() {
			    var array = 0 || [];
			    array.push(DndRowMoveCopy);
			    array.push(RowContextMenu);
			    return array;
			},

			getContentListModules : function() {
			    var viewModules = 0 || [];
			    viewModules.push(ViewDetail);
			    var array = 0 || [];
			    array.push({
				moduleClass : Bar,
				top : [ [ [ {
				    moduleClass : Toolbar
				}, {
				    moduleClasses : viewModules,
				    "className" : "BarViewModules"
				} ] ] ],
				bottom : [ [ [ {
				    moduleClass : TotalCount
				} ] ] ]
			    });
			    return array;
			},

			getLoanDetails : function() {
			    var filter;
			    with (dojo.byId('searchForm'))
				with (elements[0])
				    with (elements[checked ? 0 : 1]) {
					filter = value;
				    }
			    ;
			    try {
				var serviceName = "getLoanDtl";
				var servicetype = "GET";
				var handler = "json";
				var modifiedItems = "";
				var parameterNames = 0 || [ "number", "filter" ];
				var parameterValues = 0 || [ _self.customerId.value, filter ];
				_self.loanDetails = _self.getRestCall(serviceName, handler, servicetype, parameterNames, parameterValues,
					modifiedItems);
			    } catch (Error) {
				console.error("Loan Details fetching failed");
				console.error(Error);
			    }

			    if (_self.loanDetails.status !== "Success") {
				return;
			    }

			    if (_self.loanDetails.srcApplId === null) {
				_self.applicationNumber.style.display = 'none';
				_self.applicationNumberLabel.style.display = 'none';
			    } else {
				_self.applicationNumber.innerText = _self.loanDetails.srcApplId;
				_self.applicationNumber.style.display = 'flex';
				_self.applicationNumberLabel.style.display = 'flex';
			    }

			    if (_self.loanDetails.loanNo === null) {
				_self.loanNumber.style.display = 'none';
				_self.loanNumberLabel.style.display = 'none';
			    } else {
				_self.loanNumber.innerText = _self.loanDetails.loanNo;
				_self.loanNumber.style.display = 'flex';
				_self.loanNumberLabel.style.display = 'flex';

			    }

			    var customerDetails = _self.loanDetails.custDetails;

			    var customerList = 0 || [];

			    for ( var i in customerDetails) {
				var customer = {};
				customer.name = customerDetails[i].name;
				if (customerDetails[i].type === "P") {
				    _self.primaryCustomer = customerDetails[i].name;
				}
				customer.id = customerDetails[i].name;
				customerList.push(customer);
			    }

			    _self.customerList = customerList;

			},

			encoding : function(text) {
			    var bytes = 0 || [];
			    for (var i = 0; i < text.length; i++) {
				var realBytes = unescape(encodeURIComponent(text[i]));
				for (var j = 0; j < realBytes.length; j++) {
				    bytes.push(realBytes[j].charCodeAt(0));
				}
			    }
			    return base64.encode(bytes);
			},

			decoding : function(text) {
			    var decoded = base64.decode(text);
			    var decodedString = String.fromCharCode.apply(null, decoded);

			    return decodedString;

			},

			getCifId : function(customerName) {
			    var customerDetails = _self.loanDetails.custDetails;
			    for ( var i in customerDetails) {
				if (customerDetails[i].name === customerName) {
				    return customerDetails[i].cifId;
				}
			    }
			},

			checkAccessList : function(list) {
			    var docSubTypeId = 0 || [];

			    var docList = _self.docList;

			    if (list !== undefined) {
				if (list === "All") {
				    for ( var i in docList) {
					for ( var j in docList[i]) {
					    Object.keys(docList[i][j]).map(function(item) {
						docSubTypeId.push(docList[i][j][item]);
					    });
					}
				    }
				} else {
				    for ( var i in list[0]) {
					if (list[0][i] === "All") {
					    for ( var j in docList[i]) {
						Object.keys(docList[i][j]).map(function(item) {
						    docSubTypeId.push(docList[i][j][item]);
						});
					    }
					} else {
					    for ( var j in list[0][i]) {
						Object.keys(list[0][i][j]).map(function(item) {
						    docSubTypeId.push(list[0][i][j][item]);
						});
					    }
					}

				    }
				}
			    }

			    return docSubTypeId;
			},

			checkAccessNameList : function(list) {
			    var docSubTypeId = 0 || [];

			    var docList = _self.docList;

			    if (list !== undefined) {
				if (list === "All") {
				    for ( var i in docList) {
					for ( var j in docList[i]) {
					    docSubTypeId.push(Object.keys(docList[i][j]).toString());
					}
				    }
				} else {
				    for ( var i in list[0]) {
					if (list[0][i] === "All") {
					    for ( var j in docList[i]) {
						docSubTypeId.push(Object.keys(docList[i][j]).toString());
					    }
					} else {
					    for ( var j in list[0][i]) {
						docSubTypeId.push(Object.keys(list[0][i][j]).toString());
					    }
					}

				    }
				}
			    }

			    return docSubTypeId;
			},

			closeAllViewers : function() {
			    var contentViewer = registry.byId('contentV');
			    if (contentViewer !== undefined) {
				if (contentViewer.splitTabContainer !== null) {

				    contentViewer.splitTabContainer.unloadViewers();

				}

				contentViewer.mainTabContainer.unloadViewers();
			    }
			},

			getRestCall : function(serviceName, handler, serviceType, parameterNames, parameterValues, postdata) {
			    var filterData = "";
			    var parameters = "";
			    for (var i = 0; i < parameterNames.length; i++) {
				if (i === 0) {
				    parameters = "/?" + parameterNames[i] + "=" + parameterValues[i];
				} else {
				    parameters = parameters + "&&" + parameterNames[i] + "=" + parameterValues[i];
				}
			    }
			    var serverBase = _self.baseURL;
			    var feedURL = serverBase + serviceName + parameters;
			    var xhrArgs = {
				url : feedURL,
				handleAs : handler,
				sync : true,
				preventCache : true,
				headers : {
				    "Content-Type" : "application/json"
				},
				load : function(data) {
				    filterData = data;
				},
				error : function(error) {

				}
			    };
			    if (serviceType === "GET") {
				dojo.xhrGet(xhrArgs);
			    } else {
				xhrArgs.postData = postdata;
				dojo.xhrPost(xhrArgs);
			    }
			    return filterData;
			},

		    });
	});

