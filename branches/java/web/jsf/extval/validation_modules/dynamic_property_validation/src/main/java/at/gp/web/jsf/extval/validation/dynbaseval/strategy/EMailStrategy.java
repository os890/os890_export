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

import org.apache.myfaces.extensions.validator.core.metadata.MetaDataEntry;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import java.util.List;
import java.util.regex.Pattern;

import at.gp.web.jsf.extval.validation.dynbaseval.annotation.EMail;
import at.gp.web.jsf.extval.validation.dynbaseval.parameter.EMailPattern;

/**
 * @author Gerhard Petracek
 * @since x.x.3
 */
public class EMailStrategy extends AbstractDynamicAnnotationValidationStrategy<EMail, String>
{
    private Pattern eMailPattern;

    protected boolean init(MetaDataEntry metaDataEntry) {
        List foundPattern = this.parameterExtractor.extract(this.constraint, getConstraintAspectKeyForValidation(), Pattern.class);

        if(foundPattern != null && foundPattern.iterator().hasNext())
        {
            this.eMailPattern = (Pattern)foundPattern.iterator().next();
            return true;
        }

        return false;
    }

    protected boolean isValid(FacesContext facesContext, UIComponent uiComponent, MetaDataEntry metaDataEntry, String emailAddress) {
        return emailAddress.length() > 0 && eMailPattern.matcher(emailAddress).matches();
    }

    @Override
    protected String getCustomValidationErrorMsgKey(EMail annotation) {
        return annotation.message();
    }

    /*
     * optional
     */

    @Override
    protected String getDefaultValidationErrorMsgKey()
    {
        return EMail.DEFAULT_VALIDATION_ERROR_MESSAGE_KEY;
    }

    @Override
    protected Class getConstraintAspectKeyForValidation()
    {
        return EMailPattern.class;
    }
}
