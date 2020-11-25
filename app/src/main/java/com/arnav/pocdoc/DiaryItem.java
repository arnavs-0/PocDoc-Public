package com.arnav.pocdoc;

class DiaryItem {
    String title, content, date, alertLevel, symptoms, diagnosis;

    public DiaryItem(String title, String content, String date, String alertLevel, String symptoms, String diagnosis) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.alertLevel = alertLevel;
        this.symptoms = symptoms;
        this.diagnosis = diagnosis;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
       this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAlertLevel() {
        return alertLevel;
    }

    public void setAlertLevel(String alertLevel) {
        this.alertLevel = alertLevel;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptomsRecycler) {
        this.symptoms = symptomsRecycler;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }
}
