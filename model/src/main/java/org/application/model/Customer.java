package org.application.model;

import org.application.FileUtil;
import org.application.dto.FileDTO;

import java.util.List;

public class Customer implements Model {

    private int id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private int purchase;
    private File file;
    //private int fileId;

    public Customer() {
    }

    public Customer(int id, String firstName, String lastName, String phoneNumber, int purchase, int fileId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.purchase = purchase;
        this.file = new File();
        this.file.setId(fileId);
    }

    public Customer(int id, String firstName, String lastName, String phoneNumber, int purchase, int fileId, String fileName, byte[] content) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.purchase = purchase;
        this.file = new File();
        this.file.setId(fileId);
        this.file.setFilename(fileName);
        this.file.setContent(content);
    }

    public Customer(int id, String firstName, String lastName, String phoneNumber, int purchase, int fileId, List<FileDTO> photos) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.purchase = purchase;
        this.file = new File();
        this.file.setId(fileId);
        if (photos != null && photos.size() > 0) {
            this.file.setFilename(photos.get(0).getFileName());
            this.file.setContent(photos.get(0).getContent());
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getPurchase() {
        return purchase;
    }

    public void setPurchase(int purchase) {
        this.purchase = purchase;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
