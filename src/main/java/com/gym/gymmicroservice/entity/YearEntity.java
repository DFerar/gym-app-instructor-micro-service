package com.gym.gymmicroservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YearEntity {
    private Integer year;
    private List<MonthEntity> months;
}
