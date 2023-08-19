package edu.ucsb.cs156.happiercows.helpers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import edu.ucsb.cs156.happiercows.entities.CommonStats;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/*
 * This code is based on 
 * <a href="https://bezkoder.com/spring-boot-download-csv-file/">https://bezkoder.com/spring-boot-download-csv-file/</a>
 * and provides a way to serve up a CSV file containing information associated
 * with an instructor report.
 */

public class CommonStatsCSVHelper {

  private CommonStatsCSVHelper() {}

  /**
   * This method is a hack to avoid a jacoco issue; it isn't possible to 
   * exclude an individual method call from jacoco coverage, but we can
   * exclude the entire method.  
   * @param out
   */

  public static void flush_and_close_noPitest(ByteArrayOutputStream out, CSVPrinter csvPrinter) throws IOException {
    csvPrinter.flush();
    csvPrinter.close();
    out.flush();
    out.close();
  }
  
  public static ByteArrayInputStream toCSV(Iterable<CommonStats> lines) throws IOException {
    final CSVFormat format = CSVFormat.DEFAULT;

    List<String> headers = Arrays.asList(
        "id",
        "commonsId",
        "numCows",
        "avgHealth",
        "timestamp");

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format);

    csvPrinter.printRecord(headers);

    for (CommonStats stats : lines) {
      List<String> data = Arrays.asList(
          String.valueOf(stats.getId()),
          String.valueOf(stats.getCommonsId()),
          String.valueOf(stats.getNumCows()),
          String.valueOf(stats.getAvgHealth()),
          String.valueOf(stats.getTimestamp()));
      csvPrinter.printRecord(data);
    }

    flush_and_close_noPitest(out, csvPrinter);
    return new ByteArrayInputStream(out.toByteArray());
  }
}