package com.xptschool.parent.util;

import com.xptschool.parent.model.BeanStudent;
import com.xptschool.parent.model.GreenDaoHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dexing on 2017/4/7.
 * No1
 */

public class ParentUtil {

    public static String getStuSid() {
        String strSids = "";
        List<String> sids = new ArrayList<>();

        List<BeanStudent> students = GreenDaoHelper.getInstance().getStudents();
        for (int i = 0; i < students.size(); i++) {
            BeanStudent student = students.get(i);
            String sid = student.getS_id();
            if (!sids.contains(sid)) {
                sids.add(sid);
            }
        }
        for (int i = 0; i < sids.size(); i++) {
            strSids += sids.get(i) + ",";
        }

        if (strSids.length() > 0) {
            strSids = strSids.substring(0, strSids.length() - 1);
        }
        return strSids;
    }

}