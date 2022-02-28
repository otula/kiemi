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

var uriinitializer = {

	/**
	 * initialize the UI
	 */
	initialize : function(){
		var urlParams = new URLSearchParams(window.location.search);
	  var p = urlParams.getAll("timestamp_field");
	  if (p.length > 0) {
	    document.getElementById("input-timestamp-field").value = p[0];
	  }
		p = urlParams.getAll("only_combined");
	  if (p.length > 0 && p[0] == "true") {
	    document.getElementById("input-only-combined-selector").checked = true;
	  }
		p = urlParams.getAll("time_window");
	  if (p.length > 0) {
	    document.getElementById("input-time-window").value = p[0];
	  }
		p = urlParams.getAll("limits");
		if (p.length > 0) {
			document.getElementById("input-limits").value = p[0];
		}
		p = urlParams.getAll("filters");
		if (p.length > 0) {
			document.getElementById("input-filters").value = p[0];
		}
		p = urlParams.getAll("csv_separator");
		if (p.length > 0) {
			document.getElementById("input-csv-separator").value = p[0];
		}

		p = urlParams.getAll("highlight-values");
		if (p.length > 0) {
			document.getElementById("input-highlight-values").checked = ("true".localeCompare(p[0]) == 0 ? true : false);
		}
	}
};
