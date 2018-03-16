/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017-2018 Amdocs
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */
package org.onap.aai.sparky.viewandinspect.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.onap.aai.sparky.sync.entity.SearchableEntity;

import java.util.Set;

public class SearchableEntityList {

    private List<SearchableEntity> entities;
    
    public SearchableEntityList() {
        entities = new ArrayList<SearchableEntity>();
    }

    public List<SearchableEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<SearchableEntity> entities) {
        this.entities = entities;
    }
    
    public void addEntity(SearchableEntity entity) {
        
        if ( !entities.contains(entity)) {
            entities.add(entity);
        }
        
    }

    protected static SearchableEntity buildEntity(String entityType, String pkeyValue, String link, Map<String,String> searchTags ) {
        
        SearchableEntity se = new SearchableEntity();
        
        se.setEntityType(entityType);
        se.setEntityPrimaryKeyValue(pkeyValue);
        se.setLink(link);
        
        if ( searchTags != null) {
            
            Set<Entry<String, String>> entrySet = searchTags.entrySet();
            
            for ( Entry<String, String> entry : entrySet ) {
                se.addSearchTagWithKey(entry.getKey(), entry.getValue());
            }
        }
        
        se.deriveFields();
        
        return se;
        
    }
    
    protected static Map<String,String> getSearchTagMap(String... tags) {
        
        HashMap<String,String> dataMap = new HashMap<String,String>();
        
        if ( tags != null && tags.length >= 2 ) {
            
            int numTags = tags.length;
            int index = 0;
            
            while ( index < numTags ) {
                
                if ( index + 1 < numTags ) {
                    // we have enough parameters for the current set
                    dataMap.put(tags[index], tags[index+1]);
                    index += 2;
                } else {
                    break;
                }
            }
            
        }
        
        return dataMap;
        
        
    }

    @Override
    public String toString() {
        return "SearchableEntityList [" + (entities != null ? "entities=" + entities : "") + "]";
    }

}