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

package org.finos.legend.pure.m3.tests.validation;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.factory.Lists;
import org.finos.legend.pure.m3.navigation.PrimitiveUtilities;
import org.finos.legend.pure.m3.serialization.filesystem.repository.CodeRepository;
import org.finos.legend.pure.m3.serialization.filesystem.repository.CodeRepositoryProviderHelper;
import org.finos.legend.pure.m3.serialization.filesystem.repository.GenericCodeRepository;
import org.finos.legend.pure.m3.serialization.filesystem.usercodestorage.classpath.ClassLoaderCodeStorage;
import org.finos.legend.pure.m3.serialization.filesystem.usercodestorage.composite.CompositeCodeStorage;
import org.finos.legend.pure.m3.tests.AbstractPureTestWithCoreCompiled;
import org.finos.legend.pure.m4.exception.PureCompilationException;
import org.finos.legend.pure.m4.serialization.grammar.antlr.PureParserException;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.regex.Pattern;

public class TestGeneralization extends AbstractPureTestWithCoreCompiled
{
    @BeforeClass
    public static void setUp()
    {
        setUpRuntime(getFunctionExecution(), new CompositeCodeStorage(new ClassLoaderCodeStorage(getCodeRepositories())), getFactoryRegistryOverride(), getOptions(), getExtra());
    }

    protected static RichIterable<? extends CodeRepository> getCodeRepositories()
    {
        return Lists.immutable.with(CodeRepositoryProviderHelper.findPlatformCodeRepository(),
                GenericCodeRepository.build("test", "test(::.*)?", "platform"));
    }

    @After
    public void cleanRuntime()
    {
        runtime.delete("testSource.pure");
        runtime.delete("fromString.pure");
        runtime.delete("/test/testModel.pure");
        runtime.compile();
    }

    @Test
    public void testClassGeneralizationToEnum()
    {
        compileTestSource("fromString.pure", "Enum test::TestEnum {A, B, C}");
        PureCompilationException e = Assert.assertThrows(PureCompilationException.class, () -> compileTestSource("testSource.pure", "Class test::TestClass extends test::TestEnum {}"));
        assertPureException(PureCompilationException.class, "Invalid generalization: test::TestClass cannot extend test::TestEnum as it is not a Class", "testSource.pure", 1, 37, 1, 37, 1, 44, e);
    }

    @Test
    public void testEnumGeneralizationToEnum()
    {
        compileTestSource("fromString.pure", "Enum test::TestEnum {A, B, C}");
        PureParserException e = Assert.assertThrows(PureParserException.class, () -> compileTestSource("testSource.pure", "Enum test::TestEnum2 extends test::TestEnum {}"));
        assertPureException(PureParserException.class, "expected: '{' found: 'extends'", "testSource.pure", 1, 22, 1, 22, 1, 28, e);
    }

    @Test
    public void testClassGeneralizationToPrimitiveType()
    {
        for (String typeName : PrimitiveUtilities.getPrimitiveTypeNames())
        {
            String sourceFile = String.format("test%s.pure", typeName);
            try
            {
                PureCompilationException e = Assert.assertThrows(PureCompilationException.class, () -> compileTestSource(sourceFile, String.format("Class test::TestClass extends %s {}", typeName)));
                String expectedMessage = String.format("Invalid generalization: test::TestClass cannot extend %s as it is not a Class", typeName);
                assertPureException(PureCompilationException.class, expectedMessage, sourceFile, 1, 31, 1, 31, 1, 30 + typeName.length(), e);
            }
            finally
            {
                runtime.delete(sourceFile);
                runtime.compile();
            }
        }
    }

    @Test
    public void testPrimitiveGeneralizationToClass()
    {
        compileTestSource("fromString.pure", "Class test::NotPrimitive {}");
        PureCompilationException e = Assert.assertThrows(PureCompilationException.class, () -> compileTestSource("testSource.pure", "Primitive test::MyPrimitive extends test::NotPrimitive\n"));
        assertPureException(PureCompilationException.class, "Invalid generalization: test::MyPrimitive cannot extend test::NotPrimitive as it is not a PrimitiveType or Any", "testSource.pure", 1, 43, 1, 43, 1, 54, e);
    }

    @Test
    public void testPrimitiveNoGeneralization()
    {
        PureParserException e = Assert.assertThrows(PureParserException.class, () -> compileTestSource("testSource.pure", "Primitive test::MyPrimitive(x:Integer[1])"));
        assertPureException(PureParserException.class, "expected: 'extends' found: '<EOF>'", "testSource.pure", 1, 42, e);
    }

    @Test
    public void testDiamondWithGenericIssue()
    {
        PureCompilationException e = Assert.assertThrows(PureCompilationException.class, () -> compileTestSource(
                "fromString.pure",
                "Class A<T>{prop:T[1];}\n" +
                        "Class B extends A<String>{}\n" +
                        "Class C extends A<Integer>{}\n" +
                        "Class D extends B,C{}\n" +
                        "function simpleTest():D[1]\n" +
                        "{\n" +
                        "   ^D(prop=333);\n" +
                        "}\n"));
        assertPureException(PureCompilationException.class, Pattern.compile("^Diamond inheritance error! (('Integer' is not compatible with 'String')|('String' is not compatible with 'Integer')) going from 'D' to 'A<T>'$"), 7, 5, e);
    }

    @Test
    public void testGeneralizationWithSelfReferenceInGenerics()
    {
        PureCompilationException e = Assert.assertThrows(PureCompilationException.class, () -> compileTestSource(
                "/test/testModel.pure",
                "import test::*;\n" +
                        "Class test::A<T> {}\n" +
                        "Class test::B extends A<B> {}\n"));
        assertPureException(PureCompilationException.class, "test::B extends test::A<test::B> which contains a reference to itself", "/test/testModel.pure", 3, 23, e);
    }

    @Test
    public void testGeneralizationWithNestedSelfReferenceInGenerics()
    {
        PureCompilationException e = Assert.assertThrows(PureCompilationException.class, () -> compileTestSource(
                "/test/testModel.pure",
                "import test::*;\n" +
                        "Class test::A<T> {}\n" +
                        "Class test::B<U> {}\n" +
                        "Class test::C extends A<B<C>> {}\n"));
        assertPureException(PureCompilationException.class, "test::C extends test::A<test::B<test::C>> which contains a reference to itself", "/test/testModel.pure", 4, 23, e);
    }

    @Test
    public void testGeneralizationWithSubtypeReferenceInGenerics()
    {
        PureCompilationException e = Assert.assertThrows(PureCompilationException.class, () -> compileTestSource(
                "/test/testModel.pure",
                "import test::*;\n" +
                        "Class test::A<T> {}\n" +
                        "Class test::B extends A<C> {}\n" +
                        "Class test::C extends B {}\n"));
        assertPureException(PureCompilationException.class, "test::B extends test::A<test::C> which contains a reference to test::C which is a subtype of test::B", "/test/testModel.pure", 3, 23, e);
    }

    @Test
    public void testGeneralizationWithNestedSubtypeReferenceInGenerics()
    {
        PureCompilationException e = Assert.assertThrows(PureCompilationException.class, () -> compileTestSource(
                "/test/testModel.pure",
                "import test::*;\n" +
                        "Class test::A<T> {}\n" +
                        "Class test::B<U> {}\n" +
                        "Class test::C extends A<B<D>> {}\n" +
                        "Class test::D extends C {}\n"));
        assertPureException(PureCompilationException.class, "test::C extends test::A<test::B<test::D>> which contains a reference to test::D which is a subtype of test::C", "/test/testModel.pure", 4, 23, e);
    }
}
