package com.vaccination.BE.service.vaccine;

import com.vaccination.BE.dto.request.vaccine_request.VaccineRequest;
import com.vaccination.BE.dto.request.vaccine_type_request.StatusRequest;
import com.vaccination.BE.dto.response.vaccine_response.SearchVaccineResponse;
import com.vaccination.BE.dto.response.vaccine_response.VaccineResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VaccineService {
    VaccineResponse createVaccine(VaccineRequest request);

    VaccineResponse updateVaccine(long id, VaccineRequest request);
    SearchVaccineResponse getListVaccine(int pageNo, int pageSize, String sortBy, String sortDir, String nameOrId);
    void changeVaccineStatus (List<StatusRequest> requests);
    ResponseEntity<String> uploadDataExcelFile(MultipartFile file);
    VaccineResponse getVaccineById(long id);
    long[] getAllId(String nameOrId);


}
