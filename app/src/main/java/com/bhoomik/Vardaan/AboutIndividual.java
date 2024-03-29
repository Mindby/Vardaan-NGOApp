package com.bhoomik.Vardaan;

public class AboutIndividual {
    private String id;
    private String name;
    private int imageName;
    private String branch;

    public static final int TYPE_HUMAN = 0;
    public static final int TYPE_LINK = 1;

    public AboutIndividual(String id, String name, int imageName,String branch) {
        this.id = id;
        this.name = name;
        this.imageName = imageName;
        this.branch = branch;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageName() {
        return imageName;
    }

    public void setImageName(int imageName) {
        this.imageName = imageName;
    }

    public String getBranch() {
        return branch;
    }

//    public void setBranch(String branch) {
//        this.branch = branch;
//    }
}