package com.vaccination.BE.service.vaccinetype;

import com.vaccination.BE.dto.request.vaccine_type_request.StatusRequest;
import com.vaccination.BE.dto.request.vaccine_type_request.VaccineTypeRequest;
import com.vaccination.BE.dto.request.vaccine_type_request.VaccineTypeUpdateRequest;
import com.vaccination.BE.dto.response.vaccine_type_response.VaccineTypeResponse;
import com.vaccination.BE.dto.response.vaccine_type_response.VaccineTypeSearchResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VaccineTypeService {
    VaccineTypeResponse createVaccineType(VaccineTypeRequest request, MultipartFile file);
    VaccineTypeSearchResponse getListVaccineType(int pageNo, int pageSize, String sortBy, String sortDir, String nameOrId);
    void makeVaccineTypesInactive(long[] ids);
    VaccineTypeResponse updateVaccineType(long id, VaccineTypeUpdateRequest request, MultipartFile file);
    public void changeVaccineTypeStatus(List<StatusRequest> requests);
    public VaccineTypeResponse getVaccineTypeById(long id);
    long[] getAllVaccineTypeId(String nameOrId);
}
