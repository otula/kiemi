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
 var colorUtil = {
	 COLOR_RANDOMIZATION_ATTEMPTS : 10,
	 DEFAULT_COLORS : ["#000000","#ff0000","#00ffff","#00ff00","#ff00ff","#0000ff","#ffff00","#840000","#008484","#ff7b7b","#008400","#ff7bff","#800080","#7fff7f","#000084","#ffd700","#0028ff","#FF1493","#00eb6c","#ff7f00","#0080ff","#88540b","#77abf4","#006b3c","#3b2f2f","#80002B","#a97dfc","#614051","#febbde","#0413a2","#dcdcdc","#232323","#33003d","#ccffc2","#daa520","#255adf","#ff4f00","#00b0ff","#ff5794","#89cff0","#76300f","#dc143c","#00cc99","#39ff14","#c600eb","#ed9121","#126ede","#4b0082","#b4ff7d","#65000b","#9afff4","#006a4e","#ff95b1","#4000ff","#bfff00","#e0b0ff","#1f4f00","#ace1af","#531e50","#663399","#99cc66","#007791","#ff886e","#66023C","#99fdc3"],
	 colorIndex : 0,
	 colors : null,
	 colorList : null,

	 /**
	  * reset currently active color, after calling this function generateColor()'s colors will start from the beginning
		*/
	 resetActiveColor : function() {
		 colorUtil.colorIndex = 0;
	 },

 	/**
 	 * @return {string} new color
 	 */
 	generateColor : function() {
 		if(colorUtil.colorIndex >= colorUtil.colors.length){
 			var color = null;
 			for(let i=0;i<colorUtil.COLOR_RANDOMIZATION_ATTEMPTS;++i){
 				color = "#"+((1<<24)*Math.abs(Math.sin(colorUtil.colorIndex++))|0).toString(16);
 				if(colorUtil.colors.indexOf(color) >= 0){
 					continue;
 				}
 			}
 			for(let i=colorUtil.length;i<7;++i){
 				color += '0';
 			}
 			return color;
 		}else{
 			return colorUtil.colors[colorUtil.colorIndex++];
 		}
 	},

	/**
	 *
	 */
	initialize : function() {
		colorUtil.colorList = document.getElementById("color-bar-color-list");
		var filesToUpload = document.getElementById("color-file-upload");
		filesToUpload.ondragover = function () { $(this).addClass("hover"); return false; };
		filesToUpload.ondragleave = function () { $(this).removeClass("hover"); return false; };
		filesToUpload.ondragend = function () { $(this).removeClass("hover"); return false; };
		filesToUpload.ondrop = function (e) {
			$(this).removeClass("hover");
			e.preventDefault();
			colorUtil.readFile(e.dataTransfer.files[0]);
		};
		colorUtil.colors = colorUtil.DEFAULT_COLORS.slice();
		colorUtil.createColorBar();
	},

	/**
	 *
	 * @param {File} file
	 */
	readFile : function(file) {
		var reader = new FileReader();
  	reader.onload = function(){
			colorUtil.colors = [];
    	for(let c of this.result.split("#")){
				let color = c.substring(0,6).trim();
				if(color.length == 6){
					colorUtil.colors.push("#"+color);
				}
			}
			if(colorUtil.colors.length < 1){
				console.log("No colors in the file.");
				colorUtil.colors = colorUtil.DEFAULT_COLORS.slice();
			}else{
				colorUtil.createColorBar();
			}
  	};
  	reader.readAsText(file);
	},

	/**
	 *
	 */
	createColorBar : function() {
		colorUtil.colorList.innerHTML = "";
		for(let c of colorUtil.colors){
			let cElement = document.createElement("div");
			cElement.className = "color-bar-color";
			cElement.style.backgroundColor = c;
			colorUtil.colorList.append(cElement);
		}
	}
 };
