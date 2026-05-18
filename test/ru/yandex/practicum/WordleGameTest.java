package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WordleGame Тесты")
class WordleGameTest {

    private WordleDictionary dictionary;
    private WordleGame game;

    @BeforeEach
    void init() {
        dictionary = new WordleDictionary(new ArrayList<>(List.of(
                "маска", "водка", "лампа", "манка", "рамка", "мамка"
        )));
        game = new WordleGame("маска", dictionary);
    }

    @Test
    @DisplayName("set/get userAnswer работают корректно")
    void setAndGetUserAnswer() {
        game.setUserAnswer("рамка");

        assertEquals("рамка", game.getUserAnswer());
        assertEquals("маска", game.getAnswer());
    }

    @Test
    @DisplayName("Полное совпадение возвращает только '+' и завершает игру")
    void getMatchesAllCorrect() {
        game.setUserAnswer("маска");

        String matches = game.getMatches();

        assertEquals("+++++", matches);
        assertTrue(game.isWin());
    }

    @Test
    @DisplayName("Частичное совпадение возвращает смешанный паттерн '+', '^', '-'")
    void getMatchesMixed() {
        game.setUserAnswer("лампа");

        String matches = game.getMatches();

        // м а с к а
        // л а м п а
        // - + ^ - +
        assertEquals("-+^-+", matches);
        assertFalse(game.isWin());
    }

    @Test
    @DisplayName("Повторяющиеся буквы не переучитываются")
    void getMatchesWithRepeatedLetters() {
        game.setUserAnswer("аммма");

        String matches = game.getMatches();

        // answer = м а с к а
        // guess  = а м м м а
        // result = ^ ^ - - +
        assertEquals("^^--+", matches);
    }

    @Test
    @DisplayName("После 6 неудачных попыток игра завершается по шагам")
    void gameFinishesAfterSixSteps() {
        for (int i = 0; i < 6; i++) {
            game.setUserAnswer("лампа");
            game.getMatches();
        }

        assertTrue(game.isWin());
    }

    @Test
    @DisplayName("help возвращает слово и удаляет его из словаря")
    void helpReturnsWordAndRemovesItFromDictionary() {
        String helpWord = game.help();

        assertNotNull(helpWord);
        assertFalse(dictionary.getWordsDictionary().contains(helpWord));
    }

    @Test
    @DisplayName("help после частичного хода учитывает известные ограничения")
    void helpUsesKnownConstraintsAfterGuess() {
        game.setUserAnswer("лампа");
        game.getMatches();

        String helpWord = game.help();

        // После "лампа" для ответа "маска" остаются слова с:
        // - 'а' на позициях 1 и 4
        // - допустимым количеством 'м'
        assertTrue(List.of("маска", "рамка", "манка", "мамка", "лампа").contains(helpWord));
        assertFalse(dictionary.getWordsDictionary().contains(helpWord));
    }

    @Test
    @DisplayName("Повторные буквы не должны давать лишние '^'")
    void repeatedLettersGreenShouldNotOvercount() {
        game.setUserAnswer("мамма");

        String matches = game.getMatches();

        // answer = м а с к а
        // guess  = м а м м а
        // result: + + - - +
        assertEquals("++--+", matches);
    }

    @Test
    @DisplayName("Буква не должна получить '^', если лимит уже исчерпан другими позициями")
    void repeatedLettersShouldRespectGlobalCount() {
        game.setUserAnswer("амама");

        String matches = game.getMatches();

        // answer = м а с к а
        // guess  = а м а м а
        // куыгде: ^ ^ - - +

        assertEquals("^^--+", matches);
    }

    @Test
    @DisplayName("help два раза не возвращает одно и то же слово")
    void helpTwiceReturnsDifferentWords() {

        WordleDictionary localDictionary = new WordleDictionary(new ArrayList<>(List.of(
                "маска", "рамка", "манка", "мамка", "лампа"
        )));
        WordleGame localGame = new WordleGame("маска", localDictionary);

        String first = localGame.help();
        String second = localGame.help();

        assertNotNull(first);
        assertNotNull(second);
        assertNotEquals(first, second);
        assertFalse(localDictionary.getWordsDictionary().contains(first));
        assertFalse(localDictionary.getWordsDictionary().contains(second));
    }

    @Test
    @DisplayName("После 5 неудачных попыток игра не завершена")
    void gameNotFinishedAfterFiveWrongAttempts() {
        for (int i = 0; i < 5; i++) {
            game.setUserAnswer("лампа");
            game.getMatches();
        }

        assertFalse(game.isWin());
    }

}
