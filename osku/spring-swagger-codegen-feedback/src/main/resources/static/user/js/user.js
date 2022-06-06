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
var users = {
	questionareId : null,

	/**
	 *
	 */
	initializeCreateUser : function() {
		$('input').bind('keypress',function (event){
		  if (event.keyCode === 13){
		    users.createUser();
		  }
		});
	},

	/**
	 *
	 */
	initializeLogin : function() {
		$('input').bind('keypress',function (event){
		  if (event.keyCode === 13){
		    users.login();
		  }
		});

		var uriParams = new URLSearchParams(window.location.search);
		var p = uriParams.getAll(udefinitions.URI_PARAMETER_QUESTIONARE_ID);
	  if (p.length > 0) {
	    users.questionareId = parseInt(p[0]);
	  }

		if(uriParams.has(udefinitions.URI_PARAMETER_LOGOUT) && udefinitions.VALUE_TRUE.localeCompare(uriParams.get(udefinitions.URI_PARAMETER_LOGOUT).toLowerCase()) == 0){
			console.log("Clearing auth storage.");
			users.invalidateCredentials();
		}
	},

	/**
	 *
	 */
	login : function() {
		var uElement = $("#input-username");
		var username = uElement.val().trim();
		if(username.length < 1){
			console.log("Invalid username.");
			uElement.addClass("invalid-input");
			return;
		}else{
			uElement.removeClass("invalid-input");
		}

		var pElement = $("#input-password");
		var password = pElement.val().trim();
		if(password.length < 1){
			console.log("Invalid password.");
			pElement.addClass("invalid-input");
			return;
		}else{
			pElement.removeClass("invalid-input");
		}

		sessionStorage.setItem(udefinitions.SESSIONSTORAGE_USERNAME, username);
		sessionStorage.setItem(udefinitions.SESSIONSTORAGE_PASSWORD, password);

		console.log("CALLING GET "+udefinitions.URI_QUESTIONARES);
		$.ajax({
				url : udefinitions.URI_QUESTIONARES,
				beforeSend: function(jqXHR) {
					jqXHR.setRequestHeader("Authorization", "Basic " + btoa(username+":"+password));
				},
				success : function(data){
					if(data == null || data.length < 1){
						console.log("The authenticated user has no questionares.");
						uElement.addClass("invalid-input");
					}else{
						if(users.questionareId){
							for(let q of data){
								if(q.id == users.questionareId){
									window.location.replace(udefinitions.URI_FEEDBACK+users.questionareId);
									return;
								}
							}
							console.log("User does not have permissions for the questionare, id: "+users.questionareId+" given as url parameter, loading default questionare.");
						}

						if(data.length > 1){ // TODO show questionare selection?
							console.log("The authenticated user can access more than 1 questionare, using the first one.");
						}
						window.location.replace(udefinitions.URI_FEEDBACK+data[0].id);
					}
				},
				error : function(jqXHR, textStatus, errorThrown){
					console.log("Failed to retrieve questionares, status: "+textStatus);
					uElement.addClass("invalid-input");
					pElement.addClass("invalid-input");
				}
			});
	},

	/**
	 * create user
	 */
	createUser : function() {
		var uElement = $("#input-username");
		var user = {
			username : uElement.val()
		}
		if(user.username.length < 1 || user.username.length != user.username.trim().length){
			console.log("Invalid username.");
			uElement.addClass("invalid-input");
			return;
		}else{
			uElement.removeClass("invalid-input");
		}

		var pElement = $("#input-password");
		user.password = pElement.val();
		if(user.password.length != user.password.trim().length){
			console.log("Invalid password.");
			pElement.addClass("invalid-input");
			return;
		}else if(user.password.length < udefinitions.MIN_PASSWORD_LENGTH){
			console.log("Password was too short.");
			users.showStatusMessage("Salasanan tulee olla vähintään "+udefinitions.MIN_PASSWORD_LENGTH+" merkkiä pitkä.");
			pElement.addClass("invalid-input");
			return;
		}else if(user.password.localeCompare($("#input-password").val()) != 0){
			console.log("Passwords do not match.");
			users.showStatusMessage("Annetut salasanat eivät täsmää.");
			pElement.addClass("invalid-input");
			return;
		}else{
			pElement.removeClass("invalid-input");
		}

		var token = new URLSearchParams(window.location.search).get(udefinitions.URI_PARAMETER_TOKEN);
		if(token == null || token.length < 1){
			console.log("Invalid or missing token.");
			return;
		}

		var uri = udefinitions.URI_USERS+"?"+udefinitions.URI_PARAMETER_TOKEN+"="+token;

		console.log("CALLING POST "+uri);

		$.ajax({
	    type: 'POST',
	    url: uri,
	    data: JSON.stringify(user),
	    contentType: "application/json",
	    dataType: 'json',
			success : function(){
				users.showStatusMessage(udefinitions.USER_CREATED_OK_LINK);
			},
			error : function(jqXHR, textStatus, errorThrown){
				console.log("Failed to create new user. Server responded: "+textStatus);
				if(jqXHR.status == 400){
					users.showStatusMessage(udefinitions.USER_CREATED_FAILED_400);
				}else{
					users.showStatusMessage(udefinitions.USER_CREATED_FAILED);
				}
			}
		});
	},

	/**
	 * @param {string} message
	 */
	showStatusMessage : function(message) {
		var statusView = $("#view-status");
		statusView.html(message);
		statusView.removeClass("hidden");
	},

	/**
	 * invalidate basic auth cache
	 */
	invalidateCredentials : function() {
		sessionStorage.removeItem(udefinitions.SESSIONSTORAGE_USERNAME);
		sessionStorage.removeItem(udefinitions.SESSIONSTORAGE_PASSWORD);

		console.log("CALLING GET "+udefinitions.URI_QUESTIONARES);
		$.ajax({
				url : udefinitions.URI_QUESTIONARES,
				beforeSend: function(jqXHR) {
					jqXHR.setRequestHeader("Authorization", "Basic " + btoa(" : "));
				},
				success : function(){
					console.log("Success?"); // this should not happen
				},
				error : function(jqXHR, textStatus, errorThrown){
					console.log("Invalidate credentials, server responded: "+textStatus);
				}
			});
	}
};
