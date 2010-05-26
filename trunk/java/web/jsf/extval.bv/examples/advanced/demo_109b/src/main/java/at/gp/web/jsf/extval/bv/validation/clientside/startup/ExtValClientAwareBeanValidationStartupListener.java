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
package at.gp.web.jsf.extval.bv.validation.clientside.startup;

import at.gp.web.jsf.extval.bv.validation.clientside.interceptor.ExtValClientAwareBeanValidationInterceptor;
import at.gp.web.jsf.extval.bv.validation.clientside.validator.ClientValidationStrategy;
import at.gp.web.jsf.extval.bv.validation.clientside.validator.LengthClientValidationStrategy;
import at.gp.web.jsf.extval.bv.validation.clientside.validator.RequiredClientValidationStrategy;
import org.apache.myfaces.extensions.validator.core.ExtValContext;
import org.apache.myfaces.extensions.validator.core.metadata.CommonMetaDataKeys;
import org.apache.myfaces.extensions.validator.core.startup.AbstractStartupListener;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Gerhard Petracek
 */
public class ExtValClientAwareBeanValidationStartupListener extends AbstractStartupListener
{
    private static final long serialVersionUID = 1275885283730615618L;

    protected void init()
    {
        ExtValContext.getContext().registerRendererInterceptor(new ExtValClientAwareBeanValidationInterceptor());

        registerAvailableClientValidationStrategies();
    }

    private void registerAvailableClientValidationStrategies()
    {
        //TODO refactor it!
        Map<String, Class<? extends ClientValidationStrategy>> clientValidators =
                new HashMap<String, Class<? extends ClientValidationStrategy>>();
        clientValidators.put(CommonMetaDataKeys.MAX_LENGTH, LengthClientValidationStrategy.class);
        clientValidators.put(CommonMetaDataKeys.MIN_LENGTH, LengthClientValidationStrategy.class);
        clientValidators.put(CommonMetaDataKeys.WEAK_REQUIRED, RequiredClientValidationStrategy.class);

        ExtValContext.getContext().addGlobalProperty(ClientValidationStrategy.class.getName(), clientValidators);
    }
}
