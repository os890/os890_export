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
package at.gp.web.jsf.extval.validation.bypass.interceptor;

import org.apache.myfaces.extensions.validator.core.renderkit.exception.SkipBeforeInterceptorsException;
import org.apache.myfaces.extensions.validator.core.renderkit.exception.SkipRendererDelegationException;
import org.apache.myfaces.extensions.validator.core.el.ValueBindingExpression;
import org.apache.myfaces.extensions.validator.core.el.ELHelper;
import org.apache.myfaces.extensions.validator.core.ExtValContext;
import org.apache.myfaces.extensions.validator.core.interceptor.RendererInterceptor;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;
import org.apache.myfaces.extensions.validator.util.ReflectionUtils;
import org.apache.myfaces.extensions.validator.util.ClassUtils;
import org.apache.myfaces.extensions.validator.PropertyValidationModuleValidationInterceptor;
import org.apache.myfaces.extensions.validator.MappedConstraintSourcePropertyValidationModuleValidationInterceptor;
import org.apache.myfaces.extensions.validator.internal.ToDo;
import org.apache.myfaces.extensions.validator.internal.Priority;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.component.UICommand;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIPanel;
import javax.faces.render.Renderer;
import javax.faces.event.FacesEvent;
import java.lang.reflect.Method;
import java.util.logging.Level;

import at.gp.web.jsf.extval.validation.bypass.annotation.BypassValidation;
import at.gp.web.jsf.extval.validation.bypass.util.BypassValidationUtils;

/**
 * @author Gerhard Petracek
 */
public class ValidationInterceptorWithBypassValidationSupport extends PropertyValidationModuleValidationInterceptor
{
    private Boolean mappedConstraintSourceValidation;

    @Override
    public void beforeDecode(FacesContext facesContext, UIComponent uiComponent, Renderer wrapped) throws SkipBeforeInterceptorsException, SkipRendererDelegationException
    {
        if (uiComponent instanceof UICommand)
        {
            UIComponent parent = uiComponent.getParent();
            UIComponent virtualComponent = new UIPanel() {
                private String clientId = null;
                @Override
                public void queueEvent(FacesEvent facesEvent)
                {
                    this.clientId = facesEvent.getComponent().getClientId(FacesContext.getCurrentInstance());
                    if(isCompatible())
                    {
                        super.queueEvent(facesEvent);
                    }
                }

                @Override
                public String toString()
                {
                    return clientId;
                }
            };

            virtualComponent.setParent(parent);
            uiComponent.setParent(virtualComponent);

            //force decode
            wrapped.decode(facesContext, uiComponent);

            if(virtualComponent.toString() != null)
            {
                processActivatedCommandComponent(facesContext, uiComponent);
            }

            uiComponent.setParent(parent);
        }
        
        super.beforeDecode(facesContext, uiComponent, wrapped);
    }

    @ToDo(value = Priority.HIGH, description = "this add-on is only compatible with the property validation module - in stand alone mode")
    private boolean isCompatible()
    {
        lazyMappedConstraintSourceCheck();

        return ExtValContext.getContext().getRendererInterceptors().size() == 1;
    }

    @SuppressWarnings({"deprecation"})
    private void processActivatedCommandComponent(FacesContext facesContext, UIComponent uiComponent)
    {
        String actionString = ((UICommand) uiComponent).getAction() != null ?
                ((UICommand) uiComponent).getAction().getExpressionString() : "";

        if (!ExtValUtils.getELHelper().isELTermWellFormed(actionString))
        {
            return;
        }

        ValueBindingExpression valueBindingExpression = new ValueBindingExpression(actionString);

        if (!ExtValUtils.getELHelper()
                .isELTermValid(facesContext, valueBindingExpression.getBaseExpression().getExpressionString()))
        {
            return;
        }

        processBypassValidation(facesContext, valueBindingExpression);
    }

    @ToDo(Priority.MEDIUM)
    private void processBypassValidation(FacesContext facesContext, ValueBindingExpression valueBindingExpression)
    {
        ELHelper elHelper = ExtValUtils.getELHelper();
        Object base = elHelper.getValueOfExpression(facesContext, valueBindingExpression.getBaseExpression());

        if(base == null) //in case of c:set
        {
            //TODO this add-on isn't compatible with "virtual expressions" e.g. #{page.myAction} page is created via c:set
            return;
        }

        Method actionMethod = ReflectionUtils
                .tryToGetMethod(getClassOf(base), valueBindingExpression.getProperty());

        if(actionMethod == null)
        {
            throw new IllegalStateException("method-binding: " + valueBindingExpression.getExpressionString() + " doesn't exist");    
        }

        if (!actionMethod.isAnnotationPresent(BypassValidation.class))
        {
            return;
        }

        BypassValidation bypassValidation = actionMethod.getAnnotation(BypassValidation.class);

        ValueBindingExpression bypassExpression;
        for (String currentExpression : bypassValidation.conditions())
        {
            bypassExpression = new ValueBindingExpression(currentExpression);

            if (Boolean.TRUE.equals(elHelper.getValueOfExpression(facesContext, bypassExpression)))
            {
                BypassValidationUtils.activateBypassAllValidationsForRequest(bypassValidation);
                return;
            }
        }
    }

    private Class getClassOf(Object base)
    {
        Class targetClass = base.getClass();

        if (targetClass.getName().contains("$$EnhancerByCGLIB$$"))
        {
            String className = targetClass.getName().substring(0, targetClass.getName().indexOf("$$EnhancerByCGLIB$$"));
            targetClass = ClassUtils.tryToLoadClassForName(className);
        }
        else if (targetClass.getName().contains("$$FastClassByCGLIB$$"))
        {
            String className = targetClass.getName().substring(0,targetClass.getName().indexOf("$$FastClassByCGLIB$$"));
            targetClass = ClassUtils.tryToLoadClassForName(className);
        }

        if (targetClass == null)
        {
            this.logger.log(Level.WARNING, "couldn't get class of " +
                    base.getClass().getName() +
                    " maybe there is a proxy. if it is an issue, please report it!");
        }

        return targetClass;
    }

    @Override
    protected void processValidation(FacesContext facesContext, UIComponent uiComponent, Object convertedObject)
    {
        //required is a special case - reset it
        ((EditableValueHolder)uiComponent).setRequired(false);

        if (BypassValidationUtils.bypassAllValidationsForRequest())
        {
            return;
        }

        lazyMappedConstraintSourceCheck();

        new PropertyValidationModuleValidationInterceptor(){
            @Override
            public void processValidation(FacesContext facesContext, UIComponent uiComponent, Object convertedObject)
            {
                super.processValidation(facesContext, uiComponent, convertedObject);
            }
        }.processValidation(facesContext, uiComponent, convertedObject);

        if(this.mappedConstraintSourceValidation)
        {
            new MappedConstraintSourcePropertyValidationModuleValidationInterceptor() {
                @Override
                public void processValidation(FacesContext facesContext, UIComponent uiComponent, Object convertedObject)
                {
                    super.processValidation(facesContext, uiComponent, convertedObject);
                }
            }.processValidation(facesContext, uiComponent, convertedObject);
        }
    }

    private void lazyMappedConstraintSourceCheck()
    {
        if(this.mappedConstraintSourceValidation == null)
        {
            if(isMappedConstraintSourceValidationActive())
            {
                ExtValContext.getContext().deregisterRendererInterceptor(MappedConstraintSourcePropertyValidationModuleValidationInterceptor.class);

                this.mappedConstraintSourceValidation = Boolean.TRUE;
            }
            else
            {
                this.mappedConstraintSourceValidation = Boolean.FALSE;
            }
        }
    }

    private boolean isMappedConstraintSourceValidationActive()
    {
        for(RendererInterceptor rendererInterceptor : ExtValContext.getContext().getRendererInterceptors())
        {
            if(rendererInterceptor instanceof MappedConstraintSourcePropertyValidationModuleValidationInterceptor)
            {
                return true;
            }
        }
        return false;
    }
}
