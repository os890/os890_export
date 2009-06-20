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
package at.gp.web.jsf.extval.validation.model.transactional.recorder;

import org.apache.myfaces.extensions.validator.core.recorder.ProcessedInformationRecorder;
import org.apache.myfaces.extensions.validator.core.property.PropertyDetails;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;

import javax.faces.component.UIComponent;
import javax.faces.component.EditableValueHolder;

import at.gp.web.jsf.extval.validation.model.transactional.TransactionalModelValidationContext;
import at.gp.web.jsf.extval.validation.model.transactional.RevertableProperty;
import at.gp.web.jsf.extval.validation.model.transactional.util.ModelValidationUtils;

/**
 * @author Gerhard Petracek
 * @since 1.x.3
 */
public class ModelValidationRecorder implements ProcessedInformationRecorder
{
    public void recordUserInput(UIComponent uiComponent, Object newValue)
    {
        if (!(uiComponent instanceof EditableValueHolder))
        {
            return;
        }

        PropertyDetails propertyDetails = ExtValUtils.getELHelper().getPropertyDetailsOfValueBinding(uiComponent);

        Object baseObject = propertyDetails.getBaseObject();
        String propertyName = propertyDetails.getProperty();
        Object oldValue = ModelValidationUtils.invokeGetter(baseObject, propertyName);

        TransactionalModelValidationContext.getContext().addRevertableProperty(new RevertableProperty((EditableValueHolder)uiComponent, oldValue, newValue, baseObject, propertyName));
    }
}
