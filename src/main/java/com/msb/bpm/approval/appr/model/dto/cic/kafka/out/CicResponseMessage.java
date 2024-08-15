package com.msb.bpm.approval.appr.model.dto.cic.kafka.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

/**
 * sample message
 *
 * {
 *   "UserHeader": {
 *     "SocketChannel": "Data",
 *     "TableName": "WAY4_CLIENT",
 *     "MyAction": "SELECT",
 *     "User": "KAFKA_SERVICE_TEST",
 *     "SpecialMission": "ON_DEMAND_STREAM",
 *     "RequestJsonId": "ef98812c-f5f3-11ed-b67e-0242ac120002",
 *     "SpecialMessage": {}
 *   },
 *   "Data": [
 *     {
 *       "FieldList": [
 *         {
 *           "FieldName": "ID",
 *           "FieldType": "NUMBER"
 *         },
 *         {
 *           "FieldName": "BRANCH",
 *           "FieldType": "VARCHAR2"
 *         },
 *         {
 *           "FieldName": "ADDRESS_LINE_4",
 *           "FieldType": "VARCHAR2"
 *         }
 *       ],
 *       "Data": [
 *         {
 *           "0": "87843",
 *           "1": "0011",
 *           "2": "0983027039"
 *         },
 *         {
 *           "0": "88258",
 *           "1": "0011",
 *           "2": "0981239090"
 *         },
 *         {
 *           "0": "87532",
 *           "1": "0011",
 *           "2": "0912252745"
 *         }
 *       ]
 *     }
 *   ]
 * }
 */
@Data
public class CicResponseMessage {

  @JsonProperty("UserHeader")
  private UserHeader userHeader;

  @JsonProperty("Data")
  private List<ResponseMessageData> data;
}
