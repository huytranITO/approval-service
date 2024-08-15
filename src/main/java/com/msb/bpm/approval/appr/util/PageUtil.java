package com.msb.bpm.approval.appr.util;

import com.msb.bpm.approval.appr.model.search.BaseSearch.Pageable;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 18/10/2023, Wednesday
 **/
@UtilityClass
public class PageUtil {

  public static org.springframework.data.domain.Pageable getPageable(Pageable page) {
    Sort sort;
    if (page.getSorts().isEmpty()) {
      sort = Sort.by(Sort.Order.desc("id"));
    } else {
      sort = Sort.by(page.getSorts().stream()
          .map(el -> el.split(","))
          .map(ar -> new Sort.Order(Sort.Direction.fromString(ar[1]), ar[0]))
          .collect(Collectors.toList()));
    }
    return PageRequest.of(page.getPage(), page.getSize(), sort);
  }
}
