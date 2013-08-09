///////////////////////////////////////////////////////////////////
function debugLog(text) {
	var e = document.getElementById("debug_holder");
	if (e) {
		e.appendChild(document.createTextNode(text + " "));
	}
}

function onError(msg, href, lineNo) {
    debugLog(msg + " " + href + " " + lineNo);
}

window.onerror = onError;
