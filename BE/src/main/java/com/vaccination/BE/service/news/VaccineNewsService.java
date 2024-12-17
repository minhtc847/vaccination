package com.vaccination.BE.service.news;

import com.vaccination.BE.dto.request.news_request.NewsRequest;
import com.vaccination.BE.dto.response.news_response.NewsResponse;
import com.vaccination.BE.dto.response.news_response.SearchNewsResponse;

public interface VaccineNewsService {
    NewsResponse createNews(NewsRequest request);
    SearchNewsResponse getAllNews(int pageNo, int pageSize, String sortBy, String sortDir, String nameOrId);
    NewsResponse getNewsById(long id);
    void deleteNewsById(long id);
    long[] getAllId(String nameOrId);
    void deleteNews(long[] ids);

    NewsResponse updateNews(NewsRequest request, long id);
}
