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
package at.gp.web.jsf.extval.validation.metadata.provider.interceptor;

import at.gp.web.jsf.extval.validation.metadata.provider.annotation.extractor.DefaultPropertyDetailsAwareExtractor;
import at.gp.web.jsf.extval.validation.metadata.provider.annotation.MetaDataProvider;
import at.gp.web.jsf.extval.validation.metadata.provider.MetaDataProviderStorage;
import org.apache.myfaces.extensions.validator.core.interceptor.MetaDataExtractionInterceptor;
import org.apache.myfaces.extensions.validator.core.metadata.MetaDataEntry;
import org.apache.myfaces.extensions.validator.core.property.PropertyDetails;
import org.apache.myfaces.extensions.validator.core.property.PropertyInformation;
import org.apache.myfaces.extensions.validator.core.property.PropertyInformationKeys;
import org.apache.myfaces.extensions.validator.internal.Priority;
import org.apache.myfaces.extensions.validator.internal.ToDo;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;
import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.util.ClassUtils;

import javax.faces.context.FacesContext;
import java.util.*;

/**
 * @author Gerhard Petracek
 *
 * @since 1.x.2
 */
@UsageInformation(UsageCategory.INTERNAL)
public class MetaDataProviderScanningInterceptor implements MetaDataExtractionInterceptor
{
    public void afterExtracting(PropertyInformation propertyInformation)
    {
        if (propertyInformation != null)
        {
            for (MetaDataEntry metaDataEntry : createAdditionalMetaDataEntries(propertyInformation.getInformation(PropertyInformationKeys.PROPERTY_DETAILS, PropertyDetails.class)))
            {
                metaDataEntry.setProperty(PropertyInformationKeys.PROPERTY_DETAILS, propertyInformation.getInformation(PropertyInformationKeys.PROPERTY_DETAILS));
                propertyInformation.addMetaDataEntry(metaDataEntry);
            }
        }
    }

    private MetaDataEntry[] createAdditionalMetaDataEntries(PropertyDetails propertyDetails)
    {
        List<Class> processedClasses = new ArrayList<Class>();
        List<MetaDataEntry> result = new ArrayList<MetaDataEntry>();
        String targetName = resolveTargetName(propertyDetails.getBaseObject().getClass());
        //create new object just to reuse extractor impl. - you can improve it, if you inspect the class itself
        Object targetObject = ClassUtils.tryToInstantiateClassForName(targetName);

        if (targetObject != null)
        {
            if(!processedClasses.contains(targetObject.getClass()))
            {
                result.addAll(Arrays.asList(extractMetaData(propertyDetails, targetObject)));
                processedClasses.add(targetObject.getClass());
            }
        }

        for(Class targetClass : resolveTargetClass(propertyDetails.getBaseObject().getClass()))
        {
            //create new object just to reuse extractor impl. - you can improve it, if you inspect the class itself
            targetObject = ClassUtils.tryToInstantiateClass(targetClass);

            if (targetObject != null &&
                    //the constraint provider has to extend the target -> more typesafe in view of refactorings
                    propertyDetails.getBaseObject().getClass().isAssignableFrom(targetObject.getClass()))
            {
                if(!processedClasses.contains(targetObject.getClass()))
                {
                    result.addAll(Arrays.asList(extractMetaData(propertyDetails, targetObject)));
                    processedClasses.add(targetObject.getClass());
                }
            }
        }
        return result.toArray(new MetaDataEntry[result.size()]);
    }

    private MetaDataEntry[] extractMetaData(PropertyDetails propertyDetails, Object targetObject)
    {
        if(targetObject.getClass().isAnnotationPresent(MetaDataProvider.class))
        {
            PropertyInformation propertyInformation = new DefaultPropertyDetailsAwareExtractor()
                    .extract(FacesContext.getCurrentInstance(), new PropertyDetails(propertyDetails.getKey(), targetObject, propertyDetails.getProperty()));
            return propertyInformation.getMetaDataEntries();
        }
        return null;
    }

    /**
     * method to map from the source class to the target class via name-convention
     * advantage: entity and constraint provider aren't tied together
     * disadvantage: not typesafe -> use @MetaDataProvider(MyEntity.class)
     *
     * @param sourceClassName class which hosts the property
     * @return target class name
     */
    private String resolveTargetName(Class sourceClassName)
    {
        return sourceClassName.getName() + "MetaData";
    }

    @ToDo(Priority.HIGH)
    private List<Class> resolveTargetClass(Class sourceClass)
    {
        List<Class> result = MetaDataProviderStorage.getInstance().getMetaDataProviderClassFor(sourceClass);

        return result != null ? result : new ArrayList<Class>();
    }
}
