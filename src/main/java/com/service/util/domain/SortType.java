package com.service.util.domain;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class SortType<V, S1, S2> {
    private final V v;
    private final S1 sort1;
    private final S2 sort2;

    public static <V, S1, S2> SortType from(V v, S1 sort1, S2 sort2) {
        return SortType.builder()
                .v(v)
                .sort1(sort1)
                .sort2(sort2)
                .build();
    }
}
