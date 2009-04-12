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
package at.gp.web.jsf.extval.validation.bypass.util;

import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;
import org.apache.myfaces.extensions.validator.core.ExtValContext;
import org.apache.myfaces.extensions.validator.core.CustomInformation;

import javax.faces.context.FacesContext;
import java.util.Map;

import at.gp.web.jsf.extval.validation.bypass.annotation.BypassBeanValidation;

/**
 * @author Gerhard Petracek
 */
@UsageInformation(UsageCategory.INTERNAL)
public class BypassBeanValidationUtils
{
    public static final String BYPASS_VALIDATION_KEY = ExtValContext.getContext().getInformationProviderBean().get(CustomInformation.BASE_PACKAGE) + "BYPASS_VALIDATION_KEY";

    public static void activateBypassAllValidationsForRequest(BypassBeanValidation bypassBeanValidation)
    {
        FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put(BYPASS_VALIDATION_KEY, bypassBeanValidation);
    }

    public static void resetBypassAllValidationsForRequest()
    {
        FacesContext.getCurrentInstance().getExternalContext().getRequestMap().remove(BYPASS_VALIDATION_KEY);
    }

    public static boolean bypassAllValidationsForRequest()
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map requestMap = facesContext.getExternalContext().getRequestMap();

        if(requestMap.containsKey(BYPASS_VALIDATION_KEY))
        {
            Object value = requestMap.get(BYPASS_VALIDATION_KEY);

            if(value instanceof BypassBeanValidation)
            {
                return true;
            }
        }

        return false;
    }

    public static boolean bypassAllSkipableValidationsForRequest()
    {
        return FacesContext.getCurrentInstance().getExternalContext().getRequestMap().containsKey(BYPASS_VALIDATION_KEY);
    }
}
