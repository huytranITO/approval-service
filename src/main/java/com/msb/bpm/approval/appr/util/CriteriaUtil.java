package com.msb.bpm.approval.appr.util;

import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationHistoricIntegration;
import com.msb.bpm.approval.appr.model.entity.CustomerEntity;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class CriteriaUtil {

  @Autowired
  EntityManager entityManager;

  String keyBmpId = "bpmId";
  String keyCreatedBy = "createdBy";

  @Transactional(readOnly = true)
  public List<String> findGroupedByBpmId(String sortField, int sortOrder) {
    LocalDate currentDate = LocalDate.now();
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
    Root<ApplicationHistoricIntegration> root = criteriaQuery.from(
        ApplicationHistoricIntegration.class);
    Predicate predicate = criteriaBuilder.and(
        criteriaBuilder.greaterThanOrEqualTo(root.get("effectiveDate"), currentDate),
        criteriaBuilder.equal(root.get(keyCreatedBy), SecurityContextUtil.getCurrentUser()));

    if ((sortField.equalsIgnoreCase(keyBmpId) && sortOrder == 1) || (
        sortField.equalsIgnoreCase("undefined") && sortOrder == 1)) {
      criteriaQuery.select(root.get(keyBmpId)).where(predicate)
          .orderBy(criteriaBuilder.desc(root.get(keyBmpId))).groupBy(root.get(keyBmpId));
    } else if (sortField.equalsIgnoreCase(keyBmpId) && sortOrder == -1) {
      criteriaQuery.select(root.get(keyBmpId)).where(predicate)
          .orderBy(criteriaBuilder.asc(root.get(keyBmpId))).groupBy(root.get(keyBmpId));
    } else {
      throw new ApprovalException(DomainCode.NOT_FOUND_HISTORIC_INTEGRATION);
    }

    TypedQuery<String> query = entityManager.createQuery(criteriaQuery);
    return query.getResultList();
  }

  @Transactional(readOnly = true)
  public List<String> findBySearch(String contents, String sortField, int sortOrder) {
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
    Root<ApplicationHistoricIntegration> root = criteriaQuery.from(
        ApplicationHistoricIntegration.class);

    Join<ApplicationHistoricIntegration, ApplicationEntity> joinApplication = root.join(
        "application", JoinType.LEFT);

    Join<ApplicationEntity, CustomerEntity> joinCustomer = joinApplication.join("customer",
        JoinType.LEFT);

    Expression<String> fullName = criteriaBuilder.function("CONCAT", String.class,
        joinCustomer.get("individualCustomer").get("lastName"), criteriaBuilder.literal(" "),
        joinCustomer.get("individualCustomer").get("firstName"));

    Predicate predicate = criteriaBuilder.and(criteriaBuilder.or(
            criteriaBuilder.like(criteriaBuilder.upper(root.get("bpmId")),
                "%" + contents.toUpperCase() + "%"),
            criteriaBuilder.like(criteriaBuilder.upper(root.get("identifierCode")),
                "%" + contents.toUpperCase() + "%"),
            criteriaBuilder.like(criteriaBuilder.upper(root.get("cif")),
                "%" + contents.toUpperCase() + "%"),
            criteriaBuilder.like(criteriaBuilder.upper(fullName), "%" + contents.toUpperCase() + "%")),
        criteriaBuilder.equal(root.get(keyCreatedBy), SecurityContextUtil.getCurrentUser()));

    if ((sortField.equalsIgnoreCase(keyBmpId) && sortOrder == 1) || (
        sortField.equalsIgnoreCase("undefined") && sortOrder == 1)) {
      criteriaQuery.select(root.get(keyBmpId)).where(predicate)
          .orderBy(criteriaBuilder.desc(root.get(keyBmpId))).groupBy(root.get(keyBmpId));
    } else if (sortField.equalsIgnoreCase(keyBmpId) && sortOrder == -1) {
      criteriaQuery.select(root.get(keyBmpId)).where(predicate)
          .orderBy(criteriaBuilder.asc(root.get(keyBmpId))).groupBy(root.get(keyBmpId));
    } else {
      throw new ApprovalException(DomainCode.NOT_FOUND_HISTORIC_INTEGRATION);
    }

    TypedQuery<String> query = entityManager.createQuery(criteriaQuery);
    return query.getResultList();
  }
}
