package org.ruben.autobuilder;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.util.SimpleElementVisitor6;

import com.google.common.collect.Lists;

public class TypeHelper {

	public static String getPackageName(Element element) {
		Element topElement = getTopElement(element);
		
		//TODO: Check if this works for default package
		return topElement.accept(new SimpleElementVisitor6<String, Void>() {
			@Override
			public String visitPackage(PackageElement e, Void p) {
				return e.getQualifiedName().toString();
			}
		}, null);
	}
	
	/**
	 * Collects the public, non-static methods on a class
	 */
	public static List<ExecutableElement> getMethods(Element element) {
		List<ExecutableElement> elements = Lists.newArrayList();
		for (Element subElement: element.getEnclosedElements()){
			subElement.accept(new SimpleElementVisitor6<Void, List<ExecutableElement>>() {
				@Override
				public Void visitExecutable(ExecutableElement executableElement, List<ExecutableElement> list) {
					if (executableElement.getKind() != ElementKind.CONSTRUCTOR && isPublic(executableElement) && !isStatic(executableElement)) {
						list.add(executableElement);
					}
					return null;
				}
			}, elements);
		}
		return elements;
	}
	
	private static boolean isPublic(ExecutableElement executableElement) {
		return executableElement.getModifiers().contains(Modifier.PUBLIC);
	}

	private static boolean isStatic(ExecutableElement executableElement) {
		return executableElement.getModifiers().contains(Modifier.STATIC);
	}
	
	

	private static Element getTopElement(Element element) {
		while (element.getEnclosingElement() != null) {
			element = element.getEnclosingElement();
		}
		return element;
	}
}
