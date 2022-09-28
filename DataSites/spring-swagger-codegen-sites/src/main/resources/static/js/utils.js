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
var utilFunctions = {
	/**
	 * From https://stackoverflow.com/questions/22521982/check-if-point-is-inside-a-polygon && https://github.com/substack/point-in-polygon
	 *
	 * @param {Array[Number, Number]} point
	 * @param {Array[Array[Number, Number]]} vs
	 * @return {Boolean} true if point was inside
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
	},

	/**
	 * Calculate distance between two coordinates in kilometers
	 *
	 * @param {Number} lat1
	 * @param {Number} lon1
	 * @param {Number} lat2
	 * @param {Number} lon2
	 * @return {Number} distance in kilometers
	 */
	calculateDistance : function(lat1, lon1, lat2, lon2) {
		// based on: https://stackoverflow.com/questions/18883601/function-to-calculate-distance-between-two-coordinates
		let R = 6371; // Radius of the earth in km
		let dLat = utilFunctions.deg2rad(lat1-lat2);  // deg2rad below
		let dLon = utilFunctions.deg2rad(lon1-lon2);
		let a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(utilFunctions.deg2rad(lat1)) * Math.cos(utilFunctions.deg2rad(lat2)) * Math.sin(dLon/2) * Math.sin(dLon/2);
		let c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		let d = R * c;
		return d;
	},

	/**
	 * Convert degrees to radians
	 * @param {Number} deg
	 * @return {Number} degrees in radians
	 */
	deg2rad : function(deg) {
		return deg * (Math.PI/180)
	}
};
