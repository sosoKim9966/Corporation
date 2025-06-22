package com.work.company.common.parser;

import com.work.company.domain.dto.CsvCorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.mozilla.universalchardet.UniversalDetector;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CsvCorParser {

    public List<CsvCorResponseDto> getCorList(Path filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath.toFile()), Charset.forName("EUC-KR")))) {

            return reader.lines()
                    .skip(1)
                    .map(l -> l.split(",", 6))
                    .filter(arr -> arr.length >= 5)
                    .filter(arr -> "법인".equals(arr[4]))
                    .filter(arr -> !arr[0].isBlank())
                    .map(arr -> CsvCorResponseDto.builder()
                            .prmmiMnno(arr[0].trim())
                            .bzmnNm(arr[2].trim())
                            .brno(arr[3].replaceAll("-", ""))
                            .corpYnNm(arr[4])
                            .build())
                    .collect(Collectors.toList());
        }
    }

    public void readLines(Path filePath) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(filePath, Charset.forName("MS949"))) {
            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                System.out.println("Line " + lineNumber + ": " + line);

                String[] columns = line.split(",");
                System.out.println("Columns: " + Arrays.toString(columns));

                if (lineNumber >= 10) break;
            }
        }

    }

    public String findFileEncoding(String path) throws IOException{
        byte[] buf = new byte[4096];
        FileInputStream fis = new FileInputStream(new File(path));

        UniversalDetector detector = new UniversalDetector(null);

        int bufSize;
        while ((bufSize = fis.read(buf)) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, bufSize);
        }

        detector.dataEnd();

        String encoding = detector.getDetectedCharset();
        if (encoding != null) {
            System.out.println("Detected encoding = " + encoding);
        } else {
            System.out.println("No encoding detected.");
        }

        detector.reset();

        return encoding;
    }
}
