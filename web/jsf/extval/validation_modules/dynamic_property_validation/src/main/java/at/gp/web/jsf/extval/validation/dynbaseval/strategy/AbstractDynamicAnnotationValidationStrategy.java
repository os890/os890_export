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

import at.gp.web.jsf.extval.validation.dynbaseval.parameter.ValidationErrorMessageKey;
import org.apache.myfaces.extensions.validator.core.metadata.MetaDataEntry;
import org.apache.myfaces.extensions.validator.core.validation.parameter.ValidationParameterExtractor;
import org.apache.myfaces.extensions.validator.core.validation.strategy.AbstractAnnotationValidationStrategy;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;
import org.apache.myfaces.extensions.validator.internal.ToDo;
import org.apache.myfaces.extensions.validator.internal.Priority;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import java.lang.annotation.Annotation;

/**
 * @author Gerhard Petracek
 * @since x.x.3
 */
public abstract class AbstractDynamicAnnotationValidationStrategy<T extends Annotation, V> extends AbstractAnnotationValidationStrategy
{
    protected ValidationParameterExtractor parameterExtractor;
    protected T constraint;
    private boolean invokeValidation;

    /*
     * validate annotation based metadata entries only
     */
    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, MetaDataEntry metaDataEntry, Object convertedObject)
    {
        if (metaDataEntry.getValue() instanceof Annotation)
        {
            super.validate(facesContext, uiComponent, metaDataEntry, convertedObject);
        }
    }

    @Override
    @SuppressWarnings({"unchecked"})
    protected void initValidation(FacesContext facesContext, UIComponent uiComponent, MetaDataEntry metaDataEntry, Object convertedObject)
    {
        this.parameterExtractor = ExtValUtils.getValidationParameterExtractor();
        this.constraint = (T) metaDataEntry.getValue(Annotation.class);
        this.invokeValidation = init(metaDataEntry);
    }

    protected abstract boolean init(MetaDataEntry metaDataEntry);

    @SuppressWarnings({"unchecked"})
    @ToDo(value = Priority.HIGH, description = "check impl")
    protected String getValidationErrorMsgKey(Annotation annotation)
    {
        String key = getCustomValidationErrorMsgKey((T) annotation);

        if(key != null && multipleValuesUsed() && !key.equals(getDefaultValidationErrorMsgKey()))
        {
            return key;
        }

        if(multipleValuesUsed())
        {
            if(getDefaultValidationErrorMsgKey() == null)
            {
                throw new RuntimeException("constraints with multiple values have to provide a default ");
            }
            return getDefaultValidationErrorMsgKey();
        }

        String constraintKey = this.parameterExtractor.extract(this.constraint, getConstraintAspectKeyForValidation(), String.class, ValidationErrorMessageKey.class);

        if (constraintKey != null && !constraintKey.equals(getDefaultValidationErrorMsgKey()))
        {
            return constraintKey;
        }
        else if(key != null)
        {
            return key;
        }

        return getDefaultValidationErrorMsgKey();
    }

    protected boolean multipleValuesUsed()
    {
        return false;
    }

    protected void processValidation(FacesContext facesContext, UIComponent uiComponent, MetaDataEntry metaDataEntry, Object convertedObject) throws ValidatorException
    {
        if (this.invokeValidation)
        {
            if (convertedObject != null && !isValid(facesContext, uiComponent, metaDataEntry, (V)convertedObject))
            {
                createValidationErrorFacesMassage();
            }
        }
    }

    protected abstract boolean isValid(FacesContext facesContext, UIComponent uiComponent, MetaDataEntry metaDataEntry, V value);

    protected String getCustomValidationErrorMsgKey(T annotation)
    {
        //override if needed
        return null;
    }

    protected String getDefaultValidationErrorMsgKey()
    {
        //override if needed
        return null;
    }

    protected void createValidationErrorFacesMassage()
    {
        throw new ValidatorException(getValidationErrorFacesMassage(this.constraint));
    }

    protected boolean isEmpty(Object value)
    {
        return value == null || "".equals(value);
    }

    protected Class getConstraintAspectKeyForValidation()
    {
        return Class.class;
    }
}
