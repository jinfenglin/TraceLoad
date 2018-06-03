package loadServer.TargetAdaptors;

import java.io.File;

/**
 *
 */
public interface TargetAdaptor {
    void create(String id, File file);

    void delete(String id);

    void update(String id, File file);

    String read(String id);

    void login();
}
