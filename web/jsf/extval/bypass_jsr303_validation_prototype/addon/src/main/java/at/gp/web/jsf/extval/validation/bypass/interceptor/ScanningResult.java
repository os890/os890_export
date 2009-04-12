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

import javax.faces.context.FacesContext;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Gerhard Petracek
 */
class ScanningResult
{
    private Map<String, Boolean> foundViewIds = new HashMap<String, Boolean>();

    public void addViewId(String viewId, boolean skipAll)
    {
        this.foundViewIds.put(viewId, skipAll);
    }

    public boolean isBypassAll()
    {
        String currentViewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
        return this.foundViewIds.containsKey(currentViewId) && this.foundViewIds.get(currentViewId);
    }

    public boolean isBypassAllSkipableValidations()
    {
        String currentViewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
        return this.foundViewIds.containsKey(currentViewId) && ! this.foundViewIds.get(currentViewId);
    }
}
