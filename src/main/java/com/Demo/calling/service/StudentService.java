package com.Demo.calling.service;

import com.Demo.calling.model.Student;
import com.Demo.calling.repository.StudentRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepo;

    private final AtomicInteger sequentialIndex = new AtomicInteger(0);

    public void importFromExcel(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String studentId = getStringValue(row.getCell(0));
                String name = getStringValue(row.getCell(1));
                String major = getStringValue(row.getCell(2));

                if (studentId == null || studentId.trim().isEmpty()) continue;

                Student student = new Student();
                student.setStudentId(studentId);
                student.setName(name);
                student.setMajor(major);
                studentRepo.save(student);
            }
        }
    }

    private String getStringValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    double val = cell.getNumericCellValue();
                    if (val == Math.floor(val)) {
                        return String.valueOf((long) val);
                    } else {
                        return String.valueOf(val);
                    }
                }
            default: return "";
        }
    }

    public Student getRandomStudent() {
        List<Student> all = studentRepo.findAll();
        if (all.isEmpty()) return null;

        // 权重：积分越高，权重越低
        double totalWeight = all.stream().mapToDouble(s -> Math.max(1.0, 10 - s.getScore())).sum();
        double rand = Math.random() * totalWeight;
        double current = 0;

        for (Student s : all) {
            current += Math.max(1.0, 10 - s.getScore());
            if (current >= rand) {
                s.incrementCallCount();
                studentRepo.save(s);
                return s;
            }
        }
        return all.get(0); // fallback
    }

    public List<Student> getAllStudentsOrderedById() {
        return studentRepo.findAllByOrderByStudentIdAsc();
    }

    public List<Student> getTopRankedStudents(int limit) {
        return studentRepo.findAllByOrderByScoreDesc().stream().limit(limit).toList();
    }

    public void updateStudentScore(String studentId, double scoreChange) {
        Student s = studentRepo.findByStudentId(studentId);
        if (s != null) {
            s.addScore(scoreChange);
            studentRepo.save(s);
        }
    }

    public List<Student> getAllStudentsForExport() {
        return studentRepo.findAll();
    }

    public Student getNextSequentialStudent() {
        List<Student> students = getAllStudentsOrderedById();
        if (students.isEmpty()) {
            return null;
        }

        int currentIndex = sequentialIndex.getAndIncrement();
        int index = currentIndex % students.size(); // 循环取模
        Student student = students.get(index);

        // 增加点名次数（与随机点名行为一致）
        student.incrementCallCount();
        studentRepo.save(student);

        return student;
    }
}
