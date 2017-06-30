package com.voidaspect.checkpro;

import org.junit.Before;
import org.junit.Test;
import org.quicktheories.quicktheories.core.Source;
import org.quicktheories.quicktheories.impl.TheoryBuilder;

import java.util.function.Function;

import static com.voidaspect.checkpro.FormatterService.*;
import static org.junit.Assert.assertEquals;
import static org.quicktheories.quicktheories.QuickTheory.qt;
import static org.quicktheories.quicktheories.generators.SourceDSL.arbitrary;
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
        qt().withExamples(Case.values().length).forAll(arbitrary().enumValues(Case.class))
                .checkAssert(FormatterServiceTest::assertStringLengthInvariant);
    }

    @Test
    public void spacingMustKeepStringLength() throws Exception {
        qt().withExamples(Spacing.values().length).forAll(arbitrary().enumValues(Spacing.class))
                .checkAssert(FormatterServiceTest::assertStringLengthInvariant);
    }

    @Test
    public void trimmingMustBeIdempotent() throws Exception {
        qt().withExamples(Trim.values().length).forAll(arbitrary().enumValues(Trim.class))
                .checkAssert(FormatterServiceTest::assertOperationIdempotent);
    }

    @Test
    public void formattingMustBeIdempotent() throws Exception {
        String initial = " I am  unformatted-string";
        assertEquals(initial, formatterService.formatText(initial));
        assertEquals(initial, formatterService.formatText(initial, Function.identity()));

        qt().withExamples(10000).forAll(stringValues().andAlwaysTheValues(initial),
                arbitrary().enumValues(Case.class),
                arbitrary().enumValues(Trim.class),
                arbitrary().enumValues(Spacing.class))
                .checkAssert((text, c, t, s) -> {
                    assertEquals(formatterService.formatText(text, c, c, c, t, t, t, s, s, s),
                            formatterService.formatText(text, c, t, s));

                    assertEquals(formatterService.formatText(text, c, c),
                            formatterService.formatText(text, c, Trim.NONE, Spacing.NONE));

                    assertEquals(formatterService.formatText(text, t, t),
                            formatterService.formatText(text, Case.NONE, t, Spacing.NONE));

                    assertEquals(formatterService.formatText(text, s, s),
                            formatterService.formatText(text, Case.NONE, Trim.NONE, s));

                    assertEquals(formatterService.formatText(text, c, c, t, t),
                            formatterService.formatText(text, c, t, Spacing.NONE));

                    assertEquals(formatterService.formatText(text, c, c, s, s),
                            formatterService.formatText(text, c, Trim.NONE, s));

                    assertEquals(formatterService.formatText(text, t, t, s, s),
                            formatterService.formatText(text, Case.NONE, t, s));
                });
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
        stringTheory()
                .asWithPrecursor(s -> formatterService.formatText(s))
                .check((initial, formatted) ->
                        formatted.equals(formatterService.formatText(initial, Case.NONE, Trim.NONE, Spacing.NONE)));

        qt().withExamples(10000).forAll(stringValues(),
                arbitrary().enumValues(Case.class),
                arbitrary().enumValues(Trim.class),
                arbitrary().enumValues(Spacing.class))
                .checkAssert((initial, c, t, s) -> {
                    assertEquals(formatterService.formatText(initial, c),
                            formatterService.formatText(initial, c, Trim.NONE, Spacing.NONE));

                    assertEquals(formatterService.formatText(initial, t),
                            formatterService.formatText(initial, Case.NONE, t, Spacing.NONE));

                    assertEquals(formatterService.formatText(initial, s),
                            formatterService.formatText(initial, Case.NONE, Trim.NONE, s));

                    assertEquals(formatterService.formatText(initial, c, t),
                            formatterService.formatText(initial, c, t, Spacing.NONE));

                    assertEquals(formatterService.formatText(initial, c, s),
                            formatterService.formatText(initial, c, Trim.NONE, s));

                    assertEquals(formatterService.formatText(initial, t, s),
                            formatterService.formatText(initial, Case.NONE, t, s));
                });

    }

    private static void assertStringLengthInvariant(Function<String, String> function) {
        stringTheory()
                .asWithPrecursor(function)
                .check((s1, s2) -> s1.length() == s2.length());
    }

    private static void assertOperationIdempotent(Function<String, String> function) {
        stringTheory()
                .as(function)
                .check(newS -> newS.equals(function.apply(newS)));
    }

    private static TheoryBuilder<String> stringTheory() {
        return qt().withExamples(10000).forAll(stringValues());
    }

    private static Source<String> stringValues() {
        return strings()
                .basicLatinAlphabet()
                .ofLengthBetween(0, 1000)
                .andAlwaysTheValues("", " aeudh diao  ehod-aodO ", "aDjDio   ie j", "a_a L");
    }

}