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
 * Generates statistics based on the given data
 */
var statistics = {

	/**
	 * Calculate statistics for the given xy data set:
	 *
	 * {
	 *  xMin,
	 *  xMax,
	 *  yMin,
	 *  yMax,
	 *  yAverage,
	 *  yMedian,
	 *  ySD, // y standard deviation
 	 * }
	 *
	 * @param {Object} xy
	 * @return {Object} statistics
	 */
	calculateStatistics : function(xy) {
		var stats = new Object();
		statistics.calculateXStatistics(stats, xy);
		statistics.calculateYStatistics(stats, xy);
		return stats;
	},

	/**
	 * @param stats
	 * @param xy
	 */
	calculateXStatistics : function(stats, xy) {
		var values = [];
		for(let i=0;i<xy.points.length;++i){
			let value = xy.points[i][0];
			values.push(value);
		}
		values.sort(function(a, b){return a-b});
		stats.xMin = values[0];
		stats.xMax = values[values.length-1];
	},

	/**
	 * @param stats
	 * @param xy
	 */
	calculateYStatistics : function(stats, xy) {
		var values = [];
		var sum = 0;
		for(let i=0;i<xy.points.length;++i){
			let value = xy.points[i][1];
			sum += value;
			values.push(value);
		}
		values.sort(function(a, b){return a-b});
		stats.yMin = values[0];
		stats.yMax = values[values.length-1];
		stats.yAverage = sum / xy.points.length;
		stats.yMedian = statistics.calculateMedian(values);
		stats.ySD = statistics.calculateSD(values, stats.yAverage);
	},

	/**
	 *
	 * @param {Array[number]} array the sorted array
	 * @return {Number} median
	 */
	calculateMedian : function(array) {
		if(array == null || array.length < 2){
			console.log("Array size < 2.");
			return NaN;
		}else if(array.length % 2 == 0){
			return (array[array.length/2] + array[array.length/2 - 1])/2;
		}else{
			return array[Math.floor(array.length/2)];
		}
	},

	/**
	 *
	 * @param {Array[number]} array
	 * @return {Number} standard deviation
	 */
	calculateSD : function(array, mean) {
		if(array == null || array.length < 2){
			console.log("Array size < 2.");
			return NaN;
		}
		return Math.sqrt(array.map(x => Math.pow(x-mean,2)).reduce((a,b) => a+b)/array.length);
	}
};
