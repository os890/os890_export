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

import org.apache.myfaces.extensions.validator.ValidationInterceptorWithSkipValidationSupport;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;
import org.apache.myfaces.extensions.validator.core.renderkit.exception.SkipBeforeInterceptorsException;
import org.apache.myfaces.extensions.validator.core.renderkit.exception.SkipRendererDelegationException;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.component.EditableValueHolder;
import javax.faces.render.Renderer;
import java.io.IOException;

/**
 * @author Gerhard Petracek
 * @since 1.x.1
 */
public class PrototypingValidationInterceptor extends ValidationInterceptorWithSkipValidationSupport
{
    @Override
    public void beforeEncodeBegin(FacesContext facesContext, UIComponent uiComponent, Renderer wrapped) throws IOException, SkipBeforeInterceptorsException, SkipRendererDelegationException
    {
        if(uiComponent instanceof EditableValueHolder && processComponent(uiComponent))
        {
            super.beforeEncodeBegin(facesContext, uiComponent, wrapped);
        }
    }

    @Override
    protected void processValidation(FacesContext facesContext, UIComponent uiComponent, Object convertedObject)
    {
        if(uiComponent instanceof EditableValueHolder && processComponent(uiComponent))
        {
            super.processValidation(facesContext, uiComponent, convertedObject);
        }
    }

    private boolean processComponent(UIComponent uiComponent)
    {
        try
        {
            return ExtValUtils.getELHelper().getPropertyDetailsOfValueBinding(uiComponent) != null;
        }
        catch (Throwable t)
        {
            return false;
        }
    }
}
