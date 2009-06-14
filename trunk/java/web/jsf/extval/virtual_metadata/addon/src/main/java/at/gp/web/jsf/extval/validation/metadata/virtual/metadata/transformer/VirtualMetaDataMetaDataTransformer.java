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
package at.gp.web.jsf.extval.validation.metadata.virtual.metadata.transformer;

import org.apache.myfaces.extensions.validator.core.metadata.transformer.MetaDataTransformer;
import org.apache.myfaces.extensions.validator.core.metadata.MetaDataEntry;
import org.apache.myfaces.extensions.validator.core.validation.strategy.ValidationStrategy;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;

import java.util.Map;
import java.util.HashMap;

import at.gp.web.jsf.extval.validation.metadata.virtual.annotation.VirtualMetaData;

/**
 * @author Gerhard Petracek
 *
 * @since 1.x.2
 */
public class VirtualMetaDataMetaDataTransformer implements MetaDataTransformer
{
    public Map<String, Object> convertMetaData(MetaDataEntry metaData)
    {
        if(metaData.getValue() instanceof VirtualMetaData)
        {
            ValidationStrategy validationStrategy = ExtValUtils.getValidationStrategyForMetaData(metaData.getProperty(VirtualMetaData.TARGET, MetaDataEntry.class).getKey());
            if(validationStrategy != null)
            {
                MetaDataTransformer metaDataTransformer = ExtValUtils.getMetaDataTransformerForValidationStrategy(validationStrategy);

                if(metaDataTransformer != null)
                {
                    return metaDataTransformer.convertMetaData(metaData.getProperty(VirtualMetaData.TARGET, MetaDataEntry.class));
                }
            }
        }
        return new HashMap<String, Object>();
    }
}
