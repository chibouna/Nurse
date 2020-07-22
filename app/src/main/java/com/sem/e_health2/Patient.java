package com.sem.e_health2;

public class Patient {
    private   String name;
    private String lastName;
    private String phone;
    private String age;
    private String imageUri;
    private String namaLastName;

    public Patient(String name, String lastName, String phone, String age) {
        this.name = name;
        this.lastName = lastName;
        this.phone = phone;
        this.age = age;
    }
    public Patient(String name, String lastName, String phone, String age, String imageUri) {
        this.name = name;
        this.lastName = lastName;
        this.phone = phone;
        this.age = age;
        this.imageUri = imageUri;
    }
    public String getnamaLastName() {
        namaLastName =  lastName + " " + name ;
        return namaLastName;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public Patient() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
