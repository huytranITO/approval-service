package com.msb.bpm.approval.appr.enums.checklist;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum AssetGroup {
    REAL_ESTATE("V001","16"),
    VEHICLE("V002","17"),
    MACHINE("V003","18"),
    VALUABLE_PAPERS("V004","20"),
    ASSET_FUTURE("V005","22"),
    ASSET_OTHER("V006","25"),
    LABOR_CONTRACT("V008","23"),
    BONDS("V009","21"),
    ASSET_RIGHTS("V010","24"),
    MONEY("V011","19")
            ;
    private final String categoryCode;
    private final String assetGroup;

    public static Map<String,String> mapGroupCategoryAsset(){
        return Arrays
                .stream(AssetGroup.values())
                .collect(Collectors
                        .toMap(AssetGroup::getCategoryCode, AssetGroup::getAssetGroup));
    }
}
