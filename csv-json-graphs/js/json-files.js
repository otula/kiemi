"use strict";
/**
 * Copyright 2019 Tampere University
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
/**
 * Parses sensor data json files to format understandable by graphs.js
 *
 * The data should be an array with object containing at least the properties listed in the example below:
 *
 * [
 *  {
 *   "type": 1,
 *   "timestamp": "2019-06-03T15:01:51.000+0000",
 *   "value": 22.9
 *  }
 * ...
 * ]
 */
var jsonFiles = {
	jsonTimestampField : null,

	/**
	 * initialize the UI
	 */
	initialize : function(){
		var filesToUpload = document.getElementById("json-file-upload");
		filesToUpload.ondragover = function () { $(this).addClass("hover"); return false; };
		filesToUpload.ondragleave = function () { $(this).removeClass("hover"); return false; };
		filesToUpload.ondragend = function () { $(this).removeClass("hover"); return false; };
		filesToUpload.ondrop = function (e) {
			$(this).removeClass("hover");
			e.preventDefault();
			jsonFiles.readFiles(e.dataTransfer.files);
		};
		jsonFiles.jsonTimestampField = document.getElementById("input-timestamp-field");
	},

	/**
	 *
	 * @param {FileList} fileList
	 */
	readFiles : function(fileList){
		for(let i=0;i<fileList.length;++i){
			jsonFiles.readFile(fileList[i]);
		}
	},

	/**
	 *
	 * @param {File} file
	 */
	readFile : function(file){
		if(file.type != "application/json"){
			alert("readFiles: not a json file.");
			return;
		}

		var reader = new FileReader();
  	reader.onload = function(){
			try {
				var root = JSON.parse(this.result);
				jsonFiles.readJSON(root, file.name);
			} catch (e) {
				console.log(e);
				alert("Failed to parse JSON.");
			}
  	};
  	reader.readAsText(file);
	},

	/**
	 *
	 * @param {Object} root
	 * @param {String} label
	 */
	readJSON : function(root, label) {
		if(root.length < 2){ // there must be at 2 points of data to draw a graph
			alert("Not enough data: objects < 2.");
			return;
		}

		var timestampField = jsonFiles.jsonTimestampField.value;
		var data = [];
		for(let i=0;i<root.length;++i){
			let obj = root[i];
			let x = timeutils.parseDate(obj[timestampField]);
			if(x == null){
				alert("Invalid timestamp: "+obj[timestampField]);
				return;
			}
			for (let property in obj) { // go through all properties, and generate separate data sets
				if (obj.hasOwnProperty(property) && property != timestampField) { // skip timestamp
					let xy = null;
					for(let j=0;j<data.length;++j){
						let temp = data[j];
						if(temp.yLabel == property){
							xy = temp;
							break;
						}
					} // for find existing
					if(xy == null){ // no existing
						xy = new Object();
						xy.label = label;
						xy.xLabel = timestampField;
						xy.yLabel = property;
						xy.points = [];
						data.push(xy);
					}

					let value = obj[property];
					let y = Number(value);
					if(isNaN(y)){
						console.log("Ignored invalid value: "+value);
					}else{
						let point = [x, y];
						xy.points.push(point);
					}
				} // if own property
			}
		} // for root
		graphs.generateGraphs(data);
	}
};
