package org.ruben.autobuilder;

import java.io.IOException;
import java.io.Writer;
import java.util.EnumSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

import com.google.auto.service.AutoService;
import com.google.common.collect.Sets;
import com.squareup.javawriter.JavaWriter;

@AutoService(Processor.class)
public class AutoBuilderProcessor extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		try {
			Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(AutoBuild.class);
			for (Element element: elements) {
				processElement(element);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	private void processElement(Element element) throws IOException {
		String packageName = TypeHelper.getPackageName(element);
		
		String builderName = element.getSimpleName() + "Builder";
		JavaFileObject sourceFile = processingEnv.getFiler()
				.createSourceFile(packageName + "." + builderName);
		try (Writer writer = sourceFile.openWriter();
			JavaWriter javaWriter = new JavaWriter(writer)) {
			
			javaWriter.emitPackage(packageName)
				.emitEmptyLine()
				.beginType(builderName, "class", EnumSet.of(Modifier.FINAL))
				.endType();
		}
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
