package ming.gitlog.service.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProcessUtil {

    public static List<String> exec(String[] commands, String[] envp, String dir) {
        List<String> list = new ArrayList<String>();
        log.debug("commands:" + dir + ">" + Arrays.asList(commands));
        try {
            Process process = null;
            String os = System.getProperty("os.name");
            if (os.toLowerCase().startsWith("win")) {
                process = Runtime.getRuntime().exec("cmd", envp, new File(dir));
            } else {
                process = Runtime.getRuntime().exec("sh", envp, new File(dir));
            }
            OutputStreamWriter osw = new OutputStreamWriter(process.getOutputStream());
            BufferedWriter bw = new BufferedWriter(osw);
            for (int i = 0; i < commands.length; i++) {
                bw.write(commands[i]);
                bw.newLine();
            }
            bw.flush();
            bw.close();
            osw.close();
            SequenceInputStream sis = new SequenceInputStream(process.getInputStream(), process.getErrorStream());
            InputStreamReader isr = new InputStreamReader(sis, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            String result = "";
            while (null != (line = br.readLine())) {
                if (StringUtils.isNotEmpty(line)) {
                    result += line + "\n";
                    list.add(line);
                }
            }
            log.debug("commands result:\n" + result);
            process.destroy();
            br.close();
            isr.close();
            sis.close();
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static List<String> exec(String commands, String[] envp, String dir) {
        return exec(new String[] { commands }, envp, dir);
    }

}
