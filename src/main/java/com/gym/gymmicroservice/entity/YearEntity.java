package com.gym.gymmicroservice.entity;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YearEntity {
    private Integer year;
    private List<MonthEntity> months;
}
