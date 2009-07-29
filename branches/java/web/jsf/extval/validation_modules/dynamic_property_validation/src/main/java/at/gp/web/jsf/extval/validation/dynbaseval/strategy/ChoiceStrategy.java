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

import at.gp.web.jsf.extval.validation.dynbaseval.annotation.Choice;
import at.gp.web.jsf.extval.validation.dynbaseval.inline.provider.ChoiceProvider;
import org.apache.myfaces.extensions.validator.core.metadata.MetaDataEntry;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Gerhard Petracek
 * @since x.x.3
 */
public class ChoiceStrategy extends AbstractDynamicAnnotationValidationStrategy<Choice, Object>
{
    private List choices = new ArrayList();

    protected boolean init(MetaDataEntry metaDataEntry)
    {
        List<Collection> collections = this.parameterExtractor.extract(this.constraint, ChoiceProvider.class, Collection.class);

        for (Collection currentCollection : collections)
        {
            this.choices.addAll(currentCollection);
        }

        return this.choices.size() > 0;
    }

    protected Class getConstraintAspectKeyForValidation()
    {
        return ChoiceProvider.class;
    }

    protected boolean isValid(FacesContext facesContext, UIComponent uiComponent, MetaDataEntry metaDataEntry, Object convertedObject)
    {
        return choices.contains(convertedObject);
    }

    /*
     * optional
     */
    protected String getCustomValidationErrorMsgKey(Choice annotation)
    {
        return annotation.validationErrorMessageKey();
    }

    protected String getDefaultValidationErrorMsgKey()
    {
        return Choice.DEFAULT_VALIDATION_ERROR_MESSAGE_KEY;
    }
}
