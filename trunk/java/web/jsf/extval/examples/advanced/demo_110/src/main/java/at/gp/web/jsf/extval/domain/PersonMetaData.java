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

import org.apache.myfaces.extensions.validator.baseval.annotation.Length;
import org.apache.myfaces.extensions.validator.core.validation.parameter.ViolationSeverity;
import at.gp.web.jsf.extval.validation.metadata.provider.annotation.MetaDataProvider;
import at.gp.web.jsf.extval.validation.metadata.virtual.annotation.VirtualMetaData;
import at.gp.web.jsf.extval.validation.metadata.priority.ValidationPriority;

import javax.persistence.Column;

/**
 * via name convention *MetaData
 * 
 * @author Gerhard Petracek
 */
@MetaDataProvider
public class PersonMetaData
{
    @Length(minimum = 2, parameters = ViolationSeverity.Warn.class)
    @VirtualMetaData(target = Column.class, parameters = {ValidationPriority.Highest.class})
    private String firstName;
}
