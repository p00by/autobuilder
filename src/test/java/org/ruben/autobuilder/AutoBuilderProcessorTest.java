package org.ruben.autobuilder;

import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static org.truth0.Truth.ASSERT;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.testing.compile.JavaFileObjects;

@RunWith(JUnit4.class)
public class AutoBuilderProcessorTest {

	@Test
	public void compilesSomething() {
		ASSERT.about(javaSource())
			.that(JavaFileObjects.forResource("good/HelloWorld.java"))
			.processedWith(new AutoBuilderProcessor())
			.compilesWithoutError()
			.and().generatesSources(JavaFileObjects.forSourceString("good.HelloWorldBuilder", 
				"package good;\n" +
				"\n" +
				"final class HelloWorldBuilder {}"));
	}
	
	@Test
	public void compilesSimpleExampleWithoutGet() {
		testSimpleExample("withoutget");
	}
	
	@Test
	public void compilesSimpleExampleWithGet() {
		testSimpleExample("withget");
	}

	private void testSimpleExample(String packageName) {
		ASSERT.about(javaSource())
			.that(JavaFileObjects.forResource("good/" + packageName + "/SimpleExample.java"))
			.processedWith(new AutoBuilderProcessor())
			.compilesWithoutError()
			.and().generatesSources(JavaFileObjects.forSourceLines("good." + packageName + ".SimpleExampleBuilder", 
				"package good. " + packageName + ";",
				"",
				"final class SimpleExampleBuilder {",
				"",
				"	private String foo;",
				"	private String bar;",
				"",
				"	public SimpleExampleBuilder withFoo(String foo){",
				"		this.foo = foo;",
				"		return this;",
				"	}",
				"",
				"	public SimpleExampleBuilder withBar(String bar){",
				"		this.bar = bar;",
				"		 return this;",
				"	}",
				"",
				"}"));
	}
	
}
