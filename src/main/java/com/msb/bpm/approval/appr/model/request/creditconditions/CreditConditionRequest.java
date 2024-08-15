package com.msb.bpm.approval.appr.model.request.creditconditions;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreditConditionRequest {
	private Long id;
    @NotNull(message = "Thiếu thông tin điều kiện chi tiết")
    @NotBlank(message = "Điều kiện chi tiết không được rỗng")
    private String detail;
    @NotNull(message = "Thiếu thông tin ĐV kiểm soát")
    @NotBlank(message = "ĐV kiểm soát không được rỗng")
    private String controlLevel;
    @NotNull(message = "Thiếu thông tin state")
    @NotBlank(message = "ĐV kiểm soát state")
    private String state;
    @NotNull(message = "Thiếu thông tin Đối tượng áp dụng")
    @NotBlank(message = "Đối tượng áp dụng không được rỗng")
    private String objectApply;
    @NotNull(message = "Thiếu thông tin Thời điểm kiểm soát")
    @NotBlank(message = "Thời điểm kiểm soát không được rỗng")
    private String timeControl;
    @NotNull(message = "Thiếu thông tin Nhóm điều kiện")
    @NotBlank(message = "Nhóm điều kiện không được rỗng")
    private String creditConditionGroup;
    @NotNull(message = "Thiếu thông tin Mã điều kiện")
    @NotBlank(message = "Mã điều kiện không được rỗng")
    private String conditionGroupCode;
    @NotNull(message = "Thiếu thông tin nguồn hệ thống")
    @NotBlank(message = "Nguồn hệ thống không được rỗng")
    private String source;
    private Boolean allowDelete;
    private Long policyConditionId;
	  private String timeControlDisburse;
    private List<CreditConditionParamsRequest> creditConditionParams = new ArrayList<>();
}
