// Copyright 2024 Goldman Sachs
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.finos.legend.pure.m3.pct.functions.model;

import org.eclipse.collections.api.factory.Lists;

import java.util.List;

public class FunctionDefinition
{
    public String _package;
    public String name;
    public String sourceId;

    public List<Signature> signatures = Lists.mutable.empty();
    public int testCount;
    public int pctTestCount;

    public FunctionDefinition()
    {
    }

    public FunctionDefinition(String sourceId)
    {
        this.sourceId = sourceId;
    }
}
