package org.example;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class TextGraphTest {

    @Test
    void testQueryBridgeWords_noBridgeWords() {
        TextGraph tg = new TextGraph("input.txt");
        List<String> result = tg.queryBridgeWords("nonexistent", "word");
        assertEquals(Collections.singletonList("No nonexistent or word in the graph!"), result);
    }

    @Test
    void testQueryBridgeWords_withBridgeWords() {
        TextGraph tg = new TextGraph("input.txt");
        // Assuming "word1" -> "bridge" -> "word2"
        List<String> result = tg.queryBridgeWords("word1", "word2");
        assertEquals(Arrays.asList("bridge"), result);
    }

    @Test
    void testQueryBridgeWords_noBridgeWords_between() {
        TextGraph tg = new TextGraph("input.txt");
        List<String> result = tg.queryBridgeWords("word1", "word2");
        assertEquals(Collections.singletonList("No bridge words from word1 to word2!"), result);
    }

    @Test
    void testQueryBridgeWords_oneWordNotExist() {
        TextGraph tg = new TextGraph("input.txt");
        List<String> result = tg.queryBridgeWords("word1", "nonexistent");
        assertEquals(Collections.singletonList("No word1 or nonexistent in the graph!"), result);
    }

    @Test
    void testQueryBridgeWords_emptyInput() {
        TextGraph tg = new TextGraph("input.txt");
        List<String> result = tg.queryBridgeWords("", "word2");
        assertEquals(Collections.singletonList("No  or word2 in the graph!"), result);
    }
}
