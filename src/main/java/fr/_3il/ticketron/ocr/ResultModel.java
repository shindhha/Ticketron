package fr._3il.ticketron.ocr;

public class ResultModel {
    private String text;
    private double confidence;

    public ResultModel() {}

    public ResultModel(String text, double confidence) {
        this.text = text;
        this.confidence = confidence;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public double getConfidence() {
        return confidence;
    }
    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
}