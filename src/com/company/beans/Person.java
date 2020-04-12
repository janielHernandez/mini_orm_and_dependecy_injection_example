package com.company.beans;

import com.company.annotations.Column;
import com.company.annotations.PrimaryKey;

public class Person {


    @PrimaryKey(name="id")
    private Long id;

    @Column(name="name")
    private String name;

    @Column(name="age")
    private Integer age;

    public Person(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public Person() {}

    public static Person of(String name, Integer age){
        return new Person(name, age);
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
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
