(function ($) {
	function SlickGridPager(dataView, grid, $container) {
	  let $status;
  
	  function init() {
		dataView.onPagingInfoChanged.subscribe(function (e, pagingInfo) {
		  updatePager(pagingInfo);
		});
  
		constructPagerUI();
		updatePager(dataView.getPagingInfo());
	  }
  
	  function getNavState() {
		let cannotLeaveEditMode = !Slick.GlobalEditorLock.commitCurrentEdit();
		let pagingInfo = dataView.getPagingInfo();
		let lastPage = pagingInfo.totalPages - 1;
  
		return {
		  canGotoFirst: !cannotLeaveEditMode && pagingInfo.pageSize != 0 && pagingInfo.pageNum > 0,
		  canGotoLast: !cannotLeaveEditMode && pagingInfo.pageSize != 0 && pagingInfo.pageNum != lastPage,
		  canGotoPrev: !cannotLeaveEditMode && pagingInfo.pageSize != 0 && pagingInfo.pageNum > 0,
		  canGotoNext: !cannotLeaveEditMode && pagingInfo.pageSize != 0 && pagingInfo.pageNum < lastPage,
		  pagingInfo: pagingInfo
		}
	  }
  
	  function setPageSize(n) {
		dataView.setRefreshHints({
		  isFilterUnchanged: true
		});
		dataView.setPagingOptions({pageSize: n});
	  }
  
	  function gotoFirst() {
		if (getNavState().canGotoFirst) {
		  dataView.setPagingOptions({pageNum: 0});
		}
	  }
  
	  function gotoLast() {
		let state = getNavState();
		if (state.canGotoLast) {
		  dataView.setPagingOptions({pageNum: state.pagingInfo.totalPages - 1});
		}
	  }
  
	  function gotoPrev() {
		let state = getNavState();
		if (state.canGotoPrev) {
		  dataView.setPagingOptions({pageNum: state.pagingInfo.pageNum - 1});
		}
	  }
  
	  function gotoNext() {
		let state = getNavState();
		if (state.canGotoNext) {
		  dataView.setPagingOptions({pageNum: state.pagingInfo.pageNum + 1});
		}
	  }
  
	  function constructPagerUI() {
		$container.empty();
  
		let $nav = $("<span class='slick-pager-nav' />").appendTo($container);
		let $settings = $("<span class='slick-pager-settings' />").appendTo($container);
		$status = $("<span class='slick-pager-status' />").appendTo($container);
  
		$settings
			.append("<span class='slick-pager-settings-expanded' style='display:none'>Show: <a data=0>All</a><a data='-1'>Auto</a><a data=25>25</a><a data=50>50</a><a data=100>100</a></span>");
  
		$settings.find("a[data]").click(function (e) {
		  let pagesize = $(e.target).attr("data");
		  if (pagesize != undefined) {
			if (pagesize == -1) {
			  let vp = grid.getViewport();
			  setPageSize(vp.bottom - vp.top);
			} else {
			  setPageSize(parseInt(pagesize));
			}
		  }
		});
  
		let icon_prefix = "<span class='ui-state-default ui-corner-all ui-icon-container'><span class='ui-icon ";
		let icon_suffix = "' /></span>";
  
		$(icon_prefix + "ui-icon-lightbulb" + icon_suffix)
			.click(function () {
			  $(".slick-pager-settings-expanded").toggle()
			})
			.appendTo($settings);
  
		$(icon_prefix + "ui-icon-seek-first" + icon_suffix)
			.click(gotoFirst)
			.appendTo($nav);
  
		$(icon_prefix + "ui-icon-seek-prev" + icon_suffix)
			.click(gotoPrev)
			.appendTo($nav);
  
		$(icon_prefix + "ui-icon-seek-next" + icon_suffix)
			.click(gotoNext)
			.appendTo($nav);
  
		$(icon_prefix + "ui-icon-seek-end" + icon_suffix)
			.click(gotoLast)
			.appendTo($nav);
  
		$container.find(".ui-icon-container")
			.hover(function () {
			  $(this).toggleClass("ui-state-hover");
			});
  
		$container.children().wrapAll("<div class='slick-pager' />");
	  }
  
  
	  function updatePager(pagingInfo) {
		let state = getNavState();
  
		$container.find(".slick-pager-nav span").removeClass("ui-state-disabled");
		if (!state.canGotoFirst) {
		  $container.find(".ui-icon-seek-first").addClass("ui-state-disabled");
		}
		if (!state.canGotoLast) {
		  $container.find(".ui-icon-seek-end").addClass("ui-state-disabled");
		}
		if (!state.canGotoNext) {
		  $container.find(".ui-icon-seek-next").addClass("ui-state-disabled");
		}
		if (!state.canGotoPrev) {
		  $container.find(".ui-icon-seek-prev").addClass("ui-state-disabled");
		}
  
		if (pagingInfo.pageSize == 0) {
		  $status.text("Showing all " + pagingInfo.totalRows + " rows");
		} else {
		  $status.text("Showing page " + (pagingInfo.pageNum + 1) + " of " + pagingInfo.totalPages);
		}
	  }
  
	  init();
	}
  
	// Slick.Controls.Pager
	$.extend(true, window, { Slick:{ Controls:{ Pager:SlickGridPager }}});
  })(jQuery);
  