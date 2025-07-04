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

package org.finos.legend.pure.m4.coreinstance;

import org.eclipse.collections.api.list.ListIterable;
import org.finos.legend.pure.m4.coreinstance.compileState.CompileState;
import org.finos.legend.pure.m4.coreinstance.indexing.IndexSpecifications;
import org.finos.legend.pure.m4.transaction.ModelRepositoryTransaction;

public abstract class AbstractCoreInstance implements CoreInstance
{
    @Override
    public CoreInstance getValueForMetaPropertyToOne(CoreInstance property)
    {
        return getValueForMetaPropertyToOne(property.getName());
    }

    @Override
    public ListIterable<? extends CoreInstance> getValueForMetaPropertyToMany(CoreInstance key)
    {
        return getValueForMetaPropertyToMany(key.getName());
    }

    @Override
    public CoreInstance getValueInValueForMetaPropertyToMany(String keyName, String keyInMany)
    {
        return getValueInValueForMetaPropertyToManyByIDIndex(keyName, IndexSpecifications.getCoreInstanceNameIndexSpec(), keyInMany);
    }

    @Override
    public CoreInstance getValueInValueForMetaPropertyToManyWithKey(String keyName, String key, String keyInMany)
    {
        return getValueInValueForMetaPropertyToManyByIDIndex(keyName, IndexSpecifications.getPropertyValueNameIndexSpec(key), keyInMany);
    }

    @Override
    public void markProcessed()
    {
        addCompileState(CompileState.PROCESSED);
    }

    @Override
    public void markNotProcessed()
    {
        markNotValidated();
        removeCompileState(CompileState.PROCESSED);
    }

    @Override
    public boolean hasBeenProcessed()
    {
        return hasCompileState(CompileState.PROCESSED);
    }

    @Override
    public void markValidated()
    {
        markProcessed();
        addCompileState(CompileState.VALIDATED);
    }

    @Override
    public void markNotValidated()
    {
        removeCompileState(CompileState.VALIDATED);
    }

    @Override
    public boolean hasBeenValidated()
    {
        return hasCompileState(CompileState.VALIDATED);
    }

    @Override
    public void printFull(Appendable appendable, String tab)
    {
        // by default, just call print
        print(appendable, tab);
    }

    @Override
    public void print(Appendable appendable, String tab)
    {
        print(appendable, tab, DEFAULT_MAX_PRINT_DEPTH);
    }

    @Override
    public void printWithoutDebug(Appendable appendable, String tab)
    {
        printWithoutDebug(appendable, tab, DEFAULT_MAX_PRINT_DEPTH);
    }

    @Override
    public void printWithoutDebug(Appendable appendable, String tab, int max)
    {
        // by default, just call print
        print(appendable, tab, max);
    }

    @Override
    public String printFull(String tab)
    {
        StringBuilder builder = new StringBuilder();
        printFull(builder, tab);
        return builder.toString();
    }

    @Override
    public String print(String tab)
    {
        StringBuilder builder = new StringBuilder();
        print(builder, tab);
        return builder.toString();
    }

    @Override
    public String print(String tab, int max)
    {
        StringBuilder builder = new StringBuilder();
        print(builder, tab, max);
        return builder.toString();
    }

    @Override
    public String printWithoutDebug(String tab)
    {
        StringBuilder builder = new StringBuilder();
        printWithoutDebug(builder, tab);
        return builder.toString();
    }

    @Override
    public String printWithoutDebug(String tab, int max)
    {
        StringBuilder builder = new StringBuilder();
        printWithoutDebug(builder, tab, max);
        return builder.toString();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj instanceof AbstractCoreInstanceWrapper)
        {
            return obj.equals(this);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }

    @Override
    public void removeProperty(CoreInstance propertyNameKey)
    {
        removeProperty(propertyNameKey.getName());
    }

    @Override
    public void rollback(ModelRepositoryTransaction transaction)
    {
        // By default, rolling back is simply not committing
    }

    public abstract CoreInstance copy();
}
