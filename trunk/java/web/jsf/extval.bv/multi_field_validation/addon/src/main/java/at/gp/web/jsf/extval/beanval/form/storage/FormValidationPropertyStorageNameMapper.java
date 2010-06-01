/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package at.gp.web.jsf.extval.beanval.form.storage;

import org.apache.myfaces.extensions.validator.core.InvocationOrder;
import org.apache.myfaces.extensions.validator.core.mapper.NameMapper;
import org.apache.myfaces.extensions.validator.core.storage.DefaultPropertyStorage;
import org.apache.myfaces.extensions.validator.core.storage.PropertyStorage;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;
import org.apache.myfaces.extensions.validator.internal.UsageInformation;

/**
 * @author Gerhard Petracek
 */
@InvocationOrder(200)
@UsageInformation(UsageCategory.INTERNAL)
public class FormValidationPropertyStorageNameMapper implements NameMapper<String>
{
    public static final String PROPERTY_STORYGE_KEY = PropertyStorage.class.getName() + "|FormValidation";

    public String createName(String source)
    {
        return (PROPERTY_STORYGE_KEY.equals(source)) ? DefaultPropertyStorage.class.getName() : null;
    }
}