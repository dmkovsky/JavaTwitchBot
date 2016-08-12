package TwitchTvBot;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.Optional;

import static java.lang.Thread.sleep;

public class Controller {
    @FXML // fx:id="btnSend"
    private Button btnSend;
    @FXML // fx:id="txtToSend"
    private TextField txtToSend;
    @FXML // fx:id="txtReader"
    private TextArea txtReader;
    @FXML // fx:id="mnConnect"
    private MenuItem mnConnect;
    @FXML
    private MenuItem mnDisconnect;
    @FXML
    private ScrollPane peneMembers;
    @FXML
    private TextField txtKeyword;
    @FXML
    private TextField txtTime;
    @FXML
    private Button btnStartRaffle;
    @FXML
    private ListView<String> usersView;
    @FXML
    private CheckBox chckOption3;
    @FXML
    private CheckBox chckOption4;
    @FXML
    private TextField txtOption1;
    @FXML
    private TextField txtOption2;
    @FXML
    private TextField txtOption3;
    @FXML
    private TextField txtOption4;
    @FXML
    private Button btnStartPoll;
    @FXML
    private Button btnStopPoll;
    @FXML
    private Button btnResetPoll;
    @FXML
    private PieChart pieChart;
    @FXML
    private TextField txtPollTitle;
    @FXML
    private TextArea txtConsoleRead;
    @FXML
    private ListView<String> viewersList;
    @FXML
    private Label lblCurrentUsers;
    @FXML
    private MenuItem mnPurge;
    @FXML
    private MenuItem mnTimeout1;
    @FXML
    private MenuItem mnTimeout3;
    @FXML
    private MenuItem mnTimeout5;
    @FXML
    private MenuItem mnBan;
    @FXML
    private MenuItem mnUnban;
    @FXML
    private CheckBox chckBtnSubsMode;
    @FXML
    private CheckBox chckBtnComMode;
    @FXML
    private CheckBox chckBtnSlowMode;
    @FXML
    private TextField txtComTime;
    @FXML
    private TextField txtSlowTime;
    @FXML
    private Button btnComStart;
    @FXML
    private ToggleButton toggBtnSlow;


    private TwitchChatBot connect = new TwitchChatBot("Your Login", "Your AuthKey");
    private Task<Void> task;
    private String chan = "";
    private Raffle raffle;
    private boolean raffleFlag = false;
    private String keyword;
    private ObservableList<String> members;
    private Thread gcCleaner;
    private String option1 = null;
    private String option2 = null;
    private String option3 = null;
    private String option4 = null;
    private Poll poll;
    private boolean pollFlag = false;
    private String pollTitle = null;
    private Thread getViews;
    private boolean viewsFlag = false;
    private Viewers viewers;
    private ObservableList<String> absViewers;
    private Thread viewersThread;

    public void Connect(ActionEvent event){

        try {
            connect.connect();
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Please enter the channel name");
            dialog.setContentText("Please enter the channel name:");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()){
                chan = result.get();
                connect.join(chan);
                viewers = new Viewers(chan);
            }else if(!result.isPresent()){
                //TO Do
                System.out.println("Cancel");
            }

        } catch (IOException e) {
            e.printStackTrace();
            txtReader.appendText("-------------- CANNOT CONNECT TO THE SERVER :( -----------------");
        }
        readerMessage();
       viewersThread = new Thread(() -> {
            getViewers();
        });
        viewersThread.start();
        btnSend.setDisable(false);
        txtToSend.setDisable(false);
        mnConnect.setDisable(true);
        mnConnect.setText("Connected");
        mnDisconnect.setDisable(false);

    }

    public void readerMessage() {
            task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    while(!isCancelled()){
                        for(String line : connect.getReader()){
                            if(line.contains("PRIVMSG")){
                                appendingTxt(line);
                            }
                            else txtConsoleRead.appendText(line+"\n");
                        }
                    }
                    return null;
                }
            };
       new Thread(task).start();

    }

    public void appendingTxt(String line){
        String[] split = line.split("!");
        String nick = split[0].substring(1);
        split = line.substring(1).split(":");
        String message = split[1];
        txtReader.appendText(nick +": "+ message +"\n");
        if(raffleFlag == true){
            raffle.addUsers(nick, message);
            stringWorker(message);
        }
        else if(pollFlag == true){
            System.out.println("I'm in if poll");
            poll.addVote(message, nick);
            stringWorker(message);
        }
        else
            stringWorker(message);
    }

    public void disconnect(ActionEvent event){
        task.cancel();
        txtReader.appendText("-------------- DISCONNECT -------------");
        mnConnect.setDisable(false);
        mnDisconnect.setDisable(true);
        btnSend.setDisable(true);
        txtToSend.setDisable(true);
        viewersList.refresh();
        viewsFlag = true;
    }

    public void sendMessage(ActionEvent event){
        try {
            connect.sendMsg(txtToSend.getText(), chan);
            txtReader.appendText("PopekBot: "+txtToSend.getText()+"\n");
            txtToSend.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startRaffle(ActionEvent event){
        keyword = txtKeyword.getText();
        raffle = new Raffle(connect,keyword, chan, false, usersView);
        raffleFlag = true;
        raffle.start();
    }
    public void stopRaffle(ActionEvent event){
        raffleFlag = false;
        raffle.setStopFlag(true);
        raffle.cleanList();
    }

    public void unBlockCheckers(ActionEvent event){
        if(chckOption3.isSelected() && chckOption4.isSelected()){
            txtOption4.setDisable(false);
        }
        else if(!chckOption3.isSelected() && !chckOption4.isSelected()){
            txtOption3.setDisable(true);
            chckOption4.setDisable(true);
            txtOption4.setDisable(true);
            chckOption4.setSelected(false);
            txtOption3.clear();
            txtOption4.clear();
        }
        else if(chckOption3.isSelected() && !chckOption4.isSelected()){
            txtOption3.setDisable(false);
            chckOption4.setDisable(false);
            txtOption4.setDisable(true);
            txtOption4.clear();
        }
        else if(!chckOption3.isSelected() && chckOption4.isSelected() ){
            txtOption3.setDisable(true);
            chckOption4.setDisable(true);
            txtOption4.setDisable(true);
            chckOption4.setSelected(false);
            txtOption3.clear();
            txtOption4.clear();
        }

    }

    public void startPoll(){

        if((txtOption1 != null && txtOption2 != null) && (chckOption3.isSelected() && chckOption4.isSelected())){
            option1 = txtOption1.getText();
            option2 = txtOption2.getText();
            option3 = txtOption3.getText();
            option4 = txtOption4.getText();
            pollTitle = txtPollTitle.getText();
            pollFlag = true;
            poll = new Poll(option1, option2, option3, option4, pollTitle, connect, pieChart, pollFlag, chan);
            poll.startPoll();

        }
        else if((txtOption1 != null && txtOption2 != null) && (chckOption3.isSelected() == true && chckOption4.isSelected() == false)){
            option1 = txtOption1.getText();
            option2 = txtOption2.getText();
            option3 = txtOption3.getText();
            pollTitle = txtPollTitle.getText();
            pollFlag = true;
            poll = new Poll(option1, option2, option3, null, pollTitle, connect, pieChart, pollFlag, chan);
            poll.startPoll();
        }
        else if((txtOption1 != null && txtOption2 != null) && (chckOption3.isSelected() == false && chckOption4.isSelected() == false)){
            System.out.println("IF Controller");
            option1 = txtOption1.getText();
            option2 = txtOption2.getText();
            pollTitle = txtPollTitle.getText();
            pollFlag = true;
            poll = new Poll(option1, option2,null, null, pollTitle, connect, pieChart, pollFlag, chan);
            poll.startPoll();
        }

    }

    public void stopPoll(ActionEvent event){
        pollFlag = false;
    }

    public void resetPoll(ActionEvent event){
        pieChart.getData().clear();
        pieChart.setTitle(null);
    }

    public void stringWorker(String message){
        String spamerino1 = "ヽ༼ຈل͜ຈ༽ﾉ RAISE YOUR DONGERS ヽ༼ຈل͜ຈ༽ﾉ \n";
        String spamerino2 = " ༼ ಠل͟ರೃ༼ ಠل͟ರೃ༼ ಠل͟ರೃ༼ ಠل͟ರೃ ༽ಠل͟ರೃ ༽ಠل͟ರೃ ༽ YOU ARRIVED IN THE INCORRECT DONGERHOOD, SIR༼ ಠل͟ರೃ༼ ಠل͟ರೃ༼ ಠل͟ರೃ༼ ಠل͟ರೃ ༽ಠل͟ರೃ ༽ಠل͟ರೃ ༽\n";
        String spamerino3 = " ༼ ºل͟º༼ ºل͟º༽ºل͟º ༽ YOU COPERINO FRAPPUCCIONO PASTARINO'D THE WRONG DONGERINO ༼ ºل͟º༼ ºل͟º༽ºل͟º ༽";

        if(message.contains("!brainpower")){
            for(int i=0; i<3; i++){
                try {
                    connect.sendMsg("SourPls O-oooooooooo AAAAE-A-A-I-A-U- JO-oooooooooooo AAE-O-A-A-U-U-A- E-eee-ee-eee AAAAE-A-E-I-E-A- JO-ooo-oo-oo-oo EEEEO-A-AAA-AAAA SourPls", chan);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else if(message.contains("!spamerino")){
            try {
                connect.sendMsg(spamerino1, chan);
                connect.sendMsg(spamerino2, chan);
                connect.sendMsg(spamerino3, chan);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(message.contains("!taxibro")){
            try {
                for(int i=0; i<10; i++){
                    connect.sendMsg("TaxiBro TaxiBro TaxiBro TaxiBro TaxiBro TaxiBro TaxiBro TaxiBro TaxiBro TaxiBro TaxiBro TaxiBro TaxiBro TaxiBro TaxiBro TaxiBro TaxiBro TaxiBro TaxiBro TaxiBro TaxiBro TaxiBro TaxiBro TaxiBro TaxiBro TaxiBro TaxiBro TaxiBro TaxiBro TaxiBro TaxiBro TaxiBro TaxiBro TaxiBro TaxiBro TaxiBro TaxiBro  TaxiBro TaxiBro", chan);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void getViewers() {
        while(!viewsFlag) {

            Platform.runLater(() -> {

                try {
                    viewersList.setItems(null);
                    viewersList.setItems(viewers.getViewers());
                    lblCurrentUsers.setText("Currently number of chatters: "+viewers.getCurrentlyOnChat());
                    viewersList.refresh();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            try {
                sleep(180000);
                System.gc();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    public void purgeUser(ActionEvent event){
        String nick = viewersList.getSelectionModel().getSelectedItem();
        try {
            connect.sendMsg("/timeout "+nick+" 1", chan);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void banUser(ActionEvent event){
        String nick = viewersList.getSelectionModel().getSelectedItem();
        try {
            connect.sendMsg("/ban "+nick, chan);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void unbanUser(ActionEvent event){
        String nick = viewersList.getSelectionModel().getSelectedItem();
        try {
            connect.sendMsg("/unban "+nick, chan);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void timeout1User(ActionEvent event){
        String nick = viewersList.getSelectionModel().getSelectedItem();
        try {
            connect.sendMsg("/timeout "+nick+" 60", chan);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void timeout3User(ActionEvent event){
        String nick = viewersList.getSelectionModel().getSelectedItem();
        try {
            connect.sendMsg("/timeout "+nick+" 180", chan);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void timeout5User(ActionEvent event){
        String nick = viewersList.getSelectionModel().getSelectedItem();
        try {
            connect.sendMsg("/timeout "+nick+" 300", chan);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void subscribersMode(ActionEvent event){
        if(chckBtnSubsMode.isSelected()){
            try {
                connect.sendMsg("/subscribers", chan);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(!chckBtnSubsMode.isSelected()){
            try {
                connect.sendMsg("/subscribersoff", chan);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void activeSlowMode(ActionEvent event){
        if(chckBtnSlowMode.isSelected()){
            txtSlowTime.setDisable(false);
            toggBtnSlow.setDisable(false);
        }
        else if(!chckBtnSlowMode.isSelected()) {
            try {
                connect.sendMsg("/slowoff", chan);
                txtSlowTime.clear();
                txtSlowTime.setDisable(true);
                toggBtnSlow.setDisable(true);
                toggBtnSlow.setSelected(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setSlowMode(){
        if(toggBtnSlow.isSelected()){
            try {
                connect.sendMsg("/slow "+txtSlowTime.getText(), chan);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
            try {
                connect.sendMsg("/slowoff", chan);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
    public void activeCommercialMode(ActionEvent event){
        if(chckBtnComMode.isSelected()){
            txtComTime.setDisable(false);
            btnComStart.setDisable(false);
        }
        else if(!chckBtnComMode.isSelected()){
            txtComTime.setDisable(true);
            btnComStart.setDisable(true);
        }
    }
    public void setCommercialMode(ActionEvent event){
        try {
            connect.sendMsg("/commercial "+txtComTime.getText(), chan);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
