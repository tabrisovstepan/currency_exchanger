package org.example.Entities;

import lombok.NonNull;
import lombok.Setter;
import lombok.Getter;
import lombok.AllArgsConstructor;

@Setter
@Getter
@AllArgsConstructor
public class Currency {
    private Long id;

    @NonNull
    private String code;

    @NonNull
    private String fullName;

    @NonNull
    private String sign;
}
