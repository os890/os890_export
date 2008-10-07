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

import org.apache.myfaces.extensions.validator.core.mapper.ClassMappingFactory;
import org.apache.myfaces.extensions.validator.core.validation.strategy.ValidationStrategy;
import org.apache.myfaces.extensions.validator.core.validation.strategy.AbstractValidationStrategy;

import java.lang.annotation.Annotation;

import at.gp.web.jsf.extval.validation.CustomRequired;
import at.gp.web.jsf.extval.validation.CustomRequiredValidator;
import at.gp.web.jsf.extval.validation.StandardMessageResolver;

/**
 * instead of using the convention you can also use the web.xml context-param instead
 * (param-name: org.apache.myfaces.extensions.validator.CUSTOM_VALIDATION_STRATEGY_FACTORY)
 * 
 * @author Gerhard Petracek
 */
public class ValidationStrategyFactory implements ClassMappingFactory<Annotation, ValidationStrategy>
{
    public ValidationStrategy create(Annotation annotation)
    {
        AbstractValidationStrategy resultStrategy = null;

        //it isn't nice to keep it hardcoded - it's just a demo
        if(annotation instanceof CustomRequired)
        {
            resultStrategy = new CustomRequiredValidator();
            resultStrategy.setMessageResolver(new StandardMessageResolver());
        }

        return resultStrategy;
    }
}
