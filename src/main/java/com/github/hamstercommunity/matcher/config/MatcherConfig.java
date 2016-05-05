/**
 * Automatic hamcrest matcher for model classes
 * Copyright (C) 2016 Christoph Pirkl <christoph at users.sourceforge.net>
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
package com.github.hamstercommunity.matcher.config;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

public class MatcherConfig<T> {
	private final T expected;
	private final List<PropertyConfig<T, ?>> propertyConfigs;

	private MatcherConfig(final T expected, final List<PropertyConfig<T, ?>> propertyConfigs) {
		this.expected = expected;
		this.propertyConfigs = Collections.unmodifiableList(propertyConfigs);
	}

	T getExpected() {
		return this.expected;
	}

	@SuppressWarnings("unchecked")
	List<PropertyConfig<T, Object>> getPropertyConfigs() {
		return this.propertyConfigs.stream() //
				.map(c -> (PropertyConfig<T, Object>) c) //
				.collect(toList());
	}

	public static <B> Builder<B> builder(final B expected) {
		return new Builder<B>(expected);
	}

	/**
	 * Builder for {@link MatcherConfig}
	 */
	public static class Builder<B> {
		private final B expected;
		private final List<PropertyConfig<B, ?>> properties = new ArrayList<>();

		private Builder(final B expected) {
			this.expected = Objects.requireNonNull(expected);
		}

		/**
		 * Add a property that can be compared with
		 * {@link Matchers#equalTo(Object)}.
		 *
		 * @param propertyName
		 *            name of the property.
		 * @param propertyAccessor
		 *            the accessor function for retrieving the property value.
		 * @return the builder itself for fluent programming style.
		 */
		public <P> Builder<B> addEqualsProperty(final String propertyName, final Function<B, P> propertyAccessor) {
			return addProperty(propertyName, propertyAccessor, Matchers::equalTo);
		}

		/**
		 * Add a property that can be compared with
		 * {@link Matchers#equalTo(Object)}.
		 *
		 * @param propertyName
		 *            name of the property.
		 * @param propertyAccessor
		 *            the accessor function for retrieving the property value.
		 * @param matcherBuilder
		 *            a function for creating the matcher.
		 * @return the builder itself for fluent programming style.
		 */
		public <P> Builder<B> addProperty(final String propertyName, final Function<B, P> propertyAccessor,
				final Function<P, Matcher<P>> matcherBuilder) {
			final Matcher<P> matcher = createMatcher(propertyAccessor, matcherBuilder);
			return addPropertyInternal(propertyName, matcher, propertyAccessor);
		}

		@SuppressWarnings("unchecked")
		private <P> Matcher<P> createMatcher(final Function<B, P> propertyAccessor,
				final Function<P, Matcher<P>> matcherBuilder) {
			final P expectedValue = propertyAccessor.apply(this.expected);
			if (expectedValue == null) {
				return (Matcher<P>) Matchers.nullValue();
			}
			return matcherBuilder.apply(expectedValue);
		}

		/**
		 * Add a property of type {@link Iterable} where the element order is
		 * relevant.
		 *
		 * @param propertyName
		 *            name of the property.
		 * @param propertyAccessor
		 *            the accessor function for retrieving the property value.
		 * @param matcherBuilder
		 *            a function for creating the matcher for the iterable
		 *            elements.
		 * @return the builder itself for fluent programming style.
		 */
		public <P> Builder<B> addIterableProperty(final String propertyName,
				final Function<B, Iterable<? extends P>> propertyAccessor,
				final Function<P, Matcher<P>> matcherBuilder) {
			final Iterable<? extends P> expectedPropertyValue = propertyAccessor.apply(this.expected);
			final Matcher<Iterable<? extends P>> listMatcher = createListMatcher(matcherBuilder, expectedPropertyValue);
			return addPropertyInternal(propertyName, listMatcher, propertyAccessor);
		}

		private <P> Matcher<Iterable<? extends P>> createListMatcher(final Function<P, Matcher<P>> matcherBuilder,
				final Iterable<? extends P> expectedPropertyValue) {
			if (expectedPropertyValue == null) {
				return createNullIterableMatcher();
			}
			if (!expectedPropertyValue.iterator().hasNext()) {
				return Matchers.<P> emptyIterable();
			}
			final List<Matcher<? super P>> matchers = StreamSupport.stream(expectedPropertyValue.spliterator(), false)
					.map(matcherBuilder) //
					.collect(toList());
			return Matchers.contains(matchers);
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		private <P> Matcher<Iterable<? extends P>> createNullIterableMatcher() {
			return new NullIterableMatcher();
		}

		private <P> Builder<B> addPropertyInternal(final String propertyName, final Matcher<P> matcher,
				final Function<B, P> propertyAccessor) {
			this.properties.add(new PropertyConfig<>(propertyName, matcher, propertyAccessor));
			return this;
		}

		/**
		 * Build a new {@link MatcherConfig}.
		 *
		 * @return the new {@link MatcherConfig}.
		 */
		public MatcherConfig<B> build() {
			return new MatcherConfig<B>(this.expected, new ArrayList<>(this.properties));
		}
	}

	private static class NullIterableMatcher<T> extends BaseMatcher<Iterable<T>> {
		@Override
		public boolean matches(Object item) {
			return item == null;
		}

		@Override
		public void describeTo(Description description) {
			description.appendText("null");
		}
	}
}
