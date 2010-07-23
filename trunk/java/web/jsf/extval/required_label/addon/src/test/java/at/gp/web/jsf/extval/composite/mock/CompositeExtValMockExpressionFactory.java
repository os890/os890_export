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

import javax.el.ELContext;
import javax.el.ValueExpression;

import org.apache.myfaces.extensions.validator.test.base.mock.ExtValMockValueExpression;
import org.apache.shale.test.el.MockExpressionFactory;

/**
 * A factory needed to use the CompositeMockValueExpression class.
 */
public class CompositeExtValMockExpressionFactory extends MockExpressionFactory
{
    @Override
    public ValueExpression createValueExpression(ELContext context,
            String expression, Class expectedType)
    {
        ValueExpression result = null;
        try
        {
            result = new ExtValMockValueExpression(expression, expectedType);
        }
        catch (IllegalArgumentException exc)
        {
            result = new CompositeMockValueExpression(expression, expectedType);

        }
        return result;
    }
}
