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
package at.gp.web.jsf.extval.metadata;

import org.apache.myfaces.extensions.validator.baseval.annotation.Length;
import org.apache.myfaces.extensions.validator.baseval.annotation.Pattern;
import org.apache.myfaces.extensions.validator.core.validation.parameter.ViolationSeverity;
import at.gp.web.jsf.extval.validation.metadata.provider.annotation.MetaDataProvider;
import at.gp.web.jsf.extval.validation.metadata.priority.ValidationPriority;
import at.gp.web.jsf.extval.domain.Person;

/**
 * via typesafe MetaDataProvider.value and overridden getter method
 * it extends the target class to allow better ide support like refactoring and error detection via @Override
 *
 * the add-on uses scannotation to find this class
 *
 * @author Gerhard Petracek
 */
@MetaDataProvider(Person.class)
public class PersonMetaDataProvider extends Person
{
    @Override
    @Length(minimum = 3, parameters = {ViolationSeverity.Warn.class, ValidationPriority.Low.class})
    @Pattern(value = "[A-Z][a-z]+", parameters = {ViolationSeverity.Warn.class, ValidationPriority.High.class})
    public String getLastName()
    {
        throw new UnsupportedOperationException();
    }
}