package org.ruben.autobuilder;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.util.SimpleElementVisitor6;

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
	

	private static Element getTopElement(Element element) {
		while (element.getEnclosingElement() != null) {
			element = element.getEnclosingElement();
		}
		return element;
	}
}
