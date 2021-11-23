package com.wuhenzhizao.sku.bean;

import java.util.ArrayList;

/**
 * 由于之前不支持排序  这里是用于排序的类  保证顺序的数据从这里写入
 */
public class SkuAttrListBean {

    private String specName;
    private ArrayList<String> specValueList;

    public String getSpecName() {
        return specName;
    }

    public void setSpecName(String specName) {
        this.specName = specName;
    }

    public ArrayList<String> getSpecValueList() {
        return specValueList;
    }

    public void setSpecValueList(ArrayList<String> specValueList) {
        this.specValueList = specValueList;
    }
}
