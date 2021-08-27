"use strict";
/**
 * Copyright 2020 Tampere University
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

var charts = {
	//COLORS : ["#ff0000", "#0000ff", "#800080", "#008000", "#FFA500"],
	COLORS : ["#90A4D8", "#F7AA8A", "#C6C6C6", "#FFD684", "#A5C3E6","#9198AF","#C4978B","#9E9E9E","#A7CD95"],
	chartContainer : null,
	chartTypeSelector : null,
	pieOptions : { legend: { labels: { fontSize: 24	}	}	},
	barOptions : { legend: { display: false }, scales: { yAxes: [{ ticks: {	fontSize: 24 } }], xAxes: [{ ticks: { fontSize: 24, callback: function(label) { if (/\s/.test(label)) { return label.split(" "); }else{ return label; } } } }]	} },

	/**
	 * initialize the UI
	 */
	initialize : function(){
		charts.chartContainer = document.getElementById("chart-container");
		charts.chartTypeSelector = document.getElementsByName('csv-file-chart-type');
	},

	/**
	 * @param data
	 */
	plotData : function(data) {
		if(!data.colors){
			data.colors = charts.generateColors(data.counts.length);
		}

		var p = document.createElement("p");

		var title = document.createElement("div");
		title.className = "chart-title";
		title.appendChild(document.createTextNode(data.title));
		p.appendChild(title);

		var canvas = document.createElement("canvas");
		canvas.className = "chart-canvas";
		p.appendChild(canvas);

		var type = charts.getType();
		var c = new Chart(canvas.getContext('2d'), {
		    type: type,
		    data: {
		        labels: data.labels,
		        datasets: [{
		            data: data.counts,
		            backgroundColor: data.colors,
		        }]
		    },
				options: (type == "pie" ? charts.pieOptions : charts.barOptions)
		});

		p.appendChild(charts.createStatistics(data));
		charts.chartContainer.appendChild(p);
	},

	/**
	 * @param {Object} data
	 */
	createStatistics : function(data) {
		var stats = statistics.calculateStatistics(data);
		var sNode = document.createElement("div");
		sNode.className = "statistics-container";

		for(let i=0;i<stats.percentages.length;++i){
			let span = document.createElement("span");
			span.style.color = data.colors[i];
			span.appendChild(document.createTextNode(stats.percentages[i]+" %"));
			sNode.appendChild(span);
		}

		let span = document.createElement("span");
		span.appendChild(document.createTextNode("("+stats.total+")"));
		sNode.appendChild(span);

		return sNode;
	},

	/**
	 * Generates graphs based on the given data object array
	 *
	 * The data object contains the following attributes:
	 *
	 * Data.title // title for the chart
	 * Data.labels[] // data set labels
	 * Data.keys[] // data set keys (not used for creating charts)
	 * Data.counts[] // values per label
	 * Data.colors[] // chart data set colors (optional, if missing colors are assigned automatically)
	 *
	 * @param {Array[Data]} data object
	 */
	generateCharts : function(data) {
		for(let i=0;i<data.length;++i){
			charts.plotData(data[i]);
		} // for data
	},

	/**
	 * clear all graphs
	 */
	clearCharts : function() {
		charts.chartContainer.innerHTML = "";
	},

	/**
	 * @param {integer} count number of colors to generate
	 * @return {string[]} new colors
	 */
	generateColors : function(count) {
		var colors = [];
		for(let i=0;i<count;++i){
			if(i >= charts.COLORS.length){
				colors[i] = "#"+((1<<24)*Math.random()|0).toString(16);
			}else{
				colors[i] = charts.COLORS[i];
			}
		}
		return colors;
	},

	/**
	 * @return {string} currently selected chart type
	 */
	getType : function() {
		for (var i = 0, length = charts.chartTypeSelector.length; i < length; i++) {
		  if (charts.chartTypeSelector[i].checked) {
				return charts.chartTypeSelector[i].value;
		  }
		}
	}
};
