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
/**
 * Generates statistics based on the given data
 */
var statistics = {

	/**
	 * Calculate statistics for the given xy data set:
	 *
	 * {
	 *  total.
	 *	percentages[] // percentages of total for each count
 	 * }
	 *
	 * @param {Object} data
	 * @return {Object} statistics
	 */
	calculateStatistics : function(data) {
		var stats = {
			total : 0,
			percentages : []
		};
		for(let i=0;i<data.counts.length;++i){
			stats.total += data.counts[i];
		}
		for(let i=0;i<data.counts.length;++i){
			stats.percentages.push(Math.round(data.counts[i]/stats.total*100));
		}
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
	}
};
