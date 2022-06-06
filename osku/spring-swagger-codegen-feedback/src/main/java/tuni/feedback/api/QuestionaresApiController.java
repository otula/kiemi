/**
 * Copyright 2020 Tampere University
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
package tuni.feedback.api;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import tuni.feedback.exception.InvalidParameterException;
import tuni.feedback.model.Answer;
import tuni.feedback.model.Question;
import tuni.feedback.model.QuestionAnswer;
import tuni.feedback.model.QuestionValue;
import tuni.feedback.model.Questionare;
import tuni.feedback.model.QuestionareUser;
import tuni.feedback.model.ReportTimestamp;
import tuni.feedback.model.TimeSelection;
import tuni.feedback.model.converters.TimeConverter;
import tuni.feedback.service.QuestionaresService;

/**
 * 
 * 
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-11-24T17:52:32.498922+02:00[Europe/Helsinki]")
@Controller
@Api(tags = { "questionares" })
public class QuestionaresApiController implements QuestionaresApi {
	private static final FastDateFormat DATETIME_FORMATTER = FastDateFormat.getInstance(Definitions.DATE_FORMAT);
	private static final Logger LOGGER = LogManager.getLogger(QuestionaresApiController.class);
	@Autowired
	private QuestionaresService _service = null;

	/**
	 * 
	 * @param objectMapper
	 * @param request
	 */
	@org.springframework.beans.factory.annotation.Autowired
	public QuestionaresApiController(ObjectMapper objectMapper, HttpServletRequest request) {
		// nothing needed
	}

	@Override
	public ResponseEntity<Void> deleteAnswer(@ApiParam(value = "Delete answer from the questionare, which has the given id.", required = true) @PathVariable("questionareId") Long questionareId, @ApiParam(value = "Delete answer, which has the given id.", required = true) @PathVariable("answerId") Long answerId) {
		_service.deleteAnswer(questionareId, answerId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Void> deleteQuestion(@ApiParam(value = "Update questionare, which has the given id.", required = true) @PathVariable("questionareId") Long questionareId, @ApiParam(value = "Delete question, which has the given id.", required = true) @PathVariable("questionId") Long questionId) {
		_service.deleteQuestion(questionareId, questionId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Void> deleteQuestionare(@ApiParam(value = "Delete questionare, which has the given id.", required = true) @PathVariable("questionareId") Long questionareId) {
		_service.deleteQuestionare(questionareId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Void> deleteTimeSelection(@ApiParam(value = "Update questionare, which has the given id.", required = true) @PathVariable("questionareId") Long questionareId, @ApiParam(value = "Delete time selection, which has the given id.", required = true) @PathVariable("timeSelectionId") Long timeSelectionId) {
		_service.deleteTimeSelection(questionareId, timeSelectionId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<Answer>> getAnswers(@ApiParam(value = "Return answer(s) for the questionare, which has the given id.", required = true) @PathVariable("questionareId") Long questionareId,
			@ApiParam(value = "Select data submitted after the given value (inclusive), in ISO8601 format.") @Valid @RequestParam(value = "from", required = false) String from, @ApiParam(value = "Select data submitted before the given value (inclusive), in ISO8601 format.") @Valid @RequestParam(value = "to", required = false) String to,
			@ApiParam(value = "Select data with report timestamp of given value or greater (inclusive), in UTC time 00:00:00Z.") @Valid @RequestParam(value = "start_time", required = false) String startTime,
			@ApiParam(value = "Select data with report timestamp of given value or lesser (inclusive), in UTC time 00:00:00Z.") @Valid @RequestParam(value = "end_time", required = false) String endTime) {
		Date fromDate = null;
		Date toDate = null;
		if (!StringUtils.isBlank(from)) {
			try {
				fromDate = DATETIME_FORMATTER.parse(from);
			} catch (ParseException ex) {
				LOGGER.debug(ex.getMessage(), ex);
				throw new InvalidParameterException(ex.getMessage());
			}
		}
		if (!StringUtils.isBlank(to)) {
			try {
				toDate = DATETIME_FORMATTER.parse(to);
			} catch (ParseException ex) {
				LOGGER.debug(ex.getMessage(), ex);
				throw new InvalidParameterException(ex.getMessage());
			}
		}
		Long startTimeUtc = null;
		if(!StringUtils.isBlank(startTime)) {
			try {
				startTimeUtc = TimeConverter.timeToSeconds(startTime);
			} catch (IllegalArgumentException ex) {
				LOGGER.debug(ex.getMessage(), ex);
				throw new InvalidParameterException(ex.getMessage());
			}
		}
		Long endTimeUtc = null;
		if(!StringUtils.isBlank(endTime)) {
			try {
				endTimeUtc = TimeConverter.timeToSeconds(endTime);
			} catch (IllegalArgumentException ex) {
				LOGGER.debug(ex.getMessage(), ex);
				throw new InvalidParameterException(ex.getMessage());
			}
		}
		return new ResponseEntity<>(_service.getAnswers(questionareId, fromDate, toDate, startTimeUtc, endTimeUtc), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<Questionare>> getQuestionare(@ApiParam(value = "Return questionare, which has the given id.", required = true) @PathVariable("questionareId") Long questionareId) {
		Questionare questionare = _service.getQuestionare(questionareId);
		if (questionare == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		List<Questionare> list = new ArrayList<>(1);
		list.add(questionare);

		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<Questionare>> getQuestionares(@ApiParam(value = "Return specified maximum number of results (>=1).") @Valid @RequestParam(value = "max_results", required = false) Integer maxResults, @ApiParam(value = "Start listing from the given page (>=0).") @Valid @RequestParam(value = "start_page", required = false) Integer startPage) {
		return new ResponseEntity<>(_service.getQuestionares(maxResults, startPage), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<Question>> getQuestions(@ApiParam(value = "Return questions for the questionare, which has the given id.", required = true) @PathVariable("questionareId") Long questionareId, @ApiParam(value = "Return specified maximum number of results (>=1).") @Valid @RequestParam(value = "max_results", required = false) Integer maxResults,
			@ApiParam(value = "Start listing from the given page (>=0).") @Valid @RequestParam(value = "start_page", required = false) Integer startPage) {
		return new ResponseEntity<>(_service.getQuestions(questionareId, maxResults, startPage), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<TimeSelection>> getTimeSelections(@ApiParam(value = "Return time selections for the questionare, which has the given id.", required = true) @PathVariable("questionareId") Long questionareId,
			@ApiParam(value = "Return specified maximum number of results (>=1).") @Valid @RequestParam(value = "max_results", required = false) Integer maxResults, @ApiParam(value = "Start listing from the given page (>=0).") @Valid @RequestParam(value = "start_page", required = false) Integer startPage) {
		return new ResponseEntity<>(_service.getTimeSelections(questionareId, maxResults, startPage), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<QuestionareUser>> getUsers(@ApiParam(value = "Return (questionare) user for the questionare, which has the given id.", required = true) @PathVariable("questionareId") Long questionareId, @ApiParam(value = "Return specified maximum number of results (>=1).") @Valid @RequestParam(value = "max_results", required = false) Integer maxResults,
			@ApiParam(value = "Start listing from the given page (>=0).") @Valid @RequestParam(value = "start_page", required = false) Integer startPage) {
		return new ResponseEntity<>(_service.getUsers(questionareId, maxResults, startPage), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<Answer>> postAnswer(@ApiParam(value = "Add new answer for the questionare, which has the given id.", required = true) @PathVariable("questionareId") Long questionareId, @ApiParam(value = "The answer as an HTTP body.", required = true) @Valid @RequestBody Answer body) throws InvalidParameterException {
		body.setId(null);
		List<QuestionAnswer> qa = body.getQuestionAnswer();
		if (qa == null || qa.isEmpty()) { // this should be caught by the parser, but sometimes it isn't, so double check it here
			LOGGER.debug("Answer posted without questionanswers.");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
		}else {
			for (QuestionAnswer q : qa) {
				q.setId(null);
			}
		}
		List<ReportTimestamp> rts = body.getReportTimestamp();
		if(rts == null || rts.isEmpty()) { // this should be caught by the parser, but sometimes it isn't, so double check it here
			LOGGER.debug("Answer posted without report timestamps.");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
		}else {
			for (ReportTimestamp r : rts) {
				r.setId(null);
			}
		}

		Long id = _service.createAnswer(questionareId, body);
		if (id == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		List<Answer> list = new ArrayList<>(1);
		Answer a = new Answer();
		a.setId(id);
		list.add(a);

		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<Question>> postQuestion(@ApiParam(value = "Add question for the questionare, which has the given id.", required = true) @PathVariable("questionareId") Long questionareId, @ApiParam(value = "The question as an HTTP body.", required = true) @Valid @RequestBody Question body) {
		body.setId(null);
		List<QuestionValue> values = body.getValue();
		if (values != null && !values.isEmpty()) {
			for (QuestionValue v : values) {
				v.setId(null);
			}
		}

		Long id = _service.createQuestion(questionareId, body);
		if (id == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		List<Question> list = new ArrayList<>(1);
		Question q = new Question();
		q.setId(id);
		list.add(q);

		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<Questionare>> postQuestionare(@ApiParam(value = "The questionare as an HTTP body.", required = true) @Valid @RequestBody Questionare body) {
		body.setId(null);
		Long id = _service.createQuestionare(body);
		if (id == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		List<Questionare> list = new ArrayList<>(1);
		Questionare q = new Questionare();
		q.setId(id);
		list.add(q);

		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<TimeSelection>> postTimeSelection(@ApiParam(value = "Add time selection for the questionare, which has the given id.", required = true) @PathVariable("questionareId") Long questionareId, @ApiParam(value = "The time selection as an HTTP body.", required = true) @Valid @RequestBody TimeSelection body) {
		body.setId(null);
		Long id = _service.createTimeSelection(questionareId, body);
		if (id == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		List<TimeSelection> list = new ArrayList<>(1);
		TimeSelection ts = new TimeSelection();
		ts.setId(id);
		list.add(ts);

		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<QuestionareUser>> postUser(@ApiParam(value = "Update questionare, which has the given id.", required = true) @PathVariable("questionareId") Long questionareId, @ApiParam(value = "The user as an HTTP body.", required = true) @Valid @RequestBody QuestionareUser body) {
		body.setId(null);
		Long id = _service.createUser(questionareId, body);
		if (id == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		List<QuestionareUser> list = new ArrayList<>(1);
		QuestionareUser u = new QuestionareUser();
		u.setId(id);
		list.add(u);

		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<Questionare>> putQuestionare(@ApiParam(value = "Update questionare, which has the given id.", required = true) @PathVariable("questionareId") Long questionareId, @ApiParam(value = "The questionare as an HTTP body.", required = true) @Valid @RequestBody Questionare body) {
		body.setId(questionareId);
		_service.updateQuestionare(body);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
