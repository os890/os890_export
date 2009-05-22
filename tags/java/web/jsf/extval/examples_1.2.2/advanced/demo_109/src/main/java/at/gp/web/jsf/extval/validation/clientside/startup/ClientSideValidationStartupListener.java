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
package at.gp.web.jsf.extval.validation.clientside.startup;

import org.apache.myfaces.extensions.validator.core.startup.AbstractStartupListener;
import org.apache.myfaces.extensions.validator.core.ExtValContext;
import org.apache.myfaces.extensions.validator.core.metadata.CommonMetaDataKeys;

import java.util.Map;
import java.util.HashMap;

import at.gp.web.jsf.extval.validation.clientside.validator.RequiredClientValidationStrategy;
import at.gp.web.jsf.extval.validation.clientside.validator.LengthClientValidationStrategy;
import at.gp.web.jsf.extval.validation.clientside.interceptor.ExtValClientAwareValidationInterceptor;
import at.gp.web.jsf.extval.validation.clientside.ClientValidationStrategy;

/**
 * @author Gerhard Petracek
 * @since 1.x.2
 */
public class ClientSideValidationStartupListener extends AbstractStartupListener
{
    protected void init()
    {
        ExtValContext.getContext().registerRendererInterceptor(new ExtValClientAwareValidationInterceptor());

        initClientValidationStrategyMappings();
    }

    //just a simple implementation - override it to use a more sophisticated one
    private void initClientValidationStrategyMappings()
    {
        Map<String, Class<? extends ClientValidationStrategy>> clientValidationStrategyMap
                = new HashMap<String, Class<? extends ClientValidationStrategy>>();
        ExtValContext.getContext()
                .addGlobalProperty(ClientValidationStrategy.class.getName(), clientValidationStrategyMap);

        clientValidationStrategyMap.put(CommonMetaDataKeys.REQUIRED, RequiredClientValidationStrategy.class);
        clientValidationStrategyMap.put(CommonMetaDataKeys.WEAK_REQUIRED, RequiredClientValidationStrategy.class);
        clientValidationStrategyMap.put(CommonMetaDataKeys.MIN_LENGTH, LengthClientValidationStrategy.class);
        clientValidationStrategyMap.put(CommonMetaDataKeys.MAX_LENGTH, LengthClientValidationStrategy.class);
    }
}
