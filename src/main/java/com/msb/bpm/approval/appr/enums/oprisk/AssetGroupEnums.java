package com.msb.bpm.approval.appr.enums.oprisk;

import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum AssetGroupEnums {

  REAL_ESTATE_ASSET("V001", "Bất động sản", new String[]{"V401", "V402", "V403", "V404", "V405", "V406"}),
  TRANSPORT_ASSET("V002", "Phương tiện vận tải và xe chuyên dùng", new String[]{"V601", "V602", "V603", "V604", "V605"}),
  MACHINES_ASSET("V003", "Máy móc thiết bị, dây chuyền sản xuất", new String[]{"V701", "V702", "V703"}),
  VALUABLE_PAPERS_ASSET("V004", "Giấy tờ có giá", new String[]{"V201", "V202","V203", "V204"}),
  DREAM_ASSET("V005", "Tài sản hình thành trong tương lai", new String[]{"V801"}),
  OTHER_ASSET("V006", "Tài sản khác", new String[]{"V901"}),
  GOODS_ASSET("V007", "Hàng hóa", new String[]{"V902"}),
  LABOR_CONTRACT_ASSET("V008", "Hợp đồng lao động", new String[]{"V881"}),
  STOCK_ASSET("V009", "Trái phiếu", new String[]{"V301", "V302", "V303", "V304", "V305"}),
  RIGHT_TO_PROPERTY_ASSET("V010", "Quyền tài sản", new String[]{"V501", "V502"}),
  MONEY_ASSET("V011", "Tiền", new String[]{"V101", "V102"}),
  RECEIVABLE_ASSET("V012", "Khoản phải thu, quyền đòi nợ", new String[]{"V903"}),
  GOLD_ASSET("V013", "Vàng miếng/kim loại quý", new String[]{"V904"}),
  CONTRIBUTION_ASSET("V014", "Phần vốn góp", new String[]{"V905"});
  private final String code;
  private final String name;
  private final String[] child;

  AssetGroupEnums(String code, String name, String[] child) {
    this.code = code;
    this.name = name;
    this.child = child;
  }

  public static AssetGroupEnums findByCode(String code) {
    for (AssetGroupEnums a : AssetGroupEnums.values()) {
      if (a.getCode().equals(code)) {
        return a;
      }
    }
    throw new ApprovalException(DomainCode.INVALID_PARAMETER, new Object[]{"Nhóm tài sản không hợp lệ"});
  }

  public static boolean validCode(String code) {
    for (AssetGroupEnums a : AssetGroupEnums.values()) {
      if (a.getCode().equals(code)) {
        return true;
      }
    }
    return false;
  }

  public static boolean invalidChildByParentCode(String parentCode, String childCode) {
    for (AssetGroupEnums a : AssetGroupEnums.values()) {
      if (a.getCode().equals(parentCode) && Arrays.asList(a.child).contains(childCode)) {
        return false;
      }
    }
    return true;
  }

  public static boolean requiredDocumentField(String groupCode) {
    return
        !AssetGroupEnums.MONEY_ASSET.getCode().equals(groupCode)
            && !AssetGroupEnums.OTHER_ASSET.getCode().equals(groupCode)
            && !AssetGroupEnums.GOLD_ASSET.getCode().equals(groupCode);
  }

  public static String getAssetGroupName(String code){
    return
        Arrays.stream(AssetGroupEnums.values())
            .filter(a -> a.getCode().equals(code))
            .findFirst()
            .map(AssetGroupEnums::getName)
            .orElse(null);
  }

  public static AssetGroupEnums getAssetGroupByCode(String code){
    return
        Arrays.stream(AssetGroupEnums.values())
            .filter(e -> e.getCode().equals(code))
            .findFirst()
            .orElse(null);
  }
}

