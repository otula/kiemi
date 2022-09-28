/**
 * Copyright 2021 Tampere University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
class dropdown {
  constructor(label, items, params, action) {
    this._params = params;
    this._currentIndex = -1;
    this._values = [];
    this._action = action;

    var _this = this;
    var html = '<div class="dropdown basicButton">';
    html += '<div class="dropdown-label"></div>';
    html += '<div class="dropdown-content hidden"></div>';
    html += '</div>';

    this._elem = $(html);
    this._content = _this._elem.find('.dropdown-content');

    this.items = items;
    this.label = label;
    this.active = true;

    return this;
  }
  open(action) {
    $(document).on('mousedown.dropdown', $.proxy(function(evt) {
      var target = $(evt.target);

      if (target.hasClass('dropdown-item')) {
        var index = Number(target.attr('id').split('_')[2]);

        if (!this._params.ignoreSame || this._currentIndex !== index) {
          this._currentIndex = index;

          action(this.value);
        }
      }
      this.close();
    }, this));

    this._content.removeClass('hidden');
  }
  close() {
      var _this = this;

      setTimeout(function() { //Delay the activation of the dropdown a bit so that it is not immediately retriggered with the closing click
        _this.active = true;
      }, 300);
      $(document).off('mousedown.dropdown');

      this._content.addClass('hidden');
    }
    //Set as active or disabled
  set active(active) {
      var _this = this;

      if (active) {
        this._elem.on('click', function(evt) {
          _this.active = false;

          _this.open(_this._action);
        });
      } else {
        this._elem.off('click');
      }
    }
    //Set the items listed in the dropdown
  set items(items) {
      this._values = [];
      this._content.html('');

      items.forEach((item, index) => {
        if (!_.isObject(item)) item = { value: item, label: getText(item) };

        this._values.push(item.value);

        this._content.append('<div id="dropdown_item_' + index + '" class="dropdown-item" value="' + item.value + '">' + item.label + '</div>');
      });
    }
    //Set the label (the visible text shown when the dropdown is closed) of the dropdown
  set label(label) {
      this._elem.find('.dropdown-label').text(label);
    }
    //Get the DOM element
  get elem() {
      return this._elem;
    }
    //Get or set the currently selected value (each selectable item has a set value)
  get value() {
    return this._values[this._currentIndex];
  }
  set value(value) {
    var index = _.indexOf(this._values, value);

    if (index >= 0) {
      this._currentIndex = index;
    }
  }
}

class customPopup {
  constructor(title, content, container, params) {
    var id = 'customPopup_' + Math.floor(Math.random() * 1000);
    var html = '<div id="' + id + '" class="customPopup">';
    html += '<div class="customPopupHeader unselectable">' + title + '<div class="customPopupClose">X</div></div>';
    html += '<div class="customPopupContent">' + content + '</div>';
    html += '</div>';

    container.append(html);

    this._container = container;
    this._elem = container.find('#' + id);

    if (params.draggable) {
      this._elem.draggable({ handle: '.customPopupHeader', containment: '#' + container.attr('id'), scroll: false });
    }
    if (params.closeButton) {
      $('#content .customPopupClose').click(() => {
        this._elem.remove();
      });
    } else {
      $('#content .customPopupClose').hide();
    }
    this.position(params.x, params.y);
  }
  position(posX, posY) {
    if (posX || posX === 0) this._elem.css({ left: Math.min(posX, this._container.width() - this._elem.width()) });
    if (posY || posY === 0) this._elem.css({ top: Math.min(posY, this._container.height() - this._elem.height()) });
  }
}

var ui = {
  init: function() {
    this._forms = {
      site: [
        { type: 'inputText', id: 'name' },
        { type: 'inputText', id: 'description' },
        { type: 'inputText', id: 'organizationName' },
        { type: 'inputText', id: 'externalUrl' }
      ],
      layer: [
        { type: 'inputText', id: 'name' },
        { type: 'inputText', id: 'description' },
        { type: 'inputText', id: 'organizationName' },
        { type: 'inputText', id: 'address' },
        { type: 'inputText', id: 'postalCode' },
        { type: 'inputText', id: 'country' },
        { type: 'inputText', id: 'latitude', format: 'Number' },
        { type: 'inputText', id: 'longitude', format: 'Number' },
        { type: 'inputText', id: 'url', label: 'image', group: 'image' },
        { type: 'inputText', id: 'scale', group: 'image' },
        { type: 'inputText', id: 'externalUrl', inherit: 0 }
      ],
      sensor: [
        { type: 'inputText', id: 'name' },
        { type: 'inputText', id: 'externalId' },
        { type: 'inputText', id: 'serviceType' },
        { type: 'inputText', id: 'description' }
      ],
      dateTimeLimitsSelection: [
        { type: 'inputDate', id: 'Start date' },
        { type: 'inputDate', id: 'End date' }
      ]
    };
    this.activePopupIds = [];
    this.closePopup();
  },

  /**
   *
   */
  openPopup: function(title, content, buttons, outsideAction, popupId) {
    var _this = this;
    if (!popupId) {
      popupId = new Date().getTime();
    }

    if (_this.activePopupIds.includes(popupId)) {
      console.log("Window, id: " + popupId + " is already open.");
      return;
    }

    var html = '<div id="' + popupId + '" class="popupContent">';
    html += '<div class="titleText">' + title + '</div>';
    html += '<div class="popupContentContent">' + content + '</div>';
    html += '<div id="popupButtons' + popupId + '" class="popupButtons"></div>';
    html += '</div>';

    if (_this.activePopupIds.length > 0) {
      $('#' + _this.activePopupIds[_this.activePopupIds.length - 1]).addClass('popupDisabled');
      $('#popup').append(html);
    } else {
      $('#popup').html(html);
      $('#popup').removeClass('hidden');
    }
    _this.activePopupIds.push(popupId);

    if (buttons) {
      buttons.forEach(button => {
        var params = { this: floorplan, id: button.id + popupId || '', label: button.id, class: 'popupButton', container: $('#popupButtons' + popupId) };

        if (button.preset) {
          if (button.preset == 'ok') {
            params.id = 'okButton' + popupId;
            params.label = 'ok';
            params.closeOnAction = true;
          } else if (button.preset == 'cancel') {
            params.id = 'cancelButton' + popupId;
            params.label = 'cancel';
            params.closeOnAction = true;
          } else if (button.preset == 'close') {
            params.id = 'closeButton' + popupId;
            params.label = 'close';
            params.closeOnAction = true;
          } else if (button.preset == 'add') {
            params.id = 'addButton' + popupId;
            params.icon = 'img/icon_add.png';
          } else if (button.preset == 'delete') {
            params.id = 'deleteButton' + popupId;
            params.icon = 'img/icon_delete.png';
          } else if (button.preset == 'edit') {
            params.id = 'editButton' + popupId;
            params.icon = 'img/icon_edit.png';
          } else if (button.preset == 'info') {
            params.id = 'infoButton' + popupId;
            params.icon = 'img/icon_info.png';
          }
        }
        button.forEach((value, property) => {
          params[property] = value;
        });
        this.setButton(params, function(evt) {
          if (params.action) params.action.call(params.this, params);
          if (params.closeOnAction) _this.closePopup();
        });
      });
    }
    if (arguments.length > 3) {
      $('#popup').off('click').click(function(evt) { //Click outside of the popupContent
        if (evt.target == this) {
          if (outsideAction && _.isFunction(outsideAction)) outsideAction(params);

          _this.closePopup();
        }
      });
    }
  },

  /**
   *
   */
  closePopup: function() {
    if (this.activePopupIds.length < 1) {
      console.log("No active popups.");
      return;
    }
    let activePopupId = this.activePopupIds.pop();
    $('#' + activePopupId).remove();
    if (this.activePopupIds.length > 0) {
      $('#' + this.activePopupIds[this.activePopupIds.length - 1]).removeClass('popupDisabled');
    } else {
      $('#popup').empty();
      $('#popup').addClass('hidden');
    }
  },

  /**
   *
   */
  loadingPopup: function() {
    this.openPopup(getText('loadingTitle'), getText('loadingMain'), null, null, 'loadingPopup');
  },

  /**
   *
   */
  messagePopup: function(title, content) {
    this.openPopup(title, content, [{ preset: 'ok', action: null }], null, 'messagePopup');
  },

  /**
   *
   */
  confirmPopup: function(title, content, confirmAction, cancelAction) {
    var _this = this;

    this.openPopup(title, content, [{ preset: 'ok', action: confirmAction }, { preset: 'cancel', action: cancelAction || null }], null, "confirmPopup");
  },

  /**
   *
   */
  getForm: function(formId, params) {
    var formObject = cloneObject(this._forms[formId]);

    formObject.forEach(formPart => {
      if (params) {
        params.forEach((values, property) => {
          if (formPart.group) {
            if (_.has(values[formPart.group], formPart.id)) formPart[property] = values[formPart.group][formPart.id];
          } else if (_.has(values, formPart.id)) {
            formPart[property] = values[formPart.id];
          }
        });
      }
    });
    return formObject;
  },

  /**
   *
   */
  createFormContent: function(contents) {
    var html = '<form>';

    contents.forEach(content => {
      var valueString = _.has(content, 'value') ? ' value="' + content.value + '"' : '';
      var placeholderString = _.has(content, 'placeholder') ? ' placeholder="' + content.placeholder + '"' : '';
      var formatString = _.has(content, 'format') ? ' format="' + content.format + '"' : '';
      var groupString = _.has(content, 'group') ? ' group="' + content.group + '"' : '';

      if (content.label !== '') {
        html += '<label for="' + content.id + '">' + getText(content.label || content.id) + ':</label>';
      }
      switch (content.type) {
        case 'inputText':
          html += '<input type="text" id="' + content.id + '" name="' + content.id + '"' + valueString + placeholderString + formatString + groupString + '><br>';
          break;
        case 'inputNumber':
          html += '<input type="number" id="' + content.id + '" name="' + content.id + '"' + valueString + placeholderString + formatString + groupString + '><br>';
          break;
        case 'inputDate':
          html += '<input type="date" id="' + content.id + '" name="' + content.id + '"' + valueString + placeholderString + formatString + groupString + '><br>';
          break;
      }
    });
    html += '</form>';

    return html;
  },

  /**
   *
   */
  getFormValues: function(form) {
    form = form || $('form');

    var inputs = form.find(':input');
    var values = {};

    for (let i = 0; i < inputs.length; i++) {
      var inputField = inputs[i];
      var value = inputField.value;
      var format = inputField.getAttribute('format') || '';
      var group = inputField.getAttribute('group') || '';

      if(value.length > 0){
        switch (format.toLowerCase()) {
          case 'number':
            value = Number(value);
            break;
        }
      }
      if (group) {
        if (!_.has(values, group)) values[group] = {};

        values[group][inputField.id] = value;
      } else {
        values[inputField.id] = value;
      }
    }
    return values;
  },

  /**
   *
   */
  setButton: function(params, action) {
    var _this = this;

    if (params.container) {
      var classArray = splitString(params.class || '');
      classArray.push('basicButton');
      if (params.icon) classArray.push('iconButton');

      var classString = classArray.length > 0 ? ' class="' + classArray.join(' ') + '"' : '';
      var label = params.icon ? '<img src="' + params.icon + '"></img>' : getText(params.label || params.id);

      params.container.append('<div id="' + params.id + '"' + classString + '>' + label + '</div>');
    }
    $('#' + params.id).off('click').click(function(evt) {
      if (action) {
        if (params.this) action.call(params.this, evt);
        else action(evt);
      }
    });
  }
};
