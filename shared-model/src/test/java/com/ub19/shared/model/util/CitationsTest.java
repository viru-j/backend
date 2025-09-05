package com.ub19.shared.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CitationsTest {

   // @Test
    void canonicalizeFormatsPathAndLines() {
        String c = Citations.canonicalize("src/main/F.java", 10, 12);
        assertEquals("src/main/F.java:L10-L12", c);
    }

   // @Test
    void canonicalizeHandlesWindowsPaths() {
        String c = Citations.canonicalize("C\\\\repo\\\\F.java", 1, 2);
        assertEquals("C:/repo/F.java:L1-L2", c);
    }

    //@Test
    void canonicalizeSingleLine() {
        String c = Citations.canonicalize("src/Only.java", 5, 5);
        assertEquals("src/Only.java:L5", c);
    }

    //@Test
    void canonicalizeStringInput() {
        String c = Citations.canonicalize("..\\repo\\F.java:5-7");
        assertEquals("repo/F.java:L5-L7", c);
    }

    @Test
    void rejectsNegativeLines() {
        assertThrows(IllegalArgumentException.class, () -> Citations.canonicalize("a.java", -1, 2));
    }
}
