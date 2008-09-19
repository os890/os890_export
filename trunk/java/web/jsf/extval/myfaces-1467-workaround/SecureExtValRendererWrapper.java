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
package at.gp.web.jsf.extval.validation.secure;

import org.apache.myfaces.extensions.validator.core.ExtValRendererWrapper;
import org.apache.myfaces.extensions.validator.core.annotation.extractor.AnnotationExtractor;
import org.apache.myfaces.extensions.validator.util.ValidationUtils;

import javax.faces.application.FacesMessage;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

/**
 * check the required submit of user input.
 * secure workaround for MYFACES-1467
 *
 * @author Gerhard Petracek
 */
public class SecureExtValRendererWrapper extends ExtValRendererWrapper
{
    public SecureExtValRendererWrapper(Renderer wrapped)
    {
        super(wrapped);
    }

    @Override
    public void decode(FacesContext facesContext, UIComponent uiComponent)
    {
        this.wrapped.decode(facesContext, uiComponent);

        if (uiComponent instanceof EditableValueHolder)
        {
            if (filterRequiredComponent(facesContext, uiComponent) && ((EditableValueHolder) uiComponent).getSubmittedValue() == null)
            {
                facesContext.addMessage(uiComponent.getClientId(facesContext), new FacesMessage(FacesMessage.SEVERITY_ERROR, "input required", "security alert - input required"));
                facesContext.renderResponse();
            }
        }
    }

    private boolean filterRequiredComponent(FacesContext facesContext, UIComponent uiComponent)
    {
        return ValidationUtils.isValueOfComponentRequired(facesContext, uiComponent) || ( (EditableValueHolder) uiComponent).isRequired(); //and e.g. a custom convention like uiComponent.getId().startsWith("sr")
    }
}
