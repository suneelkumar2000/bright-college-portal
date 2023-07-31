if (typeof console === "undefined" || typeof console.log === "undefined") {
	let names = ["log", "debug", "info", "warn", "error", "assert", "dir", "dirxml",
		"group", "groupEnd", "time", "timeEnd", "count", "trace", "profile", "profileEnd"];
  
	window.console = {};
	for (let i = 0; i < names.length; ++i) {
	  window.console[names[i]] = function() {}
	}
  }