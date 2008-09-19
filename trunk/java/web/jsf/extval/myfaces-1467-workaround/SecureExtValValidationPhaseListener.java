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
package at.gp.web.jsf.extval.validation.secure;

import javax.faces.event.PhaseListener;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.context.FacesContext;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.RenderKit;
import javax.faces.FactoryFinder;

/**
 * @author Gerhard Petracek
 */
public class SecureExtValValidationPhaseListener implements PhaseListener
{
    public void afterPhase(PhaseEvent event)
    {
    }

    public void beforePhase(PhaseEvent event)
    {
        if(event.getPhaseId() != PhaseId.APPLY_REQUEST_VALUES && event.getPhaseId() != PhaseId.RENDER_RESPONSE)
        {
            return;
        }

        FacesContext facesContext = FacesContext.getCurrentInstance();

        RenderKitFactory renderKitFactory = (RenderKitFactory)
            FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);

        String renderKitId = facesContext.getViewRoot().getRenderKitId();
        RenderKit renderKit = renderKitFactory.getRenderKit(FacesContext.getCurrentInstance(), renderKitId);
        renderKitFactory.addRenderKit(SecureExtValRenderKit.ID, new SecureExtValRenderKit(renderKit));
        facesContext.getViewRoot().setRenderKitId(SecureExtValRenderKit.ID);
    }

    public PhaseId getPhaseId()
    {
        return PhaseId.ANY_PHASE;
    }
}
