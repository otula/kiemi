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
var feedbackapi = {

	/**
	 * @param {String} questionareId
	 * @param {callback function} callback
	 */
	getQuestionare : function(questionareId, callback) {
		var uri = definitions.URI_BASE+definitions.URI_QUESTIONARES+"/"+questionareId;
		console.log("CALLING GET "+uri);
		$.ajax({
				url : uri,
				beforeSend: function(jqXHR) {
					auth.authorize(jqXHR);
				},
				success : function(data){
					if(data.length < 1){
						callback(null);
					}else{
						callback(data[0]);
					}
				},
				error : function(jqXHR, textStatus, errorThrown){
					feedbackapi.handleError(jqXHR, textStatus, errorThrown, callback);
				}
			});
	},

	/**
	 * @param {jqXHR} jqXHR
	 * @param {string} textStatus
	 * @param {error object} errorThrown
	 * @param {callback function} callback
	 */
	handleError : function(jqXHR, textStatus, errorThrown, callback) {
		console.log("Failed with status "+jqXHR.status+" "+textStatus);
		if(jqXHR.status == 401 || jqXHR.status == 403){
			console.log("Authorization failed.");
			auth.loginFailed();
		}else{
			callback(null);
		}
	},

	/**
	 * @param {String} questionareId
	 * @param {callback function} callback
	 */
	getUsers : function(questionareId, callback) {
		var uri = definitions.URI_BASE+definitions.URI_QUESTIONARES+"/"+questionareId+definitions.URI_USERS;
		console.log("CALLING GET "+uri);
		$.ajax({
				url : uri,
				beforeSend: function(jqXHR) {
					auth.authorize(jqXHR);
				},
				success : function(data){
					if(data.length < 1){
						callback(null);
					}else{
						callback(data);
					}
				},
				error : function(jqXHR, textStatus, errorThrown){
					feedbackapi.handleError(jqXHR, textStatus, errorThrown, callback);
				}
			});
	},

	/**
	 * @param {callback function} callback
	 */
	getCurrentUser : function(callback) {
		var uri = definitions.URI_BASE+definitions.URI_USERS;
		console.log("CALLING GET "+uri);
		$.ajax({
				url : uri,
				beforeSend: function(jqXHR) {
					auth.authorize(jqXHR);
				},
				success : function(data){
					if(data.length < 1){
						callback(null);
					}else{
						callback(data[0]);
					}
				},
				error : function(jqXHR, textStatus, errorThrown){
					feedbackapi.handleError(jqXHR, textStatus, errorThrown, callback);
				}
			});
	},

	/**
	 * @param {String} questionareId
	 * @param {object} user
	 * @param {callback function} callback
	 */
	postUser : function(questionareId, user, callback) {
		var uri = definitions.URI_BASE+definitions.URI_QUESTIONARES+"/"+questionareId+definitions.URI_USERS;
		console.log("CALLING POST "+uri);
		$.ajax({
				url : uri,
				type : "POST",
				contentType : "application/json",
				processData : false,
				data : JSON.stringify(user),
				beforeSend: function(jqXHR) {
					auth.authorize(jqXHR);
				},
				success : function(data){
					if(data.length < 1){
						callback(null);
					}else{
						callback(data[0]);
					}
				},
				error : function(jqXHR, textStatus, errorThrown){
					feedbackapi.handleError(jqXHR, textStatus, errorThrown, callback);
				}
			});
	},

	/**
	 * @param {String} locationId
	 * @param {callback function} callback
	 */
	getLocation : function(locationId, callback) {
		var uri = definitions.URI_BASE+definitions.URI_LOCATIONS+"/"+locationId;
		console.log("CALLING GET "+uri);
		$.ajax({
				url : uri,
				beforeSend: function(jqXHR) {
					auth.authorize(jqXHR);
				},
				success : function(data){
					if(data.length < 1){
						callback(null);
					}else{
						callback(data[0]);
					}
				},
				error : function(jqXHR, textStatus, errorThrown){
					feedbackapi.handleError(jqXHR, textStatus, errorThrown, callback);
				}
			});
	},

	/**
	 * @param {String} locationId
	 * @param {callback function} callback
	 */
	getAreas : function(locationId, callback) {
		var uri = definitions.URI_BASE+definitions.URI_LOCATIONS+"/"+locationId+definitions.URI_AREAS;
		console.log("CALLING GET "+uri);
		$.ajax({
				url : uri,
				beforeSend: function(jqXHR) {
					auth.authorize(jqXHR);
				},
				success : function(data){
					if(data.length < 1){
						callback(null);
					}else{
						callback(data);
					}
				},
				error : function(jqXHR, textStatus, errorThrown){
					feedbackapi.handleError(jqXHR, textStatus, errorThrown, callback);
				}
			});
	},

	/**
	 * @param {String} questionareId
	 * @param {callback function} callback
	 */
	getQuestions : function(questionareId, callback) {
		var uri = definitions.URI_BASE+definitions.URI_QUESTIONARES+"/"+questionareId+definitions.URI_QUESTIONS;
		console.log("CALLING GET "+uri);
		$.ajax({
				url : uri,
				beforeSend: function(jqXHR) {
					auth.authorize(jqXHR);
				},
				success : function(data){
					if(data.length < 1){
						callback(null);
					}else{
						callback(data);
					}
				},
				error : function(jqXHR, textStatus, errorThrown){
					feedbackapi.handleError(jqXHR, textStatus, errorThrown, callback);
				}
			});
	},

	/**
	 * @param {String} questionareId
	 * @param {callback function} callback
	 */
	getTimeSelections : function(questionareId, callback) {
		var uri = definitions.URI_BASE+definitions.URI_QUESTIONARES+"/"+questionareId+definitions.URI_TIME_SELECTIONS;
		console.log("CALLING GET "+uri);
		$.ajax({
				url : uri,
				beforeSend: function(jqXHR) {
					auth.authorize(jqXHR);
				},
				success : function(data){
					if(data.length < 1){
						callback(null);
					}else{
						callback(data);
					}
				},
				error : function(jqXHR, textStatus, errorThrown){
					feedbackapi.handleError(jqXHR, textStatus, errorThrown, callback);
				}
			});
	},

	/**
	 * @param {String} questionareId
	 * @param {object} answer
	 * @param {callback function} callback
	 */
	postAnswer : function(questionareId, answer, callback) {
		var uri = definitions.URI_BASE+definitions.URI_QUESTIONARES+"/"+questionareId+definitions.URI_ANSWERS;
		console.log("CALLING POST "+uri);
		$.ajax({
				url : uri,
				type : "POST",
				contentType : "application/json",
				processData : false,
				data : JSON.stringify(answer),
				beforeSend: function(jqXHR) {
					auth.authorize(jqXHR);
				},
				success : function(data){
					if(data.length < 1){
						callback(null);
					}else{
						callback(data[0]);
					}
				},
				error : function(jqXHR, textStatus, errorThrown){
					feedbackapi.handleError(jqXHR, textStatus, errorThrown, callback);
				}
			});
	}
};
