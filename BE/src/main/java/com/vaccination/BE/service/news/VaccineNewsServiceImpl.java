package com.vaccination.BE.service.news;

import com.vaccination.BE.dto.request.news_request.NewsRequest;
import com.vaccination.BE.dto.response.news_response.NewsResponse;
import com.vaccination.BE.dto.response.news_response.SearchNewsResponse;
import com.vaccination.BE.entity.VaccineNew;
import com.vaccination.BE.excepiton.exceptions.ResourceNotFoundException;
import com.vaccination.BE.repository.VaccineNewsRepository;
import com.vaccination.BE.service.cloudinary.CloudinaryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VaccineNewsServiceImpl implements VaccineNewsService {
    VaccineNewsRepository newsRepository;
    CloudinaryService cloudinaryService;

    public VaccineNewsServiceImpl(VaccineNewsRepository newsRepository, CloudinaryService cloudinaryService) {
        this.newsRepository = newsRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public NewsResponse createNews(NewsRequest request) {
        String url=cloudinaryService.uploadHtml(request.getContent(),request.getTitle().replaceAll(" ",""));
        VaccineNew vaccine = VaccineNew.builder().title(request.getTitle()).content(url).preview(request.getPreview()).date(LocalDate.now()).build();
        VaccineNew vaccineNew =newsRepository.save(vaccine);
        NewsResponse newsResponse = NewsResponse.builder().id(vaccineNew.getId()).content(vaccineNew.getContent()).preview(vaccineNew.getPreview()).title(vaccineNew.getTitle()).date(vaccineNew.getDate()).build();
        return newsResponse;
    }

    @Override
    public SearchNewsResponse getAllNews(int pageNo, int pageSize, String sortBy, String sortDir, String nameOrId) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageRequest = PageRequest.of(pageNo, pageSize, sort);
        Page<VaccineNew> page;
        if (nameOrId == null) {
            page = newsRepository.findAll(pageRequest);
        } else {
            page = newsRepository.findByVaccineNameContaining(nameOrId, pageRequest);
        }
        List<VaccineNew> vaccineNewsList = page.getContent();
        List<NewsResponse> content = vaccineNewsList.stream().map(vaccineNews -> {
            NewsResponse vacineNewsResponse = NewsResponse.builder()
                    .id(vaccineNews.getId())
                    .content(vaccineNews.getContent())
                    .preview(vaccineNews.getPreview())
                    .title(vaccineNews.getTitle())
                    .date(vaccineNews.getDate()).build();
            return vacineNewsResponse;
                }
        ).collect(Collectors.toList());
        SearchNewsResponse searchResponse = SearchNewsResponse.builder()
                .totalElements(page.getTotalElements())
                .content(content)
                .last(page.isLast())
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPaged(page.getTotalPages())
                .build();
        return searchResponse;
    }

    @Override
    public NewsResponse getNewsById(long id) {
        VaccineNew vaccine = newsRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("News", "id", String.valueOf(id)));
        NewsResponse newsResponse = NewsResponse.builder().id(vaccine.getId()).content(vaccine.getContent()).preview(vaccine.getPreview()).title(vaccine.getTitle()).date(vaccine.getDate()).build();
        return newsResponse;
    }

    @Override
    public void deleteNewsById(long id) {
        VaccineNew vaccine = newsRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("News", "id", String.valueOf(id)));
        cloudinaryService.deleteFile(vaccine.getContent());
        newsRepository.delete(vaccine);
    }

    @Override
    public long[] getAllId(String nameOrId) {
        List<VaccineNew> list = newsRepository.findAllById();
        long ids[];
        if (nameOrId == null) {
            ids = list.stream().mapToLong(vaccineNew -> vaccineNew.getId()).toArray();
        } else {
            ids = list.stream().filter(vaccineNew -> vaccineNew.getTitle().toLowerCase().contains(nameOrId.toLowerCase())).mapToLong(vaccineNew -> vaccineNew.getId()).toArray();
        }
        return ids;
    }

    @Override
    @Transactional
    public void deleteNews(long[] ids) {
        for (long id : ids) {
            VaccineNew vaccine = newsRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("News", "id", String.valueOf(id)));
            cloudinaryService.deleteFile(vaccine.getContent());
            newsRepository.delete(vaccine);
        }
    }

    @Override
    public NewsResponse updateNews(NewsRequest request, long id) {
        VaccineNew vaccine = newsRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("News", "id", String.valueOf(id)));

        cloudinaryService.deleteFile(vaccine.getContent());
        String url=cloudinaryService.uploadHtml(request.getContent(),request.getTitle().replaceAll(" ",""));

        vaccine.setContent(url);
        vaccine.setPreview(request.getPreview());
        vaccine.setTitle(request.getTitle());
        VaccineNew vaccineNew = newsRepository.save(vaccine);
        return NewsResponse.builder().id(vaccineNew.getId()).content(vaccineNew.getContent()).preview(vaccineNew.getPreview()).title(vaccineNew.getTitle()).date(vaccineNew.getDate()).build();
    }

}
