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
var udefinitions = {
  /* URIs */
	URI_FEEDBACK: "/feedback/index.html?questionare_id=",
  URI_QUESTIONARES: "/questionares",
  URI_USERS: "/users",
	URI_LOGIN_PAGE : "/user/login.html",

	/* common */
	VALUE_TRUE: "true",
	USER_CREATED_OK_LINK : '<a href="/user/login.html">Käyttäjä luotu. Jatka kirjautumiseen tästä.</a>',
	USER_CREATED_FAILED_400 : 'Käyttäjän luonti annetulla käyttäjänimellä epäonnistui. Yritä toisella nimellä.',
	USER_CREATED_FAILED : 'Käyttäjää luotaessa tapahtui palvelinvirhe. Yritä myöhemmin uudelleen.',
	MIN_PASSWORD_LENGTH : 6,

	/* parameters */
	URI_PARAMETER_LOGOUT: "logout",
	URI_PARAMETER_TOKEN: "token",
	URI_PARAMETER_QUESTIONARE_ID: "questionare_id",

	/* session storage keys */
	SESSIONSTORAGE_USERNAME : "username",
	SESSIONSTORAGE_PASSWORD : "password"
};
