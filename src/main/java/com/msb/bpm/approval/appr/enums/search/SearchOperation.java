package com.msb.bpm.approval.appr.enums.search;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 18/10/2023, Wednesday
 **/
public enum SearchOperation {
  EQUALITY, NEGATION, GREATER_THAN, LESS_THAN, LIKE, STARTS_WITH, ENDS_WITH, CONTAINS, IN,
  GREATER_THAN_OR_EQUAL, LESS_THAN_OR_EQUAL, IS_NULL,LIKE_IGNORE_CASE;
  public static final String[] SIMPLE_OPERATION_SET = {":", "!", ">", "<", "~", "#", ">=", "<=", "IS_NULL"};

  public static final String OR_PREDICATE_FLAG = "OR";

  public static final String ZERO_OR_MORE_REGEX = "*";

  public static SearchOperation getSimpleOperation(final String input) {
    switch (input) {
      case ":":
        return EQUALITY;
      case "!":
        return NEGATION;
      case ">":
        return GREATER_THAN;
      case "<":
        return LESS_THAN;
      case "~":
        return LIKE;
      case "#":
        return IN;
      case ">=":
        return GREATER_THAN_OR_EQUAL;
      case "<=":
        return LESS_THAN_OR_EQUAL;
      case "IS_NULL":
        return IS_NULL;
      case "LIKE_IGNORE_CASE":
        return LIKE_IGNORE_CASE;
      default:
        return null;
    }
  }
}
