package org.example;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@Tag("homework")
@ExtendWith(HomeworkTestOutputExtension.class)
class JavaHomeworkJUnitTest {
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    // Задача 1: случайное число от 1 до 100; повторение требуется заданием по Assertions.
    @RepeatedTest(10)
    void testIsEven() {
        int number = RANDOM.nextInt(1, 101);
        assertEquals(number % 2 == 0, Main.isEven(number),
                () -> "isEven(" + number + "): ожидалось <" + (number % 2 == 0)
                        + ">, получено <" + Main.isEven(number) + ">");
    }

    // Задача 2: 20 случайных возрастов от 0 до 99.
    @RepeatedTest(20)
    void testCheckAccess() {
        int age = RANDOM.nextInt(0, 100);
        String expected = age > 18 ? "Allowed" : "Denied";
        String actual = Main.checkAccess(age);
        assertEquals(expected, actual,
                () -> "checkAccess(" + age + "): ожидалось <" + expected + ">, получено <" + actual + ">");
    }

    // Задача 3: параметризованный запуск на случайных целых числах.
    @ParameterizedTest
    @MethodSource("randomIntegers")
    void testIsPositive(int number) {
        boolean expected = number >= 0;
        boolean actual = Main.isPositive(number);
        assertEquals(expected, actual,
                () -> "isPositive(" + number + "): ожидалось <" + expected + ">, получено <" + actual + ">");
    }

    // Задача 4: случайные оценки от 0 до 100.
    @ParameterizedTest
    @MethodSource("randomScores")
    void testGetGrade(int score) {
        String expected = expectedGrade(score);
        String actual = Main.getGrade(score);
        assertEquals(expected, actual,
                () -> "getGrade(" + score + "): ожидалось <" + expected + ">, получено <" + actual + ">");
    }

    // Задача 5: случайный старт обратного отсчёта.
    @RepeatedTest(10)
    void testBlastOff() {
        int start = RANDOM.nextInt(1, 11);
        StringBuilder expected = new StringBuilder();
        for (int value = start; value >= 1; value--) {
            expected.append(value).append(' ');
        }
        expected.append("Поехали!");
        String actual = Main.blastOff(start);
        assertEquals(expected.toString(), actual,
                () -> "blastOff(" + start + "): ожидалось <" + expected + ">, получено <" + actual + ">");
    }

    // Задача 6: 10 случайных значений.
    @RepeatedTest(10)
    void testSumToN() {
        int n = RANDOM.nextInt(0, 101);
        int expected = n * (n + 1) / 2;
        int actual = Main.sumToN(n);
        assertEquals(expected, actual,
                () -> "sumToN(" + n + "): ожидалось <" + expected + ">, получено <" + actual + ">");
    }

    // Задача 7: случайные массивы; часть из них содержит Bug в разном регистре.
    @ParameterizedTest
    @MethodSource("randomMessageCases")
    void testHasBug(String[] messages, boolean expected) {
        boolean actual = Main.hasBug(messages);
        assertEquals(expected, actual,
                () -> "hasBug(" + Arrays.toString(messages) + "): ожидалось <" + expected
                        + ">, получено <" + actual + ">");
    }

    // Задача 8: 10 случайных диапазонов с включёнными границами.
    @RepeatedTest(10)
    void testGetEvenInRange() {
        int start = RANDOM.nextInt(-50, 31);
        int end = start + RANDOM.nextInt(0, 21);
        StringBuilder expected = new StringBuilder();
        for (int value = start; value <= end; value++) {
            if (value % 2 == 0) {
                if (!expected.isEmpty()) {
                    expected.append(' ');
                }
                expected.append(value);
            }
        }
        String actual = Main.getEvenInRange(start, end);
        assertEquals(expected.toString(), actual,
                () -> "getEvenInRange(" + start + ", " + end + "): ожидалось <" + expected
                        + ">, получено <" + actual + ">");
    }

    // Задача 9: случайный массив целых чисел.
    @RepeatedTest(10)
    void testFindMax() {
        int[] values = RANDOM.ints(RANDOM.nextInt(1, 11), -100, 101).toArray();
        int expected = Arrays.stream(values).max().orElseThrow();
        int actual = Main.findMax(values);
        assertEquals(expected, actual,
                () -> "findMax(" + Arrays.toString(values) + "): ожидалось <" + expected
                        + ">, получено <" + actual + ">");
    }

    // Задача 10: параметризованные массивы случайных строк.
    @ParameterizedTest
    @MethodSource("randomStringArrays")
    void testReverse(String[] values) {
        String[] expected = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            expected[i] = values[values.length - 1 - i];
        }
        String[] actual = Main.reverse(values);
        assertArrayEquals(expected, actual,
                () -> "reverse(" + Arrays.toString(values) + "): ожидалось <" + Arrays.toString(expected)
                        + ">, получено <" + Arrays.toString(actual) + ">");
    }

    // Задача 11: 10 случайных списков чисел.
    @RepeatedTest(10)
    void testCalcAverage() {
        List<Double> values = RANDOM.doubles(RANDOM.nextInt(1, 11), -100, 101)
                .boxed().toList();
        double expected = values.stream().mapToDouble(Double::doubleValue).average().orElseThrow();
        double actual = Main.calcAverage(values);
        assertEquals(expected, actual, 1.0e-9,
                () -> "calcAverage(" + values + "): ожидалось <" + expected + ">, получено <" + actual + ">");
    }

    // Задача 12: случайный список имён и случайное имя для исключения.
    @RepeatedTest(10)
    void testRemoveSpecificName() {
        String[] names = {"Anna", "Ivan", "Oleg", "Maria", "Pavel"};
        List<String> original = new ArrayList<>();
        for (int i = 0, count = RANDOM.nextInt(1, 11); i < count; i++) {
            original.add(names[RANDOM.nextInt(names.length)]);
        }
        String target = names[RANDOM.nextInt(names.length)];
        List<String> expected = original.stream().filter(name -> !name.equals(target)).toList();
        List<String> actual = Main.removeSpecificName(original, target);
        assertEquals(expected, actual,
                () -> "removeSpecificName(" + original + ", " + target + "): ожидалось <" + expected
                        + ">, получено <" + actual + ">");
    }

    private static Stream<Integer> randomIntegers() {
        return RANDOM.ints(10, -100, 101).boxed();
    }

    private static Stream<Integer> randomScores() {
        return RANDOM.ints(20, 0, 101).boxed();
    }

    private static Stream<org.junit.jupiter.params.provider.Arguments> randomMessageCases() {
        return IntStream.range(0, 10).mapToObj(index -> {
            String[] safeMessages = {"OK", "Fixed", "Ready", "Error"};
            boolean containsBug = RANDOM.nextBoolean();
            String[] messages = RANDOM.ints(RANDOM.nextInt(0, 7), 0, safeMessages.length)
                    .mapToObj(value -> safeMessages[value])
                    .toArray(String[]::new);
            if (containsBug) {
                if (messages.length == 0) {
                    messages = new String[1];
                }
                messages[RANDOM.nextInt(messages.length)] = "bUg";
            }
            return arguments(messages, containsBug);
        });
    }

    private static Stream<org.junit.jupiter.params.provider.Arguments> randomStringArrays() {
        String[] words = {"One", "Two", "Zero", "Four", "Five"};
        return IntStream.range(0, 10).mapToObj(ignored -> {
            String[] values = new String[RANDOM.nextInt(0, 8)];
            for (int i = 0; i < values.length; i++) {
                values[i] = words[RANDOM.nextInt(words.length)];
            }
            return arguments((Object) values);
        });
    }

    private static String expectedGrade(int score) {
        if (score < 0 || score > 100) return "Error";
        if (score <= 20) return "E";
        if (score <= 40) return "D";
        if (score <= 60) return "C";
        if (score <= 80) return "B";
        return "A";
    }
}
