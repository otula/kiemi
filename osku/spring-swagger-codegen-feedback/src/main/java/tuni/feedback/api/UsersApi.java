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
 * NOTE: This class is auto generated by the swagger code generator program (3.0.22).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
/**
 * NOTE: This class is auto generated by the swagger code generator program (3.0.22).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package tuni.feedback.api;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import tuni.feedback.model.User;
import tuni.feedback.model.UserToken;

/**
 * 
 * 
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-04-16T16:17:33.447797968+03:00[Europe/Helsinki]")
@Api(value = "users")
public interface UsersApi {
	
	/**
	 * 
	 * @return response
	 */
	@ApiOperation(value = "Retrieve the currently authenticated user.", nickname = "getAuthenticatedUser", notes = "", response = User.class, responseContainer = "List", authorizations = { @Authorization(value = "BasicAuth") }, tags = { "users", })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "successful operation", response = User.class, responseContainer = "List") })
	@RequestMapping(value = "/users", produces = { "application/json" }, method = RequestMethod.GET)
	ResponseEntity<List<User>> getAuthenticatedUser();
	
	/**
	 * 
	 * @param userId
	 * @return response
	 */
	@ApiOperation(value = "Get user with the given id. Non-admin users can only retrieve their own information.", nickname = "getUser", notes = "", response = User.class, responseContainer = "List", authorizations = { @Authorization(value = "BasicAuth") }, tags = { "users", })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "successful operation", response = User.class, responseContainer = "List") })
	@RequestMapping(value = "/users/{userId}", produces = { "application/json" }, method = RequestMethod.GET)
	ResponseEntity<List<User>> getUser(@ApiParam(value = "Retrieve user, which has the given id.", required = true) @PathVariable("userId") Long userId);

	/**
	 * 
	 * @param userId
	 * @return response
	 */
	@ApiOperation(value = "Delete user with the given id. User can only delete itself. Admin users can delete anyone.", nickname = "deleteUser", notes = "", authorizations = { @Authorization(value = "BasicAuth") }, tags = { "users", })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "successful operation") })
	@RequestMapping(value = "/users/{userId}", method = RequestMethod.DELETE)
	ResponseEntity<Void> deleteUser(@ApiParam(value = "Delete user, which has the given id.", required = true) @PathVariable("userId") Long userId);

	/**
	 * 
	 * @param questionareId
	 * @return response
	 */
	@ApiOperation(value = "Create new authentication token that can be used to create new users. Only admin users may create new tokens.", nickname = "postToken", notes = "", response = UserToken.class, responseContainer = "List", authorizations = { @Authorization(value = "BasicAuth") }, tags = { "users", })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "successful operation", response = UserToken.class, responseContainer = "List") })
	@RequestMapping(value = "/users/tokens", produces = { "application/json" }, method = RequestMethod.POST)
	ResponseEntity<List<UserToken>> postToken(@NotNull @ApiParam(value = "Give the user (read-only) access to the given questionares.", required = true) @Valid @RequestParam(value = "questionare_id", required = true) List<Long> questionareId);

	/**
	 * 
	 * @param token
	 * @param body
	 * @return response
	 */
	@ApiOperation(value = "Add new user.", nickname = "postUser", notes = "", response = User.class, responseContainer = "List", authorizations = { @Authorization(value = "BasicAuth") }, tags = { "users", })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "successful operation", response = User.class, responseContainer = "List") })
	@RequestMapping(value = "/users", produces = { "application/json" }, consumes = { "application/json" }, method = RequestMethod.POST)
	ResponseEntity<List<User>> postUser(@NotNull @ApiParam(value = "Authentication token to use in creating new user.", required = true) @Valid @RequestParam(value = "token", required = true) String token, @ApiParam(value = "The user as an HTTP body.", required = true) @Valid @RequestBody User body);

}
