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

import javax.faces.component.UIComponent;
import java.util.List;

/**
 * @author Gerhard Petracek
 */
public class ProcessedInformationStorageEntry
{
    private Object rootBean;
    private Object leafBean;
    private Object convertedValue;
    private UIComponent component;
    private PropertyDetails propertyDetails;
    //for complex components (e.g. a table there are multiple entries with
    //the same key (here the el expression #{entry.property})
    //however, don't override the previous entry - they arn't the same;
    private List<ProcessedInformationStorageEntry> furtherEntries;
    //just for input components within complex components e.g. dataTable,...
    private String clientId;

    private String formClientId;

    /*
    * generated
    */
    public Object getLeafBean()
    {
        return leafBean;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public <T> T getLeafBean(Class<T> targetType)
    {
        //noinspection unchecked
        return (T) getLeafBean();
    }

    public void setLeafBean(Object leafBean)
    {
        this.leafBean = leafBean;
    }

    public Object getRootBean()
    {
        return rootBean;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public <T> T getRootBean(Class<T> targetType)
    {
        //noinspection unchecked
        return (T) getRootBean();
    }

    public void setRootBean(Object rootBean)
    {
        this.rootBean = rootBean;
    }

    public Object getConvertedValue()
    {
        return convertedValue;
    }

    public void setConvertedValue(Object convertedValue)
    {
        this.convertedValue = convertedValue;
    }

    public UIComponent getComponent()
    {
        return component;
    }

    public void setComponent(UIComponent component)
    {
        this.component = component;
    }

    public String getClientId()
    {
        return clientId;
    }

    public void setClientId(String clientId)
    {
        this.clientId = clientId;
    }

    public List<ProcessedInformationStorageEntry> getFurtherEntries()
    {
        return furtherEntries;
    }

    public void setFurtherEntries(List<ProcessedInformationStorageEntry> furtherEntries)
    {
        this.furtherEntries = furtherEntries;
    }

    public PropertyDetails getPropertyDetails()
    {
        return propertyDetails;
    }

    public void setPropertyDetails(PropertyDetails propertyDetails)
    {
        this.propertyDetails = propertyDetails;
    }

    public String getFormClientId()
    {
        return formClientId;
    }

    public void setFormClientId(String formClientId)
    {
        this.formClientId = formClientId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof ProcessedInformationStorageEntry))
        {
            return false;
        }

        ProcessedInformationStorageEntry that = (ProcessedInformationStorageEntry) o;

        if (!clientId.equals(that.clientId))
        {
            return false;
        }
        if (!component.equals(that.component))
        {
            return false;
        }
        if (convertedValue != null ? !convertedValue.equals(that.convertedValue) : that.convertedValue != null)
        {
            return false;
        }
        if (!formClientId.equals(that.formClientId))
        {
            return false;
        }
        if (furtherEntries != null ? !furtherEntries.equals(that.furtherEntries) : that.furtherEntries != null)
        {
            return false;
        }
        if (!leafBean.equals(that.leafBean))
        {
            return false;
        }
        if (!propertyDetails.equals(that.propertyDetails))
        {
            return false;
        }
        //noinspection RedundantIfStatement
        if (!rootBean.equals(that.rootBean))
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = rootBean.hashCode();
        result = 31 * result + leafBean.hashCode();
        result = 31 * result + (convertedValue != null ? convertedValue.hashCode() : 0);
        result = 31 * result + component.hashCode();
        result = 31 * result + propertyDetails.hashCode();
        result = 31 * result + (furtherEntries != null ? furtherEntries.hashCode() : 0);
        result = 31 * result + clientId.hashCode();
        result = 31 * result + formClientId.hashCode();
        return result;
    }
}
