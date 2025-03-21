package com.sibijo.order.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.processing.Pattern;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {

    @NotBlank(message = "공급업체는 필수 입력값입니다.")
    private UUID supplierId;

    @NotBlank(message = "수령업체는 필수 입력값입니다.")
    private UUID recipientsId;

    @NotBlank(message = "상품정보는 필수 입력값입니다.")
    private UUID productId;

    @NotBlank(message = "주문수량은 필수 입력값입니다.")
    private Integer amount;

    @NotBlank(message = "공급업체는 필수 입력값입니다.")
    private String request;

    // 배송에 관한 정보를 위한 수령인과 수령인의 SlackId
    @NotBlank(message = "배송상품 수령자는 필수 입력값입니다.")
    private String receiver;

    @NotBlank(message = "수령자의 SlcakId는 필수 입력값입니다.")
    private String receiverSlackId;
}
