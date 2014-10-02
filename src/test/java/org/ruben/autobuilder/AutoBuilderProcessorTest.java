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
			.and().generatesSources(JavaFileObjects.forSourceString("test.HelloWorldBuilder", "final class HelloWorldBuilder {}"));
	}
	
}
