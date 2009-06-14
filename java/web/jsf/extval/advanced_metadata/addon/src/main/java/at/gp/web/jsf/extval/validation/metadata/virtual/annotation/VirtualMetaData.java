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
package at.gp.web.jsf.extval.validation.metadata.virtual.annotation;

import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;
import org.apache.myfaces.extensions.validator.core.validation.parameter.ViolationSeverity;
import org.apache.myfaces.extensions.validator.core.validation.parameter.ValidationParameter;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Gerhard Petracek
 * @since 1.x.3
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
@UsageInformation(UsageCategory.API)
public @interface VirtualMetaData
{
    static final String TARGET = "target_metadata";

    Class<? extends Annotation> target();

    String[] stringValues() default {};
    
    double[] doubleValues() default {};

    Class<? extends ValidationParameter>[] parameters() default ViolationSeverity.Error.class;
}