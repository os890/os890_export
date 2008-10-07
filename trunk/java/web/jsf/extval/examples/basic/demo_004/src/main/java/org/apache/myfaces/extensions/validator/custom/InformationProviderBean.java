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

import org.apache.myfaces.extensions.validator.core.CustomInfo;

import java.util.Map;

/**
 * instead of using the convention for the information provider bean class you can use a managed bean with the name:
 * org_apache_myfaces_extensions_validator_custom_InformationProviderBean
 *
 * instead of using the java api you can also use the web.xml context-param instead
 * (param-name: org.apache.myfaces.extensions.validator.CUSTOM_MESSAGE_BUNDLE)
 *
 * @author Gerhard Petracek
 */
public class InformationProviderBean  extends org.apache.myfaces.extensions.validator.core.InformationProviderBean
{
    @Override
    protected void applyCustomValues(Map<CustomInfo, String> map)
    {
        map.put(CustomInfo.BASE_PACKAGE, "at.gp.web.jsf.extval.");
        map.put(CustomInfo.CONVENTION_FOR_CUSTOM_MESSAGE_BUNDLE, "bundle.messages");
    }
}
