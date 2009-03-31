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
import at.gp.web.jsf.extval.validation.bypass.annotation.BypassValidationController;
import at.gp.web.jsf.extval.validation.bypass.annotation.ViewId;

/**
 * @author Gerhard Petracek
 */
@BypassValidationController
public class PersonPage2 extends PersonPage
{
    @Override
    @BypassValidationController(
            @ViewId(value = "/pages/new_person2.xhtml",
                    condition = "#{currentUserRole.privileged}",
                    all = true))
    public Person getPerson()
    {
        return super.getPerson();
    }
}