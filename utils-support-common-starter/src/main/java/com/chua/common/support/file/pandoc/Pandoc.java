package com.chua.common.support.file.pandoc;

import com.chua.common.support.constant.Projects;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.resource.repository.Metadata;
import com.chua.common.support.resource.repository.Repository;
import com.chua.common.support.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * pandoc
 *
 * @author CH
 */
@Slf4j
public class Pandoc implements InitializingAware {

    private static final String WINDOW_DOWNLOAD_PATH = "https://objects.githubusercontent.com/github-production-release-asset-2e65be/571770/0672d2cf-e8fa-4467-9a41-2a3ee178087c?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAIWNJYAX4CSVEH53A%2F20230519%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20230519T113353Z&X-Amz-Expires=300&X-Amz-Signature=8569cb6ce7735ecbfae292e8924cd87d0f6f0956704ebd0c439586591f6305cf&X-Amz-SignedHeaders=host&actor_id=0&key_id=0&repo_id=571770&response-content-disposition=attachment%3B%20filename%3Dpandoc-3.1.2-windows-x86_64.zip&response-content-type=application%2Foctet-stream";

    private AtomicBoolean pandoc = new AtomicBoolean();
    private Executor executor;

    @Override
    public void afterPropertiesSet() {
        refresh();
    }

    private void refresh() {
        if (Projects.exist("pandoc --help")) {
            executor = new EnvironmentExecutor();
            return;
        }

        if (Projects.isWindows()) {
            refreshWindow();
            return;
        }
        log.error("请安装pandoc");
    }

    private void refreshWindow() {
        String name = "pandoc.exe";
        String userHome = Projects.userHome();
        File file = new File(userHome, "/pandoc");
        if(FileUtils.exist(file)) {
            executor = new WindowExecutor(file);
            return;
        }

        Metadata database = Repository.of(file.getPath())
                .remoteResource(WINDOW_DOWNLOAD_PATH)
                .first(name);

        URL url = database.toUrl();

    }
}
