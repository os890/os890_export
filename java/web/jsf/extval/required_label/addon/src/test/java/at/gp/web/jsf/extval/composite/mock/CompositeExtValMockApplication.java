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
package at.gp.web.jsf.extval.composite.mock;

import javax.el.ELContextListener;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import org.apache.shale.test.mock.MockApplication;

/**
 *  A MockApplication subclass needed to use the CompositeMockValueExpression class.
 *  
 *  @author Rudy De Busscher
 */
public class CompositeExtValMockApplication extends MockApplication
{

    private Application wrapped;

    public CompositeExtValMockApplication()
    {
        super();
    }

    public CompositeExtValMockApplication(Application application)
    {
        super();
        this.wrapped = application;
    }

    @Override
    public ELContextListener[] getELContextListeners()
    {
        return new ELContextListener[0];
    }

    @Override
    public ELResolver getELResolver()
    {
        return this.wrapped.getELResolver();
    }

    @Override
    public ExpressionFactory getExpressionFactory()
    {
        return new CompositeExtValMockExpressionFactory();
    }

    @Override
    public Object evaluateExpressionGet(FacesContext facesContext,
            String expression, Class aClass) throws ELException
    {
        return wrapped.evaluateExpressionGet(facesContext, expression, aClass);
    }

}
