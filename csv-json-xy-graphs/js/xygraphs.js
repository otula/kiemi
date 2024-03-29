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
 *
 */
var xygraphs = {
	DECIMAL_FIXED_ROUNDING : 2, // how many decimals to preserve when rounding numbers
	MAX_META_FIELDS : 10, // maximum number of meta data fields to show in the hover popup
	DATA_SERIES_INDEX : "seriesIndex",
	DATA_POINTS_TOTAL : "pointsTotal",
	DATA_POINTS_OUTSIDE : "pointsOutside",
	SEPARATOR_POLYGON : ";",
	SEPARATOR_LIMIT : ",",
	plot : null,
	data : [],
	graphContainer : null,
	legendContainer : null,
	limitsPolygons : null, // array of {polygon[], color}
	tooltip : null,
	inputLimitsPolygon : null,
	inputAxisXYScale : null,
	inputAxisLabels : null,
	selectedTotal : 0,
	selectedOutside : 0,
	elementSelected : null,


	/**
	 * initialize the UI
	 */
	initialize : function(){
		xygraphs.graphContainer = document.getElementById("xy-graph-container");
		xygraphs.legendContainer = document.getElementById("xy-legend-container");
		xygraphs.inputLimitsPolygon = document.getElementById("input-limits-polygon");
		xygraphs.inputAxisXYScale = document.getElementById("input-axis-xy-scale");
		xygraphs.inputAxisLabels = document.getElementById("input-axis-labels");
		xygraphs.tooltip = $("#graph-tooltip");
	},

	/**
	 *
	 */
	setInitialZoom : function() {
		var scales = xygraphs.inputAxisXYScale.value.trim();
		if(scales.length < 1){
			console.log("No initial scale given for x/y.");
			return;
		}

		var axes = scales.split(";");
		if(axes.length != 2){
			console.log("Invalid xy-scale: "+scales);
			return;
		}

		var xp = axes[0].split("/");
		if(xp.length != 2){
			console.log("Invalid xy-scale: "+scales);
			return;
		}
		var xTickSize = Number(xp[1]);
		xp = xp[0].split("-");
		if(xp.length != 2){
			console.log("Invalid xy-scale: "+scales);
			return;
		}
		var xMin = Number(xp[0]);
		var xMax = Number(xp[1]);

		var yp = axes[1].split("/");
		if(yp.length != 2){
			console.log("Invalid xy-scale: "+scales);
			return;
		}
		var yTickSize = Number(yp[1]);
		yp = yp[0].split("-");
		if(xp.length != 2){
			console.log("Invalid xy-scale: "+scales);
			return;
		}
		var yMin = Number(yp[0]);
		var yMax = Number(yp[1]);

		if(isNaN(xTickSize) || isNaN(xMin) || isNaN(xMax) || isNaN(yTickSize) || isNaN(yMin) || isNaN(yMax)){
			console.log("Invalid xy-scale: "+scales);
			return;
		}

		var options = xygraphs.plot.getOptions();
		$.each(xygraphs.plot.getXAxes(), function(_, axis) {
			axis.options.min = xMin;
			axis.options.max = xMax;
			axis.options.tickSize = xTickSize;
		});
		$.each(xygraphs.plot.getYAxes(), function(_, axis) {
			axis.options.min = yMin;
			axis.options.max = yMax;
			axis.options.tickSize = yTickSize;
		});
		xygraphs.plot.setupGrid();
		xygraphs.plot.draw();
	},

	/**
	 *
	 */
	parseLimits : function() {
		var value = xygraphs.inputLimitsPolygon.value;
		if(value.length > 0){
			let polygons = value.split(xygraphs.SEPARATOR_POLYGON);
			xygraphs.limitsPolygons = [];
			for(let polygon of polygons){
				let parts = polygon.split("#");
				if(parts.length != 2){
					console.log("Invalid limit polygon: "+polygon);
					return;
				}
				let p = {
					polygon : JSON.parse(parts[0]),
					color : "#"+parts[1]
				};
				xygraphs.limitsPolygons.push(p);
			}

			if(xygraphs.limitsPolygons.length < 1){
				xygraphs.limitsPolygons = null;
				console.log("No valid polygons.");
			}
		}else{
			xygraphs.limitsPolygons = null;
		}
	},

	/**
	 * Note: parseLimits() and generateGraphs() must be called before this function
	 */
	checkLimits : function() {
		xygraphs.legendContainer.innerHTML = "";
		if(xygraphs.limitsPolygons == null){
			xygraphs.elementSelected = null;
			console.log("No limits.");
			return;
		}

		xygraphs.elementSelected = document.createElement("div");
		xygraphs.elementSelected.className = "xy-legend-total-container";
		xygraphs.legendContainer.appendChild(xygraphs.elementSelected);
		xygraphs.selectedTotal = 0;
		xygraphs.selectedOutside = 0;

		for(let i=0;i<xygraphs.data.length;++i){
			let data = xygraphs.data[i];
			let div = document.createElement("div");
			div.className = "xy-legend-container";
			div.style.color = data.color;
			xygraphs.selectedTotal += data.points.length;
			let totalOutside = 0;
			let text = data.label+" ("+data.xLabel+"/"+data.yLabel+"), yhteensä: "+data.points.length+", ulkopuolella:";
			for(let polygon of xygraphs.limitsPolygons){
				let outside = 0;
				for(let xy of data.points){
					if(!xyutils.inside(xy, polygon.polygon)){
						++outside;
					}
				}
				totalOutside += outside;
				text += "<span style='color: "+polygon.color+"'> "+outside+" ("+Math.round((outside/data.points.length)*100)+" %)</span>"
			}
			xygraphs.selectedOutside += totalOutside;
			div.innerHTML = text;

			let checkbox = document.createElement('input');
			checkbox.type = "checkbox";
			checkbox.className = "xy-legend-item-selector";
			checkbox.checked = true;
			let qc = $(checkbox);
			qc.data(xygraphs.DATA_SERIES_INDEX, i);
			qc.data(xygraphs.DATA_POINTS_TOTAL, data.points.length);
			qc.data(xygraphs.DATA_POINTS_OUTSIDE, totalOutside);
			checkbox.addEventListener("change", function() {
				let data = xygraphs.plot.getData();
				let t = $(this);
				data[t.data(xygraphs.DATA_SERIES_INDEX)].points.show = this.checked;
				let soutside = t.data(xygraphs.DATA_POINTS_OUTSIDE);
				let stotal = t.data(xygraphs.DATA_POINTS_TOTAL);
				if(this.checked){
					xygraphs.selectedOutside += soutside;
					xygraphs.selectedTotal += stotal;
				}else{
					xygraphs.selectedOutside -= soutside;
					xygraphs.selectedTotal -= stotal;
				}
			  xygraphs.plot.draw();
				xygraphs.updateTotals();
			});
			div.appendChild(checkbox);
			xygraphs.legendContainer.appendChild(div);
		}

		let button = document.createElement('button');
		button.appendChild(document.createTextNode("Valitse kaikki"));
		button.addEventListener("click", function() {
			let data = xygraphs.plot.getData();
			xygraphs.selectedTotal = 0;
			xygraphs.selectedOutside = 0;
			$("#xy-legend-container").find(".xy-legend-item-selector").each(function(){
				let t = $(this);
				data[t.data(xygraphs.DATA_SERIES_INDEX)].points.show = true; // the limits polygon is part of the data, so only change options for indexes included in selectors
				this.checked = true;
				xygraphs.selectedOutside += t.data(xygraphs.DATA_POINTS_OUTSIDE);
				xygraphs.selectedTotal += t.data(xygraphs.DATA_POINTS_TOTAL);
			});
			xygraphs.plot.draw();
			xygraphs.updateTotals();
		});
		xygraphs.legendContainer.appendChild(button);
		button = document.createElement('button');
		button.appendChild(document.createTextNode("Poista valinnat"));
		button.addEventListener("click", function() {
			let data = xygraphs.plot.getData();
			xygraphs.selectedTotal = 0;
			xygraphs.selectedOutside = 0;
			$("#xy-legend-container").find(".xy-legend-item-selector").each(function(){
				let t = $(this);
				data[t.data(xygraphs.DATA_SERIES_INDEX)].points.show = false; // the limits polygon is part of the data, so only change options for indexes included in selectors
				this.checked = false;
			});
			xygraphs.plot.draw();
			xygraphs.updateTotals();
		});
		xygraphs.legendContainer.appendChild(button);
		xygraphs.updateTotals();
	},

	/**
	 *
	 */
	updateTotals : function() {
		let text = "Valittuna: ";
		if(xygraphs.selectedTotal == 0){
			text += "0";
		}else if(xygraphs.selectedOutside == 0){
			text += xygraphs.selectedTotal + ", ulkopuolella: 0 (0 %)";
		}else{
			text += xygraphs.selectedTotal + ", ulkopuolella: "+xygraphs.selectedOutside+" ("+Math.round(xygraphs.selectedOutside/xygraphs.selectedTotal*100)+" %)";
		}
		xygraphs.elementSelected.innerHTML = text;
	},

	/**
	 * @param {Array[Object]} data
	 */
	addLimitPolygon : function(data){
		if(xygraphs.limitsPolygons == null){
			console.log("No limits.");
			return;
		}
		for(let polygon of xygraphs.limitsPolygons){
			let nd = new Object();
			nd.label = "Limit polygon";
			nd.seriesLabel = "Limit polygon";
			nd.seriesIndex = data.length;
			nd.color = polygon.color;
			nd.data = [];
			nd.lines = {
				show: true
			};
			for(let i=0;i<polygon.polygon.length;++i){
				nd.data.push(polygon.polygon[i]);
			}
			nd.data.push(polygon.polygon[0]); // close the shape
			data.push(nd);
		}
	},

	/**
	 *
	 */
	plotData : function() {
		var container = document.getElementById("xy-chart-combined-container");
		if(container){ // remove the old chart, if it exists
			xygraphs.graphContainer.removeChild(container);
		}
		container = document.createElement("div");
		container.id = "xy-chart-combined-container";
		xygraphs.graphContainer.appendChild(container);

		var div = document.createElement("div");
		div.className = "xy-chart-graph";
		div.id = "xy-chart-combined";
		div.oncontextmenu = function(e){
			e.preventDefault();
			return false;
		}
		container.appendChild(div);
		div = $(div);

		var colors = [];
		var nData = [];
		for(let i=0;i<xygraphs.data.length;++i){
			let xy = xygraphs.data[i];
			let nd = new Object();
			nd.label = xy.yLabel;
			nd.seriesLabel = xy.label;
			nd.seriesIndex = i;
			nd.color = xy.color;
			nd.data = [];
			nd.points = {
				show: true
			};
			for(let j=0;j<xy.points.length;++j){
				let p = xy.points[j];
				let np = [p[0], p[1]];
				nd.data.push(np);
			}
			nData.push(nd);
		} // for xygraphs.data

		var labelValues = xygraphs.inputAxisLabels.value;
		var xAxisLabel = xygraphs.data[0].xLabel;
		var yAxisLabel = xygraphs.data[0].yLabel;
		if(labelValues.length > 0){
			let parts = labelValues.split(",");
			xAxisLabel = parts[0];
			yAxisLabel = parts[1];
		}

		var options = {
			xaxis: {
				axisLabel : xAxisLabel
			},
			yaxis: {
				axisLabel : yAxisLabel
			},
			selection: {
				mode: "xy"
			},
			grid: {
				hoverable: true
			}
		};

		xygraphs.addLimitPolygon(nData);

		xygraphs.plot = $.plot(div, nData, options);

		xygraphs.setInitialZoom();

		div.bind("plothover", function (event, pos, item) {
			if (item) {
				let text = null;
				if(item.seriesIndex >= xygraphs.data.length){
					text = item.datapoint[0].toFixed(xygraphs.DECIMAL_FIXED_ROUNDING)+", "+item.datapoint[1].toFixed(xygraphs.DECIMAL_FIXED_ROUNDING);
				}else{
					let data = xygraphs.data[item.seriesIndex];
					let meta = data.metaPoints[item.dataIndex];
					text = data.xLabel+" = "+item.datapoint[0].toFixed(xygraphs.DECIMAL_FIXED_ROUNDING)+"<br>"+data.yLabel+" = "+item.datapoint[1].toFixed(xygraphs.DECIMAL_FIXED_ROUNDING);
					let count = 0;
					for (let key in meta) {
						if (meta.hasOwnProperty(key)) {
							text += "<br>"+key+" = "+meta[key];
							if(++count == xygraphs.MAX_META_FIELDS){
								text += "<br> ...";
								break;
							}
					  }
					}
				}
				xygraphs.tooltip.html(text).css({top: item.pageY+5, left: item.pageX+5}).fadeIn(200);
			} else {
				xygraphs.tooltip.hide();
			}
		});
		div.bind("plothovercleanup", function (event, pos, item) {
				xygraphs.tooltip.hide();
		});

		div.bind("plotselected", function (event, ranges) {
			$.each(xygraphs.plot.getXAxes(), function(_, axis) {
				axis.options.min = ranges.xaxis.from;
				axis.options.max = ranges.xaxis.to;
			});
			$.each(xygraphs.plot.getYAxes(), function(_, axis) {
				axis.options.min = ranges.yaxis.from;
				axis.options.max = ranges.yaxis.to;
			});
			xygraphs.plot.setupGrid();
			xygraphs.plot.draw();
			xygraphs.plot.clearSelection();
		});
	},

	/**
	 * Generates graphs based on the given data object
	 *
	 * The data object contains the following attributes:
	 *
	 * Data.label // data set label
	 * Data.xLabel // name of the x axis
	 * Data.yLabel // name of the y axis
	 * Data.color // chart line color (optional, if missing color is assigned automatically)
	 * Data.points // array of x,y point pairs [[x1,y1], [x2,y2] ... [xN, yN]]
	 * Data.metaPoints // array of Objects with metadata {meta1: value1, meta2: value2}
	 *
	 * @param {Array[Object]} data object
	 */
	generateGraphs : function(data) {
		xygraphs.parseLimits();

		for(let d of data){
			if(!d.color){
				d.color = colorUtil.generateColor();
			}
			xygraphs.data.push(d);
		}
		xygraphs.plotData();
		xygraphs.checkLimits();
	},

	/**
	 * clear all graphs
	 */
	clearGraphs : function() {
		xygraphs.graphContainer.innerHTML = "";
		xygraphs.legendContainer.innerHTML = "";
		colorUtil.resetActiveColor();
		xygraphs.plot = null;
		xygraphs.data = [];
	},

	/**
	 * @param {string} ids list of div ids, separated by comma
	 */
	toggleDivs : function(ids) {
		var idArray = ids.split(',');
		for(let id of idArray){
			let element = $(document.getElementById(id));
			if(element.hasClass("hidden")){
				element.removeClass("hidden");
			}else{
				element.addClass("hidden");
			}
		}
	}
};
