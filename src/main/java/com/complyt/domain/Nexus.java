package com.complyt.domain;


import java.util.List;

public class Nexus {
    private String type;
    private List<NexusRule> rules;

    public String getType() {
        return type;
    }

    public List<NexusRule> getRules() {
        return rules;
    }

    public Nexus(String type, List<NexusRule> rules) {
        this.type = type;
        this.rules = rules;
    }

    @Override
    public String toString() {
        return "Nexus{" +
                "type='" + type + '\'' +
                ", rules=" + rules +
                '}';
    }
}
