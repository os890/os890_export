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
package at.gp.web.jsf.extval.domain;

import at.gp.web.jsf.extval.validation.model.transactional.interceptor.ModelAwareValidation;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.myfaces.extensions.validator.baseval.annotation.Length;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * test this case via:<br/>
 * violate all constraints - you will only see the messages for the first name<br/>
 * fix the first-name violation -> you will see model aware and class level (not inline) violation messages<br/>
 * fix last name -> class level violation message
 * fix all -> no message
 *
 * @author Gerhard Petracek
 */
@Entity
public class Person
{
    protected final Log logger = LogFactory.getLog(Person.class);

    @Id
    private Long id;

    @Length(minimum = 3)
    @Column(nullable = false, length = 20)
    private String firstName;

    /*
     * use validation after the update model values phase e.g. if:
     * - your setter performs some logic and afterwards it has to be valid
     * - you would like to perform custom class level validation with your custom validation strategy
     * - you have a special use-case for it :)
     */
    @Length(minimum = 3, parameters = ModelAwareValidation.class)
    @Column(nullable = false, length = 40)
    private String lastName;

    private Car car = new Car();

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        //just to demonstrate the rollback
        this.logger.info("setFirstName: " + firstName);

        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        //just to demonstrate the rollback
        this.logger.info("setLastName: " + lastName);

        this.lastName = lastName;
    }

    public Car getCar()
    {
        return car;
    }
}
