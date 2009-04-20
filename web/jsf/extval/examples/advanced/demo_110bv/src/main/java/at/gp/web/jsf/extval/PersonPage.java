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

import at.gp.web.jsf.extval.domain.Person;
import at.gp.web.jsf.extval.group.User;
import at.gp.web.jsf.extval.group.Address;

import javax.faces.event.ValueChangeEvent;
import javax.faces.context.FacesContext;

import org.apache.myfaces.extensions.validator.beanval.annotation.BeanValidation;
import org.apache.myfaces.extensions.validator.beanval.annotation.ModelValidation;

/**
 * @author Gerhard Petracek
 */
@BeanValidation(useGroups = User.class)
public class PersonPage
{
    public String send()
    {
        return ("success");
    }

    @BeanValidation(useGroups = Address.class,
            modelValidation = @ModelValidation(
                    isActive = true, validationTargets = "#{person}",
                    message = "forced violation - model reverted"))
    public String sendWithModelValidation()
    {
        return ("success");
    }

    //...

    private Person person;
    private Role role;

    public void changeRole(ValueChangeEvent event)
    {
        this.role.setRoleName((String)event.getNewValue());
        FacesContext.getCurrentInstance().renderResponse();
    }

    public Person getPerson()
    {
        return person;
    }

    public void setPerson(Person person)
    {
        this.person = person;
    }

    public Role getRole()
    {
        return role;
    }

    public void setRole(Role role)
    {
        this.role = role;
    }
}