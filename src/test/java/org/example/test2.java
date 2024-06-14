package org.example;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class test2 {

    @Test
    void testQueryBridgeWords_noBridgeWords_between() {
        TextGraph tg = new TextGraph("input.txt");
        List<String> result = tg.queryBridgeWords("new", "worlds");
        assertEquals(Collections.singletonList("No bridge words from new to worlds!"), result);
    }

    @Test
    void testQueryBridgeWords_withBridgeWords() {
        TextGraph tg = new TextGraph("input.txt");
        List<String> result = tg.queryBridgeWords("and", "life");
        assertEquals(Arrays.asList("new"), result);
    }

    @Test
    void testQueryBridgeWords_noBridgeWords() {
        TextGraph tg = new TextGraph("input.txt");
        List<String> result = tg.queryBridgeWords("nonexistent", "word");
        assertEquals(Collections.singletonList("No nonexistent or word in the graph!"), result);
    }

    @Test
    void testQueryBridgeWords_withBridgeWords_multiple() {
        TextGraph tg = new TextGraph("input.txt");
        List<String> result = tg.queryBridgeWords("explore", "new");
        assertEquals(Arrays.asList("strange"), result);
    }
}
