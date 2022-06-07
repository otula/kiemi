package tuni.data.api;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import tuni.data.model.FileDetail;
import tuni.data.service.FileService;

/**
 * 
 * 
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-09-03T13:57:44.867474+03:00[Europe/Helsinki]")
@Controller
@Api(tags = { "files" })
public class FilesApiController implements FilesApi {
	@Autowired
	private FileService _service = null;

	/**
	 * 
	 * @param objectMapper
	 * @param request
	 */
	@org.springframework.beans.factory.annotation.Autowired
	public FilesApiController(ObjectMapper objectMapper, HttpServletRequest request) {
		// nothing needed
	}

	@Override
	public ResponseEntity<Void> deleteFile(@ApiParam(value = "Delete file, which has the given id.", required = true) @PathVariable("fileId") Long fileId) {
		if(_service.deleteFile(fileId)) {
			return new ResponseEntity<>(HttpStatus.OK);
		}else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@Override
	public ResponseEntity<Resource> getFile(@ApiParam(value = "Download file, which has the given id.", required = true) @PathVariable("fileId") Long fileId) {
		java.io.File file = _service.getFile(fileId);
		if(file == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(new FileSystemResource(file), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<FileDetail>> getFiles(@ApiParam(value = "Return specified maximum number of results (>=1).") @Valid @RequestParam(value = "max_results", required = false) Integer maxResults, @ApiParam(value = "Start listing from the given page (>=0).") @Valid @RequestParam(value = "start_page", required = false) Integer startPage) {
		return new ResponseEntity<>(_service.getFiles(startPage, maxResults), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<FileDetail>> postFile(@ApiParam(value = "The file as an HTTP body.", required = true) @Valid @RequestBody byte[] body) {
		FileDetail file = _service.createFile(body);
		if(file == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		List<FileDetail> files = new ArrayList<>(1);
		files.add(file);
		return new ResponseEntity<>(files, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<FileDetail>> putFile(@ApiParam(value = "Replace file, which has the given id.", required = true) @PathVariable("fileId") Long fileId, @ApiParam(value = "The file as an HTTP body.", required = true) @Valid @RequestBody byte[] body) {
		if(_service.replaceFile(body, fileId)) {
			FileDetail file = new FileDetail();
			file.setId(fileId);
			List<FileDetail> files = new ArrayList<>(1);
			files.add(file);
			return new ResponseEntity<>(files, HttpStatus.OK);
		}else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
