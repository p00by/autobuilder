package org.ruben.autobuilder;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

import com.google.auto.service.AutoService;
import com.google.common.collect.Sets;

@AutoService(Processor.class)
public class AutoBuilderProcessor extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		try {
			Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(AutoBuild.class);
			for (Element element: elements) {
				
				String builderName = element.getSimpleName() + "Builder";
				JavaFileObject sourceFile = processingEnv.getFiler()
						.createSourceFile("test." + builderName);
				try (Writer writer = sourceFile.openWriter()) {
					writer.write("final class " + builderName + " {}");
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return Sets.newHashSet(AutoBuild.class.getName());
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}

}
