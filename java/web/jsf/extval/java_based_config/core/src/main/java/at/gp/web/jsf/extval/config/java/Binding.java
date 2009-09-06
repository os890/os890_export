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
package at.gp.web.jsf.extval.config.java;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Gerhard Petracek
 */
public class Binding
{
    private ConfigEntry from;
    private List<ConfigEntry> to = new ArrayList<ConfigEntry>();
    private Command callback;

    public Binding(ConfigEntry from)
    {
        this.from = from;
    }

    public void to(Class... targets)
    {
        for(Class target : targets)
        {
            this.to.add(new ConfigEntry(target));
        }
        callback.execute();
    }

    public ConfigEntry getFrom()
    {
        return from;
    }

    public ConfigEntry[] getTo()
    {
        return to.toArray(new ConfigEntry[to.size()]);
    }

    public void setCallback(Command callback)
    {
        this.callback = callback;
    }
}
