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
package at.gp.web.jsf.extval.beanval.form.storage;

import org.apache.myfaces.extensions.validator.core.property.PropertyDetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gerhard Petracek
 */
public class DefaultProcessedInformationStorage implements ProcessedInformationStorage
{
    private List<String> formIds = new ArrayList<String>();

    private Map<String, List<ProcessedInformationStorageEntry>> formToProcessedInformationMapping =
            new HashMap<String, List<ProcessedInformationStorageEntry>>();

    public void addEntry(String formClientId, String internalComponentClientId, ProcessedInformationStorageEntry entry)
    {
        List<ProcessedInformationStorageEntry> processedInformationStorageEntryList = null;

        for (Map.Entry<String, List<ProcessedInformationStorageEntry>> currentList : this.formToProcessedInformationMapping.entrySet())
        {
            if (formClientId.endsWith(currentList.getKey()))
            {
                processedInformationStorageEntryList = currentList.getValue();
                break;
            }
        }

        //noinspection ConstantConditions
        if (!processedInformationStorageEntryList.contains(entry))
        {
            processedInformationStorageEntryList.add(entry);
        }
    }

    public void addFormId(String formClientId)
    {
        this.formIds.add(formClientId);
        this.formToProcessedInformationMapping.put(formClientId, new ArrayList<ProcessedInformationStorageEntry>());
    }

    public List<String> getFormIds()
    {
        return this.formIds;
    }

    public List<ProcessedInformationStorageEntry> getEntries(String formId)
    {
        for (Map.Entry<String, List<ProcessedInformationStorageEntry>> currentList : this.formToProcessedInformationMapping.entrySet())
        {
            if (formId.equals(currentList.getKey()))
            {
                return currentList.getValue();
            }
        }

        throw new IllegalStateException("no processed information found for form: " + formId);
    }

    public void clear()
    {
        this.formToProcessedInformationMapping.clear();
    }

    public ProcessedInformationStorageEntry findEntry(PropertyDetails propertyDetails, String formClientId, String componentId)
    {
        for (Map.Entry<String, List<ProcessedInformationStorageEntry>> currentList : this.formToProcessedInformationMapping.entrySet())
        {
            for (ProcessedInformationStorageEntry currentEntry : currentList.getValue())
            {
                if (!currentList.getKey().startsWith(formClientId))
                {
                    continue;
                }

                if (currentEntry.getPropertyDetails().getKey().equals(propertyDetails.getKey()) &&
                        currentEntry.getComponent().getId().equals(componentId) /*don't use the client-id here*/)
                {
                    return currentEntry;
                }
            }
        }
        return null;
    }
}