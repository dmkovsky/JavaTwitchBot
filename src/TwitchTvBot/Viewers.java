package TwitchTvBot;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by dmkov on 14.05.2016.
 */
public class Viewers {

    private ArrayList<String> viewers = new ArrayList<>();
    private StringBuffer buffer;
    private InputStream url;
    private BufferedReader reader;
    private int read;
    private char[] chars;
    private ObservableList<String> absViewers;
    private String chan;
    private int currentlyOnChat;

    public Viewers(String chan){
        this.chan = chan;
    }

    public ObservableList<String> getViewers() throws IOException {
        url = new URL("http://tmi.twitch.tv/group/user/" + chan + "/chatters").openStream();
        reader = new BufferedReader(new InputStreamReader(url, Charset.forName("UTF-8")));
        chars = new char[1024];
        buffer = new StringBuffer();
        while ((read = reader.read(chars)) != -1){
            buffer.append(chars, 0, read);
        }
        JSONObject object = new JSONObject(buffer.toString());
        JSONArray arr = object.getJSONObject("chatters").getJSONArray("viewers");
        if (arr.length() != 0) {
            for (int i = 0; i < arr.length(); i++) {
                viewers.add(arr.get(i).toString());
                System.out.println(arr.get(i).toString());
            }
            currentlyOnChat = viewers.size();
            absViewers = FXCollections.observableArrayList(viewers);
            viewers.clear();
            return absViewers;
        }
        return null;
    }

    public int getCurrentlyOnChat(){
        return currentlyOnChat;
    }

}
