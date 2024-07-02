package models;

public class ResultSheet extends Course {
    private float evaluation;
    private float finalExam;
    private float gradePoint;
    private String grade;

    public ResultSheet(String courseName, float evaluation, float finalExam, String grade, float gradePoint){
        this.setCourseName(courseName);
        this.evaluation = evaluation;
        this.finalExam = finalExam;
        this.grade = grade;
        this.gradePoint = gradePoint;
    }

    public float getEvaluation() {
        return evaluation;
    }
    public void setEvaluation(float evaluation) {
        this.evaluation = evaluation;
    }
    public float getFinalExam() {
        return finalExam;
    }
    public void setFinalExam(float finalExam) {
        this.finalExam = finalExam;
    }
    public String getGrade() {
        return grade;
    }
    public void setGrade(String grade) {
        this.grade = grade;
    }
    public float getGradePoint() {
        return gradePoint;
    }
    public void setGradePoint(float gradePoint) {
        this.gradePoint = gradePoint;
    }
}
