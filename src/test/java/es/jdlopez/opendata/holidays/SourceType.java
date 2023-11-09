package es.jdlopez.opendata.holidays;

import es.jdlopez.opendata.domain.Holiday;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public abstract class SourceType {
    public abstract List<Holiday> loadFromInputStream(InputStream is) throws IOException;
}
