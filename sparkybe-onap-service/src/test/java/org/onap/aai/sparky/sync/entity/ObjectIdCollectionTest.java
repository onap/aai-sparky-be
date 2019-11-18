/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2019 Nokia Intellectual Property. All rights reserved.
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
package org.onap.aai.sparky.sync.entity;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class ObjectIdCollectionTest {

    private ObjectIdCollection objectIdCollection;

    @Before
    public void setUp(){
        objectIdCollection = new ObjectIdCollection();
    }

    @Test
    public void shouldBePossibleToStoreObjectId(){
        // when
        objectIdCollection.addObjectId("1");
        objectIdCollection.addObjectId("2");

        // then
        assertEquals(2, objectIdCollection.getSize());
        assertTrue(objectIdCollection.getImportedObjectIds().contains("1"));
        assertTrue(objectIdCollection.getImportedObjectIds().contains("2"));
    }

    @Test
    public void shouldBePossibleToStoreCollectionOfObjectIds(){
        // given
        List<String> objectIds = Lists.newArrayList("1","2","3");

        // when
        objectIdCollection.addAll(objectIds);

        // then
        assertEquals(3, objectIdCollection.getSize());
        assertTrue(objectIdCollection.getImportedObjectIds().contains("1"));
        assertTrue(objectIdCollection.getImportedObjectIds().contains("2"));
        assertTrue(objectIdCollection.getImportedObjectIds().contains("3"));
    }

    @Test
    public void shouldBePossibleToClearObjectIds(){
        // given
        List<String> objectIds = Lists.newArrayList("1","2","3");
        objectIdCollection.addAll(objectIds);

        // when
        objectIdCollection.clear();

        // then
        assertEquals(0, objectIdCollection.getSize());
    }

}
