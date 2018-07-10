package loadServer.TargetAdaptors;

import loadClient.loadController.Target;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class DiskAdaptor implements TargetAdaptor {
    File targetDir;

    public DiskAdaptor(Target target) {
        assert target.getTargetType() == Target.Type.DISK;
        String path = target.getPath();
        File file = new File(path);
        file.mkdirs();
        targetDir = file;
    }

    @Override
    public void create(String id, String content, String dataSourceName) throws IOException {
        File dataSourceDir = new File(targetDir, dataSourceName);
        File createFile = new File(dataSourceDir, id);
        BufferedWriter bw = new BufferedWriter(new FileWriter(createFile));
        bw.write(content);
        bw.close();
    }

    @Override
    public void delete(String id, String dataSourceName) {
        Path path = Paths.get(targetDir.getPath(), dataSourceName, id);
        File dataSourceDir = new File(targetDir, dataSourceName);
        File delFile = new File(dataSourceDir, id);
        if (delFile.exists()) {
            delFile.delete();
        }
    }

    @Override
    public void update(String id, String content, String dataSourceName) throws IOException {
        delete(id, dataSourceName);
        create(id, content, dataSourceName);
    }

    @Override
    public List<String> read(String id, String dataSourceName) throws IOException {
        File dataSourceDir = new File(targetDir, dataSourceName);
        File readFile = new File(dataSourceDir, id);
        BufferedReader br = new BufferedReader(new FileReader(readFile));
        List<String> content = new ArrayList<>();
        String line = null;
        while ((line = br.readLine()) != null) {
            content.add(line);
        }
        return content;
    }


    @Override
    public void login() {

    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public void reset(List<String> dataSourceNames) throws Exception {
        for (File file : targetDir.listFiles()) {
            FileUtils.deleteDirectory(file);
        }
        for (String dsName : dataSourceNames) {
            File cFile = new File(targetDir, dsName);
            cFile.mkdir();
        }
    }
}
