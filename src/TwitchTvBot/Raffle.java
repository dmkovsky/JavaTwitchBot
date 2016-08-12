package TwitchTvBot;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Raffle extends Thread{
    private String channel;
    private String keyword;
    private TwitchChatBot bot;
    private ArrayList<String> members;
    private boolean stopFlag;
    private String winner;
    private ListView<String> usersView;
    private ObservableList<String> absMembers;

    public Raffle(TwitchChatBot bot, String keyword, String channel, boolean stopFlag, ListView<String> usersView) {
        this.bot = bot;
        this.keyword = keyword;
        this.channel = channel;
        this.stopFlag = stopFlag;
        this.usersView = usersView;
    }

    public void setStopFlag(boolean stopFlag) {
        this.stopFlag = stopFlag;
    }
    @Override
    public void run() {
        while (!stopFlag) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            }
        }
        //pick the user
        if (stopFlag) {
            pickWinner();
        }
    }
    @Override
    public synchronized void start(){
        members = new ArrayList<String>();
        super.start();
        try {
            bot.sendMsg("Raffle started type: "+keyword, channel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addUsers(String user, String msg){
        if(msg.contains(keyword)){
            if(members.isEmpty()){
                members.add(user);
            }
            else if(!members.contains(user)){
                members.add(user);
            }
            absMembers = FXCollections.observableArrayList(members);
            usersView.setItems(absMembers);
            usersView.refresh();
        }

    }
    public void pickWinner(){
        int sizeOfMembers = members.size();
        Random generator = new Random();
        if(sizeOfMembers > 0){
            winner = members.get(generator.nextInt(sizeOfMembers));
            try {
                bot.sendMsg("The winner is: "+winner, channel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                bot.sendMsg("Nobody signed up to raffle :(", channel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<String> getMembers() {
        return members;
    }
    public void cleanList(){
        if(!absMembers.isEmpty()){
            absMembers.clear();
            usersView.setItems(absMembers);
            usersView.refresh();
        }
    }
}