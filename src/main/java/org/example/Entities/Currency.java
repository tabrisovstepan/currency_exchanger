package org.example.Entities;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class Currency {
    private Long id;

    @NonNull
    private String code;

    @NonNull
    private String fullName;

    @NonNull
    private String sign;

    //for DTO mapper, check how to check error
    private Currency() {}
}
