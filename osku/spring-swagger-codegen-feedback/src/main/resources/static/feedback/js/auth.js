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
var auth = {
  SESSIONSTORAGE_USERNAME: "username",
  SESSIONSTORAGE_PASSWORD: "password",

  /**
   * Called when login has failed
   */
  loginFailed: function() {
    var uri = udefinitions.URI_LOGIN_PAGE + "?" + udefinitions.URI_PARAMETER_LOGOUT + "=true";

    if (feedbackui.questionareId) {
      uri += "&" + udefinitions.URI_PARAMETER_QUESTIONARE_ID + "=" + feedbackui.questionareId;
    }

    window.location.replace(uri);
  },

  /**
   * @param {jqXHR} jqXHR
   */
  authorize: function(jqXHR) {
    var username = sessionStorage.getItem(udefinitions.SESSIONSTORAGE_USERNAME);
    if (username == null) {
      console.log("authorize", "No username in sessionStorage.");
      auth.loginFailed();
      return;
    }
    var password = sessionStorage.getItem(udefinitions.SESSIONSTORAGE_PASSWORD);
    if (password == null) {
      console.log("authorize", "No password in sessionStorage.");
      auth.loginFailed();
      return;
    }
    jqXHR.setRequestHeader("Authorization", "Basic " + btoa(username + ":" + password));
  }
};