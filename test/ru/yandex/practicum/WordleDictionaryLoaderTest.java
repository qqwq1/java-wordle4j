package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WordleDictionaryLoader Тесты")
public class WordleDictionaryLoaderTest {

    @TempDir
    Path tempDir;

    private File logFile;
    private WordleDictionaryLoader loader;

    @BeforeEach
    public void setUp() throws IOException {
        logFile = tempDir.resolve("test.log").toFile();
        logFile.createNewFile();
    }

    @Test
    @DisplayName("Загрузка валидного словаря с русскими словами")
    public void testLoadValidDictionary() throws IOException {
        List<String> testWords = List.of(
                "водка",
                "коала",
                "томик"
        );
        Path dictFile = createTestFile(testWords);

        loader = new WordleDictionaryLoader(logFile, dictFile);
        WordleDictionary dictionary = loader.loadWordleDictionary();

        assertNotNull(dictionary);
        assertEquals(3, dictionary.getWordsDictionary().size());
        assertTrue(dictionary.getWordsDictionary().contains("водка"));
        assertTrue(dictionary.getWordsDictionary().contains("коала"));
        assertTrue(dictionary.getWordsDictionary().contains("томик"));
    }

    @Test
    @DisplayName("Фильтрация слов неправильной длины")
    public void testFilterWordsWithWrongLength() throws IOException {
        List<String> testWords = List.of(
                "привет",
                "тест",
                "пример",
                "слово",
                "кошка"
        );
        Path dictFile = createTestFile(testWords);


        loader = new WordleDictionaryLoader(logFile, dictFile);
        WordleDictionary dictionary = loader.loadWordleDictionary();


        assertEquals(2, dictionary.getWordsDictionary().size());
        assertTrue(dictionary.getWordsDictionary().contains("слово"));
        assertTrue(dictionary.getWordsDictionary().contains("кошка"));
        assertFalse(dictionary.getWordsDictionary().contains("привет"));
    }

    @Test
    @DisplayName("Замена буквы 'ё' на 'е'")
    public void testReplaceYoWithE() throws IOException {
        List<String> testWords = List.of(
                "ёпрст",
                "твёрдо"
        );
        Path dictFile = createTestFile(testWords);


        loader = new WordleDictionaryLoader(logFile, dictFile);
        WordleDictionary dictionary = loader.loadWordleDictionary();


        assertEquals(1, dictionary.getWordsDictionary().size());
        assertTrue(dictionary.getWordsDictionary().contains("епрст"));
        assertFalse(dictionary.getWordsDictionary().contains("ёпрст"));
    }

    @Test
    @DisplayName("Фильтрация слов с не-кириллическими символами")
    public void testFilterNonCyrillicCharacters() throws IOException {
        List<String> testWords = List.of(
                "слово",
                "слов1",
                "слов+",
                "слов=",
                "слов_",
                "english"
        );
        Path dictFile = createTestFile(testWords);


        loader = new WordleDictionaryLoader(logFile, dictFile);
        WordleDictionary dictionary = loader.loadWordleDictionary();


        assertEquals(1, dictionary.getWordsDictionary().size());
        assertTrue(dictionary.getWordsDictionary().contains("слово"));
    }

    @Test
    @DisplayName("Преобразование в нижний регистр")
    public void testConvertToLowercase() throws IOException {

        List<String> testWords = List.of(
                "СЛОВО",
                "ТестЫ",
                "ВоДкА"
        );
        Path dictFile = createTestFile(testWords);


        loader = new WordleDictionaryLoader(logFile, dictFile);
        WordleDictionary dictionary = loader.loadWordleDictionary();


        assertEquals(3, dictionary.getWordsDictionary().size());
        assertTrue(dictionary.getWordsDictionary().contains("слово"));
        assertTrue(dictionary.getWordsDictionary().contains("тесты"));
        assertTrue(dictionary.getWordsDictionary().contains("водка"));
    }

    @Test
    @DisplayName("Удаление пробелов в начале и конце")
    public void testStripWhitespace() throws IOException {

        List<String> testWords = List.of(
                "  слово  ",
                "\tтесты\t",
                "\nводка\n"
        );
        Path dictFile = createTestFile(testWords);


        loader = new WordleDictionaryLoader(logFile, dictFile);
        WordleDictionary dictionary = loader.loadWordleDictionary();


        assertEquals(3, dictionary.getWordsDictionary().size());
        assertTrue(dictionary.getWordsDictionary().contains("слово"));
        assertTrue(dictionary.getWordsDictionary().contains("тесты"));
        assertTrue(dictionary.getWordsDictionary().contains("водка"));
    }

    @Test
    @DisplayName("Обработка пустых строк")
    public void testSkipEmptyLines() throws IOException {

        List<String> testWords = List.of(
                "слово",
                "",
                "тесты",
                "   ",
                "водка"
        );
        Path dictFile = createTestFile(testWords);


        loader = new WordleDictionaryLoader(logFile, dictFile);
        WordleDictionary dictionary = loader.loadWordleDictionary();


        assertEquals(3, dictionary.getWordsDictionary().size());
        assertTrue(dictionary.getWordsDictionary().contains("слово"));
        assertTrue(dictionary.getWordsDictionary().contains("тесты"));
        assertTrue(dictionary.getWordsDictionary().contains("водка"));
    }

    @Test
    @DisplayName("Удаление дубликатов (Set)")
    public void testRemoveDuplicates() throws IOException {

        List<String> testWords = List.of(
                "слово",
                "СЛОВО",  // тот же слово в нижнем регистре
                "тесты",
                "теËсты", // дубликат
                "водка"
        );
        Path dictFile = createTestFile(testWords);


        loader = new WordleDictionaryLoader(logFile, dictFile);
        WordleDictionary dictionary = loader.loadWordleDictionary();


        assertEquals(3, dictionary.getWordsDictionary().size());
        assertTrue(dictionary.getWordsDictionary().contains("слово"));
        assertTrue(dictionary.getWordsDictionary().contains("тесты"));
        assertTrue(dictionary.getWordsDictionary().contains("водка"));
    }

    @Test
    @DisplayName("Возврат непустого словаря для валидного файла")
    public void testReturnNonEmptyDictionary() throws IOException {

        List<String> testWords = List.of("слово", "тестов");
        Path dictFile = createTestFile(testWords);


        loader = new WordleDictionaryLoader(logFile, dictFile);
        WordleDictionary dictionary = loader.loadWordleDictionary();


        assertNotNull(dictionary);
        assertFalse(dictionary.getWordsDictionary().isEmpty());
    }

    @Test
    @DisplayName("Возврат пустого словаря при отсутствии валидных слов")
    public void testReturnEmptyDictionaryWhenNoValidWords() throws IOException {

        List<String> testWords = List.of(
                "ab",
                "verylongword",
                "word1",
                "!!!!"
        );
        Path dictFile = createTestFile(testWords);


        loader = new WordleDictionaryLoader(logFile, dictFile);
        WordleDictionary dictionary = loader.loadWordleDictionary();


        assertNotNull(dictionary);
        assertTrue(dictionary.getWordsDictionary().isEmpty());
    }


    @Test
    @DisplayName("Обработка файла с только невалидными словами")
    public void testFileWithOnlyInvalidWords() throws IOException {

        List<String> testWords = List.of(
                "ab",
                "xyz",
                "123",
                "!!!"
        );
        Path dictFile = createTestFile(testWords);


        loader = new WordleDictionaryLoader(logFile, dictFile);
        WordleDictionary dictionary = loader.loadWordleDictionary();


        assertNotNull(dictionary);
        assertTrue(dictionary.getWordsDictionary().isEmpty());
    }

    @Test
    @DisplayName("Возврат WordleDictionary не null")
    public void testLoadWordleDictionaryNotNull() throws IOException {

        Path dictFile = createTestFile(List.of("слово"));


        loader = new WordleDictionaryLoader(logFile, dictFile);
        WordleDictionary dictionary = loader.loadWordleDictionary();


        assertNotNull(dictionary);
    }


    private Path createTestFile(List<String> words) throws IOException {
        Path testFile = tempDir.resolve("test_dictionary.txt");
        String content = String.join("\n", words);
        Files.writeString(testFile, content, StandardCharsets.UTF_8);
        return testFile;
    }
}
