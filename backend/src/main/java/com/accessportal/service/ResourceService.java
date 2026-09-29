package com.accessportal.service;

import com.accessportal.dto.CreateResourceDto;
import com.accessportal.dto.ResourceResponseDto;
import com.accessportal.entity.Resource;
import com.accessportal.exception.ResourceNotFoundException;
import com.accessportal.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public List<ResourceResponseDto> listResources() {
        return resourceRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public ResourceResponseDto createResource(CreateResourceDto dto) {
        Resource resource = Resource.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .resourceType(dto.getResourceType())
                .build();

        return toDto(resourceRepository.save(resource));
    }

    public void deleteResource(Long id) {
        if (!resourceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Resource not found with id: " + id);
        }
        resourceRepository.deleteById(id);
    }

    private ResourceResponseDto toDto(Resource resource) {
        return ResourceResponseDto.builder()
                .id(resource.getId())
                .name(resource.getName())
                .description(resource.getDescription())
                .resourceType(resource.getResourceType())
                .build();
    }
}
