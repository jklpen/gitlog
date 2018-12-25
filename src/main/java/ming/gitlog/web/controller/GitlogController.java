package ming.gitlog.web.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ming.gitlog.Result;
import ming.gitlog.ResultBuilder;
import ming.gitlog.service.GitlogService;
import ming.gitlog.service.request.GitlogReqBO;
import ming.gitlog.service.response.GitlogRespBO;

@Controller
public class GitlogController {

    @Autowired
    private GitlogService gitlogService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(ModelMap map) {
        List<GitlogRespBO> list = new ArrayList<>();
        GitlogRespBO vo = new GitlogRespBO();
        vo.setName("peter");
        vo.setPercent("43.33");
        vo.setAdd(14);
        vo.setDelete(2);
        vo.setFileCount(2);
        vo.setFirstCommit("2018-09-09 15:59:15");
        vo.setLastCommit("2018-12-12 15:59:15");
        list.add(vo);
        vo = new GitlogRespBO();
        vo.setName("mary");
        vo.setPercent("56.67");
        vo.setAdd(15);
        vo.setDelete(5);
        vo.setFileCount(3);
        vo.setFirstCommit("2018-09-08 15:59:15");
        vo.setLastCommit("2018-12-25 15:59:15");
        list.add(vo);
        map.addAttribute("gitlogList", list);
        return "gitlog";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody
    public Result<List<GitlogRespBO>> gitlog(Model model, @RequestParam(value = "type", required = true) String type,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {

        GitlogReqBO gitlogReqBO = new GitlogReqBO();
        gitlogReqBO.setType(type);
        gitlogReqBO.setStartDate(startDate);
        gitlogReqBO.setEndDate(endDate);
        gitlogReqBO.setAuthor("All");
        Result<List<GitlogRespBO>> result = gitlogService.gitlog(gitlogReqBO);
        return result;
    }

    @RequestMapping(value = "/pullProjects", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> pullProjects(Model model) {
        String os = System.getProperty("os.name");
//        if (os.toLowerCase().startsWith("win")) {
//            return ResultBuilder.error("你使用的是windows系统，暂不支持pull操作。");
//        } 
        Result<Integer> result = gitlogService.pullProjects();
        return result;
    }

    @RequestMapping(value = "/getRemain", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> getRemain(Model model) {
        Result<Integer> result = gitlogService.getRemain();
        return result;
    }

}
