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
package at.gp.web.jsf.extval.validation;

import org.apache.myfaces.extensions.validator.core.startup.AbstractStartupListener;
import at.gp.web.jsf.extval.config.java.ExtValModuleRegistry;
import at.gp.web.jsf.extval.config.java.AbstractExtValModule;

/**
 * @author Gerhard Petracek
 */
public class CustomJavaConfigStartupListener extends AbstractStartupListener
{
    private static final long serialVersionUID = -374662941426622756L;

    protected void init()
    {
        ExtValModuleRegistry.startConfig().modules(new DemoStyle1()).endConfig();
    }

    class DemoStyle1 extends AbstractExtValModule
    {
        protected void configure()
        {
            bind(RequiredValidator.class).to(RequiredValidation.class, DefaultMessageResolver.class);
            add(CustomExceptionInterceptor.class);
        }
    }

    /*
     * not used in this example - just to illustrate different styles
     */
    class DemoStyle2 extends AbstractExtValModule
    {
        protected void configure()
        {
            bind(RequiredValidation.class).to(RequiredValidator.class);
            bind(RequiredValidator.class).to(DefaultMessageResolver.class);
        }
    }

    /*
     * not used in this example - just to illustrate different styles
     */
    class DemoStyle3 extends AbstractExtValModule
    {
        protected void configure()
        {
            bind(RequiredValidation.class).to(RequiredValidator.class);
            bind(DefaultMessageResolver.class).to(RequiredValidator.class);
        }
    }
}
