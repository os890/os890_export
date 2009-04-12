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

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import at.gp.web.jsf.extval.group.RestrictedUser;
import at.gp.web.jsf.extval.group.User;
import at.gp.web.jsf.extval.group.Address;

/**
 * @author Gerhard Petracek
 */
public class Person
{
    private Long id;

    @NotNull(groups = User.class)
    @Size(min = 3, max = 12, groups = User.class)
    private String firstName;

    @NotNull(groups = RestrictedUser.class)
    @Size.List({
            @Size(min = 3, max = 12, groups = RestrictedUser.class)
    })
    private String lastName;

    @NotNull(groups = Address.class, message = "street is required")
    private String street;
    @NotNull(groups = Address.class, message = "zip is required")
    private String zip;
    @NotNull(groups = Address.class, message = "city is required")
    private String city;

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
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getStreet()
    {
        return street;
    }

    public void setStreet(String street)
    {
        this.street = street;
    }

    public String getZip()
    {
        return zip;
    }

    public void setZip(String zip)
    {
        this.zip = zip;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }
}
