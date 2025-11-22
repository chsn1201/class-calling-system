package com.Demo.calling.controller;

import com.Demo.calling.model.Student;
import com.Demo.calling.service.StudentService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
public class CallingController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("students", studentService.getAllStudentsOrderedById());
        return "index";
    }

    @GetMapping("/upload")
    public String uploadPage() {
        return "upload";
    }

    @PostMapping("/upload")
    public String handleUpload(@RequestParam("file") MultipartFile file, Model model) {
        try {
            studentService.importFromExcel(file);
            model.addAttribute("message", "导入成功！");
        } catch (Exception e) {
            model.addAttribute("error", "导入失败：" + e.getMessage());
        }
        return "upload";
    }

    @GetMapping("/call/random")
    @ResponseBody
    public Student randomCall() {
        return studentService.getRandomStudent();
    }

    @GetMapping("/call/sequential")
    @ResponseBody
    public Student callNextSequentialStudent() {
        return studentService.getNextSequentialStudent();
    }

    @PostMapping("/score/update")
    @ResponseBody
    public String updateScore(@RequestParam String studentId, @RequestParam double points) {
        studentService.updateStudentScore(studentId, points);
        return "OK";
    }

    @GetMapping("/ranking")
    public String ranking(Model model) {
        model.addAttribute("topStudents", studentService.getTopRankedStudents(10));
        return "ranking";
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        // 1. 设置响应头：内容类型 + 文件名
        response.setContentType("text/csv;charset=UTF-8");
        String filename = URLEncoder.encode("课堂积分详单.csv", "UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);

        // 2. 获取输出流，并写入 UTF-8 BOM（关键！）
        OutputStream out = response.getOutputStream();
        out.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF}); // UTF-8 BOM

        // 3. 包装为 UTF-8 的 PrintWriter
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));

        // 4. 写入 CSV 内容
        writer.println("学号,姓名,专业,随机点名次数,总积分");
        for (Student s : studentService.getAllStudentsForExport()) {
            String major = s.getMajor() == null ? "" : s.getMajor().replace(",", "，"); // 防止专业含逗号破坏CSV结构
            writer.printf("%s,%s,%s,%d,%.2f%n",
                    s.getStudentId(),
                    s.getName(),
                    major,
                    s.getCallCount(),
                    s.getScore());
        }

        writer.flush();
        writer.close(); // 自动 flush 并关闭流
    }

    @GetMapping("/api/ranking")
    @ResponseBody
    public List<Student> getRankingData() {
        return studentService.getTopRankedStudents(10);
    }
}
