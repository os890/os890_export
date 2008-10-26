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
package at.gp.web.jsf.extval;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.event.PhaseListener;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;

/**
 * @author Gerhard Petracek
 */
public class LoggingPhaseListener implements PhaseListener
{
    private final Log logger = LogFactory.getLog(getClass());

    public void afterPhase(PhaseEvent event)
    {
        this.logger.debug("after " + event.getPhaseId());
    }

    public void beforePhase(PhaseEvent event)
    {
        this.logger.debug("before " + event.getPhaseId());
    }

    public PhaseId getPhaseId()
    {
        return PhaseId.ANY_PHASE;
    }
}
