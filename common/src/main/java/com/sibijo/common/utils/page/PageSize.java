package com.sibijo.common.utils.page;

import java.util.EnumSet;
import lombok.Getter;

@Getter
public enum PageSize {
    DEFAULT(10),
    THIRTY(30),
    FIFTY(50);

    private int size;

    PageSize(int size) {
        this.size = size;
    }

    public static boolean isValidSize(int size) {
        return EnumSet.allOf(PageSize.class)
                .stream()
                .anyMatch(ps -> ps.getSize() == size);
    }
}
