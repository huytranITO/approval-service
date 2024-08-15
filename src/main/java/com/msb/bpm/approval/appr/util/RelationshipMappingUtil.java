package com.msb.bpm.approval.appr.util;

import java.util.HashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class RelationshipMappingUtil {

  public static final Map<String, String> CJBO_MAPPING_BPM = new HashMap() {
    {
      put("V001","R87");
      put("V003","R88");
      put("V005","R90");
      put("V008","R92");
      put("V016","R101");
      put("V017","V017");
    }
  };

  public static final Map<String, String> BPM_MAPPING_CJBO = new HashMap() {
    {
      put("R87","V001");
      put("R88","V003");
      put("R89","V003");
      put("R90","V005");
      put("R91","V005");
      put("R92","V008");
      put("R93","V008");
      put("R94","V005");
      put("R95","V008");
      put("R96","V003");
      put("R97","V005");
      put("R98","V003");
      put("V017","V017");
      put("R101","V016");
      put("R102","V016");
      put("R103","V016");
      put("R104","V008");
      put("R85","V016");
    }
  };

  public static final Map<String, String> CJ5_MAPPING_BPM = new HashMap() {
    {
      put("V001","R87");
      put("V002","R87");
      put("V003","R88");
      put("V004","R89");
      put("V005","R90");
      put("V006","R96");
      put("V007","R96");
      put("V008","R92");
      put("V009","R96");
      put("V010","R97");
      put("V011","R93");
      put("V012","R93");
      put("V016","R93");
      put("V017","V017");
    }
  };

  public static final Map<String, String> BPM_MAPPING_CJ5 = new HashMap() {
    {
      put("R87","V002");
      put("R88","V003");
      put("R89","V004");
      put("R90","V005");
      put("R91","V005");
      put("R92","V008");
      put("R93","V016");
      put("R94","V016");
      put("R95","V009");
      put("R96","V006");
      put("R97","V010");
      put("R98","V016");
      put("V017","V017");
      put("R101","V016");
      put("R102","V016");
      put("R103","V016");
      put("R104","V016");
      put("R85","V016");
    }
  };


}
