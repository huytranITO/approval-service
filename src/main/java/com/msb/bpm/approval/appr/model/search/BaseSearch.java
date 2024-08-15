package com.msb.bpm.approval.appr.model.search;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.msb.bpm.approval.appr.enums.search.SearchOperation;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 18/10/2023, Wednesday
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BaseSearch implements Serializable {

  private List<SearchKey> search;
  private Pageable pageable;

  @Setter
  @Getter
  @ToString
  public static class Pageable {

    @Schema(title = "Số trang, bắt đầu từ 0")
    @Min(0)
    private int page;

    @Schema(title = "Số bản ghi mỗi trang")
    @Min(1)
    private int size;

    @Schema(title = "Thứ tự sắp xếp")
    @NotNull
    private List<String> sorts;
  }

  @Setter
  @Getter
  @ToString
  @NoArgsConstructor
  public static class SearchKey {

    @JsonIgnore
    @Schema(title = "Tìm kiếm theo điều kiện or")
    private boolean orPredicate = false;

    @NotBlank
    @Schema(title = "Trường muốn tìm kiếm")
    private String key;

    @NotBlank
    @Schema(title = "Kiểu so sánh: =, >=, <=, like ....")
    private String operator;

    @NotNull
    @Schema(title = "Giá trị muốn tìm kếm")
    private String value;

    public SearchKey(String key, String operator, String value) {
      this.key = key;
      this.operator = operator;
      this.value = value;
    }

    public SearchKey(boolean orPredicate, String key, String operator, String value) {
      this.orPredicate = orPredicate;
      this.key = key;
      this.operator = operator;
      this.value = value;
    }

    public String isOrOperator() {
      return this.orPredicate ? SearchOperation.OR_PREDICATE_FLAG : null;
    }

    @JsonIgnore
    public String getExpect() {
      String result =
          value.startsWith(SearchOperation.ZERO_OR_MORE_REGEX) ? value.substring(1) : value;
      result = result.endsWith(SearchOperation.ZERO_OR_MORE_REGEX) ? result.substring(0,
          result.length() - 1) : result;
      return result;
    }

    @JsonIgnore
    public String getPrefix() {
      return value.startsWith(SearchOperation.ZERO_OR_MORE_REGEX)
          ? SearchOperation.ZERO_OR_MORE_REGEX : null;
    }

    @JsonIgnore
    public String getSuffix() {
      return value.endsWith(SearchOperation.ZERO_OR_MORE_REGEX) ? SearchOperation.ZERO_OR_MORE_REGEX
          : null;
    }
  }
}
