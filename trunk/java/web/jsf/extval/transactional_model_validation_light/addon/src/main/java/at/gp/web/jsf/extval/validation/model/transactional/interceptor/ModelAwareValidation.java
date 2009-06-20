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
package at.gp.web.jsf.extval.validation.model.transactional.interceptor;

import at.gp.web.jsf.extval.validation.model.transactional.ModelValidationEntry;
import at.gp.web.jsf.extval.validation.model.transactional.TransactionalModelValidationContext;
import org.apache.myfaces.extensions.validator.core.interceptor.PropertyValidationInterceptor;
import org.apache.myfaces.extensions.validator.core.metadata.MetaDataEntry;
import org.apache.myfaces.extensions.validator.core.property.PropertyInformation;
import org.apache.myfaces.extensions.validator.core.property.PropertyDetails;
import org.apache.myfaces.extensions.validator.core.property.PropertyInformationKeys;
import org.apache.myfaces.extensions.validator.core.validation.parameter.ParameterValue;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;
import org.apache.myfaces.extensions.validator.internal.UsageInformation;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Gerhard Petracek
 * @since 1.x.3
 */
@UsageInformation(UsageCategory.API)
public class ModelAwareValidation implements PropertyValidationInterceptor
{
    private static ModelAwareValidation modelAwareValidation;

    @ParameterValue
    public ModelAwareValidation getInstance()
    {
        if (modelAwareValidation == null)
        {
            modelAwareValidation = new ModelAwareValidation();
        }
        return modelAwareValidation;
    }

    public boolean beforeValidation(FacesContext facesContext, UIComponent uiComponent, Object convertedObject, Map<String, Object> properties)
    {
        //if interceptor is used manually as constraint parameter
        if(isLocalValidation(properties))
        {
            createTransactionalModelValidationEntry(uiComponent, properties);
            return false;
        }

        createModelValidationEntryForClassLevelValidation(uiComponent, properties);
        return true;
    }

    private boolean isLocalValidation(Map<String, Object> properties)
    {
        return properties.get(Annotation.class.getName()) != null;
    }

    private void createTransactionalModelValidationEntry(UIComponent uiComponent, Map<String, Object> properties)
    {
        if (uiComponent instanceof EditableValueHolder &&
                properties.get(PropertyInformation.class.getName()) != null &&
                properties.get(Annotation.class.getName()) != null)
        {
            PropertyInformation propertyInformation = (PropertyInformation) properties.get(PropertyInformation.class.getName());
            Annotation annotation = (Annotation) properties.get(Annotation.class.getName());

            ModelValidationEntry modelValidationEntry = new ModelValidationEntry();
            modelValidationEntry.setComponent(uiComponent);
            modelValidationEntry.setOldValue(((EditableValueHolder) uiComponent).getValue());
            modelValidationEntry.setProperties(properties);
            modelValidationEntry.setClassLevelConstraint(false);

            for (MetaDataEntry metaDataEntry : propertyInformation.getMetaDataEntries())
            {
                if (annotation.equals(metaDataEntry.getValue()))
                {
                    modelValidationEntry.setMetaDataEntry(metaDataEntry);
                    TransactionalModelValidationContext.getContext().addModelValidationEntry(modelValidationEntry);
                    break;
                }
            }
        }
    }

    private void createModelValidationEntryForClassLevelValidation(UIComponent uiComponent, Map<String, Object> properties)
    {
        if (uiComponent instanceof EditableValueHolder &&
                properties.get(PropertyInformation.class.getName()) != null)
        {
            PropertyInformation propertyInformation = (PropertyInformation) properties.get(PropertyInformation.class.getName());
            PropertyDetails propertyDetails = propertyInformation.getInformation(PropertyInformationKeys.PROPERTY_DETAILS, PropertyDetails.class);

            Object baseObject = propertyDetails.getBaseObject();

            ModelValidationEntry modelValidationEntry;
            MetaDataEntry modelMetaDataEntry;
            for(Annotation classConstraint : getClassLevelConstraints(baseObject.getClass()))
            {
                modelValidationEntry = new ModelValidationEntry();
                modelValidationEntry.setComponent(uiComponent);
                modelValidationEntry.setOldValue(((EditableValueHolder) uiComponent).getValue());
                modelValidationEntry.setProperties(properties);
                modelValidationEntry.setClassLevelConstraint(true);

                modelMetaDataEntry = new MetaDataEntry();
                modelMetaDataEntry.setKey(classConstraint.annotationType().getName());
                modelMetaDataEntry.setValue(classConstraint);
                //just to forward the original information
                modelMetaDataEntry.setProperty(Object.class.getName(), baseObject);
                modelMetaDataEntry.setProperty(PropertyInformationKeys.PROPERTY_DETAILS, propertyDetails);

                modelValidationEntry.setMetaDataEntry(modelMetaDataEntry);
                TransactionalModelValidationContext.getContext().addModelValidationEntry(modelValidationEntry);
            }
        }
    }

    private List<Annotation> getClassLevelConstraints(Class targetClass)
    {
        List<Annotation> result = new ArrayList<Annotation>();

        Class currentClass = targetClass;
        while (!Object.class.getName().equals(currentClass.getName()))
        {
            result.addAll(Arrays.asList(currentClass.getAnnotations()));
            processInterfaces(currentClass, result);

            currentClass = currentClass.getSuperclass();
        }

        return result;
    }

    private void processInterfaces(Class currentClass, List<Annotation> classLevelAnnotations)
    {
        for (Class currentInterface : currentClass.getInterfaces())
        {
            classLevelAnnotations.addAll(Arrays.asList(currentInterface.getAnnotations()));

            processInterfaces(currentInterface, classLevelAnnotations);
        }
    }

    public void afterValidation(FacesContext facesContext, UIComponent uiComponent, Object convertedObject, Map<String, Object> properties)
    {
        //do nothing
    }
}
