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

import org.apache.myfaces.extensions.validator.core.startup.AbstractStartupListener;
import org.apache.myfaces.extensions.validator.core.ExtValContext;
import org.apache.myfaces.extensions.validator.core.initializer.configuration.StaticInMemoryConfiguration;
import org.apache.myfaces.extensions.validator.core.initializer.configuration.StaticConfigurationNames;
import org.apache.myfaces.extensions.validator.crossval.annotation.NotEquals;
import at.gp.web.jsf.extval.validation.CustomNotEqualsValidation;
import at.gp.web.jsf.extval.validation.CustomMessageResolver;

/**
 * used for label support
 * (value of HtmlOutputLabel within required message, if there is no label attribute at the input component e.g. JSF 1.1)
 *
 * @author Gerhard Petracek
 */
public class StartupListener extends AbstractStartupListener
{
    protected void init()
    {
        ExtValContext.getContext().registerRendererInterceptor(new OutputLabelRendererInterceptor());
        ExtValContext.getContext().addValidationExceptionInterceptor(new OutputLabelValidationExceptionInterceptor());
        StaticInMemoryConfiguration configuration = new StaticInMemoryConfiguration();
        configuration.addMapping(NotEquals.class.getName(), CustomNotEqualsValidation.class.getName());
        ExtValContext.getContext().addStaticConfiguration(StaticConfigurationNames.META_DATA_TO_VALIDATION_STRATEGY_CONFIG, configuration);
        configuration = new StaticInMemoryConfiguration();
        configuration.addMapping(CustomNotEqualsValidation.class.getName(), CustomMessageResolver.class.getName());
        ExtValContext.getContext().addStaticConfiguration(StaticConfigurationNames.VALIDATION_STRATEGY_TO_MESSAGE_RESOLVER_CONFIG, configuration);
    }
}
