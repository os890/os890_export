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
package at.gp.web.jsf.extval.beanval.tag;

import org.apache.myfaces.extensions.validator.beanval.ExtValBeanValidationContext;
import org.apache.myfaces.extensions.validator.beanval.annotation.ModelValidation;
import org.apache.myfaces.extensions.validator.beanval.storage.ModelValidationEntry;
import org.apache.myfaces.extensions.validator.util.ClassUtils;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;
import org.apache.myfaces.extensions.validator.internal.ToDo;
import org.apache.myfaces.extensions.validator.internal.Priority;
import org.apache.myfaces.extensions.validator.core.property.PropertyDetails;
import org.apache.myfaces.extensions.validator.core.el.ValueBindingExpression;

import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import java.util.List;
import java.util.ArrayList;

/**
 * facelets validator tag as alternative to @BeanValidation and @ModelValidation
 */
@ToDo(Priority.MEDIUM)
public class ExtValBeanValidatorTag implements Validator
{
    private String useGroups = "";
    private String restrictGroups = "";
    private String useModelValidation = "false";
    private String conditionResults = "true";

    private String displayInline = "false";
    private String message = ModelValidation.DEFAULT;
    private String validationTargets = ModelValidation.DEFAULT;

    public void validate(FacesContext facesContext, UIComponent uiComponent, Object o) throws ValidatorException
    {
        //do nothing
    }

    public void processExtValBeanValidationMetaData(FacesContext facesContext, UIComponent uiComponent, boolean tryToProcessModelValidation)
    {
        if(!processTag())
        {
            return;
        }

        if("true".equalsIgnoreCase(this.useModelValidation.trim()) && tryToProcessModelValidation)
        {
            createModelValidationEntry(facesContext, uiComponent);
        }
        else
        {
            addGroupsToContext(facesContext, uiComponent);
            restrictGroups(facesContext, uiComponent);
        }
    }

    private boolean processTag()
    {
        for(String condition : this.conditionResults.split(" "))
        {
            if(Boolean.FALSE.equals(Boolean.valueOf(condition.trim())))
            {
                return false;
            }
        }

        return true;
    }

    private void createModelValidationEntry(FacesContext facesContext, UIComponent uiComponent)
    {
        ModelValidationEntry modelValidationEntry = new ModelValidationEntry();
        modelValidationEntry.setComponent(uiComponent);
        setMetaDataSource(modelValidationEntry);
        modelValidationEntry.setDisplayMessageInline(Boolean.valueOf(this.displayInline));
        tryToSetCustomMessage(modelValidationEntry);
        addGroups(modelValidationEntry);
        restrictGroups(modelValidationEntry);
        processValidationTargetExpressions(facesContext, modelValidationEntry);
        if(modelValidationEntry.getValidationTargets().isEmpty())
        {
            modelValidationEntry.addValidationTarget(modelValidationEntry.getMetaDataSourceObject());
        }
        getContext().addModelValidationEntry(modelValidationEntry);
    }

    private void setMetaDataSource(ModelValidationEntry modelValidationEntry)
    {
        PropertyDetails propertyDetails = ExtValUtils.getELHelper().getPropertyDetailsOfValueBinding(modelValidationEntry.getComponent());
        modelValidationEntry.setMetaDataSourceObject(propertyDetails.getBaseObject());
    }

    private void tryToSetCustomMessage(ModelValidationEntry modelValidationEntry)
    {
        if(!ModelValidation.DEFAULT.equals(this.message))
        {
            modelValidationEntry.setCustomMessage(this.message);
        }
    }

    private void addGroups(ModelValidationEntry modelValidationEntry)
    {
        String[] groups = this.useGroups.split(",");

        Class groupClass;
        for(String group : groups)
        {
            groupClass = ClassUtils.tryToLoadClassForName(group.trim());

            if(groupClass != null)
            {
                modelValidationEntry.addGroup(groupClass);
            }
        }
    }

    private List<Class> getRestrictedGroups()
    {
        String groups[] = this.restrictGroups.split(",");
        List<Class> result = new ArrayList<Class>();

        Class foundGroup;
        for(String group : groups)
        {
            foundGroup = ClassUtils.tryToLoadClassForName(group.trim());
            if(foundGroup != null)
            {
                result.add(foundGroup);
            }
        }
        return result;
    }

    private void processValidationTargetExpressions(FacesContext facesContext, ModelValidationEntry modelValidationEntry)
    {
        String[] targets = this.validationTargets.split("@");

        ValueBindingExpression valueBindingExpression;
        for(String target : targets)
        {
            if("".equals(target) ||  ModelValidation.DEFAULT.equals(target))
            {
                continue;
            }
            valueBindingExpression = new ValueBindingExpression("#" + target.trim());

            modelValidationEntry.addValidationTarget(ExtValUtils.getELHelper().getValueOfExpression(facesContext, valueBindingExpression));
        }
    }

    private void addGroupsToContext(FacesContext facesContext, UIComponent uiComponent)
    {
        String groups[] = this.useGroups.split(",");

        Class foundGroup;
        for(String group : groups)
        {
            foundGroup = ClassUtils.tryToLoadClassForName(group.trim());
            if(foundGroup != null)
            {
                getContext().addGroup(
                        foundGroup, getCurrentViewId(facesContext), getClientId(facesContext, uiComponent));
            }
        }
    }

    private void restrictGroups(FacesContext facesContext, UIComponent uiComponent)
    {
        for(Class groupClass : getRestrictedGroups())
        {
            getContext().restrictGroup(
                    groupClass, getCurrentViewId(facesContext), getClientId(facesContext, uiComponent));
        }
    }

    private void restrictGroups(ModelValidationEntry modelValidationEntry)
    {
        for(Class restrictedGroup : getRestrictedGroups())
        {
            modelValidationEntry.removeGroup(restrictedGroup);
        }
    }

    private String getCurrentViewId(FacesContext facesContext)
    {
        return facesContext.getViewRoot().getViewId();
    }

    private String getClientId(FacesContext facesContext, UIComponent uiComponent)
    {
        return uiComponent.getClientId(facesContext);
    }

    private ExtValBeanValidationContext getContext()
    {
        return ExtValBeanValidationContext.getCurrentInstance();
    }

    public void setUseGroups(String useGroups)
    {
        this.useGroups = useGroups;
    }

    public void setRestrictGroups(String restrictGroups)
    {
        this.restrictGroups = restrictGroups;
    }

    public void setConditions(String conditionResults)
    {
        this.conditionResults = conditionResults;
    }

    public void setUseModelValidation(String useModelValidation)
    {
        this.useModelValidation = useModelValidation;
    }

    public void setDisplayInline(String displayInline)
    {
        this.displayInline = displayInline;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public void setValidationTargets(String validationTargets)
    {
        this.validationTargets = validationTargets;
    }
}
