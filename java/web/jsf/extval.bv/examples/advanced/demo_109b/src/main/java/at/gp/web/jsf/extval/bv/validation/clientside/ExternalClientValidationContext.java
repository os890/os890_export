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
package at.gp.web.jsf.extval.bv.validation.clientside;

import at.gp.web.jsf.extval.bv.validation.clientside.script.ScriptBuilder;
import org.apache.myfaces.extensions.validator.beanval.ExtValBeanValidationContext;
import org.apache.myfaces.extensions.validator.beanval.util.BeanValidationUtils;
import org.apache.myfaces.extensions.validator.beanval.validation.strategy.BeanValidationVirtualValidationStrategy;
import org.apache.myfaces.extensions.validator.core.metadata.MetaDataEntry;
import org.apache.myfaces.extensions.validator.core.metadata.transformer.MetaDataTransformer;
import org.apache.myfaces.extensions.validator.core.property.PropertyDetails;
import org.apache.myfaces.extensions.validator.core.property.PropertyInformation;
import org.apache.myfaces.extensions.validator.core.property.PropertyInformationKeys;
import org.apache.myfaces.extensions.validator.internal.Priority;
import org.apache.myfaces.extensions.validator.internal.ToDo;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;
import org.apache.myfaces.extensions.validator.util.ProxyUtils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.validation.groups.Default;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.ElementDescriptor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Gerhard Petracek
 */
public class ExternalClientValidationContext
{
    private Map<String, PropertyInformation> extractedMetaData = new HashMap<String, PropertyInformation>();
    //input component client id -> message component client id
    private Map<String, String> markerComponentIdMapping = new HashMap<String, String>();
    private ScriptBuilder scriptBuilder;

    public static ExternalClientValidationContext getCurrentInstance()
    {
        return getCurrentInstance(FacesContext.getCurrentInstance());
    }

    public static ExternalClientValidationContext getCurrentInstance(FacesContext facesContext)
    {
        Map requestMap = facesContext.getExternalContext().getRequestMap();
        ExternalClientValidationContext context =
                (ExternalClientValidationContext) requestMap.get(ExternalClientValidationContext.class.getName());

        if (context == null)
        {
            context = startContext();
        }

        return context;
    }

    public void addProperty(String clientId, PropertyInformation result)
    {
        if (result.getMetaDataEntries() != null && result.getMetaDataEntries().length > 0)
        {
            this.extractedMetaData.put(clientId, result);
        }
    }

    public void addMarkerComponent(String clientId, String markerClientId)
    {
        this.markerComponentIdMapping.put(clientId, markerClientId);
    }

    public String getClientScript()
    {
        StringBuilder result = new StringBuilder();
        StringBuilder resetScriptBody = new StringBuilder();
        result.append(this.scriptBuilder.buildScriptStart());

        FacesContext fc = FacesContext.getCurrentInstance();
        for (Map.Entry<String, PropertyInformation> currentEntry : this.extractedMetaData.entrySet())
        {
            if (hasBeanValidationConstraints(currentEntry.getValue()))
            {
                result.append(this.scriptBuilder.buildValidationScript(currentEntry.getKey(),
                        extractMetaData(fc, findComponent(fc, currentEntry), getPropertyDetails(currentEntry)),
                        this.markerComponentIdMapping.get(currentEntry.getKey())));

                //TODO refactor it!
                resetScriptBody.append(this.scriptBuilder.buildResetScript(currentEntry.getKey(),
                        extractMetaData(fc, findComponent(fc, currentEntry), getPropertyDetails(currentEntry)),
                        this.markerComponentIdMapping.get(currentEntry.getKey())));
            }
        }

        result.append("ExtValClientValidator.resetStyles = function(){");
        result.append("var currentNode;");
        result.append(resetScriptBody);
        result.append("};");

        result.append(this.scriptBuilder.buildScriptEnd());
        return result.toString();
    }

    private PropertyDetails getPropertyDetails(Map.Entry<String, PropertyInformation> currentEntry)
    {
        return currentEntry.getValue()
                .getInformation(PropertyInformationKeys.PROPERTY_DETAILS, PropertyDetails.class);
    }

    private UIComponent findComponent(FacesContext facesContext, Map.Entry<String, PropertyInformation> currentEntry)
    {
        return facesContext.getViewRoot().findComponent(currentEntry.getKey());
    }

    private Map<String, Object> extractMetaData(
            FacesContext facesContext, UIComponent uiComponent, PropertyDetails propertyDetails)
    {
        Class[] foundGroups = resolveGroups(facesContext, uiComponent);

        if (foundGroups == null)
        {
            return Collections.emptyMap();
        }
        else if (foundGroups.length == 0)
        {
            foundGroups = new Class[]{Default.class};
        }

        Class targetClass = propertyDetails.getBaseObject().getClass();

        targetClass = ProxyUtils.getUnproxiedClass(targetClass);

        ElementDescriptor elementDescriptor = BeanValidationUtils.getElementDescriptor(
                targetClass, propertyDetails.getProperty());

        if (elementDescriptor == null)
        {
            return Collections.emptyMap();
        }

        return getTransformedMetaData(elementDescriptor, foundGroups);
    }

    private Class[] resolveGroups(FacesContext facesContext, UIComponent uiComponent)
    {
        return ExtValBeanValidationContext.getCurrentInstance().getGroups(
                facesContext.getViewRoot().getViewId(),
                uiComponent.getClientId(facesContext));
    }

    private Map<String, Object> getTransformedMetaData(ElementDescriptor elementDescriptor, Class... foundGroups)
    {
        Map<String, Object> metaData = new HashMap<String, Object>();

        for (ConstraintDescriptor<?> constraintDescriptor :
                elementDescriptor.findConstraints().unorderedAndMatchingGroups(foundGroups).getConstraintDescriptors())
        {
            metaData.putAll(transformConstraintDescriptorToMetaData(
                    constraintDescriptor, elementDescriptor.getElementClass()));
        }

        return metaData;
    }

    @ToDo(value = Priority.MEDIUM, description = "ConstraintDescriptor#isReportAsSingleViolation")
    private Map<String, Object> transformConstraintDescriptorToMetaData(
            ConstraintDescriptor<?> constraintDescriptor, Class elementClass)
    {
        Map<String, Object> result = new HashMap<String, Object>();
        MetaDataTransformer metaDataTransformer;

        metaDataTransformer = ExtValUtils.getMetaDataTransformerForValidationStrategy(
                new BeanValidationVirtualValidationStrategy(constraintDescriptor, elementClass));

        if (metaDataTransformer != null)
        {
            result.putAll(transformMetaData(metaDataTransformer, constraintDescriptor));
        }

        if (!constraintDescriptor.isReportAsSingleViolation())
        {
            Set<ConstraintDescriptor<?>> composingConstraints = constraintDescriptor.getComposingConstraints();
            if (composingConstraints != null && !composingConstraints.isEmpty())
            {
                result.putAll(transformComposingConstraints(composingConstraints, elementClass));
            }
        }

        return result;
    }

    private Map<String, Object> transformComposingConstraints(
            Set<ConstraintDescriptor<?>> composingConstraints, Class elementClass)
    {
        Map<String, Object> result = new HashMap<String, Object>();
        for (ConstraintDescriptor constraintDescriptor : composingConstraints)
        {
            result.putAll(transformConstraintDescriptorToMetaData(constraintDescriptor, elementClass));
        }

        return result;
    }

    private Map<String, Object> transformMetaData(
            MetaDataTransformer metaDataTransformer, ConstraintDescriptor<?> constraintDescriptor)
    {
        MetaDataEntry entry;
        Map<String, Object> result;

        entry = new MetaDataEntry();
        entry.setKey(constraintDescriptor.getAnnotation().annotationType().getName());
        entry.setValue(constraintDescriptor);

        result = metaDataTransformer.convertMetaData(entry);
        return result;
    }

    private boolean hasBeanValidationConstraints(PropertyInformation propertyInformation)
    {
        PropertyDetails propertyDetails = ExtValUtils.getPropertyDetails(propertyInformation);

        Class targetClass = ProxyUtils.getUnproxiedClass(propertyDetails.getBaseObject().getClass());

        return BeanValidationUtils.getElementDescriptor(targetClass, propertyDetails.getProperty()) != null;
    }

    public void setScriptBuilder(ScriptBuilder scriptBuilder)
    {
        this.scriptBuilder = scriptBuilder;
    }

    public ScriptBuilder getScriptBuilder()
    {
        return scriptBuilder;
    }

    @SuppressWarnings({"unchecked"})
    public static ExternalClientValidationContext startContext()
    {
        Map requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();

        ExternalClientValidationContext context = new ExternalClientValidationContext();
        requestMap.put(ExternalClientValidationContext.class.getName(), context);

        return context;
    }
}
