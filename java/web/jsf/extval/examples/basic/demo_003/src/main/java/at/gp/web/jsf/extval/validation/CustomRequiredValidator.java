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
package at.gp.web.jsf.extval.validation;

import org.apache.myfaces.extensions.validator.core.validation.strategy.AbstractValidationStrategy;
import org.apache.myfaces.extensions.validator.core.validation.NullValueAwareValidationStrategy;
import org.apache.myfaces.extensions.validator.core.validation.EmptyValueAwareValidationStrategy;
import org.apache.myfaces.extensions.validator.core.metadata.MetaDataEntry;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;

/**
 * @author Gerhard Petracek
 */
@NullValueAwareValidationStrategy
@EmptyValueAwareValidationStrategy
public class CustomRequiredValidator extends AbstractValidationStrategy
{
    protected void processValidation(FacesContext facesContext, UIComponent uiComponent,
                                     MetaDataEntry metaDataEntry, Object convertedObject) throws ValidatorException
    {
        if("".equals(convertedObject))
        {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, getErrorMessage(), getErrorMessage()));
        }
    }

    private String getErrorMessage()
    {
        return "custom required message";
    }
}
