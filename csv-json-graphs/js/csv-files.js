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
 * Parses csv files to format understandable by graphs.js
 *
 * The csv file format should be:
 * 1st line: X Axis Label,Y Axis Label,Y Axis Label, ...
 * 2nd Line: value,value,value, ...
 * ...
 * Nth Line: value,value,value, ...
 *
 * The first column is assumed to be the common X axis for all Y axis.
 * There can be any number of Y axis. X axis is assumed to be unix timestamp.
 * Naive checking is performed for timestamp to detect whether it is in seconds or milliseconds.
 *
 * The csv separator is taken from input field with id "input-csv-separator".
 */
var csvFiles = {
	inputCSVseparator : null,
	csvTimestampColumn : null,

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
			csvFiles.readFiles(e.dataTransfer.files);
		};
		csvFiles.inputCSVseparator = document.getElementById("input-csv-separator");
		csvFiles.csvTimestampColumn = document.getElementById("input-timestamp-field");
	},

	/**
	 *
	 * @param {FileList} fileList
	 */
	readFiles : function(fileList){
		for(let i=0;i<fileList.length;++i){
			csvFiles.readFile(fileList[i]);
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

		var separator = csvFiles.inputCSVseparator.value;
		if(!separator){
			alert("readFiles: Empty separator.");
			return;
		}

		var reader = new FileReader();
  	reader.onload = function(){
    	csvFiles.readCSV(this.result, file.name, separator);
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
		if(lines.length < 3){ // there must be at least column names and 2 points of data to draw a graph
			alert("Not enough data: lines < 3.");
			return;
		}

		var filters = document.getElementById("input-filters").value.split(graphs.SEPARATOR_FILTER);
		var filteredColumns = [];

		var values = lines[0].split(separator);
		var xLabel = csvFiles.csvTimestampColumn.value;
		var xLabelIndex = -1;
		var data = [];
		for(let i=0;i<values.length;++i){
			let yLabel = values[i];
			if(yLabel.localeCompare(xLabel) == 0){
				xLabelIndex = i;
				continue;
			}else if(filters.includes(yLabel)){
				filteredColumns.push(i); // mark this column as filtered, but do not remove the data label (that would cause .csv columns to go out-of-sync)
			}
			let xy = new Object();
			xy.label = label;
			xy.xLabel = xLabel;
			xy.yLabel = yLabel;
			xy.points = [];
			data.push(xy);
		}

		if(xLabelIndex < 0){
			alert("Invalid x label: "+xLabel);
			return;
		}

		for(let i=1;i<lines.length;++i){
			let line = lines[i];
			if(!line){
				console.log("readFiles: Ignored empty line, number: "+i);
				continue;
			}
			values = line.split(separator);
			if(values.length-1 != data.length){ // 1st value is x column value
				alert("Y value count "+(values.length-1)+" on line number "+i+": "+line+" differs from data Y count "+data.length);
				return;
			}

			let x = timeutils.parseDate(values[xLabelIndex]);
			if(x == null){
				alert("Invalid value: "+values[0]+" on line number "+i+": "+line);
				return;
			}

			for(let j=0;j<values.length;++j){
				if(j == xLabelIndex){ // skip timestamp field
					continue;
				}
				let y = values[j];
				if(!filteredColumns.includes(j)){ // if this column is filtered, add it as is, otherwise, convert it
					if(y == null){
						alert("Invalid value: "+values[j]+" on line number "+i+": "+line);
						return;
					}
					y = y.replace(",", ".");
					if(isNaN(y)){
						alert("Invalid value: "+values[j]+" on line number "+i+": "+line);
						return;
					}
					y = Number(y);
				}

				let point = [x, y];
				let xy = data[(j >= xLabelIndex ? j-1 : j)];
				xy.points.push(point);
			} // for values
		} // for lines
		graphs.generateGraphs(data);
	}
};
