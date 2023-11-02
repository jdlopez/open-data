package es.jdlopez.opendata.utils;

import es.jdlopez.opendata.domain.CodeList;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ExcelCodeListReader<T> {

    private Consumer<Row> consumer;
    private int rowsToSkip = 0;

    public void setConsumer(Consumer<Row> consumer) {
        this.consumer = consumer;
    }

    public void setRowsToSkip(int rowsToSkip) {
        this.rowsToSkip = rowsToSkip;
    }

    public CodeList<T> read(InputStream is) throws IOException {
        CodeList<T> ret = new CodeList<>();

        try (ReadableWorkbook wb = new ReadableWorkbook(is)) {
            Sheet sheet = wb.getFirstSheet();
            try (Stream<Row> rows = sheet.openStream()) {
                // 2 lines of headers
                rows.skip(this.rowsToSkip).forEach(r -> {
                    // TBD
                });
            } // rows
        } // download
        System.out.println("Total: " + ret.getData().size());
        return ret;
    }
}
