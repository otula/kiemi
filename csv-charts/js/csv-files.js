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
	CSV_SEPARATOR : ';',
	KEY_SEPARATOR : '=',
	VALUE_SEPRATOR : ',',
	LINE_SEPARATOR : '\n',
	SEPARATOR_REPLACEMENT : ' ',
	STRING_ESCAPE_SEPARATOR : '\"',
	ignoredColumns : null,
	inProgress : false,
	data : null,
	currentLineNro : 0,
	lines : null,

	/**
	 * initialize the UI
	 */
	initialize : function(){
		var filesToUpload = document.getElementById("csv-file-upload");
		filesToUpload.ondragover = function () { this.className = "hover"; return false; };
		filesToUpload.ondragleave = function () { this.className = ""; return false; };
		filesToUpload.ondragend = function () { this.className = ""; return false; };
		filesToUpload.ondrop = function (e) {
			this.className = "";
			e.preventDefault();
			csvFiles.readFiles(e.dataTransfer.files);
		};
	},

	/**
	 *
	 * @param {FileList} fileList
	 */
	readFiles : function(fileList){
		if(csvFiles.inProgress){
			alert("Already processing...");
			return;
		}
		csvFiles.inProgress = true;
		for(let i=0;i<fileList.length;++i){
			csvFiles.readFile(fileList[i]);
		}
		csvFiles.reset();
		csvFiles.inProgress = false;
	},

	/**
	 * reset internal variables
	 */
	reset : function() {
		csvFiles.data = [];
		csvFiles.currentLineNro = 0;
		csvFiles.ignoredColumns = [];
		csvFiles.lines = null;
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

		var reader = new FileReader();
  	reader.onload = function(){
    	csvFiles.readCSV(this.result, file.name);
  	};
  	reader.readAsText(file);
	},

	/**
	 * create data sets and process keys/labels until first empty line
	 * @return {boolean} true on success
	 */
	processSetsAndKeys : function() {
		var empty = true;
		csvFiles.ignoredColumns = [];
		do {
			let columns = csvFiles.lines[csvFiles.currentLineNro].split(csvFiles.CSV_SEPARATOR);
			if(csvFiles.currentLineNro > 0 && columns.length != csvFiles.data.length){
				alert("Invalid column count "+columns.length+" on line "+csvFiles.currentLineNro+", "+csvFiles.lines[csvFiles.currentLineNro]);
				return false;
			}
			empty = true; // empty line
			for(let i=0;i<columns.length;++i){
				let column = columns[i];
				if(csvFiles.currentLineNro == 0){ // first line
					if(column){
						let set = {
							labels : [],
							keys : [],
							counts : [],
							colors : null
						};
						let parts = column.split(csvFiles.KEY_SEPARATOR);
						if(parts.length != 2){
							alert("Invalid key on line "+csvFiles.currentLineNro+", column "+i+": "+column+", "+csvFiles.lines[csvFiles.currentLineNro]);
							return false;
						}
						set.labels.push(parts[1].trim());
						set.keys.push(parts[0].trim());
						csvFiles.data.push(set);
					}else{
						console.log("Ignored column "+i+", empty key.");
						csvFiles.ignoredColumns.push(i);
						csvFiles.data.push(null); // add to keep index in sync, will be removed
					}
					empty = false;
				}else if(column){ // not first line
					if(csvFiles.ignoredColumns.indexOf(i) >= 0) { // not first line and the column contains data even though on the first line this column did NOT contain anything
						alert("Invalid line "+csvFiles.currentLineNro+", column "+i+": found value, but did not have value on first line: "+column);
						return false;
					}
					let set = csvFiles.data[i];
					let parts = column.split(csvFiles.KEY_SEPARATOR);
					if(parts.length != 2){
						alert("Invalid key on line "+csvFiles.currentLineNro+", column "+i+": "+column+", "+csvFiles.lines[csvFiles.currentLineNro]);
						return false;
					}
					let label = parts[1].trim();
					if(set.labels.indexOf(label) >= 0){
						alert("Duplicate label on line "+csvFiles.currentLineNro+", column "+i+": "+column+", "+csvFiles.lines[csvFiles.currentLineNro]);
						return false;
					}
					let key = parts[0].trim();
					if(set.keys.indexOf(key) >= 0){
						alert("Duplicate key on line "+csvFiles.currentLineNro+", column "+i+": "+column+", "+csvFiles.lines[csvFiles.currentLineNro]);
						return false;
					}
					set.keys.push(key);
					set.labels.push(label);
					empty = false;
				} // else empty column, ignore it
			} // for
			++csvFiles.currentLineNro;
			if(csvFiles.currentLineNro >= csvFiles.lines.length){
				alert("Invalid file, not enough lines. Missing data?");
				return false;
			}
		} while(!empty);
		return true;
	},

	/**
	 * process titles
	 *
	 * @param {string} filename
	 * @return {boolean} true on sucess
	 */
	processTitles : function (filename) {
		let columns = csvFiles.lines[csvFiles.currentLineNro++].split(csvFiles.CSV_SEPARATOR);
		if(columns.length != csvFiles.data.length){
			alert("Invalid column count "+columns.length+" on line "+csvFiles.currentLineNro+", "+csvFiles.lines[csvFiles.currentLineNro]);
			return false;
		}
		for(let i=0;i<columns.length;++i){
			if(csvFiles.ignoredColumns.indexOf(i) < 0){
				csvFiles.data[i].title = filename+" : "+columns[i];
			}
		}
		return true;
	},

	/**
	 * create data
	 * @return {boolean} true on sucess
	 */
	processData : function () {
		if(csvFiles.currentLineNro >= csvFiles.data.length){
			alert("Invalid file, not enough lines. Missing data?");
			return false;
		}

		for(let i=0;i<csvFiles.data.length;++i){ // initialize counts
			if(csvFiles.ignoredColumns.indexOf(i) < 0){
				let set = csvFiles.data[i];
				for(let j=0;j<set.keys.length;++j){
					set.counts.push(0);
				}
			}
		}

		do{
			let columns = csvFiles.lines[csvFiles.currentLineNro].split(csvFiles.CSV_SEPARATOR);
			for(let i=0;i<columns.length;++i){
				let value = columns[i].trim();
				if(value && csvFiles.ignoredColumns.indexOf(i) < 0){
					let set = csvFiles.data[i];
					let values = value.split(csvFiles.VALUE_SEPRATOR);
					for(let j=0;j<values.length;++j){
						value = values[j].trim();
						let index = set.keys.indexOf(value);
						if(index < 0){
							alert("Invalid key on line "+csvFiles.currentLineNro+", column "+i+": "+value+", "+csvFiles.lines[csvFiles.currentLineNro]);
							return false;
						}
						set.counts[index] = set.counts[index]+1;
					} // for
				} // if
			} // for
			++csvFiles.currentLineNro;
		}while(csvFiles.currentLineNro < csvFiles.lines.length);
		return true;
	},

	/**
	 * Stript all delilmiters from string.
	 *
	 * Note that certain programs may not escape double quotes inside string causing hard-to-detect strings (e.g. "";"").
	 * This function assumes such string are not used.
	 *
	 * @param {string }text
	 * @return {string}
	 */
	stripDelimiters : function(text) {
		var a = Array.from(text);
		var inString = false;
		for(let i=0;i<a.length;++i){
			if(inString){
				let c = a[i];
				if(c == csvFiles.CSV_SEPARATOR || c == csvFiles.LINE_SEPARATOR){
					a[i] = csvFiles.SEPARATOR_REPLACEMENT;
				}else if(c == csvFiles.STRING_ESCAPE_SEPARATOR){
					inString = false;
				}
			}else if(a[i] == csvFiles.STRING_ESCAPE_SEPARATOR){
				inString = true;
			}
		}

		return a.join("");
	},

	/**
	 *
	 * @param {String} csv
	 * @param {String} filename
	 * @param {String} separator
	 */
	readCSV : function(csv, filename) {
		csvFiles.reset();
		csvFiles.lines = csvFiles.stripDelimiters(csv).split(csvFiles.LINE_SEPARATOR);
		if(csvFiles.lines.length < 5){ // there must be at least 2 keys, 1 empty line, labels and value line
			alert("Not enough data: lines < 5.");
			return;
		}

		if(!csvFiles.processSetsAndKeys()){
			alert("Failed to process sets and keys for "+filename);
			return;
		}

		if(!csvFiles.processTitles(filename)){
			alert("Failed to process titles for "+filename);
			return;
		}

		if(!csvFiles.processData()){
			alert("Failed to process data for "+filename);
			return;
		}

		let data = [];
		for(let i=0;i<csvFiles.data.length;++i){
			if(csvFiles.ignoredColumns.indexOf(i) < 0){
				data.push(csvFiles.data[i]);
			}
		}

		charts.generateCharts(data);
	}
};
