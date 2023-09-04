package de.lukweb.discordbeam.uploaders;

import de.lukweb.hasteit.HasteUploader;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class HastebinUploaderImpl implements HastebinUploader {

    @Override
    public String shareHaste(String content, String extension) throws IOException {
        HasteUploader uploader = HasteUploader.getInstance();

        if (uploader == null) {
            throw new IOException("there's no hastebin uploader");
        }

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> theHasteUrl = new AtomicReference<>();
        AtomicReference<IOException> theError = new AtomicReference<>();

        uploader.upload(content, extension, new HasteUploader.HasteResult() {
            @Override
            public void onHaste(String hasteUrl) {
                theHasteUrl.set(hasteUrl);
                latch.countDown();
            }

            @Override
            public void onAuthorizationRequired() {
                theError.set(new IOException("hastebin requires an API key, configure it in the 'Haste It' settings tab"));
                latch.countDown();
            }

            @Override
            public void onFailure(Throwable ex) {
                theError.set(new IOException(ex));
                latch.countDown();
            }
        });

        if (theError.get() != null) {
            throw theError.get();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new IOException(e);
        }

        return theHasteUrl.get();
    }

}
