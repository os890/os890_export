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
package at.gp.web.jsf.extval.beanval.form.validation;

import org.apache.myfaces.extensions.validator.internal.ToDo;
import org.apache.myfaces.extensions.validator.internal.Priority;

import javax.validation.groups.Default;
import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * @author Gerhard Petracek
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface FormValidator
{
    @ToDo(value = Priority.LOW, description = "rename to formValidationBeanClasses and update documentation")
    Class<? extends FormBean>[] formValidationBeanClass() default FormBean.class;

    String[] viewIds() default "*";

    String[] conditions() default "#{true}";

    Class[] groups() default {Default.class};

    @Target({TYPE})
    @Retention(RUNTIME)
    @Documented
    static @interface List
    {
        FormValidator[] value();
    }
}