package com.vaccination.BE.controller;

import com.vaccination.BE.dto.request.news_request.NewsRequest;
import com.vaccination.BE.dto.response.news_response.NewsResponse;
import com.vaccination.BE.dto.response.news_response.SearchNewsResponse;
import com.vaccination.BE.excepiton.exceptions.APIException;
import com.vaccination.BE.service.news.VaccineNewsService;
import com.vaccination.BE.utils.AppConstants;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/news")
public class VaccineNewsController {
    VaccineNewsService vaccineNewsService;

    public VaccineNewsController(VaccineNewsService vaccineNewsService) {
        this.vaccineNewsService = vaccineNewsService;
    }
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public NewsResponse createNews(@ModelAttribute @Valid NewsRequest request){
        MultipartFile content = request.getContent();
        if(content.getSize() >= 1024*1024*5){
            throw new APIException(HttpStatus.PAYLOAD_TOO_LARGE, "Content must be less than 5MB");
        }
        return vaccineNewsService.createNews(request);
    }

    @GetMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public SearchNewsResponse getAllNews(
            @RequestParam(name = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir,
            @RequestParam(name = "nameOrId", required = false) String nameOrId){
        return vaccineNewsService.getAllNews(pageNo,pageSize,sortBy,sortDir,nameOrId);
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public NewsResponse getNewsById(@PathVariable long id){
        return vaccineNewsService.getNewsById(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteNewsById(@PathVariable long id){
        vaccineNewsService.deleteNewsById(id);
        return "News deleted successfully";
    }

    @GetMapping("/getAllNewId")
//    @PreAuthorize("hasRole('ADMIN')")
    public long[] getAllNewId(@RequestParam(name = "nameOrId", required = false) String nameOrId) {
        return vaccineNewsService.getAllId(nameOrId);
    }
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteNews(@RequestBody long[] ids) {
        vaccineNewsService.deleteNews(ids);
        return "News deleted successfully";
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public NewsResponse updateNews(@ModelAttribute NewsRequest request,@PathVariable long id){
        return vaccineNewsService.updateNews(request,id);
    }
}
