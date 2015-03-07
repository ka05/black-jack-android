package com.example.clay.blackjack;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Button;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.Map;
import java.util.Iterator;
import android.util.Log;

import org.w3c.dom.Text;


public class BlackJack extends ActionBarActivity {

    private TableLayout scrollViewTableLayout;
    private TableLayout scrollableTableLayout;
    private TextView handNumTextView;
    private TextView resultsTextView;
    private TextView dealerNameTextView;
    private TextView dealerScoreTextView;
    private TextView playerNameTextView;
    private TextView playerScoreTextView;
    private TableRow dealerCardTableRow;
    private TableRow playerCardTableRow;
    private TextView dealerHandTextView;
    private TextView playerHandTextView;
    private TextView endGameResultsTextView;
    private ScrollView scrollView;

    String test = "test: ";
    HashMap<String, Drawable> cardImages =  new HashMap<String,Drawable>();
    ArrayList<String> deck = new ArrayList<String>(52);
    ArrayList<String> playerHand = new ArrayList<String>();
    ArrayList<String> dealerHand = new ArrayList<String>();
    int playerWins = 0;
    int dealerWins = 0;

    boolean inGame = false; // determines if you are in game or not (used in alert)
    boolean stay = false;

    int handCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_jack);


        scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollViewTableLayout = (TableLayout) findViewById(R.id.scrollViewTableLayout);
        endGameResultsTextView = (TextView) findViewById(R.id.endGameResultsTextView);
//        dealerHandTextView = (TextView) findViewById(R.id.dealerHandTextView);
//        playerHandTextView = (TextView) findViewById(R.id.playerHandTextView);
//        dealerCardTableRow = (TableRow) findViewById(R.id.dealerCardTableRow);
//        playerCardTableRow = (TableRow) findViewById(R.id.playerCardTableRow);
//        dealerNameTextView = (TextView) findViewById(R.id.dealerNameTextView);
//        dealerScoreTextView = (TextView) findViewById(R.id.dealerScoreTextView);
//        playerNameTextView = (TextView) findViewById(R.id.playerNameTextView);
//        playerScoreTextView = (TextView) findViewById(R.id.playerScoreTextView);

        dealerScoreTextView = (TextView)findViewById(R.id.dealerScoreTextView);
        playerScoreTextView = (TextView)findViewById(R.id.playerScoreTextView);

        findViewById(R.id.hitButton).setOnClickListener(hitButtonListener);
        findViewById(R.id.stayButton).setOnClickListener(stayButtonListener);
        findViewById(R.id.newGameButton).setOnClickListener(newGameButtonListener);

        setPlayerScoreTextView();
        setDealerScoreTextView();

        initCardImages();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_black_jack, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public View.OnClickListener hitButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hit();
        }
    };
    public View.OnClickListener stayButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            stay();
        }
    };
    public View.OnClickListener newGameButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            newGame();
        }
    };

    public void newGame(){
        if(inGame == true){
            // you are in game already
            showConfirmMsg("You are already in a game, do you want to start a new one?");
        }
        else{
            //initialize a new game
            deck.clear(); // clears out the deck

            handCount = 0; // reset hand count
            //clear out scrollViewTableView
            scrollViewTableLayout.removeAllViews();
            resetScore(); // resets dealer and player score
            resetScoreLabels(); // resets labels with player and dealer scores
            resetWinnerLabel(); // reset winner label
            createNewDealView(); // create new view for deal
            initDeck(); // initializes deck
            inGame = true;
            stay = false;
            shuffle(); // shuffles deck
            resetCardCount(); // resets card counts
            dealHands(); // deal new hands
            endGameResultsTextView.setText("Hit or Stay?");
        }
    }

    public void createNewDealView(){
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dealView = inflater.inflate(R.layout.dealview, null);
        scrollableTableLayout = (TableLayout) findViewById(R.id.scrollableTableLayout);
        dealerHandTextView = (TextView) dealView.findViewById(R.id.dealerHandTextView);
        playerHandTextView = (TextView) dealView.findViewById(R.id.playerHandTextView);
        dealerCardTableRow = (TableRow) dealView.findViewById(R.id.dealerCardTableRow);
        playerCardTableRow = (TableRow) dealView.findViewById(R.id.playerCardTableRow);
        dealerNameTextView = (TextView) dealView.findViewById(R.id.dealerNameTextView);
        playerNameTextView = (TextView) dealView.findViewById(R.id.playerNameTextView);
        handNumTextView = (TextView) dealView.findViewById(R.id.handNumTextView);
        resultsTextView = (TextView) dealView.findViewById(R.id.resultsTextView);
        handCount++;
        setHandNumTextView();
        setResultsTextView();
        scrollViewTableLayout.addView(dealView, 0);
    }

    public void dealNewHand(){
        createNewDealView();
        //going to use layout inflator to create new dealview.xml in scrollview

        //resetImages(); // resets imageviews
        resetWinnerLabel();
        stay = false;

        resetCardCount(); // resets card counts
        dealHands(); // deal new hands
    }

    public Drawable getCardImg(String resName){
        Drawable imgSrc = cardImages.get(resName);
        return imgSrc;
    }

    public void setCardImg(ImageView imgView, Drawable res){
        imgView.setImageDrawable(res);
    }

    public void createGUICard(TableRow tr, String cardName){
        // creates an imageview and adds it to the passed in table
        // if back of card
        if(cardName == "b1fv"){
            ImageView imgView = new ImageView(this);
            setCardImg(imgView, this.getResources().getDrawable(R.drawable.b1fv));
            tr.addView(imgView);
        }else{
            // regular card
            ImageView imgView = new ImageView(this);
            setCardImg(imgView, getCardImg(cardName));
            tr.addView(imgView);
        }
    }

    public void shuffle(){
        Collections.shuffle(deck);
    }

    public void dealHands(){
        String card1 = deal();
        dealerHand.add(card1);
        createGUICard(dealerCardTableRow, card1);

        String card2 = deal();
        playerHand.add(card2);
        createGUICard(playerCardTableRow, card2);

        String card3 = deal();
        dealerHand.add(card3);
        createGUICard(dealerCardTableRow, "b1fv");

        String card4 = deal();
        playerHand.add(card4);
        createGUICard(playerCardTableRow, card4);

        // update hand count in view for player
        updateHandCount(playerHandTextView, calcTotalCount(playerHand));
        dealerHandTextView.setText("Hand Count: ?");
    }

    public String deal(){
        Integer size = deck.size();
        // pop card from deck using math.random
        String cardResName = deck.get(size - 1);
        deck.remove(size - 1);

        // grab new deck if deck count is below 10
        // it will be below 10 at this point since it has been decremented above
        if(size < 10){
            Log.d(test, "deck size < 10");
            deck.clear();
            initDeck();
        }
        return cardResName;
    }

    public void stay(){
        if(!stay){
            stay = true;
            dealerAITurn();
        }
    }

    public void hit(){
        if(!stay && calcTotalCount(playerHand) < 21){
            String card = deal();
            playerHand.add(card);
            createGUICard(playerCardTableRow, card);
            updateHandCount(playerHandTextView, calcTotalCount(playerHand));
        }
    }

    public void dealerHit(){
        String card = deal();
        dealerHand.add(card);
        createGUICard(dealerCardTableRow, card);
        updateHandCount(dealerHandTextView, calcTotalCount(dealerHand));
    }

    public void setPlayerScoreTextView(){
        playerScoreTextView.setText(getResources().getString(R.string.playerScore, playerWins));
    }
    public void setDealerScoreTextView(){
        dealerScoreTextView.setText(getResources().getString(R.string.dealerScore, dealerWins));
    }
    public void setHandNumTextView(){
        handNumTextView.setText(getResources().getString(R.string.handNum, handCount));
    }
    public void setResultsTextView(){
        resultsTextView.setText(getResources().getString(R.string.resultText, " "));
    }

    public void resetImages(){
        dealerCardTableRow.removeAllViews();
        playerCardTableRow.removeAllViews();
    }

    public void resetCardCount(){
        playerHand.clear();
        dealerHand.clear();
    }

    public void resetScore(){
        playerWins = 0;
        dealerWins = 0;
    }

    public void resetScoreLabels(){
        setDealerScoreTextView();
        setPlayerScoreTextView();
    }

    public void resetLabels(){
        playerScoreTextView.setText("Score:");
        dealerScoreTextView.setText("Score:");
    }

    public void resetWinnerLabel(){
        endGameResultsTextView.setText("Hit or Stay?");
    }

    public int calcTotalCount(ArrayList hand){
        int sum = 0;
        for(int i = 0; i < hand.size(); i++){
            // need to add other scenario for if has ace (1 or 11)
            sum += getIntVal(hand.get(i));
        }
        if((hand.contains("ca") || hand.contains("da") || hand.contains("ha") || hand.contains("sa")) && sum > 21){
            sum = sum - 10;
        }
        return sum;
    }

    public int getIntVal(Object val){
        boolean isAce = false;
        String strVal = val.toString();
        strVal = strVal.substring(1);
        int intVal;
        switch(strVal){
            case "j":
            case "q":
            case "k":
                intVal = 10;
                break;
            case "a":
                intVal = 11;
                isAce = true; // need to figure this out
                break;
            default:
                intVal = Integer.parseInt(strVal);
                break;
        }
        return intVal;
    }

    // change name to updateHandCount
    public void updateHandCount(TextView txtView, int total){

        // if ace exists and current score is above 21
        if(total > 21){
            txtView.setText("Busted");
            // should i start new round automatically? or prompt them for new round
            stay();
        }else{
            if(total == 21){
                txtView.setText("Hand Count: " + total);
                dealerAITurn();
            }
            txtView.setText("Hand Count: " + total);
        }
    }

    public void dealerAITurn(){
        // first turn up card
        dealerCardTableRow.removeViewAt(1); //remove old
        createGUICard(dealerCardTableRow, dealerHand.get(1)); // reveal other card
        updateHandCount(dealerHandTextView, calcTotalCount(dealerHand));

        // need to figure out how to hide it by showing back of card
        if(calcTotalCount(dealerHand) <= 16){
            while(calcTotalCount(dealerHand) < 17){
                Log.d(test, "DEALERS HAND** < 17");
                if(calcTotalCount(dealerHand) >= 17){
                    //stop
                    Log.d(test, "DEALERS HAND** >= 17");
                    finalGameCheck();
                    break;
                }else{
                    dealerHit();

                    if(calcTotalCount(dealerHand) >= 17){
                        Log.d(test, "DEALERS HAND** >= 17");
                        finalGameCheck();
                        break;
                    }
                }
            }
        }
        else{
            Log.d(test, "dealer hand > 16");
            finalGameCheck();
        }
    }


    //When a game ends in a push (tie) or a win, an AlertDialog will pop up with the results (push or who wins).
    // Two new hands will then be dealt after the dialog has been dismissed.

    public void finalGameCheck(){
        //check for push
        // if not push - who won
        //check values of both deck counts.
        if(calcTotalCount(dealerHand) == calcTotalCount(playerHand)&& calcTotalCount(dealerHand) > 21 && calcTotalCount(playerHand) > 21){
            // we have a push
            resultsTextView.setText("It's a Push");
            showWinner("Its a push, nobody wins.");
        }
        else{
            if(calcTotalCount(dealerHand) > 21 && calcTotalCount(playerHand) > 21){
                //oth busted
                resultsTextView.setText("It's a Push");
                showWinner("Its a push, nobody wins.");
            }
            else{
                if(isNotBust(calcTotalCount(dealerHand)) > isNotBust(calcTotalCount(playerHand))){
                    if(calcTotalCount(dealerHand) <= 21){
                        // dealer won
                        resultsTextView.setText("Dealer Wins!");
                        // sets win count
                        dealerWins++;
                        setDealerScoreTextView();

                        showWinner("Dealer Won");
                    }
                    else{
                        // dealer busted
                        resultsTextView.setText("It's a Push!");
                        showWinner("Its a push, nobody wins.");
                    }
                }
                else{
                    //player won
                    resultsTextView.setText("Player Wins!");
                    // sets win count
                    playerWins++;
                    setPlayerScoreTextView();
                    showWinner("Player Won!");
                }
            }
        }
    }

    public int isNotBust(int val){
        if(val > 21){
            return 0;
        }else{
            return val;
        }
    }

    public void initCardImages(){
        // clubs
        cardImages.put("ca", this.getResources().getDrawable(R.drawable.c1));
        cardImages.put("c2", this.getResources().getDrawable(R.drawable.c2));
        cardImages.put("c3", this.getResources().getDrawable(R.drawable.c3));
        cardImages.put("c4", this.getResources().getDrawable(R.drawable.c4));
        cardImages.put("c5", this.getResources().getDrawable(R.drawable.c5));
        cardImages.put("c6", this.getResources().getDrawable(R.drawable.c6));
        cardImages.put("c7", this.getResources().getDrawable(R.drawable.c7));
        cardImages.put("c8", this.getResources().getDrawable(R.drawable.c8));
        cardImages.put("c9", this.getResources().getDrawable(R.drawable.c9));
        cardImages.put("c10", this.getResources().getDrawable(R.drawable.c10));
        cardImages.put("cj", this.getResources().getDrawable(R.drawable.cj));
        cardImages.put("cq", this.getResources().getDrawable(R.drawable.cq));
        cardImages.put("ck", this.getResources().getDrawable(R.drawable.ck));

        // diamonds
        cardImages.put("da", this.getResources().getDrawable(R.drawable.d1));
        cardImages.put("d2", this.getResources().getDrawable(R.drawable.d2));
        cardImages.put("d3", this.getResources().getDrawable(R.drawable.d3));
        cardImages.put("d4", this.getResources().getDrawable(R.drawable.d4));
        cardImages.put("d5", this.getResources().getDrawable(R.drawable.d5));
        cardImages.put("d6", this.getResources().getDrawable(R.drawable.d6));
        cardImages.put("d7", this.getResources().getDrawable(R.drawable.d7));
        cardImages.put("d8", this.getResources().getDrawable(R.drawable.d8));
        cardImages.put("d9", this.getResources().getDrawable(R.drawable.d9));
        cardImages.put("d10", this.getResources().getDrawable(R.drawable.d10));
        cardImages.put("dj", this.getResources().getDrawable(R.drawable.dj));
        cardImages.put("dq", this.getResources().getDrawable(R.drawable.dq));
        cardImages.put("dk", this.getResources().getDrawable(R.drawable.dk));

        // hearts
        cardImages.put("ha", this.getResources().getDrawable(R.drawable.h1));
        cardImages.put("h2", this.getResources().getDrawable(R.drawable.h2));
        cardImages.put("h3", this.getResources().getDrawable(R.drawable.h3));
        cardImages.put("h4", this.getResources().getDrawable(R.drawable.h4));
        cardImages.put("h5", this.getResources().getDrawable(R.drawable.h5));
        cardImages.put("h6", this.getResources().getDrawable(R.drawable.h6));
        cardImages.put("h7", this.getResources().getDrawable(R.drawable.h7));
        cardImages.put("h8", this.getResources().getDrawable(R.drawable.h8));
        cardImages.put("h9", this.getResources().getDrawable(R.drawable.h9));
        cardImages.put("h10", this.getResources().getDrawable(R.drawable.h10));
        cardImages.put("hj", this.getResources().getDrawable(R.drawable.hj));
        cardImages.put("hq", this.getResources().getDrawable(R.drawable.hq));
        cardImages.put("hk", this.getResources().getDrawable(R.drawable.hk));

        // Spades
        cardImages.put("sa", this.getResources().getDrawable(R.drawable.s1));
        cardImages.put("s2", this.getResources().getDrawable(R.drawable.s2));
        cardImages.put("s3", this.getResources().getDrawable(R.drawable.s3));
        cardImages.put("s4", this.getResources().getDrawable(R.drawable.s4));
        cardImages.put("s5", this.getResources().getDrawable(R.drawable.s5));
        cardImages.put("s6", this.getResources().getDrawable(R.drawable.s6));
        cardImages.put("s7", this.getResources().getDrawable(R.drawable.s7));
        cardImages.put("s8", this.getResources().getDrawable(R.drawable.s8));
        cardImages.put("s9", this.getResources().getDrawable(R.drawable.s9));
        cardImages.put("s10", this.getResources().getDrawable(R.drawable.s10));
        cardImages.put("sj", this.getResources().getDrawable(R.drawable.sj));
        cardImages.put("sq", this.getResources().getDrawable(R.drawable.sq));
        cardImages.put("sk", this.getResources().getDrawable(R.drawable.sk));
    }

    public void initDeck(){
        deck.add("ca");
        deck.add("c2");
        deck.add("c3");
        deck.add("c4");
        deck.add("c5");
        deck.add("c6");
        deck.add("c7");
        deck.add("c8");
        deck.add("c9");
        deck.add("c10");
        deck.add("cj");
        deck.add("cq");
        deck.add("ck");
        deck.add("da");
        deck.add("d2");
        deck.add("d3");
        deck.add("d4");
        deck.add("d5");
        deck.add("d6");
        deck.add("d7");
        deck.add("d8");
        deck.add("d9");
        deck.add("d10");
        deck.add("dj");
        deck.add("dq");
        deck.add("dk");
        deck.add("ha");
        deck.add("h2");
        deck.add("h3");
        deck.add("h4");
        deck.add("h5");
        deck.add("h6");
        deck.add("h7");
        deck.add("h8");
        deck.add("h9");
        deck.add("h10");
        deck.add("hj");
        deck.add("hq");
        deck.add("hk");
        deck.add("sa");
        deck.add("s2");
        deck.add("s3");
        deck.add("s4");
        deck.add("s5");
        deck.add("s6");
        deck.add("s7");
        deck.add("s8");
        deck.add("s9");
        deck.add("s10");
        deck.add("sj");
        deck.add("sq");
        deck.add("sk");
    }


    private void showConfirmMsg(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("New Game?")
                .setMessage(msg)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        inGame = false;
                        newGame();
                        return;
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showWinner(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage("Player Hand: "+ calcTotalCount(playerHand) + "\nDealer Hand: " + calcTotalCount(dealerHand) + "\n\nDeal Another?")
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dealNewHand();
                        return;
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
