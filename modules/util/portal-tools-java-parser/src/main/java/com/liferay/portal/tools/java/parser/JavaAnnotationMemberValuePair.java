/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.tools.java.parser;

import com.liferay.portal.kernel.util.StringBundler;

/**
 * @author Hugo Huijser
 */
public class JavaAnnotationMemberValuePair extends BaseJavaTerm {

	public JavaAnnotationMemberValuePair(String name) {
		_name = new JavaSimpleValue(name);
	}

	public void setValueJavaExpression(JavaExpression valueJavaExpression) {
		_valueJavaExpression = valueJavaExpression;
	}

	@Override
	public String toString(
		String indent, String prefix, String suffix, int maxLineLength) {

		StringBundler sb = new StringBundler();

		if (_valueJavaExpression instanceof JavaArray) {
			sb.append(_name.toString(indent, prefix, " =", -1));

			append(
				sb, _valueJavaExpression, indent, " ", suffix, maxLineLength,
				false);

			return sb.toString();
		}

		sb.append(_name.toString(indent, prefix, " = ", -1));
		sb.append(_valueJavaExpression.toString("", "", suffix, -1));

		return sb.toString();
	}

	private final JavaSimpleValue _name;
	private JavaExpression _valueJavaExpression;

}