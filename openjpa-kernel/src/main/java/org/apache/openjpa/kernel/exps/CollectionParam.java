/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openjpa.kernel.exps;

import org.apache.openjpa.kernel.StoreContext;

/**
 * Represents a collection valued input parameter.
 *
 * @author Catalina Wei
 */
class CollectionParam
    extends Val
    implements Parameter {

    
    private static final long serialVersionUID = 1L;
    private Object _key = null;
    private Class _type = null;
    private int _index = -1;

    /**
     * Constructor. Provide parameter name and type.
     */
    public CollectionParam(Object name, Class type) {
        _key = name;
        _type = type;
    }

    @Override
    public Object getParameterKey() {
        return _key;
    }

    @Override
    public Class getType() {
        return _type;
    }

    @Override
    public void setImplicitType(Class type) {
        _type = type;
    }

    @Override
    public void setIndex(int index) {
        _index = index;
    }

    @Override
    public Object getValue(Object[] params) {
        return params[_index];
    }

    @Override
    protected Object eval(Object candidate, Object orig,
        StoreContext ctx, Object[] params) {
        return getValue(params);
    }
}

