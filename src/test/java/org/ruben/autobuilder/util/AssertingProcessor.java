package org.ruben.autobuilder.util;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;

import com.google.common.collect.ImmutableSet;

//Based on:
//
//https://github.com/google/auto/blob/master/common/src/test/java/com/google/auto/common/SuperficialValidationTest.java
public abstract class AssertingProcessor extends AbstractProcessor {
	
	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return ImmutableSet.of("*");
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		try {
			runAssertions();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return false;
	}

	public abstract void runAssertions() throws Exception;
}