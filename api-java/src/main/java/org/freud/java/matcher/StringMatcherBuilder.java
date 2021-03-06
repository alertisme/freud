/*
 * Copyright 2013 LMAX Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.freud.java.matcher;

import org.hamcrest.Description;

import java.util.regex.Pattern;


public final class StringMatcherBuilder<T> {
    private final RegexMatcherAdapter<T> adapter;

    public StringMatcherBuilder(final RegexMatcherAdapter<T> adapter) {
        this.adapter = adapter;
    }

    public FreudExtendedMatcher<T> matches(final String regex) {
        return new RegexMatcher<T>(regex, true, adapter);
    }

    public FreudExtendedMatcher<T> contains(final String regex) {
        return new RegexMatcher<T>(regex, false, adapter);
    }

    public FreudExtendedMatcher<T> matches(final String regex, final int regexFlags) {
        return new RegexMatcher<T>(regex, true, regexFlags, adapter);
    }

    public FreudExtendedMatcher<T> contains(final String regex, final int regexFlags) {
        return new RegexMatcher<T>(regex, false, regexFlags, adapter);
    }


    public FreudExtendedMatcher<T> is(final org.hamcrest.Matcher<? super String> realMatcher) {
        return new FreudExtendedMatcher<T>() {
            @Override
            protected boolean matchesSafely(final T object) {
                return realMatcher.matches(adapter.getStringToMatch(object));
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText(adapter.matcherDisplayName());
                description.appendText("(");
                realMatcher.describeTo(description);
                description.appendText(")");
            }
        };
    }

    private static final class RegexMatcher<T> extends FreudExtendedMatcher<T> {
        private final Pattern regex;
        private final boolean completeMatch;
        private final RegexMatcherAdapter<T> adapter;

        public RegexMatcher(final String regex, final boolean completeMatch,
                            final RegexMatcherAdapter<T> adapter) {
            this(regex, completeMatch, 0, adapter);
        }

        public RegexMatcher(final String regex, final boolean completeMatch,
                            final int regexFlags,
                            final RegexMatcherAdapter<T> adapter) {
            this.adapter = adapter;
            this.completeMatch = completeMatch;
            this.regex = Pattern.compile(regex, regexFlags);
        }

        public final boolean matchesSafely(final T toBeAnalysed) {
            final String stringToMatch = adapter.getStringToMatch(toBeAnalysed);
            return (completeMatch) ? regex.matcher(stringToMatch).matches() :
                   regex.matcher(stringToMatch).find();
        }

        @Override
        public String toString() {
            return adapter.matcherDisplayName() + ((completeMatch) ? "Match" : "Contains") +
                    "(" + regex.pattern() + ")";
        }

        public void describeTo(Description description) {
            description.appendText(toString());
        }
    }
}