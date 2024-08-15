package com.msb.bpm.approval.appr.enums.checklist;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;

@Getter
@RequiredArgsConstructor
public enum CustomerRelationShip {
    V001("Người hôn phối không bảo lãnh trả nợ"),
    V002("Người hôn phối có bảo lãnh trả nợ"),
    V003("Bố ruột / bố nuôi hợp pháp"),
    V004("Mẹ ruột/ mẹ nuôi hợp pháp"),
    V005("Con ruột/ con nuôi hợp pháp"),
    V006("Bố vợ/ chồng"),
    V007("Mẹ vợ/ chồng"),
    V008("Anh/chị/em ruột"),
    V009("Anh/chị/em của vợ/chồng"),
    V010("Con riêng"),
    V011("Thành viên Hộ gia đình"),
    V012("Thành viên góp vốn HKD/DNTN"),
    V016("Khác"),
    V017("Khách hàng"),
    R86("Người hôn phối của chủ DNTN"),
    R87("Người hôn phối"),
    R99("Khách hàng"),
    R88("Bố/mẹ ruột"),
    R89("Bố/mẹ nuôi hợp pháp"),
    R90("Con ruột"),
    R91("Con nuôi hợp pháp"),
    R92("Anh/chị/em ruột"),
    R93("Anh rể/em rể/chi dâu/em dâu"),
    R94("Con dâu/con rể"),
    R95("Anh/chị/em của vợ/chồng"),
    R96("Bố mẹ vợ/chồng"),
    R97("Con riêng"),
    R98("Bố dượng/mẹ kế"),
    R101("Đối tượng ủy quyền đại diện phần vốn góp, cổ phần của cá nhân đó"),
    R102("Cá nhân cùng cá nhân đó được cùng 1 tổ chức ủy quyền đại diện phần vốn góp, cổ phần tại một tổ chức khác"),
    R103("Đối tượng được ủy quyền đại diện phần vốn góp, cổ phần"),
    R5("Công ty/tổ chức liên quan"),
    R104("Anh/chị/em nuôi hợp pháp"),
    R105("Đối tác kinh doanh của MSB")
    ;
    private final String description;
    public static final String SPOUSE = "NHP";
    public static final String CUSTOMER = "KH";
    public static final String OTHERS = "OTH";
    public static boolean isSpouse(String value) {
        return V001.toString().equals(value) || V002.toString().equals(value) || R87.toString().equals(value);
    }

    public static boolean isCustomer(String value) {
        return V017.toString().equals(value);
    }
}











