package com.event.cia103g1springboot.member.emp.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.event.cia103g1springboot.member.emp.model.EmployeeService;
import com.event.cia103g1springboot.member.emp.model.EmployeeVO;

/**
 * 員工控制器類
 * 提供員工登錄、註冊、更新資料、重設密碼等功能
 */
@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 員工登錄
     * @param loginData 包含員工賬號和密碼
     * @param session 用於存儲登錄用戶信息
     * @return 登錄成功返回員工信息，否則返回錯誤信息
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData, HttpSession session) {
        EmployeeVO employee = employeeService.login(loginData.get("empAcct"), loginData.get("empPwd"));
        if (employee != null) {
            // 將用戶信息存儲在 session 中
            session.setAttribute("loginUser", employee);
            return ResponseEntity.ok(employee);
        }
        return ResponseEntity.badRequest().body("登錄失敗");
    }

    /**
     * 員工登出
     * @param session 用於清除登錄用戶信息
     * @return 登出成功信息
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("登出成功");
    }

    /**
     * 員工註冊
     * @param employee 包含員工信息
     * @return 註冊成功或失敗信息
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody EmployeeVO employee) {
        if (employeeService.register(employee)) {
            return ResponseEntity.ok("註冊成功");
        }
        return ResponseEntity.badRequest().body("註冊失敗");
    }

    /**
     * 獲取員工資料
     * @param empId 員工ID
     * @return 員工資料
     */
    @GetMapping("/{empId}")
    public ResponseEntity<EmployeeVO> getProfile(@PathVariable Integer empId) {
        EmployeeVO employee = employeeService.getEmployeeProfile(empId);
        return ResponseEntity.ok(employee);
    }

    /**
     * 更新員工資料
     * @param empId 員工ID
     * @param empName 員工姓名
     * @param empJobTitle 員工職稱
     * @param empImg 員工圖片（可選）
     * @return 更新成功或失敗信息
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestParam("empId") Integer empId,
            @RequestParam("empName") String empName,
            @RequestParam("empJobTitle") String empJobTitle,
            @RequestParam(value = "empImg", required = false) MultipartFile empImg) {
        
        try {
            EmployeeVO employee = new EmployeeVO();
            employee.setEmpId(empId);
            employee.setEmpName(empName);
            employee.setEmpJobTitle(empJobTitle);
            if (empImg != null && !empImg.isEmpty()) {
                employee.setEmpImg(empImg.getBytes());
            }

            if (employeeService.updateProfile(employee)) {
                return ResponseEntity.ok("更新成功");
            }
            return ResponseEntity.badRequest().body("更新失敗");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("文件處理失敗");
        }
    }

    /**
     * 停用員工
     * @param empId 員工ID
     * @return 停用成功或失敗信息
     */
    @PutMapping("/deactivate/{empId}")
    public ResponseEntity<?> deactivateEmployee(@PathVariable Integer empId) {
        if (employeeService.deactivateEmployee(empId)) {
            return ResponseEntity.ok("停用成功");
        }
        return ResponseEntity.badRequest().body("停用失敗");
    }

    /**
     * 重設密碼
     * @param passwordData 包含員工賬號和新密碼
     * @return 重設密碼成功或失敗信息
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> passwordData) {
        if (employeeService.resetPassword(passwordData.get("empAcct"), passwordData.get("newPassword"))) {
            return ResponseEntity.ok("密碼重置成功");
        }
        return ResponseEntity.badRequest().body("密碼重置失敗");
    }

    /**
     * 獲取所有員工列表
     * @return 員工列表
     */
    @GetMapping("/list")
    public ResponseEntity<List<EmployeeVO>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }
}
