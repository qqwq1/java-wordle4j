package ru.yandex.practicum;

import java.util.List;
import java.util.Random;

/*
этот класс содержит в себе список слов List<String>
    его методы похожи на методы списка, но учитывают особенности игры
    также этот класс может содержать рутинные функции по сравнению слов, букв и т.д.
 */
public class WordleDictionary {

    private final List<String> words;
    private static final int LENGTH = 5;

    public WordleDictionary(List<String> words) {
        this.words = words;
    }

    public String getRandomWord() {
        Random random = new Random();
        return words.get(random.nextInt(words.size()));
    }

    public String isWordCorrect(String input) throws InputException {
        String result = input.strip().toLowerCase().replace("ё", "е");
        if (input.isEmpty()) {
            return input;
        }
        if (result.length() != LENGTH) {
            throw new InputException("Длина введенного слова \"" + input + "\" не равна " + LENGTH);
        }
        for (int i = 0; i < result.length(); i++) {
            if (!((result.charAt(i) >= 1072 && result.charAt(i) <= 1103))) {
                throw new InputException("Слово \"" + input + "\" содержит недопустимый символ -> " + result.charAt(i));
            }
        }
        if (!words.contains(result)) {
            throw new InputException("В словаре нет слова \"" + result + "\"");
        }
        return result;
    }

    public List<String> getWordsDictionary() {
        return List.copyOf(words);
    }

    public void removeWord(String word) {
        words.remove(word);
    }
}
