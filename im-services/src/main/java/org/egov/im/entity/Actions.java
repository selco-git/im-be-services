package org.egov.im.entity;

import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Setter
@ToString
@Builder
public class Actions {
	
	private String tenantId;
	
	private String id;
	
	private String eventId;
	
	@NotNull
	private List<ActionItem> actionUrls;
	
}
