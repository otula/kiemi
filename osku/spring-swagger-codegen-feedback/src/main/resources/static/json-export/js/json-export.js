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
var jsonexport = {
  inputQuestionareId: null,
  inputSplitArrays: null,
  inputClearIds: null,
  viewQuestionare: null,
	viewQuestionareUsers: null,
  viewQuestionareQuestions: null,
  viewQuestionareTimeSelections: null,
  viewLocation: null,
  viewLocationAreas: null,
	viewSettings: null,
	questionareId : null,

  /**
   *
   */
  initialize: function() {
    console.log("Initializing...");
    jsonexport.inputQuestionareId = document.getElementById("input-questionare-id");
    jsonexport.inputSplitArrays = document.getElementById("input-split-arrays");
    jsonexport.inputClearIds = document.getElementById("input-clear-ids");
    jsonexport.viewQuestionare = $("#view-questionare");
    jsonexport.viewQuestionareUsers = $("#view-questionare-users");
    jsonexport.viewQuestionareQuestions = $("#view-questionare-questions");
    jsonexport.viewQuestionareTimeSelections = $("#view-questionare-timeSelections");
    jsonexport.viewLocation = $("#view-location");
    jsonexport.viewLocationAreas = $("#view-location-areas");
		jsonexport.viewSettings = $("#view-settings");
  },

	/**
	 *
	 */
	retrieveJSON : function() {
		jsonexport.questionareId = jsonexport.inputQuestionareId.value;
		if(jsonexport.questionareId.length < 1){
			console.log("Invalid questionare id.");
			jsonexport.handleError();
			return;
		}

		jsonexport.viewQuestionare.empty(),
		jsonexport.viewQuestionareUsers.empty(),
	  jsonexport.viewQuestionareQuestions.empty(),
	  jsonexport.viewQuestionareTimeSelections.empty(),
	  jsonexport.viewLocation.empty(),
	  jsonexport.viewLocationAreas.empty(),
		jsonexport.viewSettings.removeClass("invalid-settings");
		feedbackapi.getQuestionare(jsonexport.questionareId, jsonexport.questionareLoaded);
	},

	/**
	 *
	 */
	handleError : function () {
		jsonexport.viewSettings.addClass("invalid-settings");
	},

  /**
   * @param {object} questionare
   */
  questionareLoaded: function(questionare) {
    if (questionare == null) {
      console.log("Failed to load questionare.");
      jsonexport.handleError();
      return;
    }

    console.log("Questionare loaded.");
    jsonexport.printJSON(jsonexport.viewQuestionare,questionare);

    if(!questionare.useServiceUsers){
			feedbackapi.getUsers(jsonexport.questionareId, jsonexport.usersLoaded);
		}

    if (questionare.locationId) {
      feedbackapi.getLocation(questionare.locationId, jsonexport.locationLoaded);
    }

    feedbackapi.getQuestions(jsonexport.questionareId, jsonexport.questionsLoaded);
		feedbackapi.getTimeSelections(jsonexport.questionareId, jsonexport.timeSelectionsLoaded);
  },

  /**
   * @param {object} location
   */
  locationLoaded: function(location) {
    if (location == null) {
      console.log("Failed to load location.");
      jsonexport.handleError();
      return;
    }

    console.log("Location loaded.");

    feedbackapi.getAreas(location.id, jsonexport.areasLoaded);

    jsonexport.printJSON(jsonexport.viewLocation,location);
  },

  /**
   * @param {object} questions
   */
  questionsLoaded: function(questions) {
    if (questions == null || questions.length < 1) {
      console.log("Failed to load questions.");
      jsonexport.handleError();
      return;
    }

    console.log("Questions loaded.");
    jsonexport.printJSON(jsonexport.viewQuestionareQuestions,questions);
  },

  /**
   * @param {object} areas
   */
  areasLoaded: function(areas) {
    if (areas == null || areas.length < 1) {
      console.log("Failed to load areas.");
      jsonexport.handleError();
      return;
    }

    console.log("Areas loaded.");
    jsonexport.printJSON(jsonexport.viewLocationAreas,areas);
  },

  /**
   * @param {object} timeSelections
   */
  timeSelectionsLoaded: function(timeSelections) {
    if (timeSelections == null || timeSelections.length < 1) {
      console.log("No time selections.");
    } else {
			console.log("Time selections loaded.");
      jsonexport.printJSON(jsonexport.viewQuestionareTimeSelections,timeSelections);
		}
  },

  /**
   * @param {object} users
   */
  usersLoaded: function(users) {
    if (users == null) {
      console.log("Failed to load users.");
      jsonexport.handleError();
			return;
    }

		if (users.length < 1) {
      console.log("No users.");
    } else {
			console.log("Users loaded.")
      jsonexport.printJSON(jsonexport.viewQuestionareUsers,users);
    }
  },

  /**
   * @param {DomElement} targetElement
   * @param {object} object
   */
  printJSON : function(targetElement, object){
    if(jsonexport.inputClearIds.checked){
      jsonexport.removeIds(object);
    }

    if(jsonexport.inputSplitArrays.checked && Array.isArray(object)){
      var content = "";
      for(let i=0;i<object.length;++i){
        content += JSON.stringify(object[i], null, 2) + "\n\n";
      }
      targetElement.text(content);
    }else{
      targetElement.text(JSON.stringify(object, null, 2));
    }
  },

  /**
   * @param {object} object
   */
  removeIds : function (object) {
    delete object.id;
    for (var k in object) {
        if (typeof object[k] == "object" && object[k] !== null){
            jsonexport.removeIds(object[k]);
        }
    }
  }
};
