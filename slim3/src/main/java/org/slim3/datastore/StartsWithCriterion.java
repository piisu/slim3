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

import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

/**
 * An implementation class for "startsWith" filter.
 * 
 * @author higa
 * @since 1.0.0
 * 
 */
public class StartsWithCriterion extends InMemoryStartsWithCriterion implements
        FilterCriterion {

    /**
     * The array of filters.
     */
    protected Query.Filter[] filters;

    /**
     * Constructor.
     * 
     * @param attributeMeta
     *            the meta data of attribute
     * @param value
     *            the value
     * @throws NullPointerException
     *             if the attributeMeta parameter is null
     */
    public StartsWithCriterion(AbstractAttributeMeta<?, ?> attributeMeta,
            String value) throws NullPointerException {
        super(attributeMeta, value);
        filters =
            new Query.Filter[] {
                new Query.FilterPredicate(
                    attributeMeta.getName(),
                    FilterOperator.GREATER_THAN_OR_EQUAL,
                    this.value),
                new Query.FilterPredicate(
                    attributeMeta.getName(),
                    FilterOperator.LESS_THAN,
                    highValue) };
    }

    @Override
    public Query.Filter[] getFilters() {
        return filters;
    }
}