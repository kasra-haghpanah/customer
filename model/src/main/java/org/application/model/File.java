package org.application.model;

public class File implements Model {

    int id;
    String filename;
    byte[] content;

    public File() {
    }

    public File(int id, String filename, byte[] content) {
        this.id = id;
        this.filename = filename;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
