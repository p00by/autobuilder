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
		ASSERT.about(javaSource())
			.that(JavaFileObjects.forResource("good/withoutget/SimpleExample.java"))
			.processedWith(new AutoBuilderProcessor())
			.compilesWithoutError()
			.and().generatesSources(JavaFileObjects.forSourceLines("good.withoutget.SimpleExampleBuilder", 
				"package good.withoutget;",
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
	
	@Test
	public void compilesSimpleExampleWithGet() {
		ASSERT.about(javaSource())
			.that(JavaFileObjects.forResource("good/withget/SimpleExample.java"))
			.processedWith(new AutoBuilderProcessor())
			.compilesWithoutError()
			.and().generatesSources(JavaFileObjects.forSourceLines("good.withget.SimpleExampleBuilder", 
				"package good.withget;",
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
				"		public String getFoo() {",
				"			return foo;",
				"		}",
				"",
				"		public String getBar() {",
				"			return bar;",
				"		}",
				"	}",
				"}"));
	}
	
	@Test
	public void compileSimpleBooleanExample() {
		ASSERT.about(javaSource())
			.that(JavaFileObjects.forSourceLines("good/SimpleExampleBoolean.java",
				"package good;",
				"",
				"import org.ruben.autobuilder.AutoBuild;",
				"",
				"@AutoBuild",
				"abstract class SimpleExampleBoolean { ",
				"	public abstract boolean gleeb();",	
				"}"
			))
			.processedWith(new AutoBuilderProcessor())
			.compilesWithoutError()
			.and().generatesSources(JavaFileObjects.forSourceLines("good.SimpleExampleBooleanBuilder", 
				"package good;",
				"",
				"final class SimpleExampleBooleanBuilder {",
				"",
				"	private boolean gleeb;",
				"",
				"	public SimpleExampleBooleanBuilder asGleeb(boolean gleeb){",
				"		this.gleeb = gleeb;",
				"		return this;",
				"	}",
				"",
				"	public SimpleExampleBoolean build() {",
				"		return new SimpleExampleBoolean_Value(gleeb)",
				"	}",
				"",
				"	private static final class SimpleExampleBoolean_Value extends SimpleExampleBoolean {",
				"",
				"		private final boolean gleeb;",
				"",
				"		public SimpleExampleBoolean_Value(boolean gleeb){ " ,
				"			this.gleeb = gleeb;",
				"		}",
				"",
				"		public boolean gleeb() {",
				"			return gleeb;",
				"		}",
				"	}",
				"}"));
	}
	
}
