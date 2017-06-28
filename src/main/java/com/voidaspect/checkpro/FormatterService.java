package com.voidaspect.checkpro;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * @author miwag.
 */
public final class FormatterService {

    private static final String TOKEN_SEPARATORS = "[ \\-_]";

    private static final String LONG_SPACES = " +";

    @SafeVarargs
    public final String formatText(String text, Function<String, String>... functions) {
        return Arrays.stream(functions)
                .reduce(Function.identity(), Function::andThen)
                .apply(text);
    }

    public final String formatText(String text, Case stringCase, Trim stringTrimming, Spacing stringSpacing) {
        return stringTrimming
                .andThen(stringSpacing)
                .andThen(stringCase)
                .apply(text);
    }

    public enum Trim implements UnaryOperator<String> {

        START_AND_END(String::trim),

        INTERNAL(s -> s.replaceAll(LONG_SPACES, " ")),

        ALL(START_AND_END.function.andThen(INTERNAL.function)),

        NONE;

        private final Function<String, String> function;

        Trim() {
            this.function = UnaryOperator.identity();
        }

        Trim(Function<String, String> function) {
            this.function = function;
        }

        @Override
        public String apply(String s) {
            return function.apply(s);
        }
    }

    public enum Case implements UnaryOperator<String> {

        LOWER(String::toLowerCase),

        UPPER(String::toUpperCase),

        NONE;

        private final UnaryOperator<String> function;

        Case() {
            this.function = UnaryOperator.identity();
        }

        Case(UnaryOperator<String> function) {
            this.function = function;
        }

        @Override
        public String apply(String s) {
            return function.apply(s);
        }
    }

    public enum Spacing implements UnaryOperator<String> {

        UNDERSCORES("_"),

        SPACES(" "),

        DASHES("-"),

        NONE;

        private final UnaryOperator<String> function;

        Spacing() {
            this.function = UnaryOperator.identity();
        }

        Spacing(String replacement) {
            this.function = s -> s.replaceAll(TOKEN_SEPARATORS, replacement);
        }

        @Override
        public String apply(String s) {
            return function.apply(s);
        }
    }


}
