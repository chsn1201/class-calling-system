package com.Demo.calling.model;

import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String studentId; // 学号

    private String name;
    private String major;

    private int callCount = 0; // 随机点名次数
    private double score = 0.0; // 总积分

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public int getCallCount() { return callCount; }
    public void setCallCount(int callCount) { this.callCount = callCount; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    // 辅助方法：增加积分
    public void addScore(double points) {
        this.score += points;
    }

    // 辅助方法：增加点名次数
    public void incrementCallCount() {
        this.callCount++;
    }
}
