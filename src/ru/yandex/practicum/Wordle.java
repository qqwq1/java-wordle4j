package ru.yandex.practicum;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

/*
в главном классе нам нужно:
    создать лог-файл (он должен передаваться во все классы)
    создать загрузчик словарей WordleDictionaryLoader
    загрузить словарь WordleDictionary с помощью класса WordleDictionaryLoader
    затем создать игру WordleGame и передать ей словарь
    вызвать игровой метод в котором в цикле опрашивать пользователя и передавать информацию в игру
    вывести состояние игры и конечный результат
 */
public class Wordle {

    private static File logFile;
    private static PrintWriter printWriter;

    private static void createLogFile(Path logFilePath) throws IOException {
        Files.writeString(logFilePath, "");
        logFile = new File(logFilePath.toString());
        printWriter = new PrintWriter(logFile);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            final Path logFilePath = args.length >= 2 ? Path.of(args[0]) : Path.of("log.txt");
            final Path dictionaryFilePath = args.length >= 2 ? Path.of(args[1]) : Path.of("words_ru.txt");
            createLogFile(logFilePath);
            WordleDictionaryLoader dictionaryLoader = new WordleDictionaryLoader(logFile, dictionaryFilePath);
            WordleDictionary dictionary = dictionaryLoader.loadWordleDictionary();
            String answer = dictionary.getRandomWord();
            WordleGame wordleGame = new WordleGame(answer, dictionary);

            printRules();
            while (true) {
                String input = scanner.nextLine();

                try {
                    input = dictionary.isWordCorrect(input);
                } catch (InputException exception) {
                    writeLog(exception);
                    System.out.println(exception.getMessage());
                    System.out.println("Попробуйте еще раз");
                    continue;
                }
                if (!input.isEmpty()) {
                    wordleGame.setUserAnswer(input);
                } else {
                    String helpWord = wordleGame.help();
                    System.out.println(helpWord);
                    wordleGame.setUserAnswer(helpWord);
                }
                System.out.println(wordleGame.getMatches());
                if (wordleGame.isWin()) {
                    if (wordleGame.getUserAnswer().equals(wordleGame.getAnswer())) {
                        System.out.println("Поздравляю, ты победил");
                    } else {
                        System.out.println("К сожалению ты проиграл");
                        System.out.println("Правильный ответ: " + wordleGame.getAnswer());
                    }
                    break;
                }
            }

        } catch (IOException exception) {
            writeLog(exception);
        } finally {
            printWriter.close();
        }
    }

    private static void writeLog(Throwable exception) {
        printWriter.println("Ошибка: " + exception);
        for (StackTraceElement stack : exception.getStackTrace()) {
            printWriter.println("Класс: " + stack.getFileName()
                    + ", Метод: " + stack.getMethodName()
                    + ", Строка: " + stack.getLineNumber());
        }
    }

    private static void printRules() {
        System.out.println("Привет, это игра \"Wordle\"!");
        System.out.println("Правила игры очень простые: я загадал существительное в единственном числе");
        System.out.println("именительном падеже, которое состоит из 5 букв русского алфавита");
        System.out.println("Как будешь готов - введи слово из 5 букв,");
        System.out.println("а после этого я дам тебе подсказку в формате:");
        System.out.println("--------");
        System.out.println("> гонец\n" + "> +^-^-");
        System.out.println("--------");
        System.out.println("Загаданное слово: \"герой\"");
        System.out.println("- введенной буквы нет в слове");
        System.out.println("+ введенная буква стоит на правильном месте");
        System.out.println("^ введенная буква есть в слове, но стоит не на своем месте");
        System.out.println();
        System.out.println("И самое главное: если затрудняешься ответить - просто нажми " +
                "Enter и я дам слово подсказку");
    }

}
