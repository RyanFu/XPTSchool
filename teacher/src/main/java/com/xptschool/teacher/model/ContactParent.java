package com.xptschool.teacher.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

/**
 * Created by dexing on 2016/12/8.
 * No1
 */
@Entity
public class ContactParent implements Serializable {

    private String sp_id;
    private String stu_id;
    private String user_id;
    private String name;
    private String phone;
    private String sex;

    @Generated(hash = 1441456147)
    public ContactParent(String sp_id, String stu_id, String user_id, String name,
            String phone, String sex) {
        this.sp_id = sp_id;
        this.stu_id = stu_id;
        this.user_id = user_id;
        this.name = name;
        this.phone = phone;
        this.sex = sex;
    }

    @Generated(hash = 645104438)
    public ContactParent() {
    }

    public String getSp_id() {
        return sp_id;
    }

    public void setSp_id(String sp_id) {
        this.sp_id = sp_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getStu_id() {
        return stu_id;
    }

    public void setStu_id(String stu_id) {
        this.stu_id = stu_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "ContactParent{" +
                "sp_id='" + sp_id + '\'' +
                ", stu_id='" + stu_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", sex='" + sex + '\'' +
                '}';
    }
}
