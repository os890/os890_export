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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.inject.spi.BeanManager;
import javax.faces.bean.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * @author Gerhard Petracek
 */
@Named
@ApplicationScoped
public class TestAppScoped implements Serializable
{
    @Inject
    private FacesContext facesContext;

    @Inject
    private TestRequestScoped testRequestScoped;

    @Inject
    private TestSessionScoped testSessionScoped;

    @PostConstruct
    protected void init()
    {
        System.out.printf(">>>used: " + getClass().getName());
    }

    public boolean isInjectionOk()
    {
        return this.facesContext.getExternalContext().getApplicationMap() != null;
    }

    @PreDestroy
    protected void deinit()
    {
        System.out.println(">>>unuse: " + getClass().getName());
    }
}