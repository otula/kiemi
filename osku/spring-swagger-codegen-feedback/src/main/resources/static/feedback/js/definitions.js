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
var definitions = {
  /* URIs */
  URI_BASE: "",
  URI_QUESTIONARES: "/questionares",
  URI_USERS: "/users",
  URI_LOCATIONS: "/locations",
  URI_AREAS: "/areas",
  URI_QUESTIONS: "/questions",
  URI_TIME_SELECTIONS: "/timeSelections",
  URI_ANSWERS: "/answers",

  /* common */
  INPUT_WAIT: 120000, //60000, // how long to wait for user input before reseting back to user page, in ms
  AREA_FONT: "16px Arial",
  AREA_FONT_COLOR: "black",
  AREA_SELECTED_FONT_COLOR: "white",
  AREA_SELECTED_COLOR: "#86D936",
  AREA_ALPHA: 0.4,
  ERROR_WAIT_DELAY: 5000, // how long to wait in error situation, in ms
  THANK_YOU_WAIT_DELAY: 10000, // how long to wait in error situation, in ms

  /* element names */
  BUTTON_NAME_SAVE: "TALLENNA",
  BUTTON_NAME_NEXT: "JATKA >",

  /* parameters */
  URI_PARAMETER_QUESTIONARE_ID: "questionare_id"
};