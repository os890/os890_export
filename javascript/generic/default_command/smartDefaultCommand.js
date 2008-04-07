/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

/*
 * author  Gerhard Petracek
 * version 0.0.3
 */

function initDefaultCommand()
{
    if (document.addEventListener)
    {
        window.addEventListener("keypress", DefaultCommandUtils.smartSubmit, false);
    }
    else
    {
        document.attachEvent("onkeypress", DefaultCommandUtils.smartSubmit);
    }
}

function DefaultCommandUtils()
{
}

DefaultCommandUtils.smartSubmit = function(event)
{
    var target = (event.target) ? event.target : event.srcElement;

    if (event.keyCode == 13 && target.type != 'textarea')
    {
        var rootNode = DefaultCommandUtils._resolveRootNode(target);
        var defaultCommandId = DefaultCommandUtils._resolveFullId(rootNode, "_defaultCommand", false);
        if (defaultCommandId != null)
        {
            document.getElementById(defaultCommandId).click();
        }
    }
}

DefaultCommandUtils._resolveRootNode = function(node)
{
    if(DefaultCommandUtils._endsWith(node.id, "defaultCommandGroup"))
    {
        return node;
    }

    if(node.tagName == "HTML")
    {
        return document.body;
    }

    if(node.tagName == "FORM")
    {
        return node;
    }

    return DefaultCommandUtils._resolveRootNode(node.parentNode);
}

DefaultCommandUtils._resolveFullId = function(node, endOfId, isRecursiveCall)
{
    if (DefaultCommandUtils._endsWith(node.id, endOfId))
    {
        return node.id;
    }

    if (isRecursiveCall && DefaultCommandUtils._endsWith(node.id, "defaultCommandGroup"))
    {
        return null;
    }

    var foundNodeId;
    for (var i = 0; i < node.childNodes.length; i++)
    {
        foundNodeId = DefaultCommandUtils._resolveFullId(node.childNodes[i], endOfId, true);
        if (foundNodeId)
        {
            return foundNodeId;
        }
    }

    return null;
}

DefaultCommandUtils._endsWith = function(fullString, endOfString)
{
    return (fullString && fullString.substring(fullString.length - endOfString.length, fullString.length) == endOfString);
}
