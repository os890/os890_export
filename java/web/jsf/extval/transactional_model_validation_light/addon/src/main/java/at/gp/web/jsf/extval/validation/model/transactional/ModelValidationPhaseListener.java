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

import org.apache.myfaces.extensions.validator.core.property.PropertyInformation;
import org.apache.myfaces.extensions.validator.core.validation.strategy.ValidationStrategy;
import org.apache.myfaces.extensions.validator.crossval.strategy.CrossValidationStrategy;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;

import javax.faces.application.FacesMessage;
import javax.faces.component.EditableValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.validator.ValidatorException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.ArrayList;

import at.gp.web.jsf.extval.validation.model.transactional.parameter.ShowGlobalViolationMessageOnly;

/**
 * no cross-validation - here class-based validation >would< be possible as well<br/>
 * the meta-data-entry holds the base object...
 *
 * @author Gerhard Petracek
 * @since 1.x.3
 */
public class ModelValidationPhaseListener implements PhaseListener
{
    private static final long serialVersionUID = 3504138079958162383L;

    public void afterPhase(PhaseEvent event)
    {
        List<Annotation> validatedClassLevelConstraints = new ArrayList<Annotation>();
        boolean foundViolation = false;

        try
        {
            ValidationStrategy validationStrategy = null;
            for (ModelValidationEntry entry : TransactionalModelValidationContext.getContext().getModelValidationEntries())
            {
                try
                {
                    //before interceptors were executed by the extval core -> don't re-call them
                    if(entry.isClassLevelConstraint())
                    {
                        if(validatedClassLevelConstraints.contains(entry.getMetaDataEntry().getValue(Annotation.class)))
                        {
                            continue;
                        }

                        validatedClassLevelConstraints.add(entry.getMetaDataEntry().getValue(Annotation.class));
                    }

                    Object baseObject = entry.getMetaDataEntry().getProperty(Object.class.getName());

                    validationStrategy = ExtValUtils.getValidationStrategyForMetaData(entry.getMetaDataEntry().getKey());

                    if(validationStrategy == null)
                    {
                        continue;
                    }
                    
                    if (validationStrategy instanceof CrossValidationStrategy)
                    {
                        throw new IllegalStateException(CrossValidationStrategy.class.getName() + " currently not supported for model aware validation");
                    }

                    //before interceptors were executed by the extval core -> don't re-call them
                    if(entry.isClassLevelConstraint())
                    {
                        //don't call global interceptors - one of them would add further model validation entries

                        if(!ExtValUtils.executeLocalBeforeValidationInterceptors(
                                FacesContext.getCurrentInstance(), entry.getComponent(),
                                baseObject,
                                PropertyInformation.class.getName(), entry.getProperties().get(PropertyInformation.class.getName()),
                                entry.getMetaDataEntry().getValue(Annotation.class)))
                        {
                            continue;
                        }
                    }

                    try
                    {
                        validationStrategy.validate(FacesContext.getCurrentInstance(), entry.getComponent(), entry.getMetaDataEntry(), ((EditableValueHolder) entry.getComponent()).getValue());
                    }
                    finally
                    {
                        if (entry.getMetaDataEntry().getValue() instanceof Annotation)
                        {
                            ExtValUtils.executeLocalAfterValidationInterceptors(
                                    FacesContext.getCurrentInstance(), entry.getComponent(), ((EditableValueHolder) entry.getComponent()).getValue(),
                                    PropertyInformation.class.getName(), entry.getProperties().get(PropertyInformation.class.getName()),
                                    entry.getMetaDataEntry().getValue(Annotation.class));
                        }
                    }
                }
                catch (ValidatorException validatorException)
                {
                    FacesMessage facesMessage = validatorException.getFacesMessage();

                    String clientId = entry.getClientId();

                    if(entry.isClassLevelConstraint() && !ExtValUtils.getValidationParameterExtractor().extract(entry.getMetaDataEntry().getValue(Annotation.class), ShowGlobalViolationMessageOnly.class).isEmpty())
                    {
                        clientId = null;
                    }

                    if (facesMessage != null &&
                            facesMessage.getSummary() != null && facesMessage.getDetail() != null)
                    {
                        event.getFacesContext().addMessage(clientId, facesMessage);
                    }
                    
                    foundViolation = true;
                }
                finally
                {
                    if(validationStrategy != null && !entry.isClassLevelConstraint())
                    {
                        ExtValUtils.executeGlobalAfterValidationInterceptors(
                                FacesContext.getCurrentInstance(),
                                entry.getComponent(),
                                ((EditableValueHolder) entry.getComponent()).getValue(),
                                PropertyInformation.class.getName(),
                                entry.getProperties().get(PropertyInformation.class.getName()));
                    }
                }
            }
        }

        finally
        {
            if(foundViolation)
            {
                TransactionalModelValidationContext.getContext().rollback();
                event.getFacesContext().renderResponse();
            }

            //cleanup
            TransactionalModelValidationContext.getContext().getModelValidationEntries().clear();
        }


    }

    public void beforePhase(PhaseEvent phaseEvent)
    {
        //do nothing
    }

    public PhaseId getPhaseId()
    {
        return PhaseId.UPDATE_MODEL_VALUES;
    }
}
