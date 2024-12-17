package com.vaccination.BE.service.vaccinetype;

import com.vaccination.BE.dto.request.vaccine_type_request.StatusRequest;
import com.vaccination.BE.dto.request.vaccine_type_request.VaccineTypeRequest;
import com.vaccination.BE.dto.request.vaccine_type_request.VaccineTypeUpdateRequest;
import com.vaccination.BE.dto.response.cloudinary_response.CloudinaryResponse;
import com.vaccination.BE.dto.response.vaccine_type_response.VaccineTypeResponse;
import com.vaccination.BE.dto.response.vaccine_type_response.VaccineTypeSearchResponse;
import com.vaccination.BE.entity.VaccineVaccineType;
import com.vaccination.BE.excepiton.exceptions.APIException;
import com.vaccination.BE.excepiton.exceptions.ResourceNotFoundException;
import com.vaccination.BE.mapper.VaccineTypeMapper;
import com.vaccination.BE.repository.VaccineRepository;
import com.vaccination.BE.repository.VaccineTypeRepository;
import com.vaccination.BE.service.cloudinary.CloudinaryService;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VaccineTypeServiceImpl implements VaccineTypeService {

    VaccineRepository vaccineRepository;
    VaccineTypeRepository vaccineTypeRepository;
    VaccineTypeMapper vaccineTypeMapper;
    CloudinaryService cloudinaryService;

    public VaccineTypeServiceImpl(VaccineTypeRepository vaccineTypeRepository, VaccineTypeMapper vaccineTypeMapper, CloudinaryService cloudinaryService,VaccineRepository vaccineRepository) {
        this.vaccineTypeRepository = vaccineTypeRepository;
        this.vaccineTypeMapper = vaccineTypeMapper;
        this.cloudinaryService = cloudinaryService;
       this.vaccineRepository =vaccineRepository;
    }

    @Override
    public VaccineTypeResponse createVaccineType(VaccineTypeRequest request, MultipartFile file) {
        VaccineVaccineType vaccineType = vaccineTypeMapper.toVaccineType(request);
        vaccineType = vaccineTypeRepository.saveAndFlush(vaccineType);
        
        String code = "VT" + String.format("%04d", vaccineType.getId());
        vaccineType.setCode(code);
        if (file != null && !file.isEmpty()) {
            CloudinaryResponse cloudinaryResponse = cloudinaryService.uploadImage(file);
            vaccineType.setImage(cloudinaryResponse.getUrl());
        }
        vaccineType.setStatus(true);
        try {
            vaccineTypeRepository.save(vaccineType);
        } catch (DataIntegrityViolationException e) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Vaccine Type Name is existed");

        }
        VaccineTypeResponse vaccineTypeResponse = new VaccineTypeResponse();
        vaccineTypeMapper.vaccineTypeToVaccineTypeResponse(vaccineTypeResponse, vaccineType);
        vaccineTypeResponse.setId(vaccineType.getId());
        return vaccineTypeResponse;
    }

    @Override
    public VaccineTypeSearchResponse getListVaccineType(int pageNo, int pageSize, String sortBy, String sortDir, String nameOrId) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageRequest = PageRequest.of(pageNo, pageSize, sort);
        Page<VaccineVaccineType> page;
        if (nameOrId == null) {
            page = vaccineTypeRepository.findAll(pageRequest);
        } else if (nameOrId.matches("\\d+")) {
            page = vaccineTypeRepository.findByIdContaining(nameOrId, pageRequest);
        } else {
            page = vaccineTypeRepository.findByVaccineTypeNameContaining(nameOrId, pageRequest);
        }
        List<VaccineVaccineType> vaccineVaccineTypes = page.getContent();
        List<VaccineTypeResponse> content = vaccineVaccineTypes.stream().map(employee -> {
                    VaccineTypeResponse vaccineTypeResponse = new VaccineTypeResponse();
                    vaccineTypeMapper.vaccineTypeToVaccineTypeResponse(vaccineTypeResponse, employee);
                    return vaccineTypeResponse;
                }
        ).collect(Collectors.toList());
        VaccineTypeSearchResponse searchResponse = VaccineTypeSearchResponse.builder()
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
    public void makeVaccineTypesInactive(long[] ids) {
        for (long id : ids) {
            VaccineVaccineType vaccineType = vaccineTypeRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("VaccineType", "id", String.valueOf(id)));
            vaccineType.setStatus(!vaccineType.isStatus());
            vaccineTypeRepository.save(vaccineType);
        }
    }


    public VaccineTypeResponse updateVaccineType(long id, VaccineTypeUpdateRequest request, MultipartFile file) {
        VaccineVaccineType vaccineType = vaccineTypeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("VaccineType", "Id", String.valueOf(id))
                );

        if (!request.getVaccineTypeName().equals(vaccineType.getVaccineTypeName())) {
            if (vaccineTypeRepository.existsByVaccineTypeName(request.getVaccineTypeName())) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Vaccine type name already exists!");
            }
        }

        cloudinaryService.deleteImage(vaccineType.getImage());

        if (file != null && !file.isEmpty()) {
            CloudinaryResponse cloudinaryResponse = cloudinaryService.uploadImage(file);
            vaccineType.setImage(cloudinaryResponse.getUrl());
        }

        vaccineType.setDescription(request.getDescription());
        vaccineType.setVaccineTypeName(request.getVaccineTypeName());
        vaccineType.setStatus(request.getStatus());
        vaccineTypeRepository.save(vaccineType);

        VaccineTypeResponse vaccineTypeResponse = new VaccineTypeResponse();
        vaccineTypeMapper.vaccineTypeToVaccineTypeResponse(vaccineTypeResponse, vaccineType);
        vaccineTypeResponse.setId(id);

        return vaccineTypeResponse;
    }

    @Transactional
    @Override
    public void changeVaccineTypeStatus(List<StatusRequest> requests) {
        for (StatusRequest request : requests) {
            System.out.println(request.getId()+ " day ");
         // check vaccine type in vaccine_vaccine
//            System.out.println(vaccineRepository.findByVaccineTypeId(request.getId(),true));
            if (vaccineRepository.existsByVaccineTypeId(request.getId(), true)) {
                throw new APIException(HttpStatus.BAD_REQUEST, "The type of vaccine that remains in the Vaccine");
            }

            System.out.println("ke");
            VaccineVaccineType vaccineType = vaccineTypeRepository.findById(request.getId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("VaccineType", "id", String.valueOf(request.getId()))
                    );
            vaccineType.setStatus(false);
            vaccineTypeRepository.save(vaccineType);
        }
    }

    @Override
    public VaccineTypeResponse getVaccineTypeById(long id) {
        VaccineVaccineType vaccineType = vaccineTypeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("VaccineType", "id", String.valueOf(id))
                );
        VaccineTypeResponse vaccineTypeResponse = new VaccineTypeResponse();
        vaccineTypeMapper.vaccineTypeToVaccineTypeResponse(vaccineTypeResponse, vaccineType);
        vaccineTypeResponse.setStatus(vaccineType.isStatus());
        vaccineTypeResponse.setId(vaccineType.getId());
        return vaccineTypeResponse;
    }

    @Override
    public long[] getAllVaccineTypeId(String nameOrId) {
        List<VaccineVaccineType> vaccineVaccineTypes = vaccineTypeRepository.findAllExceptInactive();
        long[] ids;
        if (nameOrId == null) {
            ids = vaccineVaccineTypes.stream().mapToLong(VaccineVaccineType::getId).toArray();
        } else if (nameOrId.matches("\\d+")) {
            ids = vaccineVaccineTypes.stream().filter(vaccineVaccineType -> Long.toString(vaccineVaccineType.getId()).contains(nameOrId)).mapToLong(VaccineVaccineType::getId).toArray();
        } else {
            ids = vaccineVaccineTypes.stream().filter(vaccineVaccineType -> vaccineVaccineType.getVaccineTypeName().toLowerCase().contains(nameOrId.toLowerCase())).mapToLong(VaccineVaccineType::getId).toArray();
        }
        return ids;
    }
}
