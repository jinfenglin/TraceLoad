package loadServer.TargetAdaptors;

import loadClient.loadController.Target;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 */
public class DiskAdaptor implements TargetAdaptor {
    public DiskAdaptor(Target target) {
        assert target.getTargetType() == Target.Type.DISK;
        String path = target.getPath();
        File file = new File(path);
        file.mkdirs();
    }

    @Override
    public void create(String id, File file) {

    }

    @Override
    public void delete(String id) {

    }

    @Override
    public void update(String id, File file) {

    }

    @Override
    public String read(String id) {
        return null;
    }

    @Override
    public void login() {

    }
}
