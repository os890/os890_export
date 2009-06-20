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
package at.gp.web.jsf.extval.validation.model.transactional;

import org.apache.myfaces.extensions.validator.core.metadata.MetaDataEntry;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.util.Map;

/**
 * @author Gerhard Petracek
 * @since 1.x.3
 */
public class ModelValidationEntry
{
    private Object oldValue;
    private UIComponent component;
    private String clientId;
    private MetaDataEntry metaDataEntry;
    private Map<String, Object> properties;
    private boolean classLevelConstraint;

    public void setComponent(UIComponent component)
    {
        this.component = component;
        this.clientId = component.getClientId(FacesContext.getCurrentInstance());
    }

    /*
     * generated
     */
    public Object getOldValue()
    {
        return oldValue;
    }

    public void setOldValue(Object oldValue)
    {
        this.oldValue = oldValue;
    }

    public UIComponent getComponent()
    {
        return component;
    }

    public MetaDataEntry getMetaDataEntry()
    {
        return metaDataEntry;
    }

    public void setMetaDataEntry(MetaDataEntry metaDataEntry)
    {
        this.metaDataEntry = metaDataEntry;
    }

    public Map<String, Object> getProperties()
    {
        return properties;
    }

    public void setProperties(Map<String, Object> properties)
    {
        this.properties = properties;
    }

    public String getClientId()
    {
        return clientId;
    }

    public boolean isClassLevelConstraint()
    {
        return classLevelConstraint;
    }

    public void setClassLevelConstraint(boolean classLevelConstraint)
    {
        this.classLevelConstraint = classLevelConstraint;
    }
}
