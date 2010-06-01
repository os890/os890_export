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
package at.gp.web.jsf.extval.beanval.form.interceptor;

import at.gp.web.jsf.extval.beanval.form.util.FormValidationUtils;
import org.apache.myfaces.extensions.validator.core.interceptor.AbstractRendererInterceptor;
import org.apache.myfaces.extensions.validator.core.renderkit.exception.SkipBeforeInterceptorsException;
import org.apache.myfaces.extensions.validator.core.renderkit.exception.SkipRendererDelegationException;

import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

/**
 * @author Gerhard Petracek
 */
public class FormRecordingRendererInterceptor extends AbstractRendererInterceptor
{
    @Override
    public void beforeDecode(FacesContext facesContext, UIComponent uiComponent, Renderer wrapped) throws SkipBeforeInterceptorsException, SkipRendererDelegationException
    {
        if (uiComponent instanceof UIForm)
        {
            FormValidationUtils.getOrInitProcessedInformationStorage().addFormId(uiComponent.getClientId(facesContext));
        }
    }
}
