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
package at.gp.web.jsf.codi;

import org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStage;

import javax.enterprise.event.Observes;
import javax.faces.event.PostConstructApplicationEvent;
import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * @author Gerhard Petracek
 */
public class StartupListener
{
    private final Logger logger = Logger.getLogger(StartupListener.class.getName());

    @Inject
    private ProjectStage projectStage;

    protected void initApp(@Observes PostConstructApplicationEvent proPostConstructApplicationEvent)
    {
        if(ProjectStage.Development.equals(this.projectStage))
        {
            this.logger.info("Welcome to a MyFaces CODI demo! Please also have a look at the documentation: https://cwiki.apache.org/confluence/display/EXTCDI/Documentation");
        }
        else
        {
            this.logger.info("Observed MyFaces CODI for JSF 2.0 startup.");
        }
    }
}
