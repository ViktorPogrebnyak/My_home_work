package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Простая проверка заданий без сторонних библиотек.
 * Запускается через main, поэтому для неё не нужен JUnit.
 */
public class MainTest {
    private static int checksPassed;

    public static void main(String[] args) {
        testIsEven();
        testCheckAccess();
        testIsPositive();
        testGetGrade();
        testBlastOff();
        testSumToN();
        testHasBug();
        testGetEvenInRange();
        testFindMax();
        testReverse();
        testCalcAverage();
        testRemoveSpecificName();

        System.out.println("Checks passed: " + checksPassed);
    }

    private static void testIsEven() {
        check(Main.isEven(4), "4 должно быть чётным");
        check(!Main.isEven(-3), "-3 должно быть нечётным");
    }

    private static void testCheckAccess() {
        checkEquals("Allowed", Main.checkAccess(19), "Возраст больше 18");
        checkEquals("Denied", Main.checkAccess(18), "Возраст 18");
    }

    private static void testIsPositive() {
        check(Main.isPositive(0), "Ноль должен считаться положительным");
        check(!Main.isPositive(-1), "Отрицательное число не положительное");
    }

    private static void testGetGrade() {
        checkEquals("E", Main.getGrade(0), "Нижняя граница E");
        checkEquals("E", Main.getGrade(20), "Верхняя граница E");
        checkEquals("D", Main.getGrade(21), "Нижняя граница D");
        checkEquals("D", Main.getGrade(40), "Верхняя граница D");
        checkEquals("C", Main.getGrade(41), "Нижняя граница C");
        checkEquals("C", Main.getGrade(60), "Верхняя граница C");
        checkEquals("B", Main.getGrade(61), "Нижняя граница B");
        checkEquals("B", Main.getGrade(80), "Верхняя граница B");
        checkEquals("A", Main.getGrade(81), "Нижняя граница A");
        checkEquals("A", Main.getGrade(100), "Верхняя граница A");
        checkEquals("Error", Main.getGrade(-1), "Оценка ниже диапазона");
        checkEquals("Error", Main.getGrade(101), "Оценка выше диапазона");
    }

    private static void testBlastOff() {
        checkEquals("5 4 3 2 1 Поехали!", Main.blastOff(5), "Обратный отсчёт от 5");
        checkEquals("1 Поехали!", Main.blastOff(1), "Обратный отсчёт от 1");
        checkEquals("Поехали!", Main.blastOff(0), "Старт с нуля");
    }

    private static void testSumToN() {
        checkEquals(15, Main.sumToN(5), "Сумма от 1 до 5");
        checkEquals(1, Main.sumToN(1), "Сумма до 1");
        checkEquals(0, Main.sumToN(0), "Сумма до 0");
    }

    private static void testHasBug() {
        check(Main.hasBug(new String[]{"OK", "bUg"}), "Поиск без учёта регистра");
        check(!Main.hasBug(new String[]{"OK", "Fixed"}), "В массиве нет Bug");
        check(!Main.hasBug(new String[]{}), "Пустой массив не содержит Bug");
    }

    private static void testGetEvenInRange() {
        checkEquals("2 4", Main.getEvenInRange(2, 5), "Чётные числа от 2 до 5");
        checkEquals("-2 0 2", Main.getEvenInRange(-3, 2), "Диапазон с отрицательными числами");
        checkEquals("", Main.getEvenInRange(5, 2), "Перевёрнутые границы");
        checkEquals("", Main.getEvenInRange(1, 1), "Диапазон без чётных чисел");
    }

    private static void testFindMax() {
        checkEquals(9, Main.findMax(new int[]{-5, 9, 2}), "Максимум в массиве");
        checkEquals(-2, Main.findMax(new int[]{-8, -2, -4}), "Максимум среди отрицательных");
        checkThrows(IllegalArgumentException.class, () -> Main.findMax(new int[]{}), "Пустой массив");
    }

    private static void testReverse() {
        String[] original = {"One", "Two", "Zero"};
        String[] reversed = Main.reverse(original);
        check(Arrays.equals(new String[]{"Zero", "Two", "One"}, reversed), "Порядок элементов обращён");
        check(original != reversed, "Возвращается новый массив");
        checkEquals("One", original[0], "Исходный массив не изменён");
    }

    private static void testCalcAverage() {
        checkEquals(2.0, Main.calcAverage(List.of(1, 2, 3)), "Среднее целых чисел");
        checkEquals(2.0, Main.calcAverage(List.of(1.5, 2.5)), "Среднее дробных чисел");
        checkThrows(IllegalArgumentException.class, () -> Main.calcAverage(List.of()), "Пустой список");
    }

    private static void testRemoveSpecificName() {
        List<String> original = new ArrayList<>(List.of("Anna", "Ivan", "Anna", "Oleg"));
        List<String> result = Main.removeSpecificName(original, "Anna");
        checkEquals(List.of("Ivan", "Oleg"), result, "Удалены все совпадения имени");
        checkEquals(List.of("Anna", "Ivan", "Anna", "Oleg"), original, "Исходный список не изменён");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
        checksPassed++;
    }

    private static void checkEquals(Object expected, Object actual, String message) {
        check(Objects.equals(expected, actual), message + ": ожидалось <" + expected + ">, получено <" + actual + ">");
    }

    private static void checkThrows(Class<? extends Throwable> expectedType, Runnable action, String message) {
        try {
            action.run();
        } catch (Throwable actual) {
            check(expectedType.isInstance(actual), message + ": неверный тип исключения " + actual.getClass().getSimpleName());
            return;
        }
        throw new AssertionError(message + ": ожидалось исключение " + expectedType.getSimpleName());
    }
}
