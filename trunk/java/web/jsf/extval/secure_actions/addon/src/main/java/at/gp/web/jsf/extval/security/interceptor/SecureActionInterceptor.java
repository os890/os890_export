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
package at.gp.web.jsf.extval.security.interceptor;

import at.gp.web.jsf.extval.security.SecureMethodBindingWrapper;
import at.gp.web.jsf.extval.security.util.SecureActionUtils;
import org.apache.myfaces.extensions.validator.core.interceptor.AbstractRendererInterceptor;
import org.apache.myfaces.extensions.validator.core.renderkit.exception.SkipBeforeInterceptorsException;
import org.apache.myfaces.extensions.validator.core.renderkit.exception.SkipRendererDelegationException;

import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.event.FacesEvent;
import javax.faces.render.Renderer;

/**
 * @author Gerhard Petracek
 */
public class SecureActionInterceptor extends AbstractRendererInterceptor
{
    @Override
    public void beforeDecode(final FacesContext facesContext, final UIComponent uiComponent, Renderer wrapped) throws SkipBeforeInterceptorsException, SkipRendererDelegationException
    {
        if (uiComponent instanceof UICommand)
        {
            UIComponent parent = uiComponent.getParent();
            UIComponent virtualComponent = new UIPanel()
            {
                @Override
                public void queueEvent(FacesEvent facesEvent)
                {
                    if (allowCommandComponentExecution(facesContext, uiComponent))
                    {
                        //in case of permitted execution or regularLifecycleExecution == true
                        //there is still room for improvements
                        //(it isn't required to wrap bindings if regularLifecycleExecution == false)
                        UICommand command = (UICommand) uiComponent;
                        MethodBinding originalMethodBinding;
                        if (command.getAction() instanceof SecureMethodBindingWrapper)
                        {
                            //prevent execution of other method binding wrappers
                            SecureMethodBindingWrapper originalSecureMethodBinding = (SecureMethodBindingWrapper) command.getAction();
                            originalMethodBinding = originalSecureMethodBinding.getOriginalMethodBinding();
                        }
                        else
                        {
                            originalMethodBinding = command.getAction();
                        }
                        command.setAction(new SecureMethodBindingWrapper(originalMethodBinding));

                        super.queueEvent(facesEvent);
                    }
                }
            };

            virtualComponent.setParent(parent);
            uiComponent.setParent(virtualComponent);

            //force decode
            wrapped.decode(facesContext, uiComponent);

            uiComponent.setParent(parent);
        }

        super.beforeDecode(facesContext, uiComponent, wrapped);
    }

    private boolean allowCommandComponentExecution(FacesContext facesContext, UIComponent uiComponent)
    {
        if (!SecureActionUtils.isActionMethodPresent((UICommand) uiComponent))
        {
            //button has no action method which might be secured
            return true;
        }

        String actionString = ((UICommand) uiComponent).getAction().getExpressionString();

        return SecureActionUtils.allowAction(facesContext, actionString, true);
    }
}
