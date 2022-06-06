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
 var utils = {

	/**
	 * sort by integer property
	 *
	 * @param {Array} items
	 * @param {string} property name of the property to use for sorting
	 */
	bubbleSortIntAsc : function(items, property) {
		var swapp;
		var n = items.length-1;
		do {
			swapp = false;
			for (let i=0; i < n; ++i) {
				let first = items[i];
				first = first[property];
				let second = items[i+1];
				second = second[property];
				if (parseInt(first) > parseInt(second)) {
					let temp = items[i];
					items[i] = items[i+1];
					items[i+1] = temp;
					swapp = true;
				}
			}
			--n;
		} while (swapp);
	},

	/**
	 * sort by string property (ascending)
	 *
	 * @param {Array} items
	 * @param {string} property name of the property to use for sorting
	 */
	 bubbleSortStringAsc : function(items, property) {
		 var swapp;
		 var n = items.length-1;
		 do {
			 swapp = false;
			 for (let i=0; i < n; ++i) {
				 let first = items[i];
				 first = first[property];
				 let second = items[i+1];
				 second = second[property];
				 if (first.toString().localeCompare(second.toString()) > 0) {
					 let temp = items[i];
					 items[i] = items[i+1];
					 items[i+1] = temp;
					 swapp = true;
				 }
			 }
			 --n;
		 } while (swapp);
	 },

	 /**
	  * NOTE: this does not work properly in timezone with +/-0,5h offsets
	  * @return {integer} localtime offset (in hours) to UTC
		*/
	 getTimeOffset : function() {
		 return (new Date()).getTimezoneOffset()/60*-1;
	 }
};
