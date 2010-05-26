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
package at.gp.web.jsf.extval.bv.validation.clientside.interceptor;

import at.gp.web.jsf.extval.bv.validation.clientside.ExternalClientValidationContext;
import at.gp.web.jsf.extval.bv.validation.clientside.script.HtmlJSScriptBuilder;
import at.gp.web.jsf.extval.bv.validation.clientside.script.ScriptBuilder;
import org.apache.myfaces.extensions.validator.core.interceptor.AbstractRendererInterceptor;
import org.apache.myfaces.extensions.validator.core.property.PropertyInformation;
import org.apache.myfaces.extensions.validator.core.renderkit.exception.SkipAfterInterceptorsException;
import org.apache.myfaces.extensions.validator.core.renderkit.exception.SkipBeforeInterceptorsException;
import org.apache.myfaces.extensions.validator.core.renderkit.exception.SkipRendererDelegationException;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;

import javax.faces.component.*;
import javax.faces.component.html.HtmlForm;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;
import java.io.IOException;

/**
 * @author Gerhard Petracek
 */
public class ExtValClientAwareBeanValidationInterceptor extends AbstractRendererInterceptor
{
    public void beforeEncodeBegin(FacesContext facesContext, UIComponent uiComponent, Renderer wrapped)
            throws IOException, SkipBeforeInterceptorsException, SkipRendererDelegationException
    {
        if (uiComponent instanceof UIForm)
        {
            addExtValClientValidation(facesContext, uiComponent);
        }
        else if (isComponentAvailableForClientValidation(uiComponent))
        {
            recoredProperty(facesContext, uiComponent);
        }
        else if (uiComponent instanceof UICommand && !((UICommand) uiComponent).isImmediate())
        {
            initCommandComponent(facesContext, (UICommand) uiComponent);
        }
    }

    private void initCommandComponent(FacesContext facesContext, UICommand uiCommand)
    {
        ExternalClientValidationContext.getCurrentInstance(facesContext)
                .getScriptBuilder().addClientSideValidation(uiCommand);
    }

    protected void addExtValClientValidation(FacesContext facesContext, UIComponent form)
    {
        if (form instanceof HtmlForm)
        {
            ExternalClientValidationContext.startContext();


            ScriptBuilder scriptBuilder = new HtmlJSScriptBuilder();

            ExternalClientValidationContext.getCurrentInstance(facesContext).setScriptBuilder(scriptBuilder);

            scriptBuilder.addClientSideValidation(form);
        }
    }

    protected boolean isComponentAvailableForClientValidation(UIComponent uiComponent)
    {
        return uiComponent instanceof EditableValueHolder;
    }

    private void recoredProperty(FacesContext facesContext, UIComponent uiComponent)
    {
        PropertyInformation result = resolvePropertyInformation(facesContext, uiComponent);

        ExternalClientValidationContext.getCurrentInstance(facesContext)
                .addProperty(uiComponent.getClientId(facesContext), result);
    }

    private PropertyInformation resolvePropertyInformation(FacesContext facesContext, UIComponent uiComponent)
    {
        return ExtValUtils.getComponentMetaDataExtractor().extract(facesContext, uiComponent);
    }

    @Override
    public void afterEncodeEnd(FacesContext facesContext, UIComponent uiComponent, Renderer wrapped) throws IOException, SkipAfterInterceptorsException
    {
        if (uiComponent instanceof UIMessage)
        {
            UIOutput marker = ExternalClientValidationContext.getCurrentInstance(facesContext)
                    .getScriptBuilder().getMarkerComponent();
            marker.setId("extval" + uiComponent.getId());
            marker.setParent(uiComponent.getParent());
            marker.encodeBegin(facesContext);
            marker.encodeChildren(facesContext);
            marker.encodeEnd(facesContext);

            UIComponent result = findInputOfMessage((UIMessage) uiComponent);

            //TODO
            ExternalClientValidationContext.getCurrentInstance(facesContext)
                    .addMarkerComponent(result.getClientId(facesContext), marker.getClientId(facesContext));
        }
    }

    private UIComponent findInputOfMessage(UIMessage uiMessage)
    {
        UIComponent namingContainer = findNextNamingContainer(uiMessage);
        return namingContainer.findComponent(uiMessage.getFor());
    }

    private UIComponent findNextNamingContainer(UIComponent uiComponent)
    {
        if (uiComponent.getParent() == null)
        {
            return uiComponent;
        }

        if (uiComponent.getParent() instanceof NamingContainer)
        {
            return uiComponent.getParent();
        }
        else
        {
            return findNextNamingContainer(uiComponent.getParent());
        }
    }
}
