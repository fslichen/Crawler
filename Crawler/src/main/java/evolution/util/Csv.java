package evolution.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

public class Csv {
	public static <T> void printRow(Field[] fields, T t, BufferedWriter writer) throws Exception {
		int columnCount = fields.length;
		for (int j = 0; j < columnCount; j++) {
			Field field = fields[j];
			field.setAccessible(true);
			if (t != null) {
				writer.write(field.get(t).toString());
			} else {
				writer.write(field.getName());
			}
			if (j == columnCount - 1) {
				writer.write("\n");
			} else {
				writer.write(",");
			}
		}
	}
	
	public static <T> void toFile(List<T> ts, File file) throws Exception {
		Field[] fields = ts.get(0).getClass().getDeclaredFields();
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		printRow(fields, null, writer);
		for (T t : ts) {
			printRow(fields, t, writer);
		}
		writer.close();
	}
}
