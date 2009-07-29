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

import at.gp.web.jsf.extval.validation.dynbaseval.annotation.DateRange;
import at.gp.web.jsf.extval.validation.dynbaseval.inline.provider.DateRangeProvider;
import at.gp.web.jsf.extval.validation.dynbaseval.inline.entry.DateRangeEntry;
import org.apache.myfaces.extensions.validator.core.metadata.MetaDataEntry;
import org.apache.myfaces.extensions.validator.core.property.PropertyInformationKeys;
import org.apache.myfaces.extensions.validator.core.property.PropertyDetails;
import org.apache.myfaces.extensions.validator.util.ClassUtils;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import java.util.List;
import java.util.Date;
import java.lang.reflect.Constructor;

/**
 * @author Gerhard Petracek
 * @since x.x.3
 */
public class DateRangeStrategy extends AbstractDynamicAnnotationValidationStrategy<DateRange, Date>
{
    private DateRangeEntry dateRangeEntry;

    protected boolean init(MetaDataEntry metaDataEntry)
    {
        List foundProvider = this.parameterExtractor.extract(this.constraint, getConstraintAspectKeyForValidation());

        if (foundProvider != null && foundProvider.size() > 0)
        {
            DateRangeProvider resolver;
            Class validatorClass;
            for (Object entry : foundProvider)
            {
                if(entry instanceof Class)
                {
                    validatorClass = (Class)entry;

                    if (DateRangeProvider.class.isAssignableFrom(validatorClass))
                    {
                        for(Constructor constructor : validatorClass.getDeclaredConstructors())
                        {
                            constructor.setAccessible(true);
                            try
                            {
                                resolver = (DateRangeProvider) constructor.newInstance(
                                        metaDataEntry.getProperty(PropertyInformationKeys.PROPERTY_DETAILS, PropertyDetails.class).getBaseObject());
                            }
                            catch (Throwable e)
                            {
                                resolver = (DateRangeProvider) ClassUtils.tryToInstantiateClass(validatorClass);
                            }

                            if(resolver != null)
                            {
                                this.dateRangeEntry = resolver.getDateRange();
                                break;
                            }
                        }
                    }
                }
                else if(entry instanceof DateRangeProvider)
                {
                    this.dateRangeEntry = ((DateRangeProvider)entry).getDateRange();
                }
            }
        }

        return this.dateRangeEntry != null;
    }

    protected boolean isValid(FacesContext facesContext, UIComponent uiComponent, MetaDataEntry metaDataEntry, Date value)
    {
        return value.after(this.dateRangeEntry.getFrom()) && value.before(this.dateRangeEntry.getTo());
    }

    @Override
    protected Class getConstraintAspectKeyForValidation()
    {
        return DateRangeProvider.class;
    }

    /*
     * optional
     */
    @Override
    protected String getCustomValidationErrorMsgKey(DateRange constraint)
    {
        return constraint.message();
    }

    @Override
    protected String getDefaultValidationErrorMsgKey()
    {
        return DateRange.DEFAULT_VALIDATION_ERROR_MESSAGE_KEY;
    }
}
