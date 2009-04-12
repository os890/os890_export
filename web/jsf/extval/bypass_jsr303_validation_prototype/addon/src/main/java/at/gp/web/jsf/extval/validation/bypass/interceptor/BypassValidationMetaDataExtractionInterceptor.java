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

import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;
import org.apache.myfaces.extensions.validator.core.interceptor.MetaDataExtractionInterceptor;
import org.apache.myfaces.extensions.validator.core.property.PropertyInformation;
import org.apache.myfaces.extensions.validator.core.property.PropertyInformationKeys;
import org.apache.myfaces.extensions.validator.core.property.PropertyDetails;
import org.apache.myfaces.extensions.validator.core.metadata.MetaDataEntry;
import org.apache.myfaces.extensions.validator.core.el.ValueBindingExpression;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;
import at.gp.web.jsf.extval.validation.bypass.util.BypassValidationUtils;
import at.gp.web.jsf.extval.validation.bypass.annotation.BypassValidationController;
import at.gp.web.jsf.extval.validation.bypass.annotation.ViewId;
import at.gp.web.jsf.extval.validation.bypass.annotation.extractor.DefaultValidationControllerScanningExtractor;

import javax.faces.context.FacesContext;

/**
 * @author Gerhard Petracek
 */
@UsageInformation(UsageCategory.INTERNAL)
public class BypassValidationMetaDataExtractionInterceptor implements MetaDataExtractionInterceptor
{
    public void afterExtracting(PropertyInformation propertyInformation)
    {
        if(BypassValidationUtils.bypassAllSkipableValidationsForRequest())
        {
            //if there are different maps, a separate interceptor is needed
            //to add this information to every meta-data entry
            propertyInformation.setInformation(PropertyInformationKeys.SKIP_VALIDATION, true);
        }

        processValidationControllers(propertyInformation);
    }

    private void processValidationControllers(PropertyInformation propertyInformation)
    {
        ScanningResult result = scanPathToTarget(propertyInformation);

        if(result.isBypassAll())
        {
            propertyInformation.resetMetaDataEntries();
        }
        else if(result.isBypassAllSkipableValidations())
        {
            propertyInformation.setInformation(PropertyInformationKeys.SKIP_VALIDATION, true);
        }
    }

    private ScanningResult scanPathToTarget(PropertyInformation propertyInformation)
    {
        ScanningResult result = new ScanningResult();

        PropertyDetails propertyDetails = propertyInformation.getInformation(PropertyInformationKeys.PROPERTY_DETAILS, PropertyDetails.class);

        String[] key = propertyDetails.getKey().split("\\.");

        Object firstBean = ExtValUtils.getELHelper().getBean(key[0]);

        processClass(firstBean.getClass(), result);

        //first property
        processFieldsAndProperties(key[0] + "." + key[1], firstBean, key[1], result);

        //base object (of target property)
        processClass(propertyDetails.getBaseObject().getClass(), result);

        //last property
        processFieldsAndProperties(
                propertyDetails.getKey(),
                propertyDetails.getBaseObject(),
                propertyDetails.getProperty(),
                result);

        return result;
    }

    private void processClass(Class classToInspect, ScanningResult scanningResult)
    {
        while (!Object.class.getName().equals(classToInspect.getName()))
        {
            if (classToInspect.isAnnotationPresent(BypassValidationController.class))
            {
                addResultForCurrentView((BypassValidationController)classToInspect.getAnnotation(BypassValidationController.class), scanningResult);
            }

            classToInspect = classToInspect.getSuperclass();
        }
    }

    private void processFieldsAndProperties(
            String key, Object base, String property, ScanningResult scanningResult)
    {
        PropertyInformation propertyInformation = new DefaultValidationControllerScanningExtractor()
                .extract(FacesContext.getCurrentInstance(), new PropertyDetails(key, base, property));

        for (MetaDataEntry metaDataEntry : propertyInformation.getMetaDataEntries())
        {
            if (metaDataEntry.getValue() instanceof BypassValidationController)
            {
                addResultForCurrentView((BypassValidationController)metaDataEntry.getValue(), scanningResult);
            }
        }
    }

    private void addResultForCurrentView(BypassValidationController bypassValidationController, ScanningResult scanningResult)
    {
        for(ViewId viewId : bypassValidationController.value())
        {
            if(!processViewId(viewId))
            {
                continue;
            }

            if("*".equals(viewId.value()))
            {
                scanningResult.addViewId(FacesContext.getCurrentInstance().getViewRoot().getViewId(), viewId.all());
            }
            else
            {
                scanningResult.addViewId(viewId.value(), viewId.all());
            }
        }
    }

    private boolean processViewId(ViewId viewId)
    {
        ValueBindingExpression bypassExpression;
        for (String currentExpression : viewId.condition())
        {
            bypassExpression = new ValueBindingExpression(currentExpression);

            if (Boolean.TRUE.equals(ExtValUtils.getELHelper().getValueOfExpression(FacesContext.getCurrentInstance(), bypassExpression)))
            {
                return true;
            }
        }

        return false;
    }
}
