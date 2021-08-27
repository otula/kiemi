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
 * Parses sensor data json files to format understandable by xygraphs.js
 *
 * ...
 * ]
 */
var xyjsonFiles = {
	SEPARATOR_AXIS_FIELD : ",",
	SEPARATOR_AXIS_PAIR : ";",

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
			xyjsonFiles.readFiles(e.dataTransfer.files);
		};
	},

	/**
	 *
	 * @param {FileList} fileList
	 */
	readFiles : function(fileList){
		for(let i=0;i<fileList.length;++i){
			xyjsonFiles.readFile(fileList[i]);
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
				xyjsonFiles.readJSON(root, file.name);
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
		var fieldPairs = document.getElementById("input-axis-fields").value.split(xyjsonFiles.SEPARATOR_AXIS_PAIR);

		var da = [];
		for(let pair of fieldPairs){
			let fields = pair.split(xyjsonFiles.SEPARATOR_AXIS_FIELD);
			if(fields.length != 2){
				alert("Invalid xy field pair: "+pair);
				return;
			}
			let data = {
				label : label,
				xLabel : fields[0].trim(),
				yLabel : fields[1].trim(),
				points : [],
				metaPoints : []
			};

			da.push(data);
		}

		for(let i=0;i<root.length;++i){
			let obj = root[i];
			for(let data of da){
				let x = null;
				let y = null;
				let meta = {};
				for (let property in obj) { // go through all properties, separate points and metaPoints
					if (obj.hasOwnProperty(property)) {
						if(property.localeCompare(data.xLabel) == 0){
							x = obj[property];
						}else if(property.localeCompare(data.yLabel) == 0){
							y = obj[property];
						}else{
							meta[property] = obj[property];
						}
					} // if own property
				}
				if(x == null || y == null){
					console.log("Ignored object without valid x ("+data.xLabel+") or y ("+data.yLabel+").");
					continue;
				}

				if(isNaN(x)){
					console.log("Invalid x. Attempting with different decimal separator...");
					x = x.replace(",", ".");
				}

				if(isNaN(y)){
					console.log("Invalid y. Attempting with different decimal separator...");
					y = y.replace(",", ".");
				}

				if(isNaN(x) || isNaN(y)){
					console.log("Invalid y or x. Ignoring...");
					continue;
				}

				let coords = [Number(x),Number(y)];
				data.points.push(coords);
				data.metaPoints.push(meta); // ignore empty and populated to preserve indexes
			} // for data
		} // for root

		for(let i=da.length-1;i>=0;--i){ // remove empty sets if any
			let data = da[i];
			if(data.points.length < 1){
				console.log("Removed empty set, x: "+data.xLabel+", y: "+data.yLabel);
				da.splice(i,1);
			}
		}
		if(da.length < 1){
			console.log("No data for the given axis fields.");
		}else{
			xygraphs.generateGraphs(da);
		}
	}
};
