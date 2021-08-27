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
var timeutils = {
	/**
	 *
	 * @param {string} date either as a ISO8601 string or as milliseconds from unix epoch
	 * @return {integer} time in ms or null
	 */
	parseDate : function (date) {
		if(isNaN(date)){ // string date
			var dms = Date.parse(date);
			return (isNaN(dms) ? null : dms);
		}else{
			let timestampMultiplier = null;
			if(date.toString().length - Date.now().toString().length < -2){ // the time stamp has at least 2 digits less than current timestamp
				timestampMultiplier = 1000; // the original values are probably in seconds
			}else{
				timestampMultiplier = 1; // the original values are probably in milliseconds
			}
			dms = Number(date);
			return (isNaN(dms) ? null : (dms*timestampMultiplier));
		}
	}
};
