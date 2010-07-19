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
package at.gp.web.jsf.codi.view.conversations.grouped;

import at.gp.web.jsf.codi.view.AbstractPage;
import static at.gp.web.jsf.codi.view.ViewIdEnum.GROUPED_CONVERSATION_STEP2;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationGroup;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationScoped;

import javax.inject.Named;

/**
 * Just as demo-case!
 *
 * @author Gerhard Petracek
 */
@Named
@ConversationScoped
@ConversationGroup(WizardGroup.class)
public class RegistrationWizardPage1  extends AbstractPage
{
    private static final long serialVersionUID = 5697376230248988578L;

    private String userName;

    public String next()
    {
        return navigateTo(GROUPED_CONVERSATION_STEP2);
    }
    
    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }
}
