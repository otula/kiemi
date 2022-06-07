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
package tuni.sites.api;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
import tuni.sites.model.Layer;
import tuni.sites.model.Site;
import tuni.sites.service.SitesService;

/**
 * 
 * 
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-06-18T18:21:36.637143+03:00[Europe/Helsinki]")
@Controller
@Api(tags = { "sites" })
public class SitesApiController implements SitesApi {
	@Autowired
	private SitesService _sitesService = null;

	/**
	 * 
	 * @param objectMapper
	 * @param request
	 */
	@org.springframework.beans.factory.annotation.Autowired
	public SitesApiController(ObjectMapper objectMapper, HttpServletRequest request) {
		// nothing needed
	}

	@Override
	public ResponseEntity<Void> deleteSiteById(@ApiParam(value = "Delete the site, which has the given id.", required = true) @PathVariable("siteId") Long siteId) {
		_sitesService.deleteSite(siteId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<Layer>> getLayers(@ApiParam(value = "return the site, which has the given id.", required = true) @PathVariable("siteId") Long siteId, @ApiParam(value = "Return specified maximum number of results (>=1).") @Valid @RequestParam(value = "max_results", required = false) Integer maxResults, @ApiParam(value = "Start listing from the given page (>=0).") @Valid @RequestParam(value = "start_page", required = false) Integer startPage) {
		return new ResponseEntity<>(_sitesService.getLayers(siteId, maxResults, startPage), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<Site>> getSiteById(@ApiParam(value = "Return site, which has the given id.", required = true) @PathVariable("siteId") Long siteId) {
		Site site = _sitesService.getSite(siteId);
		List<Site> sites = new ArrayList<>(1);
		if(site != null) {
			sites.add(site);
		}
		return new ResponseEntity<>(sites, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<Site>> getSites(@ApiParam(value = "Return specified maximum number of results (>=1).") @Valid @RequestParam(value = "max_results", required = false) Integer maxResults, @ApiParam(value = "Start listing from the given page (>=0).") @Valid @RequestParam(value = "start_page", required = false) Integer startPage) {
		return new ResponseEntity<>(_sitesService.getSites(maxResults, startPage), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<Layer>> postLayer(@ApiParam(value = "The new layer to create as an HTTP body.", required = true) @Valid @RequestBody Layer body, @ApiParam(value = "Update site, which has the given id.", required = true) @PathVariable("siteId") Long siteId) {
		body.setId(null);
		Long id = _sitesService.createLayer(body, siteId);
		List<Layer> layers = new ArrayList<>(1);
		if(id != null) {
			body.setId(id);
			layers.add(body);
		}
		return new ResponseEntity<>(layers, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<Site>> postSite(@ApiParam(value = "The site to create as an HTTP body.", required = true) @Valid @RequestBody Site body) {
		body.setId(null);
		Long id = _sitesService.createSite(body);
		List<Site> sites = new ArrayList<>(1);
		if(id != null) {
			body.setId(id);
			sites.add(body);
		}
		return new ResponseEntity<>(sites, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Void> putSiteById(@ApiParam(value = "The updated site data as an HTTP body.", required = true) @Valid @RequestBody Site body, @ApiParam(value = "Update site, which has the given id.", required = true) @PathVariable("siteId") Long siteId) {
		body.setId(siteId);
		_sitesService.updateSite(body);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
