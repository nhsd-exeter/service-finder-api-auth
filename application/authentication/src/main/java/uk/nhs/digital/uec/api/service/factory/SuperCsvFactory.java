package uk.nhs.digital.uec.api.service.factory;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.prefs.CsvPreference;

import uk.nhs.digital.uec.api.domain.UserDownload;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.List;

public class SuperCsvFactory {

  public void writeCsv(List<UserDownload> users,
    String[] headers,
    String[] elements,
    CellProcessor[] processors,
    String fileName) throws IOException {

    ICsvBeanWriter csvWriter = null;
    try {
      csvWriter = new CsvBeanWriter(new FileWriter(fileName),
        CsvPreference.STANDARD_PREFERENCE);

      // write the header
      csvWriter.writeHeader(headers);

      // write the elements
      for( final UserDownload user : users ) {
        csvWriter.write(user,
          elements,
          processors);
      }
    }
    finally {
      if( csvWriter != null ) {
        csvWriter.close();
      }
    }
  }

  public byte[] readCsv(String filename) throws IOException{
    byte[] csvFile = null;
    csvFile = Files.readAllBytes(Paths.get(filename));
    return csvFile;
  }

  public void deleteCsv(String filename){
    File fileToDelete = new File(filename);
    fileToDelete.delete();
  }

}
