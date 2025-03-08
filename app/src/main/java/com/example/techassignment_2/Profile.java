package com.example.techassignment_2;

public class Profile {
    private int id;
    private String name;
    private String surname;
    private float gpa;

    // ✅ Constructor
    public Profile(int id, String name, String surname, float gpa) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.gpa = gpa;
    }

    // ✅ Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public float getGpa() {
        return gpa;
    }

    // ✅ Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setGpa(float gpa) {
        this.gpa = gpa;
    }
}
