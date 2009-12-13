/*
 * Copyright 2004-2009 the original author or authors.
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

import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

/**
 * An implementation class for "in" filter.
 * 
 * @author higa
 * @since 3.0
 * 
 */
public class InCriterion extends AbstractCriterion implements FilterCriterion {

    /**
     * The value;
     */
    protected Object value;

    /**
     * Constructor.
     * 
     * @param attributeMeta
     *            the meta data of attribute
     * @param value
     *            the value
     * @see AbstractCriterion#AbstractCriterion(AbstractAttributeMeta)
     */
    public InCriterion(AbstractAttributeMeta<?, ?> attributeMeta, Object value) {
        super(attributeMeta);
        this.value = convertValueForDatastore(value);
    }

    public void apply(Query query) {
        query.addFilter(attributeMeta.getName(), FilterOperator.IN, value);
    }

    public boolean accept(Object model) {
        if (value == null) {
            return false;
        }
        Object v = convertValueForDatastore(attributeMeta.getValue(model));
        for (Object o : Iterable.class.cast(value)) {
            if (compareValue(v, o) == 0) {
                return true;
            }
        }
        return false;
    }
}