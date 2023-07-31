function examGrid() {

	let dataView;
	let grid;
	let data1 = document.getElementById("exam").value;
	let data = JSON.parse(data1);
	console.log(data);



	/*date formator start */
	function dateFormatter(row, cell, value, columnDef, dataContext) {
		const date = new Date(value);
		let getDay = date.toLocaleString("default", { day: "2-digit" });
		let getMonth = date.toLocaleString("default", { month: "2-digit" });
		let getYear = date.toLocaleString("default", { year: "numeric" }); // Set the desired date format
		const formattedDate = getDay + "-" + getMonth + "-" + getYear;// Format the date as a string
		return formattedDate; // Return the formatted date string
	}
	/*date formator end */

	function buttonFormatter(row, cell, value, columnDef, dataContext) {
		let a = dataContext.id;
		return '<form action="/activateDeactivateExam" metod="get"><button type="submit" class="tablebutton1" name="examId"  value="' + a + '" >Activate/Deactivate</button></form>';
	}

	let columns = [{
		id: "id",
		name: "Id",
		field: "id",
		type: 'numberColumn',
		width: 100,
		sortable: true
	}, {
		id: "subjectId",
		name: "Subject Id",
		field: "subjectId",
		sortable: true
	}, {
		id: "name",
		name: "Exam Name",
		field: "name",
		sortable: true
	}, {
		id: "date",
		name: "Exam Date",
		field: "date",
		sortable: true,
		formatter: dateFormatter
	}, {
		id: "type",
		name: "Exam Type",
		field: "type",
		sortable: true
	},{
		id: "actions",
		name: "Action",
		field: "actions",
		formatter: buttonFormatter,
		width: 200
	}];

	let options = {
		editable: true,
		enableAddRow: false,
		enableCellNavigation: true,
		asyncEditorLoading: true,
		forceFitColumns: false,
		showHeaderRow: true,
		headerRowHeight: 30,
		explicitInitialization: true,
		topPanelHeight: 25
	};

	let columnFilters = {};

	let sortcol = "title";

	function comparer(a, b) {
		let x = a[sortcol], y = b[sortcol];
		if (x === y) { return 0; }
		return (x > y ? 1 : -1);
	}


	$(".grid-header .ui-icon")
		.addClass("ui-state-default ui-corner-all")
		.mouseover(function(e) {
			$(e.target).addClass("ui-state-hover")
		})
		.mouseout(function(e) {
			$(e.target).removeClass("ui-state-hover")
		});

	$(function() {
		/* filter start */
		function filter(item) {
			for (let columnId in columnFilters) {
				if (columnId !== undefined && columnFilters[columnId] !== "") {
					let column = grid.getColumns()[grid.getColumnIndex(columnId)];

					if (item[column.field] !== undefined) {
						let filterResult = typeof item[column.field].indexOf === 'function'
							? (item[column.field].indexOf(columnFilters[columnId]) === -1)
							: (item[column.field] !== columnFilters[columnId]);

						if (filterResult) {
							return false;
						}
					}
				}
			}
			return true;
		}
		/* filter end */


		dataView = new Slick.Data.DataView();
		grid = new Slick.Grid("#examGrid", dataView, columns, options);
		grid.setSelectionModel(new Slick.RowSelectionModel());


		// header row start
		dataView.onRowCountChanged.subscribe(function(e, args) {
			grid.updateRowCount();
			grid.render();
		});

		dataView.onRowsChanged.subscribe(function(e, args) {
			grid.invalidateRows(args.rows);
			grid.render();
		});

		$(grid.getHeaderRow()).delegate(":input", "change keyup",
			function(e) {
				let columnId = $(this).data("columnId");
				if (columnId != null) {
					columnFilters[columnId] = $.trim($(this).val());
					dataView.refresh();
				}
			});

		grid.onHeaderRowCellRendered.subscribe(function(e, args) {
			$(args.node).empty();
			$("<input type='text'>").data("columnId", args.column.id).val(
				columnFilters[args.column.id]).appendTo(args.node);

		});
		// header row end


		// move the filter panel defined in a hidden div into grid top panel
		$("#inlineFilterPanel")
			.appendTo(grid.getTopPanel())
			.show();

		grid.onCellChange.subscribe(function(e, args) {
			dataView.updateItem(args.item.id, args.item);
		});

		grid.onKeyDown.subscribe(function(e) {
			// select all rows on ctrl-a
			if (e.which !== 65 || !e.ctrlKey) {
				return false;
			}

			let rows = [];
			for (let i = 0; i < dataView.getLength(); i++) {
				rows.push(i);
			}

			grid.setSelectedRows(rows);
			e.preventDefault();
		});

		grid.onSort.subscribe(function(e, args) {
			sortcol = args.sortCol.field;

			if ($.browser.msie && $.browser.version <= 8) {
				// using temporary Object.prototype.toString override
				// more limited and does lexicographic sort only by default, but can be much faster

				let percentCompleteValueFn = function() {
					let val = this["percentComplete"];
					if (val < 10) {
						return "00" + val;
					} else if (val < 100) {
						return "0" + val;
					} else {
						return val;
					}
				};

				// use numeric sort of % and lexicographic for everything else
				dataView.fastSort((sortcol === "percentComplete") ? percentCompleteValueFn : sortcol, args.sortAsc);
			} else {
				// using native sort with comparer
				// preferred method but can be very slow in IE with huge datasets
				dataView.sort(comparer, args.sortAsc);
			}
		});

		// wire up model events to drive the grid
		dataView.onRowCountChanged.subscribe(function(e, args) {
			grid.updateRowCount();
			grid.render();
		});

		dataView.onRowsChanged.subscribe(function(e, args) {
			grid.invalidateRows(args.rows);
			grid.render();
		});

		dataView.onPagingInfoChanged.subscribe(function(e, pagingInfo) {
			let isLastPage = pagingInfo.pageNum === pagingInfo.totalPages - 1;
			let enableAddRow = isLastPage || pagingInfo.pageSize === 0;
			let options = grid.getOptions();

			if (options.enableAddRow !== enableAddRow) {
				grid.setOptions({ enableAddRow: enableAddRow });
			}
		});

		$("#btnSelectRows").click(function() {
			if (!Slick.GlobalEditorLock.commitCurrentEdit()) {
				return;
			}

			let rows = [];
			for (let i = 0; i < 10 && i < dataView.getLength(); i++) {
				rows.push(i);
			}

			grid.setSelectedRows(rows);
		});


		// initialize the model after all the events have been hooked up
		grid.init();
		dataView.beginUpdate();
		dataView.setItems(data);

		dataView.setFilter(filter);
		dataView.endUpdate();

		// if you don't want the items that are not visible (due to being filtered out
		// or being on a different page) to stay selected, pass 'false' to the second arg
		dataView.syncGridSelection(grid, true);

	})

}