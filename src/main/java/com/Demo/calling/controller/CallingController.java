package com.Demo.calling.controller;

import com.Demo.calling.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class CallingController {

    @Autowired
    private StudentService studentService;

    /**
     * excel导入
     * @param file
     * @param model
     * @return
     */
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
}
