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

import org.apache.myfaces.extensions.validator.core.ExtValContext;
import org.apache.myfaces.extensions.validator.core.ExtValModuleConfiguration;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;
import org.apache.myfaces.extensions.validator.internal.UsageInformation;

import at.gp.web.jsf.extval.label.initializer.RequiredLabelInitializer;

/**
 * @author Gerhard Petracek
 */
public abstract class RequiredLabelAddonConfiguration implements ExtValModuleConfiguration
{
    private static ExtValContext extValContext = null;

    protected RequiredLabelAddonConfiguration()
    {
    }

    /**
     * Don't access ExtValContext during initialization of the class.  OpenWebBeans initializes all classes during
     * startup of the WebContainer.  extValContext constructor tries to access Web.xml parameters through FacesContext
     * which isn't available yet.
     * @return
     */
    private static  ExtValContext getExtValContext() {
        if (extValContext == null) {
            extValContext = ExtValContext.getContext();
        }
        return extValContext;
    }
    
    public static RequiredLabelAddonConfiguration get()
    {
        return getExtValContext().getModuleConfiguration(RequiredLabelAddonConfiguration.class);
    }

    @UsageInformation(UsageCategory.INTERNAL)
    public static boolean use(RequiredLabelAddonConfiguration config, boolean forceOverride)
    {
        return getExtValContext().addModuleConfiguration(RequiredLabelAddonConfiguration.class, config, forceOverride);
    }

    /*
     * web.xml config
     */

    public abstract String getRequiredMarker();

    public abstract String getPlaceMarker();

    public abstract String getRequiredStyleClass();

    public abstract RequiredLabelInitializer getRequiredLabelInitializer();
}
