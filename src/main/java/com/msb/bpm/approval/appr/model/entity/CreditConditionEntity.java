package com.msb.bpm.approval.appr.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "credit_condition")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditConditionEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column()
    private String code;
    @Column(name = "customer_segment")
    private String customerSegment;
    @Column()
    private String group;
    @Column()
    private String groupCode;
    @Column()
    private String detail; // 'Điều kiện chi tiết',
    @Column(name = "credit_type")
    private String creditType; // 'Khoản cấp tín dụng',
    @Column()
    private String object; // 'Đối tượng áp dụng'
    @Column(name = "control_time")
    private String controlTime; // 'Thời điểm kiểm soát',
    @Column(name = "control_unit")
    private String controlUnit; // 'Đơn vị kiểm soát',
}
