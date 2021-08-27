"use strict";
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

/**
 * Parses csv files to format understandable by xygraphs.js
 *
 * The csv separator is taken from input field with id "input-csv-separator".
 */
var xycsvFiles = {
	SEPARATOR_AXIS_FIELD : ",",
	SEPARATOR_AXIS_PAIR : ";",
	inputCSVseparator : null,

	/**
	 * initialize the UI
	 */
	initialize : function(){
		var filesToUpload = document.getElementById("csv-file-upload");
		filesToUpload.ondragover = function () { $(this).addClass("hover"); return false; };
		filesToUpload.ondragleave = function () { $(this).removeClass("hover"); return false; };
		filesToUpload.ondragend = function () { $(this).removeClass("hover"); return false; };
		filesToUpload.ondrop = function (e) {
			$(this).removeClass("hover");
			e.preventDefault();
			xycsvFiles.readFiles(e.dataTransfer.files);
		};
		xycsvFiles.inputCSVseparator = document.getElementById("input-csv-separator");
	},

	/**
	 *
	 * @param {FileList} fileList
	 */
	readFiles : function(fileList){
		for(let i=0;i<fileList.length;++i){
			xycsvFiles.readFile(fileList[i]);
		}
	},

	/**
	 *
	 * @param {File} file
	 */
	readFile : function(file) {
		if(file.type != "text/csv" && file.type != "application/csv" && file.name.split(".").pop() != "csv"){
			alert("readFiles: not a csv file.");
			return;
		}

		var separator = xycsvFiles.inputCSVseparator.value;
		if(!separator){
			alert("readFiles: Empty separator.");
			return;
		}

		var reader = new FileReader();
  	reader.onload = function(){
    	xycsvFiles.readCSV(this.result, file.name, separator);
  	};
  	reader.readAsText(file);
	},

	/**
	 *
	 * @param {String} csv
	 * @param {String} label
	 * @param {String} separator
	 */
	readCSV : function(csv, label, separator) {
		var lines = csv.split('\n');
		if(lines.length < 2){ // there must be at least column names and 1 point of data to draw a graph
			alert("Not enough data: lines < 2.");
			return;
		}

		var fieldPairs = document.getElementById("input-axis-fields").value.split(xycsvFiles.SEPARATOR_AXIS_PAIR);

		var labels = lines[0].split(separator);
		var da = [];
		for(let pair of fieldPairs){
			let fields = pair.split(xycsvFiles.SEPARATOR_AXIS_FIELD);
			if(fields.length != 2){
				alert("Invalid xy field pair: "+pair);
				return;
			}
			let data = {
				label : label,
				xLabel : fields[0].trim(),
				yLabel : fields[1].trim(),
				xIndex : null,
				yIndex : null,
				points : [],
				metaPoints : []
			};

			for(let i=0;i<labels.length;++i){ // find label indexes
				let label = labels[i].trim();
				if(data.xLabel.localeCompare(label) == 0){
					data.xIndex = i;
					if(data.xIndex != null && data.yIndex != null){
						break;
					}
				}else if(data.yLabel.localeCompare(label) == 0){
					data.yIndex = i;
					if(data.xIndex != null && data.yIndex != null){
						break;
					}
				}
			}

			if(data.yIndex == null || data.xIndex == null){
				console.log("No x ("+data.xLabel+") or y ("+data.yLabel+")");
			}else{
				da.push(data);
			}
		}

		for(let i=1;i<lines.length;++i){
			let line = lines[i];
			if(!line){
				console.log("readFiles: Ignored empty line, number: "+i);
				continue;
			}
			let values = line.split(separator);
			if(values.length != labels.length){
				alert("Y value count "+(values.length)+" on line number "+i+": "+line+" differs from data label count "+labels.length);
				return;
			}

			for(let data of da){
				let y = null;
				let x = null;
				let meta = {};
				for(let j=0;j<values.length;++j){
					if(j == data.xIndex){
						x = values[j];
					}else if(j == data.yIndex){
						y = values[j];
					}else{
						meta[labels[j]] = values[j];
					}
				} // for values

				if(x == null || y == null){
					console.log("Missing y or x. Ignoring line: "+line);
					continue;
				}

				x = x.replace(",", ".");
				y = y.replace(",", ".");
				if(isNaN(x) || isNaN(y)){
					console.log("Invalid y or x. Ignoring line: "+line);
					continue;
				}

				let c = [Number(x),Number(y)];
				data.points.push(c);
				data.metaPoints.push(meta);
			}
		} // for lines
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
