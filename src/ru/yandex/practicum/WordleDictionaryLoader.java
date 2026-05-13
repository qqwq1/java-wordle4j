package ru.yandex.practicum;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/*
этот класс содержит в себе всю рутину по работе с файлами словарей и с кодировками
    ему нужны методы по загрузке списка слов из файла по имени файла
    на выходе должен быть класс WordleDictionary
 */
public class WordleDictionaryLoader {
    private final Path dictionaryFilePath;
    private final PrintWriter printWriter;
    private static final int LENGTH = 5;

    public WordleDictionaryLoader(File logFile, Path dictionaryFilePath) throws FileNotFoundException {
        this.dictionaryFilePath = dictionaryFilePath;
        printWriter = new PrintWriter(logFile);
    }

    private Set<String> readDictionaryFromFile() {
        Set<String> uniqueWordsWithCorrectLength = new HashSet<>();
        try (BufferedReader dictionaryReader = new BufferedReader(new FileReader(dictionaryFilePath.toFile()
                , StandardCharsets.UTF_8))) {

            String line;
            while ((line = dictionaryReader.readLine()) != null) {
                StringBuilder word = new StringBuilder(line.strip().toLowerCase());
                boolean isCorrect = true;
                if (word.length() != LENGTH) {
                    continue;
                } else {
                    for (int i = 0; i < word.length(); i++) {
                        if (!((word.charAt(i) >= 1072 && word.charAt(i) <= 1103) || word.charAt(i) == 1105)) {
                            isCorrect = false;
                            break;
                        }
                        if (word.charAt(i) == 'ё') {
                            word.replace(i, i + 1, "е");
                        }
                    }
                }
                if (isCorrect) {
                    uniqueWordsWithCorrectLength.add(word.toString());
                }
            }
        } catch (IOException exception) {
            printWriter.println("Ошибка: " + exception);
            for (StackTraceElement stack : exception.getStackTrace()) {
                printWriter.println("Класс: " + stack.getFileName()
                        + ", Метод: " + stack.getMethodName()
                        + ", Строка: " + stack.getLineNumber());
            }
        } finally {
            printWriter.close();
        }
        return uniqueWordsWithCorrectLength;
    }

    public WordleDictionary getWordleDictionary() {
        Set<String> dictionary = readDictionaryFromFile();
        return new WordleDictionary(new ArrayList<>(dictionary));
    }
}
