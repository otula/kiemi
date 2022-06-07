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
var auth = {
	LOCALSTORAGE_USERNAME : "username",
	LOCALSTORAGE_PASSWORD : "password",
	URI_LOGIN_PAGE : "/login.html",
	URI_SITES_PAGE : "/index.html",

	/**
	 * Called when login has failed
	 */
	loginFailed : function() {
		localStorage.removeItem(auth.LOCALSTORAGE_USERNAME);
		localStorage.removeItem(auth.LOCALSTORAGE_PASSWORD);
		window.location.replace(auth.URI_LOGIN_PAGE+location.search);
	},

	/**
	 * redirects to login page if credentials are not in local storage
	 *
	 @return {boolean} true if credentials were in local storage
	 */
	checkStorageCredentials : function() {
		var username = localStorage.getItem(auth.LOCALSTORAGE_USERNAME);
		if(username == null){
			console.log("authorize", "No username in localStorage.");
			auth.loginFailed();
			return false;
		}
		var password = localStorage.getItem(auth.LOCALSTORAGE_PASSWORD);
		if(password == null){
			console.log("authorize", "No password in localStorage.");
			auth.loginFailed();
			return false;
		}
		return true;
	},

	/**
	 * login user
	 */
	login : function() {
		var username = document.getElementById("login-overlay-username-input").value;
		if(username.trim().length < 1){
			console.log("Invalid username.");
			return;
		}
		var password = document.getElementById("login-overlay-password-input").value;
		if(password.trim().length < 1){
			console.log("Invalid password.");
			return;
		}

		localStorage.setItem(auth.LOCALSTORAGE_USERNAME, username);
		localStorage.setItem(auth.LOCALSTORAGE_PASSWORD, password);

		window.location.replace(auth.URI_SITES_PAGE+location.search);
	},

	/**
	 * @param {XMLHttpRequest} xhttp
	 */
	authorize : function(xhttp) {
		if(auth.checkStorageCredentials()){
				xhttp.setRequestHeader("Authorization", "Basic " + btoa(localStorage.getItem(auth.LOCALSTORAGE_USERNAME) + ":" + localStorage.getItem(auth.LOCALSTORAGE_PASSWORD)));
		}
	},
};
