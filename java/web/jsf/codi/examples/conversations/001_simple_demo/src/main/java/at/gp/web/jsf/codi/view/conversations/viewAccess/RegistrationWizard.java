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
package at.gp.web.jsf.codi.view.conversations.viewAccess;

import at.gp.web.jsf.codi.domain.User;
import at.gp.web.jsf.codi.service.ExistingUserNameException;
import at.gp.web.jsf.codi.service.UserService;
import at.gp.web.jsf.codi.view.AbstractPage;
import static at.gp.web.jsf.codi.view.ViewIdEnum.*;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ViewAccessScoped;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.Conversation;
import org.apache.myfaces.extensions.cdi.javaee.jsf.api.qualifier.Jsf;
import org.apache.myfaces.extensions.cdi.message.api.MessageContext;
import static org.apache.myfaces.extensions.cdi.message.api.payload.MessageSeverity.ERROR;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Gerhard Petracek
 */
@Named
@ViewAccessScoped
public class RegistrationWizard extends AbstractPage
{
    private static final long serialVersionUID = 8331083457606169609L;

    private String userName;

    private String password;

    @Inject
    private UserService userService;

    @Inject @Jsf
    private MessageContext messageContext;

    @Inject
    private Conversation conversation;

    public String step2()
    {
        return navigateTo(VIEW_ACCESS_CONVERSATION_STEP2);
    }

    public String step3()
    {
        return navigateTo(VIEW_ACCESS_CONVERSATION_STEP3);
    }

    public String register()
    {
        try
        {
            this.userService.registerUser(this.userName, this.password);
        }
        catch (ExistingUserNameException e)
        {
            this.messageContext.message()
                    .text("the user-name " + this.userName + " already exists")
                    .payload(ERROR)
                    .add();

            this.conversation.restart();
            return navigateTo(VIEW_ACCESS_CONVERSATION_STEP1);
        }
        return navigateTo(LOGIN);
    }

    public User getNewUser()
    {
        return new User(this.userName, this.password);
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}
