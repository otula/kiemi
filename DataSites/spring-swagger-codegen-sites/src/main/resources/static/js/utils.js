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
var xyutils = {
	/**
	 * From https://stackoverflow.com/questions/22521982/check-if-point-is-inside-a-polygon && https://github.com/substack/point-in-polygon
	 *
	 * @param {Array[Number, Number]} point
	 * @param {Array[Array[Number, Number]]} vs
	 */
	inside : function (point, vs) {
	 // ray-casting algorithm based on
	 // https://wrf.ecse.rpi.edu/Research/Short_Notes/pnpoly.html/pnpoly.html

		 var x = point[0], y = point[1];

		 var inside = false;
		 for (var i = 0, j = vs.length - 1; i < vs.length; j = i++) {
				 var xi = vs[i][0], yi = vs[i][1];
				 var xj = vs[j][0], yj = vs[j][1];

				 var intersect = ((yi > y) != (yj > y))
						 && (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
				 if (intersect) inside = !inside;
		 }

		 return inside;
	}
};
