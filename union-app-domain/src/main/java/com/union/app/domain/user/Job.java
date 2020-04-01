package com.union.app.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class Job implements Serializable {

    private String jobId;

    private String jobName;


}
