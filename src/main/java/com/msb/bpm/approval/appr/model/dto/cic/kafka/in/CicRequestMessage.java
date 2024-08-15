package com.msb.bpm.approval.appr.model.dto.cic.kafka.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * sample message
 * {
 *     "UserHeader": {
 *         "User": "KAFKA_SERVICE_TEST",
 *         "TableName": "WAY4_CLIENT",
 *         "SpecialMission": "ON_DEMAND_STREAM",
 *         "KafkaTopicOut": "kafka-service-ptnlcn-out"
 *     },
 *     "Data": {
 *         "UserData": {
 *             "User": "KAFKA_SERVICE_TEST",
 *             "SocketChannel": "Data",
 *             "MyAction": "SELECT",
 *             "TableName": "WAY4_CLIENT",
 *             "SpecialMission": "ON_DEMAND_STREAM",
 *             "RequestJsonId": "ef98812c-f5f3-11ed-b67e-0242ac120002",
 *             "SpecialMessage": {
 *             }
 *
 *         },
 *         "ActionArray": [
 *             {
 *                 "Select": "SELECT ID,BRANCH,ADDRESS_LINE_4 FROM WAY4_CLIENT1 ",
 *                 "Where": " ",
 *                 "GroupBy": " ",
 *                 "Having": " ",
 *                 "OrderBy": " ",
 *                 "Paging": " OFFSET 0 ROWS FETCH NEXT 1000 ROWS ONLY "
 *             }
 *         ]
 *     }
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CicRequestMessage {

  @JsonProperty("UserHeader")
  private UserHeader userHeader;

  @JsonProperty("Data")
  private RequestMessageData data;

}
