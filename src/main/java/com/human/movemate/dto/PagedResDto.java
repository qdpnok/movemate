package com.human.movemate.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @ToString
public class PagedResDto<T> {
    private List<T> dataList;
    private PageInfoDto pageInfo;
}
