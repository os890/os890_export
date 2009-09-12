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
import org.apache.myfaces.extensions.validator.ValidationInterceptorWithSkipValidationSupport;

/**
 * Allows to deactivate ExtVal based validation even though it's in the classpath
 *
 * Configure it as application scoped bean with the name customExtValContext
 *
 * <managed-bean>
 *   <managed-bean-name>customExtValContext</managed-bean-name>
 *   <managed-bean-class>org.apache.myfaces.extensions.validator.custom.DevExtValContext</managed-bean-class>
 *   <managed-bean-scope>application</managed-bean-scope>
 * </managed-bean>
 *
 * @author Gerhard Petracek
 * @since x.x.3
 */
public class DevExtValContext extends ExtValContext
{
    @Override
    public void setSkipValidationEvaluator(SkipValidationEvaluator skipValidationEvaluator)
    {
    }

    @Override
    public void setSkipValidationEvaluator(SkipValidationEvaluator skipValidationEvaluator, boolean forceOverride)
    {
    }

    @Override
    public SkipValidationEvaluator getSkipValidationEvaluator()
    {
        return new SkipValidationEvaluator() {

            public boolean skipValidation(FacesContext facesContext, UIComponent uiComponent, ValidationStrategy validationStrategy, MetaDataEntry entry)
            {
                return false;
            }
        };
    }

    @Override
    public List<RendererInterceptor> getRendererInterceptors()
    {
        return new ArrayList<RendererInterceptor>();
    }

    @Override
    public boolean registerRendererInterceptor(RendererInterceptor rendererInterceptor)
    {
        return true;
    }

    @Override
    public void deregisterRendererInterceptor(Class rendererInterceptorClass)
    {
    }

    @Override
    public void denyRendererInterceptor(Class rendererInterceptorClass)
    {
    }

    @Override
    public void addComponentInitializer(ComponentInitializer componentInitializer)
    {
    }

    @Override
    public List<ComponentInitializer> getComponentInitializers()
    {
        return new ArrayList<ComponentInitializer>();
    }

    @Override
    public void addValidationExceptionInterceptor(ValidationExceptionInterceptor validationExceptionInterceptor)
    {
    }

    @Override
    public List<ValidationExceptionInterceptor> getValidationExceptionInterceptors()
    {
        return new ArrayList<ValidationExceptionInterceptor>();
    }

    @Override
    public void addPropertyValidationInterceptor(PropertyValidationInterceptor propertyValidationInterceptor)
    {
    }

    @Override
    public List<PropertyValidationInterceptor> getPropertyValidationInterceptors()
    {
        return new ArrayList<PropertyValidationInterceptor>();
    }

    @Override
    public List<PropertyValidationInterceptor> getPropertyValidationInterceptorsFor(Class moduleKey)
    {
        return new ArrayList<PropertyValidationInterceptor>();
    }

    @Override
    public void addMetaDataExtractionInterceptor(MetaDataExtractionInterceptor metaDataExtractionInterceptor)
    {
    }

    @Override
    public List<MetaDataExtractionInterceptor> getMetaDataExtractionInterceptors()
    {
        return new ArrayList<MetaDataExtractionInterceptor>();
    }

    @Override
    public List<ProcessedInformationRecorder> getProcessedInformationRecorders()
    {
        return new ArrayList<ProcessedInformationRecorder>();
    }

    @Override
    public void addProcessedInformationRecorder(ProcessedInformationRecorder processedInformationRecorder)
    {
    }

    @Override
    public List<StaticConfiguration<String, String>> getStaticConfiguration(StaticConfigurationNames name)
    {
        return new ArrayList<StaticConfiguration<String, String>>();
    }

    @Override
    public void addStaticConfiguration(StaticConfigurationNames name, StaticConfiguration<String, String> staticConfig)
    {
    }

    @Override
    public boolean addGlobalProperty(String name, Object value)
    {
        return true;
    }

    @Override
    public boolean addGlobalProperty(String name, Object value, boolean forceOverride)
    {
        return true;
    }

    @Override
    public Object getGlobalProperty(String name)
    {
        return null;
    }
}
