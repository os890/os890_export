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
package at.gp.web.jsf.codi.service;

import at.gp.web.jsf.codi.domain.User;

import javax.enterprise.context.SessionScoped;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Gerhard Petracek
 */
@SessionScoped
public class MockUserService implements UserService
{
    private static final long serialVersionUID = 4514721166574684170L;

    private Map<String, User> registeredUsers = new HashMap<String, User>();

    public void registerUser(String userName, String password)
    {
        User newUser = new User(userName, password);
        if(this.registeredUsers.containsKey(newUser.getUserName()))
        {
            throw new ExistingUserNameException(newUser.getUserName());
        }

        this.registeredUsers.put(newUser.getUserName(), newUser);
    }

    public boolean checkCredentials(String userName, String password)
    {
        return this.registeredUsers.containsKey(userName) &&
                this.registeredUsers.get(userName).getPassword().equals(password);
    }
}
