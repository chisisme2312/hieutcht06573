package com.luonvuituoi.shop;

import com.luonvuituoi.shop.util.DatabaseFontFixer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FontFixTest {

    @Autowired
    private DatabaseFontFixer databaseFontFixer;

    @Test
    void fixFonts() {
        databaseFontFixer.fixProductNames();
    }
}
