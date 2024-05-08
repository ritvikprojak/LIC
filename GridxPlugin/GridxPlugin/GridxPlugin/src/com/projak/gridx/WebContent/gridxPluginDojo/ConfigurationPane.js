define([
		"dojo/_base/declare",
		"dijit/_TemplatedMixin",
		"dijit/_WidgetsInTemplateMixin",
		"ecm/widget/admin/PluginConfigurationPane",
		"dojo/text!./templates/ConfigurationPane.html"
	],
	function(declare, _TemplatedMixin, _WidgetsInTemplateMixin, PluginConfigurationPane, template) {

		return declare("GridxPluginDojo.ConfigurationPane", [ PluginConfigurationPane, _TemplatedMixin, _WidgetsInTemplateMixin], {
		
		templateString: template,
		widgetsInTemplate: true,
	
		load: function(callback) {
			if(this.configurationString){
                var jsonConfig = JSON.parse(this.configurationString);
                this.dropdown.set('value',jsonConfig.configuration[0].value);
                this.api.set('value',jsonConfig.configuration[1].value)
                //this.searchTemplate.set('value',jsonConfig.configuration[1].value);
                //this.cmodFolder.set('value', jsonConfig.configuration[2].value);
            }
		},
		
        _onParamChange: function() {
            var configArray = new Array();
            var configString = {name: "dropdown", value: this.dropdown.get('value')};
            configArray.push(configString);
            configString = {name: "api", value: this.api.get('value')};
            configArray.push(configString);
            //configString = {name: "searchTemplate", value: this.searchTemplate.get('value')};
           // configArray.push(configString);
            //configString = {name: "cmodFolder", value: this.cmodFolder.get('value')};
            //configArray.push(configString);
            
            var configJson = { "configuration" : configArray};
            
            this.configurationString = JSON.stringify(configJson);
            this.onSaveNeeded(true);
        },
		
		validate: function() {
			return true;
		}
	});
});
