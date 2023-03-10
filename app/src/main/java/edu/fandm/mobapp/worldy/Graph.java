package edu.fandm.mobapp.worldy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

public class Graph {
    private Map<String, Set<String>> adjacencyMap;
    private Map<Integer,String> wordDict = new HashMap<>();

    public Graph() {
        adjacencyMap = new HashMap<>();
        populateWords();
        for (String word : wordDict.values()) {
            adjacencyMap.put(word, new HashSet<>());
        }
        buildGraph();
    }

    private void populateWords(){
        try {
            InputStream is = Objects.requireNonNull(getClass().getClassLoader()).getResourceAsStream("assets/words_simple.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = reader.readLine();
            int i = 0;
            while (line != null) {
                if (line.length() == 4) {
                    wordDict.put(i, line);
                    i++;
                }
                line = reader.readLine();
            }
            is.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildGraph() {
        for (String word1 : wordDict.values()) {
            for (String word2 : wordDict.values()) {
                if (!word1.equals(word2) && differByOneLetter(word1, word2)) {
                    Objects.requireNonNull(adjacencyMap.get(word1)).add(word2);
                    Objects.requireNonNull(adjacencyMap.get(word2)).add(word1);
                }
            }
        }
    }

    private boolean differByOneLetter(String word1, String word2) {
        if (word1.length() != word2.length()) {
            return false;
        }
        int diffCount = 0;
        for (int i = 0; i < word1.length(); i++) {
            if (word1.charAt(i) != word2.charAt(i)) {
                diffCount++;
            }
            if (diffCount > 1) {
                return false;
            }
        }
        return diffCount == 1;
    }

    private Set<String> getAdjacentWords(String word) {
        return adjacencyMap.get(word);
    }

    public Map<Integer,String> getWordDict(){
        return wordDict;
    }

    public List<String> getPath(String start, String end){
        if (!wordDict.containsValue(start) || !wordDict.containsValue(end)) {
            return null;
        }
        if (start.equals(end)) {
            return new ArrayList<>(Collections.singletonList(start));
        }
        Queue<String> queue = new ArrayDeque<>();
        Map<String, String> prev = new HashMap<>();
        Set<String> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            String current = queue.remove();
            for (String neighbour : getAdjacentWords(current)) {
                if (!visited.contains(neighbour)) {
                    queue.add(neighbour);
                    visited.add(neighbour);
                    prev.put(neighbour, current);
                }
            }
        }
        if (!prev.containsKey(end)) {
            return null;
        }
        List<String> path = new ArrayList<>();
        String current = end;
        while (current != null) {
            path.add(current);
            current = prev.get(current);
        }
        Collections.reverse(path);
        return path;
    }
}

