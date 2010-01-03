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
package at.gp.web.jsf.extval.validation.metadata.virtual.strategy;

import org.apache.myfaces.extensions.validator.core.validation.strategy.ValidationStrategy;
import org.apache.myfaces.extensions.validator.core.validation.strategy.AbstractValidationStrategy;
import org.apache.myfaces.extensions.validator.core.validation.EmptyValueAwareValidationStrategy;
import org.apache.myfaces.extensions.validator.core.validation.NullValueAwareValidationStrategy;
import org.apache.myfaces.extensions.validator.core.metadata.MetaDataEntry;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;
import org.apache.myfaces.extensions.validator.util.ReflectionUtils;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.validator.ValidatorException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import at.gp.web.jsf.extval.validation.metadata.virtual.annotation.VirtualMetaData;

/**
 * @author Gerhard Petracek
 *
 * @since 1.x.3
 */
@NullValueAwareValidationStrategy
@EmptyValueAwareValidationStrategy
public class VirtualMetaDataStrategy extends AbstractValidationStrategy
{
    protected void processValidation(FacesContext facesContext, UIComponent uiComponent, final MetaDataEntry metaDataEntry, Object convertedObject) throws ValidatorException
    {
        if(metaDataEntry.getValue() instanceof Annotation)
        {
            final ValidationStrategy validationStrategy = ExtValUtils.getValidationStrategyForMetaData(metaDataEntry.getProperty(VirtualMetaData.TARGET, MetaDataEntry.class).getKey());

            if(validationStrategy != null)
            {
                if(validationStrategy instanceof AbstractValidationStrategy)
                {
                    new AbstractValidationStrategy()
                    {
                        protected void processValidation(FacesContext facesContext, UIComponent uiComponent, MetaDataEntry metaDataEntry, Object convertedObject) throws ValidatorException
                        {
                            validationStrategy.validate(facesContext, uiComponent, metaDataEntry.getProperty(VirtualMetaData.TARGET, MetaDataEntry.class), convertedObject);
                        }

                        @Override
                        public boolean processAfterValidatorException(FacesContext facesContext, UIComponent uiComponent, MetaDataEntry mde, Object convertedObject, ValidatorException validatorException)
                        {
                            return invokeMethod((AbstractValidationStrategy)validationStrategy, "processAfterValidatorException", facesContext, uiComponent, metaDataEntry, convertedObject, validatorException);
                        }
                    }.validate(facesContext, uiComponent, metaDataEntry.getProperty(VirtualMetaData.TARGET, MetaDataEntry.class), convertedObject);
                }
                else
                {
                    validationStrategy.validate(facesContext, uiComponent, metaDataEntry.getProperty(VirtualMetaData.TARGET, MetaDataEntry.class), convertedObject);
                }
            }
        }
    }

    private boolean invokeMethod(AbstractValidationStrategy abstractValidationStrategy, String methodName, FacesContext facesContext, UIComponent uiComponent, MetaDataEntry metaDataEntry, Object convertedObject, ValidatorException validatorException)
    {
        Method method = ReflectionUtils.tryToGetMethod(abstractValidationStrategy.getClass(), methodName, FacesContext.class, UIComponent.class, MetaDataEntry.class, Object.class, ValidatorException.class);

        if(method == null)
        {
            throw new IllegalStateException("incompatible version");
        }

        try
        {
            method.setAccessible(true);
            return (Boolean)method.invoke(abstractValidationStrategy, facesContext, uiComponent, metaDataEntry, convertedObject, validatorException);
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }

        throw new IllegalStateException("incompatible version");
    }
}
