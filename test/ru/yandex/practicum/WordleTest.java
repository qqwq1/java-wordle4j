package ru.yandex.practicum;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Wordle main class tests")
class WordleTest {

    @TempDir
    Path tempDir;

    private String originalUserDir;
    private InputStream originalIn;
    private PrintStream originalOut;
    private ByteArrayOutputStream outContent;
    private Path logFilePath;
    private Path dictionaryFilePath;

    @BeforeEach
    void setUp() {
        originalUserDir = System.getProperty("user.dir");
        originalIn = System.in;
        originalOut = System.out;

        System.setProperty("user.dir", tempDir.toString());

        logFilePath = tempDir.resolve("log.txt");
        dictionaryFilePath = tempDir.resolve("words_ru.txt");
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
        System.setProperty("user.dir", originalUserDir);
    }

    @Test
    @DisplayName("Запуск с подсказкой по Enter приводит к победе")
    void mainWinsWhenUserRequestsHelp() throws Exception {
        writeDictionary("маска");
        System.setIn(new ByteArrayInputStream("\n".getBytes(StandardCharsets.UTF_8)));

        Wordle.main(new String[]{logFilePath.toString(), dictionaryFilePath.toString()});

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Привет, это игра \"Wordle\"!"));
        assertTrue(output.contains("маска"));
        assertTrue(output.contains("+++++"));
        assertTrue(output.contains("Поздравляю, ты победил"));
        assertTrue(Files.exists(tempDir.resolve("log.txt")));
    }

    @Test
    @DisplayName("Неверный ввод обрабатывается, потом принимается корректное слово")
    void mainRetriesAfterInvalidInputThenAcceptsValidWord() throws Exception {
        writeDictionary("маска");
        String input = "abc\nмаска\n";
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        Wordle.main(new String[]{logFilePath.toString(), dictionaryFilePath.toString()});

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Длина введенного слова \"abc\" не равна 5"));
        assertTrue(output.contains("Попробуйте еще раз"));
        assertTrue(output.contains("Поздравляю, ты победил"));
    }

    @Test
    @DisplayName("writeLog пишет текст ошибки и стек вызова в файл")
    void writeLogWritesThrowableToFile() throws Exception {
        writeDictionary("водка");
        String input = "q\nслово\nсло_в\n\n";
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        Wordle.main(new String[]{logFilePath.toString(), dictionaryFilePath.toString()});

        String logContent = Files.readString(logFilePath, StandardCharsets.UTF_8);
        assertTrue(logContent.contains("Ошибка: ru.yandex.practicum.InputException: Длина введенного " +
                "слова \"q\" не равна 5"));
        assertTrue(logContent.contains("Ошибка: ru.yandex.practicum.InputException: В словаре нет слова \"слово\""));
        assertTrue(logContent.contains("Ошибка: ru.yandex.practicum.InputException: Слово \"сло_в\" " +
                "содержит недопустимый символ -> _"));
        assertTrue(logContent.contains("Класс: Wordle.java"));
        assertTrue(logContent.contains("Метод: main"));
        assertTrue(logContent.contains("Класс: WordleDictionary.java"));
        assertTrue(logContent.contains("Метод: isWordCorrect"));


        assertTrue(logContent.contains("Строка:"));
    }

    private void writeDictionary(String... words) throws Exception {
        Files.writeString(dictionaryFilePath, String.join(System.lineSeparator(), words), StandardCharsets.UTF_8);
    }
}
