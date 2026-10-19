package org.example;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opentest4j.AssertionFailedError;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("assertion")
@ExtendWith(HomeworkTestOutputExtension.class)
class AssertionExamplesTest {
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    // Пример 1: проверка метода, который возвращает boolean.
    @RepeatedTest(10)
    void booleanResultHasInformativeAssertion() {
        int number = RANDOM.nextInt(-100, 101);
        boolean expected = number >= 0;
        boolean actual = Main.isPositive(number);
        assertEquals(expected, actual,
                () -> "isPositive(" + number + "): ожидалось <" + expected + ">, получено <" + actual + ">");
    }

    // Пример 2: проверка метода, который возвращает новый список.
    @RepeatedTest(10)
    void listResultHasInformativeAssertion() {
        List<String> names = List.of("Anna", "Ivan", "Anna", "Oleg");
        List<String> expected = List.of("Ivan", "Oleg");
        List<String> actual = Main.removeSpecificName(names, "Anna");
        assertEquals(expected, actual,
                () -> "removeSpecificName: ожидался список <" + expected + ">, получен <" + actual + ">");
    }

    // Пример 3: намеренно неверное ожидание. Внутренний assertEquals падает;
    // assertThrows подтверждает это и не даёт демонстрации сломать общую сборку.
    @RepeatedTest(10)
    void incorrectExpectedValueProducesInformativeAssertionFailure() {
        String actual = Main.getGrade(-1);
        AssertionFailedError failure = assertThrows(AssertionFailedError.class,
                () -> assertEquals("A", actual,
                        "getGrade(-1): намеренно неверное ожидание; ожидалось <A>, получено <" + actual + ">"),
                "Сравнение с намеренно неверным ожиданием должно было завершиться AssertionFailedError");

        String failureMessage = failure.getMessage();
        assertTrue(failureMessage.contains("ожидалось <A>") && failureMessage.contains("получено <Error>"),
                () -> "Сообщение об ошибке должно содержать ожидание и результат; получено <"
                        + failureMessage + ">");
    }

    // Пример 4: проверка массива и подробного сообщения при несовпадении.
    @RepeatedTest(10)
    void arrayResultHasInformativeAssertion() {
        String[] values = {"One", "Two", "Zero"};
        String[] expected = {"Zero", "Two", "One"};
        String[] actual = Main.reverse(values);
        assertArrayEquals(expected, actual,
                () -> "reverse(" + Arrays.toString(values) + "): ожидалось <" + Arrays.toString(expected)
                        + ">, получено <" + Arrays.toString(actual) + ">");
    }
}

class HomeworkTestOutputExtension implements org.junit.jupiter.api.extension.BeforeEachCallback,
        org.junit.jupiter.api.extension.AfterEachCallback,
        org.junit.jupiter.api.extension.TestWatcher {

    @Override
    public void beforeEach(org.junit.jupiter.api.extension.ExtensionContext context) {
        System.out.println("========================Test method start");
    }

    @Override
    public void afterEach(org.junit.jupiter.api.extension.ExtensionContext context) {
        System.out.println("Test method end");
        System.out.println("========================");
    }

    @Override
    public void testSuccessful(org.junit.jupiter.api.extension.ExtensionContext context) {
        System.out.println("TEST PASSED");
    }

    @Override
    public void testFailed(org.junit.jupiter.api.extension.ExtensionContext context, Throwable cause) {
        System.out.println("TEST FAILED");
    }
}
