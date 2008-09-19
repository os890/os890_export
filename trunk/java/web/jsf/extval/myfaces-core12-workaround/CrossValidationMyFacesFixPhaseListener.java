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
package at.gp.web.jsf.extval.crossval;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.component.UIViewRoot;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * workaround for using cross-validation of MyFaces-Extensions-Validator with the current core-1.2.x releases
 * see the following issues: MYFACES-1894 and MYFACES-1895
 *
 * @author Gerhard Petracek
 */
public class CrossValidationMyFacesFixPhaseListener implements PhaseListener {

    public void afterPhase(PhaseEvent event) {
    }

    public void beforePhase(PhaseEvent event) {
        UIViewRoot uiViewRoot = event.getFacesContext().getViewRoot();
        try {
            Method clearEventsMethod = uiViewRoot.getClass().getDeclaredMethod("clearEvents");
            clearEventsMethod.setAccessible(true);
            clearEventsMethod.invoke(uiViewRoot);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }
}