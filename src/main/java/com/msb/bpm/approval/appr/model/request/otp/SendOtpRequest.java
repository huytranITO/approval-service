package com.msb.bpm.approval.appr.model.request.otp;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
public class SendOtpRequest {
    List<String> phones;
}
