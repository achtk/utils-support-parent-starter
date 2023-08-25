package com.chua.example.document;

import com.github.artbits.quickio.api.Collection;
import com.github.artbits.quickio.api.DB;
import com.github.artbits.quickio.core.IOEntity;
import com.github.artbits.quickio.core.QuickIO;
import com.github.artbits.quickio.struct.Document;

import java.util.Optional;

/**
 * @author CH
 */
public class QuickIOExample {


    public static void main(String[] args) {
        try (DB db = QuickIO.usingDB("example_db")) {
            Collection<Document> collection = db.collection(Document.class);

            collection.save(new Document().put("city", "Canton").put("area", 7434.4));

            Document document = collection.findOne(d -> "Canton".equals(d.get("city")));
            Optional.ofNullable(document).ifPresent(IOEntity::printJson);
        }
    }
}
