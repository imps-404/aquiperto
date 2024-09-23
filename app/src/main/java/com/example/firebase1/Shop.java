package com.example.firebase1;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class UserExp
{
    public List<String> comments ;
    public List<Double> rating;
    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public List<Double> getRating() {
        return rating;
    }

    public void setRating(List<Double> rating) {
        this.rating = rating;
    }

    public UserExp(){
        comments = new ArrayList<String>();
        rating = new ArrayList<Double>();
    }
}
public class Shop {

    String name;
    String address;
    String number;
    String phone;
    String type;
    String image;
    String id;
    UserExp userexp = new UserExp();
    public UserExp getUserExp()
    {
        return userexp;
    }
    public void setUserExp(UserExp ue)
    {
        userexp = ue;
    }

    public float sumrating;

    public float calculateRating() {
        float r=0;
        int i;
        if(userexp.getRating().size() <=0 )
            return 0;
        else {
            for (i = 0; i < userexp.getRating().size(); i++) {
                r += userexp.getRating().get(i);
            }
            r = (float) r / i;
        }
        sumrating = r;
        return sumrating;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Shop() {

    }
    public Shop(String id, String name, String type, String address, String number, String phone , UserExp userexp) {
        id= this.id;
        name= this.name;
        type = this.type;
        address = this.address;
        number = this.number;
        phone = this.phone;
        userexp = this.userexp;

    }
    @Override
    public String toString() {
        return "Shop{" +
                " id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", address='" + address + '\'' +
                ", number='" + number + '\'' +
                ", phone='" + phone + '\'' +
                ", image='" + image + '\'' +
                ", userexp='" + userexp + '\'' +
                '}';
    }



}
