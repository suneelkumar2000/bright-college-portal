/*
 * QUnit - A JavaScript Unit Testing Framework
 * 
 * http://docs.jquery.com/QUnit
 *
 * Copyright (c) 2009 John Resig, Jörn Zaefferer
 * Dual licensed under the MIT (MIT-LICENSE.txt)
 * and GPL (GPL-LICENSE.txt) licenses.
 */

(function(window) {

	let QUnit = {
	
		// Initialize the configuration options
		init: function() {
			config = {
				stats: { all: 0, bad: 0 },
				moduleStats: { all: 0, bad: 0 },
				started: +new Date,
				updateRate: 1000,
				blocking: false,
				autorun: false,
				assertions: [],
				filters: [],
				queue: []
			};
	
			let tests = id("qunit-tests"),
				banner = id("qunit-banner"),
				result = id("qunit-testresult");
	
			if ( tests ) {
				tests.innerHTML = "";
			}
	
			if ( banner ) {
				banner.className = "";
			}
	
			if ( result ) {
				result.parentNode.removeChild( result );
			}
		},
		
		// call on start of module test to prepend name to all tests
		module: function(name, testEnvironment) {
			config.currentModule = name;
	
			synchronize(function() {
				if ( config.currentModule ) {
					QUnit.moduleDone( config.currentModule, config.moduleStats.bad, config.moduleStats.all );
				}
	
				config.currentModule = name;
				config.moduleTestEnvironment = testEnvironment;
				config.moduleStats = { all: 0, bad: 0 };
	
				QUnit.moduleStart( name, testEnvironment );
			});
		},
	
		asyncTest: function(testName, expected, callback) {
			if ( arguments.length === 2 ) {
				callback = expected;
				expected = 0;
			}
	
			QUnit.test(testName, expected, callback, true);
		},
		
		test: function(testName, expected, callback, async) {
			let name = testName, testEnvironment, testEnvironmentArg;
	
			if ( arguments.length === 2 ) {
				callback = expected;
				expected = null;
			}
			// is 2nd argument a testEnvironment?
			if ( expected && typeof expected === 'object') {
				testEnvironmentArg =  expected;
				expected = null;
			}
	
			if ( config.currentModule ) {
				name = config.currentModule + " module: " + name;
			}
	
			if ( !validTest(name) ) {
				return;
			}
	
			synchronize(function() {
				QUnit.testStart( testName );
	
				testEnvironment = extend({
					setup: function() {},
					teardown: function() {}
				}, config.moduleTestEnvironment);
				if (testEnvironmentArg) {
					extend(testEnvironment,testEnvironmentArg);
				}
	
				// allow utility functions to access the current test environment
				QUnit.current_testEnvironment = testEnvironment;
				
				config.assertions = [];
				config.expected = expected;
	
				try {
					if ( !config.pollution ) {
						saveGlobal();
					}
	
					testEnvironment.setup.call(testEnvironment);
				} catch(e) {
					QUnit.ok( false, "Setup failed on " + name + ": " + e.message );
				}
	
				if ( async ) {
					QUnit.stop();
				}
	
				try {
					callback.call(testEnvironment);
				} catch(e) {
					fail("Test " + name + " died, exception and test follows", e, callback);
					QUnit.ok( false, "Died on test #" + (config.assertions.length + 1) + ": " + e.message );
					// else next test will carry the responsibility
					saveGlobal();
	
					// Restart the tests if they're blocking
					if ( config.blocking ) {
						start();
					}
				}
			});
	
			synchronize(function() {
				try {
					checkPollution();
					testEnvironment.teardown.call(testEnvironment);
				} catch(e) {
					QUnit.ok( false, "Teardown failed on " + name + ": " + e.message );
				}
	
				try {
					QUnit.reset();
				} catch(e) {
					fail("reset() failed, following Test " + name + ", exception and reset fn follows", e, reset);
				}
	
				if ( config.expected && config.expected != config.assertions.length ) {
					QUnit.ok( false, "Expected " + config.expected + " assertions, but " + config.assertions.length + " were run" );
				}
	
				let good = 0, bad = 0,
					tests = id("qunit-tests");
	
				config.stats.all += config.assertions.length;
				config.moduleStats.all += config.assertions.length;
	
				if ( tests ) {
					let ol  = document.createElement("ol");
					ol.style.display = "none";
	
					for ( let i = 0; i < config.assertions.length; i++ ) {
						let assertion = config.assertions[i];
	
						let li = document.createElement("li");
						li.className = assertion.result ? "pass" : "fail";
						li.appendChild(document.createTextNode(assertion.message || "(no message)"));
						ol.appendChild( li );
	
						if ( assertion.result ) {
							good++;
						} else {
							bad++;
							config.stats.bad++;
							config.moduleStats.bad++;
						}
					}
	
					let b = document.createElement("strong");
					b.innerHTML = name + " <b style='color:black;'>(<b class='fail'>" + bad + "</b>, <b class='pass'>" + good + "</b>, " + config.assertions.length + ")</b>";
					
					addEvent(b, "click", function() {
						let next = b.nextSibling, display = next.style.display;
						next.style.display = display === "none" ? "block" : "none";
					});
					
					addEvent(b, "dblclick", function(e) {
						let target = e && e.target ? e.target : window.event.srcElement;
						if ( target.nodeName.toLowerCase() === "strong" ) {
							let text = "", node = target.firstChild;
	
							while ( node.nodeType === 3 ) {
								text += node.nodeValue;
								node = node.nextSibling;
							}
	
							text = text.replace(/(^\s*|\s*$)/g, "");
	
							if ( window.location ) {
								window.location.href = window.location.href.match(/^(.+?)(\?.*)?$/)[1] + "?" + encodeURIComponent(text);
							}
						}
					});
	
					let li = document.createElement("li");
					li.className = bad ? "fail" : "pass";
					li.appendChild( b );
					li.appendChild( ol );
					tests.appendChild( li );
	
					if ( bad ) {
						let toolbar = id("qunit-testrunner-toolbar");
						if ( toolbar ) {
							toolbar.style.display = "block";
							id("qunit-filter-pass").disabled = null;
							id("qunit-filter-missing").disabled = null;
						}
					}
	
				} else {
					for ( let i = 0; i < config.assertions.length; i++ ) {
						if ( !config.assertions[i].result ) {
							bad++;
							config.stats.bad++;
							config.moduleStats.bad++;
						}
					}
				}
	
				QUnit.testDone( testName, bad, config.assertions.length );
	
				if ( !window.setTimeout && !config.queue.length ) {
					done();
				}
			});
	
			if ( window.setTimeout && !config.doneTimer ) {
				config.doneTimer = window.setTimeout(function(){
					if ( !config.queue.length ) {
						done();
					} else {
						synchronize( done );
					}
				}, 13);
			}
		},
		
		/**
		 * Specify the number of expected assertions to gurantee that failed test (no assertions are run at all) don't slip through.
		 */
		expect: function(asserts) {
			config.expected = asserts;
		},
	
		/**
		 * Asserts true.
		 * @example ok( "asdfasdf".length > 5, "There must be at least 5 chars" );
		 */
		ok: function(a, msg) {
			QUnit.log(a, msg);
	
			config.assertions.push({
				result: !!a,
				message: msg
			});
		},
	
		/**
		 * Checks that the first two arguments are equal, with an optional message.
		 * Prints out both actual and expected values.
		 *
		 * Prefered to ok( actual == expected, message )
		 *
		 * @example equal( format("Received {0} bytes.", 2), "Received 2 bytes." );
		 *
		 * @param Object actual
		 * @param Object expected
		 * @param String message (optional)
		 */
		equal: function(actual, expected, message) {
			push(expected == actual, actual, expected, message);
		},
	
		notEqual: function(actual, expected, message) {
			push(expected != actual, actual, expected, message);
		},
		
		deepEqual: function(a, b, message) {
			push(QUnit.equiv(a, b), a, b, message);
		},
	
		notDeepEqual: function(a, b, message) {
			push(!QUnit.equiv(a, b), a, b, message);
		},
	
		strictEqual: function(actual, expected, message) {
			push(expected === actual, actual, expected, message);
		},
	
		notStrictEqual: function(actual, expected, message) {
			push(expected !== actual, actual, expected, message);
		},
		
		start: function() {
			// A slight delay, to avoid any current callbacks
			if ( window.setTimeout ) {
				window.setTimeout(function() {
					if ( config.timeout ) {
						clearTimeout(config.timeout);
					}
	
					config.blocking = false;
					process();
				}, 13);
			} else {
				config.blocking = false;
				process();
			}
		},
		
		stop: function(timeout) {
			config.blocking = true;
	
			if ( timeout && window.setTimeout ) {
				config.timeout = window.setTimeout(function() {
					QUnit.ok( false, "Test timed out" );
					QUnit.start();
				}, timeout);
			}
		},
		
		/**
		 * Resets the test setup. Useful for tests that modify the DOM.
		 */
		reset: function() {
			if ( window.jQuery ) {
				jQuery("#main").html( config.fixture );
				jQuery.event.global = {};
				jQuery.ajaxSettings = extend({}, config.ajaxSettings);
			}
		},
		
		/**
		 * Trigger an event on an element.
		 *
		 * @example triggerEvent( document.body, "click" );
		 *
		 * @param DOMElement elem
		 * @param String type
		 */
		triggerEvent: function( elem, type, event ) {
			if ( document.createEvent ) {
				event = document.createEvent("MouseEvents");
				event.initMouseEvent(type, true, true, elem.ownerDocument.defaultView,
					0, 0, 0, 0, 0, false, false, false, false, 0, null);
				elem.dispatchEvent( event );
	
			} else if ( elem.fireEvent ) {
				elem.fireEvent("on"+type);
			}
		},
		
		// Safe object type checking
		is: function( type, obj ) {
			return Object.prototype.toString.call( obj ) === "[object "+ type +"]";
		},
		
		// Logging callbacks
		done: function(failures, total) {},
		log: function(result, message) {},
		testStart: function(name) {},
		testDone: function(name, failures, total) {},
		moduleStart: function(name, testEnvironment) {},
		moduleDone: function(name, failures, total) {}
	};
	
	// Backwards compatibility, deprecated
	QUnit.equals = QUnit.equal;
	QUnit.same = QUnit.deepEqual;
	
	// Maintain internal state
	let config = {
		// The queue of tests to run
		queue: [],
	
		// block until document ready
		blocking: true
	};
	
	// Load paramaters
	(function() {
		let location = window.location || { search: "", protocol: "file:" },
			GETParams = location.search.slice(1).split('&');
	
		for ( let i = 0; i < GETParams.length; i++ ) {
			GETParams[i] = decodeURIComponent( GETParams[i] );
			if ( GETParams[i] === "noglobals" ) {
				GETParams.splice( i, 1 );
				i--;
				config.noglobals = true;
			} else if ( GETParams[i].search('=') > -1 ) {
				GETParams.splice( i, 1 );
				i--;
			}
		}
		
		// restrict modules/tests by get parameters
		config.filters = GETParams;
		
		// Figure out if we're running the tests from a server or not
		QUnit.isLocal = !!(location.protocol === 'file:');
	})();
	
	// Expose the API as global letiables, unless an 'exports'
	// object exists, in that case we assume we're in CommonJS
	if ( typeof exports === "undefined" || typeof require === "undefined" ) {
		extend(window, QUnit);
		window.QUnit = QUnit;
	} else {
		extend(exports, QUnit);
		exports.QUnit = QUnit;
	}
	
	if ( typeof document === "undefined" || document.readyState === "complete" ) {
		config.autorun = true;
	}
	
	addEvent(window, "load", function() {
		// Initialize the config, saving the execution queue
		let oldconfig = extend({}, config);
		QUnit.init();
		extend(config, oldconfig);
	
		config.blocking = false;
	
		let userAgent = id("qunit-userAgent");
		if ( userAgent ) {
			userAgent.innerHTML = navigator.userAgent;
		}
		
		let toolbar = id("qunit-testrunner-toolbar");
		if ( toolbar ) {
			toolbar.style.display = "none";
			
			let filter = document.createElement("input");
			filter.type = "checkbox";
			filter.id = "qunit-filter-pass";
			filter.disabled = true;
			addEvent( filter, "click", function() {
				let li = document.getElementsByTagName("li");
				for ( let i = 0; i < li.length; i++ ) {
					if ( li[i].className.indexOf("pass") > -1 ) {
						li[i].style.display = filter.checked ? "none" : "";
					}
				}
			});
			toolbar.appendChild( filter );
	
			let label = document.createElement("label");
			label.setAttribute("for", "qunit-filter-pass");
			label.innerHTML = "Hide passed tests";
			toolbar.appendChild( label );
	
			let missing = document.createElement("input");
			missing.type = "checkbox";
			missing.id = "qunit-filter-missing";
			missing.disabled = true;
			addEvent( missing, "click", function() {
				let li = document.getElementsByTagName("li");
				for ( let i = 0; i < li.length; i++ ) {
					if ( li[i].className.indexOf("fail") > -1 && li[i].innerHTML.indexOf('missing test - untested code is broken code') > - 1 ) {
						li[i].parentNode.parentNode.style.display = missing.checked ? "none" : "block";
					}
				}
			});
			toolbar.appendChild( missing );
	
			label = document.createElement("label");
			label.setAttribute("for", "qunit-filter-missing");
			label.innerHTML = "Hide missing tests (untested code is broken code)";
			toolbar.appendChild( label );
		}
	
		let main = id('main');
		if ( main ) {
			config.fixture = main.innerHTML;
		}
	
		if ( window.jQuery ) {
			config.ajaxSettings = window.jQuery.ajaxSettings;
		}
	
		QUnit.start();
	});
	
	function done() {
		if ( config.doneTimer && window.clearTimeout ) {
			window.clearTimeout( config.doneTimer );
			config.doneTimer = null;
		}
	
		if ( config.queue.length ) {
			config.doneTimer = window.setTimeout(function(){
				if ( !config.queue.length ) {
					done();
				} else {
					synchronize( done );
				}
			}, 13);
	
			return;
		}
	
		config.autorun = true;
	
		// Log the last module results
		if ( config.currentModule ) {
			QUnit.moduleDone( config.currentModule, config.moduleStats.bad, config.moduleStats.all );
		}
	
		let banner = id("qunit-banner"),
			tests = id("qunit-tests"),
			html = ['Tests completed in ',
			+new Date - config.started, ' milliseconds.<br/>',
			'<span class="passed">', config.stats.all - config.stats.bad, '</span> tests of <span class="total">', config.stats.all, '</span> passed, <span class="failed">', config.stats.bad,'</span> failed.'].join('');
	
		if ( banner ) {
			banner.className = (config.stats.bad ? "qunit-fail" : "qunit-pass");
		}
	
		if ( tests ) {	
			let result = id("qunit-testresult");
	
			if ( !result ) {
				result = document.createElement("p");
				result.id = "qunit-testresult";
				result.className = "result";
				tests.parentNode.insertBefore( result, tests.nextSibling );
			}
	
			result.innerHTML = html;
		}
	
		QUnit.done( config.stats.bad, config.stats.all );
	}
	
	function validTest( name ) {
		let i = config.filters.length,
			run = false;
	
		if ( !i ) {
			return true;
		}
		
		while ( i-- ) {
			let filter = config.filters[i],
				not = filter.charAt(0) == '!';
	
			if ( not ) {
				filter = filter.slice(1);
			}
	
			if ( name.indexOf(filter) !== -1 ) {
				return !not;
			}
	
			if ( not ) {
				run = true;
			}
		}
	
		return run;
	}
	
	function push(result, actual, expected, message) {
		message = message || (result ? "okay" : "failed");
		QUnit.ok( result, result ? message + ": " + QUnit.jsDump.parse(expected) : message + ", expected: " + QUnit.jsDump.parse(expected) + " result: " + QUnit.jsDump.parse(actual) );
	}
	
	function synchronize( callback ) {
		config.queue.push( callback );
	
		if ( config.autorun && !config.blocking ) {
			process();
		}
	}
	
	function process() {
		let start = (new Date()).getTime();
	
		while ( config.queue.length && !config.blocking ) {
			if ( config.updateRate <= 0 || (((new Date()).getTime() - start) < config.updateRate) ) {
				config.queue.shift()();
	
			} else {
				setTimeout( process, 13 );
				break;
			}
		}
	}
	
	function saveGlobal() {
		config.pollution = [];
		
		if ( config.noglobals ) {
			for ( let key in window ) {
				config.pollution.push( key );
			}
		}
	}
	
	function checkPollution( name ) {
		let old = config.pollution;
		saveGlobal();
		
		let newGlobals = diff( old, config.pollution );
		if ( newGlobals.length > 0 ) {
			ok( false, "Introduced global letiable(s): " + newGlobals.join(", ") );
			config.expected++;
		}
	
		let deletedGlobals = diff( config.pollution, old );
		if ( deletedGlobals.length > 0 ) {
			ok( false, "Deleted global letiable(s): " + deletedGlobals.join(", ") );
			config.expected++;
		}
	}
	
	// returns a new Array with the elements that are in a but not in b
	function diff( a, b ) {
		let result = a.slice();
		for ( let i = 0; i < result.length; i++ ) {
			for ( let j = 0; j < b.length; j++ ) {
				if ( result[i] === b[j] ) {
					result.splice(i, 1);
					i--;
					break;
				}
			}
		}
		return result;
	}
	
	function fail(message, exception, callback) {
		if ( typeof console !== "undefined" && console.error && console.warn ) {
			console.error(message);
			console.error(exception);
			console.warn(callback.toString());
	
		} else if ( window.opera && opera.postError ) {
			opera.postError(message, exception, callback.toString);
		}
	}
	
	function extend(a, b) {
		for ( let prop in b ) {
			a[prop] = b[prop];
		}
	
		return a;
	}
	
	function addEvent(elem, type, fn) {
		if ( elem.addEventListener ) {
			elem.addEventListener( type, fn, false );
		} else if ( elem.attachEvent ) {
			elem.attachEvent( "on" + type, fn );
		} else {
			fn();
		}
	}
	
	function id(name) {
		return !!(typeof document !== "undefined" && document && document.getElementById) &&
			document.getElementById( name );
	}
	
	// Test for equality any JavaScript type.
	// Discussions and reference: http://philrathe.com/articles/equiv
	// Test suites: http://philrathe.com/tests/equiv
	// Author: Philippe Rathé <prathe@gmail.com>
	QUnit.equiv = function () {
	
		let innerEquiv; // the real equiv function
		let callers = []; // stack to decide between skip/abort functions
		let parents = []; // stack to avoiding loops from circular referencing
	
	
		// Determine what is o.
		function hoozit(o) {
			if (QUnit.is("String", o)) {
				return "string";
				
			} else if (QUnit.is("Boolean", o)) {
				return "boolean";
	
			} else if (QUnit.is("Number", o)) {
	
				if (isNaN(o)) {
					return "nan";
				} else {
					return "number";
				}
	
			} else if (typeof o === "undefined") {
				return "undefined";
	
			// consider: typeof null === object
			} else if (o === null) {
				return "null";
	
			// consider: typeof [] === object
			} else if (QUnit.is( "Array", o)) {
				return "array";
			
			// consider: typeof new Date() === object
			} else if (QUnit.is( "Date", o)) {
				return "date";
	
			// consider: /./ instanceof Object;
			//           /./ instanceof RegExp;
			//          typeof /./ === "function"; // => false in IE and Opera,
			//                                          true in FF and Safari
			} else if (QUnit.is( "RegExp", o)) {
				return "regexp";
	
			} else if (typeof o === "object") {
				return "object";
	
			} else if (QUnit.is( "Function", o)) {
				return "function";
			} else {
				return undefined;
			}
		}
	
		// Call the o related callback with the given arguments.
		function bindCallbacks(o, callbacks, args) {
			let prop = hoozit(o);
			if (prop) {
				if (hoozit(callbacks[prop]) === "function") {
					return callbacks[prop].apply(callbacks, args);
				} else {
					return callbacks[prop]; // or undefined
				}
			}
		}
		
		let callbacks = function () {
	
			// for string, boolean, number and null
			function useStrictEquality(b, a) {
				if (b instanceof a.constructor || a instanceof b.constructor) {
					// to catch short annotaion VS 'new' annotation of a declaration
					// e.g. let i = 1;
					//      let j = new Number(1);
					return a == b;
				} else {
					return a === b;
				}
			}
	
			return {
				"string": useStrictEquality,
				"boolean": useStrictEquality,
				"number": useStrictEquality,
				"null": useStrictEquality,
				"undefined": useStrictEquality,
	
				"nan": function (b) {
					return isNaN(b);
				},
	
				"date": function (b, a) {
					return hoozit(b) === "date" && a.valueOf() === b.valueOf();
				},
	
				"regexp": function (b, a) {
					return hoozit(b) === "regexp" &&
						a.source === b.source && // the regex itself
						a.global === b.global && // and its modifers (gmi) ...
						a.ignoreCase === b.ignoreCase &&
						a.multiline === b.multiline;
				},
	
				// - skip when the property is a method of an instance (OOP)
				// - abort otherwise,
				//   initial === would have catch identical references anyway
				"function": function () {
					let caller = callers[callers.length - 1];
					return caller !== Object &&
							typeof caller !== "undefined";
				},
	
				"array": function (b, a) {
					let i, j, loop;
					let len;
	
					// b could be an object literal here
					if ( ! (hoozit(b) === "array")) {
						return false;
					}   
					
					len = a.length;
					if (len !== b.length) { // safe and faster
						return false;
					}
					
					//track reference to avoid circular references
					parents.push(a);
					for (i = 0; i < len; i++) {
						loop = false;
						for(j=0;j<parents.length;j++){
							if(parents[j] === a[i]){
								loop = true;//dont rewalk array
							}
						}
						if (!loop && ! innerEquiv(a[i], b[i])) {
							parents.pop();
							return false;
						}
					}
					parents.pop();
					return true;
				},
	
				"object": function (b, a) {
					let i, j, loop;
					let eq = true; // unless we can proove it
					let aProperties = [], bProperties = []; // collection of strings
	
					// comparing constructors is more strict than using instanceof
					if ( a.constructor !== b.constructor) {
						return false;
					}
	
					// stack constructor before traversing properties
					callers.push(a.constructor);
					//track reference to avoid circular references
					parents.push(a);
					
					for (i in a) { // be strict: don't ensures hasOwnProperty and go deep
						loop = false;
						for(j=0;j<parents.length;j++){
							if(parents[j] === a[i])
								loop = true; //don't go down the same path twice
						}
						aProperties.push(i); // collect a's properties
	
						if (!loop && ! innerEquiv(a[i], b[i])) {
							eq = false;
							break;
						}
					}
	
					callers.pop(); // unstack, we are done
					parents.pop();
	
					for (i in b) {
						bProperties.push(i); // collect b's properties
					}
	
					// Ensures identical properties name
					return eq && innerEquiv(aProperties.sort(), bProperties.sort());
				}
			};
		}();
	
		innerEquiv = function () { // can take multiple arguments
			let args = Array.prototype.slice.apply(arguments);
			if (args.length < 2) {
				return true; // end transition
			}
	
			return (function (a, b) {
				if (a === b) {
					return true; // catch the most you can
				} else if (a === null || b === null || typeof a === "undefined" || typeof b === "undefined" || hoozit(a) !== hoozit(b)) {
					return false; // don't lose time with error prone cases
				} else {
					return bindCallbacks(a, callbacks, [b, a]);
				}
	
			// apply transition with (1..n) arguments
			})(args[0], args[1]) && arguments.callee.apply(this, args.splice(1, args.length -1));
		};
	
		return innerEquiv;
	
	}();
	
	/**
	 * jsDump
	 * Copyright (c) 2008 Ariel Flesler - aflesler(at)gmail(dot)com | http://flesler.blogspot.com
	 * Licensed under BSD (http://www.opensource.org/licenses/bsd-license.php)
	 * Date: 5/15/2008
	 * @projectDescription Advanced and extensible data dumping for Javascript.
	 * @version 1.0.0
	 * @author Ariel Flesler
	 * @link {http://flesler.blogspot.com/2008/05/jsdump-pretty-dump-of-any-javascript.html}
	 */
	QUnit.jsDump = (function() {
		function quote( str ) {
			return '"' + str.toString().replace(/"/g, '\\"') + '"';
		};
		function literal( o ) {
			return o + '';	
		};
		function join( pre, arr, post ) {
			let s = jsDump.separator(),
				base = jsDump.indent(),
				inner = jsDump.indent(1);
			if ( arr.join )
				arr = arr.join( ',' + s + inner );
			if ( !arr )
				return pre + post;
			return [ pre, inner + arr, base + post ].join(s);
		};
		function array( arr ) {
			let i = arr.length,	ret = Array(i);					
			this.up();
			while ( i-- )
				ret[i] = this.parse( arr[i] );				
			this.down();
			return join( '[', ret, ']' );
		};
		
		let reName = /^function (\w+)/;
		
		let jsDump = {
			parse:function( obj, type ) { //type is used mostly internally, you can fix a (custom)type in advance
				let	parser = this.parsers[ type || this.typeOf(obj) ];
				type = typeof parser;			
				
				return type == 'function' ? parser.call( this, obj ) :
					   type == 'string' ? parser :
					   this.parsers.error;
			},
			typeOf:function( obj ) {
				let type;
				if ( obj === null ) {
					type = "null";
				} else if (typeof obj === "undefined") {
					type = "undefined";
				} else if (QUnit.is("RegExp", obj)) {
					type = "regexp";
				} else if (QUnit.is("Date", obj)) {
					type = "date";
				} else if (QUnit.is("Function", obj)) {
					type = "function";
				} else if (obj.setInterval && obj.document && !obj.nodeType) {
					type = "window";
				} else if (obj.nodeType === 9) {
					type = "document";
				} else if (obj.nodeType) {
					type = "node";
				} else if (typeof obj === "object" && typeof obj.length === "number" && obj.length >= 0) {
					type = "array";
				} else {
					type = typeof obj;
				}
				return type;
			},
			separator:function() {
				return this.multiline ?	this.HTML ? '<br />' : '\n' : this.HTML ? '&nbsp;' : ' ';
			},
			indent:function( extra ) {// extra can be a number, shortcut for increasing-calling-decreasing
				if ( !this.multiline )
					return '';
				let chr = this.indentChar;
				if ( this.HTML )
					chr = chr.replace(/\t/g,'   ').replace(/ /g,'&nbsp;');
				return Array( this._depth_ + (extra||0) ).join(chr);
			},
			up:function( a ) {
				this._depth_ += a || 1;
			},
			down:function( a ) {
				this._depth_ -= a || 1;
			},
			setParser:function( name, parser ) {
				this.parsers[name] = parser;
			},
			// The next 3 are exposed so you can use them
			quote:quote, 
			literal:literal,
			join:join,
			//
			_depth_: 1,
			// This is the list of parsers, to modify them, use jsDump.setParser
			parsers:{
				window: '[Window]',
				document: '[Document]',
				error:'[ERROR]', //when no parser is found, shouldn't happen
				unknown: '[Unknown]',
				'null':'null',
				undefined:'undefined',
				'function':function( fn ) {
					let ret = 'function',
						name = 'name' in fn ? fn.name : (reName.exec(fn)||[])[1];//functions never have name in IE
					if ( name )
						ret += ' ' + name;
					ret += '(';
					
					ret = [ ret, this.parse( fn, 'functionArgs' ), '){'].join('');
					return join( ret, this.parse(fn,'functionCode'), '}' );
				},
				array: array,
				nodelist: array,
				arguments: array,
				object:function( map ) {
					let ret = [ ];
					this.up();
					for ( let key in map )
						ret.push( this.parse(key,'key') + ': ' + this.parse(map[key]) );
					this.down();
					return join( '{', ret, '}' );
				},
				node:function( node ) {
					let open = this.HTML ? '&lt;' : '<',
						close = this.HTML ? '&gt;' : '>';
						
					let tag = node.nodeName.toLowerCase(),
						ret = open + tag;
						
					for ( let a in this.DOMAttrs ) {
						let val = node[this.DOMAttrs[a]];
						if ( val )
							ret += ' ' + a + '=' + this.parse( val, 'attribute' );
					}
					return ret + close + open + '/' + tag + close;
				},
				functionArgs:function( fn ) {//function calls it internally, it's the arguments part of the function
					let l = fn.length;
					if ( !l ) return '';				
					
					let args = Array(l);
					while ( l-- )
						args[l] = String.fromCharCode(97+l);//97 is 'a'
					return ' ' + args.join(', ') + ' ';
				},
				key:quote, //object calls it internally, the key part of an item in a map
				functionCode:'[code]', //function calls it internally, it's the content of the function
				attribute:quote, //node calls it internally, it's an html attribute value
				string:quote,
				date:quote,
				regexp:literal, //regex
				number:literal,
				'boolean':literal
			},
			DOMAttrs:{//attributes to dump from nodes, name=>realName
				id:'id',
				name:'name',
				'class':'className'
			},
			HTML:false,//if true, entities are escaped ( <, >, \t, space and \n )
			indentChar:'   ',//indentation unit
			multiline:false //if true, items in a collection, are separated by a \n, else just a space.
		};
	
		return jsDump;
	})();
	
	})(this);
	