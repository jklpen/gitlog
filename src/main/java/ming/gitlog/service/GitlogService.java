package ming.gitlog.service;

import java.util.List;

import ming.gitlog.Result;
import ming.gitlog.service.request.GitlogReqBO;
import ming.gitlog.service.response.GitlogRespBO;

public interface GitlogService {

    public Result<List<GitlogRespBO>> gitlog(GitlogReqBO gitlogReqBO);

    public Result<Integer> pullProjects();
    
    public Result<Integer> getRemain();

}
