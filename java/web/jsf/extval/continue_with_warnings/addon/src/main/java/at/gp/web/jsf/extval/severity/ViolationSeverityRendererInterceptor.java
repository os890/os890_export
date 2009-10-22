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
package at.gp.web.jsf.extval.severity;

import org.apache.myfaces.extensions.validator.core.interceptor.AbstractRendererInterceptor;
import org.apache.myfaces.extensions.validator.core.renderkit.exception.SkipAfterInterceptorsException;
import org.apache.myfaces.extensions.validator.core.renderkit.exception.SkipBeforeInterceptorsException;
import org.apache.myfaces.extensions.validator.core.renderkit.exception.SkipRendererDelegationException;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.component.UICommand;
import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.event.FacesEvent;
import javax.faces.render.Renderer;
import java.io.IOException;

/**
 * @author Gerhard Petracek
 * @since x.x.3
 */
public class ViolationSeverityRendererInterceptor extends AbstractRendererInterceptor
{
    private static final String
            EXTVAL_FORCE_CONTINUE_WITH_WARNINGS_PARAMETER_KEY = "extValForceContinueWithWarningsParameter";

    @Override
    /*
     * to support the parameter for the button
     */
    public void beforeDecode(final FacesContext facesContext, final UIComponent uiComponent, Renderer wrapped)
            throws SkipBeforeInterceptorsException, SkipRendererDelegationException
    {
        if (uiComponent instanceof UICommand)
        {
            UIComponent parent = uiComponent.getParent();
            UIComponent virtualComponent = new UIPanel()
            {
                @Override
                public void queueEvent(FacesEvent facesEvent)
                {
                    super.queueEvent(facesEvent);
                    UIParameter parameter;
                    for (UIComponent child : uiComponent.getChildren())
                    {
                        if (child instanceof UIParameter)
                        {
                            parameter = ((UIParameter) child);

                            if (WarnStateUtils.isForceContinueWithWarningsParameter(parameter.getName()))
                            {
                                facesContext.getExternalContext().getRequestMap()
                                        .put(EXTVAL_FORCE_CONTINUE_WITH_WARNINGS_PARAMETER_KEY, parameter.getValue());
                            }
                        }
                    }
                }
            };

            virtualComponent.setParent(parent);
            uiComponent.setParent(virtualComponent);

            //force decode
            wrapped.decode(facesContext, uiComponent);

            uiComponent.setParent(parent);
        }
    }

    /*
     * transfer the state from the hidden input to the bean before the other components get
     */
    @Override
    public void afterGetConvertedValue(FacesContext facesContext, UIComponent uiComponent, Object o, Renderer wrapped)
            throws ConverterException, SkipAfterInterceptorsException
    {
        if (uiComponent instanceof EditableValueHolder)
        {
            if (WarnStateUtils.isWarnStateComponentId(uiComponent.getId()))
            {
                if(notRestrictedByButton())
                {
                    processFlag((Boolean) wrapped.getConvertedValue(facesContext, uiComponent, o));
                }
            }
        }
    }

    /*
     * render a hidden (immediate) input field which forwards the indicator about the next step
     */
    @Override
    public void beforeEncodeBegin(FacesContext facesContext, UIComponent uiComponent, Renderer wrapped)
            throws IOException, SkipBeforeInterceptorsException, SkipRendererDelegationException
    {
        if (uiComponent instanceof EditableValueHolder && WarnStateUtils.isWarnStateComponentId(uiComponent.getId()))
        {
            WarnStateBean warnStateBean = WarnStateUtils.getOrCreateWarnStateBean();
            Boolean result = warnStateBean.isContinueWithWarnings();
            //just for rendering
            ((EditableValueHolder) uiComponent).setImmediate(true);
            ((EditableValueHolder) uiComponent).setSubmittedValue(result.toString());
        }
    }

    private boolean notRestrictedByButton()
    {
        Object parameter = FacesContext.getCurrentInstance().getExternalContext().getRequestMap()
                .get(EXTVAL_FORCE_CONTINUE_WITH_WARNINGS_PARAMETER_KEY);

        return !Boolean.FALSE.toString().equals(parameter);
    }

    private void processFlag(Boolean newValue)
    {
        WarnStateBean warnStateBean = WarnStateUtils.tryToFindExistingWarnStateBean();

        if (warnStateBean != null)
        {
            warnStateBean.setContinueWithWarnings(newValue);
            if (newValue)
            {
                warnStateBean.useNewState();
            }
        }
    }
}
