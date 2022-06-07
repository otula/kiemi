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

//Similar function to Array.forEach for Objects
//For example {a: 1, b: 2}.forEach((value, property) => {console.log(value, property)});
Object.defineProperty(Object.prototype, 'forEach', {
  value: function(callback) {
    if (Object.prototype.toString.call(this) === '[object Object]') {
      for (var property in this) {
        if (Object.prototype.hasOwnProperty.call(this, property)) {
          callback.call(null, this[property], property, this);
        }
      }
    } else {
      for (var i = 0, len = this.length; i < len; i++) {
        callback.call(null, this[i], i, this);
      }
    }
  },
  enumerable: false
});

//Sort array based on property values
function sortArray(array, property, type) {
  if (type == 'alphabetic') {
    array.sort(function(a, b) {
      var textA = a[property].toUpperCase();
      var textB = b[property].toUpperCase();
      return (textA < textB) ? -1 : (textA > textB) ? 1 : 0;
    });
  } else { //Numeric
    array = array.sort((a, b) => (a[property] > b[property]) ? 1 : -1);
  }
}

//Like split(), but removes whitespace
function splitString(string, separator) {
  if (string === '') {
    return [];
  }
  string = string.replace(/\s+/g, ''); //Remove whitespace

  return string.split(arguments.length > 1 ? separator : ',');
}

function cloneObject(object) {
  return JSON.parse(JSON.stringify(object));
}

function getText(textId, replaceObject) {
  var languageText = config.languageText[config.language];
  var text = _.has(languageText, textId) ? languageText[textId] : textId;

  for (let i in replaceObject) {
    text = text.replace('%' + i.toUpperCase() + '%', replaceObject[i]);
  }
  //Remove placeholders %TEXTINCAPS% that were not given a proper value
  var textSplit = text.split('%');
  var replaceParts = [];

  textSplit.forEach(part => {
    if (part != ' ' && part.toUpperCase() === part) {
      replaceParts.push('%' + part + '%');
    }
  });
  replaceParts.forEach(part => {
    text = text.replace(part, '');
  });
  return text;
}

function sendData(sendType, path, variables, sendParams, onComplete, onError) {
  sendParams = sendParams || {};

  var xhttp = new XMLHttpRequest();
  var url = (sendParams.configUrl || window.location.origin) + '/' + path;

  if (sendParams.urlVariables && !_.isEmpty(sendParams.urlVariables)) {
    let variablesArray = [];

    sendParams.urlVariables.forEach((value, variable) => {
      variablesArray.push(encodeURIComponent(variable) + '=' + encodeURIComponent(value));
    });
    url += '?' + variablesArray.join('&');
  }
  xhttp.onreadystatechange = function(evt) {
    if (this.readyState == 4) { //Load is ready
      if (this.status == 200) {
        var response = this.responseText !== '' ? JSON.parse(this.responseText) : null;

        if (onComplete) {
          if (sendParams.context) {
            onComplete.call(sendParams.context, response);
          } else {
            onComplete(response);
          }
        }
      } else if (this.status == 401 || this.status == 403) {
        auth.loginFailed();
      } else if (onError) {
        if (sendParams.context) {
          onError.call(sendParams.context, response);
        } else {
          onError(response);
        }
      } else {
        console.log('sendData returned status: ', this.status, 'with url "', url, '" (', variables, ')');
      }
    }
  };
  xhttp.onerror = function() {
    console.log('Error: ', this);
  };
  xhttp.open(sendType.toUpperCase(), url, true);
  auth.authorize(xhttp);

  if (variables) {
    xhttp.setRequestHeader('Content-type', 'application/json');

    xhttp.send(JSON.stringify(variables));
  } else {
    xhttp.send(null);
  }
}

function jsLoaded() {
  if(!auth.checkStorageCredentials()){
    console.log("No credentials given, aborting load...");
    return;
  }

  var noCache = config.useCache ? '' : '?nocache=' + (new Date()).getTime();
  var loadedData = {};

  loadData('language');
  loadData('GET', 'sites');

  function loadData(dataType, id) {
    loadedData[id || dataType] = null;

    switch (dataType) {
      case 'language':
        $.getJSON(config.languageFile + noCache, function(levelData) {
          config.languageText = levelData;

          dataLoaded('language', true);
        });
        break;
      case 'GET':
        sendData('GET', id, null, null, function(data) {
          dataLoaded(id, data);
        });
        break;
    }
  }

  function dataLoaded(type, data) {
    loadedData[type] = data;

    var everythingLoaded = _.every(loadedData, function(dataItem) {
      return !_.isNull(dataItem);
    });
    if (everythingLoaded) {
      ui.init();
      floorplan.init(loadedData.sites);
    }
  }
}

/**
 * @return {Object} url parameters object: {siteId:ID, layerId:ID, sensorId:ID}, empty values are null
 */
function getURLParameters() {
  var params = {
    siteId: null,
    layerId: null,
    sensorId: null,
		sensorDataFrom : null, // date object with sensor data from time (since)
		sensorDataTo : null, // date object with sensor data to time (until),
    dataDropEnabled : null,
  };
  var urlParams = new URLSearchParams(window.location.search);
  var p = urlParams.getAll("site_id");
  if (p.length > 0) {
    params.siteId = parseInt(p[0]);
  }
  p = urlParams.getAll("layer_id");
  if (p.length > 0) {
    params.layerId = parseInt(p[0]);
  }
  p = urlParams.getAll("sensor_id");
  if (p.length > 0) {
    params.sensorId = [];
		for(let i=0;i<p.length;++i){
			let parts = p[i].split(",");
			for(let j=0;j<parts.length;++j){
				params.sensorId.push(parseInt(parts[j]));
			}
		}
  }
	p = urlParams.getAll("sensor_data_from");
	if(p.length > 0){
		params.sensorDataFrom = new Date(p[0]);
	}
	p = urlParams.getAll("sensor_data_to");
	if(p.length > 0){
		params.sensorDataTo = new Date(p[0]);
	}
  p = urlParams.getAll("detect_datadrop");
	if(p.length > 0){
		params.dataDropEnabled = (p[0] == "true");
	}
  return params;
}

/**
 * @param {Object} date anything that can be parsed in to a date
 * @return {integer} the date in ms with seconds and milliseconds set to 0
 */
function getUnixTimeWithoutSeconds(date){
  var d = new Date(date);
  d.setSeconds(0);
  d.setMilliseconds(0);
  return d.getTime();
}
