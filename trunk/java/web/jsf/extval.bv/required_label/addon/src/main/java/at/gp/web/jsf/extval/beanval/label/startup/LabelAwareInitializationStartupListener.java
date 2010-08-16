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
package at.gp.web.jsf.extval.beanval.label.startup;

import at.gp.web.jsf.extval.beanval.label.DefaultRequiredLabelAddonConfiguration;
import at.gp.web.jsf.extval.beanval.label.RequiredLabelAddonConfiguration;
import at.gp.web.jsf.extval.beanval.label.interceptor.BeanValidationAwareLabelRendererInterceptor;
import at.gp.web.jsf.extval.beanval.label.interceptor.MappedConstraintSourceBeanValidationAwareLabelRendererInterceptor;
import org.apache.myfaces.extensions.validator.beanval.BeanValidationModuleValidationInterceptor;
import org.apache.myfaces.extensions.validator.beanval.MappedConstraintSourceBeanValidationModuleValidationInterceptor;
import org.apache.myfaces.extensions.validator.core.DefaultExtValCoreConfiguration;
import org.apache.myfaces.extensions.validator.core.ExtValContext;
import org.apache.myfaces.extensions.validator.core.startup.AbstractStartupListener;

/**
 * @author Gerhard Petracek
 * @author Rudy De Busscher
 */
public class LabelAwareInitializationStartupListener extends AbstractStartupListener
{

        @Override
    protected void initModuleConfig()
    {
        RequiredLabelAddonConfiguration.use(new DefaultRequiredLabelAddonConfiguration(), false);
    }
    
    protected void init()
    {
        ExtValContext extValContext = ExtValContext.getContext();

        deregisterDefaultImplementations(extValContext);
        registerLabelAwareImplementations(extValContext);

        activateRequiredInitializationSupport(extValContext);
    }

    private void deregisterDefaultImplementations(ExtValContext extValContext)
    {
        extValContext.denyRendererInterceptor(BeanValidationModuleValidationInterceptor.class);
        extValContext.denyRendererInterceptor(MappedConstraintSourceBeanValidationModuleValidationInterceptor.class);
    }

    private void registerLabelAwareImplementations(ExtValContext extValContext)
    {
        extValContext.registerRendererInterceptor(new BeanValidationAwareLabelRendererInterceptor());
        extValContext.registerRendererInterceptor(new MappedConstraintSourceBeanValidationAwareLabelRendererInterceptor());
    }

    private void activateRequiredInitializationSupport(ExtValContext extValContext)
    {
        DefaultExtValCoreConfiguration.overruleActivateRequiredInitialization(Boolean.TRUE, true);
        DefaultExtValCoreConfiguration.overruleDeactivateRequiredAttributeSupport(Boolean.TRUE, true);

    }
}
