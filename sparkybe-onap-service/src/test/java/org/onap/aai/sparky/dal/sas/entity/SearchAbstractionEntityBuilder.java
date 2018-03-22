/**
 * ============LICENSE_START===================================================
 * SPARKY (AAI UI service)
 * ============================================================================
 * Copyright © 2017 AT&T Intellectual Property.
 * Copyright © 2017 Amdocs
 * All rights reserved.
 * ============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=====================================================
 *
 * ECOMP and OpenECOMP are trademarks
 * and service marks of AT&T Intellectual Property.
 */
package org.onap.aai.sparky.dal.sas.entity;

import java.util.ArrayList;
import java.util.List;

public class SearchAbstractionEntityBuilder {

   
  public static HitEntity getHitSample1() {
 
    HitEntity hitEntity = new HitEntity();
    DocumentEntity doc = new DocumentEntity();

    hitEntity.setDocument(doc);
    hitEntity.setScore("17.073963");

    doc.addContent("entityPrimaryKeyValue", "example-vnf-id-val-4394");
    doc.addContent("entityType",            "vpe");
    doc.addContent("searchTags",            "example-vnf-id-val-4394;example-vnf-name-val-4394;example-vnf-name2-val-4394");
    doc.addContent("link",                  "https://ext1.test.onap.com:8443/aai/v9/network/vpes/vpe/example-vnf-id-val-4394");
    doc.addContent("searchTagIDs",          "0;1;2");
    doc.addContent("lastmodTimestamp",      "2017-04-18T17:20:48.072-0400");

    doc.setUrl("services/search-data-service/v1/search/indexes/entitysearchindex-localhost-ist-apr18/documents/e317a35256717f10e88d1b2c995efcdddfc911bf350c73e37e8afca6dfb11553");
    doc.setEtag("1");


    return hitEntity;

  }
  
  public static HitEntity getHitSample2() {

    HitEntity hitEntity = new HitEntity();
    DocumentEntity doc = new DocumentEntity();

    hitEntity.setDocument(doc);
    hitEntity.setScore("17.073963");

    doc.addContent("entityPrimaryKeyValue", "vpe-vnf-id-team4-11");
    doc.addContent("entityType",            "vpe");
    doc.addContent("searchTags",            "vpe-vnf-id-team4-11;example-vnf-name-val-9512;example-vnf-name2-val-9512");
    doc.addContent("link",                  "https://ext1.test.onap.com:8443/aai/v9/network/vpes/vpe/vpe-vnf-id-team4-11");
    doc.addContent("searchTagIDs",          "0;1;2");
    doc.addContent("lastmodTimestamp",      "2017-04-18T17:20:48.175-0400");

    doc.setUrl("services/search-data-service/v1/search/indexes/entitysearchindex-localhost-ist-apr18/documents/80f6d1a252e047e50e0adbeb90ad30876bb5b63cf70c9dd53f3fe46aeb50c74b");
    doc.setEtag("1");


    return hitEntity;

  }
  
  public static HitEntity getHitSample3() {

    HitEntity hitEntity = new HitEntity();
    DocumentEntity doc = new DocumentEntity();

    hitEntity.setDocument(doc);
    hitEntity.setScore("17.030035");

    doc.addContent("entityPrimaryKeyValue", "example-vnf-id-val-6176");
    doc.addContent("entityType",            "generic-vnf");
    doc.addContent("searchTags",            "example-vnf-id-val-6176;example-vnf-name-val-6176;example-vnf-name2-val-6176");
    doc.addContent("link",                  "https://ext1.test.onap.com:8443/aai/v9/network/generic-vnfs/generic-vnf/example-vnf-id-val-6176");
    doc.addContent("searchTagIDs",          "0;1;2");
    doc.addContent("lastmodTimestamp",      "2017-04-18T17:29:39.889-0400");

    doc.setUrl("services/search-data-service/v1/search/indexes/entitysearchindex-localhost-ist-apr18/documents/8dfd1136f943296508fee11efcda35a0719aa490aa60e9abffecce0b220d8c94");
    doc.setEtag("1");


    return hitEntity;

  }
  
  public static HitEntity getHitSample4() {

    HitEntity hitEntity = new HitEntity();
    DocumentEntity doc = new DocumentEntity();

    hitEntity.setDocument(doc);
    hitEntity.setScore("17.01174");

    doc.addContent("entityPrimaryKeyValue", "vnf-id-team4-11");
    doc.addContent("entityType",            "newvce");
    doc.addContent("searchTags",            "vnf-id-team4-11;example-vnf-name-val-5313;example-vnf-name2-val-5313");
    doc.addContent("link",                  "https://ext1.test.onap.com:8443/aai/v9/network/newvces/newvce/vnf-id-team4-11");
    doc.addContent("searchTagIDs",          "0;1;2");
    doc.addContent("lastmodTimestamp",      "2017-04-18T17:21:08.142-0400");

    doc.setUrl("services/search-data-service/v1/search/indexes/entitysearchindex-localhost-ist-apr18/documents/83dcab92d75b20eb94578039c8cec5e7b6b4717791e3c367d8af5069ce76dc90");
    doc.setEtag("1");


    return hitEntity;

  }
  
  public static HitEntity getHitSample5() {

    HitEntity hitEntity = new HitEntity();
    DocumentEntity doc = new DocumentEntity();

    hitEntity.setDocument(doc);
    hitEntity.setScore("17.01174");

    doc.addContent("entityPrimaryKeyValue", "example-vnf-id2-val-9501");
    doc.addContent("entityType",            "newvce");
    doc.addContent("searchTags",            "example-vnf-id2-val-9501;example-vnf-name-val-9501;example-vnf-name2-val-9501");
    doc.addContent("link",                  "https://ext1.test.onap.com:8443/aai/v9/network/newvces/newvce/example-vnf-id2-val-9501");
    doc.addContent("searchTagIDs",          "0;1;2");
    doc.addContent("lastmodTimestamp",      "2017-04-18T17:21:23.323-0400");

    doc.setUrl("services/search-data-service/v1/search/indexes/entitysearchindex-localhost-ist-apr18/documents/461816ba8aa94d01f2c978999b843dbaf10e0509db58d1945d6f5999d6db8f5e");
    doc.setEtag("1");


    return hitEntity;

  }
  
  public static HitEntity getHitSample6() {

    HitEntity hitEntity = new HitEntity();
    DocumentEntity doc = new DocumentEntity();

    hitEntity.setDocument(doc);
    hitEntity.setScore("17.01174");

    doc.addContent("entityPrimaryKeyValue", "vnf-id-dm-auto-10");
    doc.addContent("entityType",            "vce");
    doc.addContent("searchTags",            "vpe-id-dm-auto-10;vnf-id-dm-auto-10;vnf-name-dm-auto-10;vnf-name2-dm-auto-10");
    doc.addContent("link",                  "https://ext1.test.onap.com:8443/aai/v9/network/vces/vce/vnf-id-dm-auto-10");
    doc.addContent("searchTagIDs",          "0;1;2;3");
    doc.addContent("lastmodTimestamp",      "2017-04-18T17:24:57.209-0400");

    doc.setUrl("services/search-data-service/v1/search/indexes/entitysearchindex-localhost-ist-apr18/documents/1ead4512e65ee0eafb24e0156cc1abdf97368f08dfe065f02580aa09661bbcd8");
    doc.setEtag("1");


    return hitEntity;

  }
  
  public static HitEntity getHitSample7() {

    HitEntity hitEntity = new HitEntity();
    DocumentEntity doc = new DocumentEntity();

    hitEntity.setDocument(doc);
    hitEntity.setScore("13.940832");

    doc.addContent("entityPrimaryKeyValue", "e3e59c5b-ad48-44d0-b3e4-80eacdcee4c7");
    doc.addContent("entityType",            "generic-vnf");
    doc.addContent("searchTags",            "e3e59c5b-ad48-44d0-b3e4-80eacdcee4c7;VNF_Test_vNF_modules_01");
    doc.addContent("link",                  "https://ext1.test.onap.com:8443/aai/v9/network/generic-vnfs/generic-vnf/e3e59c5b-ad48-44d0-b3e4-80eacdcee4c7");
    doc.addContent("searchTagIDs",          "0;1");
    doc.addContent("lastmodTimestamp",      "2017-04-18T17:26:34.603-0400");

    doc.setUrl("services/search-data-service/v1/search/indexes/entitysearchindex-localhost-ist-apr18/documents/1462582e8fd7786f72f26548e4247b72ab6cd101cca0bbb68a60dd3ad16500d0");
    doc.setEtag("1");


    return hitEntity;

  }
  
  public static HitEntity getHitSample8() {

    HitEntity hitEntity = new HitEntity();
    DocumentEntity doc = new DocumentEntity();

    hitEntity.setDocument(doc);
    hitEntity.setScore("13.940832");

    doc.addContent("entityPrimaryKeyValue", "fusion-jitsi-vnf-001");
    doc.addContent("entityType",            "generic-vnf");
    doc.addContent("searchTags",            "fusion-jitsi-vnf-001;fusion-jitsi-vnf");
    doc.addContent("link",                  "https://ext1.test.onap.com:8443/aai/v9/network/generic-vnfs/generic-vnf/fusion-jitsi-vnf-001");
    doc.addContent("searchTagIDs",          "0;1");
    doc.addContent("lastmodTimestamp",      "2017-04-18T17:28:14.293-0400");

    doc.setUrl("services/search-data-service/v1/search/indexes/entitysearchindex-localhost-ist-apr18/documents/b79ddfec9a00184445174c91e7490a0d407f351983bba4ae53bfec0584f73ee3");
    doc.setEtag("1");


    return hitEntity;

  }
  
  public static HitEntity getHitSample9() {

    HitEntity hitEntity = new HitEntity();
    DocumentEntity doc = new DocumentEntity();

    hitEntity.setDocument(doc);
    hitEntity.setScore("13.940832");

    doc.addContent("entityPrimaryKeyValue", "vnfm0003v");
    doc.addContent("entityType",            "generic-vnf");
    doc.addContent("searchTags",            "vnfm0003v;vnfm0003v");
    doc.addContent("link",                  "https://ext1.test.onap.com:8443/aai/v9/network/generic-vnfs/generic-vnf/vnfm0003v");
    doc.addContent("searchTagIDs",          "0;1");
    doc.addContent("lastmodTimestamp",      "2017-04-18T17:29:39.594-0400");

    doc.setUrl("services/search-data-service/v1/search/indexes/entitysearchindex-localhost-ist-apr18/documents/52ae232ea5506d6de8ef35c4f46a1ceafe35f3717ff578b83531bc7615870b12");
    doc.setEtag("1");


    return hitEntity;

  }
  
  public static HitEntity getHitSample10() {

    HitEntity hitEntity = new HitEntity();
    DocumentEntity doc = new DocumentEntity();

    hitEntity.setDocument(doc);
    hitEntity.setScore("13.928098");

    doc.addContent("entityPrimaryKeyValue", "amist456vnf");
    doc.addContent("entityType",            "generic-vnf");
    doc.addContent("searchTags",            "amist456vnf;amist456vnf");
    doc.addContent("link",                  "https://ext1.test.onap.com:8443/aai/v9/network/generic-vnfs/generic-vnf/amist456vnf");
    doc.addContent("searchTagIDs",          "0;1");
    doc.addContent("lastmodTimestamp",      "2017-04-18T17:28:28.163-0400");

    doc.setUrl("services/search-data-service/v1/search/indexes/entitysearchindex-localhost-ist-apr18/documents/3424afea5963696380a0fdc78ee5320cf5fa9bc0459f1f9376db208d31196434");
    doc.setEtag("1");


    return hitEntity;

  }
  
  
  
  public static SearchAbstractionResponse getSuccessfulEntitySearchResponse() {
    
    SearchAbstractionResponse sasResponse = new SearchAbstractionResponse();
    
    SearchResult searchResult = new SearchResult();
    sasResponse.setSearchResult(searchResult);
    
    searchResult.setTotalHits(3257);
    
    List<HitEntity> hits = new ArrayList<HitEntity>();
    
    hits.add(getHitSample1());
    hits.add(getHitSample2());
    hits.add(getHitSample3());
    hits.add(getHitSample4());
    hits.add(getHitSample5());
    hits.add(getHitSample6());
    hits.add(getHitSample7());
    hits.add(getHitSample8());
    hits.add(getHitSample9());
    hits.add(getHitSample10());
    
    searchResult.setHits(hits);
    
    return sasResponse;
    
  }
  

}
