package io.phdata.bean;

public class Properties {

    private String owner;
    private String last_update_date;
    private String source_system;
    private String personal_data;


    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getLast_update_date() {
        return last_update_date;
    }

    public void setLast_update_date(String last_update_date) {
        this.last_update_date = last_update_date;
    }

    public String getSource_system() {
        return source_system;
    }

    public void setSource_system(String source_system) {
        this.source_system = source_system;
    }

    public String getPersonal_data() {
        return personal_data;
    }

    public void setPersonal_data(String personal_data) {
        this.personal_data = personal_data;
    }

    @Override
    public String toString() {
        return this.owner + " " + this.source_system + " " + this.last_update_date+ " " + this.personal_data;
    }
}
