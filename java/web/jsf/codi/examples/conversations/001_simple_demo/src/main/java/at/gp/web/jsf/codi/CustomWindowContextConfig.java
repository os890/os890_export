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
package at.gp.web.jsf.codi;

import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.DefaultWindowHandler;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.spi.WindowHandler;
import org.apache.myfaces.extensions.cdi.javaee.jsf2.impl.scope.conversation.DefaultWindowContextConfig;
import static org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils.tryToLoadClassForName;
import static org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager.WINDOW_CONTEXT_ID_PARAMETER_KEY;

import javax.faces.context.ExternalContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import java.io.IOException;

/**
 * optional config for using cookies instead of the flash scope or an url parameter
 *
 * @author Gerhard Petracek
 */
public class CustomWindowContextConfig extends DefaultWindowContextConfig
{
    private static final long serialVersionUID = 5477652111618967278L;

    private final boolean useFallback;

    public CustomWindowContextConfig()
    {
        this.useFallback = tryToLoadClassForName("org.apache.myfaces.context.FacesContextFactoryImpl") == null;
    }

    @Override
    public boolean isUrlParameterSupported()
    {
        return false;
    }

    @Override
    public WindowHandler getWindowHandler()
    {
        if(this.useFallback)
        {
            return super.getWindowHandler();
        }

        return new DefaultWindowHandler(isUrlParameterSupported())
        {
            private static final long serialVersionUID = 1053101351702872549L;

            @Override
            public void sendRedirect(ExternalContext externalContext, String url) throws IOException
            {
                createCookie(externalContext, getCurrentWindowId());

                externalContext.redirect(url);
            }

            @Override
            public String restoreWindowId(ExternalContext externalContext)
            {
                Cookie cookie = (Cookie) externalContext.getRequestCookieMap().get(WINDOW_CONTEXT_ID_PARAMETER_KEY);

                String windowId = null;

                if(cookie != null)
                {
                    windowId = cookie.getValue();

                    resetCookie(externalContext, cookie);
                }

                return windowId;
            }
        };
    }

    private void createCookie(ExternalContext externalContext, String windowId)
    {
        Cookie cookie = new Cookie(WINDOW_CONTEXT_ID_PARAMETER_KEY, windowId);
        cookie.setMaxAge(-1);
        cookie.setPath("/");

        HttpServletResponse servletResponse = (HttpServletResponse) externalContext.getResponse();
        servletResponse.addCookie(cookie);
    }

    private void resetCookie(ExternalContext externalContext, Cookie cookie)
    {
        cookie.setMaxAge(0);
        cookie.setValue(null);
        cookie.setPath("/");

        HttpServletResponse servletResponse = (HttpServletResponse) externalContext.getResponse();
        servletResponse.addCookie(cookie);
    }
}
