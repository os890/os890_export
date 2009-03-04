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

import org.apache.myfaces.extensions.validator.core.renderkit.exception.SkipAfterInterceptorsException;
import org.apache.myfaces.extensions.validator.core.el.ValueBindingExpression;
import org.apache.myfaces.extensions.validator.core.el.ELHelper;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;
import org.apache.myfaces.extensions.validator.util.ReflectionUtils;
import org.apache.myfaces.extensions.validator.util.ClassUtils;
import org.apache.myfaces.extensions.validator.ValidationInterceptorWithSkipValidationSupport;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.component.UICommand;
import javax.faces.render.Renderer;
import java.lang.reflect.Method;

import at.gp.web.jsf.extval.validation.bypass.annotation.BypassValidation;
import at.gp.web.jsf.extval.validation.bypass.util.BypassValidationUtils;

/**
 * @author Gerhard Petracek
 */
public class ValidationInterceptorWithBypassValidationSupport extends ValidationInterceptorWithSkipValidationSupport
{
    @Override
    @SuppressWarnings({"deprecation"})
    public void afterDecode(FacesContext facesContext, UIComponent uiComponent, Renderer wrapped)
            throws SkipAfterInterceptorsException
    {
        super.afterDecode(facesContext, uiComponent, wrapped);

        if (uiComponent instanceof UICommand)
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

            //check if current command component was fired
            if (facesContext.getExternalContext().getRequestParameterMap()
                    .containsKey(uiComponent.getClientId(facesContext)))
            {
                processBypassValidation(facesContext, valueBindingExpression);
            }
        }
    }

    private void processBypassValidation(FacesContext facesContext, ValueBindingExpression valueBindingExpression)
    {
        ELHelper elHelper = ExtValUtils.getELHelper();
        Object base = elHelper.getValueOfExpression(facesContext, valueBindingExpression.getBaseExpression());

        Method actionMethod = ReflectionUtils
                .tryToGetMethod(getClassOf(base), valueBindingExpression.getProperty());

        if (!actionMethod.isAnnotationPresent(BypassValidation.class))
        {
            return;
        }

        BypassValidation bypassValidation = actionMethod.getAnnotation(BypassValidation.class);

        ValueBindingExpression bypassExpression;
        for (String currentExpression : bypassValidation.value())
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

        if (targetClass == null && this.logger.isWarnEnabled())
        {
            this.logger.warn("couldn't get class of " +
                    base.getClass().getName() +
                    " maybe there is a proxy. if it is an issue, please report it!");
        }

        return targetClass;
    }

    @Override
    protected void processValidation(FacesContext facesContext, UIComponent uiComponent, Object convertedObject)
    {
        if (BypassValidationUtils.bypassAllValidationsForRequest())
        {
            return;
        }

        super.processValidation(facesContext, uiComponent, convertedObject);
    }
}
