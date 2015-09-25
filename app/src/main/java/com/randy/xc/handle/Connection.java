package com.randy.xc.handle;

import java.net.DatagramPacket;

/**
 * Created by xcg on 2015/9/19.
 */
public class Connection {
    private DatagramPacket datagramPacket;
    private String Name_msg;
    private boolean isSelected;
    public Connection(String name,DatagramPacket datagramPacket,boolean isSelected)
    {
        this.Name_msg=name;
        this.datagramPacket=datagramPacket;
        this.isSelected=isSelected;
    }

    public boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public DatagramPacket getDatagramPacket() {
        return datagramPacket;
    }

    public void setDatagramPacket(DatagramPacket datagramPacket) {
        this.datagramPacket = datagramPacket;
    }

    public void setName_msg(String name_msg) {
        Name_msg = name_msg;
    }

    public String getName_msg() {
        return Name_msg;
    }
}
