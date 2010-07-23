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
package at.gp.web.jsf.extval.label.interceptor;

import java.io.IOException;

import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlOutputLabel;

import org.apache.myfaces.extensions.validator.PropertyValidationModuleStartupListener;

import at.gp.web.jsf.extval.label.AbstractTest;
import at.gp.web.jsf.extval.label.DefaultRequiredLabelAddonConfiguration;
import at.gp.web.jsf.extval.label.RequiredLabelAddonConfiguration;
import at.gp.web.jsf.extval.label.initializer.RequiredLabelInitializer;
import at.gp.web.jsf.extval.label.interceptor.model.DataBean;
import at.gp.web.jsf.extval.label.startup.LabelAwareInitializationStartupListener;

public class PropertyValidationAwareLabelRendererInterceptorTest extends AbstractTest
{

    private HtmlOutputLabel label;
    private HtmlInputText input;

    public PropertyValidationAwareLabelRendererInterceptorTest(String name)
    {
        super(name);

    }

    @Override
    protected void invokeStartupListeners()
    {

        new PropertyValidationModuleStartupListener()
        {
            private static final long serialVersionUID = -3274169061804065986L;

            @Override
            protected void init()
            {
                super.initModuleConfig();
                super.init();
            }
        }.init();

        // Startup listener instantiates the interceptor and thus parameter must be set at that time

        new LabelAwareInitializationStartupListener()
        {
            private static final long serialVersionUID = 7271421984062276284L;

            @Override
            protected void init()
            {
                super.initModuleConfig();
                super.init();
            }
        }.init();
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        HtmlForm form = new HtmlForm();
        form.setId("form");
        facesContext.getViewRoot().getChildren().add(form);

        label = new HtmlOutputLabel();
        label.setId("label");
        label.setFor("field");
        form.getChildren().add(label);

        input = new HtmlInputText();
        input.setId("field");
        form.getChildren().add(input);
    }

    public void testWithDefaults() throws IOException
    {

        label.setValue("label :");

        DataBean bean = new DataBean();

        facesContext.getExternalContext().getRequestMap().put("dataBean", bean);

        createValueExpression(input, "value", "#{dataBean.property}");

        label.encodeAll(facesContext);

        verifyOutput(getPageContents(), "* label :", "form:label", "ExtValRequiredLabel");

    }

    public void testCustomImplementation() throws IOException
    {

        RequiredLabelAddonConfiguration.use(new DefaultRequiredLabelAddonConfiguration()
        {

            @Override
            public RequiredLabelInitializer getRequiredLabelInitializer()
            {
                return new CustomRequiredLabelInitializer();
            }

        }, true);

        label.setValue("label :");

        DataBean bean = new DataBean();

        facesContext.getExternalContext().getRequestMap().put("dataBean", bean);

        createValueExpression(input, "value", "#{dataBean.property}");

        label.encodeAll(facesContext);

        // We can't use verifyOutput here since no css class are created and thus the structure is not correct.
        //verifyOutput(getPageContents(), "required", "form:label", "");
        // We are now satisfied with the label text in the output.
        assertTrue(getPageContents().indexOf(">required</label>") > -1);

    }
}
