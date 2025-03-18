package com.sibijo.common.utils.page;

import com.sibijo.common.exception.CustomException;
import com.sibijo.common.exception.codes.CommonExceptionCode;
import org.springframework.data.domain.Pageable;

public class PageableUtils {

    private static final int FIRST_PAGE_NUMBER = 1;

    public static Pageable validatePageable(Pageable pageable) {
        validatePageNumber(pageable.getPageNumber());
        validatePageSize(pageable.getPageSize());

        return pageable.previousOrFirst();
    }

    private static void validatePageNumber(int pageNumber) {
        if (pageNumber < FIRST_PAGE_NUMBER) {
            throw new CustomException(CommonExceptionCode.INVALID_PAGE_NUMBER);
        }
    }

    private static void validatePageSize(int pageSize) {
        if (!PageSize.isValidSize(pageSize)) {
            throw new CustomException(CommonExceptionCode.INVALID_PAGE_SIZE);
        }
    }

}
