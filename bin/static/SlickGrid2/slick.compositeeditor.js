;
(function ($) {
  $.extend(true, window, {
    Slick: {
      CompositeEditor: CompositeEditor
    }
  });


  /***
   * A composite SlickGrid editor factory.
   * Generates an editor that is composed of multiple editors for given columns.
   * Individual editors are provided given containers instead of the original cell.
   * Validation will be performed on all editors individually and the results will be aggregated into one
   * validation result.
   *
   *
   * The returned editor will have its prototype set to CompositeEditor, so you can use the "instanceof" check.
   *
   * NOTE:  This doesn't work for detached editors since they will be created and positioned relative to the
   *        active cell and not the provided container.
   *
   * @namespace Slick
   * @class CompositeEditor
   * @constructor
   * @param columns {Array} Column definitions from which editors will be pulled.
   * @param containers {Array} Container HTMLElements in which editors will be placed.
   * @param options {Object} Options hash:
   *  validationFailedMsg     -   A generic failed validation message set on the aggregated validation resuls.
   *  hide                    -   A function to be called when the grid asks the editor to hide itself.
   *  show                    -   A function to be called when the grid asks the editor to show itself.
   *  position                -   A function to be called when the grid asks the editor to reposition itself.
   *  destroy                 -   A function to be called when the editor is destroyed.
   */
  function CompositeEditor(columns, containers, options) {
    let defaultOptions = {
      validationFailedMsg: "Some of the fields have failed validation",
      show: null,
      hide: null,
      position: null,
      destroy: null
    };

    let noop = function () {
    };

    let firstInvalidEditor;

    options = $.extend({}, defaultOptions, options);


    function getContainerBox(i) {
      let c = containers[i];
      let offset = $(c).offset();
      let w = $(c).width();
      let h = $(c).height();

      return {
        top: offset.top,
        left: offset.left,
        bottom: offset.top + h,
        right: offset.left + w,
        width: w,
        height: h,
        visible: true
      };
    }


    function editor(args) {
      let editors = [];


      function init() {
        let newArgs = {};
        let idx = columns.length;
        while (idx--) {
          if (columns[idx].editor) {
            newArgs = $.extend({}, args);
            newArgs.container = containers[idx];
            newArgs.column = columns[idx];
            newArgs.position = getContainerBox(idx);
            newArgs.commitChanges = noop;
            newArgs.cancelChanges = noop;

            editors[idx] = new (columns[idx].editor)(newArgs);
          }
        }
      }


      this.destroy = function () {
        let idx = editors.length;
        while (idx--) {
          editors[idx].destroy();
        }

        options.destroy && options.destroy();
      };


      this.focus = function () {
        // if validation has failed, set the focus to the first invalid editor
        (firstInvalidEditor || editors[0]).focus();
      };


      this.isValueChanged = function () {
        let idx = editors.length;
        while (idx--) {
          if (editors[idx].isValueChanged()) {
            return true;
          }
        }
        return false;
      };


      this.serializeValue = function () {
        let serializedValue = [];
        let idx = editors.length;
        while (idx--) {
          serializedValue[idx] = editors[idx].serializeValue();
        }
        return serializedValue;
      };


      this.applyValue = function (item, state) {
        let idx = editors.length;
        while (idx--) {
          editors[idx].applyValue(item, state[idx]);
        }
      };


      this.loadValue = function (item) {
        let idx = editors.length;
        while (idx--) {
          editors[idx].loadValue(item);
        }
      };


      this.validate = function () {
        let validationResults;
        let errors = [];

        firstInvalidEditor = null;

        let idx = editors.length;
        while (idx--) {
          validationResults = editors[idx].validate();
          if (!validationResults.valid) {
            firstInvalidEditor = editors[idx];
            errors.push({
              index: idx,
              editor: editors[idx],
              container: containers[idx],
              msg: validationResults.msg
            });
          }
        }

        if (errors.length) {
          return {
            valid: false,
            msg: options.validationFailedMsg,
            errors: errors
          };
        } else {
          return {
            valid: true,
            msg: ""
          };
        }
      };


      this.hide = function () {
        let idx = editors.length;
        while (idx--) {
          editors[idx].hide && editors[idx].hide();
        }
        options.hide && options.hide();
      };


      this.show = function () {
        let idx = editors.length;
        while (idx--) {
          editors[idx].show && editors[idx].show();
        }
        options.show && options.show();
      };


      this.position = function (box) {
        options.position && options.position(box);
      };


      init();
    }

    // so we can do "editor instanceof Slick.CompositeEditor
    editor.prototype = this;

    return editor;
  }
})(jQuery);