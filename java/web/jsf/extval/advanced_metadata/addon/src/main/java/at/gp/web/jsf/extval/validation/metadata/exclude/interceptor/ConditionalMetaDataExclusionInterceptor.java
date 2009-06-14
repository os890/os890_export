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
package at.gp.web.jsf.extval.validation.metadata.exclude.interceptor;

import org.apache.myfaces.extensions.validator.core.interceptor.MetaDataExtractionInterceptor;
import org.apache.myfaces.extensions.validator.core.metadata.MetaDataEntry;
import org.apache.myfaces.extensions.validator.core.property.PropertyInformation;
import org.apache.myfaces.extensions.validator.core.el.ValueBindingExpression;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;
import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.lang.annotation.Annotation;

import at.gp.web.jsf.extval.validation.metadata.exclude.annotation.Exclude;
import at.gp.web.jsf.extval.validation.metadata.virtual.annotation.VirtualMetaData;

import javax.faces.context.FacesContext;

/**
 * @author Gerhard Petracek
 *
 * @since 1.x.2
 */
@UsageInformation(UsageCategory.INTERNAL)
public class ConditionalMetaDataExclusionInterceptor implements MetaDataExtractionInterceptor
{
    public void afterExtracting(PropertyInformation propertyInformation)
    {
        if (propertyInformation != null)
        {
            List<MetaDataEntry> conditionalConstraints = new ArrayList<MetaDataEntry>();
            List<Class> excludedConstraints = new ArrayList<Class>();
            Map<Class, MetaDataEntry> constraints = new HashMap<Class, MetaDataEntry>();

            for (MetaDataEntry metaDataEntry : propertyInformation.getMetaDataEntries())
            {
                if(metaDataEntry.getValue() instanceof Annotation)
                {
                    if(metaDataEntry.getValue() instanceof Exclude)
                    {
                        conditionalConstraints.add(metaDataEntry);
                    }
                    else if(metaDataEntry.getValue() instanceof Exclude.List)
                    {
                        for(Exclude condition : metaDataEntry.getValue(Exclude.List.class).value())
                        {
                            conditionalConstraints.add(cloneMetaDataEntryFor(condition, metaDataEntry));
                        }
                    }
                    else
                    {
                        constraints.put(metaDataEntry.getValue(Annotation.class).annotationType(), metaDataEntry);
                    }
                }
            }

            propertyInformation.resetMetaDataEntries();

            Class key;
            for(MetaDataEntry conditionEntry : conditionalConstraints)
            {
                if(useMetaData(conditionEntry.getValue(Exclude.class).conditions()))
                {
                    continue;
                }

                key = conditionEntry.getValue(Exclude.class).target();

                if(constraints.containsKey(key))
                {
                    constraints.remove(key);
                }
                excludedConstraints.add(key);
            }

            for(MetaDataEntry metaDataEntry : constraints.values())
            {
                if(metaDataEntry.getValue() instanceof VirtualMetaData)
                {
                    if(!excludedConstraints.contains(metaDataEntry.getValue(VirtualMetaData.class).target()))
                    {
                        propertyInformation.addMetaDataEntry(metaDataEntry);
                    }
                }
                else
                {
                    propertyInformation.addMetaDataEntry(metaDataEntry);
                }
            }
        }
    }

    private boolean useMetaData(String[] conditions)
    {
        ValueBindingExpression valueBindingExpression;
        for(String condition : conditions)
        {
            valueBindingExpression = new ValueBindingExpression(condition);
            if(Boolean.TRUE.equals(ExtValUtils.getELHelper().getValueOfExpression(FacesContext.getCurrentInstance(), valueBindingExpression)))
            {
                return false;
            }
        }

        return true;
    }

    private MetaDataEntry cloneMetaDataEntryFor(Exclude condition, MetaDataEntry metaDataEntry)
    {
        MetaDataEntry newEntry = new MetaDataEntry();
        newEntry.setProperty("", metaDataEntry.getProperty(""));
        newEntry.setKey(metaDataEntry.getKey());
        newEntry.setValue(condition);
        return newEntry;
    }

    @Override
    public boolean equals(Object target)
    {
        return target != null && target instanceof ConditionalMetaDataExclusionInterceptor;
    }
}
