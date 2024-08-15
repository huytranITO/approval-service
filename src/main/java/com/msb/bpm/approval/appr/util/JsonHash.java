package com.msb.bpm.approval.appr.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import javax.xml.bind.DatatypeConverter;
import lombok.experimental.UtilityClass;
import org.springframework.util.DigestUtils;

/*
* @author: BaoNV2
* @since: 23/8/2023 1:53 PM
*
* */
@UtilityClass
public class JsonHash {
  private static final ObjectMapper objectMapper = new ObjectMapper();
  public static Boolean compare(String target, String source) {
    if (Objects.isNull(target) || Objects.isNull(source)) return Boolean.FALSE;
    try {
      if (getHash(objectMapper, target).equals(getHash(objectMapper, source))) {
        return Boolean.TRUE;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return  Boolean.FALSE;
  }

  private static String getHash(ObjectMapper mapper, String jsonStringA) throws IOException {
    JsonNode jsonNode = mapper.readTree(jsonStringA);
    Map map = mapper.convertValue(jsonNode, Map.class);
    TreeMap sorted = sort(map);
    String s = mapper.writeValueAsString(sorted);
    byte[] md5Digest = DigestUtils.md5Digest(s.getBytes());
    return DatatypeConverter.printHexBinary(md5Digest).toUpperCase();
  }

  private static TreeMap sort(Map map) {
    TreeMap result = new TreeMap();
    map.forEach((k, v) -> {
      if(v != null) {
        if (v instanceof Map) {
          result.put(k, sort((Map) v));
        } else if (v instanceof List) {
          result.put(k, copyArray((List) v));
        } else {
          result.put(k, v);
        }
      } else {
        result.put(k, null);
      }
    });
    return result;
  }

  private static List copyArray(List v) {
    List result = new ArrayList(v.size());

    for (int i = 0; i < v.size(); i++) {
      Object element = v.get(i);

      if(element != null) {
        if (element instanceof Map) {
          result.add(sort((Map) element));
        } else if (element instanceof List) {
          result.add(copyArray((List) element));
        } else {
          result.add(element);
        }
      } else {
        result.add(null);
      }
    }
    return result;
  }

}
