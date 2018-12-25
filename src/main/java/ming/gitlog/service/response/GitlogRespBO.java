package ming.gitlog.service.response;

import java.io.Serializable;

import lombok.Data;

@Data
public class GitlogRespBO implements Serializable {

    private String name;
    private String percent;
    private Integer add;
    private Integer delete;
    private Integer fileCount;
    private String firstCommit;
    private String lastCommit;

    private static final long serialVersionUID = 1L;

}