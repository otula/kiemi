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
package tuni.feedback.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tuni.feedback.database.AnswerRepository;
import tuni.feedback.database.LocationRepository;
import tuni.feedback.database.QuestionRepository;
import tuni.feedback.database.QuestionareRepository;
import tuni.feedback.database.QuestionareUserRepository;
import tuni.feedback.database.TimeSelectionRepository;
import tuni.feedback.exception.ForbiddenException;
import tuni.feedback.exception.IdNotFoundException;
import tuni.feedback.exception.InvalidParameterException;
import tuni.feedback.model.Answer;
import tuni.feedback.model.Question;
import tuni.feedback.model.QuestionAnswer;
import tuni.feedback.model.Questionare;
import tuni.feedback.model.QuestionareUser;
import tuni.feedback.model.ReportTimestamp;
import tuni.feedback.model.TimeSelection;
import tuni.feedback.model.converters.TimeConverter;
import tuni.feedback.permissions.model.UserPermission.Permission;
import tuni.feedback.permissions.service.PermissionService;
import tuni.feedback.relations.database.LocationToAreaRepository;
import tuni.feedback.relations.database.QuestionareToAnswerRepository;
import tuni.feedback.relations.database.QuestionareToQuestionRepository;
import tuni.feedback.relations.database.QuestionareToTimeSelectionRepository;
import tuni.feedback.relations.database.QuestionareToUserRepository;
import tuni.feedback.relations.model.QuestionareToAnswer;
import tuni.feedback.relations.model.QuestionareToQuestion;
import tuni.feedback.relations.model.QuestionareToTimeSelection;
import tuni.feedback.relations.model.QuestionareToUser;


/**
 * 
 * 
 */
@Service
public class QuestionaresService {
	private static final Logger LOGGER = LogManager.getLogger(QuestionaresService.class);
	@Autowired
	private PermissionService _permissionService = null;
	@Autowired
	private LocationRepository _locationRepository = null;
	@Autowired
	private QuestionareRepository _questionareRepository = null;
	@Autowired
	private QuestionRepository _questionRepository = null;
	@Autowired
	private QuestionareToQuestionRepository _questionareToQuestionRepository = null;
	@Autowired
	private QuestionareUserRepository _userRepository = null;
	@Autowired
	private QuestionareToUserRepository _questionareToUserRepository = null;
	@Autowired
	private AnswerRepository _answerRepository = null;
	@Autowired
	private QuestionareToAnswerRepository _questionareToAnswerRepository = null;
	@Autowired
	private TimeSelectionRepository _timeSelectionRepository = null;
	@Autowired
	private QuestionareToTimeSelectionRepository _questionareToTimeSelectionRepository = null;
	@Autowired
	private LocationToAreaRepository _locationToAreaRepository = null;
	@Deprecated
	private Comparator<ReportTimestamp> _reportTimestampComparator = new Comparator<>() { // TODO the current implementation of Answer class does not permit mixing @OrderBy and @OrderColumn, preventing database level sorting of report timestamps, this should be fixed. For now, let's re-sort in this service.
		@Override
		public int compare(ReportTimestamp rt1, ReportTimestamp rt2) {
			long first = rt1.getStartUtcSeconds();
			long second = rt2.getStartUtcSeconds();
			if(first < second) {
				return -1;
			}else if(first == second) {
				return 0;
			}else {
				return 1;
			}
		}
	};

	/**
	 * 
	 * @param questionareId
	 * @param answerId
	 * @throws IdNotFoundException 
	 * @throws ForbiddenException 
	 */
	@Transactional
	public void deleteAnswer(Long questionareId, Long answerId) throws IdNotFoundException, ForbiddenException {
		if(!_permissionService.hasQuestionarePermission(questionareId, Permission.FULL)) {
			throw new ForbiddenException("Permission was denied.");
		}
		if(!_questionareRepository.existsById(questionareId)) {
			throw new IdNotFoundException("Questionare id: "+questionareId+" was not found.");
		}
		if(!_answerRepository.existsById(answerId)) {
			throw new IdNotFoundException("Answer id: "+answerId+" was not found.");
		}
		_questionareToAnswerRepository.deleteByAnswerId(answerId);
		_answerRepository.deleteById(answerId);
	}

	/**
	 * 
	 * @param questionareId
	 * @param questionId
	 * @throws IdNotFoundException 
	 * @throws ForbiddenException 
	 */
	@Transactional
	public void deleteQuestion(Long questionareId, Long questionId) throws IdNotFoundException, ForbiddenException {
		if(!_permissionService.hasQuestionarePermission(questionareId, Permission.FULL)) {
			throw new ForbiddenException("Permission was denied.");
		}
		if(!_questionareRepository.existsById(questionareId)) {
			throw new IdNotFoundException("Questionare id: "+questionareId+" was not found.");
		}
		if(!_questionRepository.existsById(questionId)) {
			throw new IdNotFoundException("Question id: "+questionId+" was not found.");
		}
		_questionareToQuestionRepository.deleteByQuestionId(questionId);
		_questionRepository.deleteById(questionId);  //TODO the question may be referred by a questionare answer
	}

	/**
	 * 
	 * @param questionareId
	 * @throws IdNotFoundException 
	 * @throws ForbiddenException 
	 */
	@Transactional
	public void deleteQuestionare(Long questionareId) throws IdNotFoundException, ForbiddenException {
		if(!_permissionService.hasQuestionarePermission(questionareId, Permission.FULL)) {
			throw new ForbiddenException("Permission was denied.");
		}
		if(!_questionareRepository.existsById(questionareId)) {
			throw new IdNotFoundException("Questionare id: "+questionareId+" was not found.");
		}
		
		List<Long> ids = _questionareToQuestionRepository.findAllQuestionIdbyQuestionareId(questionareId);
		if(ids != null && !ids.isEmpty()) {
			for(Long id : ids) {
				_questionRepository.deleteById(id);
			}
			_questionareToQuestionRepository.deleteByQuestionareId(questionareId);
		}
		
		ids = _questionareToUserRepository.findAllUserIdbyQuestionareId(questionareId);
		if(ids != null && !ids.isEmpty()) {
			for(Long id : ids) {
				_userRepository.deleteById(id);
			}
			_questionareToUserRepository.deleteByQuestionareId(questionareId);
		}
		
		ids = _questionareToAnswerRepository.findAllAnswerIdbyQuestionareId(questionareId);
		if(ids != null && !ids.isEmpty()) {
			for(Long id : ids) {
				_answerRepository.deleteById(id);
			}
			_questionareToAnswerRepository.deleteByQuestionareId(questionareId);
		}
		
		ids = _questionareToTimeSelectionRepository.findAllTimeSelectionIdbyQuestionareId(questionareId);
		if(ids != null && !ids.isEmpty()) {
			for(Long id : ids) {
				_timeSelectionRepository.deleteById(id);
			}
			_questionareToTimeSelectionRepository.deleteByQuestionareId(questionareId);
		}
		
		_questionareRepository.deleteById(questionareId);
	}

	/**
	 * 
	 * @param questionareId
	 * @param timeSelectionId
	 * @throws IdNotFoundException 
	 * @throws ForbiddenException 
	 */
	@Transactional
	public void deleteTimeSelection(Long questionareId, Long timeSelectionId) throws IdNotFoundException, ForbiddenException {
		if(!_permissionService.hasQuestionarePermission(questionareId, Permission.FULL)) {
			throw new ForbiddenException("Permission was denied.");
		}
		if(!_questionareRepository.existsById(questionareId)) {
			throw new IdNotFoundException("Questionare id: "+questionareId+" was not found.");
		}
		if(!_timeSelectionRepository.existsById(timeSelectionId)) {
			throw new IdNotFoundException("Time selection id: "+timeSelectionId+" was not found.");
		}
		_questionareToTimeSelectionRepository.deleteByTimeSelectionId(timeSelectionId);
		_timeSelectionRepository.deleteById(timeSelectionId);
	}

	/**
	 * 
	 * @param questionareId
	 * @param to 
	 * @param from 
	 * @param endTime report end time in seconds since UTC midnights
	 * @param startTime 
	 * @return answers
	 * @throws IdNotFoundException 
	 * @throws ForbiddenException 
	 */
	@Transactional
	public List<Answer> getAnswers(Long questionareId, Date from, Date to, Long startTime, Long endTime) throws IdNotFoundException, ForbiddenException {
		if(!_permissionService.hasQuestionarePermission(questionareId, Permission.FULL)) {
			throw new ForbiddenException("Permission was denied.");
		}
		
		if(!_questionareRepository.existsById(questionareId)) {
			throw new IdNotFoundException("Questionare id: "+questionareId+" was not found.");
		}
		
		List<Long> answerIds = _questionareToAnswerRepository.findAllAnswerIdbyQuestionareId(questionareId, tuni.feedback.relations.Definitions.SORT_PARENT_TO_CHILD_RELATION);
		List<Answer> answers = null;
		if(from == null) {
			if(to == null) {
				answers = (answerIds.isEmpty() ? new ArrayList<>() : _answerRepository.findAllByIdIn(answerIds, AnswerRepository.DEFAULT_SORT));
			}else {
				answers = (answerIds.isEmpty() ? new ArrayList<>() : _answerRepository.findAllBySubmitTimestampToAndIdIn(answerIds, to.getTime(), AnswerRepository.DEFAULT_SORT));				
			}
		}else if(to == null) {
			answers = (answerIds.isEmpty() ? new ArrayList<>() : _answerRepository.findAllBySubmitTimestampFromAndIdIn(answerIds, from.getTime(), AnswerRepository.DEFAULT_SORT));		
		}else { // from != null && to != null
			answers = (answerIds.isEmpty() ? new ArrayList<>() : _answerRepository.findAllBySubmitTimestampFromToAndIdIn(answerIds, from.getTime(), to.getTime(), AnswerRepository.DEFAULT_SORT));
		}
		
		if(answers.isEmpty() || (startTime == null && endTime == null)) {
			for(Answer a : answers) {
				Collections.sort(a.getReportTimestamp(), _reportTimestampComparator);
			}
			return answers;
		}
		
		List<Answer> results = new ArrayList<>(answers.size());
		for(Answer a : answers) { // TODO should implement this with join from report_timestamp table
			boolean inRange = false;
			List<ReportTimestamp> rTimestamps = a.getReportTimestamp();
			for(ReportTimestamp rts : rTimestamps) {
				if(startTime == null || rts.getStartUtcSeconds() >= startTime) {
					inRange = true;
				}else {
					continue;
				}
				if(endTime == null || rts.getStartUtcSeconds() <= endTime) {
					break;
				}else {
					inRange = false;
					continue;
				}
			}
			if(inRange) {
				results.add(a);	
				Collections.sort(rTimestamps, _reportTimestampComparator);
			}
		}
		
		return results;
	}

	/**
	 * 
	 * @param questionareId
	 * @return questionare
	 * @throws IdNotFoundException 
	 * @throws ForbiddenException 
	 */
	@Transactional
	public Questionare getQuestionare(Long questionareId) throws IdNotFoundException, ForbiddenException {
		if(!_permissionService.hasQuestionarePermission(questionareId, Permission.READ_ONLY)) {
			throw new ForbiddenException("Permission was denied.");
		}
		return getQuestionareNoPermissionCheck(questionareId);
	}
	
	/**
	 * 
	 * @param questionareId
	 * @return questionare
	 * @throws IdNotFoundException
	 */
	private Questionare getQuestionareNoPermissionCheck(Long questionareId) throws IdNotFoundException {
		Optional<Questionare> q = _questionareRepository.findById(questionareId);
		if(q.isPresent()) {
			return q.get();
		}else {
			throw new IdNotFoundException("Questionare id: "+questionareId+" was not found.");
		}
	}

	/**
	 * 
	 * @param maxResults
	 * @param startPage
	 * @return questionares
	 * @throws InvalidParameterException 
	 */
	@Transactional
	public List<Questionare> getQuestionares(@Valid Integer maxResults, @Valid Integer startPage) throws InvalidParameterException {
		if(startPage != null && startPage != 0 && maxResults == null) {
			throw new InvalidParameterException("Start page without max result will never return results.");
		}
		
		List<Long> questionareIds = null;
		if(!_permissionService.isAdmin()) {
			questionareIds = _permissionService.getPermittedQuestionareIds();
			if(questionareIds == null) {
				LOGGER.debug("Authenticated user has no questionare permissions.");
				return new ArrayList<>(0);
			}
		}
		
		return _questionareRepository.findAllByQuestionareId(questionareIds, maxResults, startPage);
	}

	/**
	 * 
	 * @param questionareId
	 * @param maxResults
	 * @param startPage
	 * @return questions
	 * @throws IdNotFoundException 
	 * @throws InvalidParameterException 
	 * @throws ForbiddenException 
	 */
	public List<Question> getQuestions(Long questionareId, @Valid Integer maxResults, @Valid Integer startPage) throws IdNotFoundException, InvalidParameterException, ForbiddenException {
		if(!_permissionService.hasQuestionarePermission(questionareId, Permission.READ_ONLY)) {
			throw new ForbiddenException("Permission was denied.");
		}
		
		if(!_questionareRepository.existsById(questionareId)) {
			throw new IdNotFoundException("Questionare id: "+questionareId+" was not found.");
		}
		
		List<Long> questionIds = null;
		if(startPage == null || startPage == 0) { // default start page
			if(maxResults == null) { // default sort orders
				questionIds = _questionareToQuestionRepository.findAllQuestionIdbyQuestionareId(questionareId, tuni.feedback.relations.Definitions.SORT_PARENT_TO_CHILD_RELATION);
			}else {
				questionIds = _questionareToQuestionRepository.findAllQuestionIdbyQuestionareId(questionareId, PageRequest.of(0, maxResults, tuni.feedback.relations.Definitions.SORT_PARENT_TO_CHILD_RELATION)).getContent();  
			}
		}else if(maxResults == null) {
			throw new InvalidParameterException("Start page without max result will never return results.");
		}else {
			questionIds = _questionareToQuestionRepository.findAllQuestionIdbyQuestionareId(questionareId, PageRequest.of(startPage, maxResults, tuni.feedback.relations.Definitions.SORT_PARENT_TO_CHILD_RELATION)).getContent();
		}
		
		return (questionIds.isEmpty() ? new ArrayList<>() : _questionRepository.findAllByIdIn(questionIds, QuestionRepository.DEFAULT_SORT));
	}

	/**
	 * 
	 * @param questionareId
	 * @param maxResults
	 * @param startPage
	 * @return time selections
	 * @throws IdNotFoundException 
	 * @throws ForbiddenException 
	 */
	public List<TimeSelection> getTimeSelections(Long questionareId, @Valid Integer maxResults, @Valid Integer startPage) throws IdNotFoundException, ForbiddenException {
		if(!_permissionService.hasQuestionarePermission(questionareId, Permission.READ_ONLY)) {
			throw new ForbiddenException("Permission was denied.");
		}
		
		if(!_questionareRepository.existsById(questionareId)) {
			throw new IdNotFoundException("Questionare id: "+questionareId+" was not found.");
		}
		
		List<Long> tsIds = null;
		if(startPage == null || startPage == 0) { // default start page
			if(maxResults == null) { // default sort orders
				tsIds = _questionareToTimeSelectionRepository.findAllTimeSelectionIdbyQuestionareId(questionareId, tuni.feedback.relations.Definitions.SORT_PARENT_TO_CHILD_RELATION);
			}else {
				tsIds = _questionareToTimeSelectionRepository.findAllTimeSelectionIdbyQuestionareId(questionareId, PageRequest.of(0, maxResults, tuni.feedback.relations.Definitions.SORT_PARENT_TO_CHILD_RELATION)).getContent();  
			}
		}else if(maxResults == null) {
			throw new InvalidParameterException("Start page without max result will never return results.");
		}else {
			tsIds = _questionareToTimeSelectionRepository.findAllTimeSelectionIdbyQuestionareId(questionareId, PageRequest.of(startPage, maxResults, tuni.feedback.relations.Definitions.SORT_PARENT_TO_CHILD_RELATION)).getContent();
		}
		
		return (tsIds.isEmpty() ? new ArrayList<>() : _timeSelectionRepository.findAllByIdIn(tsIds, TimeSelectionRepository.DEFAULT_SORT));
	}

	/**
	 * 
	 * @param questionareId
	 * @param maxResults
	 * @param startPage
	 * @return users
	 * @throws IdNotFoundException 
	 * @throws InvalidParameterException 
	 * @throws ForbiddenException 
	 */
	public List<QuestionareUser> getUsers(Long questionareId, @Valid Integer maxResults, @Valid Integer startPage) throws IdNotFoundException, InvalidParameterException, ForbiddenException {
		if(!_permissionService.hasQuestionarePermission(questionareId, Permission.READ_ONLY)) {
			throw new ForbiddenException("Permission was denied.");
		}
		
		if(!_questionareRepository.existsById(questionareId)) {
			throw new IdNotFoundException("Questionare id: "+questionareId+" was not found.");
		}
		
		List<Long> userIds = null;
		if(startPage == null || startPage == 0) { // default start page
			if(maxResults == null) { // default sort orders
				userIds = _questionareToUserRepository.findAllUserIdbyQuestionareId(questionareId, tuni.feedback.relations.Definitions.SORT_PARENT_TO_CHILD_RELATION);
			}else {
				userIds = _questionareToUserRepository.findAllUserIdbyQuestionareId(questionareId, PageRequest.of(0, maxResults, tuni.feedback.relations.Definitions.SORT_PARENT_TO_CHILD_RELATION)).getContent();  
			}
		}else if(maxResults == null) {
			throw new InvalidParameterException("Start page without max result will never return results.");
		}else {
			userIds = _questionareToUserRepository.findAllUserIdbyQuestionareId(questionareId, PageRequest.of(startPage, maxResults, tuni.feedback.relations.Definitions.SORT_PARENT_TO_CHILD_RELATION)).getContent();
		}
		
		return (userIds.isEmpty() ? new ArrayList<>() : _userRepository.findAllByIdIn(userIds, QuestionareUserRepository.DEFAULT_SORT));
	}

	/**
	 * 
	 * @param questionareId
	 * @param answer
	 * @return id for the created answer
	 * @throws IdNotFoundException 
	 * @throws InvalidParameterException
	 * @throws ForbiddenException 
	 */
	@Transactional
	public Long createAnswer(Long questionareId, @Valid Answer answer) throws IdNotFoundException, InvalidParameterException, ForbiddenException {
		Date submitTimestamp = answer.getSubmitTimestamp();
		if(submitTimestamp == null) {
			answer.setSubmitTimestamp(new Date());
		}
		
		List<QuestionAnswer> answers = answer.getQuestionAnswer();
		if(answers == null || answers.isEmpty()) {
			throw new InvalidParameterException("Answer list was empty.");
		}
		
		if(!_permissionService.hasQuestionarePermission(questionareId, Permission.SUBMIT)) {
			throw new ForbiddenException("Permission was denied.");
		}
		Questionare questionare = getQuestionareNoPermissionCheck(questionareId); // this may throw IdNotFoundException
		
		if(questionare.isUseServiceUsers()) {
			answer.setUserId(_permissionService.getAuthenticatedUserId());
		}else { // ignore users (if any) for questionares that require the use of service level users
			Long userId = answer.getUserId();
			if(userId != null && !_questionareToUserRepository.existsByParentIdAndChildId(questionareId, userId)) {
				throw new IdNotFoundException("User id: "+userId+" was not found.");
			}
		}
		
		for(QuestionAnswer qa : answers) {
			Long questionId = qa.getQuestionId();
			if(!_questionareToQuestionRepository.existsByParentIdAndChildId(questionareId, questionId)) {
				throw new IdNotFoundException("Question id: "+questionId+" was not found.");
			}
		}
		
		List<Long> areaIds = answer.getAreaId();
		if(areaIds != null && !areaIds.isEmpty()) {
			Long locationId = questionare.getLocationId();
			if(locationId == null) {
				throw new InvalidParameterException("Questionare, id: "+questionareId+" does not have location. No area ids can be given.");
			}
			
			for(Long areaId : areaIds) {
				if(!_locationToAreaRepository.existsByParentIdAndChildId(locationId, areaId)) {
					throw new IdNotFoundException("Area id: "+areaId+" was not found.");
				}
			}
		}
		
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		for(ReportTimestamp rts : answer.getReportTimestamp()) { // calculate utc seconds
			Date start = rts.getStart();
			Date end = rts.getEnd();
			if(end.before(start)) {
				throw new InvalidParameterException("End time for report timestamp is before start time.");
			}
			c.setTime(start);
			rts.setStartUtcSeconds(TimeConverter.timeToSeconds(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND)));
			c.setTime(end);
			rts.setEndUtcSeconds(TimeConverter.timeToSeconds(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND)));
		}
		
		Long id = _answerRepository.save(answer).getId();
		_questionareToAnswerRepository.save(new QuestionareToAnswer(id, questionareId));
		return id;
	}

	/**
	 * 
	 * @param questionareId
	 * @param question
	 * @return id for the created question
	 * @throws IdNotFoundException 
	 * @throws ForbiddenException 
	 */
	@Transactional
	public Long createQuestion(Long questionareId, @Valid Question question) throws IdNotFoundException, ForbiddenException {
		if(!_permissionService.hasQuestionarePermission(questionareId, Permission.FULL)) {
			throw new ForbiddenException("Permission was denied.");
		}
		if(!_questionareRepository.existsById(questionareId)) {
			throw new IdNotFoundException("Questionare id: "+questionareId+" was not found.");
		}
		Long id = _questionRepository.save(question).getId();
		_questionareToQuestionRepository.save(new QuestionareToQuestion(id, questionareId));
		return id;
	}

	/**
	 * 
	 * @param questionare
	 * @return id for the created questionare
	 * @throws IdNotFoundException 
	 * @throws ForbiddenException 
	 */
	@Transactional
	public Long createQuestionare(@Valid Questionare questionare) throws IdNotFoundException, ForbiddenException {
		if(!_permissionService.isAdmin()) {
			throw new ForbiddenException("Permission was denied.");
		}
		Long locationId = questionare.getLocationId();
		if(locationId != null && !_locationRepository.existsById(locationId)) {
			throw new IdNotFoundException("Location id: "+locationId+" was not found.");
		}
		return _questionareRepository.save(questionare).getId();
	}

	/**
	 * 
	 * @param questionareId
	 * @param timeSelection
	 * @return id for the created time selection
	 * @throws IdNotFoundException 
	 * @throws ForbiddenException 
	 * @throws InvalidParameterException 
	 */
	@Transactional
	public Long createTimeSelection(Long questionareId, @Valid TimeSelection timeSelection) throws IdNotFoundException, ForbiddenException, InvalidParameterException {
		if(!_permissionService.hasQuestionarePermission(questionareId, Permission.FULL)) {
			throw new ForbiddenException("Permission was denied.");
		}
		if(!_questionareRepository.existsById(questionareId)) {
			throw new IdNotFoundException("Questionare id: "+questionareId+" was not found.");
		}
		
		if(!TimeConverter.isValid(timeSelection.getStart())) {
			throw new InvalidParameterException("Invalid start time.");
		}
		
		if(!TimeConverter.isValid(timeSelection.getEnd())) {
			throw new InvalidParameterException("Invalid end time.");
		}
		
		Long id = _timeSelectionRepository.save(timeSelection).getId();
		_questionareToTimeSelectionRepository.save(new QuestionareToTimeSelection(id, questionareId));
		return id;
	}

	/**
	 * 
	 * @param questionareId
	 * @param user
	 * @return id for the created user
	 * @throws IdNotFoundException 
	 * @throws ForbiddenException 
	 * @throws InvalidParameterException 
	 */
	@Transactional
	public Long createUser(Long questionareId, @Valid QuestionareUser user) throws IdNotFoundException, ForbiddenException, InvalidParameterException {
		if(!_permissionService.hasQuestionarePermission(questionareId, Permission.SUBMIT)) {
			throw new ForbiddenException("Permission was denied.");
		}
		
		Questionare found = getQuestionare(questionareId);
		if(found == null) {
			throw new IdNotFoundException("Questionare id: "+questionareId+" was not found.");
		}
		if(found.isUseServiceUsers()) {
			throw new InvalidParameterException("Questionare's user policy prevents creating new questionare level users.");
		}
		
		Long id = _userRepository.save(user).getId();
		_questionareToUserRepository.save(new QuestionareToUser(id, questionareId));
		return id;
	}

	/**
	 * 
	 * @param questionare
	 * @throws IdNotFoundException 
	 * @throws ForbiddenException 
	 * @throws InvalidParameterException 
	 */
	@Transactional
	public void updateQuestionare(@Valid Questionare questionare) throws IdNotFoundException, ForbiddenException, InvalidParameterException {
		Long questionareId = questionare.getId();
		if(!_permissionService.hasQuestionarePermission(questionareId, Permission.FULL)) {
			throw new ForbiddenException("Permission was denied.");
		}
		
		Optional<Questionare> found = _questionareRepository.findById(questionareId);
		if(found.isEmpty()) {
			throw new IdNotFoundException("Questionare id: "+questionareId+" was not found.");
		}
		
		if(!found.get().isUseServiceUsers().equals(questionare.isUseServiceUsers())) {
			throw new InvalidParameterException("Not allowed to change user policy for existing questionare.");
		}
		_questionareRepository.save(questionare);
	}
}
