package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class JavaHomeworkJUnitTest {
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    @BeforeEach
    void printMethodStart() {
        System.out.println("========================Test method start");
    }

    @AfterEach
    void printMethodEnd() {
        System.out.println("Test method end");
        System.out.println("========================");
    }

    // Задача 1: @Test, одно случайное число от 1 до 100.
    @Test
    void testIsEven() {
        int number = RANDOM.nextInt(1, 101);
        check(Main.isEven(number) == (number % 2 == 0), "isEven(" + number + ")");
    }

    // Задача 2: @RepeatedTest, 20 случайных возрастов от 0 до 99.
    @RepeatedTest(20)
    void testCheckAccess() {
        int age = RANDOM.nextInt(0, 100);
        String expected = age > 18 ? "Allowed" : "Denied";
        check(expected.equals(Main.checkAccess(age)), "checkAccess(" + age + ")");
    }

    // Задача 3: @ParameterizedTest со случайными целыми числами.
    @ParameterizedTest
    @MethodSource("randomIntegers")
    void testIsPositive(int number) {
        check(Main.isPositive(number) == (number >= 0), "isPositive(" + number + ")");
    }

    // Задача 4: @ParameterizedTest с оценками от 0 до 100.
    @ParameterizedTest
    @MethodSource("randomScores")
    void testGetGrade(int score) {
        String expected = expectedGrade(score);
        check(expected.equals(Main.getGrade(score)), "getGrade(" + score + ")");
    }

    // Задача 5: @Test.
    @Test
    void testBlastOff() {
        int start = RANDOM.nextInt(1, 11);
        StringBuilder expected = new StringBuilder();
        for (int value = start; value >= 1; value--) {
            expected.append(value).append(' ');
        }
        expected.append("Поехали!");
        check(expected.toString().equals(Main.blastOff(start)), "blastOff(" + start + ")");
    }

    // Задача 6: @RepeatedTest с разными случайными значениями.
    @RepeatedTest(10)
    void testSumToN() {
        int n = RANDOM.nextInt(0, 101);
        int expected = n * (n + 1) / 2;
        check(Main.sumToN(n) == expected, "sumToN(" + n + ")");
    }

    // Задача 7: @ParameterizedTest; часть массивов содержит Bug в случайном регистре.
    @ParameterizedTest
    @MethodSource("randomMessageCases")
    void testHasBug(String[] messages, boolean expected) {
        check(Main.hasBug(messages) == expected, "hasBug(" + Arrays.toString(messages) + ")");
    }

    // Задача 8: @RepeatedTest с включёнными границами диапазона.
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
        check(expected.toString().equals(Main.getEvenInRange(start, end)),
                "getEvenInRange(" + start + ", " + end + ")");
    }

    // Задача 9: @Test.
    @Test
    void testFindMax() {
        int[] values = RANDOM.ints(RANDOM.nextInt(1, 11), -100, 101).toArray();
        int expected = Arrays.stream(values).max().orElseThrow();
        check(Main.findMax(values) == expected, "findMax(" + Arrays.toString(values) + ")");
    }

    // Задача 10: @ParameterizedTest.
    @ParameterizedTest
    @MethodSource("randomStringArrays")
    void testReverse(String[] values) {
        String[] expected = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            expected[i] = values[values.length - 1 - i];
        }
        check(Arrays.equals(expected, Main.reverse(values)), "reverse(" + Arrays.toString(values) + ")");
    }

    // Задача 11: @RepeatedTest; список всегда содержит хотя бы одно число.
    @RepeatedTest(10)
    void testCalcAverage() {
        List<Double> values = RANDOM.doubles(RANDOM.nextInt(1, 11), -100, 101)
                .boxed().toList();
        double expected = values.stream().mapToDouble(Double::doubleValue).average().orElseThrow();
        double actual = Main.calcAverage(values);
        check(Math.abs(expected - actual) < 1.0e-9, "calcAverage(" + values + ")");
    }

    // Задача 12: @Test.
    @Test
    void testRemoveSpecificName() {
        String[] names = {"Anna", "Ivan", "Oleg", "Maria", "Pavel"};
        List<String> original = new ArrayList<>();
        for (int i = 0, count = RANDOM.nextInt(1, 11); i < count; i++) {
            original.add(names[RANDOM.nextInt(names.length)]);
        }
        String target = names[RANDOM.nextInt(names.length)];
        List<String> expected = original.stream().filter(name -> !name.equals(target)).toList();
        List<String> actual = Main.removeSpecificName(original, target);
        check(expected.equals(actual), "removeSpecificName(" + original + ", " + target + ")");
        check(original.size() >= actual.size(), "исходный список не должен становиться короче");
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

    private static void check(boolean passed, String description) {
        if (passed) {
            System.out.println("TEST PASSED");
        } else {
            System.out.println("TEST FAILED");
            fail("Проверка не пройдена: " + description);
        }
    }
}
