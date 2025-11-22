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

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepo;

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
}
