package org.ruben.autobuilder;

import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static org.truth0.Truth.ASSERT;

import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.ruben.autobuilder.util.AssertingProcessor;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourcesSubject.SingleSourceAdapter;

@RunWith(JUnit4.class)
public class TypeHelperTest {

	private static final String TESTCLASS = "test.TestClass";
	
	@Test
	public void testNoMethods() {
		assertWithTestClassBody("")
			.processedWith(new TestClassAssertingProcessor() {
				@Override
				public void runAssertions(List<ExecutableElement> executableElements) {
					Assert.assertTrue(executableElements.isEmpty());
				}
			})
			.compilesWithoutError();
	}
	
	@Test
	public void testOnePublicMethod() {
		assertWithTestClassBody("public abstract String testMethod();")
			.processedWith(new TestClassAssertingProcessor() {
				@Override
				public void runAssertions(List<ExecutableElement> executableElements) {
					Assert.assertTrue(
						containsMethod(executableElements, "testMethod")
					);
				}
			})
			.compilesWithoutError();
	}
	
	@Test
	public void testTwoPublicMethods() {
		assertWithTestClassBody(
				"public abstract String testMethod();", 
				"public abstract String testMethod2();")
			.processedWith(new TestClassAssertingProcessor() {
				@Override
				public void runAssertions(List<ExecutableElement> executableElements) {
					Assert.assertTrue(
						containsMethod(executableElements, "testMethod") && 
						containsMethod(executableElements, "testMethod2") 
					);
				}
			})
			.compilesWithoutError();
	}
	
	@Test
	public void testNoStaticMethods() {
		assertWithTestClassBody(
				"public static String testMethod() { return null; }")
			.processedWith(new TestClassAssertingProcessor() {
				@Override
				public void runAssertions(List<ExecutableElement> executableElements) {
					Assert.assertTrue(
						executableElements.isEmpty()
					);
				}
			})
			.compilesWithoutError();
	}
	
	private boolean containsMethod(List<ExecutableElement> executableElements, final String methodName) {
		return Iterables.tryFind(executableElements, new Predicate<ExecutableElement>() {
			@Override
			public boolean apply(ExecutableElement input) {
				return input.getSimpleName().toString().equals(methodName);
			}
		}).isPresent();
	}

	private SingleSourceAdapter assertWithTestClassBody(String ... body) {
		List<String> lines = Lists.newArrayList("package test;", 
				"",
				"public abstract class TestClass {");
		
		for (String line: body) {
			lines.add(line);
		}
		
		lines.add("}");
		
		return ASSERT.about(javaSource())
			.that(JavaFileObjects.forSourceLines(TESTCLASS, 
				lines.toArray(new String[lines.size()])));
	}
	
	private abstract class TestClassAssertingProcessor extends AssertingProcessor {
		@Override
		public void runAssertions() throws Exception {
			TypeElement testClassElement =
					processingEnv.getElementUtils().getTypeElement(TESTCLASS);
			runAssertions(TypeHelper.getMethods(testClassElement));
		}
		
		public abstract void runAssertions(List<ExecutableElement> executableElements);
	}
	
}
