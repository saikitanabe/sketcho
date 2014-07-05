var svgViewer = (function() {
	var svgViewer = {dojoReady: false}

	function loadSvg(svgAttachment) {
		// $('#svgfile').load('Sample.svg'})
	}

	function loadModel(modelName, pageId, width) {
		if (modelName && pageId) {
			// console.log('name: ' + modelName)
			// console.log('restServicePath: ' + restServicePath.value)

			$.getJSON(restServicePath.value + pageId + "%3A" + modelName + ".json", function(data) {
				// console.log(data)
				data.width = width
				gwtModelToSvg(data, function(svg) {
					// console.log("svg: " + svg)
					$('.' + modelName).each(function(index) {
						// need to create a separate DOM even if same
						var $svg = $(svg)
						$(this).html($svg)
					})
				})
				// console.log("svg: " + svg)
			})
			// TODO
			// - URL validation http https
		}
	}

	function _loadAll() {
		// console.log("_loadAll...")
		$('.sketcho-svg-viewer').each(function(index) {
			var width = $(this).width()
			var modelName = $(this).attr("data-model-name")
			var pageId = $(this).attr("data-page-id")
			loadModel(modelName, pageId, width)
		})
	}

	var renderSvg = _.after(2, _loadAll);
	function loadAll() {
		// console.log('loadAll...')
		renderSvg()
		// renderSvg is run once, after gwt module and dojo lib have been loaded
		// this is due to different load order Firefox and Chrome are having
		// on Chrome dojo is loaded before GWT module, on Firefox vice versa
	}

	function dojoLoaded() {
		svgViewer.dojoReady = true
	}

	var lazyLayout = _.debounce(_loadAll, 300)
	$(window).resize(lazyLayout)

	svgViewer.loadSvg = loadSvg
	svgViewer.loadAll = loadAll
	svgViewer.dojoLoaded = dojoLoaded
	svgViewer.isDojoReady = svgViewer.dojoReady

	window.sketchUpdated = function(modelName) {
		console.log("modelName..." + modelName)
		$('.sketcho-svg-viewer.' + modelName).each(function(index) {
			var pageId = $(this).attr("data-page-id")
			var width = $(this).width()
			loadModel(modelName, pageId, width)
		})
	}

	return svgViewer
}())