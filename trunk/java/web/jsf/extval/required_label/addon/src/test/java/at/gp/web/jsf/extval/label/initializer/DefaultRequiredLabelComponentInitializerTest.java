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
package at.gp.web.jsf.extval.label.initializer;

import java.io.IOException;

import javax.faces.component.html.HtmlOutputLabel;

import at.gp.web.jsf.extval.label.AbstractTest;
import at.gp.web.jsf.extval.label.DefaultRequiredLabelAddonConfiguration;
import at.gp.web.jsf.extval.label.RequiredLabelAddonConfiguration;
import at.gp.web.jsf.extval.label.initializer.model.Bean;

public class DefaultRequiredLabelComponentInitializerTest extends AbstractTest
{
    private HtmlOutputLabel labelComponent;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        labelComponent = new HtmlOutputLabel();
        labelComponent.setId("label");

    }

    public DefaultRequiredLabelComponentInitializerTest(String name)
    {
        super(name);
    }

    @Override
    protected void invokeStartupListeners()
    {
        // Make sure we have for each test the default one. Test by test the config can be overriden.
        RequiredLabelAddonConfiguration.use(new DefaultRequiredLabelAddonConfiguration(), true);
    }

    private void setLabelValue()
    {
        labelComponent.setValue("label :");
    }

    private void setLabelExpression(boolean withColon)
    {
        Bean bean = new Bean();
        if (withColon)
        {
            bean.setLabel("Label :");
        }
        else
        {
            bean.setLabel("Label");
        }
        facesContext.getExternalContext().getRequestMap().put("bean", bean);

        createValueExpression(labelComponent, "value", "#{bean.label} :");
    }

    public void testDefaults() throws IOException
    {
        RequiredLabelInitializer labelInitializer = new DefaultRequiredLabelInitializer();

        setLabelValue();

        labelInitializer.configureLabel(facesContext, labelComponent);
        labelComponent.encodeAll(facesContext);

        verifyOutput(getPageContents(), "* label :", "label",
                "ExtValRequiredLabel");
    }

    public void testDefaultsExistingMarker() throws IOException
    {
        RequiredLabelInitializer labelInitializer = new DefaultRequiredLabelInitializer();

        labelComponent.setValue("* label :");

        labelInitializer.configureLabel(facesContext, labelComponent);
        labelComponent.encodeAll(facesContext);

        verifyOutput(getPageContents(), "* label :", "label",
                "ExtValRequiredLabel");
    }

    public void testDefaultsWithCssValue() throws IOException
    {
        RequiredLabelInitializer labelInitializer = new DefaultRequiredLabelInitializer();

        setLabelValue();

        labelComponent.setStyleClass("test");
        labelInitializer.configureLabel(facesContext, labelComponent);
        labelComponent.encodeAll(facesContext);

        verifyOutput(getPageContents(), "* label :", "label",
                "ExtValRequiredLabel", "test");

    }

    public void testDefaultsWithExistingCssValue() throws IOException
    {
        RequiredLabelInitializer labelInitializer = new DefaultRequiredLabelInitializer();

        setLabelValue();

        labelComponent.setStyleClass("ExtValRequiredLabel test");
        labelInitializer.configureLabel(facesContext, labelComponent);
        labelComponent.encodeAll(facesContext);

        verifyOutput(getPageContents(), "* label :", "label",
                "ExtValRequiredLabel", "test");

    }

    public void testDefaultsWithCssExpression() throws IOException
    {
        RequiredLabelInitializer labelInitializer = new DefaultRequiredLabelInitializer();

        setLabelValue();

        Bean bean = new Bean();
        bean.setStyle("exprStyle");
        facesContext.getExternalContext().getRequestMap().put("testBean", bean);

        createValueExpression(labelComponent, "styleClass", "#{testBean.style}");

        labelInitializer.configureLabel(facesContext, labelComponent);
        labelComponent.encodeAll(facesContext);

        verifyOutput(getPageContents(), "* label :", "label",
                "ExtValRequiredLabel", "exprStyle");

    }

    public void testPositionAfter() throws IOException
    {
        useDefaultConfigWithAfterPlaceMarker();

        RequiredLabelInitializer labelInitializer = new DefaultRequiredLabelInitializer();

        setLabelValue();

        labelInitializer.configureLabel(facesContext, labelComponent);
        labelComponent.encodeAll(facesContext);

        verifyOutput(getPageContents(), "label :* ", "label",
                "ExtValRequiredLabel");
    }

    public void testPositionAfterExistingMarker() throws IOException
    {
        useDefaultConfigWithAfterPlaceMarker();

        RequiredLabelInitializer labelInitializer = new DefaultRequiredLabelInitializer();

        labelComponent.setValue("label :* ");

        labelInitializer.configureLabel(facesContext, labelComponent);
        labelComponent.encodeAll(facesContext);

        verifyOutput(getPageContents(), "label :* ", "label",
                "ExtValRequiredLabel");
    }

    private void useDefaultConfigWithAfterPlaceMarker()
    {
        RequiredLabelAddonConfiguration.use(
                new DefaultRequiredLabelAddonConfiguration()
                {
                    @Override
                    public String getPlaceMarker()
                    {
                        return "AFTER";
                    }
                }, true);
    }

    public void testPositionBeforeColon() throws IOException
    {
        useDefaultConfigWithBeforeColonPlaceMarker();
        RequiredLabelInitializer labelInitializer = new DefaultRequiredLabelInitializer();

        setLabelValue();

        labelInitializer.configureLabel(facesContext, labelComponent);
        labelComponent.encodeAll(facesContext);

        verifyOutput(getPageContents(), "label * :", "label",
                "ExtValRequiredLabel");
    }

    public void testPositionBeforeColonExistingMarker() throws IOException
    {
        useDefaultConfigWithBeforeColonPlaceMarker();
        RequiredLabelInitializer labelInitializer = new DefaultRequiredLabelInitializer();

        labelComponent.setValue("label * :");

        labelInitializer.configureLabel(facesContext, labelComponent);
        labelComponent.encodeAll(facesContext);

        verifyOutput(getPageContents(), "label * :", "label",
                "ExtValRequiredLabel");
    }

    private void useDefaultConfigWithBeforeColonPlaceMarker()
    {
        RequiredLabelAddonConfiguration.use(
                new DefaultRequiredLabelAddonConfiguration()
                {
                    @Override
                    public String getPlaceMarker()
                    {
                        return "BEFORE_COLON";
                    }
                }, true);
    }

    public void testDefineMarker() throws IOException
    {
        RequiredLabelAddonConfiguration.use(
                new DefaultRequiredLabelAddonConfiguration()
                {
                    @Override
                    public String getRequiredMarker()
                    {
                        return "X ";
                    }
                }, true);

        RequiredLabelInitializer labelInitializer = new DefaultRequiredLabelInitializer();

        setLabelValue();

        labelInitializer.configureLabel(facesContext, labelComponent);
        labelComponent.encodeAll(facesContext);

        verifyOutput(getPageContents(), "X label :", "label",
                "ExtValRequiredLabel");
    }

    public void testNoMarker() throws IOException
    {
        RequiredLabelAddonConfiguration.use(
                new DefaultRequiredLabelAddonConfiguration()
                {
                    @Override
                    public String getRequiredMarker()
                    {
                        return "";
                    }
                }, true);

        RequiredLabelInitializer labelInitializer = new DefaultRequiredLabelInitializer();

        setLabelValue();

        labelInitializer.configureLabel(facesContext, labelComponent);
        labelComponent.encodeAll(facesContext);

        verifyOutput(getPageContents(), "label :", "label",
                "ExtValRequiredLabel");
    }

    public void testDefineMarkerWithHtml() throws IOException
    {
        RequiredLabelAddonConfiguration.use(
                new DefaultRequiredLabelAddonConfiguration()
                {
                    @Override
                    public String getRequiredMarker()
                    {
                        return "<span class=\"required\">x</span> ";
                    }
                }, true);

        RequiredLabelInitializer labelInitializer = new DefaultRequiredLabelInitializer();

        setLabelValue();

        labelInitializer.configureLabel(facesContext, labelComponent);
        labelComponent.encodeAll(facesContext);

        verifyOutput(getPageContents(),
                "<span class=\"required\">x</span> label :", "label",
                "ExtValRequiredLabel");
    }

    public void testDefineCss() throws IOException
    {
        RequiredLabelAddonConfiguration.use(
                new DefaultRequiredLabelAddonConfiguration()
                {
                    @Override
                    public String getRequiredStyleClass()
                    {
                        return "CustomCssClass";
                    }
                }, true);

        RequiredLabelInitializer labelInitializer = new DefaultRequiredLabelInitializer();

        setLabelValue();

        labelInitializer.configureLabel(facesContext, labelComponent);
        labelComponent.encodeAll(facesContext);

        verifyOutput(getPageContents(), "* label :", "label", "CustomCssClass");
    }

    public void testDefaultsWithExpression() throws IOException
    {
        RequiredLabelInitializer labelInitializer = new DefaultRequiredLabelInitializer();

        setLabelExpression(false);

        labelInitializer.configureLabel(facesContext, labelComponent);
        labelComponent.encodeAll(facesContext);

        verifyOutput(getPageContents(), "* Label :", "label",
                "ExtValRequiredLabel");
    }

    public void testDefaultsWithExpressionExistingMarker() throws IOException
    {
        RequiredLabelInitializer labelInitializer = new DefaultRequiredLabelInitializer();

        setLabelExpression(false);
        createValueExpression(labelComponent, "value", "* #{bean.label} :");

        labelInitializer.configureLabel(facesContext, labelComponent);
        labelComponent.encodeAll(facesContext);

        verifyOutput(getPageContents(), "* Label :", "label",
                "ExtValRequiredLabel");
    }

    public void testPositionAfterWithExpression() throws IOException
    {
        useDefaultConfigWithAfterPlaceMarker();

        RequiredLabelInitializer labelInitializer = new DefaultRequiredLabelInitializer();

        setLabelExpression(false);

        labelInitializer.configureLabel(facesContext, labelComponent);
        labelComponent.encodeAll(facesContext);

        verifyOutput(getPageContents(), "Label :* ", "label",
                "ExtValRequiredLabel");
    }

    public void testPositionAfterWithExpressionExistingMarker()
            throws IOException
    {
        useDefaultConfigWithAfterPlaceMarker();

        RequiredLabelInitializer labelInitializer = new DefaultRequiredLabelInitializer();

        setLabelExpression(false);
        createValueExpression(labelComponent, "value", "#{bean.label} :* ");

        labelInitializer.configureLabel(facesContext, labelComponent);
        labelComponent.encodeAll(facesContext);

        verifyOutput(getPageContents(), "Label :* ", "label",
                "ExtValRequiredLabel");
    }

    public void testPositionBeforeColonWithExpression() throws IOException
    {
        useDefaultConfigWithBeforeColonPlaceMarker();

        RequiredLabelInitializer labelInitializer = new DefaultRequiredLabelInitializer();

        setLabelExpression(false);

        labelInitializer.configureLabel(facesContext, labelComponent);
        labelComponent.encodeAll(facesContext);

        verifyOutput(getPageContents(), "Label * :", "label",
                "ExtValRequiredLabel");
    }

    public void testPositionBeforeColonWithExpressionExistingMarker()
            throws IOException
    {
        useDefaultConfigWithBeforeColonPlaceMarker();

        RequiredLabelInitializer labelInitializer = new DefaultRequiredLabelInitializer();

        setLabelExpression(false);
        createValueExpression(labelComponent, "value", "#{bean.label} * :");

        labelInitializer.configureLabel(facesContext, labelComponent);
        labelComponent.encodeAll(facesContext);

        verifyOutput(getPageContents(), "Label * :", "label",
                "ExtValRequiredLabel");
    }

    public void testPositionBeforeColonWithExpressionNoColon()
            throws IOException
    {
        useDefaultConfigWithBeforeColonPlaceMarker();

        RequiredLabelInitializer labelInitializer = new DefaultRequiredLabelInitializer();

        setLabelExpression(true);
        createValueExpression(labelComponent, "value", "#{bean.label}");

        labelInitializer.configureLabel(facesContext, labelComponent);
        labelComponent.encodeAll(facesContext);

        // We can't found the colon in the label (it is in the EL) so we put it after the text
        verifyOutput(getPageContents(), "Label :* ", "label",
                "ExtValRequiredLabel");
    }

    
}
