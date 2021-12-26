package com.fahadandroid.groupchat.models;

import android.os.Parcel;
import android.os.Parcelable;

public class CompanyModel implements Parcelable{
    String pinNumber, fullLegalName, size, legalAddress, poastalCode, city, country, telephone, email, piva,
            legalRepresentative, idLegalRepresent, website, companyDescription, companyResponsibility, taskOfStudents;
    String selectedCountry, key, contactPerson;
    public CompanyModel(){}

    public CompanyModel(String pinNumber, String fullLegalName, String size, String legalAddress, String poastalCode,
                        String city, String country, String telephone, String email, String piva,
                        String legalRepresentative, String idLegalRepresent, String website, String companyDescription, String contactPerson) {
        this.pinNumber = pinNumber;
        this.fullLegalName = fullLegalName;
        this.size = size;
        this.legalAddress = legalAddress;
        this.poastalCode = poastalCode;
        this.city = city;
        this.country = country;
        this.telephone = telephone;
        this.contactPerson = contactPerson;
        this.email = email;
        this.piva = piva;
        this.legalRepresentative = legalRepresentative;
        this.idLegalRepresent = idLegalRepresent;
        this.website = website;
        this.companyDescription = companyDescription;
    }

    public CompanyModel(String fullLegalName, String size, String legalAddress, String poastalCode, String city, String country, String telephone, String email, String website, String companyDescription, String companyResponsibility, String taskOfStudents) {
        this.fullLegalName = fullLegalName;
        this.size = size;
        this.legalAddress = legalAddress;
        this.poastalCode = poastalCode;
        this.city = city;
        this.country = country;
        this.telephone = telephone;
        this.email = email;
        this.website = website;
        this.companyDescription = companyDescription;
        this.companyResponsibility = companyResponsibility;
        this.taskOfStudents = taskOfStudents;
    }


    protected CompanyModel(Parcel in) {
        pinNumber = in.readString();
        fullLegalName = in.readString();
        size = in.readString();
        legalAddress = in.readString();
        poastalCode = in.readString();
        city = in.readString();
        country = in.readString();
        telephone = in.readString();
        email = in.readString();
        piva = in.readString();
        legalRepresentative = in.readString();
        idLegalRepresent = in.readString();
        website = in.readString();
        companyDescription = in.readString();
        companyResponsibility = in.readString();
        taskOfStudents = in.readString();
        selectedCountry = in.readString();
        key = in.readString();
        contactPerson = in.readString();
    }

    public static final Creator<CompanyModel> CREATOR = new Creator<CompanyModel>() {
        @Override
        public CompanyModel createFromParcel(Parcel in) {
            return new CompanyModel(in);
        }

        @Override
        public CompanyModel[] newArray(int size) {
            return new CompanyModel[size];
        }
    };

    public String getSelectedCountry() {
        return selectedCountry;
    }

    public void setSelectedCountry(String selectedCountry) {
        this.selectedCountry = selectedCountry;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCompanyResponsibility() {
        return companyResponsibility;
    }

    public void setCompanyResponsibility(String companyResponsibility) {
        this.companyResponsibility = companyResponsibility;
    }

    public String getTaskOfStudents() {
        return taskOfStudents;
    }

    public void setTaskOfStudents(String taskOfStudents) {
        this.taskOfStudents = taskOfStudents;
    }

    public String getCompanyDescription() {
        return companyDescription;
    }

    public void setCompanyDescription(String companyDescription) {
        this.companyDescription = companyDescription;
    }

    public String getPinNumber() {
        return pinNumber;
    }

    public void setPinNumber(String pinNumber) {
        this.pinNumber = pinNumber;
    }

    public String getFullLegalName() {
        return fullLegalName;
    }

    public void setFullLegalName(String fullLegalName) {
        this.fullLegalName = fullLegalName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getLegalAddress() {
        return legalAddress;
    }

    public void setLegalAddress(String legalAddress) {
        this.legalAddress = legalAddress;
    }

    public String getPoastalCode() {
        return poastalCode;
    }

    public void setPoastalCode(String poastalCode) {
        this.poastalCode = poastalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPiva() {
        return piva;
    }

    public void setPiva(String piva) {
        this.piva = piva;
    }

    public String getLegalRepresentative() {
        return legalRepresentative;
    }

    public void setLegalRepresentative(String legalRepresentative) {
        this.legalRepresentative = legalRepresentative;
    }

    public String getIdLegalRepresent() {
        return idLegalRepresent;
    }

    public void setIdLegalRepresent(String idLegalRepresent) {
        this.idLegalRepresent = idLegalRepresent;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(pinNumber);
        parcel.writeString(fullLegalName);
        parcel.writeString(size);
        parcel.writeString(legalAddress);
        parcel.writeString(poastalCode);
        parcel.writeString(city);
        parcel.writeString(country);
        parcel.writeString(telephone);
        parcel.writeString(email);
        parcel.writeString(piva);
        parcel.writeString(legalRepresentative);
        parcel.writeString(idLegalRepresent);
        parcel.writeString(website);
        parcel.writeString(companyDescription);
        parcel.writeString(companyResponsibility);
        parcel.writeString(taskOfStudents);
        parcel.writeString(selectedCountry);
        parcel.writeString(key);
        parcel.writeString(contactPerson);
    }
}
