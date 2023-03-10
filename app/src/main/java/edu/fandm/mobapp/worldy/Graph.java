package edu.fandm.mobapp.worldy;

import static androidx.core.content.FileProvider.buildPath;

import androidx.collection.ArraySet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Graph {
    private Map<String, Set<String>> adjacencyMap;
    private List<String> words = new ArrayList<>();

    public Graph() {
        adjacencyMap = new HashMap<>();
        populateWords();
        for (String word : words) {
            adjacencyMap.put(word, new HashSet<>());
        }
        buildGraph();
    }

    private void populateWords(){
        try {
            InputStream is = Objects.requireNonNull(getClass().getClassLoader()).getResourceAsStream("assets/words.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = reader.readLine();
            while (line != null) {
                words.add(line);
                line = reader.readLine();
            }
            is.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildGraph() {
        for (int i = 0; i < words.size(); i++) {
            String word1 = words.get(i);
            for (int j = i + 1; j < words.size(); j++) {
                String word2 = words.get(j);
                if (differByOneLetter(word1, word2)) {
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

    public List<String> getPath(String start, String end){
        if (!words.contains(start) || !words.contains(end)) {
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

