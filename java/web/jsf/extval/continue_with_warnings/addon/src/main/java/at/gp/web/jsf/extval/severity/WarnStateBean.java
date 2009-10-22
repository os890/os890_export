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

/**
 * @author Gerhard Petracek
 * @since x.x.3
 */
public class WarnStateBean
{
    private boolean lockedState = false;
    //the value shouldn't be available immediately + has to support locking for the current request
    //(if an error gets added the attribute is again false and gets locked
    // so after cleanup of all error-messages the warnings are displace once more.)
    private Boolean continueWithWarnings = false;
    private boolean useNewState = false;

    public boolean isContinueWithWarnings()
    {
        return continueWithWarnings && useNewState;
    }

    public void setContinueWithWarnings(boolean continueWithWarnings)
    {
        if(!this.lockedState)
        {
            this.continueWithWarnings = continueWithWarnings;
        }
    }

    public void useNewState()
    {
        this.useNewState = true;
    }

    /**
     * lock for the rest of the request
     */
    public void lock()
    {
        this.lockedState = true;
    }
}
