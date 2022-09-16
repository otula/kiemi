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
var floorplan = {
  init: function(sites) {
    this._mode = '';
    this._zoomLevel = 1;
    this._data = { sites: {}, layers: {}, sensors: {}, siteLayers: {}, layerLayers: {}, layerSensors: {}, sensorData: {} };
    this._currentSiteId;
    this._currentLayerId;
    this.sensorStoppedOnUnpositioned = false;
    this.processUrlParameters();

    var _this = this;

    var contentHtml = '<div id="floorplan"></div>';
    contentHtml += '<div id="zoomLevel"></div>';

    var sidebarHtml = '<div id="sidebarButtons"></div><div id="sidebarContent"><div id="layerTree"></div></div>';

    sites.forEach(site => {
      this._data.sites[site.id] = site;
    });
    this._sitesDropdown = new dropdown('', [], { ignoreSame: 1 }, function(value) {
      _this.selectSite(Number(value));
    });
    this.updateSitesDropdown();

    var tools = ['addSite', 'editSite'];

    this._editDropdown = new dropdown(getText('site'), tools, {}, function(value) {
      switch (value) {
        case 'addSite':
          _this.addSite();
          break;
        case 'editSite':
          _this.editSite();
          break;
      }
    });
    this._filtersDropdown = new dropdown(getText('view'), config.sensorFilters.default, { ignoreSame: 1 }, function(value) {
      _this.filterSensors(value);
    });
    this._filtersDropdown.value = config.sensorFilters.default[0];

    $('#header').append(this._sitesDropdown.elem);
    $('#header').append(this._editDropdown.elem);
    $('#header').append(this._filtersDropdown.elem);
    $('#header').append('<div class="hidden" id="linkbar"></div>');

    $('#content').append(contentHtml);
    $('#sidebar').append(sidebarHtml);
    $('#floorplan').addClass('unselectable');

    $('#content').on('click', function(evt) {
      if (!_this._isDragging) {
        if (evt.target === evt.currentTarget) { //Add sensor to unpositioned list
          _this.addSensor(_this._currentLayerId);
        } else if (evt.target === $('#floorplan img')[0]) { //Add sensor to floorplan coordinates
          var offset = $(evt.currentTarget).offset();
          _this.addSensor(_this._currentLayerId, evt.pageX - offset.left, evt.pageY - offset.top);
        } else if (evt.target.classList.contains('sensor-item')) { //A sensor clicked -> edit that
          var sensorElem = $(evt.target).parent();
          var sensorId = Number(sensorElem.attr('id').split('_')[1]);
          _this.viewSensorPopup(sensorId);
        }
      }
      _this._isDragging = false;
    });

    this.updateLayerButtons();

    if (config.defaultSiteId != null) {
      this.selectSite(config.defaultSiteId);
      config.defaultSiteId = null;
    }

    this._unpositionedSensors = new customPopup(getText('unpositionedSensors'), '<div id="unpositionedSensors"></div>', $('#content'), { draggable: true, dragArea: '#content', closeButton: true, x: 0, y: 99999 });

    // Added a legend to show the measurements available from the sensors,
    // and the colors used to mark them.
    this._legend = new customPopup(getText('legendHeader'), '<div id="legend"></div>', $('#content'), { draggable: true, dragArea: '#content', closeButton: true, x: 0, y: 0 });

    //Sensors can be dropped into the unpositionedSensors popup
    this._unpositionedSensors._elem.droppable({
      accept: '.sensor, .sensor-item',
      drop: function(evt, ui) {
        floorplan.sensorStoppedOnUnpositioned = true;
        var sensorId = Number(ui.draggable.attr('id').split('_')[1]);
        $('#sensor_' + sensorId).addClass('unpositionedSensor');
        _this.editSensorPosition(sensorId, null, null, null);
      }
    });

    //Extend draggable function to have an event "beforeStart" that triggers before the drag starts
    //https://stackoverflow.com/questions/16715748/jquery-ui-before-start-draggable
    var oldMouseStart = $.ui.draggable.prototype._mouseStart;

    $.ui.draggable.prototype._mouseStart = function(event, overrideHandle, noActivation) {
      this._trigger("beforeStart", event, this._uiHash());
      oldMouseStart.apply(this, [event, overrideHandle, noActivation]);
    };
  },

  /**
   *
   */
  processUrlParameters: function() {
    var params = getURLParameters();
    if (params.siteId != null) {
      config.defaultSiteId = params.siteId;
    }
    if (params.layerId != null) {
      config.defaultLayerId = params.layerId;
    }
    if (params.sensorId != null) {
      config.defaultSensorId = params.sensorId;
    }
    if (params.dataDropEnabled != null) {
      config.dataDrop.enabled = params.dataDropEnabled;
    }
    if (params.sensorDataFrom != null) {
      config.sensorDataRange.from = params.sensorDataFrom;
    }
    if (params.sensorDataTo != null) {
      config.sensorDataRange.to = params.sensorDataTo;
    }
  },

  /**
   *
   */
  addSensortypeToLegend: function(sensorType) {
    let dataForLegend = [{
        type: 'ruuvi',
        availableMeasurements: 'T, p, RH%'
      },
      {
        type: 'aranet',
        availableMeasurements: 'T, p, RH%, CO2'
      },
      {
        type: 'ouman',
        availableMeasurements: 'CO2, VOC'
      },
      {
        type: 'porienergia',
        availableMeasurements: 'T, RH%'
      },
      {
        type: 'fmi',
        availableMeasurements: 'T, p, RH%, wind, rain, clouds'
      },
      {
        type: 'fourdeg',
        availableMeasurements: 'T (cur.), T (set), valve, signal, battery'
      }
    ];
    const measurement = dataForLegend.filter(m => m.type === sensorType);
    let html = `<div class="legend-${sensorType} legend-item">`;
    html += `<div class="legend-color-${sensorType}"></div>`;
    html += `<div class="legend-text">${sensorType}:&nbsp;</div>`;
    measurement.length === 1 ? html += `<div class="legend-measurements">${measurement[0].availableMeasurements}</div>` : html += `<div class="legend-measurements"></div>`;
    html += '</div>';
    $('#legend').append(html);
  },

  /**
   *
   */
  updateSitesDropdown: function(siteId) {
    var siteList = [];

    for (let i in this._data.sites) {
      var site = this._data.sites[i];

      siteList.push({ value: site.id, label: getText(site.name) });
    }
    if (siteList.length > 0) {
      sortArray(siteList, 'label', 'alphabetic');

      this._sitesDropdown.items = siteList;
      this._sitesDropdown.label = getText('noSiteSelected');
    } else {
      this._sitesDropdown.label = getText('noSitesAvailable');
    }
    this.selectSite(arguments.length > 0 ? siteId : null);
  },

  /**
   *
   */
  updateLayerButtons: function() {
    var addParams = { this: this, id: 'layerButtonAdd', icon: 'img/icon_add.png', class: 'sidebarButton', container: $('#sidebarButtons') };
    var editParams = { this: this, id: 'layerButtonEdit', icon: 'img/icon_edit.png', class: 'sidebarButton', container: $('#sidebarButtons') };

    ui.setButton(addParams, function(evt) {
      if (this._currentLayerId >= 0) this.addLayer(this._currentLayerId);
      else this.addLayer();
    });

    ui.setButton(editParams, function(evt) {
      if (this._currentLayerId >= 0) this.editLayer(this._currentLayerId);
    });
  },

  /**
   *
   * If selectLayerId is defined, that layer is automatically selected after the update.
   * If the selectLayerId === 'first' -> Select the topmost layer in the list
   */
  updateLayerList: function(selectLayerId) {
    var _this = this;
    var tree = [];

    function getLayer(layerId) {
      var node = { id: layerId, index: this._data.layers[layerId].index, name: this._data.layers[layerId].name, children: [] };

      this._data.layerLayers[layerId].forEach(subLayerId => {
        node.children.push(getLayer.call(this, subLayerId));
      });
      sortArray(node.children, 'index');

      return node;
    }
    this._data.siteLayers[this._currentSiteId].forEach(siteLayerId => {
      tree.push(getLayer.call(this, siteLayerId));
    });
    sortArray(tree, 'index');

    $('#layerTree').tree('destroy');
    $('#layerTree').tree({
      data: tree,
      autoOpen: 0,
      dragAndDrop: false,
      onCreateLi: function(node, $li) { //dragAndDrop disabled as layer rearrange not implemented yet
        $li.addClass('layerTree-item');
      }
    });

    $('#layerTree').on('tree.select', function(evt) {
      if (evt.node) {
        _this.selectLayer(evt.node.id, true);
      } else {
        _this.deselectLayer();
      }
    });
    if (arguments.length > 0) {
      if (selectLayerId === 'first' && tree.length > 0) selectLayerId = tree[0].id;

      this.selectLayer(selectLayerId);
    }
  },

  /**
   *
   */
  selectSite: function(siteId) {
    if (arguments.length === 0 || siteId === null) {
      this.deselectSite();
    } else if (!_.has(this._data.siteLayers, siteId)) { //The site's content has not yet been loaded
      ui.loadingPopup();

      this.loadSiteData(siteId, () => {
        this.selectSite(siteId);
        ui.closePopup();
      });
    } else { //The data has been loaded -> create sidebar content
      let site = this._data.sites[siteId];
      this.updateLinkBar(site.externalUrl, null, null);
      let serviceTypeSet = new Set();
      let layerSet = new Set();

      for (let i in this._data.siteLayers[siteId]) { //find the first level of layers
        let siteLayer = this._data.siteLayers[siteId][i]
        layerSet.add(siteLayer);
        this.recurseLayers(layerSet, this._data.layerLayers, siteLayer); //find subsequent levels recursively
      }

      for (let layerId of layerSet) { // inspect all layers found for appearing sensor types
        let layerSensors = this._data.layerSensors[layerId];
        for (let j of layerSensors) {
          let serviceType = this._data.sensors[j].serviceType;
          if (config.sensorFilters.hasOwnProperty(serviceType)) {
            serviceTypeSet.add(serviceType);
          } else {
            console.log("Service type was not found, skip...", serviceType);
          }
        }
      }
      if (serviceTypeSet.size < 1) { //if no other types were found, add default
        serviceTypeSet.add('default');
      }
      let filters = new Set();
      for (let sensor of serviceTypeSet) {
        for (let item in config.sensorFilters[sensor]) {
          filters.add(config.sensorFilters[sensor][item]);
        }
      }

      // Adding sensor types to legend
      $('#legend').empty();
      for (let sensor of serviceTypeSet) {
        this.addSensortypeToLegend(sensor);
      }

      let sensorFilters = Array.from(filters)

      this._filtersDropdown.items = sensorFilters;
      this._filtersDropdown.value = sensorFilters[0];

      this._currentSiteId = siteId;
      this._sitesDropdown.label = getText(site.name);

      this.deselectLayer();
      if (config.defaultLayerId != null) {
        let id = config.defaultLayerId;
        config.defaultLayerId = null;
        this.updateLayerList(id);
      } else {
        this.updateLayerList('first');
      }
    }
  },

  /**
   *
   */
  updateLinkBar: function(externalUrl, latitude, longitude) {
    var linkbar = $('#linkbar');
    linkbar.empty();

    linkbar.append('<img id="dateTimeSelectionImg" alt="dateTimeSelection" src="/img/calendar.svg" height="20" role="button" tabIndex=666 >');

    const form = ui.getForm('dateTimeLimitsSelection');

    document.getElementById("dateTimeSelectionImg").addEventListener("click", function() {
      ui.openPopup(getText('dateTimeSelectionPopupHeader'), ui.createFormContent(form), [{
          preset: 'ok',
          action: function() {
						let offset = (new Date()).getTimezoneOffset()*-60000;
            if (document.getElementById("Start date").value !== "") {
							let from = new Date(document.getElementById("Start date").value);
              config.sensorDataRange.from = new Date(from.getTime()-offset);
            }
            if (document.getElementById("End date").value) {
							let to = new Date(document.getElementById("End date").value);
              config.sensorDataRange.to = new Date(to.getTime()+config.dataTimeSelectorToOffset-offset);
            }
          }
        },
        { preset: 'cancel' }
      ], null, 'editdateTimeSelectionPopup');

    });


    if (externalUrl) {
      linkbar.append('<a href="' + externalUrl + '" target="_blank"><img alt="externalUrl" src="/img/link-45deg.svg" height="20"></a>');
    }
    if (latitude && longitude) {
      linkbar.append('<a href="' + config.mapUrl + config.mapParamLatitude + latitude + config.mapParamLongitude + longitude + '" target="_blank"><img alt="mapUrl" src="/img/geo-alt-fill.svg" height="20"></a>');
    }
    linkbar.removeClass("hidden");
  },

  /**
   *
   */
  recurseLayers: function(set, layers, layerId) {
    for (let layer of layers[layerId]) { // finds layers in layers
      set.add(layer);
      this.recurseLayers(set, layers, layer);
    }
  },

  /**
   *
   */
  deselectSite: function() {
    this._currentSiteId = null;
    this._sitesDropdown.label = getText('noSiteSelected');

    this.deselectLayer();

    $('#layerTree').tree('destroy');
  },

  /**
   *
   */
  addSite: function() {
    var form = ui.getForm('site');

    ui.openPopup(getText('addSite'), ui.createFormContent(form), [{
        preset: 'ok',
        action: function() {
          sendData('POST', 'sites', ui.getFormValues(), { context: this }, function(data) {
            var newSite = data[0];

            this._currentSiteId = newSite;
            this._data.sites[newSite.id] = newSite;
            this._data.siteLayers[newSite.id] = [];

            this.updateSitesDropdown(newSite.id);
          });
        }
      },
      { preset: 'cancel' }
    ], null, 'addSitePopup');
  },

  /**
   *
   */
  editSite: function(siteId) {
    siteId = arguments.length > 0 ? siteId : this._currentSiteId;

    var site = this._data.sites[siteId];

    if (!site) return;

    var form = ui.getForm('site', { value: site });

    ui.openPopup(getText('editSite', { site: '<b>' + site.name + '</b>' }), ui.createFormContent(form), [
      { preset: 'delete', action: this.deleteSite },
      {
        preset: 'ok',
        action: function() {
          var newData = ui.getFormValues();

          sendData('PUT', 'sites/' + siteId, newData, { context: this }, function(data) {
            newData.id = siteId;

            this._data.sites[siteId] = newData;

            this.updateSitesDropdown(siteId);
          });
        }
      },
      { preset: 'cancel' }
    ], null, 'editSitePopup');
  },

  /**
   *
   */
  deleteSite: function(siteId) {
    siteId = arguments.length > 0 ? siteId : this._currentSiteId;

    var site = this._data.sites[siteId];

    if (!site) return;

    var text = getText('deleteSiteConfirmText', { site: '<b>' + site.name + '</b>' });

    ui.confirmPopup(getText('deleteSiteConfirm'), text, function() {
      sendData('DELETE', 'sites/' + site.id, null, null, null);

      this._data.siteLayers[site.id].forEach((layerId) => {
        this.deleteLayer(layerId, true);
      });
      delete this._data.sites[site.id];
      delete this._data.siteLayers[site.id];

      this.updateSitesDropdown(null);
      this.deselectSite();

      ui.closePopup();
    });
  },

  /**
   *
   */
  selectLayer: function(layerId, isTreeClick) {
    var layer = this._data.layers[layerId];

    this.deselectLayer();

    if (!layer) return //No available layer;

    this._currentLayerId = layerId;

    this.updateLinkBar((layer.externalUrl ? layer.externalUrl : this._data.sites[this._currentSiteId].externalUrl), layer.latitude, layer.longitude);

    if (!isTreeClick) { //Select the tree node
      var node = $('#layerTree').tree('getNodeById', layerId);
      $('#layerTree').tree('selectNode', node);
      return;
    }

    this.loadFloorplanImage(layerId, () => {
      // this.filterSensors(this._filtersDropdown.value, layerId);
    });

    function recursiveCreateSensor(layerId) {
      this._data.layerSensors[layerId].forEach((sensorId) => {
        this.createSensor(this._data.sensors[sensorId]);
      });
      if (this._data.layerLayers[layerId]) {
        this._data.layerLayers[layerId].forEach((subLayerId) => {
          if (this._data.layers[subLayerId] && (!this._data.layers[subLayerId].image || !this._data.layers[subLayerId].image.url)) {
            recursiveCreateSensor.call(this, subLayerId);
          }
        });
      }
    }
    recursiveCreateSensor.call(this, layerId);

    $('.sensor').hover((evt) => {
      var sensorId = Number(evt.currentTarget.id.split('_')[1]);
      var sensorElem = $('#sensor_' + sensorId);

      if (!sensorElem.hasClass('sensor-dragged')) {
        var tooltip = $(this.createSensorDataTooltip(sensorId));
        var container = $('#content');
        var sensorElemOffset = sensorElem.offset();
        var containerOffset = sensorElem.parent().offset();

        $('#content').append(tooltip);

        var posX = sensorElemOffset.left - containerOffset.left + sensorElem.width() + 10;
        var posY = sensorElemOffset.top - containerOffset.top - (tooltip.height() - sensorElem.height()) / 2;

        if (posX > container.width() - tooltip.width()) {
          posX = sensorElemOffset.left - containerOffset.left - tooltip.width() - 20;
        }
        if (posY < 0) {
          posY = 10;
        } else if (sensorElemOffset.top > container.height() - (tooltip.height() + sensorElem.height()) / 2) {
          posY = container.height() - tooltip.height() - 20;
        }
        $('#sensorDataTooltip').css({ left: posX, top: posY });
      }
    }, (evt) => {
      $('#sensorDataTooltip').remove();
    });
  },

  /**
   *
   */
  deselectLayer: function() {
    this._currentLayerId = -1;

    $('#floorplan').html('');
    $('#floorplan').off('mousedown');
    $('#unpositionedSensors').html('');
    $('.sensor').remove();
  },

  /**
   *
   */
  addLayer: function(parentLayerId) {
    var siteId = this._currentSiteId;
    var isSiteLayer = arguments.length === 0;
    var layerValues = parentLayerId ? this.getLayerValues(parentLayerId, ['name']) : { values: {}, inheritValues: {} };
    var form = ui.getForm('layer', { placeholder: _.extend(layerValues.values, layerValues.inheritValues) });

    ui.openPopup(getText('addLayer'), ui.createFormContent(form), [{
        preset: 'ok',
        closeOnAction: 0,
        action: function() {
          var params = ui.getFormValues();
          params.type = 'floor'; //Use static for now. Possible values ['unknown', 'outdoor', 'building', 'floor', 'room']

          if (params.name !== '') {
            if (isSiteLayer) { //Add layer directly under the currently open site
              params.index = this._data.siteLayers[siteId].length;

              sendData('POST', 'sites/' + siteId + '/layers', params, { context: this }, function(layerData) {
                let newLayer = layerData[0];
                newLayer.parentSite = siteId;
                newLayer.parentLayer = -1;

                this._currentLayerId = newLayer.id;
                this._data.layers[newLayer.id] = newLayer;
                this._data.siteLayers[siteId].push(newLayer.id);
                this._data.layerLayers[newLayer.id] = [];
                this._data.layerSensors[newLayer.id] = [];

                $('#layerTree').tree('appendNode', { name: newLayer.name, id: newLayer.id, index: newLayer.index });

                this.selectLayer(newLayer.id);
              });
            } else { //Add layer under another layer
              if (!_.has(this._data.layerLayers, 'parentLayerId')) this._data.layerLayers[parentLayerId] = [];

              params.index = this._data.layerLayers[parentLayerId].length;

              sendData('POST', 'layers/' + parentLayerId + '/layers', params, { context: this }, function(layerData) {
                let parentNode = $('#layerTree').tree('getNodeById', parentLayerId);
                let newLayer = layerData[0];
                newLayer.parentSite = siteId;
                newLayer.parentLayer = parentLayerId;

                this._currentLayerId = newLayer.id;
                this._data.layers[newLayer.id] = newLayer;
                this._data.layerLayers[parentLayerId].push(newLayer.id);
                this._data.layerLayers[newLayer.id] = [];
                this._data.layerSensors[newLayer.id] = [];

                $('#layerTree').tree('appendNode', { name: newLayer.name, id: newLayer.id, index: newLayer.index }, parentNode);
                $('#layerTree').tree('openNode', parentNode);

                this.selectLayer(newLayer.id);
              });
            }
            ui.closePopup();
          } else {
            ui.messagePopup(getText('invalidValueTitle'), getText('invalidValueMain', { property: getText('name') }));
          }
        }
      },
      { preset: 'cancel' }
    ], null, 'addLayerPopup');
  },

  /**
   *
   */
  editLayer: function(layerId) {
    var layer = this._data.layers[layerId];

    if (!layer) return;

    var layerValues = this.getLayerValues(layerId);
    var form = ui.getForm('layer', { value: layerValues.values, placeholder: layerValues.inheritValues });

    ui.openPopup(getText('editLayer', { layer: '<b>' + layer.name + '</b>' }), ui.createFormContent(form), [
      { preset: 'delete', action: this.deleteLayerPopup },
      {
        preset: 'ok',
        closeOnAction: 0,
        action: function() {
          var params = ui.getFormValues();
          params.index = layer.index;
          params.type = 'floor'; //Use static for now. Possible values ['unknown', 'outdoor', 'building', 'floor', 'room']

          if (params.name !== '') {
            sendData('PUT', 'layers/' + layerId, params, { context: this }, function() {
              var oldFloorplanImage = (layer.image && layer.image.url) ? layer.image.url : '';
              var node = $('#layerTree').tree('getNodeById', layerId);

              params.parentLayer = layer.parentLayer;

              this._data.layers[layerId] = params;

              $('#layerTree').tree('updateNode', node, params.name);

              if (params.image && (params.image.url !== oldFloorplanImage)) {
                this.loadFloorplanImage(layerId, () => {
                  // this.filterSensors(this._filtersDropdown.value, layerId);
                });
              }
            });
            ui.closePopup();
          } else {
            ui.messagePopup(getText('invalidValueTitle'), getText('invalidValueMain', { property: getText('name') }));
          }
        }
      },
      { preset: 'cancel' }
    ], null, 'editLayerPopup');
  },

  /**
   *
   */
  deleteLayerPopup: function(layerId) {
    var layer = this._data.layers[arguments.length === 0 ? this._currentLayerId : layerId];

    if (!layer) return;

    var text = getText('deleteLayerConfirmText', { layer: '<b>' + layer.name + '</b>' });

    ui.confirmPopup(getText('deleteLayerConfirm'), text, function() {
      this.deleteLayer(layer.id);
      this.deselectLayer();

      ui.closePopup();
    });
  },

  /**
   *
   */
  deleteLayer: function(layerId, localOnly) {
    var node = $('#layerTree').tree('getNodeById', layerId);

    if (node) $('#layerTree').tree('removeNode', node);

    if (layerId === this._currentLayerId) this._currentLayerId = -1;

    var parentSiteId = this._data.layers[layerId].parentSite;

    if (parentSiteId >= 0) {
      this._data.siteLayers[parentSiteId] = _.reject(this._data.siteLayers[parentSiteId], (id) => (id === layerId));
    }
    if (this._data.layerLayers[layerId]) {
      this._data.layerLayers[layerId].forEach((subLayerId) => {
        this.deleteLayer(subLayerId, true);
      });
      delete this._data.layerLayers[layerId];
    }
    this._data.layerSensors[layerId].forEach(sensorId => {
      this.deleteSensor(sensorId, localOnly);
    });
    delete this._data.layerSensors[layerId];

    if (!localOnly) {
      sendData('DELETE', 'layers/' + layerId, null, null, null);
    }
    delete this._data.layers[layerId];
  },

  /**
   *
   */
  getLayerValues(layerId, ignoreProperties) {
    ignoreProperties = ignoreProperties || [];

    var layer = this._data.layers[layerId];
    var layerValues = {};
    var inheritValues = {};

    layer.forEach((value, property) => {
      if (!_.includes(ignoreProperties, property)) {
        if (_.isObject(value)) { //The value is an object that has subvalues
          value.forEach((subValue, subProperty) => {
            if (subValue !== '') {
              if (!_.has(layerValues, property)) layerValues[property] = {};
              layerValues[property][subProperty] = subValue;
            }
            if (layer.parentLayer >= 0) {
              let recursiveValue = this.getRecursiveLayerValue(layer.parentLayer, property, subProperty);

              if (recursiveValue !== null) {
                if (!_.has(inheritValues, property)) inheritValues[property] = {};
                inheritValues[property][subProperty] = recursiveValue;
              }
            }
          });
        } else if (value !== '' && value !== null) {
          layerValues[property] = value;
        }
        if (layer.parentLayer >= 0) {
          let recursiveValue = this.getRecursiveLayerValue(layer.parentLayer, property);

          if (recursiveValue !== null) {
            inheritValues[property] = recursiveValue;
          }
        }
      }
    });
    return { values: layerValues, inheritValues: inheritValues };
  },
  //Get property value for the layer. If the layer doesn't have a value, get it from its parent. Or its parent's parent etc.
  //If the value is an object, defining subProperty seeks for the value object's property instead
  getRecursiveLayerValue(layerId, property, subProperty) {
    var _this = this;
    var hasSubProperty = arguments.length > 2;

    function getLayerValue(layerId) {
      var layer = _this._data.layers[layerId];
      if (_.has(layer, property) && layer[property] !== '' && (!hasSubProperty || layer[property][subProperty])) {
        return hasSubProperty ? layer[property][subProperty] : layer[property];
      } else if (layer.parentLayer >= 0) {
        return getLayerValue(layer.parentLayer);
      }
      return null;
    }
    return getLayerValue(layerId);
  },

  /**
   *
   */
  addSensor: function(layerId, posX, posY, posZ) {
    if (!this._data.layers[layerId]) return;

    posX = arguments.length > 1 ? Math.floor(posX) : null;
    posY = arguments.length > 2 ? Math.floor(posY) : null;
    posZ = arguments.length > 3 ? Math.floor(posZ) : null;

    if (!_.isNull(posX)) posX = (posX - this._zoomPosX) / this._zoomLevel;
    if (!_.isNull(posY)) posY = (posY - this._zoomPosY) / this._zoomLevel;
    var form = ui.getForm('sensor', { value: { serviceType: 'ruuvi' } }); //Default to ruuvi

    ui.openPopup(getText('addSensor'), ui.createFormContent(form), [{
        preset: 'ok',
        action: function() {
          var params = ui.getFormValues();
          params.x = posX;
          params.y = posY;
          params.z = posZ;

          if (params.externalId == '') params.externalId = (params.name || '-');

          sendData('POST', 'layers/' + layerId + '/sensors', params, { context: this }, function(data) {
            var newSensor = data[0];
            newSensor.parentLayer = layerId;

            this._data.sensors[newSensor.id] = newSensor;
            this._data.layerSensors[layerId].push(newSensor.id);
            this._data.sensorData[newSensor.id] = [];

            this.createSensor(newSensor);
            this.filterSensors(this._filtersDropdown.value, layerId);
          });
        }
      },
      { preset: 'cancel' }
    ], null, 'addSensorPopup');
  },

  /**
   *
   */
  editSensor: function(sensorId) {
    var sensor = this._data.sensors[sensorId];

    if (!sensor) return;

    var form = ui.getForm('sensor', { value: sensor });

    ui.openPopup(getText('editSensor', { sensor: '<b>' + sensor.name + '</b>' }), ui.createFormContent(form), [{
        preset: 'delete',
        action: function() {
          this.deleteSensorPopup(sensorId);
        }
      },
      {
        preset: 'ok',
        action: function() {
          var params = ui.getFormValues();
          params.id = sensorId;
          params.x = sensor.x || 0;
          params.y = sensor.y || 0;
          params.z = sensor.z || 0;

          sendData('PUT', 'sensors/' + sensorId, params, { context: this }, function(data) {
            params.parentLayer = sensor.parentLayer;

            this._data.sensors[sensorId] = params;

            var sensorElem = $('#sensor_' + sensorId);
            sensorElem.find('.sensor-name').text(params.name);
            sensorElem.find('.sensor-externalId').text(params.externalId);
          });
        }
      },
      { preset: 'cancel' }
    ], null, 'editSensorPopup');
  },

  /**
   *
   */
  deleteSensorPopup: function(sensorId) {
    var sensor = this._data.sensors[sensorId];

    if (!sensor) return;

    var layer = this._data.layers[sensor.parentLayer];
    var text = getText('deleteSensorConfirmText', { layer: layer.name, sensor: sensor.name, externalId: sensor.externalId });

    ui.confirmPopup(getText('deleteSensorConfirm', { sensor: sensor.name, externalId: sensor.externalId }), text, function() {
      this.deleteSensor(sensorId);

      ui.closePopup();
    });
  },

  /**
   *
   */
  deleteSensor: function(sensorId, localOnly) {
    var sensor = this._data.sensors[sensorId];

    if (!localOnly) {
      sendData('DELETE', 'sensors/' + sensorId, null, null, null);
    }
    delete this._data.sensors[sensorId];
    this._data.layerSensors[sensor.parentLayer] = _.without(this._data.layerSensors[sensor.parentLayer], sensorId);
    if (this._data.sensorData[sensorId]) delete this._data.sensorData[sensorId];

    var sensorElem = $('#sensor_' + sensorId);
    var sensorContainer = sensorElem.parent();

    if (sensorContainer.hasClass('unpositionedSensorContainer')) {
      sensorContainer.remove();
    }
    sensorElem.remove();
  },

  /**
   *
   */
  viewSensorPopup: function(sensorId) {
    var fromDaysOptions = [1, 5, 14, 30];
    var _this = this;

    this.loadSensorData(sensorId, (data) => {
      var sensor = this._data.sensors[sensorId];
      var dataList = config.sensorViewData[sensor.serviceType] || config.sensorViewData.default;
      var html = '<div id="sensorViewData"><table>';

      let temperature = null;
      let humidity = null;
      dataList.forEach((property) => {
        var sensorDataObject = this.getSensorDataObject(property, sensor.serviceType);
        var activeAlert = _this.getActiveAlert(sensorId, sensorDataObject);
        var dataObject = (_.isArray(data) && data.length > 0) ? data[0] : data || {};
        var value = _.has(dataObject, sensorDataObject.dataId) ? dataObject[sensorDataObject.dataId] : ' - ';
        var unit = sensorDataObject.unit ? ' ' + sensorDataObject.unit : ''; // will add space between unit and value
        var skipSensorViewGraph = false;

        if (property == 'communication') {
          value = this.getReadableTime(value);
          skipSensorViewGraph = true; // do not add button if value is of type communication
        } else if (property == 'temperature') {
          temperature = value;
        } else if (property == 'humidity') {
          humidity = value;
        }
        if (_.isUndefined(value) || _.isNull(value)) {
          value = '-';
          unit = '';
          skipSensorViewGraph = true; // do not add button if value is not available
        } else if (isNaN(value)) {
          skipSensorViewGraph = true; // do not add button if value contains some magic string
        }
        html += '<tr><td>' + (activeAlert ? '<span class="alert-' + activeAlert + '"></span>' : '') + '</td>';
        let roundingDigits = (isNaN(sensorDataObject.roundingDigits) ? config.sensorData.defaultRoundingDigits : sensorDataObject.roundingDigits);
        html += '<td>' + getText(property) + ': </td><td>' + (isNaN(value) ? value : value.toFixed(roundingDigits)) + unit + '</td>';

        // add column containing the graph button (skip if type is communication, i.e. timestamp; or value is undefined, or some other reason mentioned above)
        if (skipSensorViewGraph === true) {
          html += '<td>&nbsp;</td>';
        } else {
          html += '<td><div class="sensorViewGraph basicButton" dataId="' + property + '"></div></td>';
        }
        html += '</tr>';
      });
      if (temperature != null && humidity != null && config.thermalComfort.limits.length > 0) { //TODO put something as value? is the current value in the "zone"?
        html += '<tr><td></td><td>Thermal comfort: </td><td>';
        for (let limit of config.thermalComfort.limits) {
          if (xyutils.inside([temperature, humidity], limit.polygon)) {
            html += '<span style="color:' + limit.color + '">' + config.thermalComfort.insideText + '</span>';
          } else {
            html += '<span style="color:' + limit.color + '">' + config.thermalComfort.outsideText + '</span>';
          }
        }
        html += '</td><td><div class="sensorViewGraph basicButton" dataid="thermalComfort"></div></td></tr>';
      }
      html += '</table></div>';

      ui.openPopup(getText('viewSensor', { sensor: (sensor.name || sensor.externalId) }), html, [{
          preset: 'edit',
          action: function() {
            ui.closePopup(); //The system doesn't support 3 level popups (view -> edit -> confirm), so need to remove one level (view)

            this.editSensor(sensorId);
          }
        },
        { preset: 'close' }
      ], null, 'viewSensorPopup');

      $('#sensorViewData table td:nth-child(4)').each((index, cell) => {
        $(cell).find('.sensorViewGraph').off('click').click(function(evt) {
          let dataId = this.getAttribute('dataId');
          let daysButtons = [];

          fromDaysOptions.forEach((days) => {
            daysButtons.push({
              label: days + 'd',
              id: 'daysButton_' + days,
              value: days,
              action: function(params) {
                if (dataId == 'thermalComfort') {
                  _this.viewThermalComfortChartPopup(sensor, days);
                } else {
                  _this.viewSensorChartPopup(sensor, dataId, days);
                }
              }
            });
          });
          daysButtons.push({
            label: '? => ?',
            id: 'daysButtonCustomInterval',
            action: function(params) {
              let from = new Date(document.getElementById('sensorDataFrom').value);
              let to = new Date(document.getElementById('sensorDataTo').value);
              if ((from instanceof Date && !isNaN(from.valueOf())) && (to instanceof Date && !isNaN(to.valueOf()))) {
                if (dataId == 'thermalComfort') {
                  _this.viewThermalComfortChartPopup(sensor, null, from, to);
                } else {
                  _this.viewSensorChartPopup(sensor, dataId, null, from, to);
                };
              } else {
                console.log("Invalid from or to.");
              }
            }
          });
          daysButtons.push({ preset: 'close' });

          let html = '<div id="sensorPropertyChart"><canvas id="sensorPropertyChartCanvas"></canvas></div>';
          html += '<div id="sensorDataIntervalSelectors"><input id="sensorDataFrom" placeholder="ISO8601" type="text" value="' + (config.sensorDataRange.from != null ? config.sensorDataRange.from.toISOString() : "") + '"><span> => </span><input id="sensorDataTo" type="text" placeholder="ISO8601" value="' + (config.sensorDataRange.to != null ? config.sensorDataRange.to.toISOString() : "") + '"></div>';

          html += '<div id="sensorDataStatistics" class="hidden">Avg. <span id="sensorDataAverage">' + getText("notANumber") + '</span>, SD. <span id="sensorDataStandardDeviation">' + getText("notANumber") + '</span>, MD. <span id="sensorDataMedian">' + getText("notANumber") + '</span>, Min. <span id="sensorDataMin">' + getText("notANumber") + '</span>, Max. <span id="sensorDataMax">' + getText("notANumber") + '</span>, ' + getText("measurementCount") + ': <span id="sensorDataCount">' + getText("notANumber") + '</span></div>';
          html += '<div id="sensorDataTCStatistics" class="hidden">' + getText("measurementCount") + ': <span id="sensorDataTCTotal">' + getText("notANumber") + '</span>, ' + getText("measurementsOutside") + ' : <span id="sensorDataTCOut">' + getText("notANumber") + '</span></div>';

          ui.openPopup(getText('viewSensorProperty', { property: getText(dataId), sensor: (sensor.name || sensor.externalId) }), html, daysButtons, null, 'viewSensorPropertyPopup');

          if (config.sensorDataRange.from || config.sensorDataRange.to) {
            if (dataId == 'thermalComfort') {
              _this.viewThermalComfortChartPopup(sensor, null, config.sensorDataRange.from, config.sensorDataRange.to);
            } else {
              _this.viewSensorChartPopup(sensor, dataId, null, config.sensorDataRange.from, config.sensorDataRange.to);
            }
          } else if (dataId == 'thermalComfort') {
            _this.viewThermalComfortChartPopup(sensor, 1);
          } else {
            _this.viewSensorChartPopup(sensor, dataId, 1);
          }
        });
      });
    });
  },

  /**
   *
   */
  viewSensorChartPopup: function(sensor, dataId, days, from, to) {
    if (this._popupChart) {
      document.getElementById("sensorPropertyChart").innerHTML = '<canvas id="sensorPropertyChartCanvas"></canvas>'; // something happens to the canvas when destroy() is called causing issues with plugins, so trash the old canvas and make a new one
      this._popupChart.destroy();
    }

    $('.popupButton.active').removeClass('active');
    if (days) {
      $('#daysButton_' + days).addClass('active');
    } else if (from && to) {
      $('#daysButtonCustomInterval').addClass('active');
    }

    this.loadSensorData(sensor.id, (data) => {
      let sensorDataObject = _this.getSensorDataObject(dataId, sensor.serviceType);
      let dataObject = _.isArray(data) ? data : [data];
      let labels = _.pluck(dataObject, _this.getSensorDataProperty('communication', sensor.serviceType)).reverse();
      let values = _.pluck(dataObject, _this.getSensorDataProperty(dataId, sensor.serviceType)).reverse();

      let roundingDigits = (isNaN(sensorDataObject.roundingDigits) ? config.sensorData.defaultRoundingDigits : sensorDataObject.roundingDigits);
      let sum = 0;
      let valuesRounded = [];
      let max = Number.MIN_VALUE;
      let min = Number.MAX_VALUE;
      for (let i = 0; i < values.length; ++i) {
        let value = values[i];
        if (isNaN(value)) { // this could be anything, make sure it is really not-a-number
          value = Number.NaN;
          values[i] = value;
        }
        if (value < min) {
          min = value;
        }
        if (value > max) {
          max = value;
        }
        sum += value;
        valuesRounded.push(Number(value.toFixed(roundingDigits))); // round all numbers to remove extra decimals
      }

      let avgEl = document.getElementById("sensorDataAverage");
      let mdEl = document.getElementById("sensorDataMedian");
      let sdEl = document.getElementById("sensorDataStandardDeviation");
      let minEl = document.getElementById("sensorDataMin");
      let maxEl = document.getElementById("sensorDataMax");
      document.getElementById("sensorDataCount").innerHTML = values.length;
      if (values.length < 1) {
        avgEl.innerHTML = getText("notANumber");
        mdEl.innerHTML = avgEl.innerHTML;
        sdEl.innerHTML = avgEl.innerHTML;
        minEl.innerHTML = avgEl.innerHTML;
        maxEl.innerHTML = avgEl.innerHTML;
      } else {
        let mean = sum / values.length;
        avgEl.innerHTML = mean.toFixed(roundingDigits);
        sdEl.innerHTML = (Math.sqrt(values.map(x => Math.pow(x - mean, 2)).reduce((a, b) => a + b) / values.length)).toFixed(roundingDigits);

        minEl.innerHTML = min.toFixed(roundingDigits);
        maxEl.innerHTML = max.toFixed(roundingDigits);

        values.sort(function(a, b) { return a - b });
        if (values.length % 2 == 0) {
          mdEl.innerHTML = ((values[values.length / 2] + values[values.length / 2 - 1]) / 2).toFixed(roundingDigits);
        } else {
          mdEl.innerHTML = (values[Math.floor(values.length / 2)]).toFixed(roundingDigits);
        }
      }

      $("#sensorDataStatistics").removeClass("hidden");

      labels.forEach((value, index) => {
        labels[index] = this.getReadableTime(value);
      });

      let chartData = {
        labels: labels,
        datasets: [{
          label: getText(dataId) + ' (' + (sensorDataObject.unit || 0) + ')',
          data: valuesRounded
        }]
      };
      let options = {
        maintainAspectRatio: false,
        plugins: {
          annotation: {
            annotations: []
          },
          zoom: {
            zoom: {
              drag: {
                enabled: true,
              },
              mode: 'xy',
            }
          }
        }
      };

      if (sensorDataObject.scaleDefaults) {
        options.scales = {
          y: {
            suggestedMin: sensorDataObject.scaleDefaults.min,
            suggestedMax: sensorDataObject.scaleDefaults.max
          }
        };
      }

      if ((config.sensorDataRange.from && config.sensorDataRange.to) || config.dataDrop.enabled) { // no need to calculate times if datadrop is disabled and from/to parameters are not set
        let timesms = [];
        timesms.push(getUnixTimeWithoutSeconds(labels[0]));
        let intervalss = []; // intervals, in seconds
        let intervalAverage = 0;
        for (let i = 1; i < labels.length; ++i) {
          let t = getUnixTimeWithoutSeconds(labels[i]);
          timesms.push(t);
          let interval = (t - timesms[i - 1]) / 1000;
          intervalss.push(interval);
          intervalAverage += interval;
        }
        if (config.dataDrop.enabled) {
          intervalAverage = intervalAverage / (labels.length - 1);
          for (let i = 0; i < intervalss.length; ++i) {
            let interval = intervalss[i];
            if (interval > config.dataDrop.threshold && interval / config.dataDrop.thresholdMultiplier > intervalAverage) {
              options.plugins.annotation.annotations.push({
                "type": "line",
                "xMin": labels[i],
                "xMax": labels[i],
                "borderWidth": 1,
                "borderColor": config.dataDrop.color,
                label: {
                  content: ["drop", "" + (interval < 60 ? (interval + " s") : (Math.round(interval / 60) + " min"))],
                  enabled: true,
                  backgroundColor: config.dataDrop.color
                }
              });
            }
          }
        }

        if (config.sensorDataRange.from && config.sensorDataRange.to) { // show from & to selector lines
          let startIndex = null;
          let from = getUnixTimeWithoutSeconds(config.sensorDataRange.from);
          if (from > timesms[0]) { // if from is inside the chart
            for (let i = 1; i < timesms.length - 1; ++i) {
              let t = timesms[i];
              if (t == from) {
                startIndex = i;
                break;
              } else if (from < t) {
                startIndex = i;
                timesms.splice(i, 0, from);
                labels.splice(i, 0, this.getReadableTime(config.sensorDataRange.from));
                valuesRounded.splice(i, 0, null);
                break;
              }
            }
          }

          let endIndex = null;
          let to = getUnixTimeWithoutSeconds(config.sensorDataRange.to);
          if (to < timesms[timesms.length - 1]) { // if to is inside the chart
            for (let i = timesms.length - 2; i > 0; --i) {
              let t = timesms[i];
              if (t == to) {
                endIndex = i;
                break;
              } else if (to > t) {
                endIndex = i + 1;
                timesms.splice(i, 0, to);
                labels.splice(i + 1, 0, this.getReadableTime(config.sensorDataRange.to));
                valuesRounded.splice(i + 1, 0, null);
                break;
              }
            }
          }

          if (startIndex != null) { // draw line if it was not outside the chart
            options.plugins.annotation.annotations.push({
              "type": "line",
              "xMin": labels[startIndex],
              "xMax": labels[startIndex],
              "borderWidth": 2,
              "borderColor": config.sensorDataRange.markerColor,
              label: {
                content: "from",
                enabled: true,
                fontColor: config.sensorDataRange.markerColor
              }
            });
          }

          if (endIndex != null) { // draw line if it was not outside the chart
            options.plugins.annotation.annotations.push({
              "type": "line",
              "xMin": labels[endIndex],
              "xMax": labels[endIndex],
              "borderWidth": 2,
              "borderColor": config.sensorDataRange.markerColor,
              label: {
                content: "to",
                enabled: true,
                fontColor: config.sensorDataRange.markerColor
              }
            });
          }
        }

      }

      this._popupChart = new Chart('sensorPropertyChartCanvas', { type: 'line', data: chartData, options: options });
    }, days, null, from, to);
  },

  /**
   *
   */
  viewThermalComfortChartPopup: function(sensor, days, from, to) {
    if (this._popupChart) {
      document.getElementById("sensorPropertyChart").innerHTML = '<canvas id="sensorPropertyChartCanvas"></canvas>'; // something happens to the canvas when destroy() is called causing issues with plugins, so trash the old canvas and make a new one
      this._popupChart.destroy();
    }

    $('.popupButton.active').removeClass('active');
    if (days) {
      $('#daysButton_' + days).addClass('active');
    } else if (from && to) {
      $('#daysButtonCustomInterval').addClass('active');
    }

    this.loadSensorData(sensor.id, (data) => {
      let chartData = [];
      let totalCount = 0;
      if (_.isArray(data)) {
        for (let d of data) {
          if (!isNaN(d.temperature) && !isNaN(d.humidity)) {
            chartData.push({ "x": d.temperature, "y": d.humidity });
            ++totalCount;
          }
        }
      }
      const dSets = {
        datasets: [{
          label: 'Thermal Comfort °C/RH%',
          data: chartData,
          pointRadius: 4,
        }],
      };

      let options = {
        maintainAspectRatio: false,
        scales: {
          y: {
            suggestedMin: 0,
            suggestedMax: 100
          },
          x: {
            suggestedMin: 0,
            suggestedMax: 40
          }
        },
        plugins: {
          autocolors: false,
          annotation: {
            annotations: []
          },
          zoom: {
            zoom: {
              drag: {
                enabled: true,
              },
              mode: 'xy',
            }
          }
        }
      };

      let outText = "";
      for (limit of config.thermalComfort.limits) {
        if (totalCount == 0) {
          outText += '<span style="color:' + limit.color + '">0 </span>';
        } else {
          let outCount = 0;
          for (let d of chartData) {
            if (!xyutils.inside([d.x, d.y], limit.polygon)) {
              ++outCount;
            }
          }
          if (outCount == 0) {
            outText += '<span style="color:' + limit.color + '">0 (0 %) </span>';
          } else {
            outText += '<span style="color:' + limit.color + '">' + outCount + ' (' + (Math.round(outCount / totalCount * 100)) + ' %) </span>';
          }
        }

        for (let i = 0; i < limit.polygon.length - 1;) {
          let c1 = limit.polygon[i];
          let c2 = limit.polygon[++i];
          let line = {
            type: 'line',
            xMin: c1[0],
            yMin: c1[1],
            xMax: c2[0],
            yMax: c2[1],
            borderWidth: config.thermalComfort.borderWidth,
            borderColor: limit.color
          }
          options.plugins.annotation.annotations.push(line);
        }
        let c1 = limit.polygon[limit.polygon.length - 1];
        let c2 = limit.polygon[0];
        let line = {
          type: 'line',
          xMin: c1[0],
          yMin: c1[1],
          xMax: c2[0],
          yMax: c2[1],
          borderWidth: config.thermalComfort.borderWidth,
          borderColor: limit.color
        }
        options.plugins.annotation.annotations.push(line);
      }

      document.getElementById("sensorDataTCTotal").innerHTML = totalCount;
      document.getElementById("sensorDataTCOut").innerHTML = outText;
      $("#sensorDataTCStatistics").removeClass("hidden");

      this._popupChart = new Chart('sensorPropertyChartCanvas', { type: 'scatter', data: dSets, options: options });
    }, days, null, from, to);
  },

  /**
   *
   */
  createSensor: function(sensor) {
    var _this = this;
    var sensorFilters = config.sensorFilters[sensor.serviceType] || config.sensorFilters.default;
    var html = '<div id="sensor_' + sensor.id + '" class="sensor basicButton';
    if (config.defaultSensorId != null && config.defaultSensorId.includes(sensor.id)) {
      html += ' selectedSensor';
    }
    html += `">`;
    if (config.alwaysShowId && sensor.id) {
      html += '<span class="floorplan-sensor-id">' + sensor.id + '</span>';
    }

    sensorFilters.forEach((property) => {
      property === "position" ?
        html += `<div class="sensor-${property} sensor-item ${sensor.serviceType} ${sensor.name} legend-color-${sensor.serviceType}"></div>` :
        html += `<div class="sensor-${property} sensor-item"></div>`;
    });
    html += '</div>';

    $('#floorplan').append(html);

    this.loadSensorData(sensor.id);

    $('#sensor_' + sensor.id).draggable({
      containment: '#content',
      scroll: false,
      zindex: 9999,
      beforeStart: (evt, ui) => {
        var sensorElem = $(evt.target);
        var sensorContainer = sensorElem.parent();

        if (sensorContainer.hasClass('unpositionedSensorContainer')) {
          var containerOffset = $('#content').offset();
          var offset = sensorElem.offset();

          sensorElem.appendTo($('#content'));
          sensorElem.css({ 'left': offset.left - containerOffset.left, 'top': offset.top - containerOffset.top });
          sensorElem.removeClass('unpositionedSensor');

          sensorContainer.remove();
        } else {
          sensorElem.appendTo($('#content'));
        }
      },
      start: (evt, ui) => {
        evt.target.classList.add('sensor-dragged');

        _this._isDragging = true;

        $('#sensorDataTooltip').remove();
      },
      stop: (evt, ui) => {
        if (_this.sensorStoppedOnUnpositioned) { // last drag ended on unpositioned drop
          _this.sensorStoppedOnUnpositioned = false;
          return;
        }
        var sensorElem = $(evt.target);
        var sensorId = Number(evt.target.id.split('_')[1]);
        var floorplan = $('#floorplan');
        if (ui.position.left <= floorplan.width() && ui.position.top <= floorplan.height()) {
          _this.editSensorPosition(sensorId, ui.position.left + sensorElem.width() / 2, ui.position.top + sensorElem.height() / 2, null);
        } else {
          _this.editSensorPosition(sensorId, null, null, null);
        }
        evt.target.classList.remove('sensor-dragged');
      }
    });
  },

  /**
   *
   */
  createSensorDataTooltip: function(sensorId) {
    var sensor = this._data.sensors[sensorId];
    var data = this._data.sensorData[sensorId];
    var tooltipObject = config.sensorTooltip[sensor.serviceType] || config.sensorTooltip.default;
    var html = '<div id="sensorDataTooltip">';
    html += '<div>' + (sensor.name || sensor.externalId) + '</div><table>';

    if (!data) return '';

    tooltipObject.forEach((property) => {
      var sensorDataObject = this.getSensorDataObject(property, sensor.serviceType);
      var activeAlert = _this.getActiveAlert(sensorId, sensorDataObject);
      var dataObject = (_.isArray(data) && data.length > 0) ? data[0] : data || {};
      var value = _.has(dataObject, sensorDataObject.dataId) ? dataObject[sensorDataObject.dataId] : ' - ';
      var unit = sensorDataObject.unit ? ' ' + sensorDataObject.unit : ''; // will add space between unit and value

      if (property == 'communication' && !_.isUndefined(value)) value = this.getReadableTime(value);
      if (_.isUndefined(value) || _.isNull(value)) {
        value = '-';
        unit = '';
      }
      html += '<td>' + (activeAlert ? '<span class="alert-' + activeAlert + '"></span>' : '') + '</td>';
      let roundingDigits = (isNaN(sensorDataObject.roundingDigits) ? config.sensorData.defaultRoundingDigits : sensorDataObject.roundingDigits);
      html += '<td>' + getText(property) + ': </td><td>' + (isNaN(value) ? value : value.toFixed(roundingDigits)) + unit + '</td><tr>';
    });
    html += '</table></div>';

    return html;
  },

  /**
   *
   */
  editSensorPosition: function(sensorId, posX, posY, posZ) {
    var sensor = this._data.sensors[sensorId];
    var params = cloneObject(sensor);
    params.x = posX;
    params.y = posY;
    params.z = posZ;

    if (!_.isNull(params.x)) {
      params.x = (params.x - this._zoomPosX) / this._zoomLevel;
    }
    if (!_.isNull(params.y)) {
      params.y = (params.y - this._zoomPosY) / this._zoomLevel;
    }

    sendData('PUT', 'sensors/' + sensorId, params, { context: this }, function() {
      this._data.sensors[sensorId] = params;
      let sensorElem = $('#sensor_' + sensorId);
      if (sensorElem.hasClass('unpositionedSensor')) {
        this.unpositionSensor(sensorElem);
      } else {
        this.positionSensor(params);
      }
    });
  },

  /**
   *
   */
  positionSensor: function(sensor) {
    var sensorElem = $('#sensor_' + sensor.id);
    var sensorItems = sensorElem.find('.sensor-item');
    var area = $('#content');
    var maxWidth = 0;
    var maxHeight = 0;

    $.each(sensorItems, (index, item) => {
      if (!item.classList.contains('hidden')) {
        maxWidth = Math.max(maxWidth, item.clientWidth);
        maxHeight = Math.max(maxHeight, item.clientHeight);
      }
    });
    sensorElem.css({ width: maxWidth + 'px', height: maxHeight + 'px' });
    if ($('#floorplan img').length > 0 && (_.has(sensor, 'x') && _.has(sensor, 'y') && !_.isNull(sensor.x) && !_.isNull(sensor.y))) {

      var posX = sensor.x * this._zoomLevel + this._zoomPosX - sensorElem.width() / 2;
      var posY = sensor.y * this._zoomLevel + this._zoomPosY - sensorElem.height() / 2;

      sensorElem.css({ left: posX, top: posY });
      sensorElem.removeClass('unpositionedSensor');
    } else {
      this.unpositionSensor(sensorElem);
    }
  },

  /**
   *
   */
  unpositionSensor: function(sensorElem) {
    sensorElem.detach();

    var sensorId = Number(sensorElem.attr('id').split('_')[1]);
    var sensor = this._data.sensors[sensorId];
    var container = $('#unpositionedSensors');

    $('#unpositionedSensor_' + sensorId).remove();

    var sensorContainer = $('<div id="unpositionedSensor_' + sensorId + '" class="unpositionedSensorContainer"></div>');

    sensorElem.css({ 'left': '', 'top': '' });
    sensorElem.addClass('unpositionedSensor');

    container.append(sensorContainer);
    sensorContainer.append(sensorElem);
  },

  /**
   *
   */
  filterSensors: function(filterList, layerId) {
    filterList = _.isArray(filterList) ? filterList : [filterList];
    layerId = layerId || this._currentLayerId;

    $('.sensor').removeClass('unpositionedSensor');
    $('.sensor-item').addClass('hidden');
    $('#unpositionedSensors .unpositionedSensorContainer .sensor').appendTo('#content');
    $('#unpositionedSensors').html('');

    filterList.forEach(filterId => {
      $('.sensor-' + filterId).removeClass('hidden');
    });
    this.scaleFloorplanImage();

    if (filterList.length === 1) {
      this._filtersDropdown.label = getText('view') + ' (' + getText(filterList[0]) + ')';
    }
  },

  /**
   *
   */
  loadSiteData: function(siteId, onComplete) {
    var _this = this;
    var loadedLayers = {};
    var loadedSensors = {};

    this._data.siteLayers[siteId] = [];

    sendData('GET', 'sites/' + siteId + '/layers', null, { context: this }, function(siteLayers) {
      if (siteLayers.length === 0) {
        siteLoaded();
      } else {
        siteLayers.forEach(layer => {
          loadedLayers[layer.id] = false;

          this._data.layers[layer.id] = layer;
          this._data.layers[layer.id].parentSite = siteId;
          this._data.layers[layer.id].parentLayer = -1;
          this._data.siteLayers[siteId].push(layer.id);

          loadSublayers(layer.id);
        });
      }
    });

    function loadSublayers(parentLayerId) {
      _this._data.layerLayers[parentLayerId] = [];

      loadSensors(parentLayerId);

      sendData('GET', 'layers/' + parentLayerId + '/layers', null, { context: _this }, function(layerData) {
        loadedLayers[parentLayerId] = true;

        if (layerData.length === 0) {
          checkLoadCompleted();
        } else {
          layerData.forEach(layer => {
            loadedLayers[layer.id] = false;

            this._data.layers[layer.id] = layer;
            this._data.layers[layer.id].parentSite = siteId;
            this._data.layers[layer.id].parentLayer = parentLayerId;
            this._data.layerLayers[parentLayerId].push(layer.id);

            loadSublayers(layer.id);
          });
        }
      });
    }

    function loadSensors(parentLayerId) {
      _this._data.layerSensors[parentLayerId] = [];

      loadedSensors[parentLayerId] = false;

      sendData('GET', 'layers/' + parentLayerId + '/sensors', null, { context: _this }, function(sensorData) {
        loadedSensors[parentLayerId] = true;

        sensorData.forEach(sensor => {
          _this._data.sensors[sensor.id] = sensor;
          _this._data.sensors[sensor.id].parentLayer = parentLayerId;
          _this._data.sensorData[sensor.id] = {};
          _this._data.layerSensors[parentLayerId].push(sensor.id);
        });
        checkLoadCompleted();
      });
    }

    function checkLoadCompleted() {
      var layersLoaded = _.every(loadedLayers, layerStatus => { return layerStatus; });
      var sensorsLoaded = _.every(loadedSensors, sensorsStatus => { return sensorsStatus; });

      if (layersLoaded && sensorsLoaded) {
        siteLoaded();
      }
    }

    function siteLoaded() {
      if (onComplete) onComplete();
    }
  },

  /**
   *
   */
  getSensorDataObject: function(id, serviceType) {
    serviceType = serviceType || 'default';

    let sensorDataObject = cloneObject(config.sensorData[id]);
    let dataId = sensorDataObject.dataId;

    if (_.isObject(dataId)) {
      dataId = dataId[serviceType] || dataId.default;
    }
    if (_.has(sensorDataObject, 'dataId')) sensorDataObject.dataId = dataId;

    return sensorDataObject;
  },

  /**
   *
   */
  getSensorDataProperty: function(id, serviceType) {
    serviceType = serviceType || 'default';

    let dataId = config.sensorData[id].dataId;

    if (_.isObject(dataId)) {
      dataId = dataId[serviceType] || dataId.default;
    }
    return dataId;
  },

  /**
   *
   */
  getActiveAlert: function(sensorId, sensorDataObject) {
    var sensorData = this._data.sensorData[sensorId][0] || {};
    var alerts = sensorDataObject.alerts ? [sensorDataObject] : [];
    var activeAlerts = [];

    if (sensorDataObject.alertGroup) { //Get the status from multiple alerts
      let sensor = this._data.sensors[sensorId];

      sensorDataObject.alertGroup.forEach((alert) => {
        alerts.push(this.getSensorDataObject(alert, sensor.serviceType));
      });
    }
    alerts.forEach((alertObject) => {
      var dataValue = sensorData[alertObject.dataId];
      var value = Number(_.isArray(dataValue) ? dataValue[0] : dataValue);

      for (var i in alertObject.alerts) {
        var alertTresholds = alertObject.alerts[i];

        if ((!_.has(alertTresholds, 'min') || alertTresholds.min <= value) && (!_.has(alertTresholds, 'max') || alertTresholds.max >= value)) {
          activeAlerts.push(alertTresholds.alert);
          break;
        }
      }
    });
    var activeAlert = '';

    if (activeAlerts.length > 0) {
      if (sensorDataObject.alertStates) { //Display the first alert in the list that is triggered by any of the included alerts
        for (let i in sensorDataObject.alertStates) {
          if (_.includes(activeAlerts, sensorDataObject.alertStates[i])) {
            activeAlert = sensorDataObject.alertStates[i];
            break;
          }
        }
      } else {
        activeAlert = activeAlerts[0];
      }
    }
    return activeAlert;
  },

  /**
   *
   */
  loadSensorData: function(sensorId, onComplete, fromDays, toDays, fromDateTime, toDateTime) {
    if (config.loadSensorData) {
      var _this = this;
      var sensor = this._data.sensors[sensorId];
      var urlVariables = {};

      if (fromDays) {
        let fromDate = new Date();
        urlVariables.to = fromDate.toISOString();
        fromDate.setDate(fromDate.getDate() - fromDays);
        urlVariables.from = fromDate.toISOString();
      } else if (fromDateTime) {
        urlVariables.from = fromDateTime.toISOString();
      }
      if (toDays) {
        let toDate = new Date();
        toDate.setDate(toDate.getDate() - toDays);
        urlVariables.to = toDate.toISOString();
      } else if (toDateTime) {
        urlVariables.to = toDateTime.toISOString();
      }
      sendData('GET', 'data/sensors/' + sensorId, null, { context: this, configUrl: config.serverUrlData, urlVariables: urlVariables }, function(sensorData) {
          dataLoaded(sensorData);
        },
        function(error) {
          console.log('loadSensorData Error:', error);
          dataLoaded({});
        });

      function dataLoaded(sensorData) {
        _this._data.sensorData[sensorId] = sensorData;

        var sensorFilters = config.sensorFilters[sensor.serviceType] || config.sensorFilters.default;

        sensorFilters.forEach((sensorFilter) => {
          var sensorDataObject = _this.getSensorDataObject(sensorFilter, sensor.serviceType);
          var sensorItem = $('#sensor_' + sensorId + ' .sensor-' + sensorFilter);
          let sensorText = null;
          if (sensorDataObject.dataId && sensorData.length > 0) {
            sensorText = sensorData[0][sensorDataObject.dataId];
          } else {
            sensorText = sensor[sensorFilter];
          }
          let roundingDigits = (isNaN(sensorDataObject.roundingDigits) ? config.sensorData.defaultRoundingDigits : sensorDataObject.roundingDigits);
          sensorItem.text((isNaN(sensorText) ? sensorText : Number(sensorText).toFixed(roundingDigits)));
          var activeAlert = sensorDataObject ? _this.getActiveAlert(sensorId, sensorDataObject) : '';

          sensorItem.removeClass(function(index, className) { //Remove classes starting with "alert-"
            return (className.match(/(^|\s)alert-\S+/g) || []).join(' ');
          });
          if (activeAlert) {
            sensorItem.addClass('alert-' + activeAlert);
          }
        });
        if (onComplete) onComplete(sensorData);
      }
    } else if (onComplete) onComplete({});
  },

  /**
   *
   */
  scaleFloorplanImage: function(scale, posX, posY) {
    var floorplanImage = document.getElementById('floorplanImage');

    if (floorplanImage) {
      if (arguments.length < 1 || scale < 0 || _.isUndefined(scale)) {
        var floorplanSize = floorplanImage.style['background-size'];

        if (floorplanSize) {
          let floorplanSizeParts = floorplanSize.split(' ');
          let width = Number(floorplanSizeParts[0].split('px')[0]);
          scale = width / this._origFloorplanImageWidth;
        }
      }
      if (arguments.length < 2) {
        var floorplanPosition = floorplanImage.style['background-position'];

        if (floorplanPosition) {
          var floorplanPositionParts = floorplanPosition.split(' ');
          posX = Number(floorplanPositionParts[0].split('px')[0]);
          posY = Number(floorplanPositionParts[1].split('px')[0]);
        }
      }
      this._zoomLevel = scale;
      this._zoomPosX = posX;
      this._zoomPosY = posY;

      $('#zoomLevel').html(getText('zoomLevel') + ': ' + Math.round(scale * 100) + '%');
    } else {
      $('#zoomLevel').html('');
    }
    $('.sensor').forEach((sensorElem) => {
      if (sensorElem.id) {
        var sensorId = Number(sensorElem.id.split('_')[1]);
        var sensorParams = cloneObject(this._data.sensors[sensorId]);

        this.positionSensor(sensorParams);
      }
    });
  },

  /**
   *
   */
  loadFloorplanImage: function(layerId, onComplete) {
    _this = this;

    $('#floorplanImage').remove();

    var imageUrl = this.getRecursiveLayerValue(layerId, 'image', 'url');

    if (imageUrl) {
      var img = new Image();
      img.id = 'floorplanImage';
      img.src = imageUrl;

      img.addEventListener('load', function(evt) {
        var container = $('#content');
        var scaledWidth = Math.min(this.width, container.width());
        var scaledHeight = Math.min(this.height, container.height());
        var scale = 1; //initial before calcs
        if (scaledWidth <= scaledHeight) {
          scale = scaledWidth / this.width;
          if (scale < 1) {
            $(this).css({ 'width': '100%' }); // forces width to 100% (will be reset later)
          }
        } else {
          scale = scaledHeight / this.height;
          if (scale < 1) {
            $(this).css({ 'height': '100%' }); // forces height to 100% (will be reset later)
          }
        }
        _this._origFloorplanImageWidth = this.width;
        _this._origFloorplanImageHeight = this.height;
        $('#floorplan').prepend(this);

        _this.filterSensors(_this._filtersDropdown.value, layerId);
        _this.scaleFloorplanImage(scale, 0, 0);

        _this._data.layerSensors[layerId].forEach((sensorId) => { //Unposition sensors that are placed outside the floorplan image
          var sensor = _this._data.sensors[sensorId];
          var sensorElem = $('#sensor_' + sensor.id);

          if (!sensorElem.hasClass('unpositionedSensor') && (sensor.x < -sensorElem.width() || sensor.y < -sensorElem.height() ||
              sensor.x > _this._origFloorplanImageWidth + sensorElem.width() || sensor.y > _this._origFloorplanImageHeight + sensorElem.height())) {
            _this.unpositionSensor(sensorElem);
          }
        });
        var floorplanZoom = wheelzoom($('#floorplan img'), { maxZoom: 10 });

        if (scale < 1) {
          setTimeout(() => {
            $('#floorplanImage').css({ 'width': 'unset', 'height': 'unset' });
          }, 100); // allows the image to grow beyond initial size
        }

        this.addEventListener('wheel', function(evt) {
          _this.scaleFloorplanImage();
        });
        this.addEventListener('mouseup', function(evt) {
          _this._mousedown = false;
        });
        this.addEventListener('mousedown', function(evt) {
          _this._mousedown = true;
        });
        this.addEventListener('mousemove', function(evt) {
          if (_this._mousedown) {
            _this._isDragging = true;
            _this.scaleFloorplanImage();
          }
        });
        if (onComplete) onComplete();
      }, { once: true });
      img.addEventListener('error', function(evt) {
        console.log('Image load error', evt);
      });
    } else {
      if (this._data.layerSensors[layerId].length > 0) { // TODO the "unpositioned sensors" box does not work and throws javascript errors when the layer has sensors, but not an image
        alert("Warning: this layer has sensors, but not an image. Sensor modification and placement will not work.");
      }
      if (onComplete) onComplete();
    }
  },

  /**
   *
   */
  getReadableTime: function(time) {
    if (!time || time == ' - ') return ' - ';

    let date = new Date(time);
    let month = date.getMonth() + 1;
    let day = date.getDate();
    let hours = date.getHours();
    let minutes = date.getMinutes();
    let seconds = date.getSeconds();
    return date.getFullYear() + (month < 10 ? "-0" : "-") + month + (day < 10 ? "-0" : "-") + day + (hours < 10 ? " 0" : " ") + hours + (minutes < 10 ? ":0" : ":") + minutes + (seconds < 10 ? ":0" : ":") + seconds;
  }
};
