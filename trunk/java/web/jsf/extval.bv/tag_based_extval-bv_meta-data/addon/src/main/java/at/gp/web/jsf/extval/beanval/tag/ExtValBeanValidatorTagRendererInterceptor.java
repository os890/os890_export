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
package at.gp.web.jsf.extval.beanval.tag;

import org.apache.myfaces.extensions.validator.core.interceptor.AbstractValidationInterceptor;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.component.EditableValueHolder;
import javax.faces.validator.Validator;

public class ExtValBeanValidatorTagRendererInterceptor extends AbstractValidationInterceptor
{
    protected void initComponent(FacesContext facesContext, UIComponent uiComponent)
    {
        tryToAddExtValBeanValidationMetaData(facesContext, uiComponent, false);
    }

    protected void processValidation(FacesContext facesContext, UIComponent uiComponent, Object convertedObject)
    {
        tryToAddExtValBeanValidationMetaData(facesContext, uiComponent, true);
    }

    private void tryToAddExtValBeanValidationMetaData(FacesContext facesContext, UIComponent uiComponent, boolean tryToProcessModelValidation)
    {
        if(uiComponent instanceof EditableValueHolder)
        {
            EditableValueHolder editableValueHolder = (EditableValueHolder)uiComponent;

            for(Validator validator : editableValueHolder.getValidators())
            {
                if(validator instanceof ExtValBeanValidatorTag)
                {
                    ((ExtValBeanValidatorTag)validator).processExtValBeanValidationMetaData(facesContext, uiComponent, tryToProcessModelValidation);
                }
            }
        }
    }
}
