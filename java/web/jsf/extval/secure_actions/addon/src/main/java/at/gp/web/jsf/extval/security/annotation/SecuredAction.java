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
package at.gp.web.jsf.extval.security.annotation;

import org.apache.myfaces.extensions.validator.internal.UsageCategory;
import org.apache.myfaces.extensions.validator.internal.UsageInformation;

import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * @author Gerhard Petracek
 * @since 1.x.1
 */
@Target({METHOD})
@Retention(RUNTIME)
@UsageInformation(UsageCategory.API)
public @interface SecuredAction
{
    static final String DEFAULT_OUTCOME = "at.gp.web.jsf.extval.security.DEFAULT_OUTCOME";
    static final String MESSAGE_BUNDLE = "at.gp.web.jsf.extval.security.MESSAGE_BUNDLE";

    String[] viewIds() default "*";

    String[] permittedIf();

    String secureAction() default "";

    //optional to overrice the outcome of the action method

    String secureOutcome() default DEFAULT_OUTCOME;

    String securityErrorMsgKey() default "";

    boolean regularLifecycleExecution() default true;

    @Target({METHOD})
    @Retention(RUNTIME)
            @interface List
    {

        SecuredAction[] value();
    }
}
