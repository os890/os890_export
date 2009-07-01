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
package at.gp.web.jsf.extval.validation.dev.interceptor;

import org.apache.myfaces.extensions.validator.internal.UsageCategory;
import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.ValidationInterceptorWithSkipValidationSupport;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.component.EditableValueHolder;

/**
 * @author Gerhard Petracek
 */
@UsageInformation(UsageCategory.INTERNAL)
public class DevValidationInterceptor extends ValidationInterceptorWithSkipValidationSupport
{
    @Override
    protected void initComponent(FacesContext facesContext, UIComponent uiComponent)
    {
        if(!(uiComponent instanceof EditableValueHolder))
        {
            return;
        }

        System.out.println(">>> processing component: " + uiComponent.getClass().getName());
        System.out.println(">>> value binding: " + uiComponent.getValueBinding("value").getExpressionString());

        super.initComponent(facesContext, uiComponent);
    }

    @Override
    protected void processValidation(FacesContext facesContext, UIComponent uiComponent, Object o)
    {
        if(!(uiComponent instanceof EditableValueHolder))
        {
            return;
        }

        System.out.println(">>> processing component: " + uiComponent.getClass().getName());
        System.out.println(">>> value binding: " + uiComponent.getValueBinding("value").getExpressionString());

        super.processValidation(facesContext, uiComponent, o);
    }
}
