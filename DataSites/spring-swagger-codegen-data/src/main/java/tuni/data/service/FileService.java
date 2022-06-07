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
package tuni.data.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicLong;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import tuni.data.model.FileDetail;

/**
 * 
 * 
 */
@Service
public class FileService {
	private static final Logger LOGGER = LogManager.getLogger(FileService.class);
	@Value("${files.directory}")
	private String _fileDirectory = null;
	private AtomicLong _currentId = new AtomicLong(System.currentTimeMillis());
	
	/**
	 * 
	 * @param fileId
	 * @return true on success, false if file did not exist
	 */
	public synchronized boolean deleteFile(Long fileId) {
		java.io.File file = new java.io.File(idToFileName(fileId));
		if(file.isFile()) {
			return file.delete();
		}else {
			return false;
		}
	}

	/**
	 * 
	 * @param fileId
	 * @return file handle or null if not found
	 */
	public java.io.File getFile(Long fileId) {
		java.io.File file = new java.io.File(idToFileName(fileId));
		return (file.isFile() ? file : null);
	}
	
	/**
	 * 
	 * @param fileId
	 * @return file path for the file id, note: this does not check file existence
	 */
	private String idToFileName(Long fileId) {
		return _fileDirectory+java.io.File.separator+fileId;
	}

	/**
	 * 
	 * @param startPage
	 * @param maxResults
	 * @return list of files or an empty list
	 */
	public List<FileDetail> getFiles(@Valid Integer startPage, @Valid Integer maxResults) {
		LinkedList<FileDetail> files = new LinkedList<>();
		java.io.File[] fl = (new java.io.File(_fileDirectory)).listFiles();
		long max = Integer.MAX_VALUE;
		if(maxResults != null) {
			if(maxResults < 1) {
				LOGGER.debug("maxResults was < 1.");
				return files;
			}
			max = maxResults;
		}
		long index = 0; // use long to prevent overflows
		if(startPage != null && startPage > 0) {
			if(maxResults == null) { // the first page (0) will be of maximum size, causing no elements ever to be present on the following pages.
				LOGGER.debug("startPage was given without maxResults: no results can be returned.");
				return files;
			}
			index = startPage*maxResults;
		}
		for(int count=0;index<fl.length && count<max;++index,++count) {
			java.io.File entry = fl[(int)index]; // array length in java is never more than int max
			if(entry.isFile()) {
				Long id = null;
				try {
					id = Long.valueOf(entry.getName());
				}catch(NumberFormatException ex) { // in case there happens to be something else inside the directory
					LOGGER.warn(ex.getMessage(), ex);
					continue;
				}
				
				FileDetail file = new FileDetail();
				long modified = entry.lastModified();
				file.setTimestamp(new Date(modified));
				file.setId(id);
				for(ListIterator<FileDetail> iter = files.listIterator();iter.hasNext();) {
					if(modified < iter.next().getTimestamp().getTime()) {
						iter.previous();
						iter.add(file);
						file = null;
						break;
					}
				}
				if(file != null) {
					files.add(file);
				}
			}
		}
		
		return files;
	}

	/**
	 * 
	 * @param file
	 * @return file details for the created file
	 */
	public FileDetail createFile(@Valid byte[] file) {
		return createFile(file, _currentId.incrementAndGet());
	}
	
	/**
	 * 
	 * @param file
	 * @param fileId
	 * @return file details or null on failure
	 */
	private FileDetail createFile(byte[] file, Long fileId) {
		String path = idToFileName(fileId);
		LOGGER.debug("Writing file: "+path);
		try(FileOutputStream fos = new FileOutputStream(path)){
			fos.write(file);
			FileDetail fd = new FileDetail();
			fd.setId(fileId);
			fd.setTimestamp(new Date(new java.io.File(path).lastModified()));
			return fd;
		} catch (IOException ex) {
			LOGGER.error(ex.getMessage(), ex);
			return null;
		}
	}

	/**
	 * 
	 * @param file
	 * @param fileId
	 * @return true on success, failure if the file does not exist
	 */
	public boolean replaceFile(@Valid byte[] file, Long fileId) {
		if(!deleteFile(fileId)) {
			return false;
		}
		
		if(createFile(file, fileId) == null) {
			return false;
		}else {
			return true;
		}
	}
}
