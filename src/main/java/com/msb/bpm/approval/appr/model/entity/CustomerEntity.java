package com.msb.bpm.approval.appr.model.entity;

import com.msb.bpm.approval.appr.enums.application.CustomerType;
import com.msb.bpm.approval.appr.mapper.CustomerMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;
import org.apache.commons.collections4.CollectionUtils;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
@SecondaryTable(name = "individual_customer", pkJoinColumns = @PrimaryKeyJoinColumn(name = "customer_id"))
@SecondaryTable(name = "enterprise_customer", pkJoinColumns = @PrimaryKeyJoinColumn(name = "customer_id"))
public class CustomerEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "ref_customer_id")
  private Long refCustomerId;

  @Column(name = "ref_cus_id")
  private String refCusId;

  @Column(name = "bpm_cif")
  private String bpmCif;

  @Column(name = "cif")
  private String cif;

  @Column(name = "customer_type")
  private String customerType;

  @Column(name = "order_display")
  private Integer orderDisplay;

  private Integer version;

  @Embedded
  private IndividualCustomerEntity individualCustomer;

  @Embedded
  private EnterpriseCustomerEntity enterpriseCustomer;

  @Transient
  private Long oldId;

  @OneToMany(mappedBy = "customer", cascade = {CascadeType.MERGE, CascadeType.PERSIST,
      CascadeType.REMOVE}, orphanRemoval = true, fetch = FetchType.LAZY)
  private Set<CustomerAddressEntity> customerAddresses = new HashSet<>();

  @OneToMany(mappedBy = "customer", cascade = {CascadeType.MERGE, CascadeType.PERSIST,
      CascadeType.REMOVE}, orphanRemoval = true, fetch = FetchType.LAZY)
  private Set<CustomerIdentityEntity> customerIdentitys = new HashSet<>();

  @OneToMany(mappedBy = "customer", cascade = {CascadeType.MERGE, CascadeType.PERSIST,
      CascadeType.REMOVE}, orphanRemoval = true, fetch = FetchType.LAZY)
  private Set<CustomerRelationShipEntity> customerRelationShips = new HashSet<>();

  @OneToMany(mappedBy = "customer", cascade = {CascadeType.MERGE, CascadeType.PERSIST,
      CascadeType.REMOVE}, orphanRemoval = true, fetch = FetchType.LAZY)
  private Set<CustomerContactEntity> customerContacts = new HashSet<>();

  @Transient
  private transient String relationship;

  @Transient
  private transient String relationshipValue;

  @Transient
  private transient Long relationshipRefId;

  private transient Boolean paymentGuarantee;

  public void addCustomerAddresses(Set<CustomerAddressEntity> addresses) {
    addresses.forEach(addr -> {
      addr.setCustomer(this);
      this.customerAddresses.add(addr);
    });
  }

  public void updateCustomerAddresses(Set<CustomerAddressEntity> addresses) {
    Map<Long, CustomerAddressEntity> map = addresses
        .stream()
        .collect(Collectors.toMap(CustomerAddressEntity::getId, Function.identity()));

    Set<CustomerAddressEntity> addressRemoveSet = new HashSet<>();
    customerAddresses.forEach(addr -> {
      if (map.containsKey(addr.getId())) {
        CustomerMapper.INSTANCE.referenceCustomerAddress(addr, map.get(addr.getId()));
        addr.setCustomer(this);
      } else {
        addressRemoveSet.add(addr);
      }
    });

    if (CollectionUtils.isNotEmpty(addressRemoveSet)) {
      removeCustomerAddresses(addressRemoveSet);
    }
  }

  public void removeCustomerAddresses(Set<CustomerAddressEntity> addresses) {
    addresses.forEach(addr -> addr.setCustomer(null));
    this.customerAddresses.removeAll(addresses);
  }

  public void cmsRemoveCustomerAddresses(Set<CustomerAddressEntity> addresses) {
    this.customerAddresses.removeAll(addresses);
  }


  public void cmsRemoveCustomerIdentities(Set<CustomerIdentityEntity> identities) {
    this.customerIdentitys.removeAll(identities);
  }

  public void addCustomerIdentities(Set<CustomerIdentityEntity> identities) {
    identities.forEach(identity -> {
      identity.setCustomer(this);
      this.customerIdentitys.add(identity);
    });
  }

  public void updateCustomerIdentities(Set<CustomerIdentityEntity> identities) {
    Map<Long, CustomerIdentityEntity> map = identities
        .stream()
        .collect(Collectors.toMap(CustomerIdentityEntity::getId, Function.identity()));

    Set<CustomerIdentityEntity> identityRemoveSet = new HashSet<>();
    customerIdentitys.forEach(identity -> {
      if (map.containsKey(identity.getId())) {
        CustomerMapper.INSTANCE.referenceCustomerIdentity(identity, map.get(identity.getId()));
        identity.setCustomer(this);
      } else {
        identityRemoveSet.add(identity);
      }
    });

    if (CollectionUtils.isNotEmpty(identityRemoveSet)) {
      removeCustomerIdentities(identityRemoveSet);
    }
  }

  public void removeCustomerIdentities(Set<CustomerIdentityEntity> identities) {
    identities.forEach(identity -> identity.setCustomer(null));
    this.customerIdentitys.removeAll(identities);
  }

  public void removeCustomerRelationshipEntities(Set<CustomerRelationShipEntity> removeCustomerRelationShips) {
    removeCustomerRelationShips.forEach(removeCustomerRelationShip -> removeCustomerRelationShip.setCustomer(null));
    this.customerRelationShips.removeAll(removeCustomerRelationShips);
  }

  public void removeAllCustomerRelationshipEntities(){
    this.customerRelationShips.forEach(item -> item.setCustomer(null));
    this.customerRelationShips.clear();
  }

  public String customerName() {
    return CustomerType.isEB(this.customerType) ?
        this.enterpriseCustomer.getCompanyName() : this.individualCustomer.fullName();
  }

  public void cleanAllCustomerIdentities() {
    this.customerIdentitys.clear();
  }
}
