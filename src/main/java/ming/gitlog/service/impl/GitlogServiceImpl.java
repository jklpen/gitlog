package ming.gitlog.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import ming.gitlog.Result;
import ming.gitlog.ResultBuilder;
import ming.gitlog.service.GitlogService;
import ming.gitlog.service.request.GitlogReqBO;
import ming.gitlog.service.response.GitlogRespBO;
import ming.gitlog.service.util.ProcessUtil;

@Slf4j
@Service
public class GitlogServiceImpl implements GitlogService {

    @Value("${projectsDir}")
    private String projectsDir = "E:\\projects";

    @Value("${gitUrls}")
    private String gitUrls = "knife,ttms";

    @Value("${initCommands}")
    private String initCommands = "";

    public static Integer remain = 0;

    @Override
    public Result<List<GitlogRespBO>> gitlog(GitlogReqBO gitlogReqBO) {

        Map<String, GitlogRespBO> gitlogRespBOMap = new HashMap<String, GitlogRespBO>();
        int add_sum = 0;

        List<File> projectFileList = new ArrayList<>();
        File parent = new File(projectsDir.replaceAll("\\\\", "\\" + File.separator));
        listProjectDir(projectFileList, parent);

        for (File projectFile : projectFileList) {

            List<String> result = execGitlog(gitlogReqBO.getType(), gitlogReqBO.getStartDate(),
                    gitlogReqBO.getEndDate(), gitlogReqBO.getAuthor(), projectFile.getAbsolutePath());

            int add = 0, delete = 0, count = 0;
            for (int j = 0; j < result.size(); j++) {

                String line = result.get(result.size() - 1 - j);
                System.out.println(line);
                Pattern pat = Pattern.compile("(\\d+)\\s+(\\d+)\\s+\\w+");
                Matcher m = pat.matcher(line);
                if (m.find()) {
                    MatchResult mr = m.toMatchResult();
                    add += Integer.parseInt(mr.group(1));
                    delete += Integer.parseInt(mr.group(2));
                    count += 1;
                } else if (Pattern.matches(
                        "^\\S+;(\\d{4})-(0\\d{1}|1[0-2])-(0\\d{1}|[12]\\d{1}|3[01])\\s\\d{2}:\\d{2}:\\d{2}\\s+\\S+;.*",
                        line)) {
                    String[] infos = line.split(";");
                    String name = infos[0];
                    String date = infos[1];
                    date = date.substring(0, 19);
                    if (gitlogRespBOMap.containsKey(name)) {
                        GitlogRespBO gitlogRespBO = gitlogRespBOMap.get(name);
                        gitlogRespBO.setAdd(gitlogRespBO.getAdd() + add);
                        gitlogRespBO.setDelete(gitlogRespBO.getDelete() + delete);
                        gitlogRespBO.setFileCount(gitlogRespBO.getFileCount() + count);
                        gitlogRespBO.setFirstCommit(gitlogRespBO.getFirstCommit().compareTo(date) > 0 ? date
                                : gitlogRespBO.getFirstCommit());
                        gitlogRespBO.setLastCommit(
                                gitlogRespBO.getLastCommit().compareTo(date) < 0 ? date : gitlogRespBO.getLastCommit());
                        gitlogRespBOMap.put(name, gitlogRespBO);
                    } else {
                        GitlogRespBO gitlogRespBO = new GitlogRespBO();
                        gitlogRespBO.setName(name);
                        gitlogRespBO.setAdd(add);
                        gitlogRespBO.setDelete(delete);
                        gitlogRespBO.setFileCount(count);
                        gitlogRespBO.setFirstCommit(date);
                        gitlogRespBO.setLastCommit(date);
                        gitlogRespBOMap.put(name, gitlogRespBO);
                    }
                    add_sum = add_sum + add;
                    add = 0;
                    delete = 0;
                    count = 0;
                }
            }
        }

        List<GitlogRespBO> gitlogRespBOList = new ArrayList<GitlogRespBO>();
        for (Iterator<String> it = gitlogRespBOMap.keySet().iterator(); it.hasNext();) {
            GitlogRespBO bo = gitlogRespBOMap.get(it.next());
            bo.setPercent(String.format("%.2f", Double.parseDouble("" + bo.getAdd()) / add_sum * 100));
            gitlogRespBOList.add(bo);
        }

        Collections.sort(gitlogRespBOList, new Comparator<GitlogRespBO>() {
            @Override
            public int compare(GitlogRespBO o1, GitlogRespBO o2) {
                return new Double(o2.getPercent()).compareTo(new Double(o1.getPercent()));
            }
        });

        return ResultBuilder.success(gitlogRespBOList);
    }

    /**
     * 找出目录下所有的git项目路径
     * 
     * @param projectDirList
     * @param parentDir
     */
    private void listProjectDir(List<File> projectFileList, File parent) {
        boolean isProject = false;
        File[] children = parent.listFiles();
        for (File child : children) {
            // 含有git目录的文件夹，认为parent是一个git项目
            if (".git".equals(child.getName())) {
                projectFileList.add(parent);
                isProject = true;
                break;
            }
        }
        // 不是git项目的情况下，循环递归下面的目录
        if (!isProject) {
            for (File child : children) {
                listProjectDir(projectFileList, child);
            }
        }
    }

    private List<String> execGitlog(String type, String startDate, String endDate, String author, String gitProject) {
        String command = "git log --pretty=format:\"%ce;%ad;%H\" --numstat --date=iso";
        switch (Integer.parseInt(type)) {
        case 0:
            if (StringUtils.isNotEmpty(startDate)) {
                command += " --since=" + startDate;
            }
            if (StringUtils.isNotEmpty(endDate)) {
                command += " --until=" + endDate;
            }
            break;
        case 1:
            command += " --since=1.day.ago";
            break;
        case 2:
            command += " --since=1.week.ago";
            break;
        case 3:
            command += " --since=1.month.ago";
            break;
        case 4:
            command += " --since=3.month.ago";
            break;
        default:
            command += " --since=1.month.ago";
            break;
        }
        List<String> result = ProcessUtil.exec(command, null, gitProject);
        return result;
    }

    @Override
    public Result<Integer> pullProjects() {
        if (remain > 0) {
            return ResultBuilder.error("wait for last task");
        }
        String[] urlArray = gitUrls.split(",");
        remain = urlArray.length;

        new Thread() {
            public void run() {
                for (String url : urlArray) {
                    log.debug("-------------start pull " + url + "-------------");
                    String projectDir = getDirByUrl(url);
                    if (!new File(projectDir).exists()) {
                        cloneProject(url);
                    } else {
                        pullProject(url);
                    }
                    remain--;
                    log.debug("-------------end pull " + url + "-------------");
                }
                remain = 0;
            }
        }.start();

        return ResultBuilder.success(remain);
    }

    private String getDirByUrl(String url) {
        String dir = new String(url);
        dir = dir.substring(dir.indexOf("/") + 1);
        dir = dir.substring(dir.indexOf("/") + 1);
        dir = dir.substring(dir.indexOf("/") + 1);
        dir = dir.substring(0, dir.lastIndexOf("."));
        return (projectsDir + "/" + dir).replaceAll("\\\\", "\\" + File.separator);
    }

    private void cloneProject(String url) {
        String commandDir = projectsDir.replaceAll("\\\\", "\\" + File.separator);
        String[] commandArray = initCommands.split(",");
        String[] cloneCommandArray = ArrayUtils.add(commandArray, "git clone " + url + " " + getDirByUrl(url));
        ProcessUtil.exec(cloneCommandArray, null, commandDir);
    }

    private void pullProject(String url) {
        String[] commandArray = initCommands.split(",");
        String[] pullCommandArray = ArrayUtils.add(commandArray, "git pull");
        ProcessUtil.exec(pullCommandArray, null, getDirByUrl(url));
    }

    public Result<Integer> getRemain() {
        return ResultBuilder.success(remain);
    }

}
