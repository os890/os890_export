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

import at.gp.web.jsf.extval.service.ValidationService;
import org.apache.myfaces.extensions.validator.core.metadata.MetaDataEntry;
import org.apache.myfaces.extensions.validator.core.validation.EmptyValueAwareValidationStrategy;
import org.apache.myfaces.extensions.validator.core.validation.NullValueAwareValidationStrategy;
import org.apache.myfaces.extensions.validator.core.validation.strategy.AbstractAnnotationValidationStrategy;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import java.lang.annotation.Annotation;

/**
 * @author Gerhard Petracek
 */
@NullValueAwareValidationStrategy
@EmptyValueAwareValidationStrategy
public class CustomRequiredValidator extends AbstractAnnotationValidationStrategy<CustomRequired>
{
    private ValidationService validationService;

    protected void processValidation(FacesContext facesContext, UIComponent uiComponent,
                                     MetaDataEntry metaDataEntry, Object convertedObject) throws ValidatorException
    {
        if (this.validationService.validate(convertedObject, metaDataEntry.getValue(Annotation.class)))
        {
            throw new ValidatorException(getValidationErrorFacesMessage(metaDataEntry.getValue(CustomRequired.class)));
        }
    }

    protected String getValidationErrorMsgKey(CustomRequired annotation)
    {
        return annotation.message();
    }

    public void setValidationService(ValidationService validationService)
    {
        this.validationService = validationService;
    }
}
