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
package at.gp.web.jsf.extval.validation.metadata.priority.interceptor;

import org.apache.myfaces.extensions.validator.core.interceptor.PropertyValidationInterceptor;
import org.apache.myfaces.extensions.validator.core.property.PropertyInformation;
import org.apache.myfaces.extensions.validator.core.metadata.MetaDataEntry;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;
import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import java.util.*;
import java.lang.annotation.Annotation;

import at.gp.web.jsf.extval.validation.metadata.priority.ValidationPriority;
import at.gp.web.jsf.extval.validation.metadata.priority.Priority;

/**
 * @author Gerhard Petracek
 *
 * @since 1.x.3
 */
@UsageInformation(UsageCategory.INTERNAL)
public class MetaDataPriorityInterceptor implements PropertyValidationInterceptor
{
    public boolean beforeValidation(FacesContext facesContext, UIComponent uiComponent, Object convertedObject, Map<String, Object> properties)
    {
        if(properties.containsKey(PropertyInformation.class.getName()))
        {
            PropertyInformation propertyInformation = (PropertyInformation)properties.get(PropertyInformation.class.getName());

            MetaDataEntry[] entries = propertyInformation.getMetaDataEntries();
            propertyInformation.resetMetaDataEntries();

            for(MetaDataEntry metaDataEntry : prioritizeEntries(entries))
            {
                propertyInformation.addMetaDataEntry(metaDataEntry);
            }
        }
        return true;
    }

    //simple implementation - just for demonstration
    private MetaDataEntry[] prioritizeEntries(MetaDataEntry[] metaDataEntries)
    {
        List<MetaDataEntry> highestPriorityList = new ArrayList<MetaDataEntry>();
        List<MetaDataEntry> highPriorityList = new ArrayList<MetaDataEntry>();
        List<MetaDataEntry> importantPriorityList = new ArrayList<MetaDataEntry>();
        List<MetaDataEntry> mediumPriorityList = new ArrayList<MetaDataEntry>();
        List<MetaDataEntry> lowPriorityList = new ArrayList<MetaDataEntry>();
        List<MetaDataEntry> lowestPriorityList = new ArrayList<MetaDataEntry>();
        List<MetaDataEntry> noPriorityList = new ArrayList<MetaDataEntry>();

        List<MetaDataEntry> noPriorityAvailableList = new ArrayList<MetaDataEntry>();
        List<MetaDataEntry> result = new ArrayList<MetaDataEntry>();

        List<Priority> parameters;
        for(MetaDataEntry metaDataEntry : metaDataEntries)
        {
            if(metaDataEntry.getValue() instanceof Annotation)
            {
                parameters = ExtValUtils.getValidationParameterExtractor().extract(
                        ((Annotation)metaDataEntry.getValue()),
                        ValidationPriority.class,
                        Priority.class);

                if(parameters == null || parameters.size() == 0)
                {
                    noPriorityAvailableList.add(metaDataEntry);
                }
                else if(parameters.size() > 0)
                {
                    switch (parameters.iterator().next())
                    {
                        case HIGHEST:
                            highestPriorityList.add(metaDataEntry);
                            continue;
                        case HIGH:
                            highPriorityList.add(metaDataEntry);
                            continue;
                        case IMPORTANT:
                            importantPriorityList.add(metaDataEntry);
                            continue;
                        case MEDIUM:
                            mediumPriorityList.add(metaDataEntry);
                            continue;
                        case LOW:
                            lowPriorityList.add(metaDataEntry);
                            continue;
                        case LOWEST:
                            lowestPriorityList.add(metaDataEntry);
                            continue;
                        case NO:
                            noPriorityList.add(metaDataEntry);
                    }
                }
            }
        }

        //build result
        result.addAll(highestPriorityList);
        result.addAll(highPriorityList);
        result.addAll(importantPriorityList);
        result.addAll(mediumPriorityList);
        result.addAll(lowPriorityList);
        result.addAll(lowestPriorityList);
        result.addAll(noPriorityAvailableList);
        result.addAll(noPriorityList);

        return result.toArray(new MetaDataEntry[result.size()]);
    }

    public void afterValidation(FacesContext facesContext, UIComponent uiComponent, Object convertedObject, Map<String, Object> properties)
    {
        //do nothing
    }
}
