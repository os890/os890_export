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
package at.gp.web.jsf.extval.validation.dynbaseval.strategy;

import at.gp.web.jsf.extval.validation.dynbaseval.annotation.ValidatorBinding;
import at.gp.web.jsf.extval.validation.dynbaseval.inline.provider.ValidationStrategyProvider;
import org.apache.myfaces.extensions.validator.core.metadata.MetaDataEntry;
import org.apache.myfaces.extensions.validator.core.property.PropertyDetails;
import org.apache.myfaces.extensions.validator.core.property.PropertyInformationKeys;
import org.apache.myfaces.extensions.validator.core.validation.strategy.ValidationStrategy;
import org.apache.myfaces.extensions.validator.util.ClassUtils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Gerhard Petracek
 * @since x.x.3
 */
public class ValidatorBindingStrategy extends AbstractDynamicAnnotationValidationStrategy<ValidatorBinding, Object>
{
    private List<ValidationStrategy> validationStrategies = new ArrayList<ValidationStrategy>();

    protected boolean init(MetaDataEntry metaDataEntry)
    {
        List foundInlineValidators = this.parameterExtractor.extract(this.constraint, ValidationStrategyProvider.class);

        if (foundInlineValidators != null && foundInlineValidators.size() > 0)
        {
            ValidationStrategyProvider resolver;
            Class validatorClass;
            for (Object entry : foundInlineValidators)
            {
                if(entry instanceof Class)
                {
                    validatorClass = (Class)entry;

                    if (ValidationStrategyProvider.class.isAssignableFrom(validatorClass))
                    {
                        for(Constructor constructor : validatorClass.getDeclaredConstructors())
                        {
                            constructor.setAccessible(true);
                            try
                            {
                                resolver = (ValidationStrategyProvider) constructor.newInstance(
                                        metaDataEntry.getProperty(PropertyInformationKeys.PROPERTY_DETAILS, PropertyDetails.class).getBaseObject());
                            }
                            catch (Throwable e)
                            {
                                resolver = (ValidationStrategyProvider)ClassUtils.tryToInstantiateClass(validatorClass);
                            }

                            if(resolver != null)
                            {
                                this.validationStrategies.add(resolver.getValidationStrategy());
                                break;
                            }
                        }
                    }
                }
                else if(entry instanceof ValidationStrategyProvider)
                {
                    this.validationStrategies.add(((ValidationStrategyProvider)entry).getValidationStrategy());
                }
            }
        }

        return validationStrategies.size() > 0;
    }

    protected boolean isValid(FacesContext facesContext, UIComponent uiComponent, MetaDataEntry metaDataEntry, Object convertedObject)
    {
        for (ValidationStrategy validationStrategy : this.validationStrategies)
        {
            validationStrategy.validate(facesContext, uiComponent, metaDataEntry, convertedObject);
        }
        return true;
    }
}
