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
package at.gp.web.jsf.extval.security.util;

import at.gp.web.jsf.extval.security.annotation.SecureAction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.extensions.validator.core.ExtValContext;
import org.apache.myfaces.extensions.validator.core.el.ELHelper;
import org.apache.myfaces.extensions.validator.core.el.ValueBindingExpression;
import org.apache.myfaces.extensions.validator.core.validation.message.resolver.DefaultValidationErrorMessageResolver;
import org.apache.myfaces.extensions.validator.util.ClassUtils;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;
import org.apache.myfaces.extensions.validator.util.ReflectionUtils;

import javax.faces.application.FacesMessage;
import javax.faces.component.UICommand;
import javax.faces.context.FacesContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Gerhard Petracek
 */
public class SecureActionUtils
{
    private static final Log LOGGER = LogFactory.getLog(SecureActionUtils.class);

    public static boolean allowAction(FacesContext facesContext, String actionExpression, boolean isDecodePhase)
    {
        ValueBindingExpression valueBindingExpression = new ValueBindingExpression(actionExpression);

        if (!ExtValUtils.getELHelper()
                .isELTermValid(facesContext, valueBindingExpression.getBaseExpression().getExpressionString()))
        {
            if (LOGGER.isWarnEnabled())
            {
                LOGGER.warn("invalid binding: " + valueBindingExpression.getExpressionString());
            }

            return true;
        }

        return !processSecureAction(facesContext, valueBindingExpression, isDecodePhase);
    }

    public static boolean isActionMethodPresent(UICommand uiCommand)
    {
        String actionString = uiCommand.getAction() != null ?
                uiCommand.getAction().getExpressionString() : "";

        return ExtValUtils.getELHelper().isELTermWellFormed(actionString);
    }

    private static boolean processSecureAction(FacesContext facesContext, ValueBindingExpression valueBindingExpression, boolean isDecodePhase)
    {
        ELHelper elHelper = ExtValUtils.getELHelper();
        Object base = elHelper.getValueOfExpression(facesContext, valueBindingExpression.getBaseExpression());

        Method actionMethod = ReflectionUtils
                .tryToGetMethod(getClassOf(base), valueBindingExpression.getProperty());

        if (actionMethod == null)
        {
            throw new IllegalStateException("method-binding: " + valueBindingExpression.getExpressionString() + " doesn't exist");
        }

        if (!(actionMethod.isAnnotationPresent(SecureAction.class) || actionMethod.isAnnotationPresent(SecureAction.List.class)))
        {
            return false;
        }

        if (actionMethod.isAnnotationPresent(SecureAction.class))
        {
            return processSecureActionAnnotation(facesContext, actionMethod.getAnnotation(SecureAction.class), base, isDecodePhase);
        }

        boolean result;

        for (SecureAction secureAction : actionMethod.getAnnotation(SecureAction.List.class).value())
        {
            result = processSecureActionAnnotation(facesContext, secureAction, base, isDecodePhase);

            if (result)
            {
                return true;
            }
        }

        return false;
    }

    private static boolean processSecureActionAnnotation(FacesContext facesContext, SecureAction secureActionAnnotation, Object actionBase, boolean isDecodePhase)
    {
        if (isDecodePhase && secureActionAnnotation.regularLifecycleExecution())
        {
            //continue lifecycle
            return false;
        }

        ELHelper elHelper = ExtValUtils.getELHelper();

        ValueBindingExpression valueBindingExpression;
        for (String currentCondition : secureActionAnnotation.permittedIf())
        {
            valueBindingExpression = new ValueBindingExpression(currentCondition);

            if (Boolean.FALSE.equals(elHelper.getValueOfExpression(facesContext, valueBindingExpression)))
            {
                String methodName;
                if (elHelper.isELTermWellFormed(secureActionAnnotation.secureAction()))
                {
                    ValueBindingExpression expression = new ValueBindingExpression(secureActionAnnotation.secureAction());
                    actionBase = elHelper.getValueOfExpression(facesContext, expression.getBaseExpression());
                    methodName = expression.getProperty();
                }
                else
                {
                    methodName = secureActionAnnotation.secureAction();
                }

                Method actionMethod = ReflectionUtils.tryToGetMethod(getClassOf(actionBase), methodName);

                if (actionMethod == null)
                {
                    if (LOGGER.isWarnEnabled())
                    {
                        LOGGER.warn("invalid binding: " + secureActionAnnotation.secureAction());
                    }
                    return true;
                }
                try
                {
                    Object result = actionMethod.invoke(actionBase);
                    String outcome = "";

                    if (result instanceof String)
                    {
                        outcome = (String) result;
                    }
                    else if (result != null)
                    {
                        outcome = result.toString();
                    }

                    if (!SecureAction.DEFAULT_OUTCOME.equals(secureActionAnnotation.secureOutcome()))
                    {
                        outcome = secureActionAnnotation.secureOutcome();
                    }

                    if (!"".equals(secureActionAnnotation.securityErrorMsgKey()))
                    {
                        String bundleBase = (String) ExtValContext.getContext().getGlobalProperty(SecureAction.MESSAGE_BUNDLE);

                        if (bundleBase == null)
                        {
                            bundleBase = actionBase.getClass().getPackage().getName() + ".security_messages";
                        }

                        DefaultValidationErrorMessageResolver messageResolver = new DefaultValidationErrorMessageResolver();

                        if (elHelper.isELTermWellFormed(bundleBase))
                        {
                            messageResolver.setMessageBundleVarName(bundleBase.substring(2, bundleBase.length() - 1));
                        }
                        else
                        {
                            messageResolver.setMessageBundleBaseName(bundleBase);
                        }

                        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                messageResolver.getMessage(secureActionAnnotation.securityErrorMsgKey(), facesContext.getViewRoot().getLocale()),
                                messageResolver.getMessage(secureActionAnnotation.securityErrorMsgKey() + "_detail", facesContext.getViewRoot().getLocale())));
                    }

                    facesContext.getApplication().getNavigationHandler().handleNavigation(facesContext, null, outcome);

                    facesContext.renderResponse();
                    return true;
                }
                catch (IllegalAccessException e)
                {
                    if (LOGGER.isWarnEnabled())
                    {
                        LOGGER.warn("couldn't invoke action method", e);
                    }
                    throw new RuntimeException(e);
                }
                catch (InvocationTargetException e)
                {
                    if (LOGGER.isWarnEnabled())
                    {
                        LOGGER.warn("couldn't invoke action method", e);
                    }
                    throw new RuntimeException(e);
                }
            }
        }

        return false;
    }

    private static Class getClassOf(Object base)
    {
        Class targetClass = base.getClass();

        if (targetClass.getName().contains("$$EnhancerByCGLIB$$"))
        {
            String className = targetClass.getName().substring(0, targetClass.getName().indexOf("$$EnhancerByCGLIB$$"));
            targetClass = ClassUtils.tryToLoadClassForName(className);
        }
        else if (targetClass.getName().contains("$$FastClassByCGLIB$$"))
        {
            String className = targetClass.getName().substring(0, targetClass.getName().indexOf("$$FastClassByCGLIB$$"));
            targetClass = ClassUtils.tryToLoadClassForName(className);
        }

        if (targetClass == null && LOGGER.isWarnEnabled())
        {
            LOGGER.warn("couldn't get class of " +
                    base.getClass().getName() +
                    " maybe there is a proxy. if it is an issue, please report it!");
        }

        return targetClass;
    }
}
