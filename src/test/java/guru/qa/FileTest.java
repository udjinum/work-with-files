package guru.qa;

import com.codeborne.pdftest.PDF;

import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileTest extends BaseTest{

    @Test
    @DisplayName("Скачивание текстового файла и проверка его содержимого")
    void downloadSimpleTextFileTest() throws IOException {
        open("https://www.rfc-editor.org/rfc/");
        File file = $("a[href*='bcp-index']").download();
        String fileContent = IOUtils.toString(new FileReader(file));
        assertTrue(fileContent.contains("BCP INDEX"));
    }

    @Test
    @DisplayName("Скачивание PDF файла")
    void pdfFileDownloadTest() throws IOException {
        open("https://doctor-gradus.ru/");
        File file = $("a[href*='901']").download();
        PDF pdf = new PDF(file);
        assertEquals(2, pdf.numberOfPages);
        assertEquals("user23", pdf.author);
    }

    @Test
    @DisplayName("Скачивание XLS файла")
    void xlsFileDownloadTest() throws IOException {
        open("https://ckmt.ru/price-download.html");
        File file = $("a[href*='TehresursPrice']").download();

        XLS xls = new XLS(file);
        int lastRow = xls.excel
                .getSheetAt(0)
                .getLastRowNum();

        assertEquals(532,lastRow);
    }

    @Test
    @DisplayName("Парсинг CSV файлов")
    void parseCsvFileTest() throws IOException, CsvException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        try (InputStream is = classLoader.getResourceAsStream("numbers.csv");
             Reader reader = new InputStreamReader(is)) {
            CSVReader csvReader = new CSVReader(reader);

            List<String[]> strings = csvReader.readAll();
            assertEquals(4, strings.size());
        }
    }

    @Test
    @DisplayName("Парсинг ZIP файлов")
    void parseZipFileTest() throws IOException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        try (InputStream is = classLoader.getResourceAsStream("numbers.zip");
             ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                System.out.println(entry.getName());
                System.out.println(entry.getCrc());
                System.out.println(entry.getLastAccessTime());
            }
        }
    }
}
