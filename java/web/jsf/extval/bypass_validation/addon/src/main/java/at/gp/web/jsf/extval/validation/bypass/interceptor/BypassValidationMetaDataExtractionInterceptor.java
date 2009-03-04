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
package at.gp.web.jsf.extval.validation.bypass.interceptor;

import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;
import org.apache.myfaces.extensions.validator.core.interceptor.MetaDataExtractionInterceptor;
import org.apache.myfaces.extensions.validator.core.property.PropertyInformation;
import org.apache.myfaces.extensions.validator.core.property.PropertyInformationKeys;
import at.gp.web.jsf.extval.validation.bypass.util.BypassValidationUtils;

/**
 * @author Gerhard Petracek
 */
@UsageInformation(UsageCategory.INTERNAL)
public class BypassValidationMetaDataExtractionInterceptor implements MetaDataExtractionInterceptor
{
    public void afterExtracting(PropertyInformation propertyInformation)
    {
        if(BypassValidationUtils.bypassAllSkipableValidationsForRequest())
        {
            //if there are different maps, a separate interceptor is needed
            //to add this information to every meta-data entry
            propertyInformation.setInformation(PropertyInformationKeys.SKIP_VALIDATION, true);
        }
    }
}
