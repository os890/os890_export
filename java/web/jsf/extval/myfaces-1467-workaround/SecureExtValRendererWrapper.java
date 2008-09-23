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
package at.gp.web.jsf.extval.validation.secure;

import org.apache.myfaces.extensions.validator.MetaDataKeys;
import org.apache.myfaces.extensions.validator.core.ExtValRendererWrapper;
import org.apache.myfaces.extensions.validator.core.MetaDataExtractor;
import org.apache.myfaces.extensions.validator.core.annotation.AnnotationEntry;
import org.apache.myfaces.extensions.validator.core.annotation.extractor.AnnotationExtractor;
import org.apache.myfaces.extensions.validator.core.validation.strategy.ValidationStrategy;
import org.apache.myfaces.extensions.validator.util.FactoryUtils;

import javax.faces.application.FacesMessage;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;
import java.util.HashMap;
import java.util.Map;

/**
 * check the required submit of user input.
 * secure workaround for MYFACES-1467
 *
 * @author Gerhard Petracek
 */
public class SecureExtValRendererWrapper extends ExtValRendererWrapper
{
    public SecureExtValRendererWrapper(Renderer wrapped)
    {
        super(wrapped);
    }

    @Override
    public void decode(FacesContext facesContext, UIComponent uiComponent)
    {
        this.wrapped.decode(facesContext, uiComponent);

        if (uiComponent instanceof EditableValueHolder)
        {
            if (checkComponent(facesContext, uiComponent))
            {
                facesContext.addMessage(uiComponent.getClientId(facesContext), new FacesMessage(FacesMessage.SEVERITY_ERROR, "input required", "security alert - input required"));
                facesContext.renderResponse();
            }
        }
    }

    private boolean checkComponent(FacesContext facesContext, UIComponent uiComponent)
    {
        return uiComponent instanceof EditableValueHolder &&
            ((EditableValueHolder) uiComponent).getSubmittedValue() == null &&
            (isValueOfComponentRequired(facesContext, uiComponent) || ((EditableValueHolder) uiComponent).isRequired());
    }

    //if you are using annotations
    private boolean isValueOfComponentRequired(FacesContext facesContext, UIComponent uiComponent)
    {
        ValidationStrategy validationStrategy;

        AnnotationExtractor annotationExtractor = FactoryUtils.getComponentAnnotationExtractorFactory().create();

        Map<String, Object> metaData;
        for (AnnotationEntry entry : annotationExtractor.extractAnnotations(facesContext, uiComponent))
        {
            validationStrategy = FactoryUtils.getValidationStrategyFactory().create(entry.getAnnotation());

            if (validationStrategy != null)
            {
                if (validationStrategy instanceof MetaDataExtractor)
                {
                    metaData = ((MetaDataExtractor) validationStrategy).extractMetaData(entry.getAnnotation());
                }
                else
                {
                    metaData = null;
                }

                if (metaData == null)
                {
                    metaData = new HashMap<String, Object>();
                }

                if (metaData.containsKey(MetaDataKeys.REQUIRED))
                {
                    return true;
                }
            }
        }
        return false;
    }
}