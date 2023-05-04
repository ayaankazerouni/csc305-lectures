package model;

import java.util.Objects;

public class Binding {
    private String name;
    private double value;

    public Binding(String name, double value) {
        this.name = Objects.requireNonNull(name);
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
