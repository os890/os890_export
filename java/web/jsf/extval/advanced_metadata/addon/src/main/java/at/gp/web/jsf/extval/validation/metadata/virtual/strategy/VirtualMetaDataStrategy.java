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

import at.gp.web.jsf.extval.validation.metadata.virtual.annotation.VirtualMetaData;
import org.apache.myfaces.extensions.validator.core.metadata.MetaDataEntry;
import org.apache.myfaces.extensions.validator.core.storage.ViolationSeverityInterpreterStorage;
import org.apache.myfaces.extensions.validator.core.validation.EmptyValueAwareValidationStrategy;
import org.apache.myfaces.extensions.validator.core.validation.NullValueAwareValidationStrategy;
import org.apache.myfaces.extensions.validator.core.validation.parameter.DefaultViolationSeverityInterpreter;
import org.apache.myfaces.extensions.validator.core.validation.strategy.AbstractValidationStrategy;
import org.apache.myfaces.extensions.validator.core.validation.strategy.ValidationStrategy;
import org.apache.myfaces.extensions.validator.internal.Priority;
import org.apache.myfaces.extensions.validator.internal.ToDo;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;
import org.apache.myfaces.extensions.validator.util.ReflectionUtils;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Gerhard Petracek
 * @since 1.x.3
 */
@NullValueAwareValidationStrategy
@EmptyValueAwareValidationStrategy
public class VirtualMetaDataStrategy extends AbstractValidationStrategy
{
    protected void processValidation(FacesContext facesContext, UIComponent uiComponent, final MetaDataEntry metaDataEntry, Object convertedObject) throws ValidatorException
    {
        if (metaDataEntry.getValue() instanceof Annotation)
        {
            final ValidationStrategy validationStrategy = ExtValUtils.getValidationStrategyForMetaData(metaDataEntry.getProperty(VirtualMetaData.TARGET, MetaDataEntry.class).getKey());

            if (validationStrategy != null && isValidationStrategyCompatibleWithValue(validationStrategy, convertedObject))
            {
                setVirtualViolationSeverityInterpreter();

                try
                {
                    delegateValidation(facesContext, uiComponent, metaDataEntry, convertedObject, validationStrategy);
                }
                finally
                {
                    //reset it in any case
                    resetViolationSeverityInterpreter();
                }
            }
        }
    }

    private void delegateValidation(FacesContext facesContext, UIComponent uiComponent, final MetaDataEntry metaDataEntry, Object convertedObject, final ValidationStrategy validationStrategy)
    {
        if (validationStrategy instanceof AbstractValidationStrategy)
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
                    resetViolationSeverityInterpreter();
                    return invokeMethod((AbstractValidationStrategy)validationStrategy, "processAfterValidatorException", facesContext, uiComponent, metaDataEntry, convertedObject, validatorException);
                }
            }.validate(facesContext, uiComponent, metaDataEntry.getProperty(VirtualMetaData.TARGET, MetaDataEntry.class), convertedObject);
        }
        else
        {
            validationStrategy.validate(facesContext, uiComponent, metaDataEntry.getProperty(VirtualMetaData.TARGET, MetaDataEntry.class), convertedObject);
        }
    }

    private void setVirtualViolationSeverityInterpreter()
    {
        ExtValUtils.getStorage(ViolationSeverityInterpreterStorage.class, ViolationSeverityInterpreterStorage.class.getName())
                .setViolationSeverityInterpreter(new DefaultViolationSeverityInterpreter()
                {

                    @Override
                    public boolean severityBlocksNavigation(FacesContext facesContext, UIComponent uiComponent, FacesMessage.Severity severity)
                    {
                        return false;
                    }

                    @Override
                    public boolean severityCausesViolationMessage(FacesContext facesContext, UIComponent uiComponent, FacesMessage.Severity severity)
                    {
                        return false;
                    }
                });
    }

    private void resetViolationSeverityInterpreter()
    {
        ExtValUtils.resetStorage(ViolationSeverityInterpreterStorage.class, ViolationSeverityInterpreterStorage.class.getName());
    }

    @ToDo(value = Priority.LOW, description = "it's implemented in ValidationInterceptor - move it to an util class")
    private boolean isValidationStrategyCompatibleWithValue(ValidationStrategy validationStrategy, Object value)
    {
        if (value == null)
        {
            return validationStrategy.getClass().isAnnotationPresent(NullValueAwareValidationStrategy.class);
        }

        return !"".equals(value) || validationStrategy.getClass()
                .isAnnotationPresent(EmptyValueAwareValidationStrategy.class);
    }

    private boolean invokeMethod(AbstractValidationStrategy abstractValidationStrategy, String methodName, FacesContext facesContext, UIComponent uiComponent, MetaDataEntry metaDataEntry, Object convertedObject, ValidatorException validatorException)
    {
        Method method = ReflectionUtils.tryToGetMethod(abstractValidationStrategy.getClass(), methodName, FacesContext.class, UIComponent.class, MetaDataEntry.class, Object.class, ValidatorException.class);

        if (method == null)
        {
            throw new IllegalStateException("incompatible version");
        }

        try
        {
            method.setAccessible(true);
            return (Boolean) method.invoke(abstractValidationStrategy, facesContext, uiComponent, metaDataEntry, convertedObject, validatorException);
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
