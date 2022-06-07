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
var views = {
	/**
	 *
	 */
	init: function() {
		var _this = this;

		this._currentView = '';
		this._backButtonTarget = '';

		views.hideElements('popupContainer');

		//Create footer buttons
		var footerButtons = ['mapButton', 'directionsButton', 'mapFiltersButton', 'arButton'];

		this.setButton({id: 'backButton', container: $('#footer')}, function(evt) {
			if(_this._backButtonTarget) {
				_this.open(_this._backButtonTarget);
			}
		});

		footerButtons.forEach(id => {
			this.setButton({id: id, container: $('#footer'), class: 'hidden', openId: id.slice(0, id.length - 6)});
		});

		this.setButton({id: 'mapFiltersPopupButton', container: $('#footer'), label: 'Popup'}, function() {
			_this.openPopup(map.showFilterList(), applyFilters);

			$('#applyFiltersButton').click(function(evt) {
				applyFilters();
			});
			function applyFilters() {
				var checkboxes = document.querySelectorAll('input[name="filterTags"]:checked');
				var filterTags = [];

				checkboxes.forEach(checkbox => {
					filterTags.push(checkbox.value);
				});
				map._activeFilters = filterTags;
				map.updateMarkers({tags: filterTags});

				views.closePopup();
			}
		});

		//TEST BUTTONS
		this.setButton({id: 'messageTestButton', class: 'test', container: $('#footer'), label: 'Message'}, function() {
			showMessage({"type": "message", "contents": [{"text": "testMessage"}, {"img": "img/ball.png"}]});
		});
		this.setButton({id: 'filtersTestButton_0', class: 'test', container: $('#footer'), label: 'No Filters'}, function() {
			map.updateMarkers({});
		});
		this.setButton({id: 'filtersTestButton_1', class: 'test', container: $('#footer'), label: 'alle 1km'}, function() {
			map.updateMarkers({maxDistance: 1000});
		});
	},

  /**
   *
   */
	open: function(id) {
		this.close();

		if(this._currentView) {
			this.setBackButton(this._currentView);
		}
		this._currentView = id;

		switch(id) {
			case 'frontPage':
				this.setBackButton('');
				this.showElements('frontPage, mapButton, arButton');
				break;
			case 'map':
				this.setBackButton('frontPage');
				this.showElements('mapContainer, directionsButton, mapFiltersButton, mapFiltersPopupButton');

				if(map._oldMarker) {
					for(var i in map._oldMarker._contentButtons) {
						$('#'+map._oldMarker._contentButtons[i]).removeClass('hidden');
					}
				}
				break;
			case 'directions':
				this.showElements('directionsContainer');
				break;
			case 'mapFilters':
				$('#contentContainer').html(map.showFilterList());

				this.setButton({id: 'filterListApplyButton', class: 'test', container: $('#contentContainer'), label: 'Apply'}, function() {
					var checkboxes = document.querySelectorAll('input[name="filterTags"]:checked');
					var filterTags = [];

					checkboxes.forEach(checkbox => {
						filterTags.push(checkbox.value);
					});
					map._activeFilters = filterTags;
					map.updateMarkers({tags: filterTags});

					views.open('map');
				});

				this.showElements('contentContainer');
				break;
			case 'ar':
				var ar = document.createElement('iframe');
				ar.setAttribute('id', 'ar');
				ar.setAttribute('src', 'arView.php?gps='+(map._geoLocationEnabled ? 1 : 0));
				document.getElementById('arContainer').appendChild(ar);
				document.getElementById('mapButton').classList.add('hidden');

				this.setBackButton('frontPage');
				this.showElements('arContainer');
				break;
			case 'content':
				this.setBackButton('map');
				this.showElements('contentContainer');
				break;
		}
	},

  /**
   *
   */
	close: function() {
		switch(this._currentView) {
			case 'frontPage':
				this.hideElements('frontPage, mapButton, arButton');
				break;
			case 'map':
				this.hideContentButtons();
				this.hideElements('mapContainer, directionsButton, mapFiltersButton, mapFiltersPopupButton');
				break;
			case 'directions':
				this.hideElements('directionsContainer');
				break;
			case 'mapFilters':
				$('#contentContainer').html = '';

				this.hideElements('contentContainer');
				break;
			case 'ar':
				var iFrame = window.parent.document.getElementById('ar');
				iFrame.parentNode.removeChild(iFrame);

				this.hideContentButtons();
				break;
			case 'content':
				this.hideElements('contentContainer');
				break;
		}
	},

  /**
   *
   */
	openPopup: function(content, closeAction) {
		var _this = this;
		var html = '<div id="popupContent" class="popupcontent">';
		html += content;
		html += '</div>';

		$('#popupContainer').removeClass('hidden');
		$('#popupContainer').html(html);

		this.setButton({id: 'popupCloseButton', container: $('#popupContent'), label: 'Close'}, function() {
			if(closeAction) closeAction();

			_this.closePopup();
		});
		$('#popupContainer').off('click').click(function(evt) { //Click outside of the popupContent
			if(evt.target == this) {
				if(closeAction) closeAction();

				_this.closePopup();
			}
		});
	},

  /**
   *
   */
	closePopup: function() {
		$('#popupContainer').html('');
		$('#popupContainer').addClass('hidden');
	},

  /**
   *
   */
	showElements: function(elementIds) {
		elements = _.isArray(elementIds) ? elementIds : splitString(elementIds);

		elements.forEach(id => {
			$('#'+id).removeClass('hidden');
		});
	},

  /**
   *
   */
	hideElements: function(elementIds) {
		elements = _.isArray(elementIds) ? elementIds : splitString(elementIds);

		elements.forEach(id => {
			$('#'+id).addClass('hidden');
		});
	},

  /**
   *
   */
	setBackButton: function(id) {
		this._backButtonTarget = id || '';

		if(this._backButtonTarget) {
			$('#backButton').removeClass('hidden');
		}
		else {
			$('#backButton').addClass('hidden');
		}
	},

  /**
   *
   */
	setButton: function(params, action) {
		var _this = this;

		if(params.container) {
			var label = params.label || params.id;
			var classArray = splitString(params.class || '');
			var classString = classArray.length > 0 ? ' class="'+classArray.join(' ')+'"' : '';

			params.container.append('<div id="'+params.id+'"'+classString+'>'+getText(label)+'</div>');
		}
		$('#'+params.id).off('click').click(function(evt) {
			if(params.openId) _this.open(params.openId);
			if(action) action(evt);
		});
	},

  /**
   *
   */
	addContentButton: function(buttonObject, id, action) {
		var label = buttonObject.label || buttonObject.type;

		$('#contentButtonContainer').append('<button id="'+id+'" class="contentButton hidden">'+getText(label)+'</button>');

		$('#'+id).click(action);
	},

  /**
   *
   */
	hideContentButtons: function() {
		$('#contentButtonContainer .contentButton').each(function() {
			$(this).addClass('hidden');
		});
	}
};
