package com.airtribe.meditrack.entity;

public class Person {

    private int p_id;
    private int age;
    private String F_name;
    private String L_name;
    private Gender gender;

    public Person(int p_id, int age, String f_name, String l_name, Gender gender) {
        this.p_id = p_id;
        this.age = age;
        F_name = f_name;
        L_name = l_name;
        this.gender = gender;
    }

    public int getP_id() {
        return p_id;
    }

    public void setP_id(int p_id) {
        this.p_id = p_id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getF_name() {
        return F_name;
    }

    public void setF_name(String f_name) {
        F_name = f_name;
    }

    public String getL_name() {
        return L_name;
    }

    public void setL_name(String l_name) {
        L_name = l_name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
}
