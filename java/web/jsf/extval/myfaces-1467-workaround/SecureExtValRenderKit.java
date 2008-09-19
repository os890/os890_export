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
package at.gp.web.jsf.extval.validation.secure;

import org.apache.myfaces.extensions.validator.core.ExtValRenderKit;

import javax.faces.render.RenderKit;
import javax.faces.render.Renderer;

/**
 * @author Gerhard Petracek
 */
public class SecureExtValRenderKit extends ExtValRenderKit
{
    public static final String ID = "SECURE_" + ExtValRenderKit.ID;

    public SecureExtValRenderKit(RenderKit wrapped)
    {
        super(wrapped);
    }

    @Override
    protected Renderer createWrapper(Renderer renderer)
    {
        return new SecureExtValRendererWrapper(renderer);
    }
}
