package ru.yandex.practicum;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;


@DisplayName("WordleDictionary Тесты")
public class WordleDictionaryTest {
    private WordleDictionary dictionary;

    @BeforeEach
    public void init() {
        List<String> listOfWords = new ArrayList<>(List.of("водка", "лодка", "сарай", "котел"));
        dictionary = new WordleDictionary(listOfWords);
    }

    @Test
    @DisplayName("Случайное слово всегда из словаря")
    void getRandomWordReturnsWordFromDictionary() {
        for (int i = 0; i < 100; i++) {
            String word = dictionary.getRandomWord();
            assertTrue(dictionary.getWordsDictionary().contains(word));
        }
    }

    @Test
    @DisplayName("Удаление слова из словаря")
    public void deleteWord() {
        String word = dictionary.getRandomWord();

        Assertions.assertTrue(dictionary.getWordsDictionary().contains(word));
        dictionary.removeWord(word);

        Assertions.assertFalse(dictionary.getWordsDictionary().contains(word));
    }

    @Test
    @DisplayName("Корректный ввод в нижнем регистре")
    void isWordCorrectValidLowercase() throws InputException {
        String result = dictionary.isWordCorrect("водка");
        assertEquals("водка", result);
    }

    @Test
    @DisplayName("Корректный ввод в верхнем регистре приводится к нижнему")
    void isWordCorrectUppercaseToLowercase() throws InputException {
        String result = dictionary.isWordCorrect("ВОДКА");
        assertEquals("водка", result);
    }

    @Test
    @DisplayName("Слово с пробелами обрезается strip()")
    void isWordCorrectStripsSpaces() throws InputException {
        String result = dictionary.isWordCorrect("  водка  ");
        assertEquals("водка", result);
    }

    @Test
    @DisplayName("Слово с ё заменяется на е")
    void isWordCorrectReplacesYo() throws InputException {
        String result = dictionary.isWordCorrect("котёл");
        assertEquals("котел", result);
    }

    @Test
    @DisplayName("Пустая строка возвращается как есть")
    void isWordCorrectEmptyInput() throws InputException {
        String result = dictionary.isWordCorrect("");
        assertEquals("", result);
    }

    @Test
    @DisplayName("Ошибка при неправильной длине")
    void isWordCorrectWrongLengthThrows() {
        assertThrows(InputException.class, () -> dictionary.isWordCorrect("дом"),
                "Длина введенного слова \"дом\" не равна 5");

    }

    @Test
    @DisplayName("Ошибка при недопустимых символах")
    void isWordCorrectInvalidSymbolsThrows() {
        assertThrows(InputException.class, () -> dictionary.isWordCorrect("во+ка"),
                "Слово \"во+ка\" содержит недопустимый символ -> +");
        assertThrows(InputException.class, () -> dictionary.isWordCorrect("коtел"),
                "Слово \"коtел\" содержит недопустимый символ -> t");
    }

    @Test
    @DisplayName("Ошибка, если слова нет в словаре")
    void isWordCorrectWordNotInDictionaryThrows() {
        assertThrows(InputException.class, () -> dictionary.isWordCorrect("книга"),
                "В словаре нет слова \"книга\"");
    }
}
