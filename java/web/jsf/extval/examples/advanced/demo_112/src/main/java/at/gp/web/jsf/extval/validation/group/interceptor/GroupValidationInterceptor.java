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
package at.gp.web.jsf.extval.validation.group.interceptor;

import org.apache.myfaces.extensions.validator.core.interceptor.PropertyValidationInterceptor;
import org.apache.myfaces.extensions.validator.core.property.PropertyInformation;
import org.apache.myfaces.extensions.validator.core.metadata.MetaDataEntry;
import org.apache.myfaces.extensions.validator.util.ClassUtils;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.component.EditableValueHolder;
import javax.faces.validator.Validator;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.lang.annotation.Annotation;

import at.gp.web.jsf.extval.validation.group.ExtValGroupValidation;
import at.gp.web.jsf.extval.validation.group.Group;

public class GroupValidationInterceptor implements PropertyValidationInterceptor
{
    public boolean beforeValidation(FacesContext facesContext, UIComponent uiComponent, Object convertedObject, Map<String, Object> properties)
    {
        EditableValueHolder inputComponent = (EditableValueHolder)uiComponent;
        List<Class> groupsToValidate = new ArrayList<Class>();

        String groupName;
        Class group;
        for(Validator validator : inputComponent.getValidators())
        {
            if(validator instanceof ExtValGroupValidation)
            {
                groupName = ((ExtValGroupValidation)validator).getValue().trim();
                group = ClassUtils.tryToLoadClassForName(groupName);

                if(group == null || !(Group.class.isAssignableFrom(group)))
                {
                    throw new IllegalStateException(groupName + " is no valid group");
                }

                groupsToValidate.add(group);
            }
        }

        if(!groupsToValidate.isEmpty())
        {
            PropertyInformation propertyInformation = (PropertyInformation)properties.get(PropertyInformation.class.getName());

            MetaDataEntry[] metaDataEntries = propertyInformation.getMetaDataEntries();

            propertyInformation.resetMetaDataEntries();

            applySimpleGroupFilter(groupsToValidate, propertyInformation, metaDataEntries);
        }
        return true;
    }

    private void applySimpleGroupFilter(List<Class> groupsToValidate, PropertyInformation propertyInformation, MetaDataEntry[] metaDataEntries)
    {
        List<Class> foundGroups;
        for(MetaDataEntry entry : metaDataEntries)
        {
            if(entry.getValue() instanceof Annotation)
            {
                foundGroups = ExtValUtils.getValidationParameterExtractor().extract(entry.getValue(Annotation.class), Group.class, Class.class);

                for(Class currentGroup : foundGroups)
                {
                    if(groupsToValidate.contains(currentGroup))
                    {
                        propertyInformation.addMetaDataEntry(entry);
                        break;
                    }
                }
            }
        }
    }

    public void afterValidation(FacesContext facesContext, UIComponent uiComponent, Object convertedObject, Map<String, Object> properties)
    {
        //do nothing
    }
}
