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
			.and().generatesSources(JavaFileObjects.forSourceLines("good.HelloWorldBuilder", 
				"package good;",
				"",
				"final class HelloWorldBuilder {", 
				"	public HelloWorld build() {",
				"		return new HelloWorld_Value()",		
				"	}",
				"",
				"	private static final class HelloWorld_Value extends HelloWorld {",
				"		public HelloWorld_Value() {}",
				"	}",
				"}"));
	}
	
	@Test
	public void compilesSimpleExampleWithoutGet() {
		testSimpleExample("withoutget");
	}
//TODO renable this
//	@Test
//	public void compilesSimpleExampleWithGet() {
//		testSimpleExample("withget");
//	}

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
				"	public SimpleExample build() {",
				"		return new SimpleExample_Value(foo, bar)",
				"	}",
				"",
				"	private static final class SimpleExample_Value extends SimpleExample {",
				"",
				"		private final String foo;",
				"		private final String bar;",	
				"",
				"		public SimpleExample_Value(String foo, String bar){ " ,
				"			this.foo = foo;",
				"			this.bar = bar;",
				"		}",
				"",
				"		public String foo() {",
				"			return foo;",
				"		}",
				"",
				"		public String bar() {",
				"			return bar;",
				"		}",
				"	}",
				"}"));
	}
	
}
