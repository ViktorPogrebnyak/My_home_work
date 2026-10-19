package org.example;

import java.util.ArrayList;
import java.util.List;

public class Main {

    // Задача 1
    public static boolean isEven(int n) {
        return n % 2 == 0;
    }

    // Задача 2
    public static String checkAccess(int age) {
        if (age > 18) {
            return "Allowed";
        }
        return "Denied";
    }

    // Задача 3
    public static boolean isPositive(int n) {
        return n >= 0 ? true : false;
    }

    // Задача 4
    public static String getGrade(int score) {
        if (score >= 0 && score <= 20) {
            return "E";
        } else if (score <= 40 && score >= 21) {
            return "D";
        } else if (score <= 60 && score >= 41) {
            return "C";
        } else if (score <= 80 && score >= 61) {
            return "B";
        } else if (score <= 100 && score >= 81) {
            return "A";
        }
        return "Error";
    }

    // Задача 5
    public static String blastOff(int start) {
        StringBuilder result = new StringBuilder();
        for (int i = start; i >= 1; i--) {
            if (result.length() > 0) {
                result.append(' ');
            }
            result.append(i);
        }
        if (result.length() > 0) {
            result.append(' ');
        }
        return result.append("Поехали!").toString();
    }

    // Задача 6
    public static int sumToN(int n) {
        int sum = 0;
        for (int i = 1; i <= n; i++) {
            sum += i;
        }
        return sum;
    }

    // Задача 7
    public static boolean hasBug(String[] messages) {
        for (String message : messages) {
            if ("Bug".equalsIgnoreCase(message)) {
                return true;
            }
        }
        return false;
    }

    // Задача 8
    public static String getEvenInRange(int start, int end) {
        StringBuilder result = new StringBuilder();
        for (long i = start; i <= end; i++) {
            if (i % 2 == 0) {
                if (result.length() > 0) {
                    result.append(' ');
                }
                result.append(i);
            }
        }
        return result.toString();
    }

    // Задача 9
    public static int findMax(int[] arr) {
        if (arr.length == 0) {
            throw new IllegalArgumentException("Массив не должен быть пустым");
        }
        int max = arr[0];
        for (int number : arr) {
            if (number > max) {
                max = number;
            }
        }
        return max;
    }

    // Задача 10
    public static String[] reverse(String[] arr) {
        String[] reversed = new String[arr.length];
        for (int i = 0; i < arr.length; i++) {
            reversed[i] = arr[arr.length - 1 - i];
        }
        return reversed;
    }

    // Задача 11
    public static double calcAverage(List<? extends Number> numbers) {
        if (numbers.isEmpty()) {
            throw new IllegalArgumentException("Список не должен быть пустым");
        }
        double sum = 0;
        for (Number number : numbers) {
            sum += number.doubleValue();
        }
        return sum / numbers.size();
    }

    // Задача 12
    public static List<String> removeSpecificName(List<String> names, String nameToRemove) {
        List<String> result = new ArrayList<>();
        for (String name : names) {
            if (!name.equals(nameToRemove)) {
                result.add(name);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println("isEven(4): " + isEven(4));
        System.out.println("checkAccess(18): " + checkAccess(18));
        System.out.println("getGrade(85): " + getGrade(85));
        System.out.println("blastOff(5): " + blastOff(5));
    }
}
