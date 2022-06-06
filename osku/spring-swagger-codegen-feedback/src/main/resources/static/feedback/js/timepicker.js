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

 var timepicker = {
	startPickerTime : null,
	startPickerDay : null,
	startPickerMonth : null,
	startInputYear : null,
	endPickerTime : null,
	endPickerDay : null,
	endPickerMonth : null,
	endInputYear : null,

	/**
	 *
	 */
	initialize : function() {
		timepicker.startPickerTime = document.getElementById("main-view-timeselection-picker-start-time");
		timepicker.createTimeOptions(timepicker.startPickerTime);
		timepicker.startPickerTime.addEventListener('change', (event) => {
			feedbackui.restartIdleTimer();
			feedbackui.checkCanProceed();
		});
		timepicker.startPickerDay = document.getElementById("main-view-timeselection-picker-start-day");
		timepicker.startPickerDay.addEventListener('change', (event) => {
			feedbackui.restartIdleTimer();
			feedbackui.checkCanProceed();
		});
		timepicker.startPickerMonth = document.getElementById("main-view-timeselection-picker-start-month");
		timepicker.startPickerMonth.addEventListener('change', (event) => {
			feedbackui.restartIdleTimer();
			feedbackui.checkCanProceed();
		});
		timepicker.startInputYear = document.getElementById("main-view-timeselection-picker-start-year");
		timepicker.startInputYear.addEventListener('change', (event) => {
			feedbackui.restartIdleTimer();
			feedbackui.checkCanProceed();
		});

		timepicker.endPickerTime = document.getElementById("main-view-timeselection-picker-end-time");
		timepicker.createTimeOptions(timepicker.endPickerTime);
		timepicker.endPickerTime.addEventListener('change', (event) => {
			feedbackui.restartIdleTimer();
			feedbackui.checkCanProceed();
		});
		timepicker.endPickerDay = document.getElementById("main-view-timeselection-picker-end-day");
		timepicker.endPickerDay.addEventListener('change', (event) => {
			feedbackui.restartIdleTimer();
			feedbackui.checkCanProceed();
		});
		timepicker.endPickerMonth = document.getElementById("main-view-timeselection-picker-end-month");
		timepicker.endPickerMonth.addEventListener('change', (event) => {
			feedbackui.restartIdleTimer();
			feedbackui.checkCanProceed();
		});
		timepicker.endInputYear = document.getElementById("main-view-timeselection-picker-end-year");
		timepicker.endInputYear.addEventListener('change', (event) => {
			feedbackui.restartIdleTimer();
			feedbackui.checkCanProceed();
		});

		timepicker.reset();
	},

	/**
	 * @return {boolean} true if the time picker has valid time selected
	 */
	isValid : function() {
		var start = timepicker.getStart();
		if(start == null){
			console.log("Invalid start time.");
			return false;
		}
		var end = timepicker.getEnd();
		if(end == null){
			console.log("Invalid end time.");
			return false;
		}
		return (start.getTime() <= end.getTime());
	},

	/**
	 * @return {Date} start time or null if invalid time is set
	 */
	getStart : function() {
		var year = parseInt(timepicker.startInputYear.value);
		if(isNaN(year)){
			console.log("Invalid start year.");
			return null;
		}
		var month = parseInt(timepicker.startPickerMonth.value);
		var time = timepicker.startPickerTime.value.split(":");
		var start = new Date(year, month, parseInt(timepicker.startPickerDay.value), parseInt(time[0]), parseInt(time[1]));
		if(start.getMonth() != month){ // javascript date advances to next month if day is > max days of month
			console.log("Month changed: invalid day?");
			return null;
		}else{
			return start;
		}
	},

	/**
	 * @return {Date} end time or null if invalid time is set
	 */
	getEnd : function(){
		var year = parseInt(timepicker.endInputYear.value);
		if(isNaN(year)){
			console.log("Invalid end year.");
			return null;
		}
		var month = parseInt(timepicker.endPickerMonth.value);
		var time = timepicker.endPickerTime.value.split(":");
		var end = new Date(year, month, parseInt(timepicker.endPickerDay.value), parseInt(time[0]), parseInt(time[1]));
		if(end.getMonth() != month){ // javascript date advances to next month if day is > max days of month
			console.log("Month changed: invalid day?");
			return null;
		}else{
			return end;
		}
	},

	/**
	 * @param {HtmlNode} select
	 */
	createTimeOptions : function(select){
		for(let i=0;i<24;++i){
			let even = document.createElement("option");
			even.value = (i<10?"0":"")+i+":00";
			even.appendChild(document.createTextNode(even.value));
			select.appendChild(even);
			half = document.createElement("option");
			half.value = (i<10?"0":"")+i+":30";
			half.appendChild(document.createTextNode(half.value));
			select.appendChild(half);
		} // for
	},

	/**
	 * reset time picker to current local time
	 */
	reset : function() {
		if(timepicker.startPickerTime == null){
			console.log("Timepicker not initialized: not reseting.")
			return;
		}

		var now = new Date();

		var year = now.getFullYear();
		timepicker.startInputYear.value = year.toString();
		timepicker.startInputYear.min = (year-1).toString();
		timepicker.startInputYear.max = (year+1).toString();
		timepicker.endInputYear.value = timepicker.startInputYear.value;
		timepicker.endInputYear.min = timepicker.startInputYear.min;
		timepicker.endInputYear.max = timepicker.startInputYear.max;

		timepicker.startPickerMonth.selectedIndex = now.getMonth();
		timepicker.endPickerMonth.selectedIndex = timepicker.startPickerMonth.selectedIndex;

		timepicker.startPickerDay.selectedIndex = now.getDate()-1;
		timepicker.endPickerDay.selectedIndex = timepicker.startPickerDay.selectedIndex;

		var hour = now.getHours();
		var time = (hour < 10 ? "0" : "")+hour;
		var minute = now.getMinutes();
		if(minute < 30){ // set to previous even hour
			time+=":00";
		}else{ // set to previous half hour
			time+=":30";
		}

		var index = 0;
		for(let option=timepicker.startPickerTime.firstChild; option!==null; option=option.nextSibling) {
			if(option.nodeType != Node.ELEMENT_NODE){
				continue;
			}else	if(option.value.localeCompare(time) == 0){
				timepicker.currentStartTime = time;
				timepicker.currentEndTime = time;
				timepicker.startPickerTime.selectedIndex = index;
				timepicker.endPickerTime.selectedIndex = index;
				break;
			}else{
				++index;
			}
		}
	}
};
