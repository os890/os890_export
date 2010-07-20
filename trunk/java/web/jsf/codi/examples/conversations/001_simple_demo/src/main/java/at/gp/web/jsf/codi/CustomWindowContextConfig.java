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

import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.spi.RedirectHandler;
import static org.apache.myfaces.extensions.cdi.javaee.jsf.impl.util.ConversationUtils.UUID_ID_KEY;
import org.apache.myfaces.extensions.cdi.javaee.jsf2.impl.scope.conversation.DefaultWindowContextConfig;
import static org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils.tryToLoadClassForName;

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
    public RedirectHandler getRedirectHandler()
    {
        if(this.useFallback)
        {
            return super.getRedirectHandler();
        }

        return new RedirectHandler()
        {
            private static final long serialVersionUID = 1053101351702872549L;

            public void sendRedirect(ExternalContext externalContext, String url, String requestIdKey) throws IOException
            {
                Cookie cookie = new Cookie(UUID_ID_KEY, requestIdKey);
                cookie.setMaxAge(-1);
                cookie.setPath("/");

                HttpServletResponse servletResponse = (HttpServletResponse) externalContext.getResponse();
                servletResponse.addCookie(cookie);

                externalContext.redirect(url);
            }

            public String restoreRequestIdKey(ExternalContext externalContext)
            {
                Cookie result = (Cookie) externalContext.getRequestCookieMap().get(UUID_ID_KEY);

                return result != null ? result.getValue() : null;
            }
        };
    }
}
