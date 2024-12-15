package com.event.cia103g1springboot.event.evtcontroller;

import com.event.cia103g1springboot.event.evtimgmodel.EvtImgService;
import com.event.cia103g1springboot.event.evtimgmodel.EvtImgVO;
import com.event.cia103g1springboot.event.evtmodel.EvtService;
import com.event.cia103g1springboot.event.evtmodel.EvtVO;
import com.event.cia103g1springboot.plan.plan.model.Plan;
import com.event.cia103g1springboot.plan.plan.model.PlanService;
import com.event.cia103g1springboot.plan.planorder.model.PlanOrder;
import com.event.cia103g1springboot.plan.planorder.model.PlanOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.validation.Valid;
import java.beans.PropertyEditorSupport;
import java.io.File;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
@Validated
@RequestMapping("/event")
@Controller
public class EvtController {

    @Autowired
    EvtService evtService;

    @Autowired
    EvtImgService evtImgService;

    @Autowired
    private PlanService planService;
    @Autowired
    private PlanOrderService planOrderService;

    //可以把前端傳進來的DATETIMELOCAL字串轉成符合TIMESTAMP格式 超爽

    @GetMapping("/listall")
    public String listAll(@RequestParam(defaultValue = "0") Integer page, Model model,EvtVO evtVO) {
        //jPA的PAGE讚讚好用
        Page<EvtVO> evtPage = evtService.getAllEvts(page);
        model.addAttribute("list", evtPage);
        return "back-end/evt/evtlistall";
    }

    @Transactional
    @PostMapping("/add")
    public String addEvent(@Valid @ModelAttribute EvtVO evtVO,
                           @RequestParam("files") MultipartFile[] files,
                           RedirectAttributes redirectAttributes) {
        try {
            EvtVO savedEvt = evtService.addEvt(evtVO);

            //圖片放的路徑
            String resourcePath = new File("src/main/resources/static/evtimg").getAbsolutePath();
            File uploadDir = new File(resourcePath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            if (files != null && files.length > 0) {
                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {

                        String originalFilename = file.getOriginalFilename();
                        //去除點前面的檔名
                        //用現在時間生成唯一ㄉ
                        String extension = originalFilename.substring(
                                originalFilename.lastIndexOf("."));
                        String fileName = System.currentTimeMillis() + extension;
                        //把檔案存到指定路徑
                        File upload = new File(uploadDir, fileName);
                        file.transferTo(upload);
                        //寫進資料庫
                        EvtImgVO evtImgVO = new EvtImgVO(
                                fileName,
                                Files.readAllBytes(upload.toPath())
                        );
                        evtImgVO.setEvtVO(savedEvt);
                        evtImgService.addEventImage(evtImgVO);
                    }
                }
            }

            redirectAttributes.addFlashAttribute("success", "活動新增成功！");
            return "redirect:/sucessandfail";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "活動新增失敗：" + e.getMessage());
            return "redirect:/sucessandfail";
        }
    }
    @Transactional
    @GetMapping("/publish")
    //修改活動狀態ㄉ按鈕
    public String publish(@RequestParam Integer id, @RequestParam Integer status) {
        evtService.updateEvtStatus(id, status);
        return "redirect:/event/listall";
    }

    @Transactional
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        //根據id拿活動
        EvtVO evt = evtService.getOneEvt(id);
        //根據活動id拿圖
        List<EvtImgVO> evtImgs = evtImgService.getImagesByEvtId(id);

        model.addAttribute("evt", evt);
        model.addAttribute("evtImgs", evtImgs);
        return "back-end/evt/evteditpage";
    }

    @Transactional
    @PostMapping("/edit")
    public String edit(@Valid @ModelAttribute EvtVO evtVO,
                       @RequestParam("files") MultipartFile[] files,
                       RedirectAttributes redirectAttributes) {
        try {
            // 刪除資料庫的舊圖片
            List<EvtImgVO> oldImages = evtImgService.getImagesByEvtId(evtVO.getEvtId());
            if (oldImages != null) {
                for (EvtImgVO oldImage : oldImages) {
                    evtImgService.deleteEventImage(oldImage.getEvtImgId());
                    // 刪除路徑的檔案
                    String resourcePath = new File("src/main/resources/static/evtimg").getAbsolutePath();
                    File oldFile = new File(resourcePath, oldImage.getEvtImgName());
                    if (oldFile.exists()) {
                        oldFile.delete();
                    }
                }
            }
            // 更新活動資訊
            EvtVO updatedEvt = evtService.addEvt(evtVO);
            //處理新圖片上傳
            String resourcePath = new File("src/main/resources/static/evtimg").getAbsolutePath();
            File uploadDir = new File(resourcePath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            if (files != null && files.length > 0) {
                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        // 生成唯一檔名,跟新增一樣邏輯
                        String originalFilename = file.getOriginalFilename();
                        String extension = originalFilename.substring(
                                originalFilename.lastIndexOf("."));
                        String fileName = System.currentTimeMillis() + extension;

                        File upload = new File(uploadDir, fileName);
                        file.transferTo(upload);

                        EvtImgVO evtImgVO = new EvtImgVO(
                                fileName,
                                Files.readAllBytes(upload.toPath())
                        );
                        evtImgVO.setEvtVO(updatedEvt);
                        evtImgService.addEventImage(evtImgVO);
                    }
                }
            }
            //跳轉一個成功頁面,可以存一次加重定向 讚讚
            redirectAttributes.addFlashAttribute("success", "活動修改成功！");
            return "redirect:/sucessandfail";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "活動修改失敗：" + e.getMessage());
            return "redirect:/sucessandfail";
        }
    }

    @GetMapping("/calendar")
    public String showCalendar(Model model, @RequestParam(required = false) String date) {

        LocalDate currentDate;

        currentDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : LocalDate.now();
        // 獲取活動列表
        List<EvtVO> events = evtService.getEventsForWeek(currentDate);

        model.addAttribute("currentDate", currentDate);
        //拿上週
        model.addAttribute("prevWeek", currentDate.minusWeeks(1).toString());
        //拿下周
        model.addAttribute("nextWeek", currentDate.plusWeeks(1).toString());
        model.addAttribute("events", events);
        return "front-end/evt/calendar";
    }




}

//    測試用--------------------------------------//
//    System.out.println("Controller - 當前日期: " + currentDate);
//        System.out.println("Controller - 活動數量: " + events.size());
//        events.forEach(event -> {
//        System.out.println("活動: " + event.getEvtName() +
//                ", 日期: " + event.getEvtDate() +
//                ", 開始時間: " + event.getEvtStart());
//    });

//        System.out.println("==========進入 add 方法==========");
//        // 轉換日期時間
//        System.out.println("接收到的數據：");
//        System.out.println("msgtype: " + evtVO.getEvtId());
//        System.out.println("msgtitle: " + evtVO.getEvtName());
//        System.out.println("msgcon: " + evtVO.getEvtDesc());
//        System.out.println("poststat: " + evtVO.getEvtDate());
//        System.out.println("posttime: " + evtVO.getEvtStart());
//        System.out.println("posttime: " + evtVO.getEvtDeadLine());
//        System.out.println("posttime: " + evtVO.getEvtDate());
//        System.out.println("posttime: " + evtVO.getEvtStat());
//    ----------------------------------------------
//    Map<String, Object> debug = new HashMap<>();
//        debug.put("currentDate", currentDate);
//        debug.put("weekStart", currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
//        debug.put("weekEnd", currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).plusDays(6));
//        debug.put("eventsCount", events.size());
//        model.addAttribute("debug", debug);


// 輸出調試信息
//        System.out.println("Current date: " + currentDate);
//        System.out.println("Found events: " + events.size());
//        events.forEach(event -> {
//        System.out.println("Event: " + event.getEvtName() +
//                ", Date: " + event.getEvtDate() +
//                ", Start: " + event.getEvtStart());
//    });




