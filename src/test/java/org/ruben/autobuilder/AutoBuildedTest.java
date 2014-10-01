package org.ruben.autobuilder;

import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static org.truth0.Truth.ASSERT;

import javax.tools.JavaFileObject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.testing.compile.JavaFileObjects;



@RunWith(JUnit4.class)
public class AutoBuildedTest {

	@Test
	public void compilesSomething() {
		ASSERT.about(javaSource())
			.that(JavaFileObjects.forSourceLines("test.HelloWorld", 
					"package test;",
					"",
					"import org.ruben.autobuilder.AutoBuilded;",
					"",
					"@AutoBuilded",
					"public class HelloWorld {",
					"}"))
			.processedWith(new AutoBuilderProcessor())
			.compilesWithoutError()
			.and().generatesSources(JavaFileObjects.forSourceString("test.HelloWorldBuilder", "final class HelloWorldBuilder {}"));
		
	}
	
}
