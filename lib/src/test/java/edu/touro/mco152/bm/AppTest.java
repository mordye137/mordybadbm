package edu.touro.mco152.bm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppTest {

    /**
     *Forcing Error Conditions - Make sure method throws a null pointer exception when passed a null value
     */
    @Test
    void updateMetrics() {
        assertThrows(NullPointerException.class, () -> {App.updateMetrics(null);});
    }
}