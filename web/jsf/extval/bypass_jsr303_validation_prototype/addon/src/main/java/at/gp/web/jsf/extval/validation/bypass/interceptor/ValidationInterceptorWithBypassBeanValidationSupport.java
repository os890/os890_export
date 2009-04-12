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
import org.apache.myfaces.extensions.validator.core.property.PropertyDetails;
import org.apache.myfaces.extensions.validator.core.property.PropertyInformation;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;
import org.apache.myfaces.extensions.validator.util.ReflectionUtils;
import org.apache.myfaces.extensions.validator.util.ClassUtils;
import org.apache.myfaces.extensions.validator.beanval.BeanValidationInterceptor;
import org.apache.myfaces.extensions.validator.beanval.ExtValBeanValidationContext;
import org.apache.myfaces.extensions.validator.beanval.annotation.BeanValidation;
import org.apache.myfaces.extensions.validator.beanval.validation.ModelValidationEntry;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.component.UICommand;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIPanel;
import javax.faces.render.Renderer;
import javax.faces.event.FacesEvent;
import java.lang.reflect.Method;
import java.util.List;
import java.util.ArrayList;

import at.gp.web.jsf.extval.validation.bypass.annotation.BypassBeanValidation;
import at.gp.web.jsf.extval.validation.bypass.util.BypassBeanValidationUtils;

/**
 * @author Gerhard Petracek
 */
public class ValidationInterceptorWithBypassBeanValidationSupport extends BeanValidationInterceptor
{
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
                    super.queueEvent(facesEvent);
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

        processBeanValidationMetaData(facesContext, valueBindingExpression);
    }

    protected void processBeanValidationMetaData(
            FacesContext facesContext, ValueBindingExpression valueBindingExpression)
    {
        ELHelper elHelper = ExtValUtils.getELHelper();
        Object base = elHelper.getValueOfExpression(facesContext, valueBindingExpression.getBaseExpression());

        Method actionMethod = ReflectionUtils
                .tryToGetMethod(getClassOf(base), valueBindingExpression.getProperty());

        if(actionMethod == null)
        {
            throw new IllegalStateException("method-binding: " + valueBindingExpression.getExpressionString() + " doesn't exist");    
        }

        if (actionMethod.isAnnotationPresent(BypassBeanValidation.class))
        {
            processBypassBeanValidation(actionMethod.getAnnotation(BypassBeanValidation.class), facesContext, elHelper);
            return;
        }

        String key = valueBindingExpression.getExpressionString();
        key = key.substring(2, key.length() -1);
        String methodName = valueBindingExpression.getProperty();
        if(actionMethod.isAnnotationPresent(BeanValidation.class))
        {
            processBeanValidation(actionMethod.getAnnotation(BeanValidation.class), key, base, methodName);
        }
        else if(actionMethod.isAnnotationPresent(BeanValidation.List.class))
        {
            for(BeanValidation beanValidation : actionMethod.getAnnotation(BeanValidation.List.class).value())
            {
                processBeanValidation(beanValidation, key, base, methodName);
            }
        }
    }

    private void processBypassBeanValidation(BypassBeanValidation bypassBeanValidation, FacesContext facesContext, ELHelper elHelper)
    {
        ValueBindingExpression bypassExpression;
        for (String currentExpression : bypassBeanValidation.conditions())
        {
            bypassExpression = new ValueBindingExpression(currentExpression);

            if (Boolean.TRUE.equals(elHelper.getValueOfExpression(facesContext, bypassExpression)))
            {
                BypassBeanValidationUtils.activateBypassAllValidationsForRequest(bypassBeanValidation);
                return;
            }
        }
    }

    private void processBeanValidation(BeanValidation beanValidation, String key, Object objectToInspect, String methodName)
    {
        List<Class> foundGroupsForPropertyValidation = new ArrayList<Class>();
        List<Class> restrictedGroupsForPropertyValidation = new ArrayList<Class>();
        List<ModelValidationEntry> modelValidationEntryList = new ArrayList<ModelValidationEntry>();
        List<Class> restrictedGroupsForModelValidation = new ArrayList<Class>();

        processMetaData(beanValidation,
                        objectToInspect,
                        foundGroupsForPropertyValidation,
                        restrictedGroupsForPropertyValidation,
                        modelValidationEntryList,
                        restrictedGroupsForModelValidation);

        ExtValBeanValidationContext extValBeanValidationContext = ExtValBeanValidationContext.getCurrentInstance();
        String currentViewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();

        processFoundGroups(extValBeanValidationContext, currentViewId, null,
                foundGroupsForPropertyValidation);

        processRestrictedGroups(extValBeanValidationContext, currentViewId, null,
                restrictedGroupsForPropertyValidation);

        initModelValidation(extValBeanValidationContext,
                currentViewId,
                null,
                new PropertyDetails(key, objectToInspect, methodName),
                modelValidationEntryList,
                restrictedGroupsForModelValidation);
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
        //required is a special case - reset it
        ((EditableValueHolder)uiComponent).setRequired(false);

        if (BypassBeanValidationUtils.bypassAllValidationsForRequest())
        {
            return;
        }

        super.processValidation(facesContext, uiComponent, convertedObject);
    }

    @Override
    protected void processFieldValidation(FacesContext facesContext, UIComponent uiComponent, Object convertedObject, PropertyInformation propertyInformation)
    {
        if(!BypassBeanValidationUtils.bypassFieldValidationsForRequest())
        {
            super.processFieldValidation(facesContext, uiComponent, convertedObject, propertyInformation);
        }
    }

    @Override
    protected void initModelValidation(ExtValBeanValidationContext extValBeanValidationContext, String currentViewId, UIComponent component, PropertyDetails propertyDetails, List<ModelValidationEntry> modelValidationEntryList, List<Class> restrictedGroupsForModelValidation)
    {
        if(!BypassBeanValidationUtils.bypassModelValidationsForRequest())
        {
            super.initModelValidation(extValBeanValidationContext, currentViewId, component, propertyDetails, modelValidationEntryList, restrictedGroupsForModelValidation);
        }
    }
}
