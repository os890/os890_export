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

import at.gp.web.jsf.extval.validation.model.transactional.strategy.AbstractAnnotationModelValidationStrategy;
import org.apache.myfaces.extensions.validator.core.metadata.MetaDataEntry;

import javax.faces.validator.ValidatorException;
import java.lang.annotation.Annotation;

public class VehicleValidationStrategy extends AbstractAnnotationModelValidationStrategy
{
    protected void processModelValidation(MetaDataEntry metaDataEntry, Object modelObject) throws ValidatorException
    {
        if(!(modelObject instanceof at.gp.web.jsf.extval.domain.Vehicle && isValid((at.gp.web.jsf.extval.domain.Vehicle)modelObject)))
        {
            throw new ValidatorException(getValidationErrorFacesMessage(metaDataEntry.getValue(Annotation.class)));
        }
    }

    private boolean isValid(at.gp.web.jsf.extval.domain.Vehicle car)
    {
        //quite stupid logic - but easy to test
        return car.getManufacturer() != null && car.getModelIdentification() != null && !car.getManufacturer().equals(car.getModelIdentification());
    }

    protected String getValidationErrorMsgKey(Annotation annotation)
    {
        return ((Vehicle)annotation).violationMessageOrKey();
    }
}
