/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.slim3.datastore;

import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import org.junit.Test;
import org.slim3.datastore.model.Hoge;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author higa
 */
public class StringUnindexedAttributeMetaTest {

    private ModelMeta<Hoge> meta = new ModelMeta<Hoge>("Hoge", Hoge.class) {

        @Override
        protected void setKey(Object model, Key key) {
        }

        @Override
        public Entity modelToEntity(Object model) {
            return null;
        }

        @Override
        protected void incrementVersion(Object model) {
        }

        @Override
        protected void prePut(Object model) {
        }

        @Override
        protected long getVersion(Object model) {
            return 0;
        }

        @Override
        protected Key getKey(Object model) {
            return null;
        }

        @Override
        public Hoge entityToModel(Entity entity) {
            return null;
        }

        @Override
        public String getClassHierarchyListName() {
            return null;
        }

        @Override
        public String getSchemaVersionName() {
            return null;
        }

        @Override
        protected void assignKeyToModelRefIfNecessary(AsyncDatastoreService ds,
                                                      Object model) throws NullPointerException {
        }

        @Override
        protected void postGet(Object model) {
            return;
        }
    };

    private StringUnindexedAttributeMeta<Hoge> myString =
            new StringUnindexedAttributeMeta<Hoge>(meta, "myString", "myString");

    /**
     * @throws Exception
     */
    @Test
    public void startsWith() throws Exception {
        assertThat(
                myString.startsWith("a"),
                isA(InMemoryStartsWithCriterion.class));
        assertThat(myString.startsWith(null), is(not(nullValue())));
    }

    /**
     * @throws Exception
     */
    @Test
    public void endsWith() throws Exception {
        assertThat(myString.endsWith("a"), isA(InMemoryEndsWithCriterion.class));
        assertThat(myString.endsWith(null), is(notNullValue()));
    }

    /**
     * @throws Exception
     */
    @Test
    public void contains() throws Exception {
        assertThat(myString.contains("a"), isA(InMemoryContainsCriterion.class));
        assertThat(myString.contains(null), is(not(nullValue())));
    }
}