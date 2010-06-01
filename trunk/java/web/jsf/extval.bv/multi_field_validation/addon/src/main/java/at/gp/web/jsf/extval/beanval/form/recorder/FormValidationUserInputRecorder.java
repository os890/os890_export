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
package at.gp.web.jsf.extval.beanval.form.recorder;

import at.gp.web.jsf.extval.beanval.form.storage.FormBeanClassStorage;
import at.gp.web.jsf.extval.beanval.form.storage.ProcessedInformationStorage;
import at.gp.web.jsf.extval.beanval.form.storage.ProcessedInformationStorageEntry;
import at.gp.web.jsf.extval.beanval.form.util.FormValidationUtils;
import org.apache.myfaces.extensions.validator.core.el.ELHelper;
import org.apache.myfaces.extensions.validator.core.el.ValueBindingExpression;
import org.apache.myfaces.extensions.validator.core.property.PropertyDetails;
import org.apache.myfaces.extensions.validator.core.recorder.ProcessedInformationRecorder;
import org.apache.myfaces.extensions.validator.internal.Priority;
import org.apache.myfaces.extensions.validator.internal.ToDo;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Gerhard Petracek
 */
public class FormValidationUserInputRecorder implements ProcessedInformationRecorder
{
    public void recordUserInput(UIComponent uiComponent, Object value)
    {
        if (!(uiComponent instanceof EditableValueHolder))
        {
            return;
        }

        //to support local cross-validation (within the same entity)
        ProcessedInformationStorage processedInformationStorage = FormValidationUtils.getOrInitProcessedInformationStorage();

        ProcessedInformationStorageEntry entry;

        ELHelper elHelper = ExtValUtils.getELHelper();
        FacesContext facesContext = FacesContext.getCurrentInstance();

        PropertyDetails propertyDetails = elHelper.getPropertyDetailsOfValueBinding(uiComponent);

        if (propertyDetails == null)
        {
            return;
        }

        String key = propertyDetails.getKey();
        String clientId = uiComponent.getClientId(facesContext);
        String internalClientId = createClientId(facesContext, uiComponent, true);
        String formClientId = getFormClientId(facesContext, uiComponent);

        entry = new ProcessedInformationStorageEntry();
        entry.setRootBean(elHelper.getValueOfExpression(facesContext, getNameOfRootBean(key)));
        entry.setLeafBean(propertyDetails.getBaseObject());
        entry.setConvertedValue(value);
        entry.setComponent(uiComponent);
        entry.setClientId(clientId);
        entry.setFormClientId(formClientId);
        entry.setPropertyDetails(propertyDetails);

        ProcessedInformationStorageEntry existingEntry = processedInformationStorage.findEntry(propertyDetails, formClientId, uiComponent.getId());
        addEntriesForValidation(entry, existingEntry, processedInformationStorage, formClientId, internalClientId);

        addFormBeanClassStorageEntry(entry);
    }

    private void addFormBeanClassStorageEntry(ProcessedInformationStorageEntry entry)
    {
        FormBeanClassStorage formBeanClassStorage = FormValidationUtils.getOrInitFormBeanClassStorage();

        FormValidationUtils.registerFormValidators(formBeanClassStorage, entry.getRootBean().getClass());

        if (!entry.getRootBean().equals(entry.getLeafBean()))
        {
            FormValidationUtils.registerFormValidators(formBeanClassStorage, entry.getLeafBean().getClass());
        }
    }

    private void addEntriesForValidation(ProcessedInformationStorageEntry currentEntry,
                                         ProcessedInformationStorageEntry existingEntry,
                                         ProcessedInformationStorage processedInformationStorage,
                                         String formClientId,
                                         String internalClientId)
    {
        //for local cross-validation
        if (existingEntry != null)
        {
            //for the validation within a complex component e.g. a table
            //don't override existing expression (style: #{entry.property}) - make a special mapping

            List<ProcessedInformationStorageEntry> furtherEntries = existingEntry.getFurtherEntries();

            if (furtherEntries == null)
            {
                furtherEntries = new ArrayList<ProcessedInformationStorageEntry>();

                existingEntry.setFurtherEntries(furtherEntries);
            }

            furtherEntries.add(currentEntry);
        }
        else
        {
            processedInformationStorage.addEntry(formClientId, internalClientId, currentEntry);
        }
    }

    @ToDo(value = Priority.HIGH, description = "find form -> if isPrependId is false use it as internal prefix if it isn't part of the client-id")
    private String createClientId(FacesContext facesContext, UIComponent uiComponent, boolean forceFormIdAsPrefix)
    {
        return uiComponent.getClientId(facesContext);
    }

    private ValueBindingExpression getNameOfRootBean(String propertyPath)
    {
        return new ValueBindingExpression("#{" + propertyPath.substring(0, propertyPath.indexOf(".")) + "}");
    }

    private String getFormClientId(FacesContext facesContext, UIComponent uiComponent)
    {
        UIComponent form = findForm(uiComponent);

        return form.getClientId(facesContext);
    }

    private UIComponent findForm(UIComponent uiComponent)
    {
        if (uiComponent.getParent() instanceof UIForm)
        {
            return uiComponent.getParent();
        }
        return findForm(uiComponent.getParent());
    }
}
