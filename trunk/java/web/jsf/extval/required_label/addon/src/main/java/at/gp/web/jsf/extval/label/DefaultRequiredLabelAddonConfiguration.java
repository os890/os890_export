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
package at.gp.web.jsf.extval.label;

import at.gp.web.jsf.extval.label.initializer.RequiredLabelInitializer;
import at.gp.web.jsf.extval.label.initializer.DefaultRequiredLabelInitializer;

/**
 * @author Gerhard Petracek
 */
public class DefaultRequiredLabelAddonConfiguration extends RequiredLabelAddonConfiguration
{
    private static final String DEFAULT_REQUIRED_MARKER = "* ";

    private static final String DEFAULT_PLACE_MARKER = "BEFORE";

    private static final String DEFAULT_REQUIRED_CLASS = "ExtValRequiredLabel";

    public String getRequiredMarker()
    {
        return getParameterValue(WebXmlParameter.REQUIRED_MARKER, DEFAULT_REQUIRED_MARKER);
    }

    public String getPlaceMarker()
    {
        return getParameterValue(WebXmlParameter.PLACE_MARKER, DEFAULT_PLACE_MARKER);
    }

    public String getRequiredStyleClass()
    {
        return getParameterValue(WebXmlParameter.REQUIRED_STYLE_CLASS, DEFAULT_REQUIRED_CLASS);
    }

    public RequiredLabelInitializer getRequiredLabelInitializer()
    {
        return new DefaultRequiredLabelInitializer();
    }

    /**
     * Read parameter and if no value is specified, use the default value.
     *
     * @param parameterValue the parameter value
     * @param defaultValue   the default value
     * @return parameter value to use
     */
    private String getParameterValue(final String parameterValue, final String defaultValue)
    {
        if (parameterValue == null || "".equals(parameterValue.trim()))
        {
            return defaultValue;
        }
        return parameterValue;
    }
}
