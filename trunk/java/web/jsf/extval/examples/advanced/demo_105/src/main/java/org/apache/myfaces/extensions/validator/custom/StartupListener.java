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
package org.apache.myfaces.extensions.validator.custom;

import org.apache.myfaces.extensions.validator.core.startup.AbstractStartupListener;
import org.apache.myfaces.extensions.validator.core.ExtValContext;
import org.apache.myfaces.extensions.validator.core.loader.StaticMappingConfigLoaderNames;
import org.apache.myfaces.extensions.validator.core.loader.StaticInMemoryMappingConfig;
import org.apache.myfaces.extensions.validator.baseval.strategy.JpaValidationStrategy;

/**
 * @author Gerhard Petracek
 */
public class StartupListener extends AbstractStartupListener
{
    protected void init()
    {
        StaticInMemoryMappingConfig config = new StaticInMemoryMappingConfig();
        config.addMapping(JpaValidationStrategy.class.getName(), MyStaticJpaMessageResolver.class.getName());
        ExtValContext.getContext().addStaticMappingConfigLoader(StaticMappingConfigLoaderNames.VALIDATION_STRATEGY_TO_MESSAGE_RESOLVER_CONFIG_LOADER, config);
    }
}
