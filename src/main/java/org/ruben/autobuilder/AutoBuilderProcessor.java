package org.ruben.autobuilder;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.PrimitiveType;
import javax.tools.JavaFileObject;

import org.ruben.autobuilder.util.StringUtil;

import com.google.auto.service.AutoService;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
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
				.createSourceFile(packageName + "." + builderName, element);
		try (Writer writer = sourceFile.openWriter();
			JavaWriter javaWriter = new JavaWriter(writer)) {
			
			javaWriter.emitPackage(packageName)
				.emitEmptyLine()
				.beginType(builderName, "class", EnumSet.of(Modifier.FINAL));
			
			Iterable<ExecutableElement> methods = collectMethods(element);
			
			createProperties(methods, javaWriter, builderName);
			createFluentMethods(methods, javaWriter, builderName);
			createBuildMethod(methods, javaWriter, element.getSimpleName().toString());
			createValueType(methods, javaWriter, element.getSimpleName().toString());
			
			javaWriter.endType();
		}
	}

	private void createProperties(Iterable<ExecutableElement> methods, JavaWriter javaWriter, String builderName) throws IOException {
		for (ExecutableElement method: methods) {
			String propertyName = guessPropertyName(method);
		
			javaWriter.emitField(
				method.getReturnType().toString(), 
				propertyName,
				EnumSet.of(Modifier.PRIVATE));
		}	
	}

	private void createFluentMethods(Iterable<ExecutableElement> methods, JavaWriter javaWriter, String builderName) throws IOException {
		for (ExecutableElement method: methods) {
			String propertyName = guessPropertyName(method);
			String verb = hasBooleanReturnType(method) ? "as" : "with";
			
			javaWriter.beginMethod(builderName, 
				verb + StringUtil.upperCaseFirst(propertyName), 
				EnumSet.of(Modifier.PUBLIC), 
				method.getReturnType().toString(),
				propertyName);

			javaWriter.emitStatement("this.%s = %s", propertyName, propertyName);
			javaWriter.emitStatement("return this");
			
			javaWriter.endMethod();
		}
	}
	
	private boolean hasBooleanReturnType(ExecutableElement executableElement) {
		return executableElement.getReturnType() instanceof PrimitiveType && "boolean".equals(executableElement.getReturnType().toString());
	}

	private void createBuildMethod(Iterable<ExecutableElement> methods, JavaWriter javaWriter, String valueType) throws IOException {
		javaWriter.beginMethod(valueType, "build", EnumSet.of(Modifier.PUBLIC));
		
		List<String> constructorParams = Lists.newArrayList();
		for (ExecutableElement method: methods) {
			String propertyName = guessPropertyName(method);
			constructorParams.add(propertyName);
		}
		
		javaWriter.emitStatement("return new %s_Value(" + Joiner.on(", ").join(constructorParams) + ")", valueType);
		
		javaWriter.endMethod();
	}


	private void createValueType(Iterable<ExecutableElement> methods,
			JavaWriter javaWriter, String valueType) throws IOException {
		javaWriter.beginType(
			valueType + "_Value", 
			"class", 
			EnumSet.of(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL), 
			valueType);
		
		List<String> constructorParams = Lists.newArrayList();		
		
		for (ExecutableElement method: methods) {
			String propertyName = guessPropertyName(method);
			String returnType = method.getReturnType().toString();
			constructorParams.add(returnType);
			constructorParams.add(propertyName);
		}
		
		for (ExecutableElement method: methods) {
			String propertyName = guessPropertyName(method);
			String returnType = method.getReturnType().toString();
			
			javaWriter.emitField(returnType, 
				propertyName, 
				EnumSet.of(Modifier.PRIVATE, Modifier.FINAL));
		}

		javaWriter.beginConstructor(EnumSet.of(Modifier.PUBLIC), constructorParams, new ArrayList<String>());
		for (ExecutableElement method: methods) {
			constructorParams.add(method.getReturnType().toString());
			String propertyName = guessPropertyName(method);
			javaWriter.emitStatement("this.%s = %s", propertyName, propertyName);
		}
		javaWriter.endConstructor();

		for (ExecutableElement method: methods) {
			String propertyName = guessPropertyName(method);
			String returnType = method.getReturnType().toString();
			
			
			javaWriter
				.beginMethod(returnType, method.getSimpleName().toString(), EnumSet.of(Modifier.PUBLIC))
				.emitStatement("return %s", propertyName)
				.endMethod();
		}

		javaWriter.endType();
	}

	private String guessPropertyName(ExecutableElement method) {
		String propertyName = method.getSimpleName().toString();
		if (propertyName.startsWith("get") && propertyName.length() > 3) {
			return StringUtil.lowerCaseFirst(propertyName.substring(3));
		} else {
			return propertyName;
		}
	}

	@SuppressWarnings("unchecked")
	private Iterable<ExecutableElement> collectMethods(Element element) {
		return Iterables.filter(TypeHelper.getMethods(element), 
			Predicates.and(
				MethodPredicates.IS_PUBLIC,
				Predicates.not(MethodPredicates.IS_STATIC),
				new Predicate<ExecutableElement>() {
					@Override
					public boolean apply(ExecutableElement input) {
						return input.getParameters().isEmpty();
					}
				}));
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
