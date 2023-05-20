package com.chua.common.support.file.pandoc;

import com.chua.common.support.constant.Projects;
import com.chua.common.support.file.CompressFile;
import com.chua.common.support.file.Decompress;
import com.chua.common.support.file.transfer.FileConverter;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.function.Joiner;
import com.chua.common.support.lang.exception.NotSupportedException;
import com.chua.common.support.resource.repository.Metadata;
import com.chua.common.support.resource.repository.Repository;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.IoUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * pandoc
 *
 * @author CH
 */
@Slf4j
public class Pandoc implements InitializingAware, FileConverter {

    private static final String WINDOW_DOWNLOAD_PATH = "https://github.com/jgm/pandoc/releases/download/3.1.2/pandoc-3.1.2-windows-x86_64.zip";

    private AtomicBoolean pandoc = new AtomicBoolean();
    private Executor executor;

    public Pandoc() {
        afterPropertiesSet();
    }

    @Override
    public void afterPropertiesSet() {
        refresh();
    }

    private void refresh() {
//        if (Projects.exist("pandoc --help")) {
//            executor = new EnvironmentExecutor();
//            return;
//        }

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
        Metadata database = Repository.of(file.getPath())
                .remoteResource(WINDOW_DOWNLOAD_PATH)
                .first(name);
        if(database.isEqualsOrigin()) {
            executor = new WindowExecutor(database.toFile());
            return;
        }


        File file1 = database.toFile();
        if(!FileUtils.isCompressFile(file1.getName())) {
            executor = new WindowExecutor(file1);
            return;
        }

        Decompress newExtension = ServiceProvider.of(Decompress.class).getDeepNewExtension(FileUtils.getSimpleExtension(file1));
        if(null == newExtension) {
            throw new NotSupportedException();
        }


    }

    /**
     * 执行器
     * @return 执行器
     */
    public Executor createExecutor() {
        return executor;
    }


    public String[] inputFormat() {
        return ("biblatex\n" +
                "bibtex\n" +
                "commonmark\n" +
                "commonmark_x\n" +
                "creole\n" +
                "csljson\n" +
                "csv\n" +
                "docbook\n" +
                "docx\n" +
                "dokuwiki\n" +
                "endnotexml\n" +
                "epub\n" +
                "fb2\n" +
                "gfm\n" +
                "haddock\n" +
                "html\n" +
                "ipynb\n" +
                "jats\n" +
                "jira\n" +
                "json\n" +
                "latex\n" +
                "man\n" +
                "markdown\n" +
                "markdown_github\n" +
                "markdown_mmd\n" +
                "markdown_phpextra\n" +
                "markdown_strict\n" +
                "mediawiki\n" +
                "muse\n" +
                "native\n" +
                "odt\n" +
                "opml\n" +
                "org\n" +
                "ris\n" +
                "rst\n" +
                "rtf\n" +
                "t2t\n" +
                "textile\n" +
                "tikiwiki\n" +
                "tsv\n" +
                "twiki\n" +
                "vimwiki").split("\\n");
    }
    public String[] outputFormat() {
        return ("asciidoc\n" +
                "asciidoctor\n" +
                "beamer\n" +
                "biblatex\n" +
                "bibtex\n" +
                "chunkedhtml\n" +
                "commonmark\n" +
                "commonmark_x\n" +
                "context\n" +
                "csljson\n" +
                "docbook\n" +
                "docbook4\n" +
                "docbook5\n" +
                "docx\n" +
                "dokuwiki\n" +
                "dzslides\n" +
                "epub\n" +
                "epub2\n" +
                "epub3\n" +
                "fb2\n" +
                "gfm\n" +
                "haddock\n" +
                "html\n" +
                "html4\n" +
                "html5\n" +
                "icml\n" +
                "ipynb\n" +
                "jats\n" +
                "jats_archiving\n" +
                "jats_articleauthoring\n" +
                "jats_publishing\n" +
                "jira\n" +
                "json\n" +
                "latex\n" +
                "man\n" +
                "markdown\n" +
                "markdown_github\n" +
                "markdown_mmd\n" +
                "markdown_phpextra\n" +
                "markdown_strict\n" +
                "markua\n" +
                "mediawiki\n" +
                "ms\n" +
                "muse\n" +
                "native\n" +
                "odt\n" +
                "opendocument\n" +
                "opml\n" +
                "org\n" +
                "pdf\n" +
                "plain\n" +
                "pptx\n" +
                "revealjs\n" +
                "rst\n" +
                "rtf\n" +
                "s5\n" +
                "slideous\n" +
                "slidy\n" +
                "tei\n" +
                "texinfo\n" +
                "textile\n" +
                "typst\n" +
                "xwiki\n" +
                "zimwiki").split("\\n");
    }

    @Override
    public String target() {
        return Joiner.on(",").join(outputFormat());
    }

    @Override
    public String source() {
        return Joiner.on(",").join(inputFormat());
    }

    @Override
    public void convert(String type, InputStream inputStream, String suffix, OutputStream outputStream) throws Exception {
        File file = new File(Projects.userName(), "/temp/0" + UUID.randomUUID().toString() + "." + type);
        File out = new File(Projects.userName(), "/temp/1" + UUID.randomUUID().toString() + "." + suffix);
        try {
            FileUtils.write(IoUtils.toByteArray(inputStream), file);
            executor.execute(file.getPath(), out.getAbsolutePath());
        } finally {
            FileUtils.delete(file);
            FileUtils.delete(out);
        }
    }

    @Override
    public void convert(File file, String suffix, OutputStream outputStream) throws Exception {
        File out = new File(Projects.userName(), "/temp/1" + UUID.randomUUID().toString() + "." + suffix);
        try {
            executor.execute(file.getPath(), out.getAbsolutePath());
        } finally {
            FileUtils.delete(out);
        }
    }

}
