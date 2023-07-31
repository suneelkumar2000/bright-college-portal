(function ($) {
	/***
	 * A sample AJAX data store implementation.
	 * Right now, it's hooked up to load Hackernews stories, but can
	 * easily be extended to support any JSONP-compatible backend that accepts paging parameters.
	 */
	function RemoteModel() {
	  // private
	  let PAGESIZE = 50;
	  let data = {length: 0};
	  let searchstr = "";
	  let sortcol = null;
	  let sortdir = 1;
	  let h_request = null;
	  let req = null; // ajax request
  
	  // events
	  let onDataLoading = new Slick.Event();
	  let onDataLoaded = new Slick.Event();
  
  
	  function init() {
	  }
  
  
	  function isDataLoaded(from, to) {
		for (let i = from; i <= to; i++) {
		  if (data[i] == undefined || data[i] == null) {
			return false;
		  }
		}
  
		return true;
	  }
  
  
	  function clear() {
		for (let key in data) {
		  delete data[key];
		}
		data.length = 0;
	  }
  
  
	  function ensureData(from, to) {
		if (req) {
		  req.abort();
		  for (let i = req.fromPage; i <= req.toPage; i++)
			data[i * PAGESIZE] = undefined;
		}
  
		if (from < 0) {
		  from = 0;
		}
  
		if (data.length > 0) {
		  to = Math.min(to, data.length - 1);
		}
  
		let fromPage = Math.floor(from / PAGESIZE);
		let toPage = Math.floor(to / PAGESIZE);
  
		while (data[fromPage * PAGESIZE] !== undefined && fromPage < toPage)
		  fromPage++;
  
		while (data[toPage * PAGESIZE] !== undefined && fromPage < toPage)
		  toPage--;
  
		if (fromPage > toPage || ((fromPage == toPage) && data[fromPage * PAGESIZE] !== undefined)) {
		  // TODO:  look-ahead
		  onDataLoaded.notify({from: from, to: to});
		  return;
		}
  
		let url = "http://api.thriftdb.com/api.hnsearch.com/items/_search?filter[fields][type][]=submission&q=" + searchstr + "&start=" + (fromPage * PAGESIZE) + "&limit=" + (((toPage - fromPage) * PAGESIZE) + PAGESIZE);
  
		if (sortcol != null) {
			url += ("&sortby=" + sortcol + ((sortdir > 0) ? "+asc" : "+desc"));
		}
  
		if (h_request != null) {
		  clearTimeout(h_request);
		}
  
		h_request = setTimeout(function () {
		  for (let i = fromPage; i <= toPage; i++)
			data[i * PAGESIZE] = null; // null indicates a 'requested but not available yet'
  
		  onDataLoading.notify({from: from, to: to});
  
		  req = $.jsonp({
			url: url,
			callbackParameter: "callback",
			cache: true,
			success: onSuccess,
			error: function () {
			  onError(fromPage, toPage)
			}
		  });
		  req.fromPage = fromPage;
		  req.toPage = toPage;
		}, 50);
	  }
  
  
	  function onError(fromPage, toPage) {
		alert("error loading pages " + fromPage + " to " + toPage);
	  }
  
	  function onSuccess(resp) {
		let from = resp.request.start, to = from + resp.results.length;
		data.length = Math.min(parseInt(resp.hits),1000); // limitation of the API
  
		for (let i = 0; i < resp.results.length; i++) {
		  let item = resp.results[i].item;
  
		  // Old IE versions can't parse ISO dates, so change to universally-supported format.
		  item.create_ts = item.create_ts.replace(/^(\d+)-(\d+)-(\d+)T(\d+:\d+:\d+)Z$/, "$2/$3/$1 $4 UTC"); 
		  item.create_ts = new Date(item.create_ts);
  
		  data[from + i] = item;
		  data[from + i].index = from + i;
		}
  
		req = null;
  
		onDataLoaded.notify({from: from, to: to});
	  }
  
  
	  function reloadData(from, to) {
		for (let i = from; i <= to; i++)
		  delete data[i];
  
		ensureData(from, to);
	  }
  
  
	  function setSort(column, dir) {
		sortcol = column;
		sortdir = dir;
		clear();
	  }
  
	  function setSearch(str) {
		searchstr = str;
		clear();
	  }
  
  
	  init();
  
	  return {
		// properties
		"data": data,
  
		// methods
		"clear": clear,
		"isDataLoaded": isDataLoaded,
		"ensureData": ensureData,
		"reloadData": reloadData,
		"setSort": setSort,
		"setSearch": setSearch,
  
		// events
		"onDataLoading": onDataLoading,
		"onDataLoaded": onDataLoaded
	  };
	}
  
	// Slick.Data.RemoteModel
	$.extend(true, window, { Slick: { Data: { RemoteModel: RemoteModel }}});
  })(jQuery);
  