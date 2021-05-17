package ru.itis.kpfu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Beat {

    private Long id;
    private String title;
    private Long price;

}
