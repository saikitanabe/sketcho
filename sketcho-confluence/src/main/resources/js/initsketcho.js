var jq172 = jQuery.noConflict(true);

AJS.toInit(function () {
	AJS.log("Configuring Sketchboard.Me...");

	var baseUrl2 = AJS.$("meta[name='sketchboard-me-plugin-url']").attr("content");
	AJS.log("Configuring Sketchboard.Me baseUrl2 " + baseUrl2 + " ...");

 	dojoConfig = { 
 		afterOnLoad:true,
	    baseUrl: baseUrl2 + "js/dojo-release-1.7.2-custom/dojo",
	 	addOnLoad: function() {
	 		AJS.log("Configuring Sketchboard.Me dojo.js... loaded");
	 		AJS.log("Configuring Sketchboard.Me dojo.js baseUrl " + dojo.config.baseUrl + "...");
	 		require(["dojox/gfx", "dojox/color"]);
			AJS.log("Configuring Sketchboard.Me... done");
	 	}
 	};
	window.onload = function() {
	    var d = document.getElementsByTagName("head")[0].appendChild(document.createElement('script'));
	    d.src = baseUrl2 + "js/dojo-release-1.7.2-custom/dojo/dojo.js";
	    d.type = "text/javascript";
	}

});
