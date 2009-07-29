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

import at.gp.web.jsf.extval.validation.dynbaseval.annotation.Zip;
import at.gp.web.jsf.extval.validation.dynbaseval.parameter.ZipCodePattern;
import org.apache.myfaces.extensions.validator.core.metadata.MetaDataEntry;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.util.List;
import java.util.regex.Pattern;

/**
 * example usages:
 * @Zip(Austria.class)
 * or
 * @Zip({Austria.class, US.class, YourCustomZip.class})
 *
 * @author Gerhard Petracek
 * @since x.x.3
 */
@SuppressWarnings({"JavaDoc"})
public class ZipStrategy extends AbstractDynamicAnnotationValidationStrategy<Zip, String>
{
    private List<Pattern> zipCodePatterns;

    protected boolean init(MetaDataEntry metaDataEntry)
    {
        List<Pattern> foundPattern = this.parameterExtractor.extract(this.constraint, getConstraintAspectKeyForValidation(), Pattern.class);

        if (foundPattern != null && foundPattern.size() > 0)
        {
            this.zipCodePatterns = foundPattern;
            return true;
        }

        //init not successful
        return false;
    }

    protected boolean isValid(FacesContext facesContext, UIComponent uiComponent, MetaDataEntry metaDataEntry, String zip)
    {
        for(Pattern zipCodePattern : this.zipCodePatterns)
        {
            if(!isEmpty(zip) && zipCodePattern.matcher(zip).matches())
            {
                return true;
            }
        }

        return false;
    }

    protected Class getConstraintAspectKeyForValidation()
    {
        return ZipCodePattern.class;
    }

    /*
     * optional
     */
    @Override
    protected String getCustomValidationErrorMsgKey(Zip annotation)
    {
        return annotation.validationErrorMessageKey();
    }

    @Override
    protected String getDefaultValidationErrorMsgKey()
    {
        return Zip.DEFAULT_VALIDATION_ERROR_MESSAGE_KEY;
    }

    @Override
    protected boolean multipleValuesUsed()
    {
        return zipCodePatterns.size() > 1;
    }
}
