package ru.yandex.practicum;

import java.util.*;

/*
в этом классе хранится словарь и состояние игры
    текущий шаг
    всё что пользователь вводил
    правильный ответ

в этом классе нужны методы, которые
    проанализируют совпадение слова с ответом
    предложат слово-подсказку с учётом всего, что вводил пользователь ранее

не забудьте про специальные типы исключений для игровых и неигровых ошибок
 */
public class WordleGame {

    private final String answer;
    private String userAnswer;
    private final Map<Character, Integer> answerMap;
    private final Map<Character, Integer> userAnswerMap;
    private final char[] correctLettersByPosition;
    private int maxSTEPS = 6;
    private final WordleDictionary dictionary;

    public WordleGame(String answer, WordleDictionary dictionary) {
        this.answer = answer;
        this.dictionary = dictionary;
        answerMap = answerToMap();
        correctLettersByPosition = new char[answer.length()];
        Arrays.fill(correctLettersByPosition, '_');
        userAnswerMap = new HashMap<>();
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public String getAnswer() {
        return answer;
    }

    public String getMatches() {

        StringBuilder matches = new StringBuilder("-----");
        Map<Character, Integer> map = new HashMap<>(answerMap);

        for (int i = 0; i < answer.length(); i++) {
            if (userAnswer.charAt(i) == answer.charAt(i)) {
                matches.setCharAt(i, '+');
                map.merge(userAnswer.charAt(i), -1, Integer::sum);
                correctLettersByPosition[i] = userAnswer.charAt(i);
            }
        }
        for (int i = 0; i < answer.length(); i++) {
            if (map.containsKey(userAnswer.charAt(i)) && !map.get(userAnswer.charAt(i)).equals(0)
                    && matches.charAt(i) != '+') {
                map.merge(userAnswer.charAt(i), -1, Integer::sum);
                matches.setCharAt(i, '^');
            }
        }
        calculateUserAnswerMap(map);
        maxSTEPS--;
        return matches.toString();
    }

    private Map<Character, Integer> answerToMap() {
        Map<Character, Integer> map = new HashMap<>();
        for (int i = 0; i < answer.length(); i++) {
            map.merge(answer.charAt(i), 1, Integer::sum);
        }
        return map;
    }

    private void calculateUserAnswerMap(Map<Character, Integer> map) {
        for (Map.Entry<Character, Integer> entry : map.entrySet()) {
            if (!answerMap.get(entry.getKey()).equals(entry.getValue())) {
                userAnswerMap.put(entry.getKey(), answerMap.get(entry.getKey()) - entry.getValue());
            }
        }
    }

    public String help() {
        List<String> suitableWords = new ArrayList<>();
        List<String> dict = dictionary.getWordsDictionary();
        boolean isSutableLetters;


        for (String word : dict) {
            isSutableLetters = true;
            for (int i = 0; i < correctLettersByPosition.length; i++) {
                if (correctLettersByPosition[i] != '_' && word.charAt(i) != correctLettersByPosition[i]) {
                    isSutableLetters = false;
                    break;
                }
            }
            if (isSutableLetters) {
                for (Map.Entry<Character, Integer> entry : userAnswerMap.entrySet()) {
                    int answerLetterEntrance = answerMap.get(entry.getKey());
                    int wordLetterEntrance = countLetterEntrance(entry.getKey(), word);

                    if (wordLetterEntrance > answerLetterEntrance || wordLetterEntrance < entry.getValue()) {
                        isSutableLetters = false;
                        break;
                    }
                }
            }
            if (isSutableLetters) {
                suitableWords.add(word);
            }
        }
        Random random = new Random();
        String helpWord = suitableWords.get(random.nextInt(suitableWords.size()));
        dictionary.removeWord(helpWord);
        return helpWord;
    }

    private int countLetterEntrance(char letter, String word) {
        int idx = 0;
        int counter = 0;
        while ((idx = word.indexOf(letter, idx)) != -1) {
            counter++;
            idx++;
        }
        return counter;
    }

    public boolean isWin() {
        return maxSTEPS == 0 || userAnswer.equals(answer);
    }
}
