package org.application.desktop.dto;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.application.FileUtil;
import org.application.model.Customer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

public class CustomerDTO {

    private int id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private int purchase;
    int fileId;
    String filename;
    private List<byte[]> photos;


    public CustomerDTO() {
    }

    public CustomerDTO(String firstName, String lastName, String phoneNumber, int purchase, List<File> photos) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.purchase = purchase;
        this.filename = filename;
        if (photos != null && photos.size() > 0) {
            this.filename = photos.get(0).getName();
            this.photos = List.of(FileUtil.readFileToByteArray(photos.get(0)));
        }
    }

    public CustomerDTO(int id, String firstName, String lastName, String phoneNumber, int purchase, int fileId, List<File> photos) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.purchase = purchase;
        this.fileId = fileId;
        if (photos != null && photos.size() > 0) {
            this.filename = photos.get(0).getName();
            this.photos = List.of(FileUtil.readFileToByteArray(photos.get(0)));
        }
    }

    public CustomerDTO(int id, String firstName, String lastName, String phoneNumber, int purchase, int fileId, String filename, List<byte[]> photos) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.purchase = purchase;
        this.fileId = fileId;
        this.filename = filename;
        this.photos = photos;
    }

    public CustomerDTO(int id, String firstName, String lastName, String phoneNumber, int purchase, int fileId, String filename, byte[] photo) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.purchase = purchase;
        this.fileId = fileId;
        this.filename = filename;
        this.photos = List.of(photo);
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

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public List<byte[]> getPhotos() {
        return photos;
    }

    public void setPhotos(List<byte[]> photos) {
        this.photos = photos;
    }

    public HBox getPhotoBox() {
        HBox box = new HBox(5);
        if (photos != null) {
            for (byte[] imageData : photos) {
                if (imageData != null && imageData.length > 0) {
                    Image img = new Image(new ByteArrayInputStream(imageData), 40, 40, true, true);
                    ImageView view = new ImageView(img);
                    box.getChildren().add(view);
                }
            }
        }
        return box;
    }

    public static Customer convertToCustomer(CustomerDTO customerDTO) {
        if (customerDTO == null) {
            return null;
        }

        byte[] photo = null;
        if (customerDTO.getPhotos() != null && customerDTO.getPhotos().size() > 0) {
            photo = customerDTO.getPhotos().get(0);
        }

        return new Customer(
                customerDTO.getId(),
                customerDTO.getFirstName(),
                customerDTO.getLastName(),
                customerDTO.getPhoneNumber(),
                customerDTO.getPurchase(),
                customerDTO.getFileId(),
                customerDTO.getFilename(),
                photo
        );

    }

    public static CustomerDTO convertToCustomerDTO(Customer customer) {
        if (customer == null) {
            return null;
        }

        int fileId = 0;
        String fileName = null;
        byte[] bytes = null;

        if (customer.getFile() != null) {
            fileId = customer.getFile().getId();
            fileName = customer.getFile().getFilename();
            bytes = customer.getFile().getContent();
        }

        return new CustomerDTO(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getPhoneNumber(),
                customer.getPurchase(),
                fileId,
                fileName,
                bytes
        );

    }


}
