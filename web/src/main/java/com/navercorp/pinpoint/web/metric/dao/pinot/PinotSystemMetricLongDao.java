/*
 * Copyright 2021 NAVER Corp.
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

package com.navercorp.pinpoint.web.metric.dao.pinot;

import com.navercorp.pinpoint.common.server.metric.bo.SystemMetricBo;
import com.navercorp.pinpoint.common.server.metric.bo.TagBo;
import com.navercorp.pinpoint.web.metric.dao.SystemMetricDao;
import com.navercorp.pinpoint.web.vo.Range;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @author Hyunjoon Cho
 */
@Repository
public class PinotSystemMetricLongDao implements SystemMetricDao {
    private static final String NAMESPACE = PinotSystemMetricLongDao.class.getPackage().getName() + "." + PinotSystemMetricLongDao.class.getSimpleName() + ".";

    private final SqlSessionTemplate sqlPinotSessionTemplate;

    public PinotSystemMetricLongDao(SqlSessionTemplate sqlPinotSessionTemplate) {
        this.sqlPinotSessionTemplate = Objects.requireNonNull(sqlPinotSessionTemplate, "sqlPinotSessionTemplate");
    }

    @Transactional(readOnly = true)
    public List<String> getMetricNameList(String applicationName) {
        return sqlPinotSessionTemplate.selectList(NAMESPACE + "selectMetricName", applicationName);
    }

    @Override
    public List<SystemMetricBo> selectSystemMetricBo(@Param("applicationName") String applicationName,
                                                     @Param("metricName") String metricName,
                                                     @Param("fieldName") String fieldName,
                                                     @Param("tagBos") List<TagBo> tags,
                                                     @Param("range") Range range) {
        return sqlPinotSessionTemplate.selectList(NAMESPACE + "selectSystemMetric");
    }
}
