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

package org.finos.legend.pure.m3.pct.aggregate.model;

import org.eclipse.collections.api.factory.Maps;
import org.finos.legend.pure.m3.pct.functions.model.FunctionDefinition;
import org.finos.legend.pure.m3.pct.reports.model.AdapterKey;
import org.finos.legend.pure.m3.pct.reports.model.FunctionTestResults;
import org.finos.legend.pure.m3.pct.shared.model.ReportScope;

import java.util.Map;

public class FunctionDocumentation
{
    public ReportScope reportScope;

    public FunctionDefinition functionDefinition;
    public Map<AdapterKey, FunctionTestResults> functionTestResults = Maps.mutable.empty();
}
