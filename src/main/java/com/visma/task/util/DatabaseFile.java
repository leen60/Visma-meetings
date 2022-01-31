package com.visma.task.util;

import com.visma.task.exception.DatasourceException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DatabaseFile {
    String fileName;

    public String read() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String res = reader
                    .lines()
                    .collect(Collectors.joining());

            reader.close();
            return res;
        } catch (IOException e) {
            throw new DatasourceException(e);
        }
    }

    public void write(String str) {
        try {
            FileOutputStream outputStream = new FileOutputStream(fileName);
            byte[] strToBytes = str.getBytes();
            outputStream.write(strToBytes);
            outputStream.close();
        } catch (IOException e) {
            throw new DatasourceException(e);
        }
    }
}