/**
 * Automatic hamcrest matcher for model classes
 * Copyright (C) 2017 Christoph Pirkl <christoph at users.sourceforge.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.hamstercommunity.matcher.model;

import java.util.Arrays;
import java.util.List;

import com.github.hamstercommunity.matcher.config.ConfigurableMatcher;

/**
 * A model class used for testing and demonstrating {@link ConfigurableMatcher}.
 */
public class DemoModel {
	private final int id;
	private final String name;
	private final DemoAttribute attr;
	private final List<DemoModel> children;
	private final String[] stringArray;
	private final Long longVal;

	public DemoModel(int id, String name, Long longVal, DemoAttribute attr, String[] stringArray,
			List<DemoModel> children) {
		this.id = id;
		this.name = name;
		this.longVal = longVal;
		this.attr = attr;
		this.stringArray = stringArray;
		this.children = children;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public DemoAttribute getAttr() {
		return attr;
	}

	public List<DemoModel> getChildren() {
		return children;
	}

	public String[] getStringArray() {
		return stringArray;
	}

	public Long getLongVal() {
		return longVal;
	}

	@Override
	public String toString() {
		return "DemoModel [id=" + id + ", name=" + name + ", attr=" + attr + ", children=" + children + ", stringArray="
				+ Arrays.toString(stringArray) + ", longVal=" + longVal + "]";
	}
}
