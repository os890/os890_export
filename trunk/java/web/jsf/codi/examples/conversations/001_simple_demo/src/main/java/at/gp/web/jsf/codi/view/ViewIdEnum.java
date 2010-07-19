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
package at.gp.web.jsf.codi.view;

/**
 * @author Gerhard Petracek
 */
public enum ViewIdEnum
{
    LOGIN("login"),
    OVERVIEW("overview"),
    GROUPED_CONVERSATION_STEP1("conversations/grouped/registration_step01"),
    GROUPED_CONVERSATION_STEP2("conversations/grouped/registration_step02"),
    GROUPED_CONVERSATION_STEP3("conversations/grouped/registration_step03"),

    VIEW_ACCESS_CONVERSATION_STEP1("conversations/viewAccess/registration_step01"),
    VIEW_ACCESS_CONVERSATION_STEP2("conversations/viewAccess/registration_step02"),
    VIEW_ACCESS_CONVERSATION_STEP3("conversations/viewAccess/registration_step03")
    ;

    private String shortPath;

    ViewIdEnum(String shortPath)
    {
        this.shortPath = shortPath;
    }


    @Override
    public String toString()
    {
        return "/pages/" + shortPath + ".xhtml";
    }
}
