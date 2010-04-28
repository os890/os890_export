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
package at.gp.web.jsf.extval.label.startup;

import org.apache.myfaces.extensions.validator.core.startup.AbstractStartupListener;
import org.apache.myfaces.extensions.validator.core.ExtValContext;
import org.apache.myfaces.extensions.validator.PropertyValidationModuleValidationInterceptor;
import at.gp.web.jsf.extval.label.interceptor.PropertyValidationAwareLabelRendererInterceptor;

/**
 * @author Gerhard Petracek
 * @author Rudy De Busscher
 */
public class LabelAwareInitializationStartupListener extends AbstractStartupListener
{
    private static final long serialVersionUID = 7048249946891862447L;

    protected void init()
    {
        ExtValContext extValContext = ExtValContext.getContext();

        deregisterDefaultImplementations(extValContext);
        registerLabelAwareImplementations(extValContext);

        activateRequiredInitializationSupport(extValContext);
    }

    private void deregisterDefaultImplementations(ExtValContext extValContext)
    {
        extValContext.denyRendererInterceptor(PropertyValidationModuleValidationInterceptor.class);
    }

    private void registerLabelAwareImplementations(ExtValContext extValContext)
    {
        extValContext.registerRendererInterceptor(new PropertyValidationAwareLabelRendererInterceptor());
    }

    private void activateRequiredInitializationSupport(ExtValContext extValContext)
    {
        extValContext.addGlobalProperty("mode:init:required", Boolean.TRUE, true);
        // needed for some add-ons
        // attention: you loose the compatibility with the "good old" required attribute in your pages
        extValContext.addGlobalProperty("mode:reset:required", Boolean.TRUE, true);
    }
}
