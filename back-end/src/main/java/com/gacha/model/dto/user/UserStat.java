package com.gacha.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserStat {
	private Integer joinDays;
	private Integer scheduleCount;
	private Integer spotCount;
	private Integer postCount;
}
