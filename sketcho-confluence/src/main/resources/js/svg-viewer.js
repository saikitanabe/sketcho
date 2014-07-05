var svgViewer = (function() {
	var svgViewer = {}

	function loadSvg(svgAttachment) {
		// $('#svgfile').load('Sample.svg'})
	}

	function loadAll(pageId) {
		console.log('loadAll...')
		$('.sketcho-svg-viewer').each(function(index) {
			// var name = _.filter($(this).attr("class").split(/\s+/), function(c) {
			// 	return c !== 'sketcho-svg-viewer'
			// })

			var modelName = $(this).attr("data-model-name")
			var pageId = $(this).attr("data-page-id")

			if (modelName && pageId) {
				console.log('name: ' + modelName)
				console.log('restServicePath: ' + restServicePath.value)

				$.getJSON(restServicePath.value + pageId + "%3A" + modelName + ".json", function(data) {
					// console.log(data)
					var svg = gwtModelToSvg(data)
					var $svg = $(svg)
					$('.' + modelName).each(function(index) {
						$(this).html($svg)
					})
					// console.log("svg: " + svg)
				})
				// TODO
				// - URL validation http https
			}
		})
	}

	svgViewer.loadSvg = loadSvg
	svgViewer.loadAll = loadAll

	return svgViewer
}())