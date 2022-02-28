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

var graphs = {
	DATA_KEY_MARKINGS : "markings",
	DECIMAL_FIXED_ROUNDING : 2, // how many decimals to preserve when rounding numbers
	LIMIT_TYPE_MIN : "min",
	LIMIT_TYPE_MAX : "max",
	LOG_TICKS : [[-100, '-100'],[-90, '-90'],[-80, '-80'],[-70, '-70'],[-60, '-60'],[-50, '-50'],[-40, '-40'],[-30, '-30'],[-20, '-20'],[-10, '-10'],[0, '0'],[10, '10'],[20, '20'], [50, '50'],[100, '100'],[1000, '1000'],[2000, '2000'],[10000, '10000'],[50000, '50000'],[120000, '120000']], // from -100 to 120000 and more if needed
	SEPARATOR_FILTER : ",",
	SEPARATOR_LIMIT : ";",
	SEPARATOR_LIMIT_VALUE : ",",
	combinedLegendValueMap : null, // series index value div map
	combinedPlot : null,
	data : [],
	defaultMarkings : [],
	drawOnlyCombined : false,
	graphContainer : null,
	filters : null,
	limitContainer : null,
	limitMap : null, // field - markings[] map
	logLegendValueMap : null, // series index value div map
	logPlot : null,
	normalizedPlot : null,
	timeWindow : 10000, // in ms, times are checked +/- this amount if the actual time
	tooltip : null,
	valueHighlightSelector : null,


	/**
	 * initialize the UI
	 */
	initialize : function(){
		graphs.graphContainer = document.getElementById("graph-container");
		graphs.limitContainer = document.getElementById("limit-container");
		graphs.valueHighlightSelector = document.getElementById("input-highlight-values");
		graphs.valueHighlightSelector.addEventListener('change', function() {
		  if (!this.checked) {
		    graphs.tooltip.hide();
				graphs.updateCombinedLegend(graphs.combinedLegendValueMap, null, null, null, null);
				graphs.updateCombinedLegend(graphs.logLegendValueMap, null, null, null, null);
		  }
		});
		graphs.tooltip = $("#graph-tooltip");
		var selector = document.getElementById("input-only-combined-selector");
		graphs.drawOnlyCombined = selector.checked;
		selector.onclick = function(){
			graphs.drawOnlyCombined = this.checked;
		};
	},

	/**
	 *
	 */
	parseLimits : function() {
		graphs.limitMap = new Map();

		var value = document.getElementById("input-limits").value;
		if(value.trim() == ''){
			console.log("No limits.");
			return;
		}
		var parts = value.split(graphs.SEPARATOR_LIMIT);
		for(let i=0;i<parts.length;++i){
			let lv = parts[i].split(graphs.SEPARATOR_LIMIT_VALUE);
			if(lv.length != 5){
				console.log("Ignored invalid limit: "+parts[i]);
				continue;
			}

			let type = lv[4];
			if(graphs.LIMIT_TYPE_MIN.localeCompare(type) != 0 && graphs.LIMIT_TYPE_MAX.localeCompare(type) != 0){
				console.log("Ignored invalid limit: "+parts[i]);
				continue;
			}

			let field = lv[1];
			let markings = graphs.limitMap.get(field);
			let value = parseFloat(lv[2]);
			if(markings == null){
				markings = [];
				graphs.limitMap.set(field, markings);
			}

			let m = {
				yaxis : {
					from : value,
					to: value,
				},
				color: lv[3],
				limitLabel: lv[0],
				limitType: type
			};
			markings.push(m);
		}
	},

	/**
	 * Note: parseLimits() and generateGraphs() must be called before this function
	 */
	checkLimits : function() {
		graphs.limitContainer.innerHTML = "";
		if(graphs.limitMap.size < 1){
			console.log("No limits.");
			return;
		}

		var elementMap = new Map();
		for(let [key, value] of graphs.limitMap){
			for(let limit of value){
				let limitElement = document.createElement("div");
				limitElement.className = "limit-item";
				limitElement.style.color = limit.color;
				limitElement.appendChild(document.createTextNode(limit.limitLabel+" ("+key+") "+limit.limitType+" "+limit.yaxis.from));
				graphs.limitContainer.appendChild(limitElement);
				elementMap.set(key+limit.limitLabel, limitElement); // use field+name as key to prevent collisions on identical names
			}
		}

		for(let data of graphs.data){
			let limits = graphs.limitMap.get(data.yLabel);
			if(limits == null){ // no limits for this data
				continue;
			}
			for(let limit of limits){
				let min = (limit.limitType.localeCompare(graphs.LIMIT_TYPE_MIN) == 0);
				let max = false;
				if(!min && limit.limitType.localeCompare(graphs.LIMIT_TYPE_MAX) == 0){
					max = true;
				}
				for(let point of data.points){
					let value = point[1];
					if(min){
						if(value < limit.yaxis.from){
							let limitElement = elementMap.get(data.yLabel+limit.limitLabel);
							limitElement.textContent += " !!"+data.label+"!!";
							break; // there may be too many individual points under the limit to list all, so only take the first
						}
					}else if(max){
						if(value > limit.yaxis.from){
							let limitElement = elementMap.get(data.yLabel+limit.limitLabel);
							limitElement.textContent += " !!"+data.label+"!!";
							break; // there may be too many individual points over the limit to list all, so only take the first
						}
					} // else if
				} // for points
			} // for limits
		} // for data
	},

	/**
	 * Append statistics to the container
	 *
	 * @param {HtmlElement} container
	 * @param {Object} stats
	 * @param {string} color
	 */
	appendStatistics : function(container, stats, color) {
		var div = document.createElement("div");
		div.className = "statistics";

		var item = document.createElement("span");
		item.style = "color: "+color;
		item.className = "statistics-item";
		item.textContent = "ka: "+stats.yAverage;
		div.appendChild(item);

		item = document.createElement("span");
		item.style = "color: "+color;
		item.className = "statistics-item";
		item.textContent = "Md: "+stats.yMedian;
		div.appendChild(item);

		item = document.createElement("span");
		item.style = "color: "+color;
		item.className = "statistics-item";
		item.textContent = "SD: "+stats.ySD;
		div.appendChild(item);

		container.appendChild(div);
	},

	/**
	 * @param xy
	 */
	plotData : function(xy) {
		var container = document.createElement("div");
		container.className = "chart-container";
		graphs.graphContainer.appendChild(container);

		var label = document.createElement("div");
		label.className = "chart-label";
		label.style.color = xy.color;
		label.appendChild(document.createTextNode(xy.label+" / "+xy.yLabel));
		container.appendChild(label);

		var div = document.createElement("div");
		div.className = "chart-graph";
		div.id = "chart-"+xy.xLabel+"-"+xy.yLabel;
		div.oncontextmenu = function(e){
			e.preventDefault();
			return false;
		}
		container.appendChild(div);
		div = $(div);

		graphs.appendStatistics(container, xy.stats, xy.color);

		var markings = graphs.limitMap.get(xy.yLabel);
		var options = {
			xaxis: {
				mode: "time",
				timezone: "browser",
				min: xy.stats.xMin,
				max: xy.stats.xMax,
				timeBase: "milliseconds",
				timeformat: "%Y-%m-%d %H:%M",
				axisLabel : xy.xLabel
			},
			yaxis: {
				axisLabel : xy.yLabel,
				min: xy.stats.yMin,
				max: xy.stats.yMax,
			},
			colors : [xy.color],
			selection: {
				mode: "xy"
			},
			grid: {
				hoverable: true,
				markings : ((markings == null || markings.length < 1) ? graphs.defaultMarkings : graphs.defaultMarkings.concat(markings))
			},
			lines: {
				show: true
			},
			points: {
				show: false
			}
		};

		var plot = $.plot(div, [xy.points], options);

		div.bind("plothover", function (event, pos, item) {
			if(graphs.valueHighlightSelector.checked){
				if (item) {
					let x = item.datapoint[0].toFixed(graphs.DECIMAL_FIXED_ROUNDING);
					let y = item.datapoint[1].toFixed(graphs.DECIMAL_FIXED_ROUNDING);
					graphs.tooltip.html((new Date(Number(x))).toISOString() + " / " + y)
						.css({top: item.pageY+5, left: item.pageX+5})
						.fadeIn(200);
				} else {
					graphs.tooltip.hide();
				}
			}
		});
		div.bind("plothovercleanup", function (event, pos, item) {
				graphs.tooltip.hide();
		});

		div.bind("plotselected", function (event, ranges) {
			$.each(plot.getXAxes(), function(_, axis) {
				let opts = axis.options;
				opts.min = ranges.xaxis.from;
				opts.max = ranges.xaxis.to;
			});
			$.each(graphs.combinedPlot.getYAxes(), function(_, axis) {
				let opts = axis.options;
				opts.min = ranges.yaxis.from;
				opts.max = ranges.yaxis.to;
			});
			plot.setupGrid();
			plot.draw();
			plot.clearSelection();
		});

		var zoomOut = document.createElement("div");
		zoomOut.appendChild(document.createTextNode("< zoom out >"));
		zoomOut.className = "zoom-out";
		label.appendChild(zoomOut);
		zoomOut = $(zoomOut);
		zoomOut.data("plot", plot);
		zoomOut.click(function (event) {
			event.preventDefault();
			$(this).data("plot").zoomOut();
		});
	},

	/**
	 *
	 */
	plotNormalizedData : function() {
		var container = document.getElementById("chart-normalized-container");
		if(container){ // remove the old chart, if it exists
			graphs.graphContainer.removeChild(container);
		}
		container = document.createElement("div");
		container.id = "chart-normalized-container";
		graphs.graphContainer.appendChild(container);

		container.innerHTML = "<h3>normalized min/max graph</h3>";

		var div = document.createElement("div");
		div.className = "chart-graph";
		div.id = "chart-normalized";
		div.oncontextmenu = function(e){
			e.preventDefault();
			return false;
		}
		container.appendChild(div);
		div = $(div);

		var legendContainer = document.createElement("div");
		legendContainer.id = "chart-normalized-legend-container";
		legendContainer.className = "legend-container";
		container.appendChild(legendContainer);
		var legendMap = new Map();

		var xMin = null;
		var xMax = null;
		var colors = [];
		var nData = [];
		var xLabel = "";
		for(let i=0;i<graphs.data.length;++i){ // re-scale all data to 0...1
			let xy = graphs.data[i];
			let nd = new Object();
			nd.label = xy.yLabel;
			nd.seriesLabel = xy.label;
			if(xLabel.indexOf(xy.xLabel) < 0){
				xLabel += xy.xLabel + "/";
			}
			nd.color = xy.color;
			nd.data = [];
			nd.yMin = xy.stats.yMin;
			nd.yMax = xy.stats.yMax;
			nd.lines = {
				show: true
			};
			for(let j=0;j<xy.points.length;++j){
				let p = xy.points[j];
				let np = [p[0], (p[1] - nd.yMin)/(nd.yMax-nd.yMin)];
				nd.data.push(np);
			}
			nData.push(nd);

			if(xMin == null || xMin > xy.stats.xMin){
				xMin = xy.stats.xMin;
			}
			if(xMax == null || xMax < xy.stats.xMax){
				xMax = xy.stats.xMax;
			}

			let checkbox = document.createElement('input');
			checkbox.type = "checkbox";
			checkbox.className = "legend-item-selector";
			checkbox.checked = true;
			checkbox.series_index = i;
			checkbox.addEventListener("change", function() {
				let data = graphs.normalizedPlot.getData();
				data[this.series_index].lines.show = this.checked;
			  graphs.normalizedPlot.draw();
			});

			let legend = legendMap.get(xy.label);
			if(!legend){
				legend = document.createElement("div");
				legend.id = "chart-normalized-legend-"+xy.label;
				legend.className = "legend";
				let legendLabel = document.createElement("div");
				legendLabel.className = "legend-label";
				legendLabel.appendChild(document.createTextNode(xy.label));
				legend.appendChild(legendLabel);
				legendContainer.appendChild(legend);
				legendMap.set(xy.label, legend);
			}

			let item = document.createElement("div");
			item.appendChild(checkbox);
			item.className = "legend-item";
			item.appendChild(document.createTextNode(xy.yLabel));
			item.style.color = xy.color;
			legend.appendChild(item);
		} // for

		let button = document.createElement('button');
		button.appendChild(document.createTextNode("Valitse kaikki"));
		button.addEventListener("click", function() {
			//TODO re-calculate x/y min
			let data = graphs.normalizedPlot.getData();
			$("#chart-normalized-legend-container").find(".legend-item-selector").each(function(){
				this.checked = true;
				data[this.series_index].lines.show = true;
			});
			graphs.normalizedPlot.draw();
		});
		legendContainer.appendChild(button);
		button = document.createElement('button');
		button.appendChild(document.createTextNode("Poista valinnat"));
		button.addEventListener("click", function() {
			//TODO re-calculate x/y min
			let data = graphs.normalizedPlot.getData();
			$("#chart-normalized-legend-container").find(".legend-item-selector").each(function(){
				this.checked = false;
				data[this.series_index].lines.show = false;
			});
			graphs.normalizedPlot.draw();
		});
		legendContainer.appendChild(button);

		var options = {
			xaxis: {
				mode: "time",
				timezone: "browser",
				min: xMin,
				max: xMax,
				timeBase: "milliseconds",
				timeformat: "%Y-%m-%d %H:%M",
				axisLabel : xLabel.slice(0, -1) // remove the extra tailing / character
			},
			yaxis: {
				min: 0,
				max: 1,
			},
			selection: {
				mode: "x"
			},
			grid: {
				hoverable: true,
				markings : graphs.defaultMarkings.slice(0)
			},
			points: {
				show: false
			}
		};

		graphs.normalizedPlot = $.plot(div, nData, options);

		div.bind("plothover", function (event, pos, item) {
			if(graphs.valueHighlightSelector.checked){
				if (item) {
					let x = item.datapoint[0].toFixed(graphs.DECIMAL_FIXED_ROUNDING);
					let y = ((item.datapoint[1] * (item.series.yMax-item.series.yMin))+item.series.yMin).toFixed(graphs.DECIMAL_FIXED_ROUNDING);
					graphs.tooltip.html((new Date(Number(x))).toISOString() + " / " + y + " ("+item.series.seriesLabel+"/"+item.series.label+")")
						.css({top: item.pageY+5, left: item.pageX+5})
						.fadeIn(200);
				} else {
					graphs.tooltip.hide();
				}
			}
		});
		div.bind("plothovercleanup", function (event, pos, item) {
				graphs.tooltip.hide();
		});

		div.bind("plotselected", function (event, ranges) {
			$.each(graphs.normalizedPlot.getXAxes(), function(_, axis) {
				let opts = axis.options;
				opts.min = ranges.xaxis.from;
				opts.max = ranges.xaxis.to;
			});
			graphs.normalizedPlot.setupGrid();
			graphs.normalizedPlot.draw();
			graphs.normalizedPlot.clearSelection();
		});
	},

	/**
	 * @param {Array[Object]} data
	 * @param {integer} timestamp
	 * @return {Object} {bestTime=integer; bestValue=number}
	 */
	findBestTimeMatch : function(data, timestamp) {
		var minTimestamp = timestamp - graphs.timeWindow;
		var maxTimestamp = timestamp + graphs.timeWindow;
		var result = {
			bestTime : null,
			bestValueIndex : null,
			bestValue : null,
		};
		var bestDif = 0;
		for(let i=data.points.length-1;i>=0;--i){
			let pair = data.points[i];
			let time = pair[0];
			if(time == timestamp){
				result.bestTime = time;
				result.bestValueIndex = i;
				break;
			}else if(time > maxTimestamp){ // all timestamps are too big
				break;
			}else if(time > minTimestamp){
				let dif = Math.abs(timestamp-time);
				if(result.bestTime == null){
					result.bestTime = time;
					bestDif = dif;
					result.bestValueIndex = i;
				}else if(dif < bestDif){
					result.bestTime = time;
					bestDif = dif;
					result.bestValueIndex = i;
				}
			}
		} // for i
		if(result.bestValueIndex != null){
			result.bestValue = data.points[result.bestValueIndex][1];
		}
		return result;
	},

	/**
	 * @param {Map} legendValueMap
	 * @param {integer} timestamp if NaN or null, the legend highlight is cleared
	 * @param {Number} value
	 * @param {integer} yLabel
	 * @param {integer} selectedIndex
	 */
	updateCombinedLegend : function(legendValueMap, timestamp, value, yLabel, selectedIndex) {
		if(isNaN(timestamp)){
			timestamp = null;
		}else if(timestamp != null){
			timestamp = Math.round(timestamp);
		}

		for (const [index, div] of legendValueMap.entries()) {
			if(timestamp == null){
				div.className = "legend-value";
				div.innerHTML = "";
				continue;
			}

			if(index == selectedIndex){
				div.innerHTML = value.toFixed(graphs.DECIMAL_FIXED_ROUNDING) + " (" + (new Date(timestamp)).toISOString()+")";
				div.className = "legend-value-selected";
				continue;
			}
			div.className = "legend-value";

			let d = graphs.data[index];
			let match = graphs.findBestTimeMatch(d, timestamp);
			if(match.bestTime == null){
				div.innerHTML = "";
				console.log("No value for reference timestamp: "+timestamp+", label: "+d.label+ "/ "+d.yLabel+", check that your data arrays are in correct order: newest result first.");
				continue;
			}

			let text = match.bestValue.toFixed(graphs.DECIMAL_FIXED_ROUNDING);
			if(d.yLabel.localeCompare(yLabel) == 0 && match.bestValue != value){ // same data type, different value
				text += " (";
				if(match.bestValue < value){
					text += "-"+Math.abs(match.bestValue-value).toFixed(graphs.DECIMAL_FIXED_ROUNDING);
				}else{
					text += "+"+Math.abs(match.bestValue-value).toFixed(graphs.DECIMAL_FIXED_ROUNDING);
				}
				text += ")";
			}

			if(match.bestTime < timestamp){
				text +=  " (-"+Math.abs(match.bestTime-timestamp)/1000+"s)";
			}else if(match.bestTime > timestamp){
				text += " (+"+Math.abs(match.bestTime-timestamp)/1000+"s)";
			}
			div.innerHTML = text;
		}
	},

	/**
	 *
	 */
	plotCombinedData : function() {
		var container = document.getElementById("chart-combined-container");
		if(container){ // remove the old chart, if it exists
			graphs.graphContainer.removeChild(container);
		}
		container = document.createElement("div");
		container.id = "chart-combined-container";
		graphs.graphContainer.appendChild(container);

		container.innerHTML = "<h3>Combined graph</h3>";

		var div = document.createElement("div");
		div.className = "chart-graph";
		div.id = "chart-combined";
		div.oncontextmenu = function(e){
			e.preventDefault();
			return false;
		}
		container.appendChild(div);
		div = $(div);

		var legendContainer = document.createElement("div");
		legendContainer.id = "chart-combined-legend-container";
		legendContainer.className = "legend-container";
		container.appendChild(legendContainer);
		var legendMap = new Map();
		graphs.combinedLegendValueMap = new Map();

		var colors = [];
		var nData = [];
		var xLabel = "";
		for(let i=0;i<graphs.data.length;++i){
			let xy = graphs.data[i];
			let nd = new Object();
			nd.label = xy.yLabel;
			nd.seriesLabel = xy.label;
			nd.seriesIndex = i;
			if(xLabel.indexOf(xy.xLabel) < 0){
				xLabel += xy.xLabel + "/";
			}
			nd.color = xy.color;
			nd.data = [];
			nd.yMin = xy.stats.yMin;
			nd.yMax = xy.stats.yMax;
			nd.lines = {
				show: true
			};
			for(let j=0;j<xy.points.length;++j){
				let p = xy.points[j];
				let np = [p[0], p[1]];
				nd.data.push(np);
			}
			nData.push(nd);

			let checkbox = document.createElement('input');
			checkbox.type = "checkbox";
			checkbox.className = "legend-item-selector";
			checkbox.checked = true;
			checkbox.series_index = i;
			checkbox.addEventListener("change", function() {
				let data = graphs.combinedPlot.getData();
				data[this.series_index].lines.show = this.checked;
				let yMin = 2147483646;
				let yMax = -2147483646;
				for(let k=0;k<data.length;++k){
					let d = data[k];
					if(d.lines.show){
						d = d.data;
						for(let h=0;h<d.length;++h){
							let xy = d[h];
							if(xy[1] < yMin){
								yMin = xy[1];
							}else if(xy[1] > yMax){
								yMax = xy[1];
							} // else if
						} // for h
					} // if
				} // for k

				yMin = Math.round(yMin-yMin*0.02);
				yMax = Math.round(yMax+yMax*0.02);

				$.each(graphs.combinedPlot.getYAxes(), function(_, axis) {
					let opts = axis.options;
					opts.min = yMin;
					opts.max = yMax;
				});
				graphs.combinedPlot.setupGrid();
			  graphs.combinedPlot.draw();
			});

			let legend = legendMap.get(xy.label);
			if(!legend){
				legend = document.createElement("div");
				legend.id = "chart-combined-legend-"+xy.label;
				legend.className = "legend";
				let legendLabel = document.createElement("div");
				legendLabel.className = "legend-label";
				legendLabel.appendChild(document.createTextNode(xy.label));
				legend.appendChild(legendLabel);
				legendContainer.appendChild(legend);
				legendMap.set(xy.label, legend);
				legend.seriesIndex = [];
			}

			let item = document.createElement("div");
			item.appendChild(checkbox);
			item.className = "legend-item";
			item.appendChild(document.createTextNode(xy.yLabel));
			item.style.color = xy.color;
			let valueDiv = document.createElement("div");
			valueDiv.className = "legend-value";
			item.appendChild(valueDiv);
			graphs.combinedLegendValueMap.set(i, valueDiv);
			legend.seriesIndex.push(i);
			legend.appendChild(item);
		} // for graphs.data

		let button = document.createElement('button');
		button.appendChild(document.createTextNode("Valitse kaikki"));
		button.addEventListener("click", function() {
			//TODO re-calculate x/y min
			let data = graphs.combinedPlot.getData();
			$("#chart-combined-legend-container").find(".legend-item-selector").each(function(){
				this.checked = true;
				data[this.series_index].lines.show = true;
			});
			graphs.combinedPlot.draw();
		});
		legendContainer.appendChild(button);
		button = document.createElement('button');
		button.appendChild(document.createTextNode("Poista valinnat"));
		button.addEventListener("click", function() {
			//TODO re-calculate x/y min
			let data = graphs.combinedPlot.getData();
			$("#chart-combined-legend-container").find(".legend-item-selector").each(function(){
				this.checked = false;
				data[this.series_index].lines.show = false;
			});
			graphs.combinedPlot.draw();
		});
		legendContainer.appendChild(button);

		var options = {
			xaxis: {
				mode: "time",
				timezone: "browser",
				timeBase: "milliseconds",
				timeformat: "%Y-%m-%d %H:%M",
				axisLabel : xLabel.slice(0, -1) // remove the extra tailing / character
			},
			selection: {
				mode: "xy"
			},
			grid: {
				hoverable: true,
				markings : graphs.defaultMarkings.slice(0)
			},
			points: {
				show: false
			}
		};

		graphs.combinedPlot = $.plot(div, nData, options);

		div.bind("plothover", function (event, pos, item) {
			if(graphs.valueHighlightSelector.checked){
				if (item) {
					let x = item.datapoint[0];
					let y = item.datapoint[1];
					graphs.tooltip.html(new Date(x).toISOString() + " / " + y.toFixed(graphs.DECIMAL_FIXED_ROUNDING) + " ("+item.series.seriesLabel+"/"+item.series.label+")")
						.css({top: item.pageY+5, left: item.pageX+5})
						.fadeIn(200);
					graphs.updateCombinedLegend(graphs.combinedLegendValueMap, x, y, item.series.label, item.series.seriesIndex);
				} else {
					graphs.tooltip.hide();
				}
			}
		});
		div.bind("plothovercleanup", function (event, pos, item) {
				graphs.tooltip.hide();
		});

		div.bind("plotselected", function (event, ranges) {
			$.each(graphs.combinedPlot.getXAxes(), function(_, axis) {
				let opts = axis.options;
				opts.min = ranges.xaxis.from;
				opts.max = ranges.xaxis.to;
			});
			$.each(graphs.combinedPlot.getYAxes(), function(_, axis) {
				let opts = axis.options;
				opts.min = ranges.yaxis.from;
				opts.max = ranges.yaxis.to;
			});
			graphs.combinedPlot.setupGrid();
			graphs.combinedPlot.draw();
			graphs.combinedPlot.clearSelection();
		});

		graphs.appendLimitLegend(container, graphs.combinedPlot);
	},

	/**
	 * @param {Number} yMin
	 * @param {Number} yMax
	 * @return {Array[[value,key]]}
	 */
	generateTicks : function(yMin, yMax){
		var ticks = [];
		let first =  true;
		for(let li=0;li<graphs.LOG_TICKS.length;++li){
			let tick = graphs.LOG_TICKS[li];
			let v = tick[0];
			if(v > yMax){
				if(v != yMax && li<graphs.LOG_TICKS.length){
					ticks.push(graphs.LOG_TICKS[li]); // add one more
				}
				break;
			}
			if(v >= yMin){
				ticks.push(tick);
				if(first && v != yMin && li>0){
					ticks.unshift(graphs.LOG_TICKS[li-1]); // add one less
					first = false;
				}
			}
		}
		return ticks;
	},

	/**
	 *
	 */
	plotLogData : function() {
		var container = document.getElementById("chart-log-container");
		if(container){ // remove the old chart, if it exists
			graphs.graphContainer.removeChild(container);
		}
		container = document.createElement("div");
		container.id = "chart-log-container";
		graphs.graphContainer.appendChild(container);

		container.innerHTML = "<h3>log10 graph</h3>";

		var div = document.createElement("div");
		div.className = "chart-graph";
		div.id = "chart-log";
		div.oncontextmenu = function(e){
			e.preventDefault();
			return false;
		}
		container.appendChild(div);
		div = $(div);

		var legendContainer = document.createElement("div");
		legendContainer.id = "chart-log-legend-container";
		legendContainer.className = "legend-container";
		container.appendChild(legendContainer);
		var legendMap = new Map();
		graphs.logLegendValueMap = new Map();

		var yMin = 2147483646;
		var yMax = -2147483646;
		var colors = [];
		var nData = [];
		var xLabel = "";
		for(let i=0;i<graphs.data.length;++i){
			let xy = graphs.data[i];
			let nd = new Object();
			nd.label = xy.yLabel;
			nd.seriesLabel = xy.label;
			nd.seriesIndex = i;
			if(xLabel.indexOf(xy.xLabel) < 0){
				xLabel += xy.xLabel + "/";
			}
			nd.color = xy.color;
			nd.data = [];
			nd.yMin = xy.stats.yMin;
			nd.yMax = xy.stats.yMax;
			if(nd.yMin < yMin){
				yMin = nd.yMin;
			}
			if(nd.yMax > yMax){
				yMax = nd.yMax;
			}
			nd.lines = {
				show: true
			};
			for(let j=0;j<xy.points.length;++j){
				let p = xy.points[j];
				let np = [p[0], p[1]];
				nd.data.push(np);
			}
			nData.push(nd);

			let checkbox = document.createElement('input');
			checkbox.type = "checkbox";
			checkbox.className = "legend-item-selector";
			checkbox.checked = true;
			checkbox.series_index = i;
			checkbox.addEventListener("change", function() {
				let data = graphs.logPlot.getData();
				data[this.series_index].lines.show = this.checked;
				let ycMin = 2147483646;
				let ycMax = -2147483646;
				for(let k=0;k<data.length;++k){
					let d = data[k];
					if(d.lines.show){
						d = d.data;
						for(let h=0;h<d.length;++h){
							let xy = d[h];
							if(xy[1] < ycMin){
								ycMin = xy[1];
							}else if(xy[1] > ycMax){
								ycMax = xy[1];
							} // else if
						} // for h
					} // if
				} // for k

				ycMin = Math.round(ycMin-ycMin*0.02);
				ycMax = Math.round(ycMax+ycMax*0.02);

				let cticks = graphs.generateTicks(ycMin, ycMax);
				$.each(graphs.logPlot.getYAxes(), function(_, axis) {
					let opts = axis.options;
					opts.ticks = cticks
					opts.min = ycMin;
					opts.max = ycMax;
				});
				graphs.logPlot.setupGrid();
			  graphs.logPlot.draw();
			});

			let legend = legendMap.get(xy.label);
			if(!legend){
				legend = document.createElement("div");
				legend.id = "chart-log-legend-"+xy.label;
				legend.className = "legend";
				let legendLabel = document.createElement("div");
				legendLabel.className = "legend-label";
				legendLabel.appendChild(document.createTextNode(xy.label));
				legend.appendChild(legendLabel);
				legendContainer.appendChild(legend);
				legendMap.set(xy.label, legend);
				legend.seriesIndex = [];
			}

			let item = document.createElement("div");
			item.appendChild(checkbox);
			item.className = "legend-item";
			item.appendChild(document.createTextNode(xy.yLabel));
			item.style.color = xy.color;
			let valueDiv = document.createElement("div");
			valueDiv.className = "legend-value";
			item.appendChild(valueDiv);
			graphs.logLegendValueMap.set(i, valueDiv);
			legend.seriesIndex.push(i);
			legend.appendChild(item);
		} // for graphs.data

		let button = document.createElement('button');
		button.appendChild(document.createTextNode("Valitse kaikki"));
		button.addEventListener("click", function() {
			//TODO re-calculate x/y min
			let data = graphs.logPlot.getData();
			$("#chart-log-legend-container").find(".legend-item-selector").each(function(){
				this.checked = true;
				data[this.series_index].lines.show = true;
			});
			graphs.logPlot.draw();
		});
		legendContainer.appendChild(button);
		button = document.createElement('button');
		button.appendChild(document.createTextNode("Poista valinnat"));
		button.addEventListener("click", function() {
			//TODO re-calculate x/y min
			let data = graphs.logPlot.getData();
			$("#chart-log-legend-container").find(".legend-item-selector").each(function(){
				this.checked = false;
				data[this.series_index].lines.show = false;
			});
			graphs.logPlot.draw();
		});
		legendContainer.appendChild(button);

		var options = {
			xaxis: {
				mode: "time",
				timezone: "browser",
				timeBase: "milliseconds",
				timeformat: "%Y-%m-%d %H:%M",
				axisLabel : xLabel.slice(0, -1) // remove the extra tailing / character
			},
			yaxis: {
				ticks: graphs.generateTicks(yMin, yMax),
				tickSize: 10,
				transform: (x) => {
					if(x == 0){
						return x;
					}else	if(x < 0){
						return (-1 * Math.log10(Math.abs(x)));
					}else{
						return Math.log10(x);
					}
				},
    		inverseTransform: (x) => {
					if(x == 0){
						return x;
					}else if(x < 0){
						return (-1 * Math.pow(10,Math.abs(x)));
					}else{
						return Math.pow(10,x);
					}
				}
			},
			selection: {
				mode: "xy"
			},
			grid: {
				hoverable: true,
				markings : graphs.defaultMarkings.slice(0)
			},
			points: {
				show: false
			}
		};

		graphs.logPlot = $.plot(div, nData, options);

		div.bind("plothover", function (event, pos, item) {
			if(graphs.valueHighlightSelector.checked){
				if (item) {
					let x = item.datapoint[0];
					let y = item.datapoint[1];
					graphs.tooltip.html(new Date(x).toISOString() + " / " + y.toFixed(graphs.DECIMAL_FIXED_ROUNDING) + " ("+item.series.seriesLabel+"/"+item.series.label+")")
						.css({top: item.pageY+5, left: item.pageX+5})
						.fadeIn(200);
					graphs.updateCombinedLegend(graphs.logLegendValueMap, x, y, item.series.label, item.series.seriesIndex);
				} else {
					graphs.tooltip.hide();
				}
			}
		});
		div.bind("plothovercleanup", function (event, pos, item) {
				graphs.tooltip.hide();
		});

		div.bind("plotselected", function (event, ranges) {
			$.each(graphs.logPlot.getXAxes(), function(_, axis) {
				let opts = axis.options;
				opts.min = ranges.xaxis.from;
				opts.max = ranges.xaxis.to;
			});
			let cticks = graphs.generateTicks(ranges.yaxis.from, ranges.yaxis.to);
			$.each(graphs.logPlot.getYAxes(), function(_, axis) {
				let opts = axis.options;
				opts.ticks = cticks;
				opts.min = ranges.yaxis.from;
				opts.max = ranges.yaxis.to;
			});
			graphs.logPlot.setupGrid();
			graphs.logPlot.draw();
			graphs.logPlot.clearSelection();
		});

		graphs.appendLimitLegend(container, graphs.logPlot);
	},

	/**
	 * generate and append limit selection container, if there are limits
	 * @param {HtmlElement} container
	 * @param {Object} plot
	 */
	appendLimitLegend : function(container, plot) {
		if(graphs.limitMap.size < 1){
			console.log("No limits, not appending limit legend.");
			return;
		}

		var legendContainer = document.createElement("div");
		legendContainer.className = "limit-legend-container";
		container.appendChild(legendContainer);

		let legend = document.createElement("div");
		legend.className = "legend";
		legendContainer.appendChild(legend);

		let legendLabel = document.createElement("div");
		legendLabel.className = "legend-label";
		legendLabel.appendChild(document.createTextNode("limits"));
		legend.appendChild(legendLabel);

		for (let [key, value] of graphs.limitMap) {
			for(let m of value){
				let checkbox = document.createElement('input');
				checkbox.type = "checkbox";
				checkbox.className = "legend-item-selector";
				checkbox.checked = false;
				checkbox.addEventListener("change", function() {
					let markings = plot.getOptions().grid.markings;
					let marking = $(this).data(graphs.DATA_KEY_MARKINGS);
					let index = markings.indexOf(marking);
					if(index < 0){
						markings.push(marking);
					}else{
						markings.splice(index,1);
					}
					plot.setupGrid();
					plot.draw();
				});
				$(checkbox).data(graphs.DATA_KEY_MARKINGS, m);

				let item = document.createElement("div");
				item.appendChild(checkbox);
				item.className = "legend-item";
				item.appendChild(document.createTextNode(m.limitLabel));
				item.style.color = m.color;
				legend.appendChild(item);
			}
		}

		let button = document.createElement('button');
		button.appendChild(document.createTextNode("Valitse kaikki"));
		button.addEventListener("click", function() {
			$(this.parentNode).find(".legend-item-selector").each(function(){
				this.checked = true;
			});
			let markings = graphs.defaultMarkings.slice(0);
			for (let value of graphs.limitMap.values()) {
				for(let m of value){
					markings.push(m);
				}
			}
			plot.getOptions().grid.markings = markings;
			plot.draw();
		});
		legendContainer.appendChild(button);
		button = document.createElement('button');
		button.appendChild(document.createTextNode("Poista valinnat"));
		button.addEventListener("click", function() {
			$(this.parentNode).find(".legend-item-selector").each(function(){
				this.checked = false;
			});
			plot.getOptions().grid.markings = graphs.defaultMarkings.slice(0);
			plot.draw();
		});
		legendContainer.appendChild(button);
	},

	/**
	 * Generates graphs based on the given data object array
	 *
	 * The data object contains the following attributes:
	 *
	 * Data.label // data set label
	 * Data.xLabel // name of the x axis
	 * Data.yLabel // name of the y axis
	 * Data.color // chart line color (optional, if missing color is assigned automatically)
	 * Data.points // array of x,y point pairs [[x1,y1], [x2,y2] ... [xN, yN]]
	 * Data.xMin // x axis minimum (optional, always re-calculated/overwritten)
	 * Data.xMax // x axis maximum (optional, always re-calculated/overwritten)
	 * Data.yMin // y axis minimum (optional, always re-calculated/overwritten)
	 * Data.yMax // y axis maximum (optional, always re-calculated/overwritten)
	 *
	 * @param {Array[Data]} data object
	 */
	generateGraphs : function(data) {
		graphs.timeWindow = parseInt($("#input-time-window").val());
		graphs.parseLimits();
		graphs.parseFieldFilters();

		for(let i=0;i<data.length;++i){
			let xy = data[i];
			if(graphs.filters.includes(xy.yLabel)){
				console.log("Ignoring filtered data: "+xy.yLabel);
				continue;
			}

			xy.stats = statistics.calculateStatistics(xy);
			if(!xy.color){
				xy.color = colorUtil.generateColor();
			}

			if(!graphs.drawOnlyCombined){
				graphs.plotData(xy);
			}
			graphs.data.push(xy);
		} // for data

		if(graphs.data.length > 1){
			graphs.plotNormalizedData();
			graphs.plotCombinedData();
			graphs.plotLogData();
		}

		graphs.checkLimits();
	},

	/**
	 * parse field filter list
	 */
	parseFieldFilters : function() {
		graphs.filters = document.getElementById("input-filters").value.split(graphs.SEPARATOR_FILTER);
	},

	/**
	 * clear all graphs
	 */
	clearGraphs : function() {
		graphs.graphContainer.innerHTML = "";
		graphs.limitContainer.innerHTML = "";
		colorUtil.resetActiveColor();
		graphs.normalizedPlot = null;
		graphs.combinedPlot = null;
		graphs.logPlot = null;
		graphs.data = [];
	},

	/**
	 * @param {string} ids list of div ids, separated by comma
	 */
	toggleDivs : function(ids) {
		console.log(ids);
		var idArray = ids.split(',');
		console.log(idArray);
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
