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

import at.gp.web.jsf.extval.validation.group.Group;
import org.apache.myfaces.extensions.validator.core.interceptor.MetaDataExtractionInterceptor;
import org.apache.myfaces.extensions.validator.core.metadata.MetaDataEntry;
import org.apache.myfaces.extensions.validator.core.property.PropertyInformation;
import org.apache.myfaces.extensions.validator.core.storage.GroupStorage;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;

import javax.faces.context.FacesContext;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

public class SimpleGroupValidationMetaDataExtractionInterceptor implements MetaDataExtractionInterceptor
{
    public void afterExtracting(PropertyInformation propertyInformation)
    {
        GroupStorage groupStorage = ExtValUtils.getStorage(GroupStorage.class, Group.class.getName());

        Class[] groupsToValidate = groupStorage.getGroups(FacesContext.getCurrentInstance().getViewRoot().getViewId(), "[current]");

        MetaDataEntry[] metaDataEntries = propertyInformation.getMetaDataEntries();

        propertyInformation.resetMetaDataEntries();

        if(groupsToValidate != null)
        {
            applySimpleGroupFilter(Arrays.asList(groupsToValidate), propertyInformation, metaDataEntries);
        }
    }

    private void applySimpleGroupFilter(List<Class> groupsToValidate, PropertyInformation propertyInformation, MetaDataEntry[] metaDataEntries)
    {
        List<Class> foundGroups;
        for (MetaDataEntry entry : metaDataEntries)
        {
            if (entry.getValue() instanceof Annotation)
            {
                foundGroups = ExtValUtils.getValidationParameterExtractor().extract(entry.getValue(Annotation.class), Group.class, Class.class);

                for (Class currentGroup : foundGroups)
                {
                    if (groupsToValidate.contains(currentGroup))
                    {
                        propertyInformation.addMetaDataEntry(entry);
                        break;
                    }
                }
            }
        }
    }
}