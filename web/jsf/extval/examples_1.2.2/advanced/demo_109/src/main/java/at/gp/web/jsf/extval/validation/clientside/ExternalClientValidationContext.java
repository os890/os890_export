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
package at.gp.web.jsf.extval.validation.clientside;

import org.apache.myfaces.extensions.validator.core.metadata.MetaDataEntry;
import org.apache.myfaces.extensions.validator.core.property.PropertyInformation;

import javax.faces.context.FacesContext;
import java.util.*;

import at.gp.web.jsf.extval.validation.clientside.script.ScriptBuilder;

/**
 * @author Gerhard Petracek
 * @since 1.x.2
 */
public class ExternalClientValidationContext
{
    private Map<String, PropertyInformation> extractedMetaData = new HashMap<String, PropertyInformation>();
    //input component client id -> message component client id
    private Map<String, String> markerComponentIdMapping = new HashMap<String, String>();
    private ScriptBuilder scriptBuilder;

    public static ExternalClientValidationContext getCurrentInstance()
    {
        Map requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
        ExternalClientValidationContext context =
                (ExternalClientValidationContext) requestMap.get(ExternalClientValidationContext.class.getName());

        if(context == null)
        {
            context = startContext();
        }

        return context;
    }

    public void addProperty(String clientId, PropertyInformation result)
    {
        this.extractedMetaData.put(clientId, result);
    }

    public void addMarkerComponent(String clientId, String markerClientId)
    {
        this.markerComponentIdMapping.put(clientId, markerClientId);
    }

    public String getClientScript()
    {
        StringBuilder result = new StringBuilder();
        result.append(this.scriptBuilder.buildScriptStart());

        for(String currentKey : this.extractedMetaData.keySet())
        {
            for(MetaDataEntry currentMetaDataEntry : this.extractedMetaData.get(currentKey).getMetaDataEntries())
            {
                result.append(this.scriptBuilder.buildValidationScript(
                        currentKey, currentMetaDataEntry, this.markerComponentIdMapping.get(currentKey)));
            }
        }

        result.append(this.scriptBuilder.buildScriptEnd());
        return result.toString();
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
