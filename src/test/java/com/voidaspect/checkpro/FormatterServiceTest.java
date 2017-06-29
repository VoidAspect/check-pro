package com.voidaspect.checkpro;

import org.junit.Before;
import org.junit.Test;
import org.quicktheories.quicktheories.impl.TheoryBuilder;

import java.util.function.Function;

import static com.voidaspect.checkpro.FormatterService.*;
import static org.junit.Assert.assertEquals;
import static org.quicktheories.quicktheories.QuickTheory.qt;
import static org.quicktheories.quicktheories.generators.SourceDSL.strings;

/**
 * @author miwag.
 */
public class FormatterServiceTest {

    private FormatterService formatterService;

    @Before
    public void setUp() {
        formatterService = new FormatterService();
    }

    @Test
    public void testDefaultFormatOperators() throws Exception {
        String initial = " I am  unformatted-string";

        String formatted1 = formatterService.formatText(initial, Case.LOWER, Trim.ALL, Spacing.UNDERSCORES);
        assertEquals("i_am_unformatted_string", formatted1);

        String formatted2 = formatterService.formatText(initial, Case.NONE, Trim.START_AND_END, Spacing.DASHES);
        assertEquals("I-am--unformatted-string", formatted2);

        String formatted3 = formatterService.formatText(initial, Case.UPPER, Trim.INTERNAL, Spacing.SPACES);
        assertEquals(" I AM UNFORMATTED STRING", formatted3);

        String formatted4 = formatterService.formatText(initial, Case.NONE, Trim.NONE, Spacing.NONE);
        assertEquals(initial, formatted4);
    }

    @Test
    public void caseMustKeepStringLength() throws Exception {
        assertStringLengthInvariant(Case.UPPER);
        assertStringLengthInvariant(Case.LOWER);
        assertStringLengthInvariant(Case.NONE);
    }

    @Test
    public void spacingMustKeepStringLength() throws Exception {
        assertStringLengthInvariant(Spacing.DASHES);
        assertStringLengthInvariant(Spacing.SPACES);
        assertStringLengthInvariant(Spacing.UNDERSCORES);
        assertStringLengthInvariant(Spacing.NONE);
    }

    @Test
    public void trimmingMustBeIdempotent() throws Exception {
        assertOperationIdempotent(Trim.ALL);
        assertOperationIdempotent(Trim.INTERNAL);
        assertOperationIdempotent(Trim.START_AND_END);
        assertOperationIdempotent(Trim.NONE);
    }

    @Test
    public void funcReduceImplementation_MustBe_EnumCompatible() throws Exception {
        stringTheory()
                .asWithPrecursor(s -> formatterService.formatText(s, Trim.INTERNAL))
                .check((initial, formatted) ->
                        formatted.equals(formatterService.formatText(initial, Case.NONE, Trim.INTERNAL, Spacing.NONE)));
        stringTheory()
                .asWithPrecursor(s -> formatterService.formatText(s, Case.LOWER, Spacing.DASHES))
                .check((initial, formatted) ->
                        formatted.equals(formatterService.formatText(initial, Case.LOWER, Trim.NONE, Spacing.DASHES)));
        //noinspection unchecked
        stringTheory()
                .asWithPrecursor(s -> formatterService.formatText(s))
                .check((initial, formatted) ->
                        formatted.equals(formatterService.formatText(initial, Case.NONE, Trim.NONE, Spacing.NONE)));
    }

    private static void assertStringLengthInvariant(Function<String, String> function) {
        stringTheory()
                .asWithPrecursor(function)
                .check((s1, s2) -> s1.length() == s2.length());
    }

    private static void assertOperationIdempotent(Function<String, String> function) {
        stringTheory()
                .as(function)
                .check(s -> s.equals(function.apply(s)));
    }

    private static TheoryBuilder<String> stringTheory() {
        return qt().withExamples(10000).forAll(strings()
                .basicLatinAlphabet()
                .ofLengthBetween(1, 1000)
                .andAlwaysTheValues(" aeudh diao  ehod-aodO ", "aDjDio   ie j", "a_a L"));
    }

}