package edu.touro.mco152.bm;

import edu.touro.mco152.bm.persist.DiskRun;
import org.eclipse.persistence.jpa.jpql.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest {

    /**
     * Right - Make sure the method returns a number within teh correct range
     * Range - Make sure method can accept a number like -5 or 2000
     */
    @ParameterizedTest
    @ValueSource(ints = { 1, 10, 30, 50, 70, 90, 100 })
    void randInt(int candidate) {
        int testNegative = Util.randInt(-5, 0);
        int testBigNumber = Util.randInt(0, 2000);

        assertTrue(testNegative >= -5 && testNegative <= 0);
        assertTrue(testBigNumber >= 0 && testBigNumber <= 2000);
        assertTrue(Util.randInt(0, candidate) >= 0 && Util.randInt(0, candidate) <= 100);
    }

    /**
     * Cross-Checking - checking the result of the method against our system
     * Reference - We reference the external file system to make sure the method works correctly
     * Performance - make sure the method does not take too long, more than 2 seconds
     */
    @Test
    void deleteDirectory() {
        String newPath = "C:\\Users\\mordy\\IdeaProjects\\mordybadbm\\lib\\src\\test\\resources\\foldertodelete";
        File fileToDelete = new File("C:\\Users\\mordy\\IdeaProjects\\mordybadbm\\lib\\src\\test\\resources\\foldertodelete\\deleteMe.txt");
        File folderToDelete = new File(newPath);
        folderToDelete.mkdir();

        long startTime = System.currentTimeMillis();
        File path = new File("C:\\Users\\mordy\\IdeaProjects\\mordybadbm\\lib\\src\\test\\resources\\foldertodelete");
        long endTime = System.currentTimeMillis();
        boolean fileExists = fileToDelete.exists();

        assertTrue(Util.deleteDirectory(path));
        assertTrue((endTime - startTime)  < 2);
        assertFalse(fileExists);
    }
}