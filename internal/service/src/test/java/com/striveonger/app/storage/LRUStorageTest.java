package com.striveonger.app.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Mr.Lee
 * @since 2025-04-26 11:59:03
 */
public class LRUStorageTest {

    private LRUStorage<String> storage;

    @BeforeEach
    public void setup() {
        storage = new LRUStorage<>(3);
    }

    @Test
    public void test() {
        // A B C
        storage.put("A", "1");
        storage.put("B", "2");
        storage.put("C", "3");

        // B C A
        System.out.println(storage.get("A"));
        // B A C
        System.out.println(storage.get("C"));
        // A C D
        storage.put("D", "4");
        // C D A
        storage.put("A", "5");
    }

}