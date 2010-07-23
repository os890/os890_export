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

import java.util.ArrayList;
import java.util.List;

import javax.el.ELContext;
import javax.el.ValueExpression;

import org.apache.myfaces.extensions.validator.test.base.mock.ExtValMockValueExpression;
import org.apache.shale.test.el.MockValueExpression;

/**
 * A valueExpression implementation, that besides the literal an pure EL versions (see MockValueExpression),
 * is capable of handling mixed versions for strings.
 * so, no only expressions like '#{msg['key']}' are allowed but also '#{msg['key']} :'
 * 
 * TODO In the future, this will be integrated into myfaces-test and testing parts of Extval 
 * and there will be no need to define it here. 
 * 
 * @author Rudy De Busscher
 */
public class CompositeMockValueExpression extends MockValueExpression
{

    private static final long serialVersionUID = 2645070462654392076L;

    private List<ValueExpression> valueExpressionChain;

    public CompositeMockValueExpression(String expression, Class expectedType)
    {

        super("#{}", expectedType);
        valueExpressionChain = new ArrayList<ValueExpression>();
        StringBuilder parser = new StringBuilder(expression);
        int pos = parser.indexOf("#{");
        while (pos > -1 || parser.length() > 0)
        {
            // We have a constant first
            if (pos > 0)
            {
                valueExpressionChain.add(new ExtValMockValueExpression(parser
                        .substring(0, pos), expectedType));
                parser.delete(0, pos);
            }
            // We have an el, maybe literal at the end
            if (pos == 0)
            {
                int pos2 = parser.indexOf("}");
                valueExpressionChain.add(new ExtValMockValueExpression(parser
                        .substring(0, pos2 + 1), expectedType));

                parser.delete(0, pos2 + 1);
            }
            // Only literal
            if (pos == -1)
            {
                valueExpressionChain.add(new ExtValMockValueExpression(parser
                        .toString(), expectedType));

                parser.setLength(0);
            }
            pos = parser.indexOf("#{");
        }

    }

    @Override
    public Object getValue(ELContext context)
    {
        // Well only composite strings are supported.

        StringBuilder result = new StringBuilder();
        for (ValueExpression valueExpression : valueExpressionChain)
        {
            result.append(valueExpression.getValue(context));
        }
        return result.toString();
    }

    @Override
    public String getExpressionString()
    {
        StringBuilder result = new StringBuilder();
        for (ValueExpression valueExpression : valueExpressionChain)
        {
            result.append(valueExpression.getExpressionString());
        }
        return result.toString();
    }

    @Override
    public boolean isReadOnly(ELContext context)
    {
        return true;
    }

}
