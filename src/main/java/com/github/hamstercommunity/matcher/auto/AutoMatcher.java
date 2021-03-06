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
package com.github.hamstercommunity.matcher.auto;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.emptyIterable;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import com.github.hamstercommunity.matcher.config.ConfigurableMatcher;

/**
 * This class configures and creates a {@link ConfigurableMatcher} using
 * reflection.
 */
public class AutoMatcher {

	private AutoMatcher() {
		// not instantiable
	}

	public static <T> Matcher<T> equalTo(T expected) {
		return AutoConfigBuilder.createEqualToMatcher(expected);
	}

	@SafeVarargs
	public static <T> Matcher<Iterable<? extends T>> contains(T... expected) {
		if (expected.length == 0) {
			return emptyIterable();
		}
		return Matchers.contains(getMatchers(expected));
	}

	@SafeVarargs
	public static <T> Matcher<Iterable<? extends T>> containsInAnyOrder(T... expected) {
		if (expected.length == 0) {
			return emptyIterable();
		}
		return Matchers.containsInAnyOrder(getMatchers(expected));
	}

	private static <T> List<Matcher<? super T>> getMatchers(T[] expected) {
		return Arrays.stream(expected) //
				.map(AutoMatcher::equalTo) //
				.collect(toList());
	}
}
