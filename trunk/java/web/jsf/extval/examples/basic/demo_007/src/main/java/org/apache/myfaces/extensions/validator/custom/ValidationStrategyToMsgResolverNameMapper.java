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
package org.apache.myfaces.extensions.validator.custom;

import org.apache.myfaces.extensions.validator.core.mapper.NameMapper;
import org.apache.myfaces.extensions.validator.core.validation.strategy.ValidationStrategy;

import at.gp.web.jsf.extval.validation.CustomMessageResolver;

/**
 * instead of using the convention you can also use the web.xml context-param instead
 * (param-name: org.apache.myfaces.extensions.validator.CUSTOM_VALIDATION_STRATEGY_TO_MESSAGE_RESOLVER_NAME_MAPPER)
 *
 * @author Gerhard Petracek
 */
public class ValidationStrategyToMsgResolverNameMapper implements NameMapper<ValidationStrategy>
{
    public String createName(ValidationStrategy validationStrategy)
    {
        return CustomMessageResolver.class.getName();
    }
}