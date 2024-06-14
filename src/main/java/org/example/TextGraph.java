package org.example;
import java.awt.Desktop;
import java.io.*;
import java.util.*;


public class TextGraph {
    private Map<String, Map<String, Integer>> graph = new HashMap<>();
    private List<String> words = new ArrayList<>();

    private static final Set<Character> delimiters = new HashSet<>(Arrays.asList(',', '.', '!', '(', ')', '-', '+', '=', '[', ']', '{', '}', ':', ';', '"', '\'', '<', '>', '?'));
    private static final Set<String> mermaidKeywords = new HashSet<>(Arrays.asList("graph", "subgraph", "end", "style", "click", "direction", "fill", "stroke"));

    public TextGraph(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            StringBuilder textBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                textBuilder.append(line).append(" ");
            }
            String text = textBuilder.toString();
            setGraph(text);
            saveGraph();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> breakIntoWords(String text) {
        List<String> words = new ArrayList<>();
        StringBuilder wordBuilder = new StringBuilder();

        // 标点符号替换为空格
        for (char c : text.toCharArray()) {
            if (delimiters.contains(c)) {
                c = ' ';
            } else {
                c = Character.toLowerCase(c);
            }
            wordBuilder.append(c);
        }

        String[] rawWords = wordBuilder.toString().split("\\s+");
        for (String rawWord : rawWords) {
            StringBuilder processedWord = new StringBuilder();
            for (char c : rawWord.toCharArray()) {
                if (Character.isAlphabetic(c)) {
                    processedWord.append(c);
                }
            }
            if (processedWord.length() > 0) {
                words.add(processedWord.toString());
            }
        }

        return words;
    }

    private void setGraph(String text) {
        words = breakIntoWords(text);

        for (int i = 0; i < words.size() - 1; ++i) {
            graph.putIfAbsent(words.get(i), new HashMap<>());
            graph.get(words.get(i)).merge(words.get(i + 1), 1, Integer::sum);
        }
    }


    private void saveGraph() {
        try (PrintWriter writer = new PrintWriter("out.mmd")) {
            writer.println("graph LR");
            for (Map.Entry<String, Map<String, Integer>> entry : graph.entrySet()) {
                for (Map.Entry<String, Integer> neighbor : entry.getValue().entrySet()) {
                    writer.printf("%s-->|%d|%s%n", entry.getKey(), neighbor.getValue(), neighbor.getKey());
                }
            }
            System.out.println("Graph generated successfully!");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", "D:\\npm-global\\mmdc.cmd", "-i", "out.mmd", "-o", "output.svg");
            processBuilder.directory(new File(System.getProperty("user.dir")));
            Process process = processBuilder.start();

            // Capture the output and error streams for debugging
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("mmdc output: " + line);
            }
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                System.out.println("mmdc error: " + line);
            }

            process.waitFor();
            System.out.println("Mermaid diagram generated successfully!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println("Error running mmdc command. Ensure that Mermaid CLI is installed and available in your system PATH.");
        }

        // Check if the SVG file was generated
        File svgFile = new File("output.svg");
        if (svgFile.exists()) {
            System.out.println("SVG file exists: " + svgFile.getAbsolutePath());
        } else {
            System.out.println("SVG file does not exist.");
        }
    }



    public void showGraph() {
        try {
            File svgFile = new File("output.svg");
            if (svgFile.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(svgFile.toURI());
                } else {
                    System.out.println("Desktop is not supported. Please open the SVG file manually: " + svgFile.getAbsolutePath());
                }
            } else {
                System.out.println("SVG file does not exist.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public List<String> queryBridgeWords(String word1, String word2) {
        List<String> bridgeWords = new ArrayList<>();
        if (!graph.containsKey(word1) || !graph.containsKey(word2)) {
            bridgeWords.add("No " + word1 + " or " + word2 + " in the graph!");
            return bridgeWords;
        }

        Map<String, Integer> word1Neighbors = graph.get(word1);
        if (word1Neighbors == null) {
            bridgeWords.add("No " + word1 + " in the graph!");
            return bridgeWords;
        }

        for (Map.Entry<String, Integer> entry : word1Neighbors.entrySet()) {
            String word3 = entry.getKey();
            if (graph.containsKey(word3) && graph.get(word3).containsKey(word2)) {
                bridgeWords.add(word3);
            }
        }

        if (bridgeWords.isEmpty()) {
            bridgeWords.add("No bridge words from " + word1 + " to " + word2 + "!");
        }
        return bridgeWords;
    }


    public String generateNewText(String text) {
        List<String> newWords = breakIntoWords(text);
        StringBuilder newText = new StringBuilder();
        String bridgeWord;

        for (int i = 0; i < newWords.size() - 1; ++i) {
            newText.append(newWords.get(i)).append(" ");
            if (graph.containsKey(newWords.get(i))) {
                bridgeWord = queryBridgeWords(newWords.get(i), newWords.get(i + 1)).get(0);
                if (!bridgeWord.startsWith("No")) {
                    newText.append(bridgeWord).append(" ");
                }
            }
        }
        newText.append(newWords.get(newWords.size() - 1));
        return newText.toString();
    }

    public List<String> calcShortestPath(String word1, String word2) {
        List<String> path = new ArrayList<>();
        if (!graph.containsKey(word1) || !graph.containsKey(word2)) {
            return path;
        }

        Map<String, String> parent = new HashMap<>();
        Map<String, Integer> dist = new HashMap<>();
        for (String word : words) {
            parent.put(word, word);
            dist.put(word, Integer.MAX_VALUE);
        }
        dist.put(word1, 0);
        Set<String> visited = new HashSet<>();
        String u = word1;

        while (!u.equals(word2)) {
            visited.add(u);
            for (Map.Entry<String, Integer> entry : graph.get(u).entrySet()) {
                String v = entry.getKey();
                int weight = entry.getValue();
                if (dist.get(u) + weight < dist.get(v)) {
                    dist.put(v, dist.get(u) + weight);
                    parent.put(v, u);
                }
            }

            int minDist = Integer.MAX_VALUE;
            for (String word : words) {
                if (!visited.contains(word) && dist.get(word) < minDist) {
                    minDist = dist.get(word);
                    u = word;
                }
            }
            if (minDist == Integer.MAX_VALUE) {
                return path;
            }
        }

        while (!u.equals(word1)) {
            path.add(u);
            u = parent.get(u);
        }
        path.add(word1);
        Collections.reverse(path);
        return path;
    }

    public String randomWalk() {
        Random rand = new Random();
        int index = rand.nextInt(words.size());
        String word, next = words.get(index);
        List<Map.Entry<String, String>> edges = new ArrayList<>();

        while (true) {
            word = next;
            if (!graph.containsKey(word) || graph.get(word).isEmpty()) {
                break;
            }
            index = rand.nextInt(graph.get(word).size());
            Iterator<Map.Entry<String, Integer>> it = graph.get(word).entrySet().iterator();
            for (int i = 0; i < index; ++i) {
                it.next();
            }
            next = it.next().getKey();
            Map.Entry<String, String> edge = new AbstractMap.SimpleEntry<>(word, next);
            if (edges.contains(edge)) {
                edges.add(edge);
                break;
            }
            edges.add(edge);
        }

        StringBuilder path = new StringBuilder();
        for (Map.Entry<String, String> edge : edges) {
            path.append(edge.getKey()).append(" ");
        }
        return path.append(edges.isEmpty() ? word : edges.get(edges.size() - 1).getValue()).toString();
    }
}
