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
var feedbackui = {
  PAGE_USERS: 1,
  PAGE_AREA_SELECTION: 2,
  PAGE_QUESTIONS: 3,
  PAGE_TIME_SELECTION: 4,
  PAGE_ADD_USER: 5,
  PAGE_THANK_YOU: 6,
  AREA_ID_PREFIX: "area-id-",
  USER_ID_PREFIX: "user-id-",
  QUESTION_ID_PREFIX: "question-id-",
  TIME_SELECTION_ID_PREFIX: "time-selection-id-",
  TIME_REOCCURRING_SELECTION_ID_PREFIX: "time-reoccurring-selection-id-",
  DATA_KEY_AREA_ID: "areaId",
  DATA_KEY_START: "start",
  DATA_KEY_END: "end",
  DATA_KEY_TIME_SELECTION_ID: "timeSelectionId",
  DATA_KEY_QUESTION_ID: "questionId",
  DATA_KEY_QUESTION_VALUE: "questionValue",
  DATA_KEY_DEFAULT_VALUE: "defaultValue",
  DATA_KEY_IMAGE_URL: "imageUrl",
  DATA_KEY_NAME: "name",
  DATA_KEY_TEXT: "text",
  currentPage: null,
  activeAnswer: null,
  areaImageUrl: null,
  areas: null,
  ctx: null,
  locationImage: null,
  elementActiveUsername: null,
  elementMainViewUsers: null,
  elementMainViewUsersUserList: null,
  elementMainViewAreaSelection: null,
  elementMainViewAreaSelectionImage: null,
  elementMainViewAreaSelectionList: null,
  elementMainViewAreaSelectionCanvas: null,
  elementMainViewQuestions: null,
  elementMainViewTimeSelections: null,
  elementMainViewTimeSelectionsList: null,
  elementMainViewAddUser: null,
  elementMainViewAddUserInput: null,
  elementMainViewMultiUser: null,
  elementMainViewSingleUser: null,
  elementMainViewThankYou: null,
  elementSideViewUserSelection: null,
  elementSideViewAreaSelectionList: null,
  elementSideViewQuestionSelectionList: null,
  elementSideViewTimeSelectionList: null,
  elementSideViewNextPageButton: null,
  elementSideViewNarrowText: null,
  elementSideViewNarrowPage: null,
  elementErrorOverlay: null,
  elementWaitOverlay: null,
  elementSideView: null,
  locationAvailable: false,
  timerInputWait: null, // page idle timer
  timerThankYouWait: null, // thank you page timer
  usernameCache: [],
  progressBarUser: null,
  progressBarArea: null,
  progressBarQuestion: null,
  progressBarTimeSelection: null,
  reoccurring: false,
  questionareId: null,
  questionsMaxSelections: null, // map of quesuonId => {maxSelections;activeSelections}
  usersAvailable: true,
  timeSelectionsAvailable: true, // use default time selection picker

  /**
   *
   */
  stopIdleTimer: function() {
    if (feedbackui.timerInputWait != null) {
      window.clearTimeout(feedbackui.timerInputWait);
      feedbackui.timerInputWait = null;
    }
  },

  /**
   *
   */
  startIdleTimer: function() {
    feedbackui.stopThankYouTimer();
    feedbackui.timerInputWait = window.setTimeout(feedbackui.idleTimerTriggered, definitions.INPUT_WAIT);
  },

  /**
   *
   */
  idleTimerTriggered: function() {
    feedbackui.loadPageUsers();
  },

  /**
   *
   */
  restartIdleTimer: function() {
    feedbackui.stopIdleTimer();
    feedbackui.startIdleTimer();
  },

  /**
   *
   */
  startThankYouTimer: function() {
    feedbackui.stopIdleTimer();
    feedbackui.timerThankYouWait = window.setTimeout(feedbackui.thankYouTimerTriggered, definitions.THANK_YOU_WAIT_DELAY);
  },

  /**
   *
   */
  stopThankYouTimer: function() {
    if (feedbackui.timerThankYouWait != null) {
      window.clearTimeout(feedbackui.timerThankYouWait);
      feedbackui.timerThankYouWait = null;
    }
  },

  /**
   *
   */
  thankYouTimerTriggered: function() {
    feedbackui.loadPageUsers();
  },

  /**
   *
   */
  handleError: function() {
    console.log("Unhandeled error. Reseting...");
    feedbackui.stopIdleTimer();
    feedbackui.showWaitMessage(false); // close any wait dialogs
    /* nothing much do except show error message, wait a while and then try again. */
    feedbackui.elementErrorOverlay.html("<p class='overlay-text'>Jotain meni pieleen. Käynnistetään uudestaan " + (definitions.ERROR_WAIT_DELAY / 1000) + " sekunnin kuluttua...</p>");
    feedbackui.elementErrorOverlay.removeClass("hidden");

    window.setTimeout(window.location.reload.bind(window.location), definitions.ERROR_WAIT_DELAY);
  },

  /**
   *
   */
  initialize: function() {
    console.log("Initializing...");
    feedbackui.elementWaitOverlay = $("#wait-overlay");
    feedbackui.showWaitMessage(true, "Ladataan...");
    feedbackui.elementActiveUsername = $("#main-view-users-single-username");
    feedbackui.elementMainViewUsers = $("#main-view-users");
    feedbackui.elementMainViewUsersUserList = $("#main-view-users-userlist");
    feedbackui.elementMainViewAreaSelection = $("#main-view-areaselection");
    feedbackui.elementMainViewAreaSelectionImage = $("#main-view-areaselection-image");
    feedbackui.elementMainViewAreaSelectionList = $("#main-view-areaselection-list");
    feedbackui.elementMainViewQuestions = $("#main-view-questions");
    feedbackui.elementMainViewQuestionsList = $("#main-view-questions-list");
    feedbackui.elementMainViewTimeSelections = $("#main-view-timeselection");
    feedbackui.elementMainViewTimeSelectionsList = $("#main-view-timeselection-list");
    feedbackui.elementMainViewMultiUser = $("#main-view-users-multi");
    feedbackui.elementMainViewSingleUser = $("#main-view-users-single");
    feedbackui.elementMainViewAddUser = $("#main-view-adduser");
    feedbackui.elementMainViewAddUserInput = $("#main-view-adduser-input");
    feedbackui.elementMainViewThankYou = $("#main-view-thank-you");
    feedbackui.elementErrorOverlay = $("#error-overlay");
    feedbackui.elementSideView = $("#side-view");
    feedbackui.elementSideViewUserSelection = $("#side-view-user-selection");
    feedbackui.elementSideViewAreaSelectionList = $("#side-view-area-selection-list");
    feedbackui.elementSideViewQuestionSelectionList = $("#side-view-question-selection-list");
    feedbackui.elementSideViewTimeSelectionList = $("#side-view-time-selection-list");
    feedbackui.elementSideViewNextPageButton = $("#side-view-next-button");
    feedbackui.elementMainViewAreaSelectionCanvas = document.getElementById("main-view-areaselection-canvas");
    feedbackui.progressBarUser = $("#progress-bar-user-wide,#progress-bar-user-narrow");
    feedbackui.progressBarArea = $("#progress-bar-area-wide,#progress-bar-area-narrow");
    feedbackui.progressBarQuestion = $("#progress-bar-question-wide,#progress-bar-question-narrow");
    feedbackui.progressBarTimeSelection = $("#progress-bar-time-selection-wide,#progress-bar-time-selection-narrow");
    feedbackui.elementSideViewNarrowText = $("#side-view-user-narrow-text");
    feedbackui.elementSideViewNarrowPage = $("#side-view-user-narrow-page");

    let uriParams = new URLSearchParams(window.location.search);
    if (uriParams.has(definitions.URI_PARAMETER_QUESTIONARE_ID)) {
      feedbackui.questionareId = uriParams.get(definitions.URI_PARAMETER_QUESTIONARE_ID);
    }
    if (feedbackui.questionareId == null) {
      auth.loginFailed();
    }

    feedbackapi.getQuestionare(feedbackui.questionareId, feedbackui.questionareLoaded);
  },

  /**
   * @param {boolean} show if true message is shown, if false, wait overlay will be hidden
   * @param {message}
   */
  showWaitMessage: function(show, message) {
    if (show) {
      feedbackui.elementWaitOverlay.empty();
      var messageElement = document.createElement("p");
      messageElement.className = "overlay-text";
      messageElement.appendChild(document.createTextNode(message));
      feedbackui.elementWaitOverlay.append(messageElement);
      feedbackui.elementWaitOverlay.removeClass("hidden");
    } else {
      feedbackui.elementWaitOverlay.addClass("hidden");
    }
  },

  /**
   * @param {object} questionare
   */
  questionareLoaded: function(questionare) {
    if (questionare == null) {
      console.log("Failed to load questionare.");
      feedbackui.handleError();
      return;
    }

    console.log("Questionare loaded.");

    feedbackui.usersAvailable = !questionare.useServiceUsers;
    if (questionare.description) {
      $(".main-view-users-welcome-text").html(questionare.description);
    }

    if (questionare.locationId) {
      feedbackapi.getLocation(questionare.locationId, feedbackui.locationLoaded);
    } else {
      console.log("No location id for questionare, id: " + feedbackui.questionareId);
      $("#side-view-area,#progress-bar-area-narrow").addClass("hidden"); // remove from side view

      $(".side-view-page-max").html(3); // area page is not available, decrease all max page counter by 1 (default max page is 4)
      $(".side-view-page-current").each(function() { // move all page numbers towards beginning
        let pageNumber = Number(this.innerHTML);
        if (pageNumber > 1) { // skip first page
          this.innerHTML = pageNumber - 1;
        }
      });

      feedbackui.locationAvailable = false;
      feedbackapi.getQuestions(feedbackui.questionareId, feedbackui.questionsLoaded);
    }
  },

  /**
   * @param {object} location
   */
  locationLoaded: function(location) {
    if (location == null) {
      console.log("Failed to load location.");
      feedbackui.handleError();
      return;
    }

    console.log("Location loaded.");
    feedbackui.areaImageUrl = location.areaImageUrl;
    if (feedbackui.areaImageUrl) {
      console.log("Area image is available for this feedback.");
      feedbackui.elementMainViewAreaSelectionImage.removeClass("hidden");
      feedbackui.elementMainViewAreaSelectionList.addClass("hidden");
    } else {
      console.log("No area image available for this feedback.");
      feedbackui.elementMainViewAreaSelectionList.removeClass("hidden");
      feedbackui.elementMainViewAreaSelectionImage.addClass("hidden");
    }
    feedbackapi.getAreas(location.id, feedbackui.areasLoaded);
  },

  /**
   * @param {object} questions
   */
  questionsLoaded: function(questions) {
    if (questions == null || questions.length < 1) {
      console.log("Failed to load questions.");
      feedbackui.handleError();
      return;
    }

    console.log("Questions loaded.");
    feedbackui.elementMainViewQuestionsList.empty();
    feedbackui.questionsMaxSelections = new Map();

    utils.bubbleSortIntAsc(questions, "indexNro");
    for (let i = 0; i < questions.length; ++i) {
      let q = questions[i];
      let div = document.createElement("div");
      div.className = "question";
      if (q.text) {
        let text = document.createElement("div");
        text.className = "question-text";
        text.appendChild(document.createTextNode(q.text));
        div.appendChild(text);
      }

      feedbackui.questionsMaxSelections.set(q.id, { maxSelections: q.maxSelections, activeSelections: 0 });

      utils.bubbleSortIntAsc(q.value, "value");
      for (let j = 0; j < q.value.length; ++j) {
        let v = q.value[j];
        let qoption = document.createElement("div");
        let bq = $(qoption);
        qoption.className = "question-value";
        qoption.classList.add("right_side_button");
        if (v.imageUrl) {
          let img = document.createElement("img");
          img.src = v.imageUrl;
          img.className = "question-value-image";
          bq.append(img);
          bq.data(feedbackui.DATA_KEY_IMAGE_URL, v.imageUrl);
        }
        if (v.text) {
          let text = document.createElement('p');
          text.classList.add('question-option-text');
          text.innerHTML = v.text;
          qoption.appendChild(text);
          bq.data(feedbackui.DATA_KEY_TEXT, v.text);
        }
        if (v.colorHint) {
          qoption.style.backgroundColor = v.colorHint;
        }

        bq.data(feedbackui.DATA_KEY_QUESTION_ID, q.id);
        bq.data(feedbackui.DATA_KEY_QUESTION_VALUE, v.value);
        if (q.hasOwnProperty("defaultValue") && q.defaultValue == v.value) {
          bq.data(feedbackui.DATA_KEY_DEFAULT_VALUE, true);
        }

        qoption.onclick = function(event) {
          feedbackui.restartIdleTimer();
          let tv = $(this);
          let questionId = tv.data(feedbackui.DATA_KEY_QUESTION_ID);
          let value = tv.data(feedbackui.DATA_KEY_QUESTION_VALUE);
          let qms = feedbackui.questionsMaxSelections.get(questionId);
          if (tv.hasClass('question-value-selected')) { //   Is this option already chosen? If so, remove selected class
            feedbackui.elementSideViewQuestionSelectionList.find("#" + feedbackui.QUESTION_ID_PREFIX + questionId + "-" + value).remove();
            tv.removeClass('question-value-selected');
            for (let k = feedbackui.activeAnswer.questionAnswer.length - 1; k >= 0; --k) {
              let qa = feedbackui.activeAnswer.questionAnswer[k];
              if (qa.questionId == questionId && qa.value == value) {
                feedbackui.activeAnswer.questionAnswer.splice(k, 1);
                break;
              }
            }
            qms.activeSelections -= 1;
          } else if (qms.activeSelections == qms.maxSelections) {
            console.log("Maximum amount of values for question, id: " + questionId + ", has been selected.");
          } else { // new selection, add to selected ones
            tv.addClass('question-value-selected');
            let div = document.createElement("div");
            div.className = "side-view-question-selection";
            div.id = feedbackui.QUESTION_ID_PREFIX + questionId + "-" + value;
            div.appendChild(feedbackui.createSelectedIconImageElement());
            let temp = tv.data(feedbackui.DATA_KEY_TEXT); // TODO: use feedbackui.DATA_KEY_IMAGE_URL if exists
            if (temp) {
              div.appendChild(document.createTextNode(temp));
            }
            feedbackui.elementSideViewQuestionSelectionList.append(div); // add to side-view
            let qa = {
              questionId: questionId,
              value: value
            }
            feedbackui.activeAnswer.questionAnswer.push(qa);
            qms.activeSelections += 1;
          } // else
          feedbackui.checkCanProceed();
        }; // onclick
        div.appendChild(qoption);
      }
      feedbackui.elementMainViewQuestionsList.append(div);
    }

    feedbackapi.getTimeSelections(feedbackui.questionareId, feedbackui.timeSelectionsLoaded);
  },

  /**
   * from: https://stackoverflow.com/questions/22521982/check-if-point-is-inside-a-polygon  || This code doesn't work reliably when the point is a corner of the polygon or on an edge.
   * ray-casting algorithm based on https://wrf.ecse.rpi.edu/Research/Short_Notes/pnpoly.html/pnpoly.html
   *
   * @param {integer} x
   * @param {integer}
   * @return {string} area or null if the coordinates are not inside an area
   */
  getArea: function(x, y) {
    for (let a = 0; a < feedbackui.areas.length; ++a) {
      let area = feedbackui.areas[a];
      let inside = false;
      for (let i = 0, j = area.polygon.length - 1; i < area.polygon.length; j = i++) {
        let xi = area.polygon[i].x;
        yi = area.polygon[i].y;
        let xj = area.polygon[j].x;
        yj = area.polygon[j].y;

        let intersect = ((yi > y) != (yj > y)) &&
          (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
        if (intersect) inside = !inside;
      }
      if (inside) {
        return area;
      }
    }
    return null;
  },

  /**
   * update next page button to reflect whether the questionare can be continued
   */
  checkCanProceed: function() {
    switch (feedbackui.currentPage) {
      case feedbackui.PAGE_USERS:
        // feedbackui.elementSideViewNextPageButton.prop("hidden", (feedbackui.activeAnswer.userId === null) ? true : false);
        feedbackui.activeAnswer.userId !== null ? feedbackui.continueEnable() : feedbackui.continueDisable();
        break;
      case feedbackui.PAGE_ADD_USER:
        // nothing needed, side bar is hidden
        break;
      case feedbackui.PAGE_AREA_SELECTION:
        // feedbackui.elementSideViewNextPageButton.prop("hidden", (feedbackui.activeAnswer.areaId.length < 1) ? true : false);
        feedbackui.activeAnswer.areaId.length >= 1 ? feedbackui.continueEnable() : feedbackui.continueDisable();
        break;
      case feedbackui.PAGE_QUESTIONS:
        // feedbackui.elementSideViewNextPageButton.prop("hidden", (feedbackui.activeAnswer.questionAnswer.length < 1) ? true : false);
        feedbackui.activeAnswer.questionAnswer.length >= 1 ? feedbackui.continueEnable() : feedbackui.continueDisable();
        // we could also allow answering "all default OK", but let's force clicking at least something for now
        break;
      case feedbackui.PAGE_TIME_SELECTION:
        if (feedbackui.timeSelectionsAvailable) {
          // feedbackui.elementSideViewNextPageButton.prop("hidden", (feedbackui.activeAnswer.reportTimestamp.length < 1) ? true : false);
          feedbackui.activeAnswer.reportTimestamp.length >= 1 ? feedbackui.continueEnable() : feedbackui.continueDisable();
        } else {
          // feedbackui.elementSideViewNextPageButton.prop("hidden", timepicker.isValid() ? false : true);
          timepicker.isValid() ? feedbackui.continueEnable() : feedbackui.continueDisable();

        }
        break;
      case feedbackui.PAGE_THANK_YOU:
        feedbackui.elementSideViewNextPageButton.prop("hidden", true);
        break;
      default:
        console.log("Unknown page: " + feedbackui.currentPage);
        feedbackui.handleError();
        break;
    }
  },

  /**
   * reset location image
   */
  resetLocationImage: function() {
    if (!feedbackui.areaImageUrl) {
      console.log("No location image: not reseting.");
      return;
    }
    feedbackui.ctx.globalAlpha = 1.0;
    feedbackui.ctx.drawImage(feedbackui.locationImage, 0, 0, feedbackui.locationImage.width, feedbackui.locationImage.height);
    for (let i = 0; i < feedbackui.areas.length; ++i) {
      let area = feedbackui.areas[i];
      let c = area.polygon[area.polygon.length - 1];
      feedbackui.ctx.fillStyle = definitions.AREA_FONT_COLOR;

      // "HTML5 canvas ctx.fillText won't do line breaks?":  https: //stackoverflow.com/a/4478894
      let lineheight = 20;
      let lines = area.name.split(' ');
      let aboutMiddleHeight = (c.y - area.polygon[0].y) / 2;


      for (let i = 0; i < lines.length; i++) {
        if (area.name === "E t e i n e n") {
          feedbackui.ctx.fillText(lines[i], c.x + 20, c.y + 20 + (i * lineheight));
        } else {
          feedbackui.ctx.fillText(lines[i], c.x + 5, c.y + (i * lineheight) - aboutMiddleHeight);
        }
      }
    }
  },

  /**
   * @param {object} areas
   */
  areasLoaded: function(areas) {
    if (areas == null || areas.length < 1) {
      console.log("Failed to load areas.");
      feedbackui.handleError();
      return;
    }

    console.log("Areas loaded.");
    feedbackui.areas = areas;

    if (feedbackui.areaImageUrl) {
      feedbackui.elementMainViewAreaSelectionCanvas.addEventListener('click', function(event) {
        feedbackui.restartIdleTimer();
        let area = feedbackui.getArea(event.pageX - feedbackui.elementMainViewAreaSelectionCanvas.offsetLeft, event.pageY - feedbackui.elementMainViewAreaSelectionCanvas.offsetTop);
        if (area == null) {
          return;
        }

        let index = feedbackui.activeAnswer.areaId.indexOf(area.id);
        if (feedbackui.activeAnswer.areaId.length > 0) { // an area was selected before
          feedbackui.elementSideViewAreaSelectionList.empty(); // remove from side-view
          feedbackui.activeAnswer.areaId = [];
          feedbackui.resetLocationImage();
        }

        if (index < 0) { // this area was not selected previously
          let div = document.createElement("div");
          div.className = "side-view-area-selection";
          div.id = feedbackui.AREA_ID_PREFIX + area.id;
          div.appendChild(feedbackui.createSelectedIconImageElement());
          const parsedAreaName = area.name.includes("–") || area.name === "E t e i n e n" ? area.name.replace(/– /g, '') : area.name;
          div.appendChild(document.createTextNode(parsedAreaName));
          feedbackui.elementSideViewAreaSelectionList.append(div); // add to side-view
          feedbackui.activeAnswer.areaId.push(area.id);

          if (area.polygon.length < 3) {
            console.log("Cannot draw area with less than 3 coordinates.");
            feedbackui.handleError();
            return;
          }

          feedbackui.ctx.globalAlpha = definitions.AREA_ALPHA;
          feedbackui.ctx.beginPath();
          feedbackui.ctx.fillStyle = definitions.AREA_SELECTED_COLOR;
          let c = area.polygon[0];
          feedbackui.ctx.moveTo(c.x, c.y);
          for (let j = 1; j < area.polygon.length; ++j) {
            c = area.polygon[j];
            feedbackui.ctx.lineTo(c.x, c.y);
          }
          feedbackui.ctx.closePath();
          feedbackui.ctx.fill();

          feedbackui.ctx.fillStyle = definitions.AREA_SELECTED_FONT_COLOR;
          feedbackui.ctx.fillText(area.name, c.x + 300, c.y + 300, 100);
        }

        feedbackui.checkCanProceed();
      }, false); // add click listener

      feedbackui.locationImage = new Image();
      feedbackui.locationImage.src = feedbackui.areaImageUrl;

      feedbackui.locationImage.onload = function() {
        feedbackui.elementMainViewAreaSelectionCanvas.height = feedbackui.locationImage.height;
        feedbackui.elementMainViewAreaSelectionCanvas.width = feedbackui.locationImage.width;
        feedbackui.ctx = feedbackui.elementMainViewAreaSelectionCanvas.getContext("2d");
        feedbackui.resetLocationImage();
        feedbackui.ctx.font = definitions.AREA_FONT;

        feedbackui.locationAvailable = true;

        feedbackapi.getQuestions(feedbackui.questionareId, feedbackui.questionsLoaded);
      }
    } else { // no image url
      for (let i = 0; i < areas.length; ++i) {
        let a = areas[i];
        let adiv = document.createElement("div");
        adiv.className = "area-selection right_side_button";

        let name = document.createElement('p');
        name.className = 'area-selection-text';
        name.innerHTML = a.name;
        adiv.appendChild(name);
        let ba = $(adiv);
        ba.data(feedbackui.DATA_KEY_NAME, a.name);
        ba.data(feedbackui.DATA_KEY_AREA_ID, a.id);

        adiv.onclick = function(event) {
          feedbackui.restartIdleTimer();
          let ta = $(this);
          let areaId = ta.data(feedbackui.DATA_KEY_AREA_ID);

          let index = feedbackui.activeAnswer.areaId.indexOf(areaId);
          if (feedbackui.activeAnswer.areaId.length > 0) { // an area was selected before
            feedbackui.elementSideViewAreaSelectionList.empty(); // remove from side-view
            $(".area-selection-selected").removeClass("area-selection-selected");
            feedbackui.activeAnswer.areaId = [];
          }

          let div = document.createElement("div");
          div.className = "side-view-area-selection";
          div.id = feedbackui.AREA_ID_PREFIX + areaId;
          let areaname = ta.data(feedbackui.DATA_KEY_NAME);
          div.appendChild(feedbackui.createSelectedIconImageElement());
          const parsedAreaName = areaname.includes("–") || areaname === "E t e i n e n" ? areaname.replace(/– /g, '') : areaname;
          div.appendChild(document.createTextNode(parsedAreaName));
          feedbackui.elementSideViewAreaSelectionList.append(div); // add to side-view
          feedbackui.activeAnswer.areaId.push(areaId);

          ta.addClass("area-selection-selected");
          feedbackui.checkCanProceed();
        }; // onclick
        feedbackui.elementMainViewAreaSelectionList.append(adiv);
      }

      feedbackui.locationAvailable = true;

      feedbackapi.getQuestions(feedbackui.questionareId, feedbackui.questionsLoaded);
    }
  },

  /**
   * initialize default time selection picker
   */
  createDefaultTimeSelections: function() {
    timepicker.initialize();
    $("#main-view-timeselection-picker").removeClass("hidden");
    feedbackui.elementMainViewTimeSelectionsList.addClass("hidden");
  },

  /**
   * @param {object} timeSelections
   */
  timeSelectionsLoaded: function(timeSelections) {
    if (timeSelections == null || timeSelections.length < 1) {
      console.log("No time selections.");
      feedbackui.timeSelectionsAvailable = false;
      feedbackui.createDefaultTimeSelections();
    } else {
      console.log("Time selections loaded.");
      feedbackui.timeSelectionsAvailable = true;

      feedbackui.elementMainViewTimeSelectionsList.empty();

      utils.bubbleSortStringAsc(timeSelections, "start");

      for (let i = 0; i < timeSelections.length; ++i) {
        let ts = timeSelections[i];
        let div = document.createElement("div");
        div.className = "time-selection";
        let tsOption = document.createElement("button");
        tsOption.classList.add('right_side_button');
        const parsedHour = parseInt(ts.start.toString().substring(0, 2)); // + utils.getTimeOffset();
        const parsedMinutes = ts.start.toString().substring(2, 5);
        let localStart = "" + parsedHour + parsedMinutes;
        const parsedHourEnd = parseInt(ts.end.toString().substring(0, 2)); // + utils.getTimeOffset();
        const parsedMinutesEnd = ts.end.toString().substring(2, 5);
        let localEnd = "" + parsedHourEnd + parsedMinutesEnd;
        const correctedTimeStringForButtons = ts.name.toString().toUpperCase();
        // localStart + "-" + localEnd + "&emsp;<b>" + ts.name.toString().toUpperCase() + "</b>" :
        // localStart + "&emsp;<b>" + ts.name.toString().toUpperCase() + "</b>";
        // const correctedTimeStringForList = localStart + " " + ts.name.toString().toUpperCase();
        const correctedTimeStringForList = ts.name.toString().toUpperCase();
        tsOption.innerHTML = correctedTimeStringForButtons;
        let bq = $(tsOption);
        bq.data(feedbackui.DATA_KEY_START, localStart);
        bq.data(feedbackui.DATA_KEY_END, localEnd);
        bq.data(feedbackui.DATA_KEY_TIME_SELECTION_ID, ts.id);

        tsOption.onclick = function() {
          feedbackui.restartIdleTimer();
          let b = $(this);
          let start = b.data(feedbackui.DATA_KEY_START);
          let end = b.data(feedbackui.DATA_KEY_END);
          let tsId = b.data(feedbackui.DATA_KEY_TIME_SELECTION_ID);
          if (b.hasClass("time-selection-selected")) {
            feedbackui.elementSideViewTimeSelectionList.find("#" + feedbackui.TIME_SELECTION_ID_PREFIX + tsId).remove(); // remove from side-view
            b.removeClass("time-selection-selected");
            for (let i = 0; i < feedbackui.activeAnswer.reportTimestamp.length; ++i) {
              let rt = feedbackui.activeAnswer.reportTimestamp[i];
              if (rt.end.localeCompare(end) == 0 && rt.start.localeCompare(start) == 0) {
                feedbackui.activeAnswer.reportTimestamp.splice(i, 1);
                break;
              }
            }
          } else {
            let div = document.createElement("div");
            div.className = "side-view-time-selection";
            div.id = feedbackui.TIME_SELECTION_ID_PREFIX + tsId;
            div.appendChild(feedbackui.createSelectedIconImageElement());
            div.appendChild(document.createTextNode(correctedTimeStringForList));
            feedbackui.elementSideViewTimeSelectionList.append(div); // add to side-view
            b.addClass("time-selection-selected");
            let rt = {
              continuous: feedbackui.reoccurring,
              start: start,
              end: end
            };
            feedbackui.activeAnswer.reportTimestamp.push(rt);
          }
          feedbackui.checkCanProceed();
        };
        div.appendChild(tsOption);

        feedbackui.elementMainViewTimeSelectionsList.append(div);
      }
    }
    feedbackui.loadPageUsers();
  },

  /**
   * This function is used for adding the tiny, tiny
   * green checkmark icons in front of selected items
   * listed at the bottom of the page
   */
  createSelectedIconImageElement: () => {
    let selectedIconImage = document.createElement('img');
    selectedIconImage.src =
      '/feedback/images/checkmark_x2.png';
    selectedIconImage.classList.add('selected-icon-image');
    return selectedIconImage;
  },

  /**
   * @param {object} user
   */
  currentUserLoaded: function(user) {
    if (user == null) {
      console.log("Failed to retrieve authenticated user.");
      feedbackui.handleError();
      return;
    }
    // we could also set user.id to activeAnswer, but if the questionare uses service level users, the value will be overridden by the service anyway
    feedbackui.elementActiveUsername.html(user.username);
    feedbackui.showPage(feedbackui.PAGE_USERS);
  },

  /**
   * @param {object} users
   */
  usersLoaded: function(users) {
    if (users == null) {
      console.log("Failed to load users.");
      feedbackui.handleError();
      return;
    } else if (users.length < 1) {
      console.log("No users.");
    } else {
      console.log("Users loaded.");

      utils.bubbleSortStringAsc(users, "username");
      for (let i = 0; i < users.length; ++i) {
        let user = users[i];
        let div = document.createElement("div");
        div.className = "user";
        let uOption = document.createElement("button");
        uOption.id = feedbackui.USER_ID_PREFIX + user.id;
        uOption.innerHTML = user.username;
        feedbackui.usernameCache.push(user.username);
        uOption.onclick = function(event) {
          if (document.querySelector("#active_user") && feedbackui.activeAnswer.userId === this.id.split(feedbackui.USER_ID_PREFIX)[1]) {
            feedbackui.removeActiveUser()
            feedbackui.checkCanProceed();
          } else {
            feedbackui.removeActiveUser();
            this.classList.add('user-value-selected');
            feedbackui.activeAnswer.userId = this.id.split(feedbackui.USER_ID_PREFIX)[1];
            console.log('feedbackui.activeAnswer.userId: ', feedbackui.activeAnswer.userId)
            feedbackui.elementSideViewNextPageButton.removeClass('hidden');
            let selectedUser = document.querySelector("#side-view-user-selection");
            let newDiv = document.createElement('div');
            newDiv.setAttribute('id', "active_user");
            newDiv.appendChild(feedbackui.createSelectedIconImageElement());
            newDiv.appendChild(document.createTextNode(user.username));
            feedbackui.elementSideViewUserSelection.append(newDiv);
            feedbackui.checkCanProceed();
          }

        };
        div.appendChild(uOption);
        feedbackui.elementMainViewUsersUserList.append(div);
      }
    }
    feedbackui.showPage(feedbackui.PAGE_USERS);
  },

  /**
   * Using an anonymous user in the feedback
   */
  anonymousUser: () => {
    if (document.querySelector("#active_user")) {
      feedbackui.removeActiveUser();
    }
    feedbackui.nextPage();
  },

  /**
   * load users page
   */
  loadPageUsers: function() {
    feedbackui.stopIdleTimer();
    feedbackui.stopThankYouTimer();
    console.log("Loading users page.");
    feedbackui.showWaitMessage(true, "Ladataan...");
    feedbackui.resetQuestionare();
    timepicker.reset();
    feedbackui.elementMainViewUsersUserList.empty();
    if (feedbackui.usersAvailable) {
      feedbackapi.getUsers(feedbackui.questionareId, feedbackui.usersLoaded);
    } else {
      feedbackapi.getCurrentUser(feedbackui.currentUserLoaded);
    }
  },

  /**
   * reset questionare
   */
  resetQuestionare: function() {
    console.log("Reseting questionare.");

    feedbackui.activeAnswer = {
      userId: null,
      areaId: [],
      questionAnswer: [],
      reportTimestamp: [],
      reoccurring: false // false, not reoccurring
    };

    var d = $(document);

    (d.find(".question-value")).each(function(index, element) {
      let e = $(element);
      if (e.data(feedbackui.DATA_KEY_DEFAULT_VALUE)) {
        e.addClass("question-value-selected");
      } else {
        e.removeClass("question-value-selected");
      }
    });

    (d.find(".time-selection-selected")).removeClass("time-selection-selected");
    (d.find(".area-selection-selected")).removeClass("area-selection-selected");

    feedbackui.elementSideViewAreaSelectionList.empty();
    document.querySelector("#active_user") && document.querySelector("#active_user").remove();
    feedbackui.elementSideViewQuestionSelectionList.empty();
    feedbackui.elementSideViewTimeSelectionList.empty();
    feedbackui.elementMainViewAddUserInput.val("");
    feedbackui.usernameCache = [];

    feedbackui.reoccurring = false; // false, not reoccurring

    document.querySelector(".time-reoccurring-selected") && document.querySelector(".time-reoccurring-selected").classList.remove("time-reoccurring-selected");

    if (feedbackui.locationAvailable) {
      feedbackui.resetLocationImage();
    }

    for (let qms of feedbackui.questionsMaxSelections.values()) {
      qms.activeSelections = 0;
    }
    feedbackui.removeBackLink();
    feedbackui.removeContinue();
  },

  /**
   *
   */
  removeActiveUser: () => {
    document.querySelector(".user-value-selected") &&
      document.querySelector(".user-value-selected").classList.remove("user-value-selected");
    document.querySelector("#active_user") && document.querySelector("#active_user").remove();
    feedbackui.activeAnswer.userId = null;
  },

  /**
   *
   */
  makeBottomAreaBold: (boldArea) => {
    const bottomAreas = ["#side-view-user-selection",
      "#side-view-area-selection-list",
      "#side-view-question-selection-list",
      "#side-view-time-selection-list"
    ]
    for (let area of bottomAreas) {
      let selectedSiblings = document.querySelectorAll(`${area} ~ p`);
      if (boldArea === area) {
        for (let sibling of selectedSiblings) {
          sibling.classList.add('bold900');
        }
      } else {
        for (let sibling of selectedSiblings) {
          sibling.classList.remove('bold900');
        }
      }
    }
  },

  /**
   * @param {integer} page to show, hides others
   */
  showPage: function(page) {
    console.log("Showing page: " + page);
    feedbackui.stopIdleTimer();
    feedbackui.elementMainViewUsers.addClass("hidden");
    feedbackui.elementMainViewAreaSelection.addClass("hidden");
    feedbackui.elementMainViewQuestions.addClass("hidden");
    feedbackui.elementMainViewTimeSelections.addClass("hidden");
    feedbackui.elementMainViewAddUser.addClass("hidden");
    feedbackui.elementMainViewThankYou.addClass("hidden");
    feedbackui.elementSideView.removeClass("hidden");

    feedbackui.progressBarUser.removeClass("bottom_progress_bar_current bottom_progress_bar_completed");
    feedbackui.progressBarArea.removeClass("bottom_progress_bar_current bottom_progress_bar_completed");
    feedbackui.progressBarQuestion.removeClass("bottom_progress_bar_current bottom_progress_bar_completed");
    feedbackui.progressBarTimeSelection.removeClass("bottom_progress_bar_current bottom_progress_bar_completed");
    feedbackui.currentPage = page;

    feedbackui.removeContinue();
    feedbackui.removeBackLink();

    switch (page) {
      case feedbackui.PAGE_USERS:
        if (feedbackui.usersAvailable) {
          feedbackui.elementMainViewMultiUser.removeClass("hidden");
          feedbackui.elementMainViewSingleUser.addClass("hidden");
          feedbackui.createContinue(true);
          feedbackui.removeBackLink();
        } else {
          feedbackui.elementMainViewMultiUser.addClass("hidden");
          feedbackui.elementMainViewSingleUser.removeClass("hidden");
        }
        feedbackui.elementMainViewUsers.removeClass("hidden");
        feedbackui.progressBarUser.addClass("bottom_progress_bar_current");

        feedbackui.makeBottomAreaBold("#side-view-user-selection");
        feedbackui.elementSideViewNarrowText.html("Käyttäjä");
        feedbackui.elementSideViewNarrowPage.html("1");
        feedbackui.scrolltoTopOfThePage();
        break;
      case feedbackui.PAGE_AREA_SELECTION:
        feedbackui.restartIdleTimer();
        feedbackui.elementMainViewAreaSelection.removeClass("hidden");
        feedbackui.elementSideViewNextPageButton.html(definitions.BUTTON_NAME_NEXT);
        feedbackui.progressBarUser.addClass("bottom_progress_bar_completed");
        feedbackui.progressBarArea.addClass("bottom_progress_bar_current");

        feedbackui.makeBottomAreaBold("#side-view-area-selection-list");
        feedbackui.elementSideViewNarrowText.html("Huone");
        feedbackui.elementSideViewNarrowPage.html("2");
        feedbackui.showBackLink();
        feedbackui.createContinue();
        feedbackui.scrolltoTopOfThePage();
        break;
      case feedbackui.PAGE_QUESTIONS:
        feedbackui.restartIdleTimer();
        feedbackui.elementMainViewQuestions.removeClass("hidden");
        feedbackui.elementSideViewNextPageButton.html(definitions.BUTTON_NAME_NEXT);
        feedbackui.progressBarUser.addClass("bottom_progress_bar_completed");
        feedbackui.progressBarArea.addClass("bottom_progress_bar_completed");
        feedbackui.progressBarQuestion.addClass("bottom_progress_bar_current");

        feedbackui.makeBottomAreaBold("#side-view-question-selection-list");
        feedbackui.elementSideViewNarrowText.html("Tuntemus");
        feedbackui.elementSideViewNarrowPage.html(3 - (feedbackui.locationAvailable ? 0 : 1)); // decrease one from count if location/area page is not available
        feedbackui.createContinue();
        feedbackui.showBackLink();
        feedbackui.scrolltoTopOfThePage();
        break;
      case feedbackui.PAGE_TIME_SELECTION:
        feedbackui.restartIdleTimer();
        feedbackui.elementMainViewTimeSelections.removeClass("hidden");
        feedbackui.elementSideViewNextPageButton.html(definitions.BUTTON_NAME_NEXT);
        feedbackui.progressBarUser.addClass("bottom_progress_bar_completed");
        feedbackui.progressBarArea.addClass("bottom_progress_bar_completed");
        feedbackui.progressBarQuestion.addClass("bottom_progress_bar_completed");
        feedbackui.progressBarTimeSelection.addClass("bottom_progress_bar_current");

        feedbackui.makeBottomAreaBold("#side-view-time-selection-list");
        feedbackui.elementSideViewNarrowText.html("Tapahtumahetki");
        feedbackui.elementSideViewNarrowPage.html(4 - (feedbackui.locationAvailable ? 0 : 1)); // decrease one from count if location/area page is not available
        feedbackui.createContinue();
        feedbackui.showBackLink();
        feedbackui.scrolltoTopOfThePage();
        break;
      case feedbackui.PAGE_ADD_USER:
        feedbackui.removeBackLink();
        feedbackui.removeContinue();
        feedbackui.restartIdleTimer();
        feedbackui.elementMainViewAddUser.removeClass("hidden");
        feedbackui.elementSideView.addClass("hidden");
        feedbackui.elementSideViewNextPageButton.addClass("hidden");
        if (document.querySelector("#active_user")) {
          feedbackui.removeActiveUser();
        }
        feedbackui.scrolltoTopOfThePage();
        break;
      case feedbackui.PAGE_THANK_YOU:
        feedbackui.removeBackLink();
        feedbackui.removeContinue();
        feedbackui.stopIdleTimer();
        feedbackui.elementMainViewThankYou.removeClass("hidden");
        feedbackui.progressBarUser.addClass("bottom_progress_bar_completed");
        feedbackui.progressBarArea.addClass("bottom_progress_bar_completed");
        feedbackui.progressBarQuestion.addClass("bottom_progress_bar_completed");
        feedbackui.progressBarTimeSelection.addClass("bottom_progress_bar_completed");
        feedbackui.saveAnswer();
        feedbackui.scrolltoTopOfThePage();
        break;
      default:
        console.log("Unknown page: " + page);
        feedbackui.handleError();
        break;
    }

    feedbackui.checkCanProceed();
    feedbackui.showWaitMessage(false, null); // close any wait dialogs
  },

  /**
   * create user
   */
  createUser: function() {
    console.log("Creating a new user...");
    var user = {
      username: $.trim(feedbackui.elementMainViewAddUserInput.val())
    };

    if (user.username.length === 0) {
      console.log("Empty username.");
      return;
    }

    if (feedbackui.usernameCache.includes(user.username)) { // TODO we should really check this from service, but as this application is more-or-less single instance, simply use cache
      console.log("Username already in use.");
      return;
    }

    feedbackui.showWaitMessage(true, "Luodaan käyttäjää...");
    feedbackapi.postUser(feedbackui.questionareId, user, feedbackui.userCreated);
  },

  /**
   * @param {object} user
   */
  userCreated: function(user) {
    if (user == null) {
      console.log("Failed to create a new user.");
      feedbackui.handleError();
      return;
    }
    feedbackui.showWaitMessage(false, null);
    feedbackui.loadPageUsers();
  },

  /**
   * send answer
   */
  saveAnswer: function() {
    console.log("Saving answer...");
    feedbackui.stopIdleTimer();
    feedbackui.showWaitMessage(true, "Lähetetään...");

    if (!feedbackui.timeSelectionsAvailable) {
      let rt = {
        start: timepicker.getStart(),
        end: timepicker.getEnd(),
        continuous: feedbackui.reoccurring
      }
      feedbackui.activeAnswer.reportTimestamp.push(rt);
    }
    for (let rt of feedbackui.activeAnswer.reportTimestamp) { // convert time selections to date-time string
      if (typeof rt.start.getMonth !== 'function') { // not a date object
        let parts = rt.start.split(":");
        rt.start = new Date();
        rt.start.setMilliseconds(0);
        rt.start.setSeconds(0);
        rt.start.setHours(parseInt(parts[0]));
        rt.start.setMinutes(parseInt(parts[1]));
      }
      rt.start = rt.start.toISOString();
      if (typeof rt.end.getMonth !== 'function') { // not a date object
        let parts = rt.end.split(":");
        rt.end = new Date();
        rt.end.setMilliseconds(0);
        rt.end.setSeconds(0);
        rt.end.setHours(parseInt(parts[0]));
        rt.end.setMinutes(parseInt(parts[1]));
      }
      rt.end = rt.end.toISOString();
    }

    feedbackapi.postAnswer(feedbackui.questionareId, feedbackui.activeAnswer, feedbackui.answerSaved);
  },

  /**
   * @param {object} answer
   */
  answerSaved: function(answer) {
    if (answer == null) {
      console.log("Failed to send answer.");
    }
    feedbackui.showWaitMessage(false, null);
    feedbackui.startThankYouTimer();
  },

  /**
   *
   */
  nextPage: function() {
    console.log("Changing to next page.")

    switch (feedbackui.currentPage) {
      case feedbackui.PAGE_USERS:
        if (feedbackui.locationAvailable) {
          feedbackui.showPage(feedbackui.PAGE_AREA_SELECTION);
        } else {
          feedbackui.showPage(feedbackui.PAGE_QUESTIONS);
        }
        break;
      case feedbackui.PAGE_AREA_SELECTION:
        feedbackui.showPage(feedbackui.PAGE_QUESTIONS);
        break;
      case feedbackui.PAGE_QUESTIONS:
        feedbackui.showPage(feedbackui.PAGE_TIME_SELECTION);
        break;
      case feedbackui.PAGE_TIME_SELECTION:
        feedbackui.showPage(feedbackui.PAGE_THANK_YOU);
        break;
      case feedbackui.PAGE_THANK_YOU:
        break; // there is no "next page" for thank you, this is the last page
      case feedbackui.PAGE_ADD_USER:
        feedbackui.loadPageUsers(); // go back to first page
        break;
      default:
        console.log("Unknown page: " + feedbackui.currentPage);
        feedbackui.handleError();
        break;
    }
  },

  /**
   *
   */
  previousPage: () => {
    switch (feedbackui.currentPage) {
      case feedbackui.PAGE_AREA_SELECTION:
        feedbackui.loadPageUsers();
        break;
      case feedbackui.PAGE_QUESTIONS:
        if (feedbackui.locationAvailable) {
          feedbackui.showPage(feedbackui.PAGE_AREA_SELECTION);
        } else {
          feedbackui.loadPageUsers();
        }
        break;
      case feedbackui.PAGE_TIME_SELECTION:
        feedbackui.showPage(feedbackui.PAGE_QUESTIONS);
        break;
    }
  },

  /**
   *
   */
  showBackLink: () => {
    if (!document.querySelector(".back-link")) {
      let backDiv = document.createElement("div");
      backDiv.className = "back-link";
      backDiv.innerHTML = "&lt; TAKAISIN";
      backDiv.onclick = feedbackui.previousPage;
      document.querySelector("#main-container").appendChild(backDiv);
    }
  },

  /**
   *
   */
  removeBackLink: () => {
    document.querySelector("div.back-link") && document.querySelector("div.back-link").remove();
  },

  /**
   *
   */
  createContinue: (isUserList) => {
    if (isUserList !== true) {
      let continueDiv = document.createElement("div");
      continueDiv.classList.add('side-view-next-button-container');
      continueDiv.style.gridArea = 'buttons';
      let continueButton = document.createElement('button');
      continueButton.classList.add('right_side_button', 'black_button', 'continue_button');
      continueButton.id = "continue_button";
      continueButton.innerHTML = 'JATKA &gt';
      continueButton.style.opacity = 0.1;
      continueDiv.appendChild(continueButton);
      const mainContainer = document.querySelector('#main-container');
      mainContainer.appendChild(continueDiv);
    }

    if (isUserList === true) {
      let continueDiv = document.createElement("div");
      continueDiv.classList.add('side-view-next-button-container');
      continueDiv.style.gridArea = 'buttons';

      let createUserButton = document.createElement('button');
      createUserButton.classList.add('right_side_button', 'green_button', 'userlist_button');
      createUserButton.id = "main-view-add-user-button";
      createUserButton.innerHTML = '<span id="bigger_font">✚</span> LUO KÄYTTÄJÄ';
      createUserButton.onclick = function() { feedbackui.showPage(feedbackui.PAGE_ADD_USER) };
      continueDiv.appendChild(createUserButton);

      let anonymousContinue = document.createElement('button');
      anonymousContinue.classList.add('right_side_button', 'black_button', 'userlist_button');
      anonymousContinue.id = "main-view-skip-user-selection-button";
      anonymousContinue.innerHTML = 'ANONYYMI PALAUTE';
      anonymousContinue.onclick = feedbackui.anonymousUser;
      continueDiv.appendChild(anonymousContinue);

      let continueButton = document.createElement('button');
      continueButton.classList.add('right_side_button', 'black_button', 'continue_button',
        'userlist_button');
      continueButton.id = "continue_button";
      continueButton.innerHTML = 'JATKA &gt';
      continueButton.style.opacity = 0.1;
      continueDiv.appendChild(continueButton);

      const mainContainer = document.querySelector('#main-container');
      mainContainer.appendChild(continueDiv);
    }


  },

  /**
   *
   */
  removeContinue: () => {
    document.querySelector('.side-view-next-button-container') && document.querySelector('.side-view-next-button-container').remove();
  },

  /**
   *
   */
  continueEnable: () => {
    const continueButton = document.querySelector('#continue_button');
    if (continueButton) {
      continueButton.style.opacity = 1;
      continueButton.onclick = feedbackui.nextPage;
    }
  },

  /**
   *
   */
  continueDisable: () => {
    const continueButton = document.querySelector('#continue_button');
    if (continueButton) {
      continueButton.onclick = null;
      continueButton.style.opacity = 0.5;
    }
  },

  /**
   *
   */
  scrolltoTopOfThePage: () => {
    document.querySelector('#main-view').scrollTop = 0;
  },

  /**
   *
   */
  showLongHelpText: () => {
    const showHelpButton = document.querySelector('#show-long-help-text');
    helpDiv = document.querySelector('#long-help-text');
    if (helpDiv.classList.contains('hidden')) {
      helpDiv.classList.remove('hidden');
      showHelpButton.innerHTML = "Piilota ohje";
    } else {
      helpDiv.classList.add('hidden');
      showHelpButton.innerHTML = "Näytä ohje";
    }
  }
};