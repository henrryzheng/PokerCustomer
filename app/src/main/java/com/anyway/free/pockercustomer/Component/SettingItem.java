package com.anyway.free.pockercustomer.Component;

/**
 * Created by Administrator on 2016/12/19.
 */
public class SettingItem {
    private String settingName;
    private String selectedText;

    public SettingItem(String name, String title){
        this.settingName = name;
        this.selectedText = title;
    }

    public String getName(){
        return settingName;
    }

    public String getSelectedTitle(){
        return selectedText;
    }
}
