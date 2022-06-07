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
package tuni.sites.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tuni.sites.exception.IdNotFoundException;
import tuni.sites.exception.InvalidParameterException;
import tuni.sites.model.Definitions;
import tuni.sites.model.Layer;
import tuni.sites.model.Site;
import tuni.sites.model.database.SitesRepository;
import tuni.sites.relations.database.SiteToLayerRepository;
import tuni.sites.relations.model.SiteToLayer;

/**
 * Sites service
 * 
 */
@Service
public class SitesService {
	private static final Sort SORT_DEFAULT = Sort.by(Sort.Direction.DESC, Definitions.COLUMN_ID);
	private static final Sort SORT_SITE_TO_LAYER_RELATION = Sort.by(Sort.Direction.DESC, "childId"); // default sort order for site-to-layer relations
	@Autowired
	private LayersService _layersService = null;
	@Autowired
	private SitesRepository _sitesRepository = null;
	@Autowired
	private SiteToLayerRepository _siteToLayerRepository = null;
	
	/**
	 * Delete site and all associated child layers and sensors.
	 * 
	 * @param siteId
	 * @throws IdNotFoundException 
	 */
	@Transactional
	public void deleteSite(Long siteId) throws IdNotFoundException {
		// we could check user permissions here, but there is currently no need
		if(!_sitesRepository.existsById(siteId)) {
			throw new IdNotFoundException("Site id "+siteId+" was not found.");
		}
		
		for(Long layerId : _siteToLayerRepository.findAllLayerIdbySiteId(siteId)) {
			_layersService.deleteLayer(layerId);
			 // no need to clean up site-to-layer relations, the layers will clean up their own relations
		}
			
		_sitesRepository.deleteById(siteId);
	}

	/**
	 * 
	 * @param siteId
	 * @param startPage if given, max results must also be given
	 * @param maxResults 
	 * @return list of layers
	 * @throws IdNotFoundException 
	 * @throws InvalidParameterException 
	 */
	@Transactional
	public List<Layer> getLayers(Long siteId, Integer maxResults, Integer startPage) throws IdNotFoundException, InvalidParameterException {
		// we could check user permissions here, but there is currently no need
		if(!_sitesRepository.existsById(siteId)) {
			throw new IdNotFoundException("Site id "+siteId+" was not found.");
		}
		
		List<Long> layerIds = null;
		if(startPage == null || startPage == 0) { // default start page
			if(maxResults == null) { // default sort orders
				layerIds = _siteToLayerRepository.findAllLayerIdbySiteId(siteId, SORT_SITE_TO_LAYER_RELATION);
			}else {
				layerIds = _siteToLayerRepository.findAllLayerIdbySiteId(siteId, PageRequest.of(0, maxResults, SORT_SITE_TO_LAYER_RELATION)).getContent();  
			}
		}else if(maxResults == null) {
			throw new InvalidParameterException("Start page without max result will never return results.");
		}else {
			layerIds = _siteToLayerRepository.findAllLayerIdbySiteId(siteId, PageRequest.of(startPage, maxResults, SORT_SITE_TO_LAYER_RELATION)).getContent();
		}
		
		return (layerIds.isEmpty() ? new ArrayList<>() : _layersService.getLayers(layerIds, SORT_DEFAULT));
	}

	/**
	 * 
	 * @param siteId
	 * @return status and site
	 * @throws IdNotFoundException 
	 */
	public Site getSite(Long siteId) throws IdNotFoundException {
		// we could check user permissions here, but there is currently no need
		Optional<Site> site = _sitesRepository.findById(siteId);
		if(site.isPresent()) {
			return site.get();
		}else {
			throw new IdNotFoundException("Site id "+siteId+" was not found.");
		}
	}

	/**
	 * 
	 * @param maxResults
	 * @param startPage if given, max results must also be given
	 * @return list of sites
	 * @throws InvalidParameterException 
	 */
	public List<Site> getSites(Integer maxResults, Integer startPage) throws InvalidParameterException {
		// we could check user permissions here, but there is currently no need
		
		List<Site> sites = null;
		if(startPage == null || startPage == 0) { // default start page
			if(maxResults == null) { // default sort orders
				sites = _sitesRepository.findAll(SORT_DEFAULT);
			}else {
				sites = _sitesRepository.findAll(PageRequest.of(0, maxResults, SORT_DEFAULT)).getContent();  
			}
		}else if(maxResults == null) {
			throw new InvalidParameterException("Start page without max result will never return results.");
		}else {
			sites = _sitesRepository.findAll(PageRequest.of(startPage, maxResults, SORT_DEFAULT)).getContent();
		}
		return sites;
	}

	/**
	 * 
	 * @param layer
	 * @param siteId
	 * @return id for the created layer
	 * @throws IdNotFoundException 
	 */
	@Transactional
	public Long createLayer(Layer layer, Long siteId) throws IdNotFoundException {
		// we could check user permissions here, but there is currently no need
		if(!_sitesRepository.existsById(siteId)) {
			throw new IdNotFoundException("Site id "+siteId+" was not found.");
		}
		
		Long id = _layersService.createLayer(layer);
		_siteToLayerRepository.save(new SiteToLayer(id, siteId));
		return id;
	}

	/**
	 * 
	 * @param site
	 * @return id for the created site
	 */
	public Long createSite(Site site) {
		// we could check user permissions here, but there is currently no need
		return _sitesRepository.save(site).getId();
	}

	/**
	 * 
	 * @param site
	 * @throws IdNotFoundException 
	 */
	public void updateSite(Site site) throws IdNotFoundException {
		// we could check user permissions here, but there is currently no need
		Long siteId = site.getId();
		if(!_sitesRepository.existsById(siteId)) {
			throw new IdNotFoundException("Site id "+siteId+" was not found.");
		}
		
		_sitesRepository.save(site);
	}
}
