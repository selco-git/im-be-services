package org.egov.im.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A Object holds the
 */
@ApiModel(description = "A Object holds the")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-12-04T11:26:25.532+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
@Table(name="eg_wf_businessservice_v2")
@EqualsAndHashCode(of = {"tenantId","businessService"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BusinessService {

	  @NotNull
      @Size(max=256)
      @JsonProperty("tenantId")
	  @Column(name="tenantid")
      private String tenantId = null;	
       
        @Size(max=256)
        @JsonProperty("uuid")
  	  	@Column(name="uuid")
        @Id
        private String uuid = null;

        @NotNull
        @Size(max=256)
  	    @Column(name="businessservice")
        @JsonProperty("businessService")
        private String businessService = null;

        @NotNull
        @Size(max=256)
        @JsonProperty("business")
  	    @Column(name="business")
        private String business = null;

        @Size(max=1024)
        @JsonProperty("getUri")
  	    @Column(name="geturi")
        private String getUri = null;

        @Size(max=1024)
        @JsonProperty("postUri")
  	    @Column(name="posturi")
        private String postUri = null;

        @JsonProperty("businessServiceSla")
  	  	@Column(name="businessservicesla")
        private Long businessServiceSla = null;

        @NotNull
        @Valid
        @JsonProperty("states")
        @OneToMany(fetch = FetchType.LAZY,cascade =  CascadeType.ALL,mappedBy = "businessService")
        @OnDelete(action=OnDeleteAction.CASCADE)
        private List<State> states = null;

        @JsonProperty("createdBy")
	    @Column(name="createdby")
        private String createdBy = null;

        @JsonProperty("lastModifiedBy")
	    @Column(name="lastmodifiedby")
        private String lastModifiedBy = null;

        @JsonProperty("createdTime")
	    @Column(name="createdtime")
        private Long createdTime = null;

        @JsonProperty("lastModifiedTime")
	    @Column(name="lastmodifiedtime")
        private Long lastModifiedTime = null;


        public BusinessService addStatesItem(State statesItem) {
            if (this.states == null) {
            this.states = new ArrayList<>();
            }
        this.states.add(statesItem);
        return this;
        }


        /**
         * Returns the currentState with the given uuid if not present returns null
         * @param uuid the uuid of the currentState to be returned
         * @return
         */
        public State getStateFromUuid(String uuid) {
               State state = null;
               if(this.states!=null){
                       for(State s : this.states){
                               if(s.getUuid().equalsIgnoreCase(uuid)){
                                       state = s;
                                       break;
                               }
                       }
               }
               return state;
        }



}

