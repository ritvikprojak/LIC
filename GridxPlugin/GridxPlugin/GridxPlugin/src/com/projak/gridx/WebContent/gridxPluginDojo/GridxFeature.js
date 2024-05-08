define([ "dojo/_base/declare", "dojo/store/Memory", "dijit/form/Form", 'dijit/form/TextBox', 'dijit/form/ComboBox', 'dijit/form/DateTextBox',
	'dijit/form/TimeTextBox', 'dijit/form/NumberTextBox', 'dijit/form/FilteringSelect', 'dijit/form/SimpleTextarea', 'dijit/form/Textarea',
	'dijit/form/Select', 'dijit/form/HorizontalSlider', 'dijit/form/NumberSpinner', 'dijit/form/CheckBox', 'dijit/form/ToggleButton',
	'dijit/Calendar', 'dijit/ColorPalette', "dijit/Menu", "ecm/widget/layout/_LaunchBarPane", "ecm/widget/listView/gridModules/RowContextMenu",
	"gridx/core/model/cache/Sync", "gridx/Grid", "gridxPluginDojo/MusicData", "gridxPluginDojo/StoreFactory", "gridx/modules/CellWidget",
	"gridx/modules/Edit", "gridx/modules/Pagination", "gridx/modules/Menu", "gridx/modules/select/Row", "gridx/modules/pagination/PaginationBar",
	"dojo/text!./templates/GridxFeature.html", ], function(declare, Memory, Form, TextBox, ComboBox, DateTextBox, TimeTextBox, NumberTextBox,
	FilteringSelect, SimpleTextarea, Textarea, Select, HorizontalSlider, NumberSpinner, CheckBox, ToggleButton, Calendar, ColorPalette, Menu,
	_LaunchBarPane, RowContextMenu, Cache, Grid, MusicData, StoreFactory, CellWidget, Edit, Pagination, Menu, Row, PaginationBar, template) {
    /**
     * @name gridxPluginDojo.GridxFeature
     * @class
     * @augments ecm.widget.layout._LaunchBarPane
     */
    return declare("gridxPluginDojo.GridxFeature", [ _LaunchBarPane ], {
	/** @lends gridxPluginDojo.GridxFeature.prototype */

	templateString : template,

	// Set to true if widget template contains DOJO widgets.
	widgetsInTemplate : true,

	postCreate : function() {
	    console.log("GridxFeature Loading...");
	    this.logEntry("postCreate");
	    this.inherited(arguments);

	    mystore = StoreFactory({
		dataSource : MusicData,
		size : 200
	    });

	    fsStore = this.createSelectStore('Album');
	    selectStore = this.createSelectStore('Length');

	    // Dijit edit grid
	    store1 = StoreFactory({
		dataSource : MusicData,
		size : 100
	    });

	    layout1 = [
		    {
			field : "id",
			name : "ID",
			width : '200px'
		    },
		    {
			field : "Color",
			name : "Color Palatte",
			width : '205px',
			editable : true,
			decorator : function(data) {
			    return [ '<div style="display: inline-block; border: 1px solid black; ', 'width: 20px; height: 20px; background-color: ',
				    data, '"></div>', data ].join('');
			},
			editor : 'dijit/ColorPalette',
			editorArgs : {
			    fromEditor : function(v, cell) {
				return v || cell.data(); // If
				// no
				// color
				// selected,
				// use
				// the
				// orginal
				// one.
			    }
			}
		    }, {
			field : "Genre",
			name : "TextBox",
			width : '200px',
			alwaysEditing : true,
			editable : true,
			editor : 'dijit/form/TextBox'
		    }, {
			field : "Genre",
			name : "Textarea(with editorIgnoresEnter:true)",
			width : '200px',
			editable : true,
			editor : 'dijit/form/Textarea',
			editorIgnoresEnter : true
		    }, {
			field : "Artist",
			name : "ComboBox",
			width : '200px',
			editable : true,
			editor : "dijit/form/ComboBox",
			editorArgs : {
			    props : 'store: mystore, searchAttr: "Artist"'
			}
		    }, {
			field : "Year",
			name : "NumberTextBox",
			width : '200px',
			editable : true,
			alwaysEditing : true,
			editor : "dijit.form.NumberTextBox"
		    }, {
			field : "Album",
			name : "FilteringSelect",
			width : '200px',
			editable : true,
			alwaysEditing : true,
			editor : FilteringSelect,
			editorArgs : {
			    props : 'store: fsStore, searchAttr: "id"'
			}
		    }, {
			field : "Length",
			name : "Select",
			width : '200px',
			editable : true,
			alwaysEditing : true,
			// FIXME: this is still buggy, hard to
			// set width
			editor : Select,
			editorArgs : {
			    props : 'store: selectStore, labelAttr: "id"'
			}
		    }, {
			field : "Progress",
			name : "HorizontalSlider",
			width : '200px',
			editable : true,
			alwaysEditing : true,
			editor : "dijit/form/HorizontalSlider",
			editorArgs : {
			    props : 'minimum: 0, maximum: 1'
			}
		    }, {
			field : "Track",
			name : "Number Spinner",
			width : '200px',
			alwaysEditing : true,
			editable : true,
			width : '200px',
			editor : "dijit/form/NumberSpinner"
		    }, {
			field : "Heard",
			name : "Check Box",
			width : '200px',
			editable : true,
			alwaysEditing : true,
			editor : "dijit.form.CheckBox",
			editorArgs : {
			    props : 'value: true'
			}
		    }, {
			field : "Heard",
			name : "ToggleButton",
			width : '200px',
			editable : true,
			alwaysEditing : true,
			editor : "dijit.form.ToggleButton",
			editorArgs : {
			    valueField : 'checked',
			    props : 'label: "Press me"'
			}
		    }, {
			field : "Download Date",
			name : "Calendar",
			width : '200px',
			editable : true,
			dataType : 'date',
			storePattern : 'yyyy/M/d',
			gridPattern : 'yyyy/MMMM/dd',
			editor : 'dijit/Calendar',
			editorArgs : {
			    fromEditor : getDate
			}
		    }, {
			field : "Download Date",
			name : "DateTextBox",
			width : '200px',
			editable : true,
			alwaysEditing : true,
			dataType : 'date',
			storePattern : 'yyyy/M/d',
			gridPattern : 'yyyy--MM--dd',
			editor : DateTextBox,
			editorArgs : {
			    fromEditor : getDate
			}
		    },
		    // FIXME: this is still buggy, can not TAB
		    // out.
		    // { field: "Composer", name:"Editor",
		    // width: '200px', editable: true,
		    // editor: "dijit/Editor"
		    // },
		    {
			field : "Last Played",
			name : "TimeTextBox",
			width : '200px',
			editable : true,
			dataType : "time",
			storePattern : 'HH:mm:ss',
			formatter : 'hh:mm a',
			editor : TimeTextBox,
			editorArgs : {
			    fromEditor : getTime
			}
		    } ];

	    var grid = Grid({
		id : 'grid',
		cacheClass : Cache,
		store : store1,
		structure : layout1,
		selectRowTriggerOnCell : true,
		modules : [ CellWidget, Edit, Pagination, PaginationBar, Menu, Row ]
	    });

	    grid.placeAt(this.gridContainer);

	    var menu = dijit.byId('headerMenu');

	    grid.menu.bind(menu, {
		hookPoint : "row",
		selected : false
	    });

	    grid.startup();

	    function getDate(d) {
		res = locale.format(d, {
		    selector : 'date',
		    datePattern : 'yyyy/M/d'
		});
		return res;
	    }
	    function getTime(d) {
		res = locale.format(d, {
		    selector : 'time',
		    timePattern : 'hh:mm:ss'
		});
		return res;
	    }

	    this.logExit("postCreate");

	},

	/**
	 * Optional method that sets additional parameters when the user clicks
	 * on the launch button associated with this feature.
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
	 * Loads the content of the pane. This is a required method to insert a
	 * pane into the LaunchBarContainer.
	 */
	loadContent : function() {
	    this.logEntry("loadContent");

	    if (!this.isLoaded) {
		/**
		 * Add custom load logic here. The LaunchBarContainer widget
		 * will call this method when the user clicks on the launch
		 * button associated with this feature.
		 */
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
	     * This is an option method that allows you to force the
	     * LaunchBarContainer to reset when the user clicks on the launch
	     * button associated with this feature.
	     */
	    this.needReset = false;

	    this.logExit("reset");
	},

	createSelectStore : function(field) {
	    var data = MusicData.getData(100).items;
	    // Make the items unique
	    var res = {};
	    for (var i = 0; i < data.length; ++i) {
		res[data[i][field]] = 1;
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

	fetchData : function() {
	    var query = "";
	    // console.log("Query is ------ "+query);

	    var repository = Desktop.getRepository(repo_Id);
	}

    });
});
