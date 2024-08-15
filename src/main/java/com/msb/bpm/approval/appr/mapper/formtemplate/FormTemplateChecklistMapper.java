package com.msb.bpm.approval.appr.mapper.formtemplate;

import com.msb.bpm.approval.appr.model.dto.checklist.ChecklistDto;
import com.msb.bpm.approval.appr.model.dto.checklist.FileDto;
import com.msb.bpm.approval.appr.model.dto.formtemplate.request.FormTemplateChecklistRequestDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.request.FormTemplateFileRequestDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.response.ChecklistFileMessageDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.response.ChecklistMessageResponseDTO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface FormTemplateChecklistMapper {

  FormTemplateChecklistRequestDTO toFormTemplateChecklistRequestDTO(ChecklistDto checklistDto);

  FormTemplateFileRequestDTO toFormTemplateChecklistRequestDTO(FileDto checklistDto);

  List<ChecklistDto> toListChecklistDto(List<ChecklistMessageResponseDTO> listSource);

  @Mapping(source = "checklistFiles", target = "listFile", qualifiedByName = "mapToListFileDTO")
  ChecklistDto toChecklistDto(ChecklistMessageResponseDTO source);

  @Named("mapToListFileDTO")
  List<FileDto> toListFileDto(List<ChecklistFileMessageDTO> listSource);

  @Mapping(source = "filePath", target = "minioPath")
  @Mapping(source = "code", target = "props", qualifiedByName = "mapProps")
  FileDto toChecklistDto(ChecklistFileMessageDTO source);

  @Named("mapProps")
  static Map<String, String> mapProps(String templateCode) {
    Map<String, String> mapProps = new HashMap<>();
    mapProps.put("templateCode", templateCode);
    return mapProps;
  }
}
