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
package org.apache.myfaces.extensions.validator.custom;

import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.util.Map;
import java.util.HashMap;

/**
 * used for label support
 * (value of HtmlOutputLabel within required message, if there is no label attribute at the input component e.g. JSF 1.1)
 *
 * @author Gerhard Petracek
 */
public class OutputLabelRecorder
{
    private static final String ID = OutputLabelRecorder.class.getName() + "_KEY";

    public static void record(HtmlOutputLabel outputLabel)
    {
        if (outputLabel.getFor() == null)
        {
            return;
        }

        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map requestMap = facesContext.getExternalContext().getRequestMap();
        Map<String, String> mapping;

        if (!requestMap.containsKey(ID))
        {
            mapping = new HashMap<String, String>();
            requestMap.put(ID, mapping);
        }

        mapping = (Map<String, String>) requestMap.get(ID);

        String key = outputLabel.getClientId(facesContext);
        if (key.contains(":"))
        {
            key = key.substring(0, key.lastIndexOf(":") + 1) + outputLabel.getFor();
        }
        else
        {
            key = outputLabel.getFor();
        }

        if (!outputLabel.getClientId(facesContext).contains(outputLabel.getParent().getId()))
        {
            String foundKey = findParentOfCurrentParentId(outputLabel, outputLabel).getClientId(facesContext);
            key = foundKey.substring(0, foundKey.lastIndexOf(":")) + ":" + outputLabel.getFor();
        }

        mapping.put(key, (String) outputLabel.getValue());
    }

    public static Map<String, String> getRecordedLabels()
    {
        Map requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();

        if (requestMap.containsKey(ID))
        {
            return (Map<String, String>) requestMap.get(ID);
        }
        return new HashMap<String, String>();
    }

    private static UIComponent findParentOfCurrentParentId(UIComponent uiComponent, UIComponent currentComponent)
    {
        if (uiComponent == null)
        {
            return null;
        }
        if (!currentComponent.getParent().getClientId(FacesContext.getCurrentInstance()).contains(":" + uiComponent.getParent().getId()))
        {
            return currentComponent.getParent();
        }
        return findParentOfCurrentParentId(uiComponent, currentComponent.getParent());
    }
}
