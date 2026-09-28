package com.mentorconnect.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "interest_tags")
public class InterestTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    public InterestTag() {
    }

    public InterestTag(String name) {
        this.name = name != null ? name.trim() : null;
    }

    public InterestTag(Long id, String name) {
        this.id = id;
        this.name = name != null ? name.trim() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name.trim() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InterestTag that)) return false;
        return Objects.equals(id, that.id) || (name != null && name.equalsIgnoreCase(that.name));
    }

    @Override
    public int hashCode() {
        return Objects.hash(name != null ? name.toLowerCase() : id);
    }

    @Override
    public String toString() {
        return "InterestTag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
