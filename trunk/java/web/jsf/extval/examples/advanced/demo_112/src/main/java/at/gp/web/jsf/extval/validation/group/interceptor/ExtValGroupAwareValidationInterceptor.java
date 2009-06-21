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
package at.gp.web.jsf.extval.validation.group.interceptor;

import org.apache.myfaces.extensions.validator.ValidationInterceptorWithSkipValidationSupport;
import org.apache.myfaces.extensions.validator.core.property.PropertyInformation;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.component.EditableValueHolder;

public class ExtValGroupAwareValidationInterceptor extends ValidationInterceptorWithSkipValidationSupport
{
    @Override
    protected void processFieldValidation(FacesContext facesContext, UIComponent uiComponent, Object convertedObject, PropertyInformation propertyInformation)
    {
        //the group validation concept isn't compatible with the required attribute
        ((EditableValueHolder)uiComponent).setRequired(false);
        
        super.processFieldValidation(facesContext, uiComponent, convertedObject, propertyInformation);
    }
}