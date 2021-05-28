package com.barber.app.entities;


public class ShopWorkers {
    private String name;
    private String surname;
    private String workerProfilePictureLink;
    private String workerDesc;

    public ShopWorkers(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getWorkerProfilePictureLink() {
        return workerProfilePictureLink;
    }

    public void setWorkerProfilePictureLink(String workerProfilePictureLink) {
        this.workerProfilePictureLink = workerProfilePictureLink;
    }

    public String getWorkerDesc() {
        return workerDesc;
    }

    public void setWorkerDesc(String workerDesc) {
        this.workerDesc = workerDesc;
    }
}
