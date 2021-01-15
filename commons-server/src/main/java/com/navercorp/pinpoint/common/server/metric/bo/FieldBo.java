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
public class FieldBo<T extends Number>{
    private String fieldName;
    private T fieldValue;
    // if false, float -> save as double for accuracy

    public FieldBo(String fieldName, T fieldValue) {
        this.fieldName = Objects.requireNonNull(fieldName, "fieldName");
        this.fieldValue = fieldValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public T getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(T fieldValue) {
        this.fieldValue = fieldValue;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("name=").append(fieldName);
        sb.append(", value=").append(fieldValue);
        sb.append('}');
        return sb.toString();
    }
}
