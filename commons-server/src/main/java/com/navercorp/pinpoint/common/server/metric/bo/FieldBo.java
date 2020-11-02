/*
 * Copyright 2020 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.common.server.metric.bo;

import java.util.Objects;

/**
 * @author Hyunjoon Cho
 */
public class FieldBo {
    private final String fieldName;
    private long fieldLongValue;
    private double fieldDoubleValue;
    private final boolean isLong;
    // if false, float -> save as double for accuracy

    public FieldBo(String fieldName, long fieldLongValue) {
        this.fieldName = Objects.requireNonNull(fieldName, "fieldName");
        this.fieldLongValue = fieldLongValue;
        this.isLong = true;
    }

    public FieldBo(String fieldName, double fieldDoubleValue) {
        this.fieldName = Objects.requireNonNull(fieldName, "fieldName");
        this.fieldDoubleValue = fieldDoubleValue;
        this.isLong = false;
    }

    public String getFieldName() {
        return fieldName;
    }

    public long getFieldLongValue() {
        return fieldLongValue;
    }

    public double getFieldDoubleValue() {
        return fieldDoubleValue;
    }

    public boolean isLong() {
        return isLong;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("name=").append(fieldName);
        sb.append(", value=");
        if (isLong) {
            sb.append(fieldLongValue);
        } else {
            sb.append(fieldDoubleValue);
        }
        sb.append(", isLong=").append(isLong);
        sb.append('}');
        return sb.toString();
    }
}
