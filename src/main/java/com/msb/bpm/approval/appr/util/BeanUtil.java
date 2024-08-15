package com.msb.bpm.approval.appr.util;


import com.google.gson.Gson;

/*
* @author: BaoNV2
* @since: 16/8/2023 3:11 PM
* @description:
* @update:
*
* */
public class BeanUtil {

  public static<E, T> T copyBean(E source, Class<T> destinationType) {
    if (source == null) return null;
    try {
      Gson gson = new Gson();
      String copyData = gson.toJson(source);
      return gson.fromJson(copyData, destinationType);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
