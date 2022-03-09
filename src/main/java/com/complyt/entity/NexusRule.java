package com.complyt.entity;

public class NexusRule {
    private String type;
    private int value;

    public String getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    public NexusRule(String type, int value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return "NexusRule{" +
                "type='" + type + '\'' +
                ", value=" + value +
                '}';
    }
}
