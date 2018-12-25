package ming.gitlog.service.request;

import java.io.Serializable;

import lombok.Data;

@Data
public class GitlogReqBO implements Serializable {

    private String type;
    private String startDate;
    private String endDate;
    private String author;

    private static final long serialVersionUID = 1L;

}