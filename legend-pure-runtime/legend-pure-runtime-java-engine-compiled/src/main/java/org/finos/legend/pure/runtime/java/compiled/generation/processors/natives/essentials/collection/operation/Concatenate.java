// Copyright 2020 Goldman Sachs
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

package org.finos.legend.pure.runtime.java.compiled.generation.processors.natives.essentials.collection.operation;

import org.eclipse.collections.api.list.ListIterable;
import org.finos.legend.pure.m3.navigation.M3Properties;
import org.finos.legend.pure.m4.coreinstance.CoreInstance;
import org.finos.legend.pure.runtime.java.compiled.generation.ProcessorContext;
import org.finos.legend.pure.runtime.java.compiled.generation.processors.natives.AbstractNativeFunctionGeneric;
import org.finos.legend.pure.runtime.java.compiled.generation.processors.support.CompiledSupport;
import org.finos.legend.pure.runtime.java.compiled.generation.processors.type.TypeProcessor;

public class Concatenate extends AbstractNativeFunctionGeneric
{
    public Concatenate()
    {
        super(getMethod(CompiledSupport.class, "concatenate"), "concatenate_T_MANY__T_MANY__T_MANY_");
    }

    @Override
    public String build(CoreInstance topLevelElement, CoreInstance functionExpression, ListIterable<String> transformedParams, ProcessorContext processorContext)
    {
        String type = TypeProcessor.typeToJavaObjectWithMul(functionExpression.getValueForMetaPropertyToOne(M3Properties.genericType), functionExpression.getValueForMetaPropertyToOne(M3Properties.multiplicity), processorContext.getSupport());
        String list1 = transformedParams.get(0);
        String list2 = transformedParams.get(1);
        return "((" + type + ")(Object)CompiledSupport.concatenate(" + list1 + ", " + list2 + "))";
    }
}
