package loadServer.TargetAdaptors;

import loadClient.loadController.Target;

import java.io.*;
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
    public void create(String id, String content) throws IOException {
        File createFile = new File(targetDir, id);
        BufferedWriter bw = new BufferedWriter(new FileWriter(createFile));
        bw.write(content);
        bw.close();
    }

    @Override
    public void delete(String id) {
        File delFile = new File(targetDir, id);
        if (delFile.exists()) {
            delFile.delete();
        }
    }

    @Override
    public void update(String id, String content) throws IOException {
        delete(id);
        create(id, content);
    }

    @Override
    public List<String> read(String id) throws IOException {
        File readFile = new File(targetDir, id);
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
    public void reset() throws Exception {
        for (File file : targetDir.listFiles()) {
            file.delete();
        }
    }
}
