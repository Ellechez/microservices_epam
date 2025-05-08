package com.training.convertor;

import com.training.model.ResourceMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

public class Mp3MetadataConvertor {

    private static final Logger logger = LogManager.getLogger(Mp3MetadataConvertor.class);

    public static ResourceMetadata convert(InputStream stream) throws IOException, SAXException, TikaException {
        Metadata metadata = extractMetadataUsingParser(stream);

        logger.info("Extracted metadata from MP3 file: {}", metadata);

        ResourceMetadata resourceMetadata = new ResourceMetadata();

        resourceMetadata.setName(metadata.get("dc:title"));
        resourceMetadata.setArtist(metadata.get("xmpDM:artist"));
        resourceMetadata.setAlbum(metadata.get("xmpDM:album"));
        resourceMetadata.setLength(convertToMMSS(metadata.get("xmpDM:duration")));
        resourceMetadata.setYear(metadata.get("xmpDM:releaseDate"));

        if (isEmpty(resourceMetadata.getName()) || isEmpty(resourceMetadata.getArtist()) || isEmpty(resourceMetadata.getAlbum())) {
            logger.error("Invalid MP3 metadata: Missing mandatory fields (name, artist, album). Metadata: {}", resourceMetadata);
            throw new IllegalArgumentException("Invalid MP3 Metadata: Missing mandatory fields like name, artist, or album.");
        }

        logger.info("Successfully created ResourceMetadata: {}", resourceMetadata);
        return resourceMetadata;
    }

    private static String convertToMMSS(String s) {
        double parsedValue = Double.parseDouble(s);
        long roundedValue = (long) Math.round(parsedValue);
        long mm = roundedValue / 60000;
        long ss = roundedValue / 1000 % 60;
        return String.format("%02d:%02d", mm, ss);
    }

    private static Metadata extractMetadataUsingParser(InputStream stream) throws IOException, SAXException, TikaException {
        Metadata metadata = new Metadata();
        Mp3Parser parser = new Mp3Parser();
        parser.parse(stream, new BodyContentHandler(), metadata, new ParseContext());
        return metadata;
    }

    private static boolean isEmpty(String field) {
        return field == null || field.trim().isEmpty();
    }
}