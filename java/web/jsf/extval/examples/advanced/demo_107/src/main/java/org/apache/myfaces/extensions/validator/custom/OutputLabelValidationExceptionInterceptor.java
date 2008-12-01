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

import org.apache.myfaces.extensions.validator.core.metadata.MetaDataEntry;
import org.apache.myfaces.extensions.validator.core.validation.strategy.ValidationStrategy;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.validator.ValidatorException;
import javax.faces.context.FacesContext;
import javax.faces.application.FacesMessage;
import java.util.Map;

/**
 * used for label support
 * (value of HtmlOutputLabel within required message, if there is no label attribute at the input component e.g. JSF 1.1)
 *
 * @author Gerhard Petracek
 */
public class OutputLabelValidationExceptionInterceptor implements org.apache.myfaces.extensions.validator.core.interceptor.ValidationExceptionInterceptor
{
    public boolean afterThrowing(UIComponent uiComponent, MetaDataEntry metaDataEntry, Object convertedObject, ValidatorException validatorException, ValidationStrategy validatorExceptionSource)
    {
        if(uiComponent instanceof HtmlInputText)
        {
            HtmlInputText inputText = (HtmlInputText)uiComponent;
            String key = inputText.getClientId(FacesContext.getCurrentInstance());

            FacesMessage facesMessage = validatorException.getFacesMessage();

            String label = inputText.getLabel();

            if(label == null)
            {
                Map<String, String> mapping = OutputLabelRecorder.getRecordedLabels();
                String foundLabel = mapping.get(key);

                if(foundLabel != null)
                {
                    label = foundLabel.replace(":", "");
                }
            }

            for(int i = 0; i < 3; i++)
            {
                ExtValUtils.tryToPlaceLabel(facesMessage, label, i);
            }
        }

        return true;
    }
}
