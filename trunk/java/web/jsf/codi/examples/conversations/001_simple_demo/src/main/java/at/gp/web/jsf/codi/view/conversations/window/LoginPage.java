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
package at.gp.web.jsf.codi.view.conversations.window;

import at.gp.web.jsf.codi.domain.User;
import at.gp.web.jsf.codi.service.UserService;
import at.gp.web.jsf.codi.view.AbstractPage;
import static at.gp.web.jsf.codi.view.ViewIdEnum.*;

import at.gp.web.jsf.codi.view.config.Pages;
import org.apache.myfaces.extensions.cdi.core.api.config.view.ViewConfig;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.Conversation;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationScoped;
import org.apache.myfaces.extensions.cdi.jsf.api.Jsf;
import org.apache.myfaces.extensions.cdi.message.api.MessageContext;
import static org.apache.myfaces.extensions.cdi.message.api.payload.MessageSeverity.ERROR;

import javax.enterprise.inject.New;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Gerhard Petracek
 */
@Named
@ConversationScoped
public class LoginPage extends AbstractPage
{
    private static final long serialVersionUID = 8871807980080906751L;

    @Inject
    @New
    private User userToLogIn;

    @Inject
    private UserService userService;

    @Inject
    LoginManager loginManager;

    @Inject
    private Conversation conversation;

    @Inject @Jsf
    private MessageContext messageContext;

    public String login()
    {
        if(this.userService.checkCredentials(this.userToLogIn.getUserName(), this.userToLogIn.getPassword()))
        {
            this.loginManager.setLoggedInUser(this.userToLogIn);

            this.conversation.close(); //cleanup manually - we don't wait for the time-out
            return navigateTo(OVERVIEW);
        }

        this.conversation.restart(); //just for demo-cases
        this.messageContext.message().text("invalid login").payload(ERROR).add();

        return navigateTo(LOGIN);
    }

    public Class<? extends ViewConfig> newRegistrationGroupedConversation()
    {
        return Pages.Conversations.Grouped.Registration_step01.class;
    }

    public String newRegistrationViewAccessConversation()
    {
        return navigateTo(VIEW_ACCESS_CONVERSATION_STEP1);
    }

    public User getUserToLogIn()
    {
        return userToLogIn;
    }
}