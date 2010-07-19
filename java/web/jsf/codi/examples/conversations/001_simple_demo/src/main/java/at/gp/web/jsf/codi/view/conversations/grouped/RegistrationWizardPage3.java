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

import at.gp.web.jsf.codi.domain.User;
import at.gp.web.jsf.codi.service.ExistingUserNameException;
import at.gp.web.jsf.codi.service.UserService;
import at.gp.web.jsf.codi.view.AbstractPage;
import static at.gp.web.jsf.codi.view.ViewIdEnum.GROUPED_CONVERSATION_STEP1;
import static at.gp.web.jsf.codi.view.ViewIdEnum.LOGIN;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.Conversation;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationGroup;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationScoped;
import org.apache.myfaces.extensions.cdi.javaee.jsf.api.qualifier.Jsf;
import org.apache.myfaces.extensions.cdi.message.api.MessageContext;
import static org.apache.myfaces.extensions.cdi.message.api.payload.MessageSeverity.ERROR;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Just as demo-case!
 *
 * @author Gerhard Petracek
 */
@Named
@ConversationScoped
@ConversationGroup(WizardGroup.class)
public class RegistrationWizardPage3 extends AbstractPage
{
    private static final long serialVersionUID = -3789975058003904263L;

    @Inject
    private UserService userService;

    @Inject
    private Conversation wizardConversation;

    @Inject @Jsf
    private MessageContext messageContext;

    //Just as demo-case!
    private RegistrationWizardPage1 registrationWizardPage1;
    private RegistrationWizardPage2 registrationWizardPage2;

    protected RegistrationWizardPage3()
    {
    }

    @Inject
    public RegistrationWizardPage3(@ConversationGroup(WizardGroup.class) RegistrationWizardPage1 registrationWizardPage1,
                                   @ConversationGroup(WizardGroup.class) RegistrationWizardPage2 registrationWizardPage2)
    {
        this.registrationWizardPage1 = registrationWizardPage1;
        this.registrationWizardPage2 = registrationWizardPage2;
    }

    public String register()
    {
        //Just as demo-case!

        try
        {
            this.userService.registerUser(this.registrationWizardPage1.getUserName(),
                                          this.registrationWizardPage2.getPassword());
        }
        catch (ExistingUserNameException e)
        {
            this.messageContext.message()
                    .text("the user-name " + this.registrationWizardPage1.getUserName() + " already exists")
                    .payload(ERROR)
                    .add();
            this.wizardConversation.restart();
            return navigateTo(GROUPED_CONVERSATION_STEP1);
        }

        //this call ends the whole conversation (-> all 3 page-beans get destroyed)
        this.wizardConversation.end();
        return navigateTo(LOGIN);
    }

    public User getNewUser()
    {
        //Just as demo-case!
        return new User(this.registrationWizardPage1.getUserName(),
                        this.registrationWizardPage2.getPassword());
    }
}