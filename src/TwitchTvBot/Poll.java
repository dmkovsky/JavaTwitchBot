package TwitchTvBot;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by dmkov on 09.05.2016.
 */
public class Poll  extends Thread{
    private String option1 = null;
    private String option2 = null;
    private String option3 = null;
    private String option4 = null;
    private String member = null;
    private TwitchChatBot bot;
    private ArrayList<String> members;
    private ObservableList<PieChart.Data> absPieList;
    private PieChart pieChart;
    private double votes = 0.0;
    private boolean stopFlag;
    private String chan;
    private String pollTitle = null;

    public Poll(String option1, String option2, String option3, String option4, String pollTitle, TwitchChatBot bot, PieChart pieChart, boolean stopFlag, String chan){
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.bot = bot;
        this.pieChart = pieChart;
        this.stopFlag = stopFlag;
        this.chan = chan;
        this.pollTitle = pollTitle;
    }

    public void startPoll(){

        if(option3 == null && option4 == null){
            absPieList = FXCollections.observableArrayList(
                    new PieChart.Data(option1, 0),
                    new PieChart.Data(option2, 0));
            try {
                bot.sendMsg("Poll question is: "+pollTitle, chan);
                bot.sendMsg("The poll is started type: !vote1 for "+option1+" !vote2 for "+option2, chan);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(option3 != null && option4 == null){
            absPieList = FXCollections.observableArrayList(
                    new PieChart.Data(option1, 0),
                    new PieChart.Data(option2, 0),
                    new PieChart.Data(option3, 0));
            try {
                bot.sendMsg("Poll question is: "+pollTitle, chan);
                bot.sendMsg("The poll is started type: !vote1 for "+option1+" !vote2 for "+option2+" !vote3 for "+option3, chan);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(option3 != null && option4 != null){
            absPieList = FXCollections.observableArrayList(
                    new PieChart.Data(option1, 0),
                    new PieChart.Data(option2, 0),
                    new PieChart.Data(option3, 0),
                    new PieChart.Data(option4, 0));
            try {
                bot.sendMsg("Poll question is: "+pollTitle, chan);
                bot.sendMsg("The poll is started type: !vote1 for "+option1+" | !vote2 for "+option2+" | !vote3 for "+option3+" | !vote4 for "+option4, chan);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        pieChart.setData(absPieList);
        pieChart.setTitle(pollTitle);
        pieChart.setClockwise(false);
        pieChart.setLegendVisible(true);
        super.start();
        members = new ArrayList<>();
    }

    public void addVote(String message, String user){
        if(messageCheck(message)){
            System.out.println(message);
            if(members.isEmpty()){
                if(message.contains("!vote1")){
                    members.add(user);
                    System.out.println("vote1 add");
                    votes = pieChart.getData().get(0).getPieValue();
                    pieChart.getData().get(0).setPieValue(votes + 1);
                }
                else if(message.contains("!vote2")){
                    members.add(user);
                    System.out.println("vote2 add");
                    votes = pieChart.getData().get(1).getPieValue();
                    pieChart.getData().get(1).setPieValue(votes + 1);
                }
                else if(message.contains("!vote3")) {
                    members.add(user);
                    System.out.println("vote3 add");
                    votes = pieChart.getData().get(2).getPieValue();
                    pieChart.getData().get(2).setPieValue(votes + 1);
                }
                else if(message.contains("!vote4")) {
                    members.add(user);
                    System.out.println("vote4 add");
                    votes = pieChart.getData().get(3).getPieValue();
                    pieChart.getData().get(3).setPieValue(votes + 1);
                }
            }
            else if(!members.contains(user)){
                if(message.contains("!vote1")) {
                    System.out.println("vote1 add");
                    votes = pieChart.getData().get(0).getPieValue();
                    pieChart.getData().get(0).setPieValue(votes + 1);
                }
                else if(message.contains("!vote2")){
                    System.out.println("vote2 add");
                    votes = pieChart.getData().get(1).getPieValue();
                    pieChart.getData().get(1).setPieValue(votes + 1);
                }
                else if(message.contains("!vote3")) {
                    System.out.println("vote3 add");
                    votes = pieChart.getData().get(2).getPieValue();
                    pieChart.getData().get(2).setPieValue(votes + 1);
                }
                else if(message.contains("!vote4")) {
                    System.out.println("vote4 add");
                    votes = pieChart.getData().get(3).getPieValue();
                    pieChart.getData().get(3).setPieValue(votes + 1);
                }
            }
        }
    }
    private boolean messageCheck(String message){
        return message.contains("!vote1") || message.contains("!vote2") || message.contains("!vote3") || message.contains("!vote4");
    }
}
