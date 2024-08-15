package com.msb.bpm.approval.appr.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class PermissionConstant {

  public static final String MANAGE_INTERGATED_SEARCH = "hasPermission('Manage Legals', 'umr:legals:search')";

  public static final String MANAGE_APPLICATION_INITIALIZE = "hasPermission('Manage Application', 'pd:app:create')";
  public static final String MANAGE_APPLICATION_VIEW = "hasPermission('Manage Application', 'pd:app:view')";
  public static final String MANAGE_APPLICATION_CLOSE = "hasPermission('Manage Application', 'pd:app:close')";
  public static final String MANAGE_APPLICATION_ALLOCATE = "hasPermission('Manage Application', 'pd:app:allocate')";
  public static final String MANAGE_APPLICATION_REASSIGN = "hasPermission('Manage Application', 'pd:app:reassign')";
  public static final String MANAGE_APPLICATION_ENDORSE = "hasPermission('Manage Application', 'pd:app:endorse')";
  public static final String MANAGE_APPLICATION_REWORK = "hasPermission('Manage Application', 'pd:app:rework')";

  public static final String MANAGE_SUBTASK_SAVE = "hasPermission('Manage Subtask', 'pd:subtask:save')";
  public static final String MANAGE_CIC_QUERY = "hasPermission('Manage Subtask', 'pd:cic:query')";
  public static final String MANAGE_AML_OPR_QUERY = "hasPermission('Manage Subtask', 'pd:aml:opr:query')";
  public static final String MANAGE_RATING_QUERY = "hasPermission('Manage Subtask', 'pd:rating:query')";

  public static final String MANAGE_APPLICATION_FEEDBACK = "hasPermission('Manage Application', 'pd:app:feedback')";

  public static final String COLLATERAL_OPRISK = "hasPermission('COLLATERAL_OPRISK', 'cm:bpm_collateral:oprisk')";

  public static final String APPLICATION_PUSH_KAFKA = "hasPermission('Application Push kafka', 'pd:app:pushKafka')";
}
