package com.lftech.sqlapi.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@RequiredArgsConstructor
@Slf4j
@EqualsAndHashCode(callSuper=false)
public class Query extends ConnectionConfig {
    private transient String queryName;
    private transient String sql;
    private transient Long queryId;
}
