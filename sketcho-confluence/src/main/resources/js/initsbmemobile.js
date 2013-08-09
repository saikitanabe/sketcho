//AJS.toInit(function () {
//	<script type="text/javascript" src="$pluginPath/js/sketcho_confluence_app/sketcho_confluence_app.nocache.js">
//	</script>
//
//	<script type="text/javascript" src="$pluginPath/js/dojo-release-1.3.2/dojo/dojo.js" djConfig="parseOnLoad:true,gfxRenderer:'silverlight'"></script>
//	<script type="text/javascript" src="$pluginPath/js/dojo-release-1.3.2/dojo/colors.js"></script>
//	console.log('JIHAA');
//});

//var context = document.getElementById("confluence-context-path").content;
//var path = context+"/download/resources/net.sevenscales.confluence.plugins.sketcho-confluence";
//var jspath = path+"/js/sketcho_confluence_app/sketcho_confluence_app.nocache.js"
//
//var fileref=document.createElement('script');
//fileref.setAttribute("type","text/javascript");
//fileref.setAttribute("src", jspath);
//
//document.getElementsByTagName("head")[0].appendChild(fileref);
//
//console.log(jspath);

// var jq172 = jQuery.noConflict(true);


ConfluenceMobile.contentEventAggregator.on("displayed", function() {
	AJS.log("Configuring Sketchboard.Me...");
	
	dojo.require("dojox.gfx");
	dojo.require("dojox.color");
	
	AJS.log("Configuring Sketchboard.Me... done");
});

var sketchoEditors = new Array();
function addOnLoadEditor(editor) {
	sketchoEditors.push(editor);
}
function sketchoEditorLoaded() {
	// AJS.log(sketchoEditors);
	for (var i = 0; i < sketchoEditors.length; ++i) {
    // AJS.log(sketchoEditors[i].spaceId+' '+sketchoEditors[i].name);
		loadSketch(sketchoEditors[i].spaceId, sketchoEditors[i].pageId, sketchoEditors[i].name, sketchoEditors[i].editable);
	}
}


//dojo.require("dojo.parser");
//
//dojo.addOnLoad(function(){
//        dojo.parser.parse(dojo.body());
//});

