package com.striveonger.common.example.app.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Mr.Lee
 * @since 2025-04-26 11:08
 */
public class LRUStorage<T> {
    private final Logger log = LoggerFactory.getLogger(LRUStorage.class);

    private int capacity, size;

    /**
     * 规则:
     * 1. head 为前加入
     * 2. tail 为后加入
     * 3. node 刷新, node回到 tail
     */
    private Node<T> head, tail;

    private final Map<String, Node<T>> map = new ConcurrentHashMap<>();

    public LRUStorage(int capacity) {
        log.info("storage init");
        this.capacity = capacity;
        this.size = 0;
    }

    public T get(String key) {
        if (map.containsKey(key)) {
            Node<T> node = map.get(key);
            // 刷新 Node 链表
            refresh(node);
            return node.value;
        }
        return null;
    }

    public void put(String key, T value) {
        if (map.containsKey(key)) {
            // 更新 Node
            Node<T> node = map.get(key);
            node.value = value;
            // 刷新 Node 链表
            refresh(node);
        } else {
            // 新增 Node
            Node<T> node = new Node<>(key, value);
            map.put(key, node);
            if (size == 0) {
                head = node;
                tail = node;
            } else {
                tail.next = node;
                node.prev = tail;
                tail = node;
            }
            if (++size > capacity) {
                map.remove(head.key);
                head = head.next;
                size--;
            }
        }
    }

    public T remove(String key) {
        Node<T> node = map.get(key);
        if (Objects.nonNull(node)) {
            // P1 删除 tail
            if (node == tail) {
                tail = tail.prev;
                if (tail == null) {
                    head = null;
                } else {
                    tail.next = null;
                }
            } else if (node == head) {
                head = head.next;
                head.prev = null;
            } else {
                Node<T> prev = node.prev;
                Node<T> next = node.next;
                prev.next = next;
                next.prev = prev;
            }
            size--;
            map.remove(key);
            return node.value;
        }
        return null;
    }

    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    public int size() {
        return size;
    }

    public Collection<T> values() {
        return map.values().stream().map(o -> o.value).toList();
    }

    private void refresh(Node<T> node) {
        if (node == tail) {
            // P1 无事发生
            return;
        }

        if (node == head) {
            // P2 更新的是 head
            head = head.next;
            head.prev = null;
        } else {
            // P3 正常更新中间的节点
            Node<T> prev = node.prev;
            Node<T> next = node.next;
            prev.next = next;
            next.prev = prev;
        }
        tail.next = node;
        node.prev = tail;
        tail = node;
        tail.next = null;
    }

    private static class Node<T> {
        private final String key;
        private T value;
        private Node<T> prev;
        private Node<T> next;

        public Node(String key, T value) {
            this.key = key;
            this.value = value;
        }
    }
}
