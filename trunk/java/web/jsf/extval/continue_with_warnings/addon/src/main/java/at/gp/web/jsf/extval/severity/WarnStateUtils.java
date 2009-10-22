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
package at.gp.web.jsf.extval.severity;

import org.apache.myfaces.extensions.validator.util.ExtValUtils;

import javax.faces.context.FacesContext;
import java.util.Map;

/**
 * @author Gerhard Petracek
 * @since x.x.3
 */
public class WarnStateUtils
{
    private static final String
            EXTVAL_WARN_STATE_COMPONENT_ID = "extValWarnState";
    private static final String
            EXTVAL_WARN_STATE_BEAN_NAME = "extValWarnState";
    private static final String
            EXTVAL_FORCE_CONTINUE_WITH_WARNINGS_PARAMETER_NAME = "extValForceContinueWithWarnings";

    public static boolean isForceContinueWithWarningsParameter(String name)
    {
        return EXTVAL_FORCE_CONTINUE_WITH_WARNINGS_PARAMETER_NAME.equals(name);
    }

    public static boolean isWarnStateComponentId(String id)
    {
        return id.equals(EXTVAL_WARN_STATE_COMPONENT_ID);
    }

    /*
     * just resolve it if it's already created - don't force creation!!!
     */
    public static WarnStateBean tryToFindExistingWarnStateBean()
    {
        Map requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();

        if (!requestMap.containsKey(EXTVAL_WARN_STATE_BEAN_NAME))
        {
            return null;
        }

        Object foundBean = requestMap.get(EXTVAL_WARN_STATE_BEAN_NAME);

        if (!(foundBean instanceof WarnStateBean))
        {
            return null;
        }
        return (WarnStateBean) foundBean;
    }

    public static WarnStateBean getOrCreateWarnStateBean()
    {
        return (WarnStateBean)ExtValUtils.getELHelper().getBean(EXTVAL_WARN_STATE_BEAN_NAME);
    }
}