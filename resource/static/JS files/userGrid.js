function userGrid() {

	let dataView;
	let grid;
	let data1 = document.getElementById("users").value;
	let data = JSON.parse(data1);
	console.log(data);

	/* set unique it to array */

	let nextId = 1;
	data.forEach(function(item) {
		// Generate a unique id using the nextId value
		let itemId = "item_" + nextId;

		// Set the id property of the item object
		item.id = itemId;

		// Increment the nextId value
		nextId++;
	});

	// Use the updated items array list with unique ids
	console.log(data);
	//console.log(dataList);
	/* unique id end */

	function buttonFormatter(row, cell, value, columnDef, dataContext) {
		let a = dataContext.userId;
		let status = dataContext.status;
		if (status === ("approved")) {
			return '<form action="/reject" metod="get"><button type="submit" class="tablebutton2" name="userID"  value="' + a + '" >Reject</button></form>';
		} else {
			return '<form action="/approve" metod="get"><button type="submit" class="tablebutton1" name="userID"  value="' + a + '" >Approve</button></form> ';
		}
	}

	let columns = [{
		id: "userId",
		name: "UserId",
		field: "userId",
		width: 100,
		sortable: true
	}, {
		id: "firstName",
		name: "First Name",
		field: "firstName",
		sortable: true
	}, {
		id: "lastName",
		name: "Last Name",
		field: "lastName",
		sortable: true
	}, {
		id: "gender",
		name: "Gender",
		field: "gender",
		width: 100,
		sortable: true
	}, {
		id: "parentName",
		name: "parent Name",
		field: "parentName",
		sortable: true
	}, {
		id: "email",
		name: "Email Id",
		field: "email",
		sortable: true
	}, {
		id: "phone",
		name: "Phone Number",
		field: "phone",
		type: 'numberColumn',
		sortable: true
	}, {
		id: "department",
		name: "Department",
		field: "department",
		sortable: true
	}, {
		id: "status",
		name: "Status",
		field: "status",
		width: 100,
		sortable: true
	}, {
		id: "actions",
		name: "Actions",
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
		return (x === y ? 0 : (x > y ? 1 : -1));
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


		//		dataView = new Slick.Data.DataView({ inlineFilters: true });
		dataView = new Slick.Data.DataView();
		grid = new Slick.Grid("#myGrid", dataView, columns, options);
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