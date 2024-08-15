package com.msb.bpm.approval.appr.factory.cbt;

public interface ObjectGenericBuilder<O,I,J> {
    O build(I i, J j);
}
