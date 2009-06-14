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
package at.gp.web.jsf.extval.validation.metadata.virtual.interceptor;

import org.apache.myfaces.extensions.validator.core.interceptor.MetaDataExtractionInterceptor;
import org.apache.myfaces.extensions.validator.core.metadata.MetaDataEntry;
import org.apache.myfaces.extensions.validator.core.property.PropertyInformation;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;
import org.apache.myfaces.extensions.validator.internal.UsageInformation;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.lang.annotation.Annotation;

import at.gp.web.jsf.extval.validation.metadata.virtual.annotation.VirtualMetaData;

/**
 * @author Gerhard Petracek
 *
 * @since 1.x.3
 */
@UsageInformation(UsageCategory.INTERNAL)
public class VirtualMetaDataInterceptor implements MetaDataExtractionInterceptor
{
    public void afterExtracting(PropertyInformation propertyInformation)
    {
        if (propertyInformation != null)
        {
            List<MetaDataEntry> virtualConstraints = new ArrayList<MetaDataEntry>();
            Map<Class, MetaDataEntry> constraints = new HashMap<Class, MetaDataEntry>();

            for (MetaDataEntry metaDataEntry : propertyInformation.getMetaDataEntries())
            {
                if(metaDataEntry.getValue() instanceof Annotation)
                {
                    if(metaDataEntry.getValue() instanceof VirtualMetaData)
                    {
                        virtualConstraints.add(metaDataEntry);
                    }
                    else
                    {
                        constraints.put(metaDataEntry.getValue(Annotation.class).annotationType(), metaDataEntry);
                    }
                }
            }

            propertyInformation.resetMetaDataEntries();

            MetaDataEntry targetEntry;
            Class key;
            for(MetaDataEntry metaDataEntry : virtualConstraints)
            {
                key = metaDataEntry.getValue(VirtualMetaData.class).target();

                if(constraints.containsKey(key))
                {
                    targetEntry = constraints.get(key);

                    metaDataEntry.setProperty(VirtualMetaData.TARGET, targetEntry);
                    propertyInformation.addMetaDataEntry(metaDataEntry);

                    constraints.remove(key);
                }
            }

            for(MetaDataEntry metaDataEntry : constraints.values())
            {
                propertyInformation.addMetaDataEntry(metaDataEntry);
            }
        }
    }
}
