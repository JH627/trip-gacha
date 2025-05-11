package com.gacha.model.dto.response.trip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 목적지 정보
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DestinationInfo {
	private Integer destinationId;
	private String name;
	private String country;
}
