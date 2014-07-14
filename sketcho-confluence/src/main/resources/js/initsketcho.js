var jq172 = jQuery.noConflict(true);

var cancelStream = jq172(window).asEventStream('keydown').filter(function(e) {
		return e.keyCode == 27 && 
					 e.ctrlKey != 1 && e.metaKey != 1 && e.shiftKey != 1 && e.altKey != 1
	})

  window.shiftDownStream = jq172(window).asEventStream('keydown').filter(function(e) {
    return e.keyCode == 16 && e.shiftKey == 1 && !isEditorOpen() && e.ctrlKey != 1 && e.metaKey != 1 && e.altKey != 1
  })

  window.shiftUpStream = jq172(window).asEventStream('keyup').filter(function(e) {
    return e.keyCode == 16 && !isEditorOpen() && e.ctrlKey != 1 && e.metaKey != 1 && e.altKey != 1
  })

  window.changeFreehandColorStream = Bacon.fromBinder(function(sink) {
    function push(event, data) {
      sink(data)
    }

    $(document).on('showFreehandColorMenu', push)

    return function() {
      $(document).off('showFreehandColorMenu', push)
    }
  })

	var newLibraryImageStream = Bacon.fromBinder(function(sink) {
    function push(event, data) {
      sink(data)
    }

    jq172(document).on('add-img-lib', push)

    return function() {
      jq172(document).off('add-img-lib', push)
    }
  })

  var deleteLibraryImageStream = Bacon.fromBinder(function(sink) {
  	function push(event, data) {
  		sink(data.deleted)
  	}

  	jq172(document).on('del-img-lib', push)

  	return function() {
  		jq172(document).off('del-img-lib', push)
  	}
  })

  var themeChangedStream = Bacon.fromBinder(function(sink) {
  	function push(event, themeName) {
  		sink(themeName)
  	}

  	jq172(document).on('theme-changed', push)

  	return function() {
  		jq172(document).off('theme-changed', push)
  	}
  })

  var mapViewStream = Bacon.fromBinder(function(sink) {
  	function push(event, data) {
  		sink(data)
  	}

  	jq172(document).on('map-view', push)

  	return function() {
  		jq172(document).off('map-view', push)
  	}
  }).map(function(v) {
  	if (v == 'start') {
  		return true
  	} else {
  		return false
  	}
  })


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
      svgViewer.loadAll()
			AJS.log("Configuring Sketchboard.Me... done");
	 	}
 	};
	window.onload = function() {
	    var d = document.getElementsByTagName("head")[0].appendChild(document.createElement('script'));
	    d.src = baseUrl2 + "js/dojo-release-1.7.2-custom/dojo/dojo.js";
	    d.type = "text/javascript";
	}

});
