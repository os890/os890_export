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
package at.gp.web.jsf.extval.severity.warn;

import javax.faces.event.PhaseListener;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;

/**
 * @author Gerhard Petracek
 * @since x.x.3
 */
public class ContinueWithWarningsPhaseListener implements PhaseListener
{
    private static final long serialVersionUID = -3750556189842345645L;

    public void afterPhase(PhaseEvent phaseEvent)
    {
    }

    public void beforePhase(PhaseEvent phaseEvent)
    {
        //to activate the new value before it comes to navigation or at least before it comes to rendering
        if(phaseEvent.getPhaseId().equals(PhaseId.INVOKE_APPLICATION) ||
                phaseEvent.getPhaseId().equals(PhaseId.RENDER_RESPONSE))
        {
            useNewState();
        }
    }

    private void useNewState()
    {
        Object foundBean = WarnStateUtils.tryToFindExistingWarnStateBean();

        if(foundBean instanceof WarnStateBean)
        {
            ((WarnStateBean)foundBean).useNewState();
        }
    }

    public PhaseId getPhaseId()
    {
        return PhaseId.ANY_PHASE;
    }
}
