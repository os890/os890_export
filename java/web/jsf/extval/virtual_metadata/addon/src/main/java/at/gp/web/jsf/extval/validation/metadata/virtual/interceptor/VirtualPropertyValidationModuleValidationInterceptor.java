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
package at.gp.web.jsf.extval.validation.metadata.virtual.interceptor;

import at.gp.web.jsf.extval.validation.metadata.virtual.annotation.VirtualMetaData;
import at.gp.web.jsf.extval.validation.metadata.virtual.strategy.VirtualMetaDataStrategy;
import org.apache.myfaces.extensions.validator.PropertyValidationModuleValidationInterceptor;
import org.apache.myfaces.extensions.validator.core.metadata.MetaDataEntry;
import org.apache.myfaces.extensions.validator.core.validation.strategy.ValidationStrategy;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;

/**
 * @author Rudy De Busscher
 */
public class VirtualPropertyValidationModuleValidationInterceptor extends PropertyValidationModuleValidationInterceptor
{
    @Override
    protected boolean isValidationStrategyCompatibleWithValue(ValidationStrategy validationStrategy,
                                                              Object value,
                                                              MetaDataEntry metaDataEntry)
    {
        if (validationStrategy instanceof VirtualMetaDataStrategy)
        {
            MetaDataEntry targetMetaDate = metaDataEntry.getProperty(VirtualMetaData.TARGET, MetaDataEntry.class);
            ValidationStrategy targetValidationStrategy = ExtValUtils.getValidationStrategyForMetaData(targetMetaDate.getKey());

            return isValidationStrategyCompatibleWithValue(targetValidationStrategy, value, targetMetaDate);
        }

        return super.isValidationStrategyCompatibleWithValue(validationStrategy, value, metaDataEntry);
    }
}
