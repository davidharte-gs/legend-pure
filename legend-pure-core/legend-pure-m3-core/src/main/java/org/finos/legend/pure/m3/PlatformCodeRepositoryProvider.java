// Copyright 2023 Goldman Sachs
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

package org.finos.legend.pure.m3;

import org.finos.legend.pure.m3.pct.reports.model.Adapter;
import org.finos.legend.pure.m3.pct.shared.model.ReportScope;
import org.finos.legend.pure.m3.serialization.filesystem.repository.CodeRepository;
import org.finos.legend.pure.m3.serialization.filesystem.repository.CodeRepositoryProvider;
import org.finos.legend.pure.m3.serialization.filesystem.repository.GenericCodeRepository;

public class PlatformCodeRepositoryProvider implements CodeRepositoryProvider
{
    public static final Adapter nativeAdapter = new Adapter(
            "Native",
            "",
            "meta::pure::test::pct::testAdapterForInMemoryExecution_Function_1__X_o_"
    );

    public static final ReportScope essentialFunctions = new ReportScope(
            "essential",
            "meta::pure::functions",
            "/platform/pure/essential/"
    );

    public static final ReportScope grammarFunctions = new ReportScope(
            "grammar",
            "meta::pure::functions",
            "/platform/pure/grammar/functions/"
    );


    public static final ReportScope variantFunctions = new ReportScope(
            "variant",
            "meta::pure::functions::variant",
            "/platform/pure/"
    );

    @Override
    public CodeRepository repository()
    {
        return GenericCodeRepository.build("platform.json");
    }
}

