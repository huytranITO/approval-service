package com.msb.bpm.approval.appr.client.creditconditions;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.AbstractClient;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.model.request.creditconditions.CreateCreditConditionRequest;
import com.msb.bpm.approval.appr.model.request.creditconditions.DeleteCreditConditionRequest;
import com.msb.bpm.approval.appr.model.request.creditconditions.UpdateCreditConditionRequest;
import com.msb.bpm.approval.appr.model.request.creditconditions.UpdateFlagCreditConditionRequest;
import com.msb.bpm.approval.appr.model.response.creditconditions.CreditConditionClientResponse;
import com.msb.bpm.approval.appr.model.response.creditconditions.CreditConditionResponse;
import com.msb.bpm.approval.appr.util.HeaderUtil;
import com.msb.bpm.approval.appr.util.JsonUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class CreditConditionClient extends AbstractClient {

	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;
	private final CreditConditionClientConfigProperties configProperties;

	@Override
	protected RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public CreditConditionClientResponse<List<CreditConditionResponse>> createCreditConditions(
			CreateCreditConditionRequest request) {
		try {
			log.info("[CALL_API_CREATE_CREDIT_CONDITION] call api request : {}", request);
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setBearerAuth(HeaderUtil.getToken());
			httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			httpHeaders.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<CreateCreditConditionRequest> httpEntity = new HttpEntity<>(request, httpHeaders);

			String uri = UriComponentsBuilder
					.fromUriString(this.configProperties.getUrlBase() + this.configProperties.getUrlCreditCondition())
					.toUriString();
			CreditConditionClientResponse<List<CreditConditionResponse>> response = exchange(uri, HttpMethod.POST,
					httpEntity, CreditConditionClientResponse.class);
			log.info("[CALL_API_CREATE_CREDIT_CONDITION] response : {}", response);
			String responseString = JsonUtil.convertObject2String(response.getValue(), objectMapper);
			response.setValue(Arrays.asList(JsonUtil.convertString2Object(responseString,CreditConditionResponse[].class, objectMapper)));
			return response;
		} catch (Exception exception) {
			log.error("method: createCreditConditions, error ", exception);
			throw new ApprovalException(DomainCode.INVALID_PARAMETER, new Object[] { request });
		}
	}

	public CreditConditionClientResponse<List<CreditConditionResponse>> getCreditConditionByListId(List<Long> ids) {
		try {
			log.info("[CALL_API_GET_CREDIT_CONDITION] {} call api request : {}", this.configProperties.getUrlBase() + this.configProperties.getUrlCreditCondition(), ids);
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setBearerAuth(HeaderUtil.getToken());
			httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			httpHeaders.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);
			String uri = UriComponentsBuilder
					.fromUriString(this.configProperties.getUrlBase() + this.configProperties.getUrlCreditCondition() +  "?ids="
				              + String.join(",",ids.stream().map(s -> s.toString()).collect(Collectors.toList())))
					.toUriString();
			CreditConditionClientResponse<List<CreditConditionResponse>> response = exchange(uri, HttpMethod.GET,
					httpEntity, CreditConditionClientResponse.class);
			log.info("[CALL_API_GET_CREDIT_CONDITION] response : {}", response);
			String responseString = JsonUtil.convertObject2String(response.getValue(), objectMapper);
			response.setValue(Arrays.asList(JsonUtil.convertString2Object(responseString,CreditConditionResponse[].class, objectMapper)));
			//order by create Date
			if(Objects.nonNull(response.getValue())) {
				response.getValue().sort(Comparator.comparing(CreditConditionResponse::getCreateDate));
			}
			return response;
		} catch (Exception exception) {
			log.error("method: getCreditConditionByListId, error ", exception);
			throw new ApprovalException(DomainCode.INVALID_PARAMETER, new Object[] { ids });
		}
	}

	public CreditConditionClientResponse<Object> updateCreditCondition(UpdateCreditConditionRequest request) {
		try {
			log.info("[CALL_API_UPDATE_CREDIT_CONDITION] call api request : {}", request);
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setBearerAuth(HeaderUtil.getToken());
			httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			httpHeaders.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<UpdateCreditConditionRequest> httpEntity = new HttpEntity<>(request, httpHeaders);

			String uri = UriComponentsBuilder
					.fromUriString(this.configProperties.getUrlBase() + this.configProperties.getUrlCreditCondition())
					.toUriString();
			CreditConditionClientResponse<Object> response = exchange(uri, HttpMethod.PUT, httpEntity,
					CreditConditionClientResponse.class);
			log.info("[CALL_API_UPDATE_CREDIT_CONDITION] response : {}", response);
			return response;
		} catch (Exception exception) {
			log.error("method: updateCreditCondition, error ", exception);
			throw new ApprovalException(DomainCode.INVALID_PARAMETER, new Object[] { request });
		}
	}

	public CreditConditionClientResponse<Object> deleteCreditConditionByListId(DeleteCreditConditionRequest request) {
		try {
			log.info("[CALL_API_DELETE_CREDIT_CONDITION] call api request : {}", request);
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setBearerAuth(HeaderUtil.getToken());
			httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			httpHeaders.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<DeleteCreditConditionRequest> httpEntity = new HttpEntity<>(request, httpHeaders);

			String uri = UriComponentsBuilder
					.fromUriString(this.configProperties.getUrlBase() + this.configProperties.getUrlDeleteFlag())
					.toUriString();
			CreditConditionClientResponse<Object> response = exchange(uri, HttpMethod.PUT, httpEntity,
					CreditConditionClientResponse.class);
			log.info("[CALL_API_DELETE_CREDIT_CONDITION] response : {}", response);
			return response;
		} catch (Exception exception) {
			log.error("method: deleteCreditConditionByListId, error ", exception);
			throw new ApprovalException(DomainCode.INVALID_PARAMETER, new Object[] { request });
		}
	}

	public CreditConditionClientResponse<Object> updateFlagCreditConditionByListId(UpdateFlagCreditConditionRequest request) {
		try {
			log.info("[CALL_API_UPDATE_FLAG_CREDIT_CONDITION] call api request : {}", request);
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setBearerAuth(HeaderUtil.getToken());
			httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			httpHeaders.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<UpdateFlagCreditConditionRequest> httpEntity = new HttpEntity<>(request, httpHeaders);

			String uri = UriComponentsBuilder
					.fromUriString(this.configProperties.getUrlBase() + this.configProperties.getUrlUpdateFlag())
					.toUriString();
			CreditConditionClientResponse<Object> response = exchange(uri, HttpMethod.PUT, httpEntity,
					CreditConditionClientResponse.class);
			log.info("[CALL_API_UPDATE_FLAG_CREDIT_CONDITION] response : {}", response);
			return response;
		} catch (Exception exception) {
			log.error("method: updateFlagCreditConditionByListId, error ", exception);
			throw new ApprovalException(DomainCode.INVALID_PARAMETER, new Object[] { request });
		}
	}
}