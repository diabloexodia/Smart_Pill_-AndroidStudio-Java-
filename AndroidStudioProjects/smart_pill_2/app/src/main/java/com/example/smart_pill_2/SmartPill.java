package com.example.smart_pill_2;

import java.util.Objects;
import java.time.format.DateTimeFormatter;

import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.*;
import com.amplifyframework.core.model.temporal.Temporal;

@ModelConfig(pluralName = "SmartPills")
public class SmartPill implements Model {

    private String id;


    private String name;


    private int dosage;

    private Temporal.DateTime time;

    public SmartPill() {
        // Default constructor is required by AWS DataStore
    }

    private SmartPill(String id, String name, int dosage, Temporal.DateTime time) {
        this.id = id;
        this.name = name;
        this.dosage = dosage;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getDosage() {
        return dosage;
    }

    public Temporal.DateTime getTime() {
        return time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDosage(int dosage) {
        this.dosage = dosage;
    }

    public void setTime(Temporal.DateTime time) {
        this.time = time;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SmartPill item = (SmartPill) o;
        return dosage == item.dosage &&
                Objects.equals(id, item.id) &&
                Objects.equals(name, item.name) &&
                Objects.equals(time, item.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, dosage, time);
    }

    @Override
    public String toString() {
        return "SmartPill{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", dosage=" + dosage +
                ", time=" + time +
                '}';
    }

    public static final class Builder {
        private String id;
        private String name;
        private int dosage;
        private Temporal.DateTime time;

        private Builder() {
            id = null;
            name = null;
            dosage = 0;
            time = null;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder dosage(int dosage) {
            this.dosage = dosage;
            return this;
        }

        public Builder time(Temporal.DateTime time) {
            this.time = time;
            return this;
        }

        public SmartPill build() {
            Objects.requireNonNull(name);
            Objects.requireNonNull(time);
            return new SmartPill(id, name, dosage, time);
        }
    }
}
