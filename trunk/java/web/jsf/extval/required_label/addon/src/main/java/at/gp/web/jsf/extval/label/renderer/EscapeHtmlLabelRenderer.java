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
package at.gp.web.jsf.extval.label.renderer;

import org.apache.myfaces.renderkit.html.HtmlLabelRenderer;
import org.apache.myfaces.shared_impl.renderkit.JSFAttr;
import org.apache.myfaces.shared_impl.renderkit.RendererUtils;
import org.apache.myfaces.shared_impl.renderkit.html.HTML;
import org.apache.myfaces.shared_impl.renderkit.html.HtmlRendererUtils;

import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * A subclass of HtmlLabelRenderer that correctly uses the value of the escape property.  See also MYFACES-2751.
 * When using Myfaces 1.2.9 or lower, define this Renderer in the facesConfig file if you wan't to use a custom marker that contains HTML or XML.
 *
 * @author Rudy De Busscher
 */
@Deprecated
public class EscapeHtmlLabelRenderer extends HtmlLabelRenderer
{
    /**
     * The Constant log.
     */
    protected final Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent) throws IOException
    {
        if (facesContext == null)
        {
            throw new NullPointerException("context");
        }
        if (uiComponent == null)
        {
            throw new NullPointerException("component");
        }

        ResponseWriter writer = facesContext.getResponseWriter();

        encodeBefore(facesContext, writer, uiComponent);

        writer.startElement(HTML.LABEL_ELEM, uiComponent);
        HtmlRendererUtils.writeIdIfNecessary(writer, uiComponent, facesContext);
        HtmlRendererUtils.renderHTMLAttributes(writer, uiComponent, HTML.LABEL_PASSTHROUGH_ATTRIBUTES);

        String forAttr = getFor(uiComponent);

        if (forAttr != null)
        {
            writer.writeAttribute(HTML.FOR_ATTR, getClientId(facesContext, uiComponent, forAttr), JSFAttr.FOR_ATTR);
        }
        else
        {
            logger.warning("Attribute 'for' of label component with id " + uiComponent.getClientId(facesContext) + " is not defined");
        }

        if (uiComponent instanceof ValueHolder)
        {
            String text = RendererUtils.getStringValue(facesContext, uiComponent);
            boolean isEscape = RendererUtils.getBooleanAttribute(uiComponent, JSFAttr.ESCAPE_ATTR, true);

            if (text != null)
            {
                if (isEscape)
                {
                    writer.writeText(text, "value");
                }
                else
                {
                    writer.write(text);
                }
            }
        }

        writer.flush(); // close start tag

        encodeAfterStart(facesContext, writer, uiComponent);
    }
}
