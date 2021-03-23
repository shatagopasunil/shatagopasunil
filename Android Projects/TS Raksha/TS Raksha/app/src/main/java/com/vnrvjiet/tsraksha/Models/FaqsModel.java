package com.vnrvjiet.tsraksha.Models;

public class FaqsModel {
    String question, answer;

    public FaqsModel() {
    }

    public FaqsModel(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }


    public String getAnswer() {
        return answer;
    }

}
